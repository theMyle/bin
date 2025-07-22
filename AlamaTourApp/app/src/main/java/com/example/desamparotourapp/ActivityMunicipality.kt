package com.example.desamparotourapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.desamparotourapp.databinding.ActivityMunicipalityBinding
import com.google.firebase.firestore.FirebaseFirestore

class ActivityMunicipality : AppCompatActivity() {

    private lateinit var binding: ActivityMunicipalityBinding
    private lateinit var municipalityID: String
    private lateinit var touristSpotsContainer: LinearLayout

    // Cache all tourist spots
    private var cachedTouristSpots: List<Map<String, Any>> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMunicipalityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Handle search bar input
        binding.editTextSearchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim()?.lowercase() ?: ""

                if (query.isEmpty()) {
                    displayTouristSpots(cachedTouristSpots)
                } else {
                    val filtered = cachedTouristSpots.filter { spot ->
                        val name = spot["name"] as? String ?: ""
                        val location = spot["location"] as? String ?: ""
                        val amenities = spot["amenities"] as? List<String> ?: emptyList()

                        name.lowercase().contains(query) ||
                                location.lowercase().contains(query) ||
                                amenities.any { it.lowercase().contains(query) }
                    }
                    displayTouristSpots(filtered)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        municipalityID = intent.getStringExtra("municipality").toString().lowercase()
        touristSpotsContainer = findViewById(R.id.touristSpotsContainer)

        loadTouristDestination()

        // Show content after fetching
        binding.main.visibility = View.VISIBLE
    }

    private fun loadTouristDestination() {
        FirebaseFirestore.getInstance().collection("municipalities")
            .document(municipalityID)
            .get()
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch data from DB", Toast.LENGTH_SHORT).show()
                binding.txtViewMunName.text = "No Internet"
                binding.txtViewNoOfTouristDest.text = "No Internet"
                binding.imgViewLogo.setImageResource(R.drawable.ic_launcher_foreground)
                binding.main.visibility = View.VISIBLE
            }
            .addOnSuccessListener { doc ->
                val munName = doc.getString("name")
                binding.txtViewMunName.text = munName

                val description = doc.getString("description")
                val logoUri = doc.getString("logo_uri")

                Glide.with(this)
                    .load(logoUri)
                    .into(binding.imgViewLogo)

                val spots = doc["tourist_spots"] as? List<Map<String, Any>>
                cachedTouristSpots = spots ?: emptyList()
                cachedTouristSpots = cachedTouristSpots.shuffled()

                val spotCount = cachedTouristSpots.size
                binding.txtViewNoOfTouristDest.text = "$spotCount Tourist Destination"

                displayTouristSpots(cachedTouristSpots)

                binding.btnReadHistory.setOnClickListener {
                    val intent = Intent(this, ActivityHistory::class.java)
                    intent.putExtra("municipalityName", munName)
                    intent.putExtra("municipalityDescription", description)
                    intent.putExtra("municipalityLogoUri", logoUri)
                    startActivity(intent)
                }
            }
    }

    private fun displayTouristSpots(spots: List<Map<String, Any>>) {
        touristSpotsContainer.removeAllViews()
        for (spot in spots) {
            val name = spot["name"] as? String ?: ""
            val fee = spot["fee"] as? String ?: ""
            val rating = spot["rating"] as? String ?: ""
            val location = spot["location"] as? String ?: ""
            val description = spot["description"] as? String ?: ""
            val imageUri = spot["image_uri"] as? String ?: ""

            addTouristDestination(location, fee, imageUri, rating, description, name)
        }
    }

    private fun addTouristDestination(
        location: String,
        entranceFee: String,
        imageUri: String,
        rating: String,
        description: String,
        name: String
    ) {
        val itemView = layoutInflater.inflate(R.layout.tourist_spot_item, touristSpotsContainer, false)

        itemView.findViewById<TextView>(R.id.txtViewEntranceFee).text = entranceFee
        itemView.findViewById<TextView>(R.id.txtViewLocation).text = location
        itemView.findViewById<TextView>(R.id.txtViewRating).text = rating

        Glide.with(this)
            .load(imageUri)
            .fallback(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(itemView.findViewById(R.id.ImageViewDestinationLogo))

        itemView.findViewById<Button>(R.id.btnReadMoreDestSpot).setOnClickListener {
            val intent = Intent(this, ActivityDestinationDescription::class.java)
            intent.putExtra("name", name)
            intent.putExtra("description", description)
            intent.putExtra("imageUri", imageUri)
            startActivity(intent)
        }

        touristSpotsContainer.addView(itemView)
    }
}
