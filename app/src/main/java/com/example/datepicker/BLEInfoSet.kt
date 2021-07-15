package com.example.datepicker

import android.bluetooth.BluetoothDevice

class BLEInfoSet {
    private var device: BluetoothDevice? = null
    private var rssi = 0

    fun getDevice(): BluetoothDevice? {
        return device
    }

    fun setDevice(device: BluetoothDevice?) {
        this.device = device
    }

    fun getRssi(): Int {
        return rssi
    }

    fun setRssi(rssi: Int) {
        this.rssi = rssi
    }

}