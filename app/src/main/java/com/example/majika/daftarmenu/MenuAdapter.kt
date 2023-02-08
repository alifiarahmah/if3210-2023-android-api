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
        init {
            textView = view.findViewById(R.id.asu)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        //saat buat viewnya
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_menu,parent,false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: MenuAdapter.Holder, position: Int) {
        //saat ngebind data
        holder.textView.text = list[position].name
    }

    override fun getItemCount(): Int = list.size

}