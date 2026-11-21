package com.uth.uthtodo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uth.uthtodo.R
import com.uth.uthtodo.model.PasswordResetFlowData
import com.uth.uthtodo.model.PasswordResetValidator
import com.uth.uthtodo.model.ValidationResult
import com.uth.uthtodo.ui.theme.BackgroundWhite
import com.uth.uthtodo.ui.theme.BorderLight
import com.uth.uthtodo.ui.theme.PrimaryBlue
import com.uth.uthtodo.ui.theme.SoftBlueCard
import com.uth.uthtodo.ui.theme.SurfaceMuted
import com.uth.uthtodo.ui.theme.TextPrimary
import com.uth.uthtodo.ui.theme.TextSecondary

// Khung giao diện dùng chung cho 4 màn hình đặt lại mật khẩu
@Composable
private fun AuthFlowScaffold(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            if (showBackButton) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF3898EC))
                        .clickable { onBackClick() }
                        .align(Alignment.CenterStart),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_back),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_uth_todo_logo),
            contentDescription = stringResource(id = R.string.brand_smart_tasks),
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.brand_smart_tasks),
            color = Color(0xFF2196F3),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        content()
    }
}

// Màn hình 1: Nhập Email và nhận lại dữ liệu trả về từ màn hình 4
@Composable
fun ForgetPasswordScreen(
    returnedData: PasswordResetFlowData?,
    onBackToHome: () -> Unit,
    onNext: (String) -> Unit
) {
    val validator = remember { PasswordResetValidator() }
    var email by remember(returnedData) { mutableStateOf(returnedData?.email ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AuthFlowScaffold(
        showBackButton = true,
        onBackClick = onBackToHome,
        title = stringResource(id = R.string.forget_password_title),
        subtitle = stringResource(id = R.string.forget_password_subtitle)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.hint_your_email)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_email),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceMuted,
                unfocusedContainerColor = SurfaceMuted,
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = BorderLight
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage.orEmpty(),
                color = Color(0xFFDC2626),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                when (val result = validator.validateEmail(email)) {
                    is ValidationResult.Valid -> onNext(email.trim())
                    is ValidationResult.Invalid -> errorMessage = result.message
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.action_next),
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (returnedData != null) {
            Spacer(modifier = Modifier.height(28.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SoftBlueCard
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "email: ${returnedData.email}",
                        color = Color(0xFFDC2626),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "otp: ${returnedData.otp}",
                        color = Color(0xFFDC2626),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "pass: ${returnedData.password}",
                        color = Color(0xFFDC2626),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "(từ màn hình 4)",
                        color = Color(0xFFDC2626),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Màn hình 2: Nhập mã OTP 6 chữ số nhận email từ màn hình 1
@Composable
fun VerifyCodeScreen(
    email: String,
    onBack: () -> Unit,
    onNext: (String) -> Unit
) {
    val validator = remember { PasswordResetValidator() }
    var otp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AuthFlowScaffold(
        showBackButton = true,
        onBackClick = onBack,
        title = stringResource(id = R.string.verify_code_title),
        subtitle = "${stringResource(id = R.string.verify_code_subtitle)}\n($email)"
    ) {
        BasicTextField(
            value = otp,
            onValueChange = { value ->
                val digitsOnly = value.filter { it.isDigit() }.take(6)
                otp = digitsOnly
                errorMessage = null
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    repeat(6) { index ->
                        val charText = otp.getOrNull(index)?.toString() ?: ""
                        val isFocused = otp.length == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceMuted)
                                .border(
                                    width = if (isFocused) 1.5.dp else 1.dp,
                                    color = if (isFocused) Color(0xFF2196F3) else BorderLight,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = charText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage.orEmpty(),
                color = Color(0xFFDC2626),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when (val result = validator.validateOtp(otp)) {
                    is ValidationResult.Valid -> onNext(otp)
                    is ValidationResult.Invalid -> errorMessage = result.message
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.action_next),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// Màn hình 3: Tạo mật khẩu mới nhận email và otp từ màn hình trước
@Composable
fun CreateNewPasswordScreen(
    onBack: () -> Unit,
    onNext: (String) -> Unit
) {
    val validator = remember { PasswordResetValidator() }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AuthFlowScaffold(
        showBackButton = true,
        onBackClick = onBack,
        title = stringResource(id = R.string.create_password_title),
        subtitle = stringResource(id = R.string.create_password_subtitle)
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.hint_password)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceMuted,
                unfocusedContainerColor = SurfaceMuted,
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = BorderLight
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(id = R.string.hint_confirm_password)) },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_lock),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceMuted,
                unfocusedContainerColor = SurfaceMuted,
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = BorderLight
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage.orEmpty(),
                color = Color(0xFFDC2626),
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when (val result = validator.validatePasswords(password, confirmPassword)) {
                    is ValidationResult.Valid -> onNext(password)
                    is ValidationResult.Invalid -> errorMessage = result.message
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.action_next),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// Màn hình 4: Hiển thị tổng hợp email, otp, password và trả kết quả về màn hình 1
@Composable
fun ConfirmResetScreen(
    flowData: PasswordResetFlowData,
    onBack: () -> Unit,
    onSubmit: (PasswordResetFlowData) -> Unit
) {
    AuthFlowScaffold(
        showBackButton = true,
        onBackClick = onBack,
        title = stringResource(id = R.string.confirm_title),
        subtitle = stringResource(id = R.string.confirm_subtitle)
    ) {
        ConfirmReadOnlyField(iconRes = R.drawable.ic_person, text = flowData.email)
        Spacer(modifier = Modifier.height(12.dp))
        ConfirmReadOnlyField(iconRes = R.drawable.ic_email, text = flowData.otp)
        Spacer(modifier = Modifier.height(12.dp))
        ConfirmReadOnlyField(iconRes = R.drawable.ic_lock, text = flowData.maskedPassword())

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSubmit(flowData) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.action_submit),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun ConfirmReadOnlyField(iconRes: Int, text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceMuted,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = text,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }
    }
}
