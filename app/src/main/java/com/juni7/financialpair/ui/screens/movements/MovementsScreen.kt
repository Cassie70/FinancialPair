package com.juni7.financialpair.ui.screens.movements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.juni7.financialpair.data.entity.Category
import com.juni7.financialpair.data.entity.Movement
import com.juni7.financialpair.data.entity.MovementWithTopic
import com.juni7.financialpair.data.entity.Topic
import com.juni7.financialpair.data.entity.TopicWithCategory
import com.juni7.financialpair.ui.components.FPMovement
import com.juni7.financialpair.ui.components.FPMovementHeader
import kotlinx.coroutines.flow.MutableStateFlow
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
    val uiState: MovementsScreenState by vm.uiState.collectAsStateWithLifecycle()
    val movements = vm.movementsPaged.collectAsLazyPagingItems()

    MovementsScreenContent(
        uiState = uiState,
        movements = movements,
        onDescriptionChange = vm::onDescriptionChange,
        onAmountChange = vm::onAmountChange,
        onTopicSelected = vm::onTopicSelected,
        insertMovement = vm::insertMovement,
        onMovementClick = onMovementClick
    )
}

@Composable
fun MovementsScreenContent(
    uiState: MovementsScreenState,
    movements: LazyPagingItems<MovementWithTopic>,
    onDescriptionChange: (String) -> Unit = {},
    onAmountChange: (String) -> Unit = {},
    onTopicSelected: (Topic) -> Unit = {},
    insertMovement: () -> Unit = {},
    onMovementClick: (Long) -> Unit = {}
){
    var textFieldWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

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
            Box(
                modifier = Modifier
                    .weight(2f)
                    .onGloballyPositioned { coordinates ->
                        textFieldWidth = with(density) { coordinates.size.width.toDp() }
                    }
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.description,
                    onValueChange = onDescriptionChange,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    maxLines = 1,
                    label = { Text("Descripción") },
                    isError = uiState.hasDescriptionError
                )

                DropdownMenu(
                    expanded = uiState.suggestedTopics.isNotEmpty(),
                    onDismissRequest = { },
                    properties = PopupProperties(
                        focusable = false
                    ),
                    modifier = Modifier
                        .width(textFieldWidth)
                        .heightIn(max = 56.dp) // Solo una línea aprox y scrollable
                ) {
                    uiState.suggestedTopics.forEach { topic ->
                        DropdownMenuItem(
                            text = { Text(topic.name) },
                            onClick = { onTopicSelected(topic) }
                        )
                    }
                }
            }
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

        LazyColumn {
            items(
                count = movements.itemCount,
                key = movements.itemKey { it.movement.id }
            ) { index ->
                val movementWithTopic = movements[index]
                if (movementWithTopic != null) {
                    val movement = movementWithTopic.movement
                    val topic = movementWithTopic.topic
                    val previous = if (index > 0) movements[index - 1] else null
                    val showHeader = previous == null || previous.movement.date != movement.date

                    if (showHeader) {
                        FPMovementHeader(
                            date = movement.date,
                            total = uiState.totalsByDate[movement.date] ?: 0
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
    val mockMovements = listOf(
        MovementWithTopic(
            movement = Movement(
                id = 1,
                description = "Comida",
                amount = 2500,
                date = 20260702,
                topicId = 1
            ),
            topicWithCategory = TopicWithCategory(
                topic = Topic(id = 1, name = "Comida", categoryId = 2),
                category = Category(id = 2, name = "Comida")
            )
        ),
        MovementWithTopic(
            movement = Movement(
                id = 2,
                description = "Salario",
                amount = 15000,
                date = 20260702,
                topicId = 2
            ),
            topicWithCategory = TopicWithCategory(
                topic = Topic(id = 2, name = "Salario", categoryId = 0),
                category = Category(id = 0, name = "General")
            )
        )
    )
    
    val pagingDataFlow = remember { 
        MutableStateFlow(PagingData.from(mockMovements))
    }
    val movements = pagingDataFlow.collectAsLazyPagingItems()

    MovementsScreenContent(
        uiState = MovementsScreenState(
            description = "Hola cara de bola",
            amount = "25",
        ),
        movements = movements,
        onDescriptionChange = {},
        onAmountChange = {},
        insertMovement = {}
    )
}
