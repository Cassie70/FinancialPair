package com.juni7.financialpair.ui.screens.topics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.juni7.financialpair.data.entity.Category
import com.juni7.financialpair.data.entity.Topic
import com.juni7.financialpair.data.entity.TopicWithCategory
import com.juni7.financialpair.util.getLocalizedName
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TopicsScreen(
    vm: TopicsScreenViewModel = koinViewModel()
){
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    TopicsScreenContent(
        uiState = uiState,
        onInsertTopic = vm::insertTopic,
        onNameChange = vm::onNameChange,
        onCategoryChange = vm::onCategoryChange,
        onTopicClick = vm::onTopicClick,
        onDeleteTopic = vm::deleteTopic
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreenContent(
    uiState: TopicsScreenState = TopicsScreenState(),
    onInsertTopic: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onCategoryChange: (Category) -> Unit = {},
    onTopicClick: (TopicWithCategory) -> Unit = {},
    onDeleteTopic: (Topic) -> Unit = {}
){
    var topicToDelete by remember { mutableStateOf<Topic?>(null) }
    val scope = rememberCoroutineScope()

    if (topicToDelete != null) {
        AlertDialog(
            onDismissRequest = { topicToDelete = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar el tópico \"${topicToDelete?.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        topicToDelete?.let { onDeleteTopic(it) }
                        topicToDelete = null
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { topicToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
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
            label = { Text(if (uiState.editingTopic == null) "Nombre o buscar..." else "Nombre del tópico") },
            isError = uiState.hasNameError
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(uiState.categories) { category ->
                FilterChip(
                    selected = uiState.selectedCategory?.id == category.id,
                    onClick = { onCategoryChange(category) },
                    label = { Text(category.getLocalizedName(LocalContext.current)) },
                    leadingIcon = {
                        AsyncImage(
                            model = uiState.logoUrls[category.name],
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                )
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            onClick = onInsertTopic
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text(text = "Añadir")
        }

        uiState.error?.let {
            Text(text = it, color = Color.Red)
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(uiState.filteredTopics) { index, item ->
                val previous = uiState.filteredTopics.getOrNull(index - 1)
                val showHeader = previous == null || previous.topic.categoryId != item.topic.categoryId
                val isEditing = uiState.editingTopic?.topic?.id == item.topic.id

                if (showHeader) {
                    Text(
                        text = item.category.getLocalizedName(LocalContext.current),
                        modifier = Modifier.padding(top = 10.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                val dismissState = rememberSwipeToDismissBoxState()

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = true,
                    enableDismissFromEndToStart = false,
                    onDismiss = { value ->
                        if (value == SwipeToDismissBoxValue.StartToEnd) {
                            topicToDelete = item.topic
                            scope.launch { dismissState.reset() }
                        }
                    },
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                modifier = Modifier.padding(start = 16.dp),
                                tint = Color.Red
                            )
                        }
                    }
                ) {
                    ListItem(
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = uiState.logoUrls[item.topic.name],
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(4.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        },
                        headlineContent = {
                            Text(
                                text = item.topic.name,
                                fontWeight = if (isEditing) FontWeight.Bold else FontWeight.Normal,
                                color = if (isEditing) Color.Blue else Color.Unspecified,
                                fontSize = 16.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTopicClick(item) },
                        tonalElevation = if (isEditing) 4.dp else 0.dp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopicsScreenPreview(){
    TopicsScreenContent(
        uiState = TopicsScreenState(
            filteredTopics = listOf(
                TopicWithCategory(
                    topic = Topic(id = 1, name = "Uber", categoryId = 1),
                    category = Category(id = 1, name = "transport")
                ),
                TopicWithCategory(
                    topic = Topic(id = 2, name = "Camión", categoryId = 1),
                    category = Category(id = 1, name = "transport")
                ),
                TopicWithCategory(
                    topic = Topic(id = 3, name = "Carls jr", categoryId = 2),
                    category = Category(id = 1, name = "food")
                ),
                TopicWithCategory(
                    topic = Topic(id = 4, name = "Tacos", categoryId = 2),
                    category = Category(id = 1, name = "food")
                )
            ),
            categories = listOf(
                Category(id = 1, name = "transport"),
                Category(id = 2, name = "food")
            )
        )
    )
}
