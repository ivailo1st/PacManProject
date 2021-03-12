package org.pondar.pacmankotlin

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    //reference to the game class.
    private var game: Game? = null

    //State of game
    private var running = false

    //Timer Setup
    private var counter: Int = 0
    private var theTimer: Timer = Timer()
    private var timeLeft: Timer = Timer()

    //Default directions of Player and Enemy
    private var direction = "right"
    private var ghostDirection = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)

        game = Game(this,pointsView)

        //Initialization of the gameView class and game class
        game?.setGameView(gameView)
        gameView.setGame(game)
        game?.newGame()


        //Event Listener Setups
        pauseButton.setOnClickListener {
            running = !running
        }

        gameView.setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity){
            override fun onSwipeTop() {
                super.onSwipeTop()
                Log.d("Movement","Top")
                direction = "top"
            }

            override fun onSwipeBottom() {
                super.onSwipeBottom()
                Log.d("Movement","Bottom")
                direction = "bottom"
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                Log.d("Movement","Left")
                direction = "left"
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                Log.d("Movement","Right")
                direction = "right"
            }


        })

        //Game Setup (State and Timer)
        running = true
        theTimer.schedule(object : TimerTask(){
            override fun run() {
                timerMethod()
            }
        }, 0, 150)

        timeLeft.schedule(object: TimerTask(){
            override  fun run(){
                timeLeftMethod()
            }
        }, 0, 1000)
    }

    //Stop functionality for both timers
    override fun onStop() {
        super.onStop()
        theTimer.cancel()
        timeLeft.cancel()
    }

    //Timer Setup
    private fun timeLeftMethod(){
        this.runOnUiThread(timeLeftTick)
    }

    private fun timerMethod(){
        this.runOnUiThread(timerTick)
    }

    //Timer for time-left functionality
    private val timeLeftTick = Runnable {
        if ((running) && (game!!.countDown > 0)){
            //Enemy Movement Generator based on distance value
            var arrayOfMove = emptyArray<Int>()

            arrayOfMove += game!!.getDistance(game!!.pacx,game!!.pacy,game!!.ghostx + 25 + game!!.difficulty,game!!.ghosty).toInt()
            arrayOfMove += game!!.getDistance(game!!.pacx,game!!.pacy,game!!.ghostx - (25 + game!!.difficulty),game!!.ghosty).toInt()
            arrayOfMove += game!!.getDistance(game!!.pacx,game!!.pacy,game!!.ghostx,game!!.ghosty + 25 + game!!.difficulty).toInt()
            arrayOfMove += game!!.getDistance(game!!.pacx,game!!.pacy,game!!.ghostx,game!!.ghosty - (25 + game!!.difficulty)).toInt()

            when(arrayOfMove.minOrNull()){
                arrayOfMove[0] -> ghostDirection = 1
                arrayOfMove[1] -> ghostDirection = 2
                arrayOfMove[2] -> ghostDirection = 4
                arrayOfMove[3] -> ghostDirection = 3
                else -> {Log.d("Prediction Error","Prediction failed")}
            }

            //Ghost Random Direction Generator Example
            //ghostDirection = game!!.rand(1,4)

            //Game Countdown for TimeLeft
            game!!.countDown--
            count_down.text = "TimeLeft: ${game!!.countDown}"
        }
        else if (game!!.countDown <= 0){
            //GameOver
            count_down.text = "TimeLeft: 0"
            game?.stopTheGame()
        }
    }
    //Timer for Player and Enemy movement
    private val timerTick = Runnable {
        if(running){
            counter++

            //Enemy Movement
            when(ghostDirection){
                1 -> game?.movement(25 + game!!.difficulty, "Right","Ghost")
                2 -> game?.movement(25 + game!!.difficulty,"Left","Ghost")
                3 -> game?.movement(25 + game!!.difficulty,"Top","Ghost")
                4 -> game?.movement(25 + game!!.difficulty,"Bottom","Ghost")
                else -> {
                    Log.d("Error in Case", "No proper direction condition was met")
                }
            }

            //Player Movement
            when(direction){
               "right" -> game?.movement(50,"Right","Pacman")
               "left" -> game?.movement(50,"Left","Pacman")
               "top" -> game?.movement(50,"Top","Pacman")
               "bottom" -> game?.movement(50,"Bottom","Pacman")
               else -> {
                   Log.d("Error in Case", "No proper direction condition was met")
               }
           }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_SHORT).show()
            return true
        } else if (id == R.id.action_newGame) {
            Toast.makeText(this, "New Game clicked", Toast.LENGTH_SHORT).show()
            game?.newGame()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
