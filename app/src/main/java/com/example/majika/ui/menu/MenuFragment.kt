package com.example.majika.ui.menu

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majika.daftarmenu.MenuAPI
import com.example.majika.daftarmenu.MenuAdapter
import com.example.majika.daftarmenu.MenuClient
import com.example.majika.data.MenuItem
import com.example.majika.data.MenuList
import com.example.majika.data.MenuType
import com.example.majika.databinding.FragmentMenuBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //api clinet
    private var API_service:MenuAPI? = null
    private var menuFood:ArrayList<MenuItem> = ArrayList()

    private var adapter:MenuAdapter? = null

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

        //set api service
        API_service = MenuClient.getInstance().create(MenuAPI::class.java)
//        val list = ArrayList<MenuItem>()
//        list.add(MenuItem("Nasi Goreng","-","IDR",100,20, MenuType.FOOD))
//        list.add(MenuItem("Nasi Lemak","Kebenaran tak peranh sok sendiri - Papa Zola - ","IDR",100,20,MenuType.FOOD))
//        list.add(MenuItem("Nasi Bakar","-","MYR",100,20, MenuType.FOOD))
//        list.add(MenuItem("Susu Panas!","-","MYR",100,20,MenuType.DRINK))

        adapter = MenuAdapter(menuFood)
      //  adapter.notifyDataSetChanged()

        recyclerView.adapter = adapter

        //fetch data
        fetchData()


//        val textView: TextView = binding.textMenu
//        menuViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    private fun fetchData() {
        //data makanan
        val call = API_service!!.getMenu()
        call.enqueue(object :Callback<MenuList>{
            override fun onResponse(call: Call<MenuList>, response: Response<MenuList>) {
               val foods = response.body()
                if(foods!=null){
                    menuFood.addAll(foods.data)
                    adapter!!.notifyDataSetChanged()
                    Log.d("JALAN","harusnya mah jalan ieu")
                }
            }

            override fun onFailure(call: Call<MenuList>, t: Throwable) {
                Log.e("ERROR","Request gagal:"+t.localizedMessage)
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}