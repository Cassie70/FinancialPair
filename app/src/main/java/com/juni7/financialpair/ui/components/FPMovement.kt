package com.juni7.financialpair.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.juni7.financialpair.data.entity.Movement
import java.text.DecimalFormat

@Composable
fun FPMovement(
    movement: Movement,
    logoUrl: String? = null,
    onClick: () -> Unit = {}
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val df = DecimalFormat("$#,##0.00")

        // Contenedor circular para logo
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = logoUrl,
                contentDescription = "Logo de ${movement.description}",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentScale = ContentScale.Fit,
                onState = { state ->
                    if (state is coil.compose.AsyncImagePainter.State.Error) {
                        android.util.Log.e("FPMovement", "Error loading image: ${state.result.throwable.message}")
                    }
                }
            )
        }

        Text(
            text = movement.description,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = df.format(movement.amount/100.0F),
            style = MaterialTheme.typography.titleMedium,
            color = if (movement.amount < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    }
}