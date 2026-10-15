package com.example.model

data class OptimizationItem(
    val id: String,
    val title: String,
    val description: String,
    val category: OptimizationCategory,
    val estimatedMinutesGain: Int,
    val isOptimized: Boolean,
    val actionType: OptimizationActionType = OptimizationActionType.INTERNAL_TOGGLE
)

enum class OptimizationCategory(val label: String) {
    DISPLAY("Pantalla & Brillo"),
    SYSTEM("Sistema & Fondo"),
    CONNECTIVITY("Conectividad & Redes"),
    HAPTICS("Vibración & Sensores")
}

enum class OptimizationActionType {
    INTERNAL_TOGGLE,
    OPEN_BATTERY_SAVER_SETTINGS,
    OPEN_DISPLAY_SETTINGS,
    OPEN_APP_OPTIMIZATION_SETTINGS,
    OPEN_LOCATION_SETTINGS
}

data class DiagnosticResult(
    val healthScore: Int, // 0 - 100
    val issuesFound: Int,
    val totalEstimatedMinutesSaved: Int,
    val statusText: String
)
