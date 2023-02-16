package com.example.majika.daftarmenu

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.majika.R
import com.example.majika.data.MenuItem
import com.example.majika.data.MenuSection
import com.example.majika.data.UIConstant
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency

class MenuAdapter(private var mContext: Context, private val list:ArrayList<MenuSection>): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    class ItemHolder( view: View): RecyclerView.ViewHolder(view){
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

    class SectionHolder(view:View):RecyclerView.ViewHolder(view){
        val sectionName = view.findViewById(R.id.section_name) as TextView?
    //    val sectionListMenu = view.findViewById(R.id.section_list_menu) as ImageView?
        val sectionMenu = view.findViewById<ConstraintLayout>(R.id.section_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //saat buat viewnya
        return if(viewType==UIConstant.PARENT){
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.section_menu,parent,false)
            SectionHolder(view)
        }else{
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_menu,parent,false)
            ItemHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //saat ngebind data
        val data = list[position]
        if(data.type==UIConstant.PARENT){
            holder as SectionHolder
            holder.apply {
                sectionName?.text = data.title
                sectionMenu?.setOnClickListener{
                    expandOrCollapseSectionMenu(data,position,holder)
                }
            }
        }
        else{
            holder as ItemHolder
            holder.apply {
                var item: MenuItem? = null
                try {
                    item = data.datas.first()
                }catch(e:Exception){
                    println("Data kosong!")
                    return
                }
                holder.textView.text = item.name
                //format harga
                val price = NumberFormat.getCurrencyInstance()
                price.maximumFractionDigits = 2
                var price_text_pre = price.format(item.price)
                holder.priceView.text = price_text_pre.replace("$","Rp.")
                //format pesanan
                val order = 0
                holder.soldView.text = buildString {
                    append(NumberFormat.getNumberInstance().format(item.sold))
                    append(" Terjual")
                }
                holder.descView.text = item.description
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
        }

    }

    private fun expandOrCollapseSectionMenu(section: MenuSection, position: Int,holder: SectionHolder) {
        if(section.isExpanded){
            collapseSection(position,holder)
        }
        else{
            expandSection(position,holder)
        }
    }

    private fun collapseSection(position: Int,holder:SectionHolder) {
        val currentRow = list[position]
        val items = currentRow.datas
        list[position].isExpanded = false
        //ganti warna
        //bg
        holder.sectionMenu.setBackgroundColor(Color.WHITE)
        //fg
        holder.sectionName!!.setTextColor(Color.BLACK)
        if(list[position].type==UIConstant.PARENT){
            items.forEach{ _ ->
                list.removeAt(position+1)
            }

            notifyDataSetChanged()
        }
    }

    private fun expandSection(position: Int,holder:SectionHolder) {
        val currentRow = list[position]
        val items = currentRow.datas
        list[position].isExpanded = true
        var nextPosition = position
        //ganti warna
        //bg
        holder.sectionMenu.setBackgroundColor(Color.GRAY)
        //fg
        holder.sectionName!!.setTextColor(Color.BLACK)
        if(currentRow.type==UIConstant.PARENT){
            //kalau parent maka render childnya
            items.forEach{ item->
                val parentModel = MenuSection()
                parentModel.type = UIConstant.CHILD
                parentModel.datas.add(item)
                list.add(++nextPosition,parentModel)
            }
            //update data
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int = list.size

}