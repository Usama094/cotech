package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        val loginButton: Button = findViewById(R.id.loginButton)
        val signupButton: Button = findViewById(R.id.signupButton)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        loginButton.setOnClickListener {
            // Open LoginActivity
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            // Validate the input fields
            if (validateInput()) {
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                // Create user with email and password
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign up success, save user to Firebase Realtime Database
                            val user = User(name, email, username, password)
                            val reference = database.getReference("users")
                            reference.child(username).setValue(user)

                            // Redirect to HomePage
                            val intent = Intent(this@SignupActivity, HomePage::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                // Email already exists
                                Toast.makeText(
                                    baseContext,
                                    "Email already exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Other errors
                                Toast.makeText(
                                    baseContext, "Sign up failed. Please try again later.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
            }
        }
    }

    private fun validateInput(): Boolean {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            // Show error message if any field is empty
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Show error message if email is not in a valid format
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 8) {
            // Show error message if password is too short
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate password format
        val passwordRegex = "(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#\$%^&*()-+]).{8,}"
        if (!password.matches(passwordRegex.toRegex())) {
            // Show error message if password format is incorrect
            Toast.makeText(this, "Password must contain at least one uppercase letter, one lowercase letter, and one special character", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    data class User(val name: String, val email: String, val username: String, val password: String)
}
