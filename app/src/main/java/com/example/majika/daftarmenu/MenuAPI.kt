package com.example.majika.daftarmenu

import com.example.majika.data.MenuList
import retrofit2.Call
import retrofit2.http.GET

interface MenuAPI {
    @GET("/v1/menu")
    fun getMenu(): Call<MenuList>

    @GET("/v1/menu/food")
    fun getFood(): Call<MenuList>

    @GET("/v1/menu/drink")
    fun getDrink(): Call<MenuList>
}