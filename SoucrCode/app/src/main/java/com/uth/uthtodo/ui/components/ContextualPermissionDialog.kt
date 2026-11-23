package com.uth.uthtodo.ui.components

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.uth.uthtodo.R
import com.uth.uthtodo.model.AppPermissionType
import com.uth.uthtodo.ui.theme.BorderLight
import com.uth.uthtodo.ui.theme.PrimaryBlue
import com.uth.uthtodo.ui.theme.SoftBlueCard
import com.uth.uthtodo.ui.theme.TextPrimary
import com.uth.uthtodo.ui.theme.TextSecondary

// Kiểm tra quyền hệ thống đã được cấp hay chưa
fun isAppPermissionGranted(context: Context, type: AppPermissionType): Boolean {
    if (!type.requiresRuntimePrompt()) return true
    return ContextCompat.checkSelfPermission(
        context,
        type.runtimePermission()
    ) == PackageManager.PERMISSION_GRANTED
}

// Lấy biểu tượng vector tương ứng với từng loại quyền của UTH Todo
fun permissionIconRes(type: AppPermissionType): Int = when (type) {
    AppPermissionType.NOTIFICATION -> R.drawable.ic_bell
    AppPermissionType.CAMERA -> R.drawable.ic_camera
    AppPermissionType.MEDIA_IMAGES -> R.drawable.ic_gallery
}

// Hộp thoại giải thích ngữ cảnh xin quyền đồng bộ giao diện UTH Todo
@Composable
fun ContextualPermissionDialog(
    permissionType: AppPermissionType,
    onAllow: () -> Unit,
    onSkip: () -> Unit
) {
    Dialog(onDismissRequest = onSkip) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(SoftBlueCard),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = permissionIconRes(permissionType)),
                        contentDescription = permissionType.title,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = "Cấp quyền ${permissionType.title}",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = permissionType.description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onAllow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(
                        text = permissionType.actionLabel,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Text(
                        text = "Để sau",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
