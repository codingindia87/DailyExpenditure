package com.codingindia.dailyexpenditure.ui.screens.download

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    
    val currentExpensesList by viewModel.filteredExpenses.collectAsState()

    var showSinglePicker by remember { mutableStateOf(false) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Download Report", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!viewModel.isRangeMode) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable { viewModel.setMode(false) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("One Day's", color = if (!viewModel.isRangeMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (viewModel.isRangeMode) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable { viewModel.setMode(true) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Date Range", color = if (viewModel.isRangeMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!viewModel.isRangeMode) {
                        Text("Select Date", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { showSinglePicker = true }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(viewModel.formatDate(viewModel.singleDate), fontWeight = FontWeight.Medium, fontSize = 16.sp)
                            Icon(imageVector = Icons.Default.CalendarMonth, tint = MaterialTheme.colorScheme.primary, contentDescription = null)
                        }
                    } else {
                        Text("Start Date", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { showStartPicker = true }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(viewModel.formatDate(viewModel.startDate), fontWeight = FontWeight.Medium, fontSize = 16.sp)
                            Icon(imageVector = Icons.Default.CalendarMonth, tint = MaterialTheme.colorScheme.primary, contentDescription = null)
                        }

                        Text("End Date", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { showEndPicker = true }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(viewModel.formatDate(viewModel.endDate), fontWeight = FontWeight.Medium, fontSize = 16.sp)
                            Icon(imageVector = Icons.Default.CalendarMonth, tint = MaterialTheme.colorScheme.primary, contentDescription = null)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Total items as of this date: ${currentExpensesList.size}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (currentExpensesList.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1.0f))

            Button(
                onClick = {
                    viewModel.generatePdfReport(context) { message ->
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = currentExpensesList.isNotEmpty()
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Download PDF", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showSinglePicker) {
        MyDatePickerDialog(
            initialTime = viewModel.singleDate,
            onDismiss = { showSinglePicker = false },
            onDateSelected = { viewModel.updateSingleDate(it); showSinglePicker = false }
        )
    }
    if (showStartPicker) {
        MyDatePickerDialog(
            initialTime = viewModel.startDate,
            onDismiss = { showStartPicker = false },
            onDateSelected = { viewModel.updateStartDate(it); showStartPicker = false }
        )
    }
    if (showEndPicker) {
        MyDatePickerDialog(
            initialTime = viewModel.endDate,
            onDismiss = { showEndPicker = false },
            onDateSelected = { viewModel.updateEndDate(it); showEndPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    initialTime: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialTime)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { onDateSelected(it) }
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) { DatePicker(state = datePickerState) }
}