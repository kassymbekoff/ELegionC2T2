package kz.smartideagroup.serviceapp

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ProgressService : Service() {

    private var counter: Int = 0
    private var mScheduledExecutorService: ScheduledExecutorService? = null
    private val binder: IBinder = LocalBinder()
    private var isWorking = false

    override fun onBind(intent: Intent): IBinder {
        mScheduledExecutorService = Executors.newScheduledThreadPool(1)
        return binder
    }

    fun startProgress() {
        isWorking = true
        mScheduledExecutorService?.scheduleAtFixedRate({
            incrementCounter()
            val intent = Intent(CountReceiver.SIMPLE_ACTION)
            intent.putExtra("COUNTER", counter)
            sendBroadcast(intent)
        }, 1000, 200, TimeUnit.MILLISECONDS)
    }

    fun setCounter(){
        if(isWorking){
            if(counter > 49){
                counter -= 50
            }else{
                counter  = 0
            }
        }else{
            counter = 0
            mScheduledExecutorService = Executors.newScheduledThreadPool(1)
            startProgress()
        }

    }

    private fun incrementCounter(){
        counter += 5
        if(counter > 99){
            mScheduledExecutorService?.shutdownNow()
            isWorking = false
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() : ProgressService {
            return this@ProgressService
        }
    }

    override fun unbindService(conn: ServiceConnection) {
        mScheduledExecutorService?.shutdownNow()
        isWorking = false
        super.unbindService(conn)
    }

    override fun onDestroy() {
        mScheduledExecutorService?.shutdownNow()
        isWorking = false
    }
}
