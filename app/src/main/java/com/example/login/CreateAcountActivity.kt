package com.example.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.login.databinding.ActivityCreateAcountBinding
import com.example.login.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CreateAcountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAcountBinding
    private lateinit var auth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient? = null
    val TAG = "CreateActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAcountBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        initViews()
        initGoogle()
        setSectionTitle()
    }

    private fun initGoogle() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        auth = Firebase.auth
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun initViews() {
        val btSignUp = binding.btSignUp

        btSignUp.setOnClickListener {
            if ((binding.etSignUpPass.text.toString() == binding.etSignUpConfirmPass.text.toString()) && binding.etSignUpPass.text.length > 5) {
                createAccount(
                    binding.etSignUpEmail.text.toString(),
                    binding.etSignUpPass.text.toString()
                )
            } else {
                if (binding.etSignUpPass.text.toString() != binding.etSignUpConfirmPass.text.toString()) {
                    Toast.makeText(
                        baseContext, "Las contraseñas no coinciden",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext, "La contraseña debe tener al menos 6 caracteres!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun createAccount(email: String, password: String) {

        Log.v(TAG, "Email: $email, password: $password")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    //updateUI(user)
                    changeActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //updateUI(null)
                    changeActivity()
                }
            }
    }

    private fun setSectionTitle() {
        title = getString(R.string.titleRegister)
    }

    private fun changeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        finish()
        startActivity(intent)
    }
}