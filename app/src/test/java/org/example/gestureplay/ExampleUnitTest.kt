package org.example.gestureplay

import android.os.Parcel
import android.os.SystemClock
import android.view.DragEvent
import android.view.InputDevice
import android.view.MotionEvent
import android.widget.GridLayout
import android.widget.ImageView
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.core.IsSame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class ChessActivityTest {

    private lateinit var activityController: ActivityController<ChessActivity>

    private lateinit var activity: ChessActivity


    @Before
    fun setUp() {

        activityController = Robolectric.buildActivity(ChessActivity::class.java).setup()

        activity = activityController
            .get()
    }

    @Test
    fun testDragMotion() {
        val shadowLooper = shadowOf(activity.mainLooper)

        val initialTouchTime = SystemClock.uptimeMillis()

        val board = activity.findViewById<GridLayout>(R.id.board)
        val view0 = board.getChildAt(0)
        val view4 = board.getChildAt(4)

        val motionEvent0 = MotionEvent.obtain(
            initialTouchTime,
            initialTouchTime,
            MotionEvent.ACTION_DOWN,
            view0.x, view0.y, 0
        ).apply {
            source = InputDevice.SOURCE_TOUCHSCREEN
        }

        board.dispatchTouchEvent(motionEvent0)

        shadowLooper.idleFor(500, TimeUnit.MILLISECONDS)


        val event1Time = SystemClock.uptimeMillis()
        val motionEvent1 = MotionEvent.obtain(initialTouchTime, event1Time, MotionEvent.ACTION_CANCEL, view0.x, view0.y, 0).apply {
            source = InputDevice.SOURCE_TOUCHSCREEN
        }
        board.dispatchTouchEvent(motionEvent1)

        shadowLooper.idleFor(500, TimeUnit.MILLISECONDS)

        board.dispatchDragEvent(mkDragEvent(view0.x, view0.y, DragEvent.ACTION_DRAG_STARTED))
//         dragStart notifies every view
//        board.forEach {
//            //position is relative to view
//            val eventDragStarted: DragEvent =
//                mkDragEvent(view0.x - it.x, view0.y - it.y, DragEvent.ACTION_DRAG_STARTED)
//            it.dispatchDragEvent(eventDragStarted)
//        }

//        // view that is hosting the DragEvent receives drag entered   // happens automatically
//        val eventEntered0 = mkDragEvent(view0.x, view0.y, DragEvent.ACTION_DRAG_ENTERED)
//        view0.dispatchDragEvent(eventEntered0)
//        shadowLooper.idleFor(50, TimeUnit.MILLISECONDS)

        // move until halfway down between view0 and view4
        val halfwayY0 = view0.height / 2f
        val eventMove0To4 = mkDragEvent(0f, halfwayY0, DragEvent.ACTION_DRAG_LOCATION)
        view0.dispatchDragEvent(eventMove0To4)
        shadowLooper.idleFor(50, TimeUnit.MILLISECONDS)

//        // move until border
//        val border = view4.y - view0.y
//        val eventMove0To4Border = mkDragEvent(0f, border, DragEvent.ACTION_DRAG_LOCATION)
//        view0.dispatchDragEvent(eventMove0To4Border)
//        shadowLooper.idleFor(50, TimeUnit.MILLISECONDS)
//
//        // exit view0   //happens automatically
//        val eventExit0 = mkDragEvent(0f, border, DragEvent.ACTION_DRAG_EXITED)
//        view0.dispatchDragEvent(eventExit0)
//        shadowLooper.idleFor(50, TimeUnit.MILLISECONDS)
//
//
//        // enter view4    //happens automatically
//        val eventEntered4 = mkDragEvent(0f, border, DragEvent.ACTION_DRAG_ENTERED)
//        view4.dispatchDragEvent(eventEntered4)
//        shadowLooper.idleFor(50, TimeUnit.MILLISECONDS)

        // move until halfway down between view0 and view4
        val halfwayY4 = view4.height / 2f
        val eventMove4 = mkDragEvent(0f, halfwayY4, DragEvent.ACTION_DRAG_LOCATION)
        view4.dispatchDragEvent(eventMove4)
        shadowLooper.idleFor(50, TimeUnit.MILLISECONDS)

        // drop
        val eventDrop4 = mkDragEvent(0f, halfwayY4, DragEvent.ACTION_DROP)
        view4.dispatchDragEvent(eventDrop4)
        shadowLooper.idleFor(50, TimeUnit.MILLISECONDS)

        // endDrag
        board.dispatchDragEvent(mkDragEvent(0f, 0f, DragEvent.ACTION_DRAG_ENDED))

//        board.forEach {
//            val eventDragStarted: DragEvent =
//                mkDragEvent(0f, 0f, DragEvent.ACTION_DRAG_ENDED)
//            it.dispatchDragEvent(eventDragStarted)
//        }
        shadowLooper.idleFor(50, TimeUnit.MILLISECONDS)


        assertNull("", (view0 as ImageView).drawable)
        assertEquals("", R.drawable.lonely_chess_king_24, shadowOf((view4 as ImageView).drawable).createdFromResId)
    }

    fun mkDragEvent(x: Float, y: Float, type: Int): DragEvent {
        val parcel: Parcel = Parcel.obtain().apply {
            writeInt(type)
            writeFloat(x)
            writeFloat(y)
            writeInt(0) // Result
            writeInt(0) // No Clipdata
            writeInt(0) // No Clip Description
            setDataPosition(0)
        }

        return DragEvent.CREATOR.createFromParcel(parcel)
    }

    @Test
    fun testingDragMotionWithEspresso() {

        val scenario = launch(ChessActivity::class.java)

        scenario.onActivity { activity ->
            val board = activity.findViewById<GridLayout>(R.id.board)

            val view0 = board.getChildAt(0)
            val view4 = board.getChildAt(4)


            onView(IsSame(view0)).perform(GeneralSwipeAction(
                Swipe.SLOW,
                { brd -> floatArrayOf(view0.x, view0.y) },
                { brd -> floatArrayOf(view4.x, view4.y) },
                { floatArrayOf(10f, 10f) }
            ))
            onView(IsSame(view0)).perform(ViewActions.click())

            assertNull("", (view0 as ImageView).drawable)
            assertEquals("", R.drawable.lonely_chess_king_24, shadowOf((view4 as ImageView).drawable).createdFromResId)


            shadowOf(activity.mainLooper).idleFor(500, TimeUnit.MILLISECONDS)
        }
    }
}