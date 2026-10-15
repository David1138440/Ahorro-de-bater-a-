package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BatteryInfo
import com.example.ui.theme.EmeraldPrimary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AmoledBlackScreen(
    batteryInfo: BatteryInfo,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Intercept back navigation to exit cleanly
    BackHandler {
        onExit()
    }

    var currentTime by remember {
        mutableStateOf(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
    }
    var currentDate by remember {
        mutableStateOf(SimpleDateFormat("EEEE, d MMMM", Locale("es", "ES")).format(Date()))
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(15000)
            currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            currentDate = SimpleDateFormat("EEEE, d MMMM", Locale("es", "ES")).format(Date())
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("amoled_black_screen")
    ) {
        // Exit button top-end
        IconButton(
            onClick = onExit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                .testTag("exit_amoled_button")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Salir de Modo OLED",
                tint = Color.LightGray
            )
        }

        // Center minimal clock & battery
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = currentTime,
                fontSize = 72.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                color = Color.White.copy(alpha = 0.85f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = currentDate.replaceFirstChar { it.uppercase() },
                fontSize = 14.sp,
                color = Color.Gray.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(36.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0F1E16))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (batteryInfo.isCharging) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Cargando",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = "${batteryInfo.level}% Batería",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${batteryInfo.remainingTimeFormatted} de autonomía restante",
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedButton(
                onClick = onExit,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.LightGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Toca para Salir del Modo OLED",
                    fontSize = 12.sp
                )
            }
        }

        // Bottom battery eco note
        Text(
            text = "Modo Pantalla Negra OLED • Consumo de panel: ~0.05W",
            fontSize = 11.sp,
            color = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
