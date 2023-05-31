package org.example.gestureplay

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.DragEvent
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class ChessActivity : AppCompatActivity(), View.OnTouchListener, View.OnDragListener, GestureDetector.OnGestureListener {

    lateinit var gestureDetector: GestureDetector
    lateinit var shadowBuilder: View.DragShadowBuilder
    lateinit var squares: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess)
        val board = findViewById<GridLayout>(R.id.board)
        gestureDetector = GestureDetector(this, this)

        squares = (0..15).map {
            ImageView(this).apply {
                this.background = ColorDrawable(
                    if (it % 2 == 0) {
                        if ((it / 4) % 2 == 0)
                            Color.BLACK
                        else Color.WHITE
                    } else {
                        if ((it / 4) % 2 == 0)
                            Color.WHITE
                        else Color.BLACK
                    }
                )
                layoutParams = GridLayout.LayoutParams().apply {
                    this.height = 0
                    this.width = 0
                    this.columnSpec = GridLayout.spec(it % 4, 1, 1.0f)
                    this.rowSpec = GridLayout.spec(it / 4, 1, 1.0f)
                }

                if (it == 0) {
                    this.setImageResource(R.drawable.lonely_chess_king_24)
                    this.tag = true
                } else {
                    this.setImageDrawable(null)
                    this.tag = false
                }
                setOnClickListener { _ ->
                    println(it)
                }
                setOnTouchListener(this@ChessActivity)
                setOnDragListener(this@ChessActivity)
            }
        }
        
        squares.forEach {
            board.addView(it)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        println("onTouch $event $v")
        event?.also {
            if(v is ImageView && v.tag == true) {
                shadowBuilder = View.DragShadowBuilder(v)
                gestureDetector.onTouchEvent(it)
                return true
            }
        }

        return false
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        println("onDrag $event $v")
        when (event?.action) {
            DragEvent.ACTION_DROP -> {
                println("drop")
                val sourceView = shadowBuilder.view as? ImageView ?: return false
                val targetView = v as? ImageView ?: return false

                if(areNeighbourSquares(sourceView, targetView).also { println("are neigh? $it") }) {
                    val sourceDrawable = sourceView?.drawable
                    val targetDrawable = targetView?.drawable

                    sourceView.setImageDrawable(targetDrawable)
                    sourceView.tag = false
                    targetView.setImageDrawable(sourceDrawable)
                    targetView.tag = true
                    return true
                }
            }
        }
        return true
    }

    private fun areNeighbourSquares(sourceView: ImageView, targetView: ImageView): Boolean {
        val indexSource = squares.indexOfFirst { it === sourceView }
        val indexTarget = squares.indexOfFirst { it === targetView }

        val sourceRow = indexSource / 4
        val sourceCol = indexSource % 4
        val targetRow = indexTarget / 4
        val targetCol = indexTarget % 4

        return if(sourceRow == targetRow) {
            abs(sourceCol - targetCol) == 1
        } else if(sourceCol == targetCol) {
            abs(sourceRow - targetRow) == 1
        } else {
            abs(sourceCol - targetCol) == 1 && abs(sourceRow - targetRow) == 1
        }
    }

    override fun onDown(e: MotionEvent): Boolean {
        println("onDown $e")
        return false
    }

    override fun onShowPress(e: MotionEvent) {
        println("onShowPress $e")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        println("onSingleTapUp $e")
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {
        println("onScroll")
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        println("onLongPress $e")
        val view = shadowBuilder.view
        view.startDragAndDrop(
            null,
            shadowBuilder,
            null,
            0
        )
        View.DRAG_FLAG_GLOBAL
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        println("onFling")
        return false
    }
}