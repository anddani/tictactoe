package com.example.algo.tictactoe

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Field
import java.util.*
import rx.Observable

data class TTTApiResponse(val data: TTTDataResponse)
class TTTDataResponse(
        val board: String,
        val winner: String,
        val score: Int
)

interface TTTApi {
    @POST("/play/")
    fun play(@Query("board") board: String): Call<TTTDataResponse>
}
