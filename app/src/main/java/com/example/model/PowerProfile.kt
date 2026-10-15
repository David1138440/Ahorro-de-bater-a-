package com.example.model

enum class PowerProfileType(
    val title: String,
    val subtitle: String,
    val estimatedBonusMinutes: Int,
    val description: String,
    val recommendedFor: String
) {
    NORMAL(
        title = "Equilibrado",
        subtitle = "Rendimiento completo estándar",
        estimatedBonusMinutes = 0,
        description = "Mantiene todas las funciones y animaciones del sistema activas sin restricciones de consumo.",
        recommendedFor = "Uso habitual con carga disponible"
    ),
    SMART(
        title = "Ahorro Inteligente",
        subtitle = "+2h 45m de duración estimada",
        estimatedBonusMinutes = 165,
        description = "Limita la sincronización en segundo plano, reduce brillo al 40%, apaga vibraciones no esenciales y optimiza la tasa de refresco.",
        recommendedFor = "Recomendado para el día a día laboral"
    ),
    ULTRA(
        title = "Ultra Ahorro Extremo",
        subtitle = "+12h a 18h de duración",
        estimatedBonusMinutes = 750,
        description = "Interfaz monocromática de negro puro OLED, suspende tareas en segundo plano, deshabilita animaciones y restringe conectividad redundante.",
        recommendedFor = "Emergencias o cuando queda menos del 20%"
    ),
    NIGHT(
        title = "Modo Noche / Reposo",
        subtitle = "Consumo standby mínimo (<1%/noche)",
        estimatedBonusMinutes = 240,
        description = "Pausa la búsqueda de redes, apaga vibraciones y notificaciones no prioritarias mientras descansas.",
        recommendedFor = "Para la noche sin cargador cerca"
    ),
    CUSTOM(
        title = "Personalizado",
        subtitle = "Configuración a tu medida",
        estimatedBonusMinutes = 90,
        description = "Ajusta individualmente las reglas de brillo, conexiones, vibración y temas según tus preferencias.",
        recommendedFor = "Usuarios avanzados"
    )
}

data class PowerProfileConfig(
    val type: PowerProfileType = PowerProfileType.SMART,
    val dimScreen: Boolean = true,
    val shortScreenTimeout: Boolean = true,
    val disableHaptics: Boolean = true,
    val restrictBackgroundSync: Boolean = true,
    val amoledBlackTheme: Boolean = false,
    val aggressiveBatterySaver: Boolean = false
)
