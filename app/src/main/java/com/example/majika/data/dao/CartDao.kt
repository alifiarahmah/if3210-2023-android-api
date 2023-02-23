package com.example.majika.data.dao

import androidx.room.*
import com.example.majika.data.entity.Cart

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    fun getAll(): List<Cart>

    @Query("SELECT * FROM cart WHERE name IN (:names)")
    fun loadAllByIds(names: IntArray): List<Cart>

    // get cart item quantity by name
    @Query("SELECT quantity FROM cart WHERE name = :name")
    fun getQuantityByName(name: String): Int

    @Insert
    fun insert(cartItem: Cart)

    @Update
    fun update(cartItem: Cart)

    @Delete
    fun delete(cartItem: Cart)
}