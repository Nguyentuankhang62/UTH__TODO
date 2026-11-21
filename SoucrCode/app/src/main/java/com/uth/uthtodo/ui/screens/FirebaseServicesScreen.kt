package com.uth.uthtodo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthtodo.firebase.FirebaseServiceItem
import com.uth.uthtodo.firebase.FirebaseServiceStatus
import com.uth.uthtodo.firebase.FirebaseServicesManager
import com.uth.uthtodo.ui.theme.BorderLight
import com.uth.uthtodo.ui.theme.PrimaryBlue
import com.uth.uthtodo.ui.theme.StudyBadgeBg
import com.uth.uthtodo.ui.theme.StudyBadgeText
import com.uth.uthtodo.ui.theme.SurfaceMuted
import com.uth.uthtodo.ui.theme.TextPrimary
import com.uth.uthtodo.ui.theme.TextSecondary

// Màn hình tổng quan và kiểm thử các dịch vụ Firebase đã tích hợp
@Composable
fun FirebaseServicesScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val manager = remember(context) { FirebaseServicesManager(context) }
    var uiState by remember { mutableStateOf(manager.buildSnapshot()) }

    LaunchedEffect(Unit) {
        manager.logTestEvent("firebase_services_viewed", "FirebaseServicesScreen")
        manager.fetchFcmToken { ok, token, _ ->
            if (ok) {
                uiState = manager.buildSnapshot(
                    fcmToken = token,
                    remoteBannerText = uiState.remoteBannerText
                ).copy(
                    lastActionMessage = uiState.lastActionMessage,
                    isError = uiState.isError
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceMuted)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "< Quay lại",
                color = PrimaryBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onBack() }
                    .padding(vertical = 6.dp)
            )
            Text(
                text = "Dịch vụ Firebase",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = PrimaryBlue
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Firebase Project: ${uiState.projectId}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${uiState.services.size} dịch vụ đã tích hợp",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = uiState.remoteBannerText,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        if (uiState.lastActionMessage != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (uiState.isError) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
            ) {
                Text(
                    text = uiState.lastActionMessage.orEmpty(),
                    color = if (uiState.isError) Color(0xFFB91C1C) else Color(0xFF166534),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }

        Text(
            text = "Kiểm thử nhanh dịch vụ",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val msg = manager.logTestEvent("manual_analytics_ping", "FirebaseServicesScreen")
                    uiState = uiState.copy(lastActionMessage = msg, isError = false)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Analytics", fontSize = 13.sp)
            }

            Button(
                onClick = {
                    uiState = uiState.copy(isBusy = true)
                    manager.fetchRemoteConfig { ok, banner, msg ->
                        uiState = manager.buildSnapshot(
                            fcmToken = uiState.fcmToken,
                            remoteBannerText = banner
                        ).copy(
                            lastActionMessage = msg,
                            isError = !ok,
                            isBusy = false
                        )
                    }
                },
                enabled = !uiState.isBusy,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Remote Config", fontSize = 13.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    uiState = uiState.copy(isBusy = true)
                    manager.fetchFcmToken { ok, token, msg ->
                        uiState = manager.buildSnapshot(
                            fcmToken = token,
                            remoteBannerText = uiState.remoteBannerText
                        ).copy(
                            lastActionMessage = msg,
                            isError = !ok,
                            isBusy = false
                        )
                    }
                },
                enabled = !uiState.isBusy,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Text("FCM Token", color = PrimaryBlue, fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = {
                    val msg = manager.recordNonFatalTestLog()
                    uiState = uiState.copy(lastActionMessage = msg, isError = false)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Text("Crashlytics", color = PrimaryBlue, fontSize = 13.sp)
            }
        }

        OutlinedButton(
            onClick = {
                uiState = uiState.copy(isBusy = true)
                manager.syncSampleToFirestore { ok, msg ->
                    uiState = uiState.copy(
                        lastActionMessage = msg,
                        isError = !ok,
                        isBusy = false
                    )
                }
            },
            enabled = !uiState.isBusy,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Text("Kiểm tra đồng bộ Cloud Firestore", color = PrimaryBlue, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "Danh sách dịch vụ tích hợp",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        uiState.services.forEach { item ->
            FirebaseServiceCard(item = item)
        }
    }
}

@Composable
private fun FirebaseServiceCard(item: FirebaseServiceItem) {
    val badgeBg = when (item.status) {
        FirebaseServiceStatus.ACTIVE -> Color(0xFFDCFCE7)
        FirebaseServiceStatus.READY -> StudyBadgeBg
        FirebaseServiceStatus.WARNING -> Color(0xFFFEF3C7)
    }
    val badgeText = when (item.status) {
        FirebaseServiceStatus.ACTIVE -> Color(0xFF15803D)
        FirebaseServiceStatus.READY -> StudyBadgeText
        FirebaseServiceStatus.WARNING -> Color(0xFFB45309)
    }

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
                Column {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = item.category,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = item.status.label,
                        color = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = item.detail,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}
