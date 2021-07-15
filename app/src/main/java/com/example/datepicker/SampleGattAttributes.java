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

import java.util.HashMap;
import java.util.UUID;

// # SampleGattAttributes
// * This class includes a small subset of standard GATT attributes for demonstration purposes.
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static String CMA_NOTI_CHARACTERISTIC_CONFIG 	= "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String CMA_GET_CHARACTERISTIC_CONFIG 		= "00002901-0000-1000-8000-00805f9b34fb";

    public static final UUID CMA_BATTERTY_SERVICE_UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    public static final UUID CMA_BATTERTY_LEVEL_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    public static final UUID CMA_MOISTURE_SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static final UUID CMA_MOISTURE_LEVEL_UUID = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");

    public static String INPUT_KEY_SERVICE_UUID				= "0000ffe1-0000-1000-8000-00805f9b34fb";	// WRITE only
    public static String RETURN_VALUE_STATUS_SERVICE_UUID	= "0000ffe2-0000-1000-8000-00805f9b34fb";	// READ only

    public static String INPUT_KEY_CHARACTER_UUID			= "0000ffe9-0000-1000-8000-00805f9b34fb";
    public static String RETURN_VALUE_STATUS_CHARACTER_UUID	= "0000ffea-0000-1000-8000-00805f9b34fb";

    public static final UUID UUID_RETURN_VALUE_STATUS_CHARACTER_UUID = UUID.fromString("0000ffea-0000-1000-8000-00805f9b34fb");

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    public static byte VSL_LED_ON 	= (byte) 0x21;
    public static byte UV_LED_ON 	= (byte) 0x22;
    public static byte MODE_OFF 	= (byte) 0x80;
    public static byte GET_MOISTURE	= (byte) 0x41;
    public static byte POWER_OFF	= (byte) 0x12;

//    public static byte LED_MODE		= (byte) 0x18;

}
