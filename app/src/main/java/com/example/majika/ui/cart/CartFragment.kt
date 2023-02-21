package com.example.majika.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majika.cart.Cart
import com.example.majika.cart.CartAdapter
import com.example.majika.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this)[CartViewModel::class.java]

        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.recyclerListCart
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

//        val db = Room.databaseBuilder(
//            requireContext(),
//            CartDatabase::class.java, "cart-database"
//        ).allowMainThreadQueries().build()

//        val cartDao = db.cartDao()

//        val cart = cartDao.getAll()

        // dummy data
        val cart : ArrayList<Cart> = ArrayList()
        cart.add(Cart(1, "Baju", 10000, 1))
        cart.add(Cart(2, "Celana", 20000, 2))
        cart.add(Cart(3, "Sepatu", 30000, 3))

        val adapter = CartAdapter(cart as ArrayList<Cart>)

        recyclerView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}