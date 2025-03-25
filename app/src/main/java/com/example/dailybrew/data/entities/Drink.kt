package com.example.dailybrew.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drinks")
data class Drink(
    @PrimaryKey(autoGenerate = true)
    val drinkId: Long = 0,
    val name: String,
    val caffeinePerServing: Int,
    val servingSize: Int,
    val iconResourceName: String? = null
)