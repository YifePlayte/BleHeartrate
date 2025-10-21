package com.yifeplayte.bleheartrate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yifeplayte.bleheartrate.R
import com.yifeplayte.bleheartrate.model.BluetoothDeviceInfo
import com.yifeplayte.bleheartrate.model.ConnectionState
import com.yifeplayte.bleheartrate.model.DisplayMode
import com.yifeplayte.bleheartrate.model.HeartRateData

@Composable
fun PipScreen(heartRate: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Heart Rate",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (heartRate > 0) "$heartRate" else "--",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "BPM",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isServiceRunning: Boolean,
    heartRateData: HeartRateData,
    connectionState: ConnectionState,
    displayMode: DisplayMode,
    autoReconnect: Boolean,
    pairedDevices: List<BluetoothDeviceInfo>,
    selectedDevice: BluetoothDeviceInfo?,
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    onDisplayModeChange: (DisplayMode) -> Unit,
    onAutoReconnectChange: (Boolean) -> Unit,
    onRefreshDevices: () -> Unit,
    onSelectDevice: (BluetoothDeviceInfo) -> Unit,
    onConnectDevice: () -> Unit,
    onDisconnectDevice: () -> Unit,
    onUnpairDevice: (BluetoothDeviceInfo) -> Unit
) {
    var showDeviceDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Heart Rate Display Card
            item {
                HeartRateCard(
                    heartRate = heartRateData.heartRate,
                    connectionState = connectionState
                )
            }

            // Service Control
            item {
                ServiceControlSection(
                    isServiceRunning = isServiceRunning,
                    onStartService = onStartService,
                    onStopService = onStopService
                )
            }

            // Display Mode Selection
            item {
                DisplayModeSection(
                    displayMode = displayMode,
                    onDisplayModeChange = onDisplayModeChange
                )
            }

            // Bluetooth Device Section
            item {
                BluetoothDeviceSection(
                    selectedDevice = selectedDevice,
                    connectionState = connectionState,
                    onSelectDevice = { showDeviceDialog = true },
                    onConnectDevice = onConnectDevice,
                    onDisconnectDevice = onDisconnectDevice
                )
            }

            // Auto Reconnect Switch
            item {
                AutoReconnectSection(
                    autoReconnect = autoReconnect,
                    onAutoReconnectChange = onAutoReconnectChange
                )
            }
        }

        if (showDeviceDialog) {
            DeviceSelectionDialog(
                devices = pairedDevices,
                selectedDevice = selectedDevice,
                onDismiss = { showDeviceDialog = false },
                onSelectDevice = {
                    onSelectDevice(it)
                    showDeviceDialog = false
                },
                onRefresh = onRefreshDevices,
                onUnpair = onUnpairDevice
            )
        }
    }
}

@Composable
fun HeartRateCard(
    heartRate: Int,
    connectionState: ConnectionState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = stringResource(R.string.heart_rate),
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (heartRate > 0) "$heartRate" else "--",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.bpm),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when (connectionState) {
                    ConnectionState.CONNECTED -> stringResource(R.string.connected)
                    ConnectionState.CONNECTING -> stringResource(R.string.connecting)
                    ConnectionState.DISCONNECTED -> stringResource(R.string.disconnected)
                    ConnectionState.DISCONNECTING -> stringResource(R.string.disconnected)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ServiceControlSection(
    isServiceRunning: Boolean,
    onStartService: () -> Unit,
    onStopService: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.start),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { if (isServiceRunning) onStopService() else onStartService() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isServiceRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isServiceRunning) 
                        stringResource(R.string.stop) 
                    else 
                        stringResource(R.string.start)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayModeSection(
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.display_mode),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = displayMode == DisplayMode.FLOATING_WINDOW,
                    onClick = { onDisplayModeChange(DisplayMode.FLOATING_WINDOW) },
                    label = { Text(stringResource(R.string.floating_window)) },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
                FilterChip(
                    selected = displayMode == DisplayMode.PICTURE_IN_PICTURE,
                    onClick = { onDisplayModeChange(DisplayMode.PICTURE_IN_PICTURE) },
                    label = { Text(stringResource(R.string.picture_in_picture)) },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PictureInPicture,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun BluetoothDeviceSection(
    selectedDevice: BluetoothDeviceInfo?,
    connectionState: ConnectionState,
    onSelectDevice: () -> Unit,
    onConnectDevice: () -> Unit,
    onDisconnectDevice: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.bluetooth_device),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = onSelectDevice,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedDevice?.name ?: stringResource(R.string.no_device_selected)
                )
            }
            
            if (selectedDevice != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (connectionState == ConnectionState.CONNECTED) {
                            onDisconnectDevice()
                        } else {
                            onConnectDevice()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = connectionState != ConnectionState.CONNECTING
                ) {
                    Icon(
                        imageVector = if (connectionState == ConnectionState.CONNECTED) 
                            Icons.Default.BluetoothDisabled 
                        else 
                            Icons.Default.BluetoothConnected,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (connectionState == ConnectionState.CONNECTED)
                            stringResource(R.string.disconnect)
                        else
                            stringResource(R.string.connect)
                    )
                }
            }
        }
    }
}

@Composable
fun AutoReconnectSection(
    autoReconnect: Boolean,
    onAutoReconnectChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.auto_reconnect),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Switch(
                checked = autoReconnect,
                onCheckedChange = onAutoReconnectChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceSelectionDialog(
    devices: List<BluetoothDeviceInfo>,
    selectedDevice: BluetoothDeviceInfo?,
    onDismiss: () -> Unit,
    onSelectDevice: (BluetoothDeviceInfo) -> Unit,
    onRefresh: () -> Unit,
    onUnpair: (BluetoothDeviceInfo) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.bluetooth_device)) },
        text = {
            LazyColumn {
                items(devices) { device ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = { onSelectDevice(device) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = device.name,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = device.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (selectedDevice?.address == device.address) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onRefresh) {
                Text(stringResource(R.string.pair_device))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}
