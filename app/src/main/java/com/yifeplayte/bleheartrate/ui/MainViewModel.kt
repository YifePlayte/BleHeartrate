package com.yifeplayte.bleheartrate.ui

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yifeplayte.bleheartrate.model.BluetoothDeviceInfo
import com.yifeplayte.bleheartrate.model.ConnectionState
import com.yifeplayte.bleheartrate.model.DisplayMode
import com.yifeplayte.bleheartrate.model.HeartRateData
import com.yifeplayte.bleheartrate.repository.PreferencesRepository
import com.yifeplayte.bleheartrate.service.HeartRateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository(application)
    private val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private var heartRateService: HeartRateService? = null
    private var serviceBound = false

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    private val _heartRateData = MutableStateFlow(HeartRateData())
    val heartRateData: StateFlow<HeartRateData> = _heartRateData.asStateFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _displayMode = MutableStateFlow(DisplayMode.FLOATING_WINDOW)
    val displayMode: StateFlow<DisplayMode> = _displayMode.asStateFlow()

    private val _autoReconnect = MutableStateFlow(true)
    val autoReconnect: StateFlow<Boolean> = _autoReconnect.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDeviceInfo>> = _pairedDevices.asStateFlow()

    private val _selectedDevice = MutableStateFlow<BluetoothDeviceInfo?>(null)
    val selectedDevice: StateFlow<BluetoothDeviceInfo?> = _selectedDevice.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as HeartRateService.LocalBinder
            heartRateService = binder.getService()
            serviceBound = true

            viewModelScope.launch {
                heartRateService?.heartRateData?.collect { data ->
                    _heartRateData.value = data
                }
            }

            viewModelScope.launch {
                heartRateService?.connectionState?.collect { state ->
                    _connectionState.value = state
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            heartRateService = null
            serviceBound = false
        }
    }

    init {
        viewModelScope.launch {
            preferencesRepository.displayMode.collect { mode ->
                _displayMode.value = mode
            }
        }

        viewModelScope.launch {
            preferencesRepository.autoReconnect.collect { enabled ->
                _autoReconnect.value = enabled
            }
        }

        viewModelScope.launch {
            preferencesRepository.savedDeviceAddress.collect { address ->
                if (address != null) {
                    loadPairedDevices()
                    _selectedDevice.value = _pairedDevices.value.find { it.address == address }
                }
            }
        }
    }

    fun bindService(context: Context) {
        val intent = Intent(context, HeartRateService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        if (serviceBound) {
            context.unbindService(serviceConnection)
            serviceBound = false
        }
    }

    fun startService(context: Context) {
        val intent = Intent(context, HeartRateService::class.java)
        context.startForegroundService(intent)
        _isServiceRunning.value = true
    }

    fun stopService(context: Context) {
        heartRateService?.disconnect()
        val intent = Intent(context, HeartRateService::class.java)
        context.stopService(intent)
        _isServiceRunning.value = false
    }

    @Suppress("MissingPermission")
    fun loadPairedDevices() {
        val devices = bluetoothAdapter?.bondedDevices?.map { device ->
            BluetoothDeviceInfo(
                name = device.name ?: "Unknown",
                address = device.address,
                isPaired = true,
                isConnected = false
            )
        } ?: emptyList()
        _pairedDevices.value = devices
    }

    fun selectDevice(device: BluetoothDeviceInfo) {
        _selectedDevice.value = device
        viewModelScope.launch {
            preferencesRepository.saveDevice(device.name, device.address)
        }
    }

    fun connectToDevice() {
        _selectedDevice.value?.let { device ->
            heartRateService?.connectToDevice(device.address)
        }
    }

    fun disconnectDevice() {
        heartRateService?.disconnect()
    }

    fun setDisplayMode(mode: DisplayMode) {
        viewModelScope.launch {
            preferencesRepository.setDisplayMode(mode)
        }
    }

    fun setAutoReconnect(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAutoReconnect(enabled)
        }
    }

    fun unpairDevice(device: BluetoothDeviceInfo) {
        viewModelScope.launch {
            if (_selectedDevice.value?.address == device.address) {
                _selectedDevice.value = null
                preferencesRepository.clearSavedDevice()
            }
            loadPairedDevices()
        }
    }

    override fun onCleared() {
        super.onCleared()
        heartRateService = null
    }
}
