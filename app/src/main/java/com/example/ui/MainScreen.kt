package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.OptimizationSuccessDialog
import com.example.ui.components.OptimizingProgressDialog
import com.example.ui.screens.AmoledBlackScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DiagnosticScreen
import com.example.ui.screens.HealthCareScreen
import com.example.ui.screens.ProfilesScreen
import com.example.ui.screens.TipsAndAmoledScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TealSecondary
import com.example.viewmodel.BatteryViewModel

enum class MainTab(val label: String, val icon: ImageVector) {
    DASHBOARD("Inicio", Icons.Default.BatteryChargingFull),
    PROFILES("Modos", Icons.Default.FlashOn),
    DIAGNOSTIC("Drenaje", Icons.Default.Speed),
    HEALTH("Salud", Icons.Default.Favorite),
    TIPS("Consejos", Icons.Default.Lightbulb)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BatteryViewModel,
    modifier: Modifier = Modifier
) {
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val currentProfile by viewModel.currentProfile.collectAsState()
    val optimizationItems by viewModel.optimizationItems.collectAsState()
    val isOptimizing by viewModel.isOptimizing.collectAsState()
    val stepText by viewModel.optimizingStepText.collectAsState()
    val lastGainMinutes by viewModel.lastOptimizationGainMinutes.collectAsState()
    val showSuccessSheet by viewModel.showOptimizationSuccessSheet.collectAsState()
    val chargeAlert80 by viewModel.chargeAlert80.collectAsState()
    val chargeAlert100 by viewModel.chargeAlert100.collectAsState()
    val amoledSaverActive by viewModel.amoledBlackSaverActive.collectAsState()
    val batteryHistory by viewModel.batteryHistory.collectAsState()

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    // If AMOLED pure black saver is launched, show full-screen overlay
    if (amoledSaverActive) {
        AmoledBlackScreen(
            batteryInfo = batteryInfo,
            onExit = { viewModel.setAmoledBlackSaver(false) }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ahorro de Batería",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                actions = {
                    // Quick AMOLED tool button
                    IconButton(
                        onClick = { viewModel.setAmoledBlackSaver(true) },
                        modifier = Modifier.testTag("top_bar_amoled_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = "Modo Pantalla Negra OLED",
                            tint = EmeraldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                MainTab.values().forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = EmeraldPrimary,
                            indicatorColor = EmeraldPrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> DashboardScreen(
                    batteryInfo = batteryInfo,
                    onOptimizeClick = { viewModel.optimizeAll() },
                    onOpenSystemAction = { actionType ->
                        viewModel.openSystemAction(viewModel.getApplication(), actionType)
                    },
                    onLaunchAmoledMode = { viewModel.setAmoledBlackSaver(true) }
                )
                1 -> ProfilesScreen(
                    currentConfig = currentProfile,
                    onSelectProfile = { profileType ->
                        viewModel.selectProfile(profileType)
                    }
                )
                2 -> DiagnosticScreen(
                    score = viewModel.calculateHealthScore(),
                    optimizationItems = optimizationItems,
                    drainCategories = viewModel.drainCategories,
                    onToggleItem = { id -> viewModel.toggleOptimizationItem(id) },
                    onOpenSystemAction = { actionType ->
                        viewModel.openSystemAction(viewModel.getApplication(), actionType)
                    },
                    onOptimizeAllClick = { viewModel.optimizeAll() }
                )
                3 -> HealthCareScreen(
                    batteryInfo = batteryInfo,
                    chargeAlert80 = chargeAlert80,
                    chargeAlert100 = chargeAlert100,
                    batteryHistory = batteryHistory,
                    onToggleAlert80 = { viewModel.toggleChargeAlert80() },
                    onToggleAlert100 = { viewModel.toggleChargeAlert100() }
                )
                4 -> TipsAndAmoledScreen(
                    tips = viewModel.chargingTips,
                    onLaunchAmoledMode = { viewModel.setAmoledBlackSaver(true) }
                )
            }

            // Progress Dialog during optimization
            if (isOptimizing) {
                OptimizingProgressDialog(stepText = stepText)
            }

            // Success Dialog
            if (showSuccessSheet) {
                OptimizationSuccessDialog(
                    minutesGained = lastGainMinutes,
                    onDismiss = { viewModel.dismissSuccessSheet() }
                )
            }
        }
    }
}
