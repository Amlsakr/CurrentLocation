package com.example.currentlocation

import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    companion object {
        var REQUEST_CODE_LOCATION_PERMISSION = 1
    }
    private val sharedPrefFile = "kotlinsharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences: SharedPreferences = getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)

        var buttonStartService = findViewById<Button>(R.id.buttonStartLocationUpdates)
        var buttonStopService = findViewById<Button>(R.id.buttonStopLocationUpdates)
        var textView = findViewById<TextView>(R.id.textView)

        var cont = sharedPreferences.getString("shutdown" , "no thing stored")

        textView.text = cont

        buttonStartService.setOnClickListener {
            if(ContextCompat.checkSelfPermission(applicationContext , android.Manifest.permission.ACCESS_FINE_LOCATION )!= PackageManager.PERMISSION_GRANTED
                &&
                (ContextCompat.checkSelfPermission(applicationContext , android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this , arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION , android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_CODE_LOCATION_PERMISSION )
            }else {
                startLocationService()
            }
        }

        buttonStopService.setOnClickListener {
            stopLocationService()
        }

        val br: BroadcastReceiver = MyBroadcastReceiver()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction(Intent.ACTION_SHUTDOWN)
            addAction(Intent.ACTION_BOOT_COMPLETED)
            addAction(Intent.ACTION_INPUT_METHOD_CHANGED)
        }
        registerReceiver(br, filter)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.size > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService()
            }else {
                Toast.makeText(this , "Permission Denied",Toast.LENGTH_LONG).show()
            }
        }
    }

    private  fun isLocationServiceRunning (): Boolean {
        var activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if(activityManager != null){
            for (item in  activityManager.getRunningServices(Int.MAX_VALUE)){

                if(LocationService::class.java.name.equals(item.service.className)){
                    if (item.foreground){
                        return true
                    }
                    }

            }
        }
        return false
    }


    private fun startLocationService (){
        if(!isLocationServiceRunning()){
            var intent = Intent(applicationContext , LocationService::class.java)
            intent.setAction(Constants.ACTION_LOCATION_START_SERVICE)
           startService(intent)

            Toast.makeText(this,"Location Service Started",Toast.LENGTH_LONG).show()
        }
    }

    private fun stopLocationService(){
        if(isLocationServiceRunning()){
            var intent = Intent(applicationContext , LocationService::class.java)
            intent.setAction(Constants.ACTION_LOCATION_STOP_SERVICE)
            stopService(intent)
            Toast.makeText(this,"Location Service stopped",Toast.LENGTH_LONG).show()
        }
    }
}