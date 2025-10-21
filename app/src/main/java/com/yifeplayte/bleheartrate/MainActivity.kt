package com.yifeplayte.bleheartrate

import android.Manifest
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.yifeplayte.bleheartrate.model.DisplayMode
import com.yifeplayte.bleheartrate.service.FloatingWindowService
import com.yifeplayte.bleheartrate.ui.MainScreen
import com.yifeplayte.bleheartrate.ui.MainViewModel
import com.yifeplayte.bleheartrate.ui.theme.BleHeartrateTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    
    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            viewModel.loadPairedDevices()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        viewModel.bindService(this)
        
        setContent {
            BleHeartrateTheme {
                val configuration = LocalConfiguration.current
                val isInPipMode = remember(configuration) {
                    isInPictureInPictureMode
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isServiceRunning by viewModel.isServiceRunning.collectAsState()
                    val heartRateData by viewModel.heartRateData.collectAsState()
                    val connectionState by viewModel.connectionState.collectAsState()
                    val displayMode by viewModel.displayMode.collectAsState()
                    val autoReconnect by viewModel.autoReconnect.collectAsState()
                    val pairedDevices by viewModel.pairedDevices.collectAsState()
                    val selectedDevice by viewModel.selectedDevice.collectAsState()

                    // Update floating window when heart rate changes
                    LaunchedEffect(heartRateData, displayMode, isServiceRunning) {
                        if (isServiceRunning && displayMode == DisplayMode.FLOATING_WINDOW) {
                            updateFloatingWindow(heartRateData.heartRate)
                        }
                    }

                    if (isInPipMode) {
                        // Simple PiP UI showing only heart rate
                        com.yifeplayte.bleheartrate.ui.PipScreen(
                            heartRate = heartRateData.heartRate
                        )
                    } else {
                        MainScreen(
                        isServiceRunning = isServiceRunning,
                        heartRateData = heartRateData,
                        connectionState = connectionState,
                        displayMode = displayMode,
                        autoReconnect = autoReconnect,
                        pairedDevices = pairedDevices,
                        selectedDevice = selectedDevice,
                        onStartService = { startMonitoring() },
                        onStopService = { stopMonitoring() },
                        onDisplayModeChange = { mode -> 
                            viewModel.setDisplayMode(mode)
                            if (isServiceRunning) {
                                updateDisplayMode(mode)
                            }
                        },
                        onAutoReconnectChange = { viewModel.setAutoReconnect(it) },
                        onRefreshDevices = { 
                            if (checkBluetoothPermissions()) {
                                viewModel.loadPairedDevices()
                            } else {
                                requestBluetoothPermissions()
                            }
                        },
                        onSelectDevice = { viewModel.selectDevice(it) },
                        onConnectDevice = { viewModel.connectToDevice() },
                        onDisconnectDevice = { viewModel.disconnectDevice() },
                        onUnpairDevice = { viewModel.unpairDevice(it) }
                        )
                    }
                }
            }
        }

        requestBluetoothPermissions()
    }

    private fun startMonitoring() {
        val displayMode = viewModel.displayMode.value
        
        if (displayMode == DisplayMode.FLOATING_WINDOW) {
            if (checkOverlayPermission()) {
                startFloatingWindowService()
            } else {
                requestOverlayPermission()
                return
            }
        }
        
        viewModel.startService(this)
        
        if (displayMode == DisplayMode.PICTURE_IN_PICTURE) {
            enterPipMode()
        }
    }

    private fun stopMonitoring() {
        stopFloatingWindowService()
        viewModel.stopService(this)
    }

    private fun updateDisplayMode(mode: DisplayMode) {
        stopFloatingWindowService()
        
        if (mode == DisplayMode.FLOATING_WINDOW) {
            if (checkOverlayPermission()) {
                startFloatingWindowService()
            }
        } else if (mode == DisplayMode.PICTURE_IN_PICTURE) {
            enterPipMode()
        }
    }

    private fun startFloatingWindowService() {
        val intent = Intent(this, FloatingWindowService::class.java)
        startService(intent)
    }

    private fun stopFloatingWindowService() {
        val intent = Intent(this, FloatingWindowService::class.java)
        stopService(intent)
    }

    private fun updateFloatingWindow(heartRate: Int) {
        val intent = Intent(this, FloatingWindowService::class.java).apply {
            action = FloatingWindowService.ACTION_UPDATE_HEART_RATE
            putExtra(FloatingWindowService.EXTRA_HEART_RATE, heartRate)
        }
        startService(intent)
    }

    private fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(1, 1))
                .build()
            enterPictureInPictureMode(params)
        }
    }

    private fun checkBluetoothPermissions(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            listOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
        
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
        
        bluetoothPermissionLauncher.launch(permissions)
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.unbindService(this)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (viewModel.isServiceRunning.value && 
            viewModel.displayMode.value == DisplayMode.PICTURE_IN_PICTURE) {
            enterPipMode()
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        // The UI will automatically update due to configuration change
    }
}
