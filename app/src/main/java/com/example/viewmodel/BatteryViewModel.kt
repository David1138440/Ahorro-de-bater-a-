package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BatteryMonitor
import com.example.model.BatteryHistoryPoint
import com.example.model.BatteryInfo
import com.example.model.ChargingTip
import com.example.model.DrainCategory
import com.example.model.OptimizationActionType
import com.example.model.OptimizationCategory
import com.example.model.OptimizationItem
import com.example.model.PowerProfileConfig
import com.example.model.PowerProfileType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val batteryMonitor = BatteryMonitor(application)

    private val _batteryInfo = MutableStateFlow(BatteryInfo())
    val batteryInfo: StateFlow<BatteryInfo> = _batteryInfo.asStateFlow()

    private val _currentProfile = MutableStateFlow(PowerProfileConfig(type = PowerProfileType.SMART))
    val currentProfile: StateFlow<PowerProfileConfig> = _currentProfile.asStateFlow()

    private val _optimizationItems = MutableStateFlow(createInitialOptimizationItems())
    val optimizationItems: StateFlow<List<OptimizationItem>> = _optimizationItems.asStateFlow()

    private val _isOptimizing = MutableStateFlow(false)
    val isOptimizing: StateFlow<Boolean> = _isOptimizing.asStateFlow()

    private val _optimizingStepText = MutableStateFlow("")
    val optimizingStepText: StateFlow<String> = _optimizingStepText.asStateFlow()

    private val _lastOptimizationGainMinutes = MutableStateFlow(165)
    val lastOptimizationGainMinutes: StateFlow<Int> = _lastOptimizationGainMinutes.asStateFlow()

    private val _showOptimizationSuccessSheet = MutableStateFlow(false)
    val showOptimizationSuccessSheet: StateFlow<Boolean> = _showOptimizationSuccessSheet.asStateFlow()

    private val _chargeAlert80 = MutableStateFlow(true)
    val chargeAlert80: StateFlow<Boolean> = _chargeAlert80.asStateFlow()

    private val _chargeAlert100 = MutableStateFlow(false)
    val chargeAlert100: StateFlow<Boolean> = _chargeAlert100.asStateFlow()

    private val _amoledBlackSaverActive = MutableStateFlow(false)
    val amoledBlackSaverActive: StateFlow<Boolean> = _amoledBlackSaverActive.asStateFlow()

    private val _batteryHistory = MutableStateFlow(createSampleBatteryHistory())
    val batteryHistory: StateFlow<List<BatteryHistoryPoint>> = _batteryHistory.asStateFlow()

    val drainCategories: List<DrainCategory> = listOf(
        DrainCategory("Pantalla y Brillo", 42, "ic_screen", "El consumo de la pantalla es el principal drenaje. Reducir brillo al 45% y usar tema oscuro OLED ahorra hasta 35% de batería."),
        DrainCategory("Aplicaciones en Segundo Plano", 28, "ic_apps", "Redes sociales y mensajería sincronizan en tiempo real. Restringe la actividad en reposo en Ajustes."),
        DrainCategory("Sistema Android & CPU", 14, "ic_cpu", "Procesos del sistema operativo y tasa de refresco a 120Hz. Activar 60Hz estándar ahorra energía."),
        DrainCategory("Conexión Celular 5G / 4G", 10, "ic_signal", "En zonas con poca cobertura de red móvil, el módem celular amplifica su potencia gastando el triple."),
        DrainCategory("Wi-Fi & Bluetooth", 6, "ic_wifi", "La búsqueda continua de redes Wi-Fi cercanas gasta batería incluso si estás desconectado.")
    )

    val chargingTips: List<ChargingTip> = listOf(
        ChargingTip(
            id = "tip_80_rule",
            title = "Regla 20% - 80% (Regla de Oro)",
            category = "Salud Celular",
            summary = "Evita descargar por debajo del 20% y no cargues constantemente al 100%.",
            detail = "Las celdas de litio sufren mayor estrés químico en los extremos de voltaje (>4.2V y <3.4V). Mantener la carga entre 20% y 80% duplica la vida útil de la batería de 500 a más de 1200 ciclos completos.",
            impactLevel = "CRÍTICO"
        ),
        ChargingTip(
            id = "tip_heat",
            title = "Cuidado con la Temperatura",
            category = "Prevención de Daño",
            summary = "No uses el móvil para juegos pesados mientras está cargando.",
            detail = "El calor es el enemigo número uno de las baterías. Cargas a más de 40°C aceleran la degradación de la capacidad. Si notas el móvil caliente, retira la funda durante la carga rápida.",
            impactLevel = "CRÍTICO"
        ),
        ChargingTip(
            id = "tip_amoled",
            title = "Tema Negro Puro en Pantallas OLED",
            category = "Ahorro de Pantalla",
            summary = "En pantallas OLED/AMOLED, los píxeles negros están 100% apagados.",
            detail = "Usar fondo negro puro (#000000) en el sistema y tus aplicaciones reduce el consumo de la pantalla entre un 30% y un 50% en comparación con fondos blancos o claros.",
            impactLevel = "ALTO"
        ),
        ChargingTip(
            id = "tip_refresh_rate",
            title = "Tasa de Refresco de Pantalla (60Hz vs 120Hz)",
            category = "Optimización",
            summary = "Los 120Hz/90Hz consumen hasta un 25% más de batería activa.",
            detail = "Si vas a estar un día largo sin cargador, cambia temporalmente la pantalla a 60Hz estándar en Ajustes de Pantalla. Notarás una extensión inmediata de 1.5 a 2 horas.",
            impactLevel = "ALTO"
        ),
        ChargingTip(
            id = "tip_5g_lte",
            title = "Uso de Redes 5G en Movimiento",
            category = "Conectividad",
            summary = "El cambio continuo entre 5G y 4G drena la batería con rapidez.",
            detail = "El módem 5G consume sustancialmente más energía cuando la señal es irregular. Si estás viajando o la cobertura es baja, fijar la red a 4G/LTE ahorra un 15% de energía diaria.",
            impactLevel = "MEDIO"
        ),
        ChargingTip(
            id = "tip_haptics",
            title = "Vibración del Teclado y Táctil",
            category = "Hardware",
            summary = "El motor háptico gasta más energía que los sonidos breves.",
            detail = "Si escribes muchos mensajes a lo largo del día, desactivar la respuesta háptica al escribir en el teclado suma entre 20 y 40 minutos extra de uso.",
            impactLevel = "MEDIO"
        )
    )

    init {
        viewModelScope.launch {
            batteryMonitor.getBatteryFlow().collect { info ->
                _batteryInfo.value = info
            }
        }
    }

    private fun createInitialOptimizationItems(): List<OptimizationItem> = listOf(
        OptimizationItem(
            id = "screen_brightness",
            title = "Brillo Adaptativo & Atenuación",
            description = "Ajustar brillo automático al nivel óptimo para ahorrar hasta 45m.",
            category = OptimizationCategory.DISPLAY,
            estimatedMinutesGain = 45,
            isOptimized = false,
            actionType = OptimizationActionType.INTERNAL_TOGGLE
        ),
        OptimizationItem(
            id = "screen_timeout",
            title = "Tiempo de Espera de Pantalla a 30s",
            description = "Apagar la pantalla más rápido tras inactividad evita drenaje accidental.",
            category = OptimizationCategory.DISPLAY,
            estimatedMinutesGain = 35,
            isOptimized = false,
            actionType = OptimizationActionType.OPEN_DISPLAY_SETTINGS
        ),
        OptimizationItem(
            id = "background_apps",
            title = "Limitar Procesos en Reposo",
            description = "Suspender procesos no prioritarios que despiertan la CPU en espera.",
            category = OptimizationCategory.SYSTEM,
            estimatedMinutesGain = 60,
            isOptimized = false,
            actionType = OptimizationActionType.OPEN_APP_OPTIMIZATION_SETTINGS
        ),
        OptimizationItem(
            id = "haptic_feedback",
            title = "Desactivar Vibración Háptica",
            description = "Silenciar el motor háptico en toques secundarios y pulsaciones de teclas.",
            category = OptimizationCategory.HAPTICS,
            estimatedMinutesGain = 25,
            isOptimized = false,
            actionType = OptimizationActionType.INTERNAL_TOGGLE
        ),
        OptimizationItem(
            id = "wifi_scan",
            title = "Búsqueda Continua de Wi-Fi",
            description = "Evitar que los servicios de ubicación rastreen redes Wi-Fi en reposo.",
            category = OptimizationCategory.CONNECTIVITY,
            estimatedMinutesGain = 30,
            isOptimized = false,
            actionType = OptimizationActionType.INTERNAL_TOGGLE
        ),
        OptimizationItem(
            id = "system_battery_saver",
            title = "Modo Ahorro Nativo de Android",
            description = "Activa la optimización profunda del sistema operativo Android.",
            category = OptimizationCategory.SYSTEM,
            estimatedMinutesGain = 90,
            isOptimized = false,
            actionType = OptimizationActionType.OPEN_BATTERY_SAVER_SETTINGS
        )
    )

    fun calculateHealthScore(): Int {
        val items = _optimizationItems.value
        val optimizedCount = items.count { it.isOptimized }
        val totalCount = items.size.coerceAtLeast(1)
        val ratio = optimizedCount.toFloat() / totalCount.toFloat()
        // Score ranges from 60 to 99
        return (60 + (ratio * 39)).toInt()
    }

    fun optimizeAll() {
        if (_isOptimizing.value) return
        viewModelScope.launch {
            _isOptimizing.value = true
            _optimizingStepText.value = "Analizando consumo de pantalla..."
            vibrateShort()
            delay(400)

            _optimizingStepText.value = "Cerrando procesos en segundo plano..."
            delay(450)

            _optimizingStepText.value = "Ajustando perfiles de conectividad..."
            delay(450)

            _optimizingStepText.value = "Activando ahorro inteligente..."
            delay(400)

            var totalGained = 0
            val updated = _optimizationItems.value.map { item ->
                if (!item.isOptimized) {
                    totalGained += item.estimatedMinutesGain
                }
                item.copy(isOptimized = true)
            }
            _optimizationItems.value = updated
            _lastOptimizationGainMinutes.value = if (totalGained > 0) totalGained else 195
            _currentProfile.value = _currentProfile.value.copy(type = PowerProfileType.SMART)

            // Update remaining time estimate
            val currentInfo = _batteryInfo.value
            _batteryInfo.value = currentInfo.copy(
                estimatedMinutesRemaining = currentInfo.estimatedMinutesRemaining + _lastOptimizationGainMinutes.value
            )

            vibrateSuccess()
            _isOptimizing.value = false
            _showOptimizationSuccessSheet.value = true
        }
    }

    fun toggleOptimizationItem(id: String) {
        val updated = _optimizationItems.value.map {
            if (it.id == id) it.copy(isOptimized = !it.isOptimized) else it
        }
        _optimizationItems.value = updated
        vibrateShort()
    }

    fun selectProfile(type: PowerProfileType) {
        _currentProfile.value = _currentProfile.value.copy(type = type)
        vibrateShort()
        val context = getApplication<Application>()
        Toast.makeText(context, "Perfil activado: ${type.title}", Toast.LENGTH_SHORT).show()
    }

    fun toggleChargeAlert80() {
        _chargeAlert80.value = !_chargeAlert80.value
        vibrateShort()
    }

    fun toggleChargeAlert100() {
        _chargeAlert100.value = !_chargeAlert100.value
        vibrateShort()
    }

    fun setAmoledBlackSaver(active: Boolean) {
        _amoledBlackSaverActive.value = active
        vibrateShort()
    }

    fun dismissSuccessSheet() {
        _showOptimizationSuccessSheet.value = false
    }

    fun openSystemAction(context: Context, actionType: OptimizationActionType) {
        try {
            val intent = when (actionType) {
                OptimizationActionType.OPEN_BATTERY_SAVER_SETTINGS -> {
                    Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
                }
                OptimizationActionType.OPEN_DISPLAY_SETTINGS -> {
                    Intent(Settings.ACTION_DISPLAY_SETTINGS)
                }
                OptimizationActionType.OPEN_APP_OPTIMIZATION_SETTINGS -> {
                    Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                }
                OptimizationActionType.OPEN_LOCATION_SETTINGS -> {
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                }
                else -> {
                    Intent(Settings.ACTION_SETTINGS)
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo abrir Ajustes del sistema", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun vibrateShort() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                v?.vibrate(35)
            }
        } catch (_: Exception) {
        }
    }

    private fun vibrateSuccess() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                val pattern = longArrayOf(0, 50, 70, 100)
                val amplitudes = intArrayOf(0, 150, 0, 255)
                v?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
            }
        } catch (_: Exception) {
        }
    }

    private fun createSampleBatteryHistory(): List<BatteryHistoryPoint> = listOf(
        BatteryHistoryPoint("08:00", 98),
        BatteryHistoryPoint("10:00", 91),
        BatteryHistoryPoint("12:00", 84),
        BatteryHistoryPoint("14:00", 79),
        BatteryHistoryPoint("16:00", 72),
        BatteryHistoryPoint("18:00", 67),
        BatteryHistoryPoint("Ahora", 78)
    )
}
