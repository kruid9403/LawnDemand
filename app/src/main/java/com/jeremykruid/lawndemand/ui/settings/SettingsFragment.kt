package com.jeremykruid.lawndemand.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.jeremykruid.lawndemand.R

class SettingsFragment : Fragment(), View.OnClickListener {

    private lateinit var thisView: View
    private lateinit var signOutBtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var historyBtn: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_settings, container, false)

        checkAuth()

        initViews()

        return thisView
    }

    private fun initViews() {
        signOutBtn = thisView.findViewById(R.id.settings_sign_out)
        signOutBtn.setOnClickListener(this)

        historyBtn = thisView.findViewById(R.id.settings_history)
        historyBtn.setOnClickListener(this)
    }

    private fun checkAuth() {
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null){
            findNavController().navigate(R.id.action_settingsFragment_to_logInFragment)
        }
    }

    override fun onClick(v: View?) {
        when(v){
            signOutBtn -> {
                auth.signOut()
                checkAuth()
            }
            historyBtn -> {
                findNavController().navigate(R.id.action_settingsFragment_to_orderHistoryFragment)
            }
        }
    }
}