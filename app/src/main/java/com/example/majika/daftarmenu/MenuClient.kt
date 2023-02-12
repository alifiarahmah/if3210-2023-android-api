package com.example.majika.daftarmenu

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object MenuClient {
    private val BASE_URL:String = "http://192.168.2.105:3000/" //localhost
    private var mRetrofit: Retrofit? = null

    fun getInstance():Retrofit{
        //bikin retrofit client
        if(mRetrofit==null) {
            mRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return this.mRetrofit!!
    }

}