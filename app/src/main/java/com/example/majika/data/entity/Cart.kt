package com.example.majika.data.entity

import androidx.room.Entity

@Entity(primaryKeys = ["name", "price", "type"])
data class Cart (
    val name: String,
    val price: Int,
    val type: String,
    var quantity: Int
)

// static database, no need for null safety