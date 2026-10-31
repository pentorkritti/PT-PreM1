package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.i18n.LocalAppLanguage
import com.example.i18n.formatNumber
import com.example.i18n.t
import com.example.model.GameCatalog
import com.example.ui.components.PtGlassCard
import com.example.ui.theme.PtRosePink
import com.example.ui.theme.PtSkyBlue
import com.example.ui.theme.PtSoftGold
import com.example.viewmodel.GameViewModel

@Composable
fun LeaderboardsScreen(
    currentUsername: String,
    vm: GameViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    var selectedGroup by rememberSaveable { mutableStateOf("OVERALL") }
    val boardsInGroup = remember(selectedGroup) {
        GameCatalog.allLeaderboardBoards.filter { it.group == selectedGroup }
    }
    var selectedBoardId by rememberSaveable(selectedGroup) {
        mutableStateOf(boardsInGroup.firstOrNull()?.boardId ?: "OVERALL_TOTAL")
    }

    val top10Entries by remember(selectedBoardId) {
        vm.observeLeaderboard(selectedBoardId)
    }.collectAsState(initial = emptyList())

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                }
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = PtSoftGold, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = t("กระดานจัดอันดับ Top 10", "Top 10 Rank Leaderboards", "全服 Top 10 荣誉排行榜"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Category Group Tabs (Overall + 10 Subjects + 3 Modes + 7 Mini-Games = 21 Boards!)
        item {
            val groupTabs = listOf(
                "OVERALL" to t("👑 อันดับรวมทั้งหมด", "👑 Overall Combined", "👑 全服综合总榜"),
                "SUBJECT" to t("📚 รายวิชา (10 วิชา)", "📚 By Subject (10)", "📚 单科排行(10)"),
                "MODE" to t("🎯 รายโหมดสอบ (3)", "🎯 By Exam Mode (3)", "🎯 模式排行(3)"),
                "GAME" to t("🎮 รายมินิเกม (7)", "🎮 By Mini-Game (7)", "🎮 小游戏榜(7)")
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(groupTabs) { (grp, label) ->
                    FilterChip(
                        selected = selectedGroup == grp,
                        onClick = {
                            selectedGroup = grp
                            val firstId = GameCatalog.allLeaderboardBoards.firstOrNull { it.group == grp }?.boardId
                            if (firstId != null) selectedBoardId = firstId
                        },
                        label = { Text(label, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        if (boardsInGroup.size > 1) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(boardsInGroup, key = { it.boardId }) { b ->
                        FilterChip(
                            selected = selectedBoardId == b.boardId,
                            onClick = { selectedBoardId = b.boardId },
                            label = { Text(b.name(lang)) }
                        )
                    }
                }
            }
        }

        // Privacy Notice
        item {
            Text(
                text = t(
                    "🔒 แสดงเฉพาะชื่อผู้ใช้ในเกมและอวตารเท่านั้น เพื่อคุ้มครองความเป็นส่วนตัวของผู้เรียนทุกคน",
                    "🔒 Displays in-game Username & Avatar only to protect student privacy.",
                    "🔒 仅展示游戏用户名与头像，严格保护玩家隐私。"
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (top10Entries.isEmpty()) {
            item {
                PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = t(
                                "ยังไม่มีผู้พิชิตกระดานหมวดนี้! เข้าทำข้อสอบหรือเล่นมินิเกมเพื่อขึ้นอันดับ #1 คนแรกเลย!",
                                "No rankings yet on this board! Complete an exam or mini-game to claim #1!",
                                "该榜单虚位以待！立即完成考试或小游戏夺取第一名吧！"
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            itemsIndexed(top10Entries, key = { _, e -> e.id }) { idx, entry ->
                val rank = idx + 1
                val isMe = entry.username.equals(currentUsername, ignoreCase = true)
                val medalColor = when (rank) {
                    1 -> PtSoftGold
                    2 -> Color(0xFF94A3B8)
                    3 -> Color(0xFFD97706)
                    else -> PtSkyBlue
                }

                PtGlassCard(
                    borderColors = if (isMe) listOf(PtRosePink, PtSkyBlue) else listOf(medalColor.copy(alpha = 0.5f), Color(0xFFE2E8F0)),
                    containerColor = if (isMe) Color(0xFFFDF2F8) else Color.White.copy(alpha = 0.92f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(medalColor)
                        ) {
                            Text(
                                text = if (rank <= 3) listOf("🥇", "🥈", "🥉")[rank - 1] else "#$rank",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        PlayerAvatarBadge(avatarIndex = entry.avatarIndex, size = 42.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.username + if (isMe) " (${t("คุณ", "You", "你")})" else "",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.Transparent,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(Brush.horizontalGradient(rankBadgeColors(entry.rankTitle)))
                            ) {
                                Text(
                                    text = entry.rankTitle,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${formatNumber(lang, entry.score)} pts",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = PtSkyBlue
                        )
                    }
                }
            }
        }
    }
}
