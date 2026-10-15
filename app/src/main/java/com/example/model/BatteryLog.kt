package com.example.model

data class BatteryHistoryPoint(
    val timeLabel: String,
    val level: Int
)

data class DrainCategory(
    val name: String,
    val percentage: Int,
    val iconName: String,
    val tips: String
)

data class ChargingTip(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val detail: String,
    val impactLevel: String // "ALTO", "MEDIO", "CRÍTICO"
)
