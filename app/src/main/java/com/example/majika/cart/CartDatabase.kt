package com.example.majika.cart

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Cart::class], version = 1)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}