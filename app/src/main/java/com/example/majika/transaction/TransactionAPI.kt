package com.example.majika.transaction

import com.example.majika.data.TransactionStatus
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path

interface TransactionAPI {
    @POST("/v1/payment/{transactionId}")
    fun verifyTransaction(@Path("transactionId") transactionId: String): Call<TransactionStatus>
}