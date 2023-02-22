package com.example.majika.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.data.entity.Cart

class CartAdapter(private val list:ArrayList<Cart>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        val cartNameView: TextView
        val cartPriceView: TextView
        val cartDeleteButton: ImageButton
        val cartIncreaseButton: Button
        val cartItemAmount: TextView
        val cartDecreaseButton: Button

        init {
            cartNameView = view.findViewById(R.id.cart_name)
            cartPriceView = view.findViewById(R.id.cart_price)
            cartDeleteButton = view.findViewById(R.id.cart_delete)
            cartIncreaseButton = view.findViewById(R.id.cart_increase)
            cartItemAmount = view.findViewById(R.id.item_amount)
            cartDecreaseButton = view.findViewById(R.id.cart_decrease)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_cart, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        list[position]
        holder as ItemHolder
        val item: Cart = list[position]

        holder.cartNameView.text = item.name
        holder.cartPriceView.text = item.price.toString()
        holder.cartItemAmount.text = item.quantity.toString()
        holder.cartDeleteButton.setOnClickListener {
            list.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, list.size)
        }
        holder.cartDecreaseButton.setOnClickListener {
            if (item.quantity != 0) {
                item.quantity = item.quantity?.minus(1)
                holder.cartItemAmount.text = item.quantity.toString()
            }
        }
        holder.cartIncreaseButton.setOnClickListener {
            item.quantity = item.quantity?.plus(1)
            holder.cartItemAmount.text = item.quantity.toString()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}