package com.example.desamparotourapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.desamparotourapp.databinding.ActivityDestinationDescriptionBinding

class ActivityDestinationDescription : AppCompatActivity() {
    private lateinit var binding: ActivityDestinationDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDestinationDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra("name")
        val description = intent.getStringExtra("description")
        val imageUri = intent.getStringExtra("imageUri")

        binding.txtViewDestName.text = name
        binding.txtViewDescription.text = description
        Glide.with(this)
            .load(imageUri)
            .fallback(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(binding.imageView)
    }
}