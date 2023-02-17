package com.example.majika.branch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.data.BranchItem

class BranchAdapter(private val list: ArrayList<BranchItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        val branchNameView: TextView
        val branchLocationView: TextView
        val mapsButton: Button

        init {
            branchNameView = view.findViewById(R.id.branch_name)
            branchLocationView = view.findViewById(R.id.branch_location)
            mapsButton = view.findViewById(R.id.maps_button)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_branch, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list[position]
        holder as ItemHolder
        val item: BranchItem = list[position]

        holder.branchNameView.text = item.name
        holder.branchLocationView.text = item.address
    }

    override fun getItemCount(): Int {
        return list.size
    }

}