package com.jeremykruid.lawndemand.functions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jeremykruid.lawndemand.R
import com.jeremykruid.lawndemand.model.PropertyObject
import de.hdodenhof.circleimageview.CircleImageView

class HomeAdapter(val context: Context, private val propertyList: MutableList<PropertyObject>,
                  val action: PropertyClicked):
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(property: PropertyObject){
            itemView.findViewById<TextView>(R.id.home_row_address).text = property.address?.streetAddress
            itemView.findViewById<TextView>(R.id.home_row_lot).text = property.resoFacts?.lotSize

            itemView.findViewById<TextView>(R.id.home_row_get_service).setOnClickListener {
                action.getQuotes(property)
            }

            val view = itemView.findViewById<CircleImageView>(R.id.home_row_img)
            Glide.with(context).load(property.imgSrc).into(view)
        }
    }

    interface PropertyClicked{
        fun getQuotes(property: PropertyObject)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
            .inflate(R.layout.row_home_recycler, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val property = propertyList[position]
        holder.bind(property)
    }

    override fun getItemCount(): Int {
        return propertyList.size
    }
}