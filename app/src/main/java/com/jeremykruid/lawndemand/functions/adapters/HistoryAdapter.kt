package com.jeremykruid.lawndemand.functions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jeremykruid.lawndemand.R
import com.jeremykruid.lawndemand.functions.DoMath
import com.jeremykruid.lawndemand.model.OrderObject
import de.hdodenhof.circleimageview.CircleImageView

class HistoryAdapter(val context: Context, private val orderList: MutableList<OrderObject>,
                     val action: OrderClicked):
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(order: OrderObject){
            itemView.findViewById<TextView>(R.id.history_row_address).text = order.streetAddress
            itemView.findViewById<TextView>(R.id.history_row_lot).text = "${order.lotSize} sqft"
            if (order.status != "In Progress" && order.status != "Completed"){
                itemView.findViewById<TextView>(R.id.history_status).text = context.getString(R.string.waiting_for_provider)
            }else {
                itemView.findViewById<TextView>(R.id.history_status).text = "${order.status}"
            }
            itemView.findViewById<TextView>(R.id.history_row_price).text = DoMath().convertToDollars(order.price)

            itemView.findViewById<TextView>(R.id.history_row_cancel).setOnClickListener {
                action.cancelOrder(order)
            }

            val view = itemView.findViewById<CircleImageView>(R.id.history_row_img)
            Glide.with(context).load(order.imgUrl).into(view)
        }
    }

    interface OrderClicked{
        fun cancelOrder(order: OrderObject)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.row_order_history_recycler, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}