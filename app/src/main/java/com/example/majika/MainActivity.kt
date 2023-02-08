package com.example.majika

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.daftarmenu.MenuAdapter
import com.example.majika.data.MenuItem
import com.example.majika.data.MenuType
import com.example.majika.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        //set view
        val view = binding.root
        setContentView(view)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerListMenu)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val list = ArrayList<MenuItem>()
        list.add(MenuItem("Nasi Goreng","-","MYR",100,20,MenuType.FOOD))
        list.add(MenuItem("Nasi Lemak","-","MYR",100,20,MenuType.FOOD))

        val adapter = MenuAdapter(list)
        adapter.notifyDataSetChanged()

        recyclerView.adapter = adapter
    }
}