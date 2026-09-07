package com.android.s22present

import android.animation.ObjectAnimator
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.view.isInvisible
import java.io.File

class ListenerService : Service()
{
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        Log.i("S22PresListServInit", "Hello!")
        val displaymanager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display0 = displaymanager.displays[0]
        val display1 = displaymanager.displays[1]
        val file = "settings"
        val filedir = File(filesDir, file)
        try
        {
            val settings = filedir.readText().split("|").toTypedArray()
            Globals.style = settings[0].toString()
            Globals.font = settings[1].toString()
        }
        catch (e: Exception)
        {
            Log.w("S22PresListServInit", "Failed to load settings. Continuing with defaults.")
        }
        val present = PresentationHandler(this, display1)
        present.show()
        Globals.loading.progress = 3
        Globals.loadingtext.text = "Done!"
        Log.i("S22PresListServInit", "Listening...")
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

class NotificationService : NotificationListenerService() {
    var currentnotifs : String? = null
    var musicactive = false
    var musicnotiftitle: String = ""
    var musicnotiftext: String = ""

    override fun onCreate() {
        Log.i("S22PresNotifServInit", "Listening...")
        super.onCreate()
    }

    // Gmail and Chat use styled text; getString() returns null for it.
    private fun titleOf(sbn: StatusBarNotification?): String =
        sbn?.notification?.extras?.getCharSequence("android.title")?.toString() ?: ""

    private fun textOf(sbn: StatusBarNotification?): String =
        sbn?.notification?.extras?.getCharSequence("android.text")?.toString() ?: ""

    private fun clearDisplay() {
        Globals.titlefield.text = ""
        Globals.contentfield.text = ""
        if (Globals.style != "3") {
            if (Globals.style != "4") {
                ObjectAnimator.ofFloat(Globals.timefield, "translationY", 0f).apply { duration = 500; start() }
            } else {
                ObjectAnimator.ofFloat(Globals.timefield, "translationY", -12.5f).apply { duration = 500; start() }
            }
            ObjectAnimator.ofFloat(Globals.datefield, "translationY", 0f).apply { duration = 500; start() }
            ObjectAnimator.ofFloat(Globals.titlefield, "translationY", 20f).apply { duration = 500; start() }
            ObjectAnimator.ofFloat(Globals.contentfield, "translationY", 20f).apply { duration = 500; start() }
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val title = titleOf(sbn)
        val text = textOf(sbn)
        if (title.isNotEmpty() || text.isNotEmpty()) {
            if (Globals.titlefield.text == "" && Globals.style != "3") {
                if (Globals.style != "4") {
                    ObjectAnimator.ofFloat(Globals.datefield, "translationY", -20f).apply { duration = 500; start() }
                    ObjectAnimator.ofFloat(Globals.timefield, "translationY", -20f).apply { duration = 500; start() }
                } else {
                    ObjectAnimator.ofFloat(Globals.datefield, "translationY", -18f).apply { duration = 500; start() }
                    ObjectAnimator.ofFloat(Globals.timefield, "translationY", -30f).apply { duration = 500; start() }
                }
                ObjectAnimator.ofFloat(Globals.titlefield, "translationY", 0f).apply { duration = 500; start() }
                ObjectAnimator.ofFloat(Globals.contentfield, "translationY", 0f).apply { duration = 500; start() }
            }
            if (title != Globals.titlefield.text) {
                Log.v("S22PresNotifServ", "Ping!")
                if (packageName == "it.vfsfitvnm.vimusic" || packageName == "com.google.android.apps.youtube.music" || packageName == "com.spotify.music" || packageName == "org.fossify.musicplayer" || packageName == "com.pandora.android" || packageName == "com.clearchannel.iheartradio.controller" || packageName == "com.soundcloud.android" || packageName == "com.amazon.mp3" || packageName == "com.sec.android.app.music" || packageName == "com.apple.android.music") {
                    Log.v("S22PresNotifServ", "Music")
                    musicactive = true
                    musicnotiftitle = title
                    musicnotiftext = text
                    when (Globals.visual) {
                        1 -> { Globals.visualbar.isInvisible = false }
                        2 -> { Globals.visualsquare.isInvisible = false }
                    }
                }
                Globals.titlefield.text = title
                Intent().also { broadcast ->
                    broadcast.setAction("com.android.s22present.NOTIFICATION_RECEIVED")
                    sendBroadcast(broadcast)
                }
            }
            if (text != Globals.contentfield.text) {
                Globals.contentfield.text = text
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.v("S22PresNotifServ", "Something got removed.")
        val title = titleOf(sbn)

        if (musicactive && title == musicnotiftitle) {
            Log.v("S22PresNotifServ", "Clearing music")
            when (Globals.visual) {
                1 -> { Globals.visualbar.isInvisible = true }
                2 -> { Globals.visualsquare.isInvisible = true }
            }
            musicactive = false
            musicnotiftitle = ""
            musicnotiftext = ""
        }

        // Don't try to match what was removed - ask what is still there.
        val remaining = try {
            activeNotifications?.filter {
                it.isClearable &&
                !it.notification.extras.getBoolean("android.isGroupSummary", false) &&
                titleOf(it).isNotEmpty()
            }
        } catch (e: Exception) {
            Log.w("S22PresNotifServ", "Couldn't read active notifications.")
            null
        }

        when {
            remaining == null -> { }
            musicactive -> {
                Globals.titlefield.text = musicnotiftitle
                Globals.contentfield.text = musicnotiftext
            }
            remaining.isEmpty() -> clearDisplay()
            else -> {
                if (Globals.titlefield.text.isNotEmpty()) {
                    val newest = remaining.maxByOrNull { it.postTime }
                    Globals.titlefield.text = titleOf(newest)
                    Globals.contentfield.text = textOf(newest)
                }
            }
        }
        super.onNotificationRemoved(sbn)
    }
}
