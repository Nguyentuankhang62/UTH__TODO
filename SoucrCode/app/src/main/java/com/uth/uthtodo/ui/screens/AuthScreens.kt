package com.uth.uthtodo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthtodo.R
import com.uth.uthtodo.auth.AuthUiState
import com.uth.uthtodo.model.TaskCategory
import com.uth.uthtodo.model.TaskItem
import com.uth.uthtodo.model.UserProfile
import com.uth.uthtodo.ui.theme.BackgroundWhite
import com.uth.uthtodo.ui.theme.BorderLight
import com.uth.uthtodo.ui.theme.LifeBadgeBg
import com.uth.uthtodo.ui.theme.LifeBadgeText
import com.uth.uthtodo.ui.theme.PrimaryBlue
import com.uth.uthtodo.ui.theme.SoftBlueCard
import com.uth.uthtodo.ui.theme.StudyBadgeBg
import com.uth.uthtodo.ui.theme.StudyBadgeText
import com.uth.uthtodo.ui.theme.SurfaceMuted
import com.uth.uthtodo.ui.theme.TextPrimary
import com.uth.uthtodo.ui.theme.TextSecondary

// Màn hình Đăng nhập Firebase Authentication theo thiết kế UTH Todo
@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onEmailSignIn: (String, String) -> Unit,
    onEmailRegister: (String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onGuestSignIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_uth_todo_logo),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Chào mừng đến với\nUTH Todo",
                fontSize = 26.sp,
                lineHeight = 33.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Email (vd: uth@gmail.com)") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_email),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceMuted,
                    unfocusedContainerColor = SurfaceMuted,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderLight
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Mật khẩu (tối thiểu 6 ký tự)") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceMuted,
                    unfocusedContainerColor = SurfaceMuted,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = BorderLight
                )
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEE2E2)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = Color(0xFFB91C1C),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isRegisterMode) {
                        onEmailRegister(email, password)
                    } else {
                        onEmailSignIn(email, password)
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (isRegisterMode) "Đăng ký tài khoản" else "Đăng nhập với Email",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                    Text(
                        text = if (isRegisterMode) {
                            "Đã có tài khoản? Đăng nhập"
                        } else {
                            "Chưa có tài khoản? Đăng ký mới"
                        },
                        color = PrimaryBlue,
                        fontSize = 13.sp
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
                Text(
                    text = "  hoặc  ",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderLight)
            }

            OutlinedButton(
                onClick = onGoogleSignIn,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Tiếp tục với Google",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(
                onClick = onGuestSignIn,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Dùng thử không cần tài khoản",
                    color = PrimaryBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",
            fontSize = 11.sp,
            lineHeight = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}

// Màn hình Trang chủ sau khi đăng nhập thành công, bấm vào Avatar để mở Hồ sơ cá nhân
@Composable
fun AuthHomeScreen(
    user: UserProfile,
    tasks: List<TaskItem>,
    bannerMessage: String?,
    onProfileClick: () -> Unit
) {
    val completedCount = tasks.count { it.done }
    val totalCount = tasks.size.coerceAtLeast(1)
    val percent = (completedCount * 100) / totalCount
    val studyRemaining = tasks.count { it.category == TaskCategory.STUDY && !it.done }
    val lifeRemaining = tasks.count { it.category == TaskCategory.LIFE && !it.done }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceMuted)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (bannerMessage != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFDCFCE7)
                ) {
                    Text(
                        text = bannerMessage,
                        color = Color(0xFF166534),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.clickable { onProfileClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(StudyBadgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.initials(),
                            color = StudyBadgeText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Thứ Ba, 20 tháng 10",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Chào ${user.displayName}!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bell),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = PrimaryBlue
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Tiến độ hôm nay",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                        Text(
                            text = "$completedCount/$totalCount việc",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Đã hơn nửa chặng, cố lên!",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$percent%",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategorySummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Học tập",
                    subtitle = "$studyRemaining việc còn lại",
                    badgeBg = StudyBadgeBg
                )
                CategorySummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "Cuộc sống",
                    subtitle = "$lifeRemaining việc còn lại",
                    badgeBg = LifeBadgeBg
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Việc hôm nay",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Xem tất cả",
                    fontSize = 13.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            tasks.forEach { task ->
                HomeTaskRow(task = task)
            }
        }

        HomeBottomNavBar(onProfileClick = onProfileClick)
    }
}

@Composable
private fun CategorySummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    badgeBg: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeBg)
            )
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun HomeTaskRow(task: TaskItem) {
    val isStudy = task.category == TaskCategory.STUDY
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (task.done) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .border(1.8.dp, BorderLight, CircleShape)
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (task.done) TextSecondary else TextPrimary,
                    textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isStudy) StudyBadgeBg else LifeBadgeBg
                    ) {
                        Text(
                            text = task.category.displayName,
                            color = if (isStudy) StudyBadgeText else LifeBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = task.time,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_chevron_right),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun HomeBottomNavBar(onProfileClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(iconRes = R.drawable.ic_home, label = "Trang chủ", selected = true)
            BottomNavItem(iconRes = R.drawable.ic_list, label = "Danh sách", selected = false)

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }

            BottomNavItem(iconRes = R.drawable.ic_bell, label = "Thông báo", selected = false)
            Box(modifier = Modifier.clickable { onProfileClick() }) {
                BottomNavItem(iconRes = R.drawable.ic_settings, label = "Hồ sơ", selected = false)
            }
        }
    }
}

@Composable
private fun BottomNavItem(iconRes: Int, label: String, selected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (selected) PrimaryBlue else TextSecondary,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// Màn hình Thông tin cá nhân hiển thị hồ sơ tài khoản, thống kê và nút Đăng xuất
@Composable
fun ProfileScreen(
    uiState: AuthUiState,
    onBack: () -> Unit,
    onSignOut: () -> Unit
) {
    val user = uiState.currentUser ?: return
    val weeklyCounts = listOf("T2" to 0.25f, "T3" to 0.65f, "T4" to 0.1f, "T5" to 0.1f, "T6" to 0.1f, "T7" to 0.1f, "CN" to 0.1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceMuted)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "< Trang chủ",
                    color = PrimaryBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onBack() }
                        .padding(vertical = 6.dp)
                )
                Text(
                    text = "Hồ sơ",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(StudyBadgeBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.initials(),
                    color = StudyBadgeText,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = user.displayName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = user.email,
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFDCFCE7)
            ) {
                Text(
                    text = uiState.providerType.badgeText,
                    color = Color(0xFF15803D),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(modifier = Modifier.weight(1f), value = "2", label = "Việc đã xong")
                ProfileStatCard(modifier = Modifier.weight(1f), value = "4", label = "Đang chờ")
                ProfileStatCard(modifier = Modifier.weight(1f), value = "33%", label = "Hoàn thành")
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tuần này",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Việc xong mỗi ngày",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(74.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        weeklyCounts.forEach { (day, ratio) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height((48 * ratio).dp.coerceAtLeast(6.dp))
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (ratio > 0.2f) PrimaryBlue else SoftBlueCard
                                        )
                                )
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFFFECACA)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color(0xFFFEF2F2),
                contentColor = Color(0xFFDC2626)
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Đăng xuất",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProfileStatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
