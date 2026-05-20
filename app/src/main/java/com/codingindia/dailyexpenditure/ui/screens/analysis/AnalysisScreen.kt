package com.codingindia.dailyexpenditure.ui.screens.analysis

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.codingindia.dailyexpenditure.ui.screens.home.HomeViewModel
import java.util.Calendar

enum class FilterPeriod(val title: String) {
    LAST_7_DAYS("Last 7 Days"), LAST_24_DAYS("Last 24 Days"), LAST_30_DAYS("Last 30 Days"), LAST_6_MONTHS(
        "Last 6 Months"
    ),
    LAST_1_YEAR("Last 1 Year"), LIFETIME("Lifetime")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    viewModel: HomeViewModel, onNavigateBack: () -> Unit
) {
    val allExpenses by viewModel.expensesState.collectAsState()

    var selectedPeriod by remember { mutableStateOf(FilterPeriod.LAST_7_DAYS) }

    val filteredExpenses = remember(allExpenses, selectedPeriod) {
        val calendar = Calendar.getInstance()
        val endMillis = System.currentTimeMillis()

        val startMillis = when (selectedPeriod) {
            FilterPeriod.LAST_7_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }

            FilterPeriod.LAST_24_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -24)
                calendar.timeInMillis
            }

            FilterPeriod.LAST_30_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                calendar.timeInMillis
            }

            FilterPeriod.LAST_6_MONTHS -> {
                calendar.add(Calendar.MONTH, -6)
                calendar.timeInMillis
            }

            FilterPeriod.LAST_1_YEAR -> {
                calendar.add(Calendar.YEAR, -1)
                calendar.timeInMillis
            }

            FilterPeriod.LIFETIME -> 0L
        }

        if (selectedPeriod == FilterPeriod.LIFETIME) {
            allExpenses
        } else {
            allExpenses.filter { expense -> expense.timestamp in startMillis..endMillis }
        }
    }

    fun getCategoryColor(category: String): Color {
        return when (category) {
            "Food" -> Color(0xFFFF9800)
            "Travel" -> Color(0xFF2196F3)
            "Shopping" -> Color(0xFFE91E63)
            "Given/Lent" -> Color(0xFF9C27B0)
            "Petrol" -> Color(0xFF4CAF50)
            "Water" -> Color(0xFF03A9F4)
            "Grocery" -> Color(0xFF2E7D32)
            "Miscellaneous" -> Color(0xFF64748B)
            "Rent" -> Color(0xFFC2410C)
            "Electricity Bill" -> Color(0xFFF59E0B)
            "Mobile Recharge" -> Color(0xFFD946EF)
            "Medicine/Healthcare" -> Color(0xFF06B6D4)
            else -> Color(0xFF607D8B)
        }
    }

    val categoryBreakdown = remember(filteredExpenses) {
        val total = filteredExpenses.sumOf { it.amount }
        if (total == 0.0) emptyList()
        else {
            filteredExpenses.groupBy { it.category }.map { (category, list) ->
                val sum = list.sumOf { it.amount }
                CategoryShare(
                    category = category,
                    amount = sum,
                    percentage = (sum / total).toFloat(),
                    color = getCategoryColor(category)
                )
            }.sortedByDescending { it.amount }
        }
    }

    val totalSelectedExpense = filteredExpenses.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Expense Analysis", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterPeriod.entries.forEach { period ->
                    val isSelected = selectedPeriod == period

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPeriod = period },
                        label = {
                            Text(
                                text = period.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available for ${selectedPeriod.title.lowercase()}.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ), modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PieChart(
                                    data = categoryBreakdown,
                                    totalAmount = totalSelectedExpense,
                                    modifier = Modifier.size(140.dp)
                                )

                                Spacer(modifier = Modifier.width(24.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Total Expenses",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "₹${String.format("%.2f", totalSelectedExpense)}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Category Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(categoryBreakdown) { share ->
                        CategoryAnalysisRow(share = share)
                    }
                }
            }
        }
    }
}

@Composable
fun PieChart(
    data: List<CategoryShare>, totalAmount: Double, modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animateSweep by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "PieChartAnimation"
    )

    LaunchedEffect(key1 = data) {
        animationPlayed = true
    }

    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f

            data.forEach { share ->
                val sweepAngle = share.percentage * 360f * animateSweep

                drawArc(
                    color = share.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 32f)
                )
                startAngle += sweepAngle
            }
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CategoryAnalysisRow(share: CategoryShare) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(share.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = share.category,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "(${String.format("%.1f", share.percentage * 100)}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = "₹${String.format("%.2f", share.amount)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { share.percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = share.color,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
        )
    }
}

data class CategoryShare(
    val category: String, val amount: Double, val percentage: Float, val color: Color
)