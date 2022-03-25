package com.example.currentlocation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

class MyBroadcastReceiver : BroadcastReceiver () {
    private  val TAG = "MyBroadcastReceiver"
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onReceive(context: Context, intent: Intent) {

        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(TAG, log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }

        var intent = Intent(context , LocationService::class.java)
        intent.setAction(Constants.ACTION_LOCATION_START_SERVICE)
        context?.let { ContextCompat.startForegroundService(it,intent) }
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()


        if(intent.action.equals(Intent.ACTION_BOOT_COMPLETED)){
            var mainIntent = Intent(context ,MainActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mainIntent)

        }

        if(intent.action.equals(Intent.ACTION_SHUTDOWN , true)  ){
            Toast.makeText(context, "now shutdown koko koko wawa milk", Toast.LENGTH_LONG).show()
            Log.e("shutdown","now shutdown koko koko wawa milk")
            editor.putString("shutdown","now shutdown koko koko wawa milk")
            editor.apply()
            editor.commit()
        }
    }
}