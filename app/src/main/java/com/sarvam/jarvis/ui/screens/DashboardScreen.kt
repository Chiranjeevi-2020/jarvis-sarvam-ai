package com.sarvam.jarvis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarvam.jarvis.data.local.entities.AssignmentEntity
import com.sarvam.jarvis.data.local.entities.GoalEntity
import com.sarvam.jarvis.data.local.entities.ScheduleEntity
import com.sarvam.jarvis.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    assignments: List<AssignmentEntity>,
    scheduleSlots: List<ScheduleEntity>,
    goals: List<GoalEntity>,
    totalDsaSolved: Int,
    onSpeakBriefing: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome & Briefing Banner
        item {
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
                        Text(
                            text = "COMMAND CENTER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanGlow,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Welcome back, Student",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You have ${assignments.count { !it.isSubmitted }} pending assignments and ${scheduleSlots.size} scheduled blocks today.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onSpeakBriefing,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("🎧 Daily Voice Briefing", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.Code, contentDescription = "DSA", tint = CyanGlow, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "$totalDsaSolved", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "DSA Solved", fontSize = 12.sp, color = TextSecondary)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Default.Assignment, contentDescription = "Deadlines", tint = AccentAmber, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "${assignments.count { !it.isSubmitted }}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Pending Deadlines", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Today's Routine / Schedule
        item {
            Text(text = "Today's Schedule Routine", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        if (scheduleSlots.isEmpty()) {
            item {
                Text(text = "No study or DSA slots scheduled yet. Ask Jarvis to schedule one!", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            items(scheduleSlots.size) { i ->
                val slot = scheduleSlots[i]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = CyanGlow)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = slot.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(text = "${slot.startTime} - ${slot.endTime} • ${slot.category}", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Urgent Deadlines Radar
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Urgent Deadlines Radar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        val urgentDeadlines = assignments.filter { !it.isSubmitted }.take(3)
        if (urgentDeadlines.isEmpty()) {
            item {
                Text(text = "All caught up! No impending deadlines.", color = AccentEmerald, fontSize = 13.sp)
            }
        } else {
            items(urgentDeadlines.size) { i ->
                val item = urgentDeadlines[i]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.courseName, color = CyanGlow, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = item.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Due: ${dateFormat.format(Date(item.dueDate))}", color = AccentAmber, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
