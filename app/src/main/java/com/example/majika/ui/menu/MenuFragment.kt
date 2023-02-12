package com.example.majika.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majika.daftarmenu.MenuAdapter
import com.example.majika.data.MenuItem
import com.example.majika.data.MenuType
import com.example.majika.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val menuViewModel =
            ViewModelProvider(this).get(MenuViewModel::class.java)

        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root:View = binding.root
        val recyclerView = binding.recyclerListMenu
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val list = ArrayList<MenuItem>()
        list.add(MenuItem("Nasi Goreng","-","IDR",100,20, MenuType.FOOD))
        list.add(MenuItem("Nasi Lemak","Kebenaran tak peranh sok sendiri - Papa Zola - ","IDR",100,20,MenuType.FOOD))
        list.add(MenuItem("Nasi Bakar","-","MYR",100,20, MenuType.FOOD))
        list.add(MenuItem("Susu Panas!","-","MYR",100,20,MenuType.DRINK))

        val adapter = MenuAdapter(list)
      //  adapter.notifyDataSetChanged()

        recyclerView.adapter = adapter



//        val textView: TextView = binding.textMenu
//        menuViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}