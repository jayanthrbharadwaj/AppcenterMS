package com.app.appcenter.shared.model.storage.dao.db.secure

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.math.BigInteger
import java.security.*
import java.security.cert.Certificate
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

class SecureRandomGenerator(private val context: Context): SecureRandomContract {

    private val ANDROID_KEY_STORE = "AndroidKeyStore"
    private val RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
    private val ALIAS = "RSA_KEY"
    private val RSA_ALGORITHM = "RSA"
    private val CERTIFICATE_PRINCIPAL = "CN=ICT.AM3,O=ICT"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun generateRandom(byteArray: ByteArray): String =
        encrypt(byteArray, context)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun encrypt(byteArrayToBeEncrypt: ByteArray, context: Context): String {
        return try {
            val certificate: Certificate = getRsaKeyEntry(context).getCertificate()
            val cipher =
                Cipher.getInstance(RSA_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, certificate)
            val encrypted = cipher.doFinal(byteArrayToBeEncrypt)
            Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            throw(e)
        }
    }

    /**
     * Get the RSA key.
     *
     * @return object of [KeyStore.PrivateKeyEntry]
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getRsaKeyEntry(context: Context): KeyStore.PrivateKeyEntry {
        return try {
            val keyStore =
                KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore.load(null)
            if (keyStore.containsAlias(ALIAS)) {
                // Return existing key
                return getKeyEntryCompat(keyStore)
            }
            val spec: AlgorithmParameterSpec = createSpecBuilder(context)
            val generator: KeyPairGenerator = KeyPairGenerator.getInstance(
                RSA_ALGORITHM,
                ANDROID_KEY_STORE
            )
            generator.initialize(spec)
            generator.generateKeyPair()
            getKeyEntryCompat(keyStore)
        } catch (e: Exception) {
            throw(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createSpecBuilder(context: Context): AlgorithmParameterSpec {
        val principal: X500Principal =
            X500Principal(CERTIFICATE_PRINCIPAL)
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 25)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createSpecBuilderAbove22Api(principal, start, end)
        } else {
            createSpecBuilderBelow23Api(context, principal, start, end)
        }
    }

    /**
     * Following code is for API 18-22. Generate new RSA KeyPair and save it on the KeyStore.
     *
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Deprecated("It is used in below L.")
    private fun createSpecBuilderBelow23Api(
        context: Context, principal: X500Principal, start: Calendar, end: Calendar
    ): AlgorithmParameterSpec {
        val specBuilder = KeyPairGeneratorSpec.Builder(context)
            .setAlias(ALIAS)
            .setSubject(principal)
            .setKeySize(2048)
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(start.time)
            .setEndDate(end.time)
        val keyGuardManager =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (keyGuardManager != null) {
            // The next call can return null when the LockScreen is not configured.
            val authIntent = keyGuardManager.createConfirmDeviceCredentialIntent(null, null)
            val keyguardEnabled = keyGuardManager.isKeyguardSecure && authIntent != null
            if (keyguardEnabled) {
                // If a ScreenLock is setup, protect this key pair.
                specBuilder.setEncryptionRequired()
            }
        }
        return specBuilder.build()
    }

    /** Following code is for API Above 22. Generate new RSA KeyPair and save it on the KeyStore.  */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun createSpecBuilderAbove22Api(
        principal: X500Principal, start: Calendar, end: Calendar
    ): AlgorithmParameterSpec {
        return KeyGenParameterSpec.Builder(
            ALIAS,
            KeyProperties.PURPOSE_DECRYPT or KeyProperties.PURPOSE_ENCRYPT
        )
            .setCertificateSubject(principal)
            .setCertificateSerialNumber(BigInteger.ONE)
            .setCertificateNotBefore(start.time)
            .setCertificateNotAfter(end.time)
            .setKeySize(2048)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .build()
    }

    /** Creates new RSA Public/Private Key pairs from the Android Key Store.  */
    @Throws(
        KeyStoreException::class,
        NoSuchAlgorithmException::class,
        UnrecoverableEntryException::class
    )
    private fun getKeyEntryCompat(keyStore: KeyStore): KeyStore.PrivateKeyEntry {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return keyStore.getEntry(
                ALIAS,
                null
            ) as KeyStore.PrivateKeyEntry
        }

        // Following code is for API 28+
        val privateKey =
            keyStore.getKey(
                ALIAS,
                null
            ) as PrivateKey
                ?: return keyStore.getEntry(
                    ALIAS,
                    null
                ) as KeyStore.PrivateKeyEntry
        val certificate =
            keyStore.getCertificate(ALIAS)
        return KeyStore.PrivateKeyEntry(privateKey, arrayOf(certificate))
    }
}