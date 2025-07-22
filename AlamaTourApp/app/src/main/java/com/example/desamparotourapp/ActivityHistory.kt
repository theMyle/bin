package com.example.desamparotourapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.desamparotourapp.databinding.ActivityHistoryBinding

class ActivityHistory : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // take all extra info from intent
        val munName = intent.getStringExtra("municipalityName").toString()
        val munDescription = intent.getStringExtra("municipalityDescription").toString()
        val munLogoUri = intent.getStringExtra("municipalityLogoUri").toString()

        // load all
        binding.txtViewMunicipalityName.text = munName
        binding.txtViewMunicipalityDescription.text = munDescription
        Glide.with(this)
            .load(munLogoUri)
            .into(binding.imageViewMunicipalityLogo)
    }
}
