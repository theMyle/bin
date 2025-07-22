package com.example.desamparotourapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.desamparotourapp.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firestore.v1.FirestoreGrpc

class ActivityHome : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var currentSelectedButton: Button
    private lateinit var buttonMap: Map<Button, String>
    private lateinit var cachedSpotCount: Map<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        buttonMap = mapOf(
            binding.btnDonsol to "Donsol",
            binding.btnPilar to "Pilar",
            binding.btnCastilla to "Castilla",
            binding.btnSorsogon to "Sorsogon",
            binding.btnPrietoDiaz to "PrietoDiaz",
            binding.btnGubat to "Gubat",
            binding.btnCasiguran to "Casiguran",
            binding.btnBarcelona to "Barcelona",
            binding.btnJuban to "Juban",
            binding.btnBulusan to "Bulusan",
            binding.btnIrosin to "Irosin",
            binding.btnMagallanes to "Magallanes",
            binding.btnBulan to "Bulan",
            binding.btnSantaMagdalena to "SantaMagdalena",
            binding.btnMatnog to "Matnog"
        )

        for ((button, name) in buttonMap) {
            button.setOnClickListener {
                handleButtonClick(button)
            }
        }

        // set donsol as initial selected
        currentSelectedButton = binding.btnDonsol
        setSelectedButton(currentSelectedButton)

    }

    private fun handleButtonClick(button: Button) {
        if (currentSelectedButton == button) {
            // do something
            val intent = Intent(this, ActivityMunicipality::class.java)
            intent.putExtra("municipality", buttonMap[button] ?: "")
            startActivity(intent)
        }

        FirebaseFirestore.getInstance().collection("municipalities")
            .document(buttonMap[button]?.lowercase() as String)
            .get()
            .addOnSuccessListener { doc ->
                val spots = doc["tourist_spots"] as? List<Map<String, Any>>
                val spotCount = spots?.size ?: 0
                val message = "${buttonMap[button] ?: "Unkown"} has $spotCount tourist spots"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }

        setSelectedButton(button)
    }

    private fun setSelectedButton(button: Button) {
        // turn off previous selected button
        currentSelectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.highlight_off))

        // highlight new selected button
        currentSelectedButton = button
        currentSelectedButton.setBackgroundColor(ContextCompat.getColor(this, R.color.highlight))
    }
}
