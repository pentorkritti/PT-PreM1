package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameCatalog
import com.example.ui.theme.PtBubblegumPink
import com.example.ui.theme.PtElectricCyan
import com.example.ui.theme.PtLavender
import com.example.ui.theme.PtRosePink
import com.example.ui.theme.PtSkyBlue
import com.example.ui.theme.PtSoftGold

enum class AppRoute(val webPath: String) {
    HOME("/home"),
    PRACTICE_CENTER("/practice"),
    EXAM_CENTER("/exam"),
    SUBJECT_HUB("/subjects"),
    ACTIVE_EXAM("/exam/room"),
    EXAM_RESULT("/exam/result"),
    PT_SHOP("/shop"),
    PET_ZONE("/pets"),
    PET_INVENTORY("/pets/codex"),
    BACKGROUND_COLLECTION("/wardrobe"),
    QUESTS("/quests"),
    AI_TUTOR("/ai-tutor"),
    STUDY_SHEETS("/study-sheets"),
    SMART_STUDY_HUB("/study"),
    DAILY_REWARDS_PLAZA("/wheel"),
    BOSS_RAID("/boss"),
    MINI_GAMES("/minigames"),
    LEADERBOARDS("/leaderboard"),
    PROFILE_STATS("/profile"),
    SETTINGS("/settings"),
    ADMIN_PANEL("/admin")
}

fun parseHttpsDeepLinkRoute(rawUrlOrPath: String?): AppRoute? {
    if (rawUrlOrPath.isNullOrBlank()) return null
    val cleaned = rawUrlOrPath.trim()
        .removePrefix("https://www.PTPreM1.online")
        .removePrefix("https://www.ptprem1.online")
        .removePrefix("https://PTPreM1.online")
        .removePrefix("https://ptprem1.online")
        .lowercase()
    return when {
        cleaned.startsWith("/@") || cleaned.startsWith("/profile") -> AppRoute.PROFILE_STATS
        cleaned.startsWith("/exam") -> AppRoute.EXAM_CENTER
        cleaned.startsWith("/practice") || cleaned.startsWith("/subjects") -> AppRoute.SUBJECT_HUB
        cleaned.startsWith("/study-sheets") -> AppRoute.STUDY_SHEETS
        cleaned.startsWith("/study") || cleaned.startsWith("/flashcard") || cleaned.startsWith("/mistakes") -> AppRoute.SMART_STUDY_HUB
        cleaned.startsWith("/wheel") || cleaned.startsWith("/chest") || cleaned.startsWith("/rewards") -> AppRoute.DAILY_REWARDS_PLAZA
        cleaned.startsWith("/boss") -> AppRoute.BOSS_RAID
        cleaned.startsWith("/shop") -> AppRoute.PT_SHOP
        cleaned.startsWith("/pets/codex") -> AppRoute.PET_INVENTORY
        cleaned.startsWith("/pets") -> AppRoute.PET_ZONE
        cleaned.startsWith("/wardrobe") || cleaned.startsWith("/background") -> AppRoute.BACKGROUND_COLLECTION
        cleaned.startsWith("/quests") -> AppRoute.QUESTS
        cleaned.startsWith("/ai") -> AppRoute.AI_TUTOR
        cleaned.startsWith("/minigames") || cleaned.startsWith("/games") -> AppRoute.MINI_GAMES
        cleaned.startsWith("/leaderboard") || cleaned.startsWith("/rank") -> AppRoute.LEADERBOARDS
        cleaned.startsWith("/settings") -> AppRoute.SETTINGS
        cleaned.startsWith("/admin") -> AppRoute.ADMIN_PANEL
        cleaned == "/" || cleaned.startsWith("/home") -> AppRoute.HOME
        else -> null
    }
}

object PlayerAvatarCatalog {
    val avatars: List<Triple<String, Long, Long>> = listOf(
        Triple("🦉", 0xFF38BDF8, 0xFF0284C7),
        Triple("🦊", 0xFFFDBA74, 0xFFEA580C),
        Triple("🐰", 0xFFFBCFE8, 0xFFEC4899),
        Triple("🐼", 0xFFA7F3D0, 0xFF059669),
        Triple("🦄", 0xFFE9D5FF, 0xFF9333EA),
        Triple("🐲", 0xFF93C5FD, 0xFF2563EB),
        Triple("🦁", 0xFFFDE047, 0xFFD97706),
        Triple("🦌", 0xFFF472B6, 0xFF0EA5E9),
        Triple("🐱", 0xFFFFB5A7, 0xFFF43F5E),
        Triple("🐹", 0xFFFDE68A, 0xFFF59E0B)
    )
}

@Composable
fun PlayerAvatarBadge(
    avatarIndex: Int,
    size: Dp = 44.dp,
    frameId: String = "FRAME_DEFAULT",
    modifier: Modifier = Modifier
) {
    val item = PlayerAvatarCatalog.avatars.getOrElse(avatarIndex.coerceAtLeast(0) % PlayerAvatarCatalog.avatars.size) {
        PlayerAvatarCatalog.avatars.first()
    }
    val frameSpec = remember(frameId) { GameCatalog.getFrameById(frameId) }
    val frameColors = remember(frameSpec) {
        frameSpec.colorsHex.map { Color(it) }.let { if (it.size < 2) it + it else it }
    }
    val isSpecialFrame = frameSpec.id != "FRAME_DEFAULT"

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Outer Equipped Avatar Frame Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .shadow(if (isSpecialFrame) 6.dp else 2.dp, CircleShape)
                .clip(CircleShape)
                .background(Brush.sweepGradient(frameColors))
                .padding(if (isSpecialFrame) 3.5.dp else 2.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(item.second), Color(item.third))))
                    .border(1.5.dp, Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Text(
                    text = item.first,
                    fontSize = (size.value * 0.46f).sp
                )
            }
        }

        // Corner Frame Crest Emoji for Special Frames
        if (isSpecialFrame && size >= 42.dp) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
            ) {
                Text(
                    text = frameSpec.badgeEmoji,
                    fontSize = (size.value * 0.24f).coerceIn(10f, 15f).sp,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}

fun rankBadgeColors(rankTitle: String): List<Color> = when (rankTitle) {
    "Grandmaster" -> listOf(PtRosePink, PtSoftGold, PtElectricCyan)
    "Challenger" -> listOf(PtLavender, PtBubblegumPink)
    "Diamond" -> listOf(PtSkyBlue, PtElectricCyan)
    "Platinum" -> listOf(Color(0xFF14B8A6), Color(0xFF38BDF8))
    "Gold" -> listOf(PtSoftGold, Color(0xFFF59E0B))
    "Silver" -> listOf(Color(0xFF94A3B8), Color(0xFFCBD5E1))
    else -> listOf(Color(0xFFD97706), Color(0xFFFBBF24))
}
