package com.example.majika.branch

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.data.BranchItem

class BranchAdapter(private val list: ArrayList<BranchItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        val branchNameView: TextView
        val branchLocationView: TextView
        val branchPhoneView: TextView
        val mapsButton: Button
        val phoneButton: ImageButton

        init {
            branchNameView = view.findViewById(R.id.branch_name)
            branchLocationView = view.findViewById(R.id.branch_location)
            branchPhoneView = view.findViewById(R.id.branch_phone)
            mapsButton = view.findViewById(R.id.maps_button)
            phoneButton = view.findViewById(R.id.phone_button)
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
        holder.branchPhoneView.text = item.phone_number
        holder.mapsButton.setOnClickListener {
            // map should be from google map
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${item.latitude},${item.longitude}?q=${item.address}"))
            mapIntent.setPackage("com.google.android.apps.maps")
            holder.mapsButton.context.startActivity(mapIntent)
        }
        holder.phoneButton.setOnClickListener {
            val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.phone_number}"))
            holder.phoneButton.context.startActivity(phoneIntent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}