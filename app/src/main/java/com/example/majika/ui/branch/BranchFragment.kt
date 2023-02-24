package com.example.majika.ui.branch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majika.branch.BranchAPI
import com.example.majika.branch.BranchAdapter
import com.example.majika.branch.BranchClient
import com.example.majika.data.BranchItem
import com.example.majika.data.BranchList
import com.example.majika.databinding.FragmentBranchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BranchFragment : Fragment() {

    private var _binding: FragmentBranchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // API client
    private var apiService: BranchAPI? = null
    private var branches: ArrayList<BranchItem> = ArrayList()

    private var adapter: BranchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ViewModelProvider(this)[BranchViewModel::class.java]

        _binding = FragmentBranchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.recyclerListBranch
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // set api service
        apiService = BranchClient.getInstance().create(BranchAPI::class.java)

        adapter = BranchAdapter(branches)
        recyclerView.adapter = adapter

        fetchData()

        return root
    }

    private fun fetchData() {
        val call: Call<BranchList> = apiService!!.getBranches()
        call.enqueue(object : Callback<BranchList> {
            override fun onResponse(call: Call<BranchList>, response: Response<BranchList>) {
                if (response.isSuccessful) {
                    val branchList: BranchList? = response.body()
                    if (branchList != null) {
                        branches.addAll(branchList.data)
                    }
                    // order by branch name
                    branches.sortBy { it.name }
                }
                adapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<BranchList>, t: Throwable) {
                Log.e("Error", t.message.toString())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}