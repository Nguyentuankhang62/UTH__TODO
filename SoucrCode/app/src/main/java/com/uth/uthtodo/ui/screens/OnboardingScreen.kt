package com.uth.uthtodo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthtodo.R
import com.uth.uthtodo.model.OnboardingIllustration
import com.uth.uthtodo.model.OnboardingStep
import com.uth.uthtodo.ui.theme.BackgroundWhite
import com.uth.uthtodo.ui.theme.BorderLight
import com.uth.uthtodo.ui.theme.DotInactive
import com.uth.uthtodo.ui.theme.LifeBadgeBg
import com.uth.uthtodo.ui.theme.LifeBadgeText
import com.uth.uthtodo.ui.theme.PrimaryBlue
import com.uth.uthtodo.ui.theme.SoftBlueCard
import com.uth.uthtodo.ui.theme.StudyBadgeBg
import com.uth.uthtodo.ui.theme.StudyBadgeText
import com.uth.uthtodo.ui.theme.TextPrimary
import com.uth.uthtodo.ui.theme.TextSecondary
import kotlinx.coroutines.launch

// Màn hình Onboarding 3 bước nhận dữ liệu OOP từ OnboardingRepository
@Composable
fun OnboardingScreen(
    steps: List<OnboardingStep>,
    onFinishOnboarding: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { steps.size })
    val coroutineScope = rememberCoroutineScope()
    val currentStep = steps[pagerState.currentPage]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        OnboardingTopBar(onSkipClick = onFinishOnboarding)

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            OnboardingStepContent(step = steps[pageIndex])
        }

        OnboardingBottomBar(
            totalSteps = steps.size,
            currentStepIndex = pagerState.currentPage,
            buttonText = currentStep.primaryButtonText,
            onPrimaryClick = {
                if (pagerState.currentPage < steps.lastIndex) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onFinishOnboarding()
                }
            }
        )
    }
}

// Thanh tiêu đề gồm logo UTH Todo và nút Bỏ qua
@Composable
private fun OnboardingTopBar(onSkipClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_uth_todo_logo),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        TextButton(onClick = onSkipClick) {
            Text(
                text = stringResource(id = R.string.action_skip),
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Nội dung từng trang Onboarding gồm khung minh họa và tiêu đề mô tả
@Composable
private fun OnboardingStepContent(step: OnboardingStep) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.84f)
                    .height(276.dp),
                shape = RoundedCornerShape(28.dp),
                color = SoftBlueCard
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (step.illustration) {
                        OnboardingIllustration.TASK_LIST -> TaskListIllustration()
                        OnboardingIllustration.CATEGORY_GROUPS -> CategoryGroupsIllustration()
                        OnboardingIllustration.REMINDER_BELL -> ReminderIllustration()
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

// Khung minh họa danh sách công việc cho bước 1
@Composable
private fun TaskListIllustration() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IllustrationTaskRow(isCompleted = true, barFraction = 0.65f)
        IllustrationTaskRow(isCompleted = false, barFraction = 0.78f)
        IllustrationTaskRow(isCompleted = false, barFraction = 0.52f)
    }
}

@Composable
private fun IllustrationTaskRow(isCompleted: Boolean, barFraction: Float) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isCompleted) {
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

            Box(
                modifier = Modifier
                    .fillMaxWidth(barFraction)
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) Color(0xFFCBD5E1) else Color(0xFFE2E8F0))
            )
        }
    }
}

// Khung minh họa phân nhóm Học tập và Cuộc sống cho bước 2
@Composable
private fun CategoryGroupsIllustration() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IllustrationCategoryCard(
            label = stringResource(id = R.string.category_study),
            badgeBg = StudyBadgeBg,
            badgeText = StudyBadgeText,
            barFraction = 0.7f
        )
        IllustrationCategoryCard(
            label = stringResource(id = R.string.category_life),
            badgeBg = LifeBadgeBg,
            badgeText = LifeBadgeText,
            barFraction = 0.56f
        )
    }
}

@Composable
private fun IllustrationCategoryCard(
    label: String,
    badgeBg: Color,
    badgeText: Color,
    barFraction: Float
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeBg
            ) {
                Text(
                    text = label,
                    color = badgeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(barFraction)
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
            )
        }
    }
}

// Khung minh họa chuông nhắc nhở cho bước 3
@Composable
private fun ReminderIllustration() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.ic_bell),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            shadowElevation = 3.dp
        ) {
            Text(
                text = stringResource(id = R.string.sample_reminder_text),
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

// Thanh dưới gồm chấm chỉ báo trang và nút điều hướng chính
@Composable
private fun OnboardingBottomBar(
    totalSteps: Int,
    currentStepIndex: Int,
    buttonText: String,
    onPrimaryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSteps) { index ->
                val isSelected = index == currentStepIndex
                Box(
                    modifier = Modifier
                        .width(if (isSelected) 24.dp else 8.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) PrimaryBlue else DotInactive)
                )
            }
        }

        Button(
            onClick = onPrimaryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue,
                contentColor = Color.White
            )
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
