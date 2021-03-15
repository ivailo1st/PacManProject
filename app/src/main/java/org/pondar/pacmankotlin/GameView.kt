package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlin.math.sqrt
import kotlin.random.Random

class GameView : View {

    private var game: Game? = null
    private var h: Int = 0
    var over = false
    private var w: Int = 0 //used for storing height and width of the view

    fun setGame(game: Game?) {
        this.game = game

    }

    /*
        Constructors for the Android view system,
        when we there is a custom view.
	 */
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //onDraw Function
    override fun onDraw(canvas: Canvas) {
        //Height and Width Setup
        h = canvas.height
        w = canvas.width

        game?.setSize(h, w)
        Log.d("GAMEVIEW", "h = $h, w = $w")


        //Condition that checks if game is over
        if(game!!.GameOver()){
            //Variable used to display game over message once
            over = false

            //Making a new paint object
            val paint = Paint()
            canvas.drawColor(game!!.colorCanvas) //clear entire canvas to a specific color

            //Initialize coins if there arent
            if (!(game!!.coinsInitialized))
                game?.initializeGoldcoins(canvas,paint)

            //Draw the Player and Enemy
            canvas.drawBitmap(game!!.ghost, game?.ghostx!!.toFloat(),
                    game?.ghosty!!.toFloat(), paint)

            canvas.drawBitmap(game!!.pacBitmap, game?.pacx!!.toFloat(),
                    game?.pacy!!.toFloat(), paint)

            //Get Distance between Player and Enemy
            if(game!!.getDistance(game!!.pacx,game!!.pacy,game!!.ghostx,game!!.ghosty) < 200 ){
                //Set game state to Game Over
                game!!.countDown = 0
            }

            //For loop where it creates each coin by first checking if it is taken
            //and then checks the distance between the player and the coin, where
            //if the player is close enough coin will be marked as taken and be removed
            for(item in game!!.coins){
                if((!item.taken)){

                    if(game!!.getDistance(game!!.pacx,game!!.pacy,item.posX,item.posY)<125){
                        item.taken = true
                        game!!.points ++
                    }
                    else{
                        canvas.drawBitmap(game!!.pacCoin,item.posX.toFloat(),item.posY.toFloat(),paint)
                    }

                }
            }

            //Update Points
            game?.pointsCheck()
        }

        //Game Over Message
        else if(!(over) && !(game!!.GameOver())) {
            game!!.countDown = 0
            if(game!!.points == 11){
                Toast.makeText(context, "Player Wins", Toast.LENGTH_SHORT).show()
                game!!.difficulty += 5
            }
            else{
                Toast.makeText(context, "Enemy Wins", Toast.LENGTH_SHORT).show()
            }
            over = true
        }

        super.onDraw(canvas)
    }

}
