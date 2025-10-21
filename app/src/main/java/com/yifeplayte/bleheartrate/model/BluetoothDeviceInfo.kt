package com.yifeplayte.bleheartrate.model

data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val isPaired: Boolean = false,
    val isConnected: Boolean = false
)
