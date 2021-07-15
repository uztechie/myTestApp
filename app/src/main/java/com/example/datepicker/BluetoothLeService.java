/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.datepicker;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.UUID;


// # BluetoothLeService
// * Service for managing connection and data communication with a GATT server hosted on a
// given Bluetooth LE device.
public class BluetoothLeService extends Service {
    private final static String TAG = "TEST"; // BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA";
    public final static String BATTERY_DATA = "com.chowis.cma.battery.DATA";
    public final static String MOISTURE_DATA = "com.chowis.cma.moisture.DATA";
    public final static String ENABLE_BATTERY = "com.chowis.cma.battery.ENABLE";
    public final static String ENABLE_MOISTURE = "com.chowis.cma.moisture.ENABLE";
    public final static String ACTION_CHARACTERISTIC_WRITE_COMPLETE = "com.chowis.cma.characteristic.write.complete";
    public final static String ACTION_CHARACTERISTIC_WRITE_ERROR = "com.chowis.cma.characteristic.write.error";
    public final static String ACTION_RESPONSE_ENALBE_BATTERY = "com.chowis.cma.characteristic.battery.enable";
    public final static String ACTION_CHANGE_BATTERY_LEVEL = "com.chowis.cma.characteristic.battery.level";
    public final static String ACTION_RESPONSE_ENALBE_MOISTURE = "com.chowis.cma.characteristic.moisture.enable";

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e(TAG, "jay:onConnectionStateChange: " + newState);
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                // connected 가 완료되면 다음 스텝으로 discoereServices를 요청
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e(TAG, "onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.e(TAG, "onCharacteristicChanged");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e(TAG, "onCharacteristicWrite");
            Log.e(TAG, "characteristic : " + characteristic.getUuid());
            Log.e(TAG, "status		   : " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_CHARACTERISTIC_WRITE_COMPLETE);
            } else {
                broadcastError(status);
            }
        }
    };

    private void broadcastWithDataUpdate(final String action, String name, boolean enable) {
        Log.d(TAG, "broadcastWithDataUpdate - action: " + action);
        Intent intent = new Intent(action);
        intent.putExtra(name, enable);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action) {
        Log.d(TAG, "broadcastUpdate - action: " + action);
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    protected void broadcastError(int status) {
        final Intent intent = new Intent(ACTION_CHARACTERISTIC_WRITE_ERROR);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);

        Log.d(TAG, "broadcastUpdate - characteristic.getUuid(): " + characteristic.getUuid());

        if (SampleGattAttributes.CMA_BATTERTY_LEVEL_UUID.equals(characteristic.getUuid())) {
            Log.d(TAG, "characteristic.getStringValue(0) = " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
            int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            Log.d(TAG, "byteToString: " + value);
            intent = new Intent(ACTION_CHANGE_BATTERY_LEVEL);
            intent.putExtra(BATTERY_DATA, value);
        } else if (UUID.fromString(SampleGattAttributes.RETURN_VALUE_STATUS_CHARACTER_UUID).equals(characteristic.getUuid())) {
            Log.d(TAG, "UUID: RETURN_VALUE_STATUS_CHARACTER_UUID");
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                byte[] value = new byte[data.length];
                for (int i = 0; i < data.length; i++) {
                    if (data[i] == 0x30 ||
                            data[i] == 0x31 ||
                            data[i] == 0x32 ||
                            data[i] == 0x33 ||
                            data[i] == 0x34 ||
                            data[i] == 0x35 ||
                            data[i] == 0x36 ||
                            data[i] == 0x37 ||
                            data[i] == 0x38 ||
                            data[i] == 0x39) {
                        value[i] = data[i];
                    }
                }

                Log.d(TAG, "MOISTURE DATA: " + new String(value).trim());

                intent.putExtra(MOISTURE_DATA, new String(value).trim()); // + "\n" + stringBuilder.toString());
            }
        } else {

            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }

                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
//        close();
        super.onUnbind(intent);
        return true;
//        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
//        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
//            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
//            if (mBluetoothGatt.connect()) {
//                mConnectionState = STATE_CONNECTING;
//                return true;
//            } else {
//                return false;
//            }
//        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.

        // Changed autoConnect to true, as per testing it is more stable than passing false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        Log.w(TAG, "readCharacteristic");

        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    @SuppressLint("NewApi")
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        // 다시 확인해본다.
        // jhong
        // since 190327
//        mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER);

        Log.e(TAG, "setCharacteristicNotification - Noti: " + characteristic.getUuid());
        Log.e(TAG, "setCharacteristicNotification - YES: " + mBluetoothGatt.setCharacteristicNotification(characteristic, enabled));

        if (SampleGattAttributes.CMA_NOTI_CHARACTERISTIC_CONFIG.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            Log.w(TAG, "CMA_CHARACTERISTIC_CONFIG - Noti: " + characteristic.getUuid());
        } else if (SampleGattAttributes.CMA_GET_CHARACTERISTIC_CONFIG.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        } else if (SampleGattAttributes.CMA_BATTERTY_LEVEL_UUID.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            if (mBluetoothGatt.writeDescriptor(descriptor)) {
                Log.e(TAG, "배터리 등록 유무 확인 YES");
                broadcastWithDataUpdate(ACTION_RESPONSE_ENALBE_BATTERY, ENABLE_BATTERY, true);
            } else {
                Log.e(TAG, "배터리 등록 유무 확인 NO");
                broadcastWithDataUpdate(ACTION_RESPONSE_ENALBE_BATTERY, ENABLE_BATTERY, false);
            }
        } else if (SampleGattAttributes.UUID_RETURN_VALUE_STATUS_CHARACTER_UUID.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            if (mBluetoothGatt.writeDescriptor(descriptor)) {
                Log.e(TAG, "수분 활성화 YES");
                broadcastWithDataUpdate(ACTION_RESPONSE_ENALBE_MOISTURE, ENABLE_MOISTURE, true);
            } else {
                Log.e(TAG, "수분 활성화 NO");
                broadcastWithDataUpdate(ACTION_RESPONSE_ENALBE_MOISTURE, ENABLE_MOISTURE, false);
            }
        } else {
            Log.w(TAG, "else");
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic charac) {
        boolean status = mBluetoothGatt.writeCharacteristic(charac);
        return status;
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();

    }

    public void requestBatteryLevel() {

        BluetoothGattService batteryService = mBluetoothGatt.getService(SampleGattAttributes.CMA_BATTERTY_SERVICE_UUID);
        if (batteryService == null) {
            Log.d(TAG, "Battery service not found!");
            return;
        }

        BluetoothGattCharacteristic batteryLevel = batteryService.getCharacteristic(SampleGattAttributes.CMA_BATTERTY_LEVEL_UUID);
        if (batteryLevel == null) {
            Log.d(TAG, "Battery level not found!");
            return;
        }

        readCharacteristic(batteryLevel);
    }

    public void onEnableNotificationBattery(boolean enable) {

        Log.d(TAG, "START - onEnableNotificationBattery");

        if (mBluetoothGatt == null) {
            return;
        }

        BluetoothGattService batteryService = mBluetoothGatt.getService(SampleGattAttributes.CMA_BATTERTY_SERVICE_UUID);
        if (batteryService == null) {
            Log.d(TAG, "Battery service not found!");
            return;
        }

        BluetoothGattCharacteristic batteryLevel = batteryService.getCharacteristic(SampleGattAttributes.CMA_BATTERTY_LEVEL_UUID);
        if (batteryLevel == null) {
            Log.d(TAG, "Battery level not found!");
            return;
        }

        this.setCharacteristicNotification(batteryLevel, enable);
    }

    public void onEnableNotificationMoisture(boolean enable) {

        Log.d(TAG, "START - onEnableNotificationMoisture");

        if (mBluetoothGatt == null) {
            return;
        }

        BluetoothGattService mService = mBluetoothGatt.getService(UUID.fromString(SampleGattAttributes.RETURN_VALUE_STATUS_SERVICE_UUID));
        if (mService == null) {
            Log.d(TAG, "Config service not found!");
            return;
        }

        BluetoothGattCharacteristic config = mService.getCharacteristic(UUID.fromString(SampleGattAttributes.RETURN_VALUE_STATUS_CHARACTER_UUID));
        if (config == null) {
            Log.d(TAG, "config character not found!");
            return;
        }

        this.setCharacteristicNotification(config, enable);
    }
}