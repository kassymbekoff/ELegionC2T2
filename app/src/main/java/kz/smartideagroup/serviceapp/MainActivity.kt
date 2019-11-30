package kz.smartideagroup.serviceapp

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var progressService : ProgressService
    private lateinit var tvShow: TextView
    private lateinit var btnShow: Button
    private lateinit var progressBar: ProgressBar
    private var countReceiver:CountReceiver? = null
    private var intentFilter:IntentFilter? = null

    private val onProgressCountUpdateListener = object : OnProgressCountUpdateListener {
        override fun onUpdate(counter: Int) {
            progressBar.setProgress(counter)
            tvShow.setText("Загрузка: $counter %")
            if(counter == 100){
                Toast.makeText(this@MainActivity, "Загрузка завершена", Toast.LENGTH_LONG).show()
                tvShow.text = "Загрузка завершена"
            }
        }
    }
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val binder = service as ProgressService.LocalBinder
            progressService = binder.getService()
            progressService.startProgress()
        }

        override fun onServiceDisconnected(className: ComponentName?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvShow = findViewById(R.id.tvPercentValue)
        btnShow = findViewById(R.id.btnShow)
        progressBar = findViewById(R.id.progressBar)

        btnShow.setOnClickListener {
            progressService.setCounter()
        }

        val serviceIntent = Intent(this, ProgressService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        countReceiver = CountReceiver(onProgressCountUpdateListener)
        intentFilter = IntentFilter(CountReceiver.SIMPLE_ACTION)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(countReceiver, intentFilter)
    }

    override fun onPause(){
        super.onPause()
        unregisterReceiver(countReceiver)
    }
}
