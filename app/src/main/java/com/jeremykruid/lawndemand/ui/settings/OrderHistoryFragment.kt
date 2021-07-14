package com.jeremykruid.lawndemand.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jeremykruid.lawndemand.R
import com.jeremykruid.lawndemand.functions.adapters.HistoryAdapter
import com.jeremykruid.lawndemand.model.OrderObject

class OrderHistoryFragment : Fragment(), HistoryAdapter.OrderClicked {

    private lateinit var thisView: View
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var uid: String

    private var orderList: ArrayList<OrderObject> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_order_history, container, false)

        initViews()

        setRecycler()

        getOrders()

        return thisView
    }

    private fun getOrders() {
        val orderRef = Firebase.firestore.collection("customerOrders").document(uid)

        orderRef.get().addOnSuccessListener { snapshot ->
            if (snapshot != null && snapshot.exists()) {
                val keyList = snapshot.data?.keys
                keyList?.forEach {
                    Firebase.firestore.collection("orders").document(it).get()
                        .addOnSuccessListener { snapshot ->
                            if (snapshot != null && snapshot.exists()){
                                val order = snapshot.toObject(OrderObject::class.java)
                                if (order != null) {
                                    orderList.add(order)
                                    adapter.notifyItemRangeChanged(0, orderList.size)
                                }
                            }
                    }
                }
            }
        }
    }

    private fun setRecycler() {
        adapter = HistoryAdapter(requireContext(), orderList, this)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter
    }

    private fun initViews() {
        if (FirebaseAuth.getInstance().currentUser != null){
            uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        }else{
            Toast.makeText(requireContext(), "Unexpected Error", Toast.LENGTH_SHORT).show()
        }

        recycler = thisView.findViewById(R.id.order_history_recycler)
    }

    override fun cancelOrder(order: OrderObject) {
        //Todo: create cancel order database function
        Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
    }

}