package com.example.model

data class BatteryInfo(
    val level: Int = 78,
    val isCharging: Boolean = false,
    val pluggedState: PluggedState = PluggedState.UNPLUGGED,
    val health: HealthStatus = HealthStatus.GOOD,
    val temperatureCelsius: Float = 28.5f,
    val voltageMv: Int = 4150,
    val technology: String = "Li-ion",
    val capacityMah: Int = 4800,
    val currentNowMa: Int = -380, // Negative when discharging, positive when charging
    val powerSaveModeActive: Boolean = false,
    val estimatedMinutesRemaining: Int = 890, // ~14h 50m
    val estimatedChargeMinutesFull: Int = 65
) {
    val formattedVoltage: String
        get() = String.format("%.2f V", voltageMv / 1000f)

    val formattedTemperature: String
        get() = String.format("%.1f °C", temperatureCelsius)

    val isTemperatureHigh: Boolean
        get() = temperatureCelsius >= 40.0f

    val remainingTimeFormatted: String
        get() {
            val hours = estimatedMinutesRemaining / 60
            val minutes = estimatedMinutesRemaining % 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }

    val chargeTimeFormatted: String
        get() {
            val hours = estimatedChargeMinutesFull / 60
            val minutes = estimatedChargeMinutesFull % 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }
}

enum class PluggedState(val displayName: String) {
    UNPLUGGED("En Batería"),
    AC("Cargador CA (Enchufe)"),
    USB("Puerto USB"),
    WIRELESS("Carga Inalámbrica")
}

enum class HealthStatus(val label: String, val isGood: Boolean) {
    GOOD("Excelente", true),
    OVERHEAT("Sobrecalentada", false),
    DEAD("Agotada", false),
    OVER_VOLTAGE("Sobretensión", false),
    COLD("Muy fría", false),
    UNKNOWN("Normal", true)
}
