package com.example.algo.tictactoe

import org.junit.Test

import org.junit.Assert.*


class GameStateTest {
    val gameState = GameState(listOf("X", "O", "V",
                                     "X", "V", "O",
                                     "V", "V", "V"),  "")
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
        // TODO
        assertEquals(true, false)
    }

    @Test
    fun gameOver_isCorrect() {
        // TODO
        assertEquals(true, false)
    }

    @Test
    fun winner_isCorrect() {
        // TODO
        assertEquals(true, false)
    }
}
