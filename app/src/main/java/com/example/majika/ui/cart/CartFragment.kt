package com.example.majika.ui.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.cart.CartAdapter
import com.example.majika.data.AppDatabase
import com.example.majika.data.entity.Cart
import com.example.majika.databinding.FragmentCartBinding
import com.example.majika.ui.menu.MenuFragment
import com.example.majika.ui.qr.QrFragment
import com.example.majika.utils.AppUtil

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * Handle back button on create
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            /**
             * Handle the back button event
             */
            val menuFragment = MenuFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, menuFragment)
            transaction.commit()
            /**
             * Select the main menu
             */
            val navigation = requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
        }
    }



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
//        cart.add(Cart("Baju", 10000, 1))
//        cart.add(Cart("Celana", 20000, 2))
//        cart.add(Cart("Sepatu", 30000, 3))

        // require context
        val mContext = requireContext()
        // create instance of database
        val db = AppDatabase.getInstance(mContext)
        val cartDao = db?.cartDao()

        // get all data from cart table
        cart.addAll(cartDao?.getAll()!!)

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

        // if cart is not empty, show checkout button to payment fragment
        binding.checkoutButton.setOnClickListener {
            /**
             * QR Code scanner
             */
            val qrFragment = QrFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, qrFragment)
            // transaction.addToBackStack("scanner")
            transaction.commit()
            Log.d("BACKSTACK", requireActivity().supportFragmentManager.backStackEntryCount.toString())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}