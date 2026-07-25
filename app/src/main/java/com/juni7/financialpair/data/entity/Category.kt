package com.juni7.financialpair.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)
