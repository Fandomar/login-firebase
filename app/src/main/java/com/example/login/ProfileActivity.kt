package com.example.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.login.databinding.ActivityHomeBinding
import com.example.login.databinding.ActivityProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var auth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient? = null
    val TAG = "ProfileActivity"
    var currentUser: FirebaseUser? = null
    val db = Firebase.firestore
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initGoogle()
        initViews()
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
        currentUser = Firebase.auth.currentUser

        val btSave = binding.btSave

        btSave.setOnClickListener {
            if (isNotEmpty() && user != null) {
                setNewUserData()
                saveData()
            }
        }

        leerDatosUsuario()
    }

    private fun saveData() {

        currentUser?.uid?.let { Log.v(TAG, it) }

        currentUser?.uid?.let {
            db.collection("users").document(it)
                .set(user!!)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        }

        Toast.makeText(
            baseContext, "Data has been updated!",
            Toast.LENGTH_SHORT
        ).show()

        backToHome()
    }

    private fun leerDatosUsuario() {
        currentUser?.uid?.let {
            db.collection("users").document(it)
                .get()
                .addOnSuccessListener { result ->
                    user = result.toObject(User::class.java)
                    fillUserData(user)
                    Log.d(TAG, "${result.id} => ${result.data}")
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
    }

    private fun fillUserData(user: User?) {

        Log.v(TAG, "el usuario = ${user.toString()}")

        binding.etName.setText(user?.nombre.toString())
        binding.etLastName.setText(user?.apellidos.toString())
        binding.etAge.setText(if (user?.edad == 0) "" else user?.edad.toString())
        binding.etProfession.setText(user?.profesion.toString())
    }

    private fun setNewUserData() {
        user?.nombre =
            if (!binding.etName.text.isNullOrEmpty()) binding.etName.text.toString() else user?.nombre
        user?.apellidos =
            if (!binding.etLastName.text.isNullOrEmpty()) binding.etLastName.text.toString() else user?.apellidos
        user?.edad = if (!binding.etAge.text.isNullOrEmpty()
        ) binding.etAge.text.toString().toInt() else user?.edad
        user?.profesion =
            if (!binding.etProfession.text.isNullOrEmpty()) binding.etProfession.text.toString() else user?.profesion
    }

    private fun isNotEmpty(): Boolean {
        return !binding.etName.text.isNullOrEmpty() || !binding.etLastName.text.isNullOrEmpty() || !binding.etAge.text.isNullOrEmpty() || !binding.etProfession.text.isNullOrEmpty()
    }

    private fun backToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        finish()
        startActivity(intent)
    }
}