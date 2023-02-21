package com.example.majika.cart

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart (
    @PrimaryKey(autoGenerate = true) val id : Int,
    val name: String?,
    val price: Int?,
    var quantity: Int?
)