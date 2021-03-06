package com.jeremykruid.lawndemand.ui.ordering

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jeremykruid.lawndemand.R
import com.jeremykruid.lawndemand.functions.adapters.HomeAdapter
import com.jeremykruid.lawndemand.model.PropertyObject

class HomeFragment : Fragment(), View.OnClickListener, HomeAdapter.PropertyClicked {

    private lateinit var thisView: View
    private lateinit var fab: FloatingActionButton
    private lateinit var auth: FirebaseAuth
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: HomeAdapter

    private var propertyList: ArrayList<PropertyObject> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_home, container, false)

        initViews()

        checkAuth()

        getProperties()

        setRecycler()

        return thisView
    }

    private fun getProperties() {
        propertyList.clear()
        val firestore = Firebase.firestore.collection("customers").document(auth.uid.toString())
            .collection("properties")
        firestore.get().addOnSuccessListener { snapshot ->
            snapshot.forEach {property ->
                val newProperty = property.toObject(PropertyObject::class.java)
                if (property != null) {
                    propertyList.add(newProperty)
                    adapter.notifyItemRangeChanged(0, propertyList.size)
                }
            }
        }
    }

    private fun setRecycler() {
        adapter = HomeAdapter(requireContext(), propertyList, this)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
    }

    private fun checkAuth() {
        FirebaseApp.initializeApp(requireContext())
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null){
            findNavController().navigate(R.id.action_homeFragment_to_logInFragment)
        }
    }

    override fun onClick(v: View?) {
        when(v){
            fab ->{
                findNavController().navigate(R.id.action_homeFragment_to_newPropertyFragment)
            }
        }
    }

    private fun initViews(){
        fab = thisView.findViewById(R.id.home_fab)
        fab.setOnClickListener(this)
        recycler = thisView.findViewById(R.id.home_property_recycler)
    }

    override fun getQuotes(property: PropertyObject) {
        val bundle = bundleOf(Pair("property", property))
        findNavController().navigate(R.id.action_homeFragment_to_quoteFragment, bundle)
    }
}