package com.marusys.auto.music.playback.receiver

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.os.UserManager
import com.marusys.auto.music.playback.PlaybackService
import timber.log.Timber

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.i("onReceive: $intent")
        val userHandle: UserHandle = android.os.Process.myUserHandle()
        val userManager = context?.getSystemService(Context.USER_SERVICE) as UserManager?
        val userSerialNumber = userManager?.getSerialNumberForUser(userHandle)
        Timber.i("$userHandle User Serial Number: $userSerialNumber")
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val isMyServiceRunning = isMyServiceRunning(context, PlaybackService::class.java)
            if (!isMyServiceRunning && userSerialNumber != 0L) {
                context.startForegroundService(Intent(context, PlaybackService::class.java))
            }
        }
    }

    private fun isMyServiceRunning(context: Context?, serviceClass: Class<*>): Boolean {
        (context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?)?.let {
            for (service in it.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
        }
        return false
    }
}