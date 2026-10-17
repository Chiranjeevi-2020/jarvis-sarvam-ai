package com.sarvam.jarvis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarvam.jarvis.data.local.entities.GoalEntity
import com.sarvam.jarvis.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GoalsScreen(
    goals: List<GoalEntity>
) {
    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    val longTermGoals = goals.filter { it.type == "LONG_TERM" }
    val shortTermGoals = goals.filter { it.type == "SHORT_TERM" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Text(
            text = "Goals & Career Roadmap",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Connecting your daily routine to your final destination",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Long Term Goals Section
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "North Star (Long-Term Goal)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CyanGlow)
                }
            }

            items(longTermGoals) { goal ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(listOf(DarkSurface, DarkSurfaceVariant))
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Text(text = goal.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = goal.description, fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Target: ${dateFormat.format(Date(goal.targetDate))}", fontSize = 11.sp, color = AccentEmerald)
                                Text(text = "${goal.progressPercent}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanGlow)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (goal.progressPercent / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = CyanGlow,
                                trackColor = DarkBackground
                            )
                        }
                    }
                }
            }

            // Short Term Weekly Sprints
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Flag, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Weekly Sprints (Short-Term)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AccentEmerald)
                }
            }

            items(shortTermGoals) { goal ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = goal.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        if (goal.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = goal.description, fontSize = 12.sp, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (goal.progressPercent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(2.5.dp)),
                            color = AccentEmerald,
                            trackColor = DarkSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
