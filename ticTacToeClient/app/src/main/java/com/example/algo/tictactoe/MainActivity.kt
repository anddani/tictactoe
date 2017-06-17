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
//    - Have cpuTurn as an immutable variable in gameStates instead. If the last
//      move was made by player, it is CPUs turn
//    - Add undo/redo functionality

class MainActivity : AppCompatActivity() {

    val ids : List<Int> = listOf(R.id.tile0, R.id.tile1, R.id.tile2,
                                 R.id.tile3, R.id.tile4, R.id.tile5,
                                 R.id.tile6, R.id.tile7, R.id.tile8)

    // Add initial state (empty) to list of states
    val initialGameState = GameState(listOf("V", "V", "V",
                                            "V", "V", "V",
                                            "V", "V", "V"), "")

    var gameStates : ArrayList<GameState> = arrayListOf(initialGameState)
    var cpuTurn : Boolean = false

    val api = RestAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup touchListners for each tile on the board
        ids.map { findViewById(it) as TextView }
                .forEach {
                    it.setOnTouchListener { view, event -> tilePressed(view, event) }
                }
    }

    fun tilePressed(view: View, event: MotionEvent) : Boolean {
        // If event is not releasing button, return
        if (event.action != MotionEvent.ACTION_UP || cpuTurn) {
            return true
        }

        // If game is over, reset game
        if (gameStates.last().gameOver()) {
            gameStates = arrayListOf(initialGameState)
            log.setText("")
            renderGameState(gameStates.last())
            return true
        }

        val playerMove = ids.indexOf(view.id)
        val previousState = gameStates.last()
        if (previousState.validMove(playerMove)) {
            // Create new game state
            val statusMessage = "Player move: (${playerMove % 3}, ${playerMove / 3})\n"
            val newBoard = previousState.makeMove(playerMove)

            // Make player move
            addNewState(newBoard, statusMessage)

            // Opponent move if game is not over
            if (!gameStates.last().gameOver()) {
                cpuMove()
            }
        } else {
            // Show error message
            coloredLogMessage("red", "Not a valid move")
        }

        return true
    }

    fun cpuMove() {
        cpuTurn = true

        val obs: Observable<TTTDataResponse> = Observable.create {
            subscriber ->
            val callResponse = api.play(gameStates.last().board.toString())
            val response = callResponse.execute()
            response.body()
            if (response.isSuccessful) {
                subscriber.onNext(response.body())
                subscriber.onCompleted()
            } else {
                subscriber.onError(Throwable(response.message()))
            }
        }


        obs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { cpuNewState ->
                            val cpuBoard = cpuNewState.board
                            Log.d("MyFilter", "board: ${cpuBoard}")

                            // Convert response format to List<String>
                            val newBoard = cpuBoard.replace(Regex("[\",\\[\\]]"), "")
                                    .split("")
                                    .drop(1)
                                    .dropLast(1)

                            val statusMessage = "CPU move: ${cpuNewState.move}\n"

                            // Make CPU move
                            addNewState(newBoard, statusMessage)

                            cpuTurn = false
                        },
                        { e ->
                            Log.d("MyFilter", "Error: ${e}")
                            cpuTurn = false
                        }
                )

    }

    fun addNewState(newBoard: List<String>, statusMessage: String) {
        val newState = GameState(newBoard, statusMessage)

        // Add new game state to list of game states
        gameStates.add(newState)

        // Render game state
        renderGameState(gameStates.last())

        // If game over, show message
        if (gameStates.last().gameOver()) {
            val winner = gameStates.last().getWinner()
            if (winner == "Tie") {
                coloredLogMessage("blue", "A Tie!")
            } else {
                coloredLogMessage("green", "${gameStates.last().getWinner()} has won!")
            }
        }
    }

    fun renderGameState(gameState: GameState) {
        // Render game board
        ids.map { findViewById(it) as TextView }
                .zip(gameState.board.map { it.replace("V", "") }) { tv, s -> tv.setText(s) }

        // Log last move
        log.append(gameState.log)
    }

    fun coloredLogMessage(messageColor: String, message: String) {
        val color = when {
            messageColor.equals("red") -> "#ff0000"
            messageColor.equals("green") -> "#00ff00"
            messageColor.equals("blue") -> "#0000ff"
            else -> "#000000"
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            log.append(Html.fromHtml("<font color=${color}>${message}</font>", Html.FROM_HTML_MODE_LEGACY))
        } else {
            log.append(Html.fromHtml("<font color=${color}>${message}</font>"))
        }
        log.append("\n")
    }
}
