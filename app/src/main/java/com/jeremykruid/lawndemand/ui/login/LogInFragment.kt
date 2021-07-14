package com.jeremykruid.lawndemand.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jeremykruid.lawndemand.R

class LogInFragment : Fragment(), View.OnClickListener {

    private lateinit var thisView: View
    private lateinit var emailEdit: EditText
    private lateinit var passEdit: EditText
    private lateinit var registerBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var forgotBtn: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_log_in, container, false)

        checkAuth()

        initViews()

        return thisView
    }

    private fun initViews() {
        registerBtn = thisView.findViewById(R.id.login_register_btn)
        registerBtn.setOnClickListener(this)
        loginBtn = thisView.findViewById(R.id.login_login_btn)
        loginBtn.setOnClickListener(this)
        forgotBtn = thisView.findViewById(R.id.login_forgot_password)
        forgotBtn.setOnClickListener(this)

        emailEdit = thisView.findViewById(R.id.login_email)
        passEdit = thisView.findViewById(R.id.login_password)
    }

    private fun checkAuth() {
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null){
            findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
        }
    }

    override fun onClick(v: View?) {
        when(v){
            loginBtn -> {
                login()
            }
            registerBtn -> {
                register()
            }
            forgotBtn -> {
                forgot()
            }
        }
    }

    private fun forgot() {
        val email = emailEdit.text.toString().trim()
        if (email != ""){
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                Toast.makeText(requireContext(), "Email Sent", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Try Again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login() {
        val email = emailEdit.text.toString().trim()
        val pass = passEdit.text.toString().trim()
        if (email != "" && pass != ""){
            auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener { 
                findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun register() {
        val email = emailEdit.text.toString().trim()
        val pass = passEdit.text.toString().trim()
        if (email != "" && pass != ""){
            auth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener {
                auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener {
                    val uid = FirebaseAuth.getInstance().uid.toString()
                    val data = hashMapOf(
                        "uid" to uid
                    )
                    Firebase.firestore.collection("customers").document(uid).set(data, SetOptions.merge())
                    findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(), "Email and Password are Required", Toast.LENGTH_SHORT).show()
        }
    }
}