package com.example.financialpair.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financialpair.data.entity.Movement
import org.koin.compose.viewmodel.koinViewModel
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


@Composable
fun MovementsScreen(
    vm: MovementsScreenViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 25.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = uiState.error ?: "")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    modifier = Modifier.weight(2f),
                    value = uiState.description,
                    onValueChange = vm::onDescriptionChange,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1,
                    isError = uiState.hasDescriptionError
                )
                TextField(
                    modifier = Modifier.weight(1f),
                    value = uiState.amount,
                    onValueChange = vm::onAmountChange,
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    maxLines = 1,
                    isError = uiState.hasAmountError,
                    keyboardActions = KeyboardActions(
                        onDone = { vm.insertMovement() }
                    )
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                onClick = vm::insertMovement
            ) {
                Text(text = "+")
            }

            val totalsByDate = remember(uiState.movements) {
                uiState.movements
                    .groupBy { it.date }
                    .mapValues { (_, movements) ->
                        movements.sumOf { it.amount }
                    }
            }

            LazyColumn {
                itemsIndexed(uiState.movements) { index, movement ->

                    val previous = uiState.movements.getOrNull(index - 1)
                    val showHeader = previous == null || previous.date != movement.date

                    if (showHeader) {
                        FPMovementHeader(
                            date = movement.date,
                            total = totalsByDate[movement.date] ?: 0
                        )
                    }

                    FPMovement(movement)
                }
            }
        }
    }
}

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

@Composable
fun FPMovement(movement: Movement){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val df = DecimalFormat("$#,##0.00")

        Icon(
            modifier = Modifier.size(50.dp),
            imageVector = Icons.Default.ArrowDownward,
            contentDescription = null,
        )
        Text(text = movement.id.toString())
        Text(text = movement.description, modifier = Modifier.weight(1f))
        Text(
            text = df.format(movement.amount/100.0F),
        )
    }
}

fun Int.toLocalizedDate(): String {
    val date = LocalDate.parse(
        this.toString(),
        DateTimeFormatter.BASIC_ISO_DATE
    )

    return date.format(
        DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
            .withLocale(Locale.getDefault())
    )
}