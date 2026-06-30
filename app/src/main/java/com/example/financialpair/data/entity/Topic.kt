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
data class Topic(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val categoryId: Int
)