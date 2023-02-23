package com.example.majika.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart (
    @PrimaryKey val name: String,
    val price: Int,
    var quantity: Int
)

// static database, no need for null safety