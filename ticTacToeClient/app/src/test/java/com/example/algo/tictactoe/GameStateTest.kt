package com.example.algo.tictactoe

import org.junit.Test

import org.junit.Assert.*

class GameStateTest {
    val gameState = GameState(listOf("X", "O", "V",
                                     "X", "V", "O",
                                     "V", "V", "V"),  "")

    val gameOverState1 = GameState(listOf("X", "O", "V",
                                          "X", "X", "O",
                                          "V", "V", "X"),  "")

    val gameOverState2 = GameState(listOf("X", "O", "X",
                                          "X", "X", "O",
                                          "O", "X", "O"),  "")
    @Test
    fun validMove_isCorrect() {
        assertEquals(true,  gameState.validMove(2))
        assertEquals(false, gameState.validMove(3))
        assertEquals(false, gameState.validMove(-1))
    }

    @Test
    fun makeMove_isCorrect() {
        assertEquals(listOf("X", "O", "X",
                            "X", "V", "O",
                            "V", "V", "V"),
                gameState.makeMove(2))

        assertEquals(listOf("X", "O", "V",
                            "X", "V", "O",
                            "V", "V", "X"),
                gameState.makeMove(8))
    }

    @Test
    fun gameStateToString_isCorrect() {
        assertEquals("[[X, O, V], [X, V, O], [V, V, V]]",
                gameState.toString())
    }

    @Test
    fun gameOver_isCorrect() {
        assertEquals(false, gameState.gameOver())
        assertEquals(true, gameOverState1.gameOver())
        assertEquals(true, gameOverState2.gameOver())
    }

    @Test
    fun numCons_isCorrect() {
        assertEquals(2, gameState.maxCons('X'))
        assertEquals(1, gameState.maxCons('O'))
        assertEquals(3, gameOverState1.maxCons('X'))
    }

    @Test
    fun transpose_isCorrect() {
        assertEquals(listOf("X", "X", "V",
                            "O", "V", "V",
                            "V", "O", "V" ),
                gameState.transpose(gameState.board))
    }

    @Test
    fun winner_isCorrect() {
        assertEquals("", gameState.getWinner())
        assertEquals("Player", gameOverState1.getWinner())
        assertEquals("Tie", gameOverState2.getWinner())
    }
}
