package com.uth.uthtodo.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

private enum class TimePickerMode(val label: String) {
    WHEEL("Lướt chọn"),
    INPUT("Nhập giờ")
}

// Hộp thoại chọn giờ hỗ trợ cả lướt bánh xe và nhập trực tiếp giờ:phút
@Composable
fun FlexibleTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var mode by remember { mutableStateOf(TimePickerMode.WHEEL) }
    var selectedHour by remember { mutableIntStateOf(initialHour.coerceIn(0, 23)) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute.coerceIn(0, 59)) }

    var hourText by remember { mutableStateOf("%02d".format(selectedHour)) }
    var minuteText by remember { mutableStateOf("%02d".format(selectedMinute)) }
    var inputError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Chọn giờ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                // Thanh chuyển nhanh giữa Lướt chọn và Nhập giờ
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TimePickerMode.entries.forEach { item ->
                            val active = mode == item
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (item == TimePickerMode.INPUT) {
                                            hourText = "%02d".format(selectedHour)
                                            minuteText = "%02d".format(selectedMinute)
                                            inputError = null
                                        }
                                        mode = item
                                    },
                                shape = RoundedCornerShape(9.dp),
                                color = if (active) colorScheme.surface else Color.Transparent
                            ) {
                                Text(
                                    text = item.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (active) colorScheme.onSurface else colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                if (mode == TimePickerMode.WHEEL) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimeWheelColumn(
                            label = "Giờ",
                            range = 0..23,
                            selectedValue = selectedHour,
                            onValueSelected = { selectedHour = it }
                        )
                        Text(
                            text = ":",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 18.dp)
                        )
                        TimeWheelColumn(
                            label = "Phút",
                            range = 0..59,
                            selectedValue = selectedMinute,
                            onValueSelected = { selectedMinute = it }
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                value = hourText,
                                onValueChange = { raw ->
                                    val digits = raw.filter { it.isDigit() }.take(2)
                                    hourText = digits
                                    inputError = null
                                    digits.toIntOrNull()?.takeIf { it in 0..23 }?.let {
                                        selectedHour = it
                                    }
                                },
                                modifier = Modifier.width(88.dp),
                                singleLine = true,
                                label = { Text("Giờ", fontSize = 12.sp) },
                                textStyle = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = colorScheme.onSurface
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = colorScheme.surfaceVariant,
                                    unfocusedContainerColor = colorScheme.surfaceVariant,
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.outlineVariant
                                )
                            )

                            Text(
                                text = ":",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            OutlinedTextField(
                                value = minuteText,
                                onValueChange = { raw ->
                                    val digits = raw.filter { it.isDigit() }.take(2)
                                    minuteText = digits
                                    inputError = null
                                    digits.toIntOrNull()?.takeIf { it in 0..59 }?.let {
                                        selectedMinute = it
                                    }
                                },
                                modifier = Modifier.width(88.dp),
                                singleLine = true,
                                label = { Text("Phút", fontSize = 12.sp) },
                                textStyle = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = colorScheme.onSurface
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = colorScheme.surfaceVariant,
                                    unfocusedContainerColor = colorScheme.surfaceVariant,
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.outlineVariant
                                )
                            )
                        }

                        if (inputError != null) {
                            Text(
                                text = inputError.orEmpty(),
                                color = Color(0xFFEF4444),
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = "Nhập giờ từ 00–23 và phút từ 00–59",
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Hủy",
                            color = colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (mode == TimePickerMode.INPUT) {
                                val parsedHour = hourText.toIntOrNull()
                                val parsedMinute = minuteText.toIntOrNull()
                                if (parsedHour == null || parsedHour !in 0..23 || parsedMinute == null || parsedMinute !in 0..59) {
                                    inputError = "Giờ hợp lệ từ 00–23, phút từ 00–59."
                                    return@Button
                                }
                                onConfirm(parsedHour, parsedMinute)
                            } else {
                                onConfirm(selectedHour, selectedMinute)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Xong", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimeWheelColumn(
    label: String,
    range: IntRange,
    selectedValue: Int,
    onValueSelected: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val values = remember(range) { range.toList() }
    val itemHeight = 44.dp
    val initialIndex = (selectedValue - range.first).coerceIn(0, values.lastIndex)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val scope = rememberCoroutineScope()

    val centeredIndex by remember {
        derivedStateOf {
            val offset = listState.firstVisibleItemScrollOffset
            val base = listState.firstVisibleItemIndex
            (if (offset > 55) base + 1 else base).coerceIn(0, values.lastIndex)
        }
    }

    LaunchedEffect(centeredIndex) {
        onValueSelected(values[centeredIndex])
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .width(84.dp)
                .height(itemHeight * 3),
            contentAlignment = Alignment.Center
        ) {
            // Dải nổi bật ở giữa đánh dấu mốc giờ/phút đang chọn
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.primaryContainer)
            )

            LazyColumn(
                state = listState,
                flingBehavior = flingBehavior,
                contentPadding = PaddingValues(vertical = itemHeight),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(values.size) { idx ->
                    val value = values[idx]
                    val isSelected = idx == centeredIndex
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .clickable {
                                onValueSelected(value)
                                scope.launch { listState.animateScrollToItem(idx) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "%02d".format(value),
                            fontSize = if (isSelected) 22.sp else 17.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) colorScheme.onPrimaryContainer else colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}
