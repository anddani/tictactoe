package com.example.algo.tictactoe

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import rx.Observable

class RestAPI {
    private val tttApi: TTTApi
    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.0.173:3000")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        tttApi = retrofit.create(TTTApi::class.java)
    }

    fun play(board: String): Call<TTTDataResponse> = tttApi.play(board)
}
