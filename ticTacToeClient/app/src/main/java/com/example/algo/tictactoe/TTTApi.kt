package com.example.algo.tictactoe

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

class TTTDataResponse(
        val board: String,
        val move: String,
        val winner: String,
        val score: Int
)

interface TTTApi {
    @POST("/play/")
    fun play(@Query("board") board: String): Call<TTTDataResponse>
}
