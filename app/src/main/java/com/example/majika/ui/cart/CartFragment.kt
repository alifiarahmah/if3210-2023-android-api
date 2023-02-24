package com.example.majika.ui.cart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.QrActivity
import com.example.majika.cart.CartAdapter
import com.example.majika.data.AppDatabase
import com.example.majika.data.CartRepositoryImpl
import com.example.majika.data.entity.Cart
import com.example.majika.databinding.FragmentCartBinding
import com.example.majika.utils.AppUtil

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

        val cart : ArrayList<Cart> = ArrayList()

        // require context
        val mContext = requireContext()
        // create instance of database
        val db = AppDatabase.getInstance(mContext)
        val repo = CartRepositoryImpl(db!!.cartDao())

        // get all data from cart table
        repo.getCart().forEach {
            cart.add(it)
        }

        // initialize total price
        var totalPrice = 0
        for (item in cart) {
            totalPrice += item.price * item.quantity
        }
        // format total price with rupiah currency
        binding.totalPrice.text = AppUtil.toRupiah(totalPrice)

        val adapter = CartAdapter(cart)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                // recalculate total price
                totalPrice = 0
                for (item in cart) {
                    totalPrice += item.price * item.quantity
                }
                binding.totalPrice.text = AppUtil.toRupiah(totalPrice)
            }
        })

        recyclerView.adapter = adapter

        // if cart is empty, hide checkout button
        if (cart.isEmpty()) {
            binding.checkoutButton.visibility = View.GONE
        }

        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
            if (result.resultCode == 0) {
                Log.d("DEBUG", result.resultCode.toString())
                TODO("rEPLACE FRAGMENT TO MENU")
            }
        }

        // if cart is not empty, show checkout button to payment fragment
        binding.checkoutButton.setOnClickListener {

            /**
             * QR Code scanner
             */
            val qrIntent = Intent(requireActivity(), QrActivity::class.java)
            // startActivity(qrIntent)
            resultLauncher.launch(qrIntent)
        }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}