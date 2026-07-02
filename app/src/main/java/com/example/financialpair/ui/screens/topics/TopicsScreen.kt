package com.example.financialpair.ui.screens.topics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.financialpair.data.entity.Category
import com.example.financialpair.data.entity.Topic
import com.example.financialpair.data.entity.TopicWithCategory
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TopicsScreen(
    vm: TopicsScreenViewModel = koinViewModel()
){
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    TopicsScreenContent(
        uiState = uiState,
        insertTopic = vm::insertTopic,
        onNameChange = vm::onNameChange,
        onCategoryChange = vm::onCategoryChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreenContent(
    uiState: TopicsScreenState = TopicsScreenState(),
    insertTopic: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onCategoryChange: (Category) -> Unit = {}
){
    var showSheet by remember { mutableStateOf(false) }

    if (showSheet) {

        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
            }
        ) {
            LazyColumn {
                uiState.categories.forEach { category ->
                    item {
                        ListItem(
                            headlineContent = {
                                Text(category.name)
                            },
                            modifier = Modifier.clickable {
                                onCategoryChange(category)
                                showSheet = false
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text("Nombre del tópico") }
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            onClick = { showSheet = true }
        ) {
            Text(text = uiState.selectedCategory?.name ?: "seleccionar categoria")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            onClick = insertTopic
        ) {
            Text(text = "Añadir +")
        }
        uiState.error?.let {
            Text(text = it, color = androidx.compose.ui.graphics.Color.Red)
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(uiState.topics) { index, item ->
                val previous = uiState.topics.getOrNull(index - 1)
                val showHeader = previous == null || previous.topic.categoryId != item.topic.categoryId

                if (showHeader) {
                    Text(
                        text = item.categoryName,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                Text(text = item.topic.name,)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopicsScreenPreview(){
    TopicsScreenContent(
        uiState = TopicsScreenState(
            topics = listOf(
                TopicWithCategory(
                    topic = Topic(id = 1, name = "Uber", categoryId = 1),
                    categoryName = "Transporte"
                ),
                TopicWithCategory(
                    topic = Topic(id = 2, name = "Camión", categoryId = 1),
                    categoryName = "Transporte"
                ),
                TopicWithCategory(
                    topic = Topic(id = 3, name = "Carls jr", categoryId = 2),
                    categoryName = "Comida"
                ),
                TopicWithCategory(
                    topic = Topic(id = 4, name = "Tacos", categoryId = 2),
                    categoryName = "Comida"
                )
            ),
            categories = listOf(
                Category(id = 1, name = "Transporte"),
                Category(id = 2, name = "Comida")
            )
        )
    )
}
