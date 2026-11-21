package com.uth.uthtodo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthtodo.R
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import com.uth.uthtodo.ui.theme.LifeBadgeBg
import com.uth.uthtodo.ui.theme.LifeBadgeText
import com.uth.uthtodo.ui.theme.PrimaryBlue
import com.uth.uthtodo.ui.theme.StudyBadgeBg
import com.uth.uthtodo.ui.theme.StudyBadgeText
import com.uth.uthtodo.ui.theme.SurfaceMuted
import com.uth.uthtodo.ui.theme.TextPrimary
import com.uth.uthtodo.ui.theme.TextSecondary

// Màn hình đích sau Onboarding hiển thị danh sách TaskItem OOP mẫu
@Composable
fun HomePreviewScreen(
    sampleTasks: List<TaskItem>,
    onOpenDataFlow: () -> Unit,
    onOpenFirebaseAuth: () -> Unit,
    onOpenFirebaseServices: () -> Unit,
    onOpenTaskList: () -> Unit,
    onReplayOnboarding: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceMuted)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_uth_todo_logo),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.home_preview_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = R.string.home_preview_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sampleTasks, key = { it.id }) { task ->
                TaskOopCard(task = task)
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onOpenTaskList,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_open_list_flow),
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenFirebaseServices,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_open_firebase_services),
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenFirebaseAuth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_open_firebase_auth),
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenDataFlow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_open_data_flow),
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Button(
                        onClick = onReplayOnboarding,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_replay_onboarding),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskOopCard(task: TaskItem) {
    val isStudy = task.category == TaskCategory.STUDY
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isStudy) StudyBadgeBg else LifeBadgeBg
                ) {
                    Text(
                        text = task.category.displayName,
                        color = if (isStudy) StudyBadgeText else LifeBadgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "${task.time} · ${task.priority.displayName}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            if (task.subtasks.isNotEmpty()) {
                Text(
                    text = "Việc nhỏ hoàn thành: ${task.subtaskProgressText()}",
                    fontSize = 12.sp,
                    color = PrimaryBlue
                )
            }
        }
    }
}
