package com.example.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val OFFICIAL_HTTPS_DOMAIN = "PTPreM1.online"
const val OFFICIAL_HTTPS_ORIGIN = "https://PTPreM1.online"

enum class AppLanguage(val code: String, val label: String, val flag: String, val locale: Locale) {
    TH("th", "ไทย", "🇹🇭", Locale("th", "TH")),
    EN("en", "English", "🇬🇧", Locale.ENGLISH),
    ZH("zh", "中文", "🇨🇳", Locale.SIMPLIFIED_CHINESE);

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: TH
    }
}

enum class PlayerGender(
    val code: String,
    val emoji: String,
    val labelTh: String,
    val labelEn: String,
    val labelZh: String,
    val primaryHex: Long,
    val secondaryHex: Long
) {
    MALE("MALE", "👦", "ชาย", "Male", "男", 0xFF0EA5E9, 0xFF38BDF8),
    FEMALE("FEMALE", "👧", "หญิง", "Female", "女", 0xFFEC4899, 0xFFF472B6),
    UNSPECIFIED("UNSPECIFIED", "✨", "ไม่ระบุ", "Unspecified", "不透露", 0xFF8B5CF6, 0xFF2EC4B6);

    fun label(lang: AppLanguage): String = tr(lang, labelTh, labelEn, labelZh)
    val primaryColor: Color get() = Color(primaryHex)
    val secondaryColor: Color get() = Color(secondaryHex)

    companion object {
        fun fromCode(code: String?): PlayerGender =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: UNSPECIFIED
    }
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.TH }

fun tr(lang: AppLanguage, th: String, en: String, zh: String): String = when (lang) {
    AppLanguage.TH -> th
    AppLanguage.EN -> en
    AppLanguage.ZH -> zh
}

@Composable
fun t(th: String, en: String, zh: String): String {
    val lang = LocalAppLanguage.current
    return tr(lang, th, en, zh)
}

fun formatNumber(lang: AppLanguage, value: Number): String {
    return try {
        NumberFormat.getIntegerInstance(lang.locale).format(value.toLong())
    } catch (_: Exception) {
        value.toString()
    }
}

fun formatDateTime(lang: AppLanguage, millis: Long): String {
    if (millis <= 0L) return "-"
    return try {
        val pattern = when (lang) {
            AppLanguage.TH -> "d MMM yyyy HH:mm"
            AppLanguage.EN -> "MMM d, yyyy HH:mm"
            AppLanguage.ZH -> "yyyy年M月d日 HH:mm"
        }
        SimpleDateFormat(pattern, lang.locale).format(Date(millis))
    } catch (_: Exception) {
        millis.toString()
    }
}

fun buildOfficialHttpsUrl(path: String): String {
    val cleanPath = path.trim().removePrefix("/")
    return if (cleanPath.isEmpty()) OFFICIAL_HTTPS_ORIGIN else "$OFFICIAL_HTTPS_ORIGIN/$cleanPath"
}

fun dynamicGreeting(lang: AppLanguage, username: String, hourOfDay: Int): Pair<String, String> {
    val greet = when (lang) {
        AppLanguage.TH -> "สวัสดี $username! 🌈"
        AppLanguage.EN -> "Hello, $username! 🌈"
        AppLanguage.ZH -> "你好，$username！🌈"
    }
    val sub = when {
        hourOfDay in 5..11 -> tr(
            lang,
            "เช้านี้สดใสมาก! มาเก็บความรู้พิชิตสอบเข้า ม.1 กันเถอะ!",
            "Bright morning! Let's conquer M.1 knowledge together!",
            "阳光灿烂的早晨！一起来冲刺初一入学考试吧！"
        )
        hourOfDay in 12..17 -> tr(
            lang,
            "เติมพลังสมองยามบ่าย สะสม XP & PT Points สู่โรงเรียนในฝัน!",
            "Afternoon brain boost! Earn XP & PT Points toward your dream school!",
            "午后脑力充能！积累XP与PT积分迈向理想名校！"
        )
        else -> tr(
            lang,
            "ทบทวนยามค่ำอีกนิด เก่งขึ้นทุกวันแน่นอน!",
            "Cozy evening review — getting smarter every single day!",
            "晚间轻松温习，每天都在稳步进步！"
        )
    }
    return greet to sub
}
