{-# LANGUAGE DeriveGeneric #-}
{-# LANGUAGE OverloadedStrings #-}
module Main where
import Data.Aeson (FromJSON, ToJSON)
import Data.List
import Data.List.Split
import Data.Monoid ((<>))
import Web.Scotty
import GHC.Generics

import qualified Data.Text.Lazy as TL

data Response = Response { board :: String, winner :: Maybe Char, score :: Int, move :: String } deriving (Show, Generic)
instance ToJSON Response
instance FromJSON Response

data Move = Move Int (Int, Int) deriving (Show, Eq)
instance Ord Move where
    (Move s1 _) `compare` (Move s2 _) = s1 `compare` s2

boardCoords = [(x, y) | x <- [0..2], y <- [0..2]]

-- Convert a string representation to a board
-- "[XOO,XVX,OXO]" -> ["XOO", "XVX", "OXO"]
stringToBoard :: String -> [String]
stringToBoard s =
    chunksOf 3 [x | x <- s, x == 'O' || x == 'X' || x == 'V']

getMax :: [String] -> Int
getMax [] = 0
getMax x  = maximum $ map length x

-- Count number of consecutive points in a row
numCons :: Char -> String -> Int
numCons player seq = 
    getMax $ filter (player `elem`) (group seq)

maxRow :: [String] -> Char -> Int
maxRow board player =
    maximum $ map (numCons player) board

maxCol :: [String] -> Char -> Int
maxCol board player =
    maxRow (transpose board) player

maxDia :: [String] -> Char -> Int
maxDia [[a1, _, b1],
        [_, c,  _],
        [b2, _, a2]] player =
            max (numCons player [a1, c, a2]) (numCons player [b1, c, b2])

maxCons :: [String] -> Char -> Int
maxCons board player =
    let r = maxRow board player
        c = maxCol board player
        d = maxDia board player
     in maximum [r, c, d]

free :: [String] -> (Int, Int) -> Bool
free board (x, y) = board !! x !! y == 'V'

possibleMoves :: [String] -> [(Int, Int)]
possibleMoves board = filter (free board) boardCoords

-- Player (X), CPU (O), Tie (V), ""
getWinner :: [String] -> Maybe Char
getWinner board
  | maxCons board 'X' == 3     = Just 'X'
  | maxCons board 'O' == 3     = Just 'O'
  | 'V' `notElem` concat board = Just 'V'
  | otherwise                  = Nothing

makeMove :: [String] -> Char -> (Int, Int) -> [String]
makeMove board turn pos =
    chunksOf 3 $ map (\(x, y) -> if (x, y) == pos then turn else board !! x !! y) boardCoords

changeTurn :: Char -> Char
changeTurn 'X' = 'O'
changeTurn 'O' = 'X'

minOrMax 'O' = maximum
minOrMax 'X' = minimum

minimax :: Char -> Int -> [String] -> Int
minimax turn score board
  | getWinner board == Just 'X' = -10
  | getWinner board == Just 'O' = 10
  | getWinner board == Just 'V' = 0
  | otherwise =
      let p    = possibleMoves board
          next = map (makeMove board turn) p
       in minOrMax turn $ map (minimax (changeTurn turn) score) next

getBestMove :: [String] -> Move
getBestMove board =
    let scores   = map (\x -> Move (minimax 'X' 0 (makeMove board 'O' x)) x) $ possibleMoves board
        bestMove = maximum scores
     in bestMove

play :: String -> Response
play state = 
    let inputBoard  = stringToBoard state
        bestMove    = getBestMove inputBoard
        Move s move = bestMove
        nextState   = makeMove inputBoard 'O' move
        w           = getWinner nextState
     in Response { board = show nextState, winner = w, score = s, move = show move }

main :: IO ()
main = do
    putStrLn "-- Starting HaTTT Backend"
    scotty 3000 $
        post "/play" $ do
            board <- param "board"
            json $ play board
