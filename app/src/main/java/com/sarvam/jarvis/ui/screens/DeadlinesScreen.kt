package com.sarvam.jarvis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarvam.jarvis.data.local.entities.AssignmentEntity
import com.sarvam.jarvis.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlinesScreen(
    assignments: List<AssignmentEntity>,
    onToggleSubmitted: (AssignmentEntity) -> Unit,
    onDeleteAssignment: (AssignmentEntity) -> Unit,
    onAddAssignment: (course: String, title: String, days: Long, priority: String) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CyanGlow,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Assignment")
            }
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Coursework & Deadlines",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Track homework, lab reports, and semester submissions",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (assignments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No coursework added yet. Tap + to add or ask Jarvis!", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(assignments) { item ->
                        val isOverdue = !item.isSubmitted && item.dueDate < System.currentTimeMillis()
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
                                Checkbox(
                                    checked = item.isSubmitted,
                                    onCheckedChange = { onToggleSubmitted(item) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = AccentEmerald,
                                        uncheckedColor = TextSecondary
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.courseName,
                                            fontSize = 12.sp,
                                            color = CyanGlow,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        // Priority badge
                                        val priorityColor = when (item.priority.uppercase()) {
                                            "HIGH" -> AccentCoral
                                            "LOW" -> CyanGlow
                                            else -> AccentAmber
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(priorityColor.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(item.priority, fontSize = 9.sp, color = priorityColor, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = item.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (item.isSubmitted) TextSecondary else TextPrimary,
                                        textDecoration = if (item.isSubmitted) TextDecoration.LineThrough else TextDecoration.None
                                    )

                                    Text(
                                        text = if (isOverdue) "Overdue! Due: ${dateFormat.format(Date(item.dueDate))}" else "Due: ${dateFormat.format(Date(item.dueDate))}",
                                        fontSize = 11.sp,
                                        color = if (isOverdue) AccentCoral else TextSecondary
                                    )
                                }

                                IconButton(onClick = { onDeleteAssignment(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextSecondary.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var course by remember { mutableStateOf("") }
        var title by remember { mutableStateOf("") }
        var days by remember { mutableStateOf("3") }
        var priority by remember { mutableStateOf("MEDIUM") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Coursework / Assignment", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = course,
                        onValueChange = { course = it },
                        label = { Text("Course (e.g. Operating Systems)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Assignment Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it },
                        label = { Text("Due in (days)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onAddAssignment(course.ifBlank { "General" }, title, days.toLongOrNull() ?: 3L, priority)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanGlow)
                ) {
                    Text("Add", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}
