package com.example.thirdlab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thirdlab.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val received = intent.getStringExtra(MainActivity.EXTRA_USER_TEXT).orEmpty()
        binding.tvResult.text = received.ifBlank { "(пусто)" }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
