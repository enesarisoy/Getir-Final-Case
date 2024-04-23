package com.ns.getirfinalcase.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.ns.getirfinalcase.MainActivity
import com.ns.getirfinalcase.R
import com.ns.getirfinalcase.presentation.custom.CustomLoadingView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<CustomLoadingView>(R.id.progressBar)
        val customLoadingView = progressBar.findViewById<LottieAnimationView>(R.id.lottieAnimationView)

        CoroutineScope(Dispatchers.Main).launch {
            delay(4000L)
            customLoadingView.cancelAnimation()
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}