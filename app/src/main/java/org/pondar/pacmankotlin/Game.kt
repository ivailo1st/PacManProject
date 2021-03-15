package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.*
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import java.util.ArrayList
import kotlin.math.sqrt
import kotlin.random.Random

class Game(private var context: Context, view: TextView, view2: TextView) {

    private var pointsView: TextView = view
    private var level: TextView = view2

    //TimeLimit and Points
    var points: Int = 0
    var countDown: Int = 30

    //bitmaps
    var pacBitmap: Bitmap
    var pacCoin: Bitmap
    var ghost: Bitmap

    //Coordinates
    var pacx: Int = 0
    var pacy: Int = 0

    var ghostx: Int = 0
    var ghosty: Int = 0

    var colorCanvas: Int = 0

    var coinsInitialized = false

    //the list of goldcoins - initially empty
    var coins = ArrayList<GoldCoin>()

    var difficulty : Int = 0

    //a reference to the gameview
    private var gameView: GameView? = null
    private var h: Int = 0
    private var w: Int = 0 //height and width of screen


    //The init code is called when we create a new Game class.
    init {
        pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman)
        pacCoin = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
        ghost = BitmapFactory.decodeResource(context.resources, R.drawable.ghost)
    }

    fun rand(start: Int, end: Int): Int {
        require(!(start > end || end - start + 1 > Int.MAX_VALUE)) { "Illegal Argument" }
        return Random(System.nanoTime()).nextInt(end - start + 1) + start
    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    fun getDistance (x1: Int, y1:Int, x2:Int, y2:Int):Float{
        var result = (((y2-y1)*(y2-y1))+((x2-x1)*(x2-x1)))
        return sqrt(result.toDouble()).toFloat()
    }

    private fun randomColourPicker(): Int {

        return when (rand(1, 5)) {
            1 -> Color.CYAN
            2 -> Color.MAGENTA
            3 -> Color.YELLOW
            4 -> Color.LTGRAY
            5 -> Color.GREEN
            else -> {
                0
            }
        }
    }

    fun initializeGoldcoins(canvas: Canvas, paint: Paint) {
        for (i in 1..11) {
            val randY = rand(100, h - 200)
            val randX = rand(100, w - 200)

            coins.add(GoldCoin(false, randX, randY))

        }

        for (item in coins) {
            canvas.drawBitmap(pacCoin, item.posX.toFloat(), item.posY.toFloat(), paint)
        }

        coinsInitialized = true
    }

    fun GameOver(): Boolean {
        return ((points < 11) && (countDown > 0))

    }

    fun stopTheGame() {
        pointsCheck()
        gameView!!.invalidate()
    }

    fun newGame() {
        countDown = 30
        pacx = 50
        pacy = 400

        colorCanvas = randomColourPicker()
        ghostx = ghost.width * 4
        ghosty = ghost.height + 320

        coins.clear()
        coinsInitialized = false
        points = 0
        pointsView.text = "${context.resources.getString(R.string.points)} $points"
        level.text = "Bonus Enemy Speed: $difficulty"
        gameView?.invalidate()
    }

    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w


    }

    fun movement(pixels: Int, direction: String, Type: String) {
        if(Type == "Pacman") {
            when (direction) {
                "Top" -> {
                    if (pacy - pixels >= 0) {
                        pacy -= pixels
                        gameView!!.invalidate()
                    }
                }
                "Bottom" -> {
                    if (pacy + pixels + pacBitmap.height < h) {
                        pacy += pixels
                        gameView!!.invalidate()
                    }
                }
                "Right" -> {
                    if (pacx + pixels + pacBitmap.width < w) {
                        pacx += pixels
                        gameView!!.invalidate()
                    }
                }
                "Left" -> {
                    if (pacx - pixels >= 0) {
                        pacx -= pixels
                        gameView!!.invalidate()
                    }
                }
            }
        }
        else if (Type =="Ghost"){
            when (direction) {
                "Top" -> {
                    if (ghosty - pixels >= 0) {
                        ghosty -= pixels
                        gameView!!.invalidate()
                    }
                }
                "Bottom" -> {
                    if (ghosty + pixels + ghost.height < h) {
                        ghosty += pixels
                        gameView!!.invalidate()
                    }
                }
                "Right" -> {
                    if (ghostx + pixels + ghost.width < w) {
                        ghostx += pixels
                        gameView!!.invalidate()
                    }
                }
                "Left" -> {
                    if (ghostx - pixels >= 0) {
                        ghostx -= pixels
                        gameView!!.invalidate()
                    }
                }
            }
        }
    }

    fun pointsCheck() {
        pointsView.text = "Points: ${points}"
        level.text = "Bonus Enemy Speed: $difficulty"
    }


}