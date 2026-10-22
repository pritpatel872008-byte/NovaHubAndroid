package com.novahub.android

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal object SecureVault {
    private const val KEY_ALIAS = "NovaHubDocumentVaultKey"

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    fun encrypt(input: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        return cipher.iv + cipher.doFinal(input)
    }

    fun decrypt(blob: ByteArray): ByteArray {
        require(blob.size > 12) { "Encrypted document is incomplete." }
        val iv = blob.copyOfRange(0, 12)
        val ciphertext = blob.copyOfRange(12, blob.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, iv))
        return cipher.doFinal(ciphertext)
    }

    fun save(privateDir: File, id: String, clearBytes: ByteArray) {
        require(clearBytes.isNotEmpty()) { "The selected file is empty." }
        require(clearBytes.size <= 20 * 1024 * 1024) { "Maximum supported document size is 20 MB." }
        File(privateDir, "vault_$id.bin").writeBytes(encrypt(clearBytes))
    }

    fun read(privateDir: File, id: String): ByteArray =
        decrypt(File(privateDir, "vault_$id.bin").readBytes())

    fun delete(privateDir: File, id: String) {
        File(privateDir, "vault_$id.bin").delete()
    }
}
