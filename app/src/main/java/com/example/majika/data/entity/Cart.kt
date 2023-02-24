package com.example.majika.data.entity

import androidx.room.Entity

@Entity(primaryKeys = ["name", "price"])
data class Cart (
    val name: String,
    val price: Int,
    var quantity: Int
)

// static database, no need for null safety