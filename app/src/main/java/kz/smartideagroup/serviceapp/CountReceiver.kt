package kz.smartideagroup.serviceapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CountReceiver(var onProgressCountUpdateListener: OnProgressCountUpdateListener): BroadcastReceiver() {

    companion object {
        const val SIMPLE_ACTION = "kz.smartideagroup.serviceapp.UPDATE_COUNT_ACTION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val counter = intent?.getIntExtra("COUNTER", 0)
        onProgressCountUpdateListener.onUpdate(counter!!)
    }
}