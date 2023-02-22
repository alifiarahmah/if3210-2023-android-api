package com.example.majika.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.cart.Cart
import com.example.majika.cart.CartAdapter
import com.example.majika.databinding.FragmentCartBinding
import com.example.majika.ui.payment.PaymentFragment

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

        val adapter = CartAdapter(cart)

        recyclerView.adapter = adapter

        // set total price
        var totalPrice = 0
        for (item in cart) {
            totalPrice += item.price!! * item.quantity!!
        }
        binding.totalPrice.text = totalPrice.toString()

        // if item in cart changed, update total price
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                var totalCartPrice = 0
                for (item in cart) {
                    totalCartPrice += item.price!! * item.quantity!!
                }
                binding.totalPrice.text = totalCartPrice.toString()
            }
        })

        // if cart is empty, hide checkout button
        if (cart.isEmpty()) {
            binding.checkoutButton.visibility = View.GONE
        }

        // if cart is not empty, show checkout button to payment fragment
        binding.checkoutButton.setOnClickListener {
            val paymentFragment = PaymentFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, paymentFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}