package com.yifeplayte.bleheartrate.model

data class HeartRateData(
    val heartRate: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
