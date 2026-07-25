package com.juni7.financialpair.ui.screens.movements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.juni7.financialpair.data.entity.Movement
import com.juni7.financialpair.data.entity.MovementWithTopic
import com.juni7.financialpair.data.entity.Topic
import com.juni7.financialpair.ui.components.FPMovement
import com.juni7.financialpair.ui.components.FPMovementHeader
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


@Composable
fun MovementsScreen(
    onMovementClick: (Long) -> Unit = {},
    vm: MovementsScreenViewModel = koinViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    MovementsScreenContent(
        uiState = uiState,
        onDescriptionChange = vm::onDescriptionChange,
        onAmountChange = vm::onAmountChange,
        insertMovement = vm::insertMovement,
        onMovementClick = onMovementClick
    )
}

@Composable
fun MovementsScreenContent(
    uiState: MovementsScreenState,
    onDescriptionChange: (String) -> Unit = {},
    onAmountChange: (String) -> Unit = {},
    insertMovement: () -> Unit = {},
    onMovementClick: (Long) -> Unit = {}
){
    Column(
        modifier = Modifier
            .fillMaxSize()
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
                onValueChange = onDescriptionChange,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                maxLines = 1,
                label = { Text("Descripción") },
                isError = uiState.hasDescriptionError
            )
            TextField(
                modifier = Modifier.weight(1f),
                value = uiState.amount,
                onValueChange = onAmountChange,
                prefix = { Text("$") },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                maxLines = 1,
                isError = uiState.hasAmountError,
                keyboardActions = KeyboardActions(
                    onDone = { insertMovement() }
                )
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            onClick = insertMovement
        ) {
            Text(text = "+")
        }

        val totalsByDate = remember(uiState.movements) {
            uiState.movements
                .groupBy { it.movement.date }
                .mapValues { (_, movements) ->
                    movements.sumOf { it.movement.amount }
                }
        }
        LazyColumn {
            itemsIndexed(uiState.movements) { index, movementWithTopic ->
                val movement = movementWithTopic.movement
                val topic = movementWithTopic.topic
                val previous = uiState.movements.getOrNull(index - 1)
                val showHeader = previous == null || previous.movement.date != movement.date

                if (showHeader) {
                    FPMovementHeader(
                        date = movement.date,
                        total = totalsByDate[movement.date] ?: 0
                    )
                }
                FPMovement(
                    movement = movement,
                    logoUrl = uiState.logoUrls[topic.name],
                    onClick = { onMovementClick(movement.id) }
                )
            }
        }
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

@Preview(showBackground = true)
@Composable
private fun MovementsScreenPreview() {
    MovementsScreenContent(
        uiState = MovementsScreenState(
            description = "Hola cara de bola",
            amount = "25",
            movements = listOf(
                MovementWithTopic(
                    movement = Movement(
                        id = 1,
                        description = "Comida",
                        amount = 2500,
                        date = 20260702,
                        topicId = 1
                    ),
                    topic = Topic(id = 1, name = "Comida", categoryId = 0)
                ),
                MovementWithTopic(
                    movement = Movement(
                        id = 2,
                        description = "Salario",
                        amount = 15000,
                        date = 20260702,
                        topicId = 2
                    ),
                    topic = Topic(id = 2, name = "Salario", categoryId = 0)
                ),
                MovementWithTopic(
                    movement = Movement(
                        id = 3,
                        description = "Netflix",
                        amount = -150,
                        date = 20260701,
                        topicId = 3
                    ),
                    topic = Topic(id = 3, name = "Netflix", categoryId = 0)
                ),
                MovementWithTopic(
                    movement = Movement(
                        id = 4,
                        description = "Comida",
                        amount = 2500,
                        date = 20260701,
                        topicId = 1
                    ),
                    topic = Topic(id = 1, name = "Comida", categoryId = 0)
                )
            )
        ),
        onDescriptionChange = {},
        onAmountChange = {},
        insertMovement = {}
    )
}
