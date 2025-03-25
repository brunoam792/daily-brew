package com.example.dailybrew.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_limits",
    indices = [Index("userId")],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DailyLimit(
    @PrimaryKey(autoGenerate = true)
    val limitId: Long = 0,
    val userId: Long,
    val limitAmount: Int,
    val dateCreated: Long = System.currentTimeMillis()
)