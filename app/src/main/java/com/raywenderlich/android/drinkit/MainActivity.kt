package com.raywenderlich.android.drinkit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

// TODO: import libraries

/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  lateinit var button_retrieve_token: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // TODO: create OnClickListener for the button_retrieve_token

    button_retrieve_token = findViewById(R.id.button_retrieve_token)

    val bundle = intent.extras
    if (bundle != null){
      text_view_notification.text = bundle.getString("Text")
    }

    if (checkGooglePlayServiceEnable()) {
      button_retrieve_token.setOnClickListener {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                  if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                  }

                  val token = task.result?.token

                  val msg = getString(R.string.token_prefix, token)
                  Log.d("Debug", msg)

                  Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()

                })
      }
    }else{
      Log.e(TAG, "Devices does not have google play service")
    }

    // TODO: check in bundle extras for notification data
  }

  private fun checkGooglePlayServiceEnable(): Boolean{

    var status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

    return if (status != ConnectionResult.SUCCESS){
      Log.e(TAG, "error")
      false
    }else{
      Log.i(TAG, "Google play service updated")
      true
    }
  }

  override fun onStart() {
    super.onStart()
    //TODO: Register the receiver for notifications

    LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter("MyData"))
  }

  override fun onStop() {
    super.onStop()
    LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    // TODO: Unregister the receiver for notifications
  }

  // TODO: Add a method for receiving notifications

  // TODO: Add a function to check for Google Play Services

  // TODO: Create a message receiver constant

  private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
      text_view_notification.text = intent?.extras?.getString("message")
    }

  }

  companion object {
    private const val TAG = "MainActivity"
  }
}