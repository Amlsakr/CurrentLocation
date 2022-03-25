package com.example.currentlocation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationService : Service() {

   var locationCallback: LocationCallback = object  : LocationCallback(){

       override fun onLocationResult(locationResult: LocationResult) {
           super.onLocationResult(locationResult)
           if(locationResult.lastLocation != null) {
               var lat = locationResult.lastLocation.latitude
               var longitude = locationResult.lastLocation.longitude
               Log.e("location", "location latitude" + lat + "lastLocation.longitude" + longitude)
               Toast.makeText(this@LocationService ,"location latitude" + lat + "lastLocation.longitude" + longitude
               ,Toast.LENGTH_SHORT).show()

           }
       }
  }



    override fun onBind(p0: Intent?): IBinder? {
throw  UnsupportedOperationException("Not yet Implemented")
    }

    private fun startLocationService (){

        var channelID = "Location_Notification_Channel"
        var notificationManager : NotificationManager? = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var resultIntent = Intent()
        var pendingIntent = PendingIntent.getActivity(applicationContext ,0 , resultIntent ,
            PendingIntent.FLAG_MUTABLE)

        var builder = NotificationCompat.Builder(applicationContext , channelID)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("Location Service")
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        builder.setContentText("Running")
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(false)
        builder.setPriority(NotificationCompat.PRIORITY_MAX)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(notificationManager != null && notificationManager.getNotificationChannel(channelID) == null){
                var notificationChannel = NotificationChannel(channelID,"Location Service" , NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.description= "This channel is used by location service"
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        var locationRequest = LocationRequest()
        locationRequest.setInterval(120000)
        locationRequest.setFastestInterval(120000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest,locationCallback , Looper.getMainLooper())

        startForeground(Constants.LOCATION_SERVICE_ID ,builder.build())
    }

    private fun stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent!= null){
            var action = intent.action
            if(action != null) {
if (action.equals(Constants.ACTION_LOCATION_START_SERVICE)){
    startLocationService()
}else if (action.equals(Constants.ACTION_LOCATION_STOP_SERVICE)){
    stopLocationService()
}
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startLocationService()
    }
}