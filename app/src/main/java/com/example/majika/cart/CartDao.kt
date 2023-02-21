package com.example.majika.cart

import androidx.room.*

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    fun getAll(): List<Cart>

    @Insert
    fun insertAll(vararg cartItem: Cart)

    @Update
    fun addOne(cartItem: Cart)

    @Update
    fun removeOne(cartItem: Cart)

    @Delete
    fun delete(cartItem: Cart)
}