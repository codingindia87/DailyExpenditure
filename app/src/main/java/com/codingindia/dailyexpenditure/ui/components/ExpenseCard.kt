package com.codingindia.dailyexpenditure.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codingindia.dailyexpenditure.data.local.entity.Expense
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseCard(expense: Expense, modifier: Modifier = Modifier) {

    val (icon, iconColor) = remember(expense.category) {
        when (expense.category) {
            "Food" -> Icons.Default.Fastfood to Color(0xFFFF9800)
            "Travel" -> Icons.Default.DirectionsCar to Color(0xFF2196F3)
            "Shopping" -> Icons.Default.ShoppingBag to Color(0xFFE91E63)
            "Given/Lent" -> Icons.Default.Handshake to Color(0xFF9C27B0)
            "Petrol" -> Icons.Default.LocalGasStation to Color(0xFF4CAF50)
            "Water" -> Icons.Default.WaterDrop to Color(0xFF03A9F4)
            "Grocery" -> Icons.Default.ShoppingCart to Color(0xFF2E7D32)
            "Miscellaneous" -> Icons.Default.Category to Color(0xFF64748B)
            "Rent" -> Icons.Default.Home to Color(0xFFC2410C)
            "Electricity Bill" -> Icons.Default.ElectricBolt to Color(0xFFF59E0B)
            "Mobile Recharge" -> Icons.Default.PhoneAndroid to Color(0xFFD946EF)
            "Medicine/Healthcare" -> Icons.Default.Medication to Color(0xFF06B6D4)
            else -> Icons.Default.AttachMoney to Color(0xFF607D8B)
        }
    }

    val dateString = remember(expense.timestamp) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault())
        dateFormat.format(Date(expense.timestamp))
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = BorderStroke(
            width = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor.copy(alpha = 0.12f)), contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description, style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold, letterSpacing = 0.2.sp
                    ), color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = expense.category,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "₹${expense.amount}", style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp
                ), color = if (expense.category == "Given/Lent") {
                    Color(0xFFD32F2F)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}