package com.juni7.financialpair.util

import android.content.Context
import com.juni7.financialpair.R
import com.juni7.financialpair.data.entity.Category

fun Category.getLocalizedName(context: Context): String {
    val resourceId = when (name.lowercase()) {
        "general" -> R.string.category_general
        "transport" -> R.string.category_transport
        "food" -> R.string.category_food
        "health" -> R.string.category_health
        "entertainment" -> R.string.category_entertainment
        "clothes" -> R.string.category_clothes
        "education" -> R.string.category_education
        "shopping" -> R.string.category_shopping
        "housing" -> R.string.category_housing
        "family" -> R.string.category_family
        "travel" -> R.string.category_travel
        else -> null
    }
    
    return resourceId?.let { context.getString(it) } ?: name
}
