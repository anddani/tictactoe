package com.example.algo.tictactoe

import android.util.Log

class GameState (val board : List<String>, val log : String) {

    val playerSymbol = "X"

    fun printLog() {
        Log.d("MyFilter", log)
    }

    fun makeMove(move: Int) : List<String> =
        board.take(move) + playerSymbol + board.drop(move+1)

    fun validMove(move: Int) : Boolean =
        move != -1 && "V".equals(board[move])

    override fun toString() : String {
        return ""
    }
}
