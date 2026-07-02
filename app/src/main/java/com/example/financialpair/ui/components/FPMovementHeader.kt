package com.example.financialpair.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financialpair.ui.screens.movements.toLocalizedDate
import java.text.DecimalFormat

@Composable
fun FPMovementHeader(date: Int, total: Int){
    val df = DecimalFormat("$#,##0.00")
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = date.toLocalizedDate(), modifier = Modifier.weight(1f))
        Text(text = df.format(total/100.0F))
    }
}
