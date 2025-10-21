package com.yifeplayte.bleheartrate.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.yifeplayte.bleheartrate.R

class FloatingWindowService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null
    private var heartRateText: TextView? = null
    
    companion object {
        const val ACTION_UPDATE_HEART_RATE = "com.yifeplayte.bleheartrate.UPDATE_HEART_RATE"
        const val EXTRA_HEART_RATE = "heart_rate"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        floatingView = inflater.inflate(R.layout.floating_window, null)
        heartRateText = floatingView?.findViewById(R.id.heart_rate_text)
        
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 100
        }
        
        windowManager?.addView(floatingView, params)
        
        // Make the floating window draggable
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        
        floatingView?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(floatingView, params)
                    true
                }
                else -> false
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.action == ACTION_UPDATE_HEART_RATE) {
                val heartRate = it.getIntExtra(EXTRA_HEART_RATE, 0)
                updateHeartRate(heartRate)
            }
        }
        return START_STICKY
    }

    private fun updateHeartRate(heartRate: Int) {
        heartRateText?.text = "$heartRate BPM"
    }

    override fun onDestroy() {
        super.onDestroy()
        floatingView?.let { windowManager?.removeView(it) }
    }
}
