package com.example.algo.tictactoe

class GameState (val board: List<String>, val log: String = "") {

    val playerSymbol = "X"

    fun makeMove(move: Int) : List<String> =
            board.take(move) + playerSymbol + board.drop(move+1)

    fun validMove(move: Int) : Boolean =
            move != -1 && "V".equals(board[move])

    fun gameOver() : Boolean =
            maxCons('X') == 3 || maxCons('O') == 3 || board.indexOf("V") == -1

    fun getWinner() : String =
        when {
            maxCons('X') == 3 -> "Player"
            maxCons('O') == 3 -> "CPU"
            board.indexOf("V") == -1 -> "Tie"
            else -> ""
        }

    fun maxCons(player: Char) : Int {
        val row : Int? = splitInThree(board)
                .map { numCons(player, it.joinToString("")) }
                .max()
        val col : Int? = splitInThree(transpose(board))
                .map { numCons(player, it.joinToString("")) }
                .max()
        val dia : Int? = maxOf(numCons(player, listOf(board[0], board[4], board[8]).joinToString("")),
                               numCons(player, listOf(board[2], board[4], board[6]).joinToString("")))

        return maxOf(row!!, col!!, dia!!)
    }

    fun transpose(b: List<String>) =
            b.withIndex()
                    .groupBy { it.index % 3 }
                    .map { it.value.map { it.value } }
                    .flatten()

    fun splitInThree(b: List<String>) =
            b.withIndex()
                    .groupBy { it.index / 3 }
                    .map { it.value.map { it.value } }

    fun numCons(player: Char, row: String) : Int {
        var cons = 0
        var maxCons = 0
        row.forEach {
            if (it == player) {
                cons++
            }
            else {
                maxCons = maxOf(maxCons, cons)
                cons = 0
            }
        }
        return maxOf(maxCons, cons)
    }

    override fun toString() : String {
        return board.withIndex()
                .groupBy { it.index / 3 }
                .map { it.value.map { it.value } }
                .toString()
    }
}
