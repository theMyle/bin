package com.example.desamparotourapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.desamparotourapp.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ActivityLogin : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var captchaText: String
    private lateinit var auth: FirebaseAuth

    // TEST FLAGS
    private val isTesting : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        // generate captcha image
        binding.captchaImage.post {
            captchaText = Captcha.generateCaptchaText()
            binding.captchaImage.setImageBitmap(Captcha.renderCaptchaImage(
                captchaText,
                binding.captchaImage.width,
                binding.captchaImage.height
            ))
        }

        // re-generate captcha image
        binding.btnRegenerate.setOnClickListener {
            captchaText = Captcha.generateCaptchaText()
            binding.captchaImage.setImageBitmap(Captcha.renderCaptchaImage(
                captchaText,
                binding.captchaImage.width,
                binding.captchaImage.height
            ))
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        // handle login
        binding.btnLogin.setOnClickListener {

            if (isTesting) {
                val testEmail = "admin@gmail.com"
                val testPassword = "adminadmin"
                performLogin(testEmail, testPassword)
                return@setOnClickListener
            }

            val email = binding.txtInputUsername.text.toString().trim()
            val password = binding.txtInputPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // validate captcha
            if (binding.txtInputCaptcha.text.toString() != captchaText) {
                Toast.makeText(this, "Invalid Captcha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // validate username and db
            performLogin(email, password)
        }
    }

    private fun performLogin(email:String, password:String) {
        // sanitize first and add needed padding
        var username = email
        var pass = password

        if (!email.contains("@")) {
            username = username.plus("@gmail.com")
        }

        // min password length is 6
        if (password.length < 6) {
            val padding_len = 6 - password.length
            val padding = "@".repeat(padding_len)
            pass = password.plus(padding)
        }

        auth.signInWithEmailAndPassword(username, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, ActivityHome::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = when (val error = task.exception) {
                        is FirebaseAuthInvalidUserException-> "User does not exist."
                        is FirebaseAuthInvalidCredentialsException -> "Incorrect password."
                        is FirebaseNetworkException -> "No Internet Connection."
                        else -> "Login Failed: ${error?.message}"
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }
}