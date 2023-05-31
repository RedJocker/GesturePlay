package org.example.gestureplay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.View.OnDragListener
import android.view.View.OnTouchListener
import android.widget.ImageView
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), OnTouchListener,  OnDragListener, GestureDetector.OnGestureListener {

    companion object {
        const val imageUrl1 = "https://picsum.photos/id/237/200/300"
        const val imageUrl2 = "https://picsum.photos/200/300"
    }

    lateinit var gestureDetector: GestureDetector
    lateinit var shadowBuilder: DragShadowBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val imageView1 = findViewById<ImageView>(R.id.imageView1)
        val imageView2 = findViewById<ImageView>(R.id.imageView2)

        val picasso = Picasso.get()
        picasso.load(imageUrl1).into(imageView1)
        picasso.load(imageUrl2).into(imageView2)

        gestureDetector = GestureDetector(this, this)

        imageView1.setOnTouchListener(this)
        imageView2.setOnTouchListener(this)
        imageView1.setOnDragListener(this)
        imageView2.setOnDragListener(this)

    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        motionEvent?.also {
            shadowBuilder = DragShadowBuilder(view)
            gestureDetector.onTouchEvent(it)
            return true
        }

        return false
    }

    override fun onDrag(targetV: View?, event: DragEvent?): Boolean {
        when (event?.action) {
            DragEvent.ACTION_DROP -> {
                val sourceView = shadowBuilder.view as? ImageView ?: return false
                val targetView = targetV as? ImageView ?: return false

                val sourceDrawable = sourceView.drawable
                val targetDrawable = targetView.drawable

                sourceView.setImageDrawable(targetDrawable)
                targetView.setImageDrawable(sourceDrawable)
            }
        }
        return true
    }

    override fun onDown(p0: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(p0: MotionEvent) {

    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {
        val view = shadowBuilder.view
        view.startDragAndDrop(
            null,
            shadowBuilder,
            null,
            0
        )
    }

    override fun onFling(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        return false
    }
}