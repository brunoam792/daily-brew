package com.example.dailybrew.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(
    tableName = "intakes",
    indices = [
        Index("userId"),
        Index("drinkId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Drink::class,
            parentColumns = ["drinkId"],
            childColumns = ["drinkId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Intake(
    @PrimaryKey(autoGenerate = true)
    val intakeId: Long = 0,
    val userId: Long,
    val drinkId: Long,
    val servings: Float,
    val totalCaffeine: Int,
    val timestamp: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
)