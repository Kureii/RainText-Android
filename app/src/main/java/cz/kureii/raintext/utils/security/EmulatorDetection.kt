package cz.kureii.raintext.utils.security

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log

class EmulatorDetection() {
    fun isEmulator(): Boolean {
        var counter = 0
        if (isEmulatorBrand()) {
            Log.e("EmulatorDetection", "Brand")
            counter++
        }
        if (isEmulatorHardware()) {
            Log.e("EmulatorDetection", "Hardware")
            counter++
        }
        if (isEmulatorManufacturer()) {
            Log.e("EmulatorDetection", "Manufacturer")
            counter++
        }
        if (isEmulatorModel()) {
            Log.e("EmulatorDetection", "Model")
            counter++
        }
        if (isEmulatorFingerprint()) {
            Log.e("EmulatorDetection", "Fingerprint")
            counter++
        }
        if (isEmulatorBoard()) {
            Log.e("EmulatorDetection", "Board")
            counter++
        }
        Log.i("EmulatorDetection","Counter: $counter")
        if (isEmulatorProduct()) {
            Log.e("EmulatorDetection", "Product")
            return true
        }
        if ((Build.MANUFACTURER == "Google" && Build.BRAND == "google" &&
                ((Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                        && Build.FINGERPRINT.endsWith(":user/release-keys")
                        && Build.PRODUCT.startsWith("sdk_gphone_")
                        && Build.MODEL.startsWith("sdk_gphone_"))
                        //alternative
                        || (Build.FINGERPRINT.startsWith("google/sdk_gphone64_")
                        && (Build.FINGERPRINT.endsWith(":userdebug/dev-keys") || Build.FINGERPRINT.endsWith(":user/release-keys"))
                        && Build.PRODUCT.startsWith("sdk_gphone64_")
                        && Build.MODEL.startsWith("sdk_gphone64_"))))) {
            return true
        }
        Log.i("EmulatorDetection","Counter: $counter")
        Log.i("EmulatorDetection", "idk")
       return counter > 3
    }

    private fun isEmulatorHardware(): Boolean {
        val hw = Build.HARDWARE
        Log.e("EmulatorDetectionFun", "Hardware: $hw")
        return Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
    }

    private fun isEmulatorModel(): Boolean {
        val hw = Build.MODEL
        Log.e("EmulatorDetectionFun", "Model: $hw")
        return Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
    }

    private fun isEmulatorFingerprint(): Boolean {
        val hw = Build.FINGERPRINT
        Log.e("EmulatorDetectionFun", "Fingerprint: $hw")
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
    }

    private fun isEmulatorManufacturer(): Boolean {
        val hw = Build.MANUFACTURER
        Log.e("EmulatorDetectionFun", "Manufacturer: $hw")
        return Build.MANUFACTURER.contains("Genymotion")
    }

    private fun isEmulatorProduct(): Boolean {
        val hw = Build.PRODUCT
        val hww = Build.PRODUCT.contains("sdk").toString()
        Log.e("EmulatorDetectionFun", "Product: $hw")
        Log.d("EmulatorDetectionFun", "contain sdk: $hww" )
        return Build.PRODUCT.contains("sdk")
               || Build.PRODUCT.contains("vbox86p")
               || Build.PRODUCT.contains("emulator")
               || Build.PRODUCT.contains("simulator");
    }

    private fun isEmulatorBrand(): Boolean {
        val hw = Build.BRAND
        Log.e("EmulatorDetectionFun", "Brand: $hw")
        val hww = Build.DEVICE
        Log.e("EmulatorDetectionFun", "Device: $hww")
        return Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
    }

    private fun isEmulatorBoard(): Boolean {
        val hw = Build.BRAND
        Log.e("EmulatorDetectionFun", "Board: $hw")
        return "QC_Reference_Phone" == Build.BOARD && !"Xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
    }
}