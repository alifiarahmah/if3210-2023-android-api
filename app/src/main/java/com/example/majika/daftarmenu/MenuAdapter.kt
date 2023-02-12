package com.example.majika.daftarmenu

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.data.MenuItem

class MenuAdapter(private val list:ArrayList<MenuItem>): RecyclerView.Adapter<MenuAdapter.Holder>()
{
    class Holder( view: View): RecyclerView.ViewHolder(view){
        val textView:TextView
        val priceView:TextView
        val soldView:TextView
        val descView:TextView
        val orderView: TextView
        val decreaseButton: Button
        val increaseButton: Button
        init {
            textView = view.findViewById(R.id.title)
            priceView = view.findViewById(R.id.price)
            soldView = view.findViewById(R.id.sold_number)
            descView = view.findViewById(R.id.description)
            orderView = view.findViewById(R.id.totalOrder)
            decreaseButton = view.findViewById(R.id.decreaseOrderButton)
            increaseButton = view.findViewById(R.id.increaseOrderButton)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        //saat buat viewnya
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_menu,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        //saat ngebind data
        val data = list[position]
        holder.textView.text = data.name
        holder.priceView.text =  when(data.currency){
            "IDR"->{
                "Rp." + data.price.toString()
            }
            else->{
                "Rp.0"
            }
        }
        val order = 0
        holder.soldView.text = data.sold.toString() + " Terjual"
        holder.descView.text = data.description
        holder.orderView.text = order.toString()
        holder.decreaseButton.isClickable = false
        holder.decreaseButton.setBackgroundColor(Color.GRAY)
        holder.decreaseButton.setOnClickListener {
            var order =  Integer.parseInt(holder.orderView.text.toString()) - 1
            if(order<0){
                order = 0
            }
            holder.orderView.text = order.toString()
            if(order<=0){
                holder.decreaseButton.isClickable = false
                holder.decreaseButton.setBackgroundColor(Color.GRAY)
            }
        }
        holder.increaseButton.setBackgroundColor(Color.rgb(247,225,201))
        holder.increaseButton.setOnClickListener {
            holder.decreaseButton.isClickable = true
            holder.decreaseButton.setBackgroundColor(Color.rgb(247,225,201))
            val order =  Integer.parseInt(holder.orderView.text.toString()) + 1
            holder.orderView.text = order.toString()
        }
    }

    override fun getItemCount(): Int = list.size

}