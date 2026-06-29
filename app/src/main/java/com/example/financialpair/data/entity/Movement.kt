package com.example.financialpair.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"]
        )
    ]
)
data class Movement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Int,
    val date: Int,
    val categoryId: Long
)