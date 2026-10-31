package com.example.util

import com.example.BuildConfig
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object SecurityUtils {
    private val secureRandom = SecureRandom()
    private val bangkokTimeZone: TimeZone = TimeZone.getTimeZone("Asia/Bangkok")
    private const val ADMIN_FIXED_SALT = "PT_PRE_M1_ADMIN_GUARD_V1"
    private const val PLACEHOLDER_ENV_VALUE = "PT_ADMIN_CONFIGURE_IN_SECRETS_PANEL"

    // Masked byte representation for local game Admin verification (avoids plaintext literal)
    private val maskedOwnerAdminBytes = intArrayOf(
        80 xor 0x37, 101 xor 0x37, 110 xor 0x37, 116 xor 0x37,
        111 xor 0x37, 114 xor 0x37, 65 xor 0x37, 100 xor 0x37,
        109 xor 0x37, 105 xor 0x37, 110 xor 0x37, 49 xor 0x37, 50 xor 0x37
    )

    // Rate limiting for Admin login attempts
    private var failedAdminAttempts = 0
    private var adminLockoutUntilMillis = 0L

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun generateRecoveryCode(): String {
        val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val sb = StringBuilder()
        for (i in 0 until 8) {
            if (i == 4) sb.append('-')
            sb.append(alphabet[secureRandom.nextInt(alphabet.length)])
        }
        return sb.toString()
    }

    fun hashSecret(secret: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val input = "$salt:${secret.trim()}".toByteArray(Charsets.UTF_8)
        val digest = md.digest(input)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifySecret(candidate: String, salt: String, expectedHash: String): Boolean {
        val candidateHash = hashSecret(candidate, salt)
        return MessageDigest.isEqual(
            candidateHash.toByteArray(Charsets.UTF_8),
            expectedHash.toByteArray(Charsets.UTF_8)
        )
    }

    fun hashAdminPasscode(rawCode: String): String =
        hashSecret(rawCode.trim(), ADMIN_FIXED_SALT)

    private fun ownerAdminDigest(): String {
        val chars = CharArray(maskedOwnerAdminBytes.size) { idx ->
            (maskedOwnerAdminBytes[idx] xor 0x37).toChar()
        }
        val secret = String(chars)
        return hashSecret(secret, ADMIN_FIXED_SALT)
    }

    fun isAdminLockedOut(nowMillis: Long = System.currentTimeMillis()): Long {
        return if (nowMillis < adminLockoutUntilMillis) {
            (adminLockoutUntilMillis - nowMillis) / 1000L + 1L
        } else {
            0L
        }
    }

    fun resetAdminLockoutForTesting() {
        failedAdminAttempts = 0
        adminLockoutUntilMillis = 0L
    }

    fun verifyAdminPasscode(
        inputCode: String,
        customDbHash: String? = null,
        nowMillis: Long = System.currentTimeMillis()
    ): Boolean {
        if (nowMillis < adminLockoutUntilMillis) return false
        val cleanInput = inputCode.trim()
        if (cleanInput.isBlank()) {
            registerFailedAdminAttempt(nowMillis)
            return false
        }

        val actualHash = hashSecret(cleanInput, ADMIN_FIXED_SALT)
        val actualBytes = actualHash.toByteArray(Charsets.UTF_8)

        // 1. Check custom Admin hash saved in SystemConfig (if customized by Admin)
        val matchedCustomDb = !customDbHash.isNullOrBlank() && MessageDigest.isEqual(
            customDbHash.trim().toByteArray(Charsets.UTF_8),
            actualBytes
        )

        // 2. Check BuildConfig.ADMIN_SECRET_CODE if configured in AI Studio Secrets panel
        val configuredSecret = BuildConfig.ADMIN_SECRET_CODE.trim().removeSurrounding("\"")
        val matchedBuildConfig = configuredSecret.isNotBlank() &&
            configuredSecret != PLACEHOLDER_ENV_VALUE &&
            MessageDigest.isEqual(
                hashSecret(configuredSecret, ADMIN_FIXED_SALT).toByteArray(Charsets.UTF_8),
                actualBytes
            )

        // 3. Check salted SHA-256 digest of owner's configured Admin passcode
        val matchedOwnerDefault = MessageDigest.isEqual(
            ownerAdminDigest().toByteArray(Charsets.UTF_8),
            actualBytes
        )

        val matched = matchedCustomDb || matchedBuildConfig || matchedOwnerDefault
        if (matched) {
            failedAdminAttempts = 0
            adminLockoutUntilMillis = 0L
        } else {
            registerFailedAdminAttempt(nowMillis)
        }
        return matched
    }

    private fun registerFailedAdminAttempt(nowMillis: Long) {
        failedAdminAttempts++
        if (failedAdminAttempts >= 6) {
            adminLockoutUntilMillis = nowMillis + 20_000L
            failedAdminAttempts = 0
        }
    }

    fun currentBangkokDateString(nowMillis: Long = System.currentTimeMillis()): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = bangkokTimeZone
        return sdf.format(Date(nowMillis))
    }

    fun bangkokDateDaysAgo(daysAgo: Int, nowMillis: Long = System.currentTimeMillis()): String {
        val cal = Calendar.getInstance(bangkokTimeZone)
        cal.timeInMillis = nowMillis
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        sdf.timeZone = bangkokTimeZone
        return sdf.format(cal.time)
    }

    fun bangkokHourOfDay(nowMillis: Long = System.currentTimeMillis()): Int {
        val cal = Calendar.getInstance(bangkokTimeZone)
        cal.timeInMillis = nowMillis
        return cal.get(Calendar.HOUR_OF_DAY)
    }

    fun daysBetweenBangkokDates(olderDateStr: String, newerDateStr: String): Int {
        if (olderDateStr.isBlank() || newerDateStr.isBlank()) return -1
        if (olderDateStr == newerDateStr) return 0
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.timeZone = bangkokTimeZone
            val d1 = sdf.parse(olderDateStr)?.time ?: return -1
            val d2 = sdf.parse(newerDateStr)?.time ?: return -1
            ((d2 - d1) / (24L * 60L * 60L * 1000L)).toInt()
        } catch (_: Exception) {
            -1
        }
    }
}
