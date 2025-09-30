package com.example.thirdlab

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.thirdlab.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val EXTRA_USER_TEXT = "EXTRA_USER_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnNext.setOnClickListener {
            val text = binding.etInput.text?.toString().orEmpty()
            val intent = Intent(this, SecondActivity::class.java).apply {
                putExtra(EXTRA_USER_TEXT, text)
            }
            startActivity(intent)
        }
    }
}
