package com.example.majika.branch

import com.example.majika.data.BranchList
import retrofit2.Call
import retrofit2.http.GET

interface BranchAPI {
    @GET("/v1/branch")
    fun getBranches(): Call<BranchList>
}