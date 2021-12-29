package com.telechaplaincy

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.telechaplaincy.chaplain_sign_activities.ChaplainLogIn
import com.telechaplaincy.network_connection.ConnectionType
import com.telechaplaincy.network_connection.NetworkMonitorUtil
import com.telechaplaincy.patient_sign_activities.LoginPage
import kotlinx.android.synthetic.main.activity_main_entry.*

class MainEntry : AppCompatActivity() {

    private val networkMonitor = NetworkMonitorUtil(this)
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_entry)

        main_entry_admin_textView.setOnClickListener {
            val intent = Intent(this, ChaplainLogIn::class.java)
            intent.putExtra("admin_login", 3)
            startActivity(intent)
            finish()
        }

        patient_entry_button.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
        chaplain_entry_button.setOnClickListener {
            val intent = Intent(this, ChaplainLogIn::class.java)
            startActivity(intent)
            finish()
        }

        networkMonitor.result = { isAvailable, type ->
            runOnUiThread {
                when (isAvailable) {
                    true -> {
                        when (type) {
                            ConnectionType.Wifi -> {
                                Log.i("NETWORK_MONITOR_STATUS", "Wifi Connection")
                            }
                            ConnectionType.Cellular -> {
                                Log.i("NETWORK_MONITOR_STATUS", "Cellular Connection")
                            }
                            else -> {
                            }
                        }
                    }
                    false -> {
                        //MoviesFragment().alert.dismiss()
                        dialog = Dialog(this)
                        Log.i("NETWORK_MONITOR_STATUS", "No Connection")
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.internet_connection_layout)
                        val yesBtn = dialog.findViewById(R.id.buttonCustomDialogTryAgain) as Button
                        yesBtn.setOnClickListener {
                            val intent = Intent(this, MainEntry::class.java)
                            startActivity(intent)
                            finish()
                        }
                        dialog.show()
                    }
                }
            }
        }
    }
    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }

    override fun onResume() {
        super.onResume()
        networkMonitor.register()

    }

}