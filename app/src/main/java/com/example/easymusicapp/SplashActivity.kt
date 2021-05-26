package com.example.easymusicapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    var a = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECORD_AUDIO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!checkPermissions(this@SplashActivity, a)){
            //Ask this device for permissions
            ActivityCompat.requestPermissions(this@SplashActivity,a, 131)
        }else{
            val handler = Handler()
            handler.postDelayed({               // Delay start activity
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                this.finish()
            }, 1000)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            131 -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    val handler = Handler()
                    handler.postDelayed({
                        val startAct = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(startAct)
                        this.finish()
                    }, 1000)
                } else {
                    Toast.makeText(this@SplashActivity, "Lỗi. Không đủ các quyền", Toast.LENGTH_SHORT).show()
                    this.finish()
                }
                return
            }
            else->{
                Toast.makeText(this@SplashActivity, "Lỗi", Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }
        }
    }

    private fun checkPermissions (context: Context, permissions: Array<String>): Boolean {
        var hasAllPermissions = true
        for (permission in permissions){
            val result = context.checkCallingOrSelfPermission(permission) // Determine whether you have been granted a particular permission
            if (result != PackageManager.PERMISSION_GRANTED){
                hasAllPermissions = false
            }
        }
        return hasAllPermissions
    }

}
