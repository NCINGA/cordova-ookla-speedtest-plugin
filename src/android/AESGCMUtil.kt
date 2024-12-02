package com.ncinga.speedtest

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

object AESGCMUtil {
    private const val AES = "AES"
    private const val AES_GCM_NO_PADDING = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12
    private const val SECRET_KEY_STRING = "8c4ce881b23cc97862b59aae01e895723fe9e84e6bedb73fe5538c223f4c8e10"

    private val SECRET_KEY = SecretKeySpec(SECRET_KEY_STRING.toByteArray().copyOf(16), AES)

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
        val iv = ByteArray(GCM_IV_LENGTH).apply {
            SecureRandom().nextBytes(this)
        }
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, spec)
        val encryptedData = cipher.doFinal(data.toByteArray())

        val combined = iv + encryptedData
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    fun decrypt(combinedBase64: String): String {
        val combined = Base64.decode(combinedBase64, Base64.NO_WRAP)

        val iv = combined.sliceArray(0 until GCM_IV_LENGTH)
        val encryptedData = combined.sliceArray(GCM_IV_LENGTH until combined.size)

        val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, spec)
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData)
    }
}
