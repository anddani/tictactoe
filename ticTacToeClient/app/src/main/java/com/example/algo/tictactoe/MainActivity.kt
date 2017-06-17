package com.example.algo.tictactoe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*;
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

// TODO:
//    - new function for create board, add to list and render
//    - Add win state
//    - Retrofit for api call
//    - RxKotlin for async call
//    - Add undo/redo functionality

class MainActivity : AppCompatActivity() {

    val ids : List<Int> = listOf(R.id.tile0, R.id.tile1, R.id.tile2,
                                 R.id.tile3, R.id.tile4, R.id.tile5,
                                 R.id.tile6, R.id.tile7, R.id.tile8)

    var gameStates : ArrayList<GameState> = arrayListOf()
    var cpuTurn : Boolean = false

    val api = RestAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add initial state (empty) to list of states
        gameStates.add(GameState(listOf("V", "V", "V",
                                        "V", "V", "V",
                                        "V", "V", "V"), ""))

        ids.map { findViewById(it) as TextView }
                .forEach {
                    it.setOnTouchListener { view, event -> tilePressed(view, event) }
                }

        val testboard = "[[V,V,V],[V,V,V],[X,X,X]]"

        log.append("")
    }

    fun tilePressed(view: View, event: MotionEvent) : Boolean {
        // If event is not releasing button, return
        if (event.action != MotionEvent.ACTION_UP) {
            return true
        }

        val playerMove = ids.indexOf(view.id)
        val previousState = gameStates.last()
        if (previousState.validMove(playerMove)) {
            // Create new game state
            val gameState = GameState(previousState.makeMove(playerMove), "Player move: (${playerMove % 3}, ${playerMove / 3})\n")

            // Add new game state to list of game states
            gameStates.add(gameState)

            // Render game state
            renderGameState(gameStates.last())

            // Opponent move
            cpuMove()
        } else {
            // Show error message
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                log.append(Html.fromHtml("<font color=#ff0000>Not a valid move</font>", Html.FROM_HTML_MODE_LEGACY))
            } else {
                log.append(Html.fromHtml("<font color=#ff0000>Not a valid move</font>"))
            }
            log.append("\n")
        }

        return true
    }

    fun renderGameState(gameState: GameState) {
        // Render game board
        ids.map { findViewById(it) as TextView }
                .zip(gameState.board.map { it.replace("V", "") }) { tv, s -> tv.setText(s) }

        // Log last move
        log.append(gameState.log)
    }

    fun cpuMove() {
        cpuTurn = true

        val obs: Observable<String> = Observable.create {
            subscriber ->
            val callResponse = api.play(gameStates.last().board.toString())
            val response = callResponse.execute()
            response.body()
            if (response.isSuccessful) {
                val newState = response.body().board
                subscriber.onNext(newState)
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }


        obs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { cpuBoard ->
                            Log.d("MyFilter", "board: ${cpuBoard}")

                            val newBoard = cpuBoard.replace(Regex("[\",\\[\\]]"), "")
                                    .split("")
                                    .drop(1)
                                    .dropLast(1)

                            val newState = GameState(newBoard)
                            log.append(newState.toString())

                            // Add new game state to list of game states
                            gameStates.add(newState)

                            // Render game state
                            renderGameState(gameStates.last())

                            cpuTurn = false
                        },
                        { e ->
                            Log.d("MyFilter", "Error: ${e}")
                            cpuTurn = false
                        }
                )

    }
}
