package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CafeLogo
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QrGeneratorDialog(
    cafeId: String = "cafe_main",
    onDismiss: () -> Unit
) {
    var selectedTable by remember { mutableIntStateOf(5) }
    val qrLink = "https://cafe-menu.app/?cafeId=$cafeId&table=$selectedTable"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCode2, contentDescription = null, tint = CyanPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "رموز الـ QR لطاولات الكفتريا",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "اختر رقم الطاولة لعرض رمز الـ QR المخصص لطلب الزبائن:",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Table Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(1, 3, 5, 7, 9).forEach { tableNum ->
                        FilterChip(
                            selected = selectedTable == tableNum,
                            onClick = { selectedTable = tableNum },
                            label = { Text("طاولة $tableNum") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanPrimary,
                                selectedLabelColor = Color(0xFF031619),
                                containerColor = Color(0xFF061A1E),
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simulated Beautiful QR Frame with Cafe Logo in Center
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(3.dp, CyanPrimary, RoundedCornerShape(20.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            tint = Color(0xFF08262C),
                            modifier = Modifier.size(110.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "CAFE - طاولة $selectedTable",
                            color = Color(0xFF08262C),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "رابط المسح المباشر: $qrLink",
                    color = TextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color(0xFF031619))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("طباعة / إغلاق", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        }
    )
}
