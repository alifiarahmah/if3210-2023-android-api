package com.example.majika.daftarmenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.data.MenuItem

class MenuAdapter(private val list:ArrayList<MenuItem>): RecyclerView.Adapter<MenuAdapter.Holder>()
{
    class Holder(val view: View): RecyclerView.ViewHolder(view){
        val textView:TextView
        val priceView:TextView
        val soldView:TextView
        val descView:TextView
        init {
            textView = view.findViewById(R.id.title)
            priceView = view.findViewById(R.id.price)
            soldView = view.findViewById(R.id.sold_number)
            descView = view.findViewById(R.id.description)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        //saat buat viewnya
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_menu,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: MenuAdapter.Holder, position: Int) {
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
        holder.soldView.text = data.sold.toString() + " Terjual"
        holder.descView.text = data.description
    }

    override fun getItemCount(): Int = list.size

}