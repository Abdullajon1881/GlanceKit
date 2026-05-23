package com.glancekit.demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.glancekit.demo.databinding.ActivityMainBinding
import dev.glancekit.androidcore.ProgressCardData
import dev.glancekit.androidcore.WidgetUpdateManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindIntent(intent?.data)
        binding.updateButton.setOnClickListener { submitUpdate() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        bindIntent(intent.data)
    }

    private fun bindIntent(uri: Uri?) {
        binding.deepLinkLabel.text = uri?.toString() ?: getString(R.string.deep_link_idle)
    }

    private fun submitUpdate() {
        val widgetId = binding.widgetIdInput.text?.toString()?.toIntOrNull()
        if (widgetId == null || widgetId <= 0) {
            binding.statusLabel.text = "Widget ID must be a positive integer."
            return
        }

        val data = ProgressCardData(
            title = binding.titleInput.text?.toString().orEmpty(),
            subtitle = binding.subtitleInput.text?.toString().orEmpty(),
            progress = binding.progressInput.text?.toString()?.toIntOrNull() ?: -1,
        )

        lifecycleScope.launch {
            try {
                val savedData = WidgetUpdateManager.updateProgressCardWidget(
                    context = applicationContext,
                    widgetId = widgetId,
                    data = data,
                )
                binding.statusLabel.text =
                    "Updated widget $widgetId with ${savedData.progress}% progress."
            } catch (error: IllegalArgumentException) {
                binding.statusLabel.text = error.message ?: "Invalid widget data."
            } catch (error: Exception) {
                val message = "Update failed: ${error.message ?: "unknown error"}"
                binding.statusLabel.text = message
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
