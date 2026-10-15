package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BatteryHistoryPoint
import com.example.model.BatteryInfo
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TealSecondary

@Composable
fun HealthCareScreen(
    batteryInfo: BatteryInfo,
    chargeAlert80: Boolean,
    chargeAlert100: Boolean,
    batteryHistory: List<BatteryHistoryPoint>,
    onToggleAlert80: () -> Unit,
    onToggleAlert100: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Salud y Cuidado de la Batería",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Extiende la vida útil de las celdas químicas de litio durante años con hábitos de carga saludables.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Safe 20-80% Charging Range Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.18f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Zona Segura de Carga (20% - 80%)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Duplica la vida de la batería de 2 a 4+ años",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Safe zone graphic
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("0% (Estrés alto)", fontSize = 10.sp, color = CoralDanger)
                            Text("20% - 80% (Zona Óptima)", fontSize = 10.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                            Text("100% (Sobrecarga)", fontSize = 10.sp, color = AmberWarning)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                // 0-20% Danger zone
                                Box(
                                    modifier = Modifier
                                        .weight(0.2f)
                                        .fillMaxSize()
                                        .background(CoralDanger.copy(alpha = 0.6f))
                                )
                                // 20-80% Safe sweet spot
                                Box(
                                    modifier = Modifier
                                        .weight(0.6f)
                                        .fillMaxSize()
                                        .background(EmeraldPrimary)
                                )
                                // 80-100% High voltage stress zone
                                Box(
                                    modifier = Modifier
                                        .weight(0.2f)
                                        .fillMaxSize()
                                        .background(AmberWarning.copy(alpha = 0.7f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val isInSweetSpot = batteryInfo.level in 20..80
                        Text(
                            text = if (isInSweetSpot) "✓ Tu batería (${batteryInfo.level}%) se encuentra en la zona química óptima."
                            else if (batteryInfo.level > 80) "⚠ Estás sobre el 80%. Para máxima durabilidad, desconéctalo."
                            else "⚠ Nivel menor al 20%. Conecta el cargador para evitar estrés de descarga profunda.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isInSweetSpot) EmeraldPrimary else AmberWarning,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }

        // Smart Charge Alarms
        item {
            Text(
                text = "Alarmas de Carga Inteligente",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 80% Alarm
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("alert_80_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.18f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Aviso al alcanzar el 80%",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Emite sonido/vibración para desenchufar a tiempo.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Switch(
                        checked = chargeAlert80,
                        onCheckedChange = { onToggleAlert80() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("switch_alert_80")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 100% Alarm
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("alert_100_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(TealSecondary.copy(alpha = 0.18f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = TealSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Alerta de Carga Completa (100%)",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Evita dejar el teléfono conectado horas innecesarias.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Switch(
                        checked = chargeAlert100,
                        onCheckedChange = { onToggleAlert100() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = TealSecondary
                        ),
                        modifier = Modifier.testTag("switch_alert_100")
                    )
                }
            }
        }

        // Real-Time Temperature Monitor
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeviceThermostat,
                                contentDescription = null,
                                tint = if (batteryInfo.temperatureCelsius < 36f) EmeraldPrimary else AmberWarning,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Temperatura de la Batería",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Text(
                            text = batteryInfo.formattedTemperature,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (batteryInfo.temperatureCelsius < 36f) EmeraldPrimary else AmberWarning
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val tempRatio = ((batteryInfo.temperatureCelsius - 15f) / 35f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { tempRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (batteryInfo.temperatureCelsius < 36f) EmeraldPrimary else AmberWarning,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (batteryInfo.temperatureCelsius < 35f) "Temperatura ideal. Las celdas operan con máxima eficiencia química."
                        else "Temperatura algo elevada. Evita tareas pesadas o juegos mientras carga.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Battery Discharge History Graph
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Historial de Nivel de Batería",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Curva de descarga durante el día:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BatteryHistoryChart(
                        points = batteryHistory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BatteryHistoryChart(
    points: List<BatteryHistoryPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val emerald = EmeraldPrimary
    val teal = TealSecondary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = modifier) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            val width = size.width
            val height = size.height
            val stepX = width / (points.size - 1).coerceAtLeast(1)

            val path = Path()
            val fillPath = Path()

            points.forEachIndexed { index, point ->
                val x = index * stepX
                // Level 0 to 100 mapped to bottom to top (with 10% padding)
                val y = height - (point.level / 100f * (height * 0.8f) + (height * 0.1f))

                if (index == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, height)
                    fillPath.lineTo(x, y)
                } else {
                    val prevX = (index - 1) * stepX
                    val prevLevel = points[index - 1].level
                    val prevY = height - (prevLevel / 100f * (height * 0.8f) + (height * 0.1f))

                    // Cubic bezier smooth curve
                    val cx1 = prevX + (stepX / 2f)
                    val cy1 = prevY
                    val cx2 = prevX + (stepX / 2f)
                    val cy2 = y
                    path.cubicTo(cx1, cy1, cx2, cy2, x, y)
                    fillPath.cubicTo(cx1, cy1, cx2, cy2, x, y)
                }

                if (index == points.size - 1) {
                    fillPath.lineTo(x, height)
                    fillPath.close()
                }

                // Draw point circle
                drawCircle(
                    color = emerald,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }

            // Fill area gradient
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        emerald.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = height
                )
            )

            // Draw line stroke
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(listOf(teal, emerald)),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Time labels row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { point ->
                Text(
                    text = point.timeLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
