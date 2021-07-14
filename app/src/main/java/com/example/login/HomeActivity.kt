package com.example.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.isVisible
import com.example.login.databinding.ActivityHomeBinding
import com.example.login.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var auth: FirebaseAuth
    private var googleSignInClient: GoogleSignInClient? = null
    val TAG = "HomeActivity"
    var currentUser: FirebaseUser? = null
    var user: User? = null
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initGoogle()
        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.profile -> {
                goToProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

        val btLogOut = binding.btLogOut

        btLogOut.setOnClickListener {
            logOut()
            //showUserData(auth.currentUser)
        }

        leerDatosUsuario()
    }

    private fun createUser() {

        var newUser: User = User()
        newUser.nombre = ""
        newUser.apellidos = ""
        newUser.edad = 0
        newUser.profesion = ""

        currentUser?.uid?.let {
            db.collection("users").document(it)
                .set(newUser)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

    private fun leerDatosUsuario() {
        currentUser?.uid?.let {
            db.collection("users").document(it)
                .get()
                .addOnSuccessListener { result ->
                    if (result.data == null) {
                        createUser()
                    } else {
                        user = result.toObject(User::class.java)
                        fillUserData(user)
                        Log.d(TAG, "${result.id} => ${result.data}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }
        }
    }

    private fun logOut() {
        googleSignInClient?.signOut()?.addOnCompleteListener {
            googleSignInClient?.revokeAccess()?.addOnCompleteListener {
                Firebase.auth.signOut()
                goToLogin()
            }
        }
    }

    private fun fillUserData(user: User?) {

        Log.v(TAG, "el usuario = ${user.toString()}")

        binding.tvFullName.text =
            if (user?.nombre.isNullOrEmpty()) "Full Name: - " else " Full Name: ${user?.nombre} ${user?.apellidos}"
        binding.tvAge.text = if (user?.edad == 0) "Age: - " else "Age: ${user?.edad.toString()}"
        binding.tvProfession.text = if (user?.profesion.isNullOrEmpty()) "Profession: - " else "Profession: ${user?.profesion}"
    }

    private fun goToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun goToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }
}