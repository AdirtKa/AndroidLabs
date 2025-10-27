package com.example.lab9menuapp

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.lab9menuapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerForContextMenu(binding.editTextInput)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_toggle_visibility -> {
                val checked = !item.isChecked
                item.isChecked = checked
                if (checked) {
                    binding.textViewInfo.visibility = View.VISIBLE
                    item.title = "Показать текст"
                } else {
                    binding.textViewInfo.visibility = View.GONE
                    item.title = "Скрыть текст"
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v == binding.editTextInput) {
            menuInflater.inflate(R.menu.context_menu, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_clear_text -> {
                binding.editTextInput.setText("")
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}
