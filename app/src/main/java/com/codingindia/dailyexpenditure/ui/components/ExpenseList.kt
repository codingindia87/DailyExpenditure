package com.codingindia.dailyexpenditure.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.codingindia.dailyexpenditure.data.local.entity.Expense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseList(
    expenses: List<Expense>, onExpenseDeleted: (Expense) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    LaunchedEffect(showDialog) {
        if (!showDialog) {
            expenseToDelete = null
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        items(
            items = expenses, key = { expense -> expense.id }) { expense ->

            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                        expenseToDelete = expense
                        showDialog = true
                        false
                    } else {
                        false
                    }
                })

            if (!showDialog && expenseToDelete == expense) {
                LaunchedEffect(Unit) {
                    dismissState.reset()
                }
            }

            SwipeToDismissBox(
                state = dismissState, enableDismissFromStartToEnd = false, backgroundContent = {

                    val isSwiping = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart

                    val color by animateColorAsState(
                        targetValue = if (isSwiping && dismissState.progress > 0f) Color.Red else Color.Transparent,
                        label = "BackgroundColor"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp), contentAlignment = Alignment.CenterEnd
                    ) {

                        if (isSwiping && dismissState.progress > 0.1f) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White
                            )
                        }
                    }
                }) {
                ExpenseCard(expense = expense)
            }
        }
    }

    if (showDialog && expenseToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Delete Expense") },
            text = { Text(text = "Are you sure you want to delete this expense? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        expenseToDelete?.let { onExpenseDeleted(it) }
                        showDialog = false
                    }) {
                    Text(text = "Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = "Cancel")
                }
            })
    }
}