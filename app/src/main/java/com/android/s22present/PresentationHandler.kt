package com.android.s22present

import android.animation.ObjectAnimator
import android.app.Presentation
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextClock
import android.widget.TextView
import androidx.core.view.isInvisible
import com.chibde.visualizer.BarVisualizer
import com.chibde.visualizer.SquareBarVisualizer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

// Manages the Presentation and it's contents.
class PresentationHandler(context: Context, display: Display?): Presentation(context,display)
{
    private var batteryReceiver: BroadcastReceiver? = null
    private var volumeReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        Log.i("S22PresHandlerInit", "Presentation start triggered")
        Display.FLAG_PRESENTATION
        Display.FLAG_SECURE
        WindowManager.LayoutParams.FLAG_SECURE
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        super.onCreate(savedInstanceState)
        setContentView(R.layout.presentation)
        var today = LocalDateTime.now()
        var format = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        var localtoday = today.format(format)
        Globals.datefield = findViewById(R.id.textView2)
        Globals.datefield.text = localtoday
        Globals.titlefield = findViewById(R.id.textViewTitle)
        Globals.timefield = findViewById(R.id.textClock)
        Globals.contentfield = findViewById(R.id.textViewContent)
        val visual: BarVisualizer = findViewById(R.id.visualizerBar)
        val visualSquare: SquareBarVisualizer = findViewById(R.id.visualizerSquare)
        val digifont = resources.getFont(R.font.digital7)
        val pixelfont = resources.getFont(R.font.dogica)
        val submarinerfont = resources.getFont(R.font.submariner)
        Globals.visualbar = findViewById(R.id.visualizerBar)
        Globals.visualsquare = findViewById(R.id.visualizerSquare)
        visual.setColor(255255255)
        visualSquare.setColor(255255255)
        findViewById<ImageView>(R.id.imageView).isInvisible = true

        fun digifontset()
        {
            findViewById<TextView>(R.id.textClock).typeface=digifont
            findViewById<TextView>(R.id.textView2).typeface=digifont
            findViewById<TextView>(R.id.textViewTitle).typeface=digifont
            findViewById<TextView>(R.id.textViewContent).typeface=digifont
            findViewById<TextView>(R.id.textClock).textSize=17f
            findViewById<TextView>(R.id.textView2).textSize=17f
            findViewById<TextView>(R.id.textViewTitle).textSize=14f
            findViewById<TextView>(R.id.textViewContent).textSize=10f
        }
        fun pixelfontset()
        {
            findViewById<TextView>(R.id.textClock).typeface=pixelfont
            findViewById<TextView>(R.id.textView2).typeface=pixelfont
            findViewById<TextView>(R.id.textViewTitle).typeface=pixelfont
            findViewById<TextView>(R.id.textViewContent).typeface=pixelfont
            findViewById<TextView>(R.id.textViewContent).letterSpacing=-0.05f
            findViewById<TextView>(R.id.textViewContent).setLineSpacing(3f, 1f)
            findViewById<TextView>(R.id.textClock).letterSpacing=-0.05f
            findViewById<TextView>(R.id.textView2).letterSpacing=-0.05f
            findViewById<TextView>(R.id.textViewTitle).letterSpacing=-0.05f
            findViewById<TextView>(R.id.textClock).textSize=12f
            findViewById<TextView>(R.id.textView2).textSize=12f
            findViewById<TextView>(R.id.textViewTitle).textSize=10f
            findViewById<TextView>(R.id.textViewContent).textSize=9f
        }
        fun submarinerset()
        {
            findViewById<TextView>(R.id.textClock).typeface=submarinerfont
            findViewById<TextView>(R.id.textView2).typeface=submarinerfont
            findViewById<TextView>(R.id.textViewTitle).typeface=submarinerfont
            findViewById<TextView>(R.id.textViewContent).typeface=submarinerfont
        }
        // Visualiser disabled - no audio capture, no repainting.
        fun squarevis()
        {
            visualSquare.isEnabled = false
            visualSquare.isInvisible = true
            Globals.visual = 0
        }
        fun barvis()
        {
            visual.isEnabled = false
            visual.isInvisible = true
            Globals.visual = 0
        }
        fun normallayout()
        {
            ObjectAnimator.ofFloat(Globals.titlefield, "translationY", 20f).apply { duration = 5; start() }
            ObjectAnimator.ofFloat(Globals.contentfield, "translationY", 20f).apply { duration = 5; start() }
            ObjectAnimator.ofFloat(Globals.datefield, "translationY", 0f).apply { duration = 5; start() }
            ObjectAnimator.ofFloat(Globals.timefield, "translationY", 0f).apply { duration = 5; start() }
        }
        fun razrlayout()
        {
            ObjectAnimator.ofFloat(Globals.titlefield, "translationY", -10f).apply { duration = 5; start() }
            ObjectAnimator.ofFloat(Globals.contentfield, "translationY", -10f).apply { duration = 5; start() }
            ObjectAnimator.ofFloat(Globals.timefield, "translationY", 50f).apply { duration = 5; start() }
        }
        fun nokialayout()
        {
            ObjectAnimator.ofFloat(Globals.datefield, "translationY", 0f).apply { duration = 5; start() }
            ObjectAnimator.ofFloat(Globals.timefield, "translationY", -12.5f).apply { duration = 5; start() }
            ObjectAnimator.ofFloat(Globals.titlefield, "translationY", 20f).apply { duration = 5; start() }
            ObjectAnimator.ofFloat(Globals.contentfield, "translationY", 20f).apply { duration = 5; start() }
        }
        if(Globals.style=="0")
        {
            barvis()
            visual.setColor(255255255)
        }
        if(Globals.style=="1")
        {
            pixelfontset()
            squarevis()
            normallayout()
            findViewById<TextView>(R.id.textClock).setTextColor(Color.parseColor("#052745"))
            findViewById<TextView>(R.id.textView2).setTextColor(Color.parseColor("#052745"))
            findViewById<TextView>(R.id.textViewTitle).setTextColor(Color.parseColor("#052745"))
            findViewById<TextView>(R.id.textViewContent).setTextColor(Color.parseColor("#052745"))
            findViewById<View>(R.id.view).setBackgroundColor(Color.parseColor("#093c6c"))
            visualSquare.setColor(Color.parseColor("#10508c"))
        }
        if(Globals.style=="2")
        {
            digifontset()
            squarevis()
            normallayout()
            findViewById<TextView>(R.id.textClock).setTextColor(Color.parseColor("#cc0000"))
            findViewById<TextView>(R.id.textView2).setTextColor(Color.parseColor("#cc0000"))
            findViewById<TextView>(R.id.textViewTitle).setTextColor(Color.parseColor("#cc0000"))
            findViewById<TextView>(R.id.textViewContent).setTextColor(Color.parseColor("#cc0000"))
            visualSquare.setColor(Color.parseColor("#790000"))
        }
        if(Globals.style=="3")
        {
            findViewById<ImageView>(R.id.imageView).isInvisible = false
            findViewById<TextView>(R.id.textView2).isInvisible = true
            findViewById<TextView>(R.id.textClock).setTextColor(Color.parseColor("#000000"))
            findViewById<TextView>(R.id.textViewTitle).setTextColor(Color.parseColor("#000000"))
            findViewById<TextView>(R.id.textViewContent).setTextColor(Color.parseColor("#000000"))
            findViewById<TextView>(R.id.textClock).typeface=Typeface.DEFAULT_BOLD
            findViewById<TextView>(R.id.textViewTitle).typeface=Typeface.DEFAULT_BOLD
            findViewById<TextView>(R.id.textClock).scaleX=1.2f
            findViewById<TextView>(R.id.textClock).scaleY=1.4f
            visualSquare.setColor(Color.parseColor("#0721B3"))
            squarevis()
            razrlayout()
        }
        if(Globals.style=="4")
        {
            findViewById<TextClock>(R.id.textClock).format12Hour = "hh:mm"
            format = DateTimeFormatter.ofPattern("E, LLL dd")
            localtoday = today.format(format)
            findViewById<TextView>(R.id.textView2).text = localtoday
            findViewById<TextView>(R.id.textClock).scaleX=2f
            findViewById<TextView>(R.id.textClock).scaleY=2.25f
            barvis()
            nokialayout()
            submarinerset()
        }
        if(Globals.font=="1")
        {
            digifontset()
        }
        if(Globals.font=="2")
        {
            pixelfontset()
        }

        // Scroll long titles instead of clipping them.
        Globals.titlefield.isSingleLine = true
        Globals.titlefield.ellipsize = TextUtils.TruncateAt.MARQUEE
        Globals.titlefield.marqueeRepeatLimit = -1
        Globals.titlefield.isSelected = true

        // Date in the top left corner, replacing the one in the main layout.
        val dateText = Globals.datefield.text.toString()
        Globals.datefield.isInvisible = true

        // Fill the space the date left behind.
        Globals.timefield.translationY = Globals.timefield.translationY + 12f
        Globals.timefield.scaleX = Globals.timefield.scaleX * 1.25f
        Globals.timefield.scaleY = Globals.timefield.scaleY * 1.25f

        val accent = Globals.timefield.currentTextColor
        val dim = Color.argb(70, Color.red(accent), Color.green(accent), Color.blue(accent))

        val dateView = TextView(context)
        dateView.textSize = 11f
        dateView.setPadding(5, 2, 0, 0)
        dateView.typeface = Globals.timefield.typeface
        dateView.setTextColor(accent)
        dateView.text = dateText
        addContentView(
            dateView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.START
            )
        )

        // Battery percentage, pinned to the top right corner.
        val batteryView = TextView(context)
        batteryView.textSize = 11f
        batteryView.setPadding(0, 2, 5, 0)
        batteryView.typeface = Globals.timefield.typeface
        batteryView.setTextColor(accent)
        addContentView(
            batteryView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.END
            )
        )

        // Vertical volume bar, centred on the right edge. Hidden until the keys are pressed.
        val trackWidth = 5
        val trackHeight = 56
        val volTrack = FrameLayout(context)
        volTrack.setBackgroundColor(dim)
        val volFill = View(context)
        volFill.setBackgroundColor(accent)
        volTrack.addView(
            volFill,
            FrameLayout.LayoutParams(trackWidth, 0, Gravity.BOTTOM)
        )
        val volParams = FrameLayout.LayoutParams(
            trackWidth,
            trackHeight,
            Gravity.CENTER_VERTICAL or Gravity.END
        )
        volParams.rightMargin = 4
        addContentView(volTrack, volParams)
        volTrack.visibility = View.INVISIBLE

        val volHider = Handler(Looper.getMainLooper())
        val hideVol = Runnable { volTrack.visibility = View.INVISIBLE }

        fun showVolume()
        {
            try
            {
                val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val cur = am.getStreamVolume(AudioManager.STREAM_MUSIC)
                val filled = if (max > 0) (trackHeight * cur / max) else 0
                volFill.post {
                    val lp = volFill.layoutParams
                    lp.height = filled
                    volFill.layoutParams = lp
                }
            }
            catch (e: Exception)
            {
                Log.w("S22PresVolume", "Couldn't read volume.")
            }
        }

        volumeReceiver = object : BroadcastReceiver()
        {
            override fun onReceive(ctx: Context?, receivedIntent: Intent?)
            {
                showVolume()
                volTrack.post {
                    volTrack.visibility = View.VISIBLE
                    volHider.removeCallbacks(hideVol)
                    volHider.postDelayed(hideVol, 3000)
                }
            }
        }
        try
        {
            context.applicationContext.registerReceiver(
                volumeReceiver,
                IntentFilter("android.media.VOLUME_CHANGED_ACTION")
            )
        }
        catch (e: Exception)
        {
            Log.w("S22PresVolume", "Couldn't register volume receiver.")
        }

        fun showBattery(batteryIntent: Intent?)
        {
            val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level >= 0 && scale > 0)
            {
                val pct = level * 100 / scale
                Log.i("S22PresBattery", "Battery is $pct%")
                batteryView.post { batteryView.text = "$pct%" }
            }
            else
            {
                Log.w("S22PresBattery", "No battery data in intent.")
            }
        }

        batteryReceiver = object : BroadcastReceiver()
        {
            override fun onReceive(ctx: Context?, receivedIntent: Intent?)
            {
                showBattery(receivedIntent)
            }
        }
        try
        {
            val sticky = context.applicationContext.registerReceiver(
                batteryReceiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            showBattery(sticky)
        }
        catch (e: Exception)
        {
            Log.w("S22PresBattery", "Couldn't register battery receiver.")
        }

        Log.i("S22PresHandlerInit", "Presentation displayed")
    }

    override fun onStop()
    {
        batteryReceiver?.let {
            try { context.applicationContext.unregisterReceiver(it) } catch (e: Exception) { }
        }
        batteryReceiver = null
        volumeReceiver?.let {
            try { context.applicationContext.unregisterReceiver(it) } catch (e: Exception) { }
        }
        volumeReceiver = null
        super.onStop()
    }
}
