package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuestionEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.formatNumber
import com.example.i18n.t
import com.example.i18n.tr
import com.example.model.BossSpec
import com.example.model.GameCatalog
import com.example.model.LuckyWheelSlice
import com.example.ui.components.HttpsPortalBar
import com.example.ui.components.PetCharacterCanvas
import com.example.ui.components.PtGlassCard
import com.example.ui.components.PtGradientButton
import com.example.ui.components.PtPointsCoinBadge
import com.example.ui.components.PtSectionRibbonHeader
import com.example.ui.theme.NegativeRedAlert
import com.example.ui.theme.PtBubblegumPink
import com.example.ui.theme.PtElectricCyan
import com.example.ui.theme.PtEmerald
import com.example.ui.theme.PtLavender
import com.example.ui.theme.PtRosePink
import com.example.ui.theme.PtSkyBlue
import com.example.ui.theme.PtSoftGold
import com.example.ui.theme.RainbowCoral
import com.example.ui.theme.ShopNeonRed
import com.example.util.SecurityUtils
import com.example.util.SfxType
import com.example.util.SoundEngine
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyRewardsPlazaScreen(
    user: UserEntity,
    vm: GameViewModel,
    onOpenShop: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    val chestOutcome by vm.lastChestOutcome.collectAsState()

    val todayBangkok = SecurityUtils.currentBangkokDateString()
    val hasFreeDailySpin = user.lastFreeSpinDateBangkok != todayBangkok

    var targetWheelAngle by remember { mutableFloatStateOf(0f) }
    var lastWonSlice by remember { mutableStateOf<LuckyWheelSlice?>(null) }

    val animatedWheelAngle by animateFloatAsState(
        targetValue = targetWheelAngle,
        animationSpec = tween(durationMillis = if (user.reducedMotion) 120 else 1700, easing = FastOutSlowInEasing),
        label = "lucky_wheel_spin"
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        item {
            HttpsPortalBar(
                currentPath = "/wheel",
                shareMessageExtra = tr(
                    lang,
                    "มาหมุนวงล้อนำโชครายวันและเปิดหีบสมบัติสายรุ้งที่ PT's มาสอบเข้า ม.1!",
                    "Spin the Daily Lucky Wheel & unbox Mystery Chests on PT's Pre-M.1!",
                    "快来 PT's 幸运广场转动每日大转盘、开启彩虹神秘宝箱！"
                ),
                onCopiedToast = { vm.showToast(it) }
            )
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                    }
                    Column {
                        Text(
                            text = t("ลานวงล้อนำโชค & หีบสมบัติ", "Lucky Wheel & Mystery Chests", "幸运大转盘与神秘宝箱广场"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = t(
                                "ตั๋ววงล้อ: 🎟️ ${user.luckyWheelTickets} • กุญแจหีบสมบัติ: 🗝️ ${user.mysteryBoxesOwned}",
                                "Wheel Tickets: 🎟️ ${user.luckyWheelTickets} • Chest Keys: 🗝️ ${user.mysteryBoxesOwned}",
                                "转盘奖券：🎟️ ${user.luckyWheelTickets} • 宝箱钥匙：🗝️ ${user.mysteryBoxesOwned}"
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = PtRosePink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                PtPointsCoinBadge(ptPoints = user.ptPoints, reducedMotion = user.reducedMotion)
            }
        }

        // 1. 8-Slice Rainbow Daily Lucky Wheel Card
        item {
            PtSectionRibbonHeader(
                emoji = "🎡",
                title = t("วงล้อนำโชครายวัน 8 ช่อง!", "8-Slice Rainbow Lucky Wheel!", "8格彩虹幸运大转盘！"),
                subtitle = if (hasFreeDailySpin) {
                    t("✨ วันนี้หมุนฟรีได้ 1 ครั้ง!", "✨ 1 FREE Daily Spin Ready!", "✨ 今日享有 1 次免费抽奖！")
                } else {
                    t("ใช้ตั๋ว 1 ใบ หรือ 60 PT ต่อการหมุน", "1 Ticket or 60 PT per extra spin", "每次额外旋转消耗1张奖券或60 PT")
                },
                colors = listOf(PtSkyBlue, PtRosePink, PtSoftGold)
            )
        }

        item {
            PtGlassCard(
                borderColors = listOf(PtSoftGold, PtRosePink, PtSkyBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(18.dp)
                ) {
                    // Golden Top Pointer + Custom 8-Slice Rainbow Wheel Canvas
                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier.size(250.dp)
                    ) {
                        Canvas(
                            modifier = Modifier
                                .size(236.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            val w = size.width
                            val h = size.height
                            val slices = GameCatalog.luckyWheelSlices
                            val sweep = 360f / slices.size

                            // Outer Gold Rim
                            drawCircle(
                                brush = Brush.sweepGradient(listOf(PtSoftGold, PtRosePink, PtSkyBlue, PtEmerald, PtSoftGold)),
                                radius = w * 0.49f
                            )

                            rotate(degrees = animatedWheelAngle) {
                                slices.forEachIndexed { idx, slice ->
                                    val startDeg = -90f - sweep / 2f + idx * sweep
                                    drawArc(
                                        color = Color(slice.colorHex),
                                        startAngle = startDeg,
                                        sweepAngle = sweep,
                                        useCenter = true,
                                        topLeft = Offset(w * 0.04f, h * 0.04f),
                                        size = Size(w * 0.92f, h * 0.92f)
                                    )
                                    drawArc(
                                        color = Color.White,
                                        startAngle = startDeg,
                                        sweepAngle = sweep,
                                        useCenter = true,
                                        topLeft = Offset(w * 0.04f, h * 0.04f),
                                        size = Size(w * 0.92f, h * 0.92f),
                                        style = Stroke(width = 2.5.dp.toPx())
                                    )
                                }
                            }

                            // Center Crystal Hub
                            drawCircle(color = Color.White, radius = w * 0.16f)
                            drawCircle(
                                brush = Brush.linearGradient(listOf(PtSkyBlue, PtRosePink)),
                                radius = w * 0.12f
                            )
                        }

                        // Top Pointer Triangle
                        Canvas(modifier = Modifier.size(34.dp)) {
                            val p = Path().apply {
                                moveTo(size.width * 0.5f, size.height * 0.95f)
                                lineTo(size.width * 0.12f, size.height * 0.12f)
                                lineTo(size.width * 0.88f, size.height * 0.12f)
                                close()
                            }
                            drawPath(p, color = ShopNeonRed)
                            drawPath(p, color = Color.White, style = Stroke(width = 2.dp.toPx()))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Slice Legend Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GameCatalog.luckyWheelSlices.forEach { sl ->
                            val highlighted = lastWonSlice?.index == sl.index
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(sl.colorHex).copy(alpha = if (highlighted) 1f else 0.16f),
                                border = BorderStroke(1.dp, Color(sl.colorHex))
                            ) {
                                Text(
                                    text = sl.label(lang),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (highlighted) Color.White else Color(0xFF0F172A),
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (lastWonSlice != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(1.5.dp, PtEmerald)
                        ) {
                            Text(
                                text = "🎉 ${t("ผลลัพธ์ล่าสุด", "Last Spin Won", "最新中奖")}: ${lastWonSlice!!.label(lang)}!",
                                color = PtEmerald,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val spinLabel = when {
                        hasFreeDailySpin -> t("🎡 หมุนวงล้อนำโชคฟรีประจำวัน!", "🎡 Spin Free Daily Wheel!", "🎡 立即免费旋转今日大转盘！")
                        user.luckyWheelTickets > 0 -> t("🎟️ ใช้ตั๋วหมุนวงล้อ (เหลือ ${user.luckyWheelTickets} ใบ)", "🎟️ Use Ticket to Spin (${user.luckyWheelTickets} left)", "🎟️ 使用奖券旋转（剩余 ${user.luckyWheelTickets} 张）")
                        else -> t("🎡 หมุนวงล้อนำโชค (60 PT)", "🎡 Spin Wheel (60 PT)", "🎡 花费 60 PT 旋转")
                    }

                    PtGradientButton(
                        text = spinLabel,
                        onClick = {
                            vm.spinDailyWheel { wonSlice ->
                                lastWonSlice = wonSlice
                                val sliceSweep = 360f / GameCatalog.luckyWheelSlices.size
                                val targetMod = (360f - (wonSlice.index * sliceSweep)) % 360f
                                val baseTurns = ((targetWheelAngle / 360f).toInt() + 5) * 360f
                                targetWheelAngle = baseTurns + targetMod
                            }
                        },
                        enabled = hasFreeDailySpin || user.luckyWheelTickets > 0 || user.ptPoints >= 60,
                        gradientColors = listOf(PtRosePink, PtSoftGold, PtSkyBlue),
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "spin_lucky_wheel_button"
                    )
                }
            }
        }

        // 2. 3-Tier Mystery Treasure Chests
        item {
            PtSectionRibbonHeader(
                emoji = "🎁",
                title = t("กล่องสมบัติปริศนา 3 ระดับ!", "3-Tier Mystery Treasure Chests!", "3阶彩虹神秘宝箱！"),
                subtitle = t(
                    "ลุ้นรับกรอบอวตารหายาก โล่พิทักษ์ไฟ และเหรียญ PT ก้อนโต!",
                    "Unbox exclusive Avatar Frames, Streak Shields & massive PT jackpots!",
                    "开启限定头像框、连胜护盾与巨额PT积分大奖！"
                ),
                colors = listOf(PtLavender, PtBubblegumPink),
                trailingContent = {
                    OutlinedButton(onClick = onOpenShop, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("ซื้อกุญแจ", "Shop", "商店"), fontSize = 11.sp)
                    }
                }
            )
        }

        items(GameCatalog.mysteryChests, key = { it.id }) { chest ->
            val canUseOwnedKey = chest.canUseKey && user.mysteryBoxesOwned > 0
            val canAfford = canUseOwnedKey || user.ptPoints >= chest.costPt

            PtGlassCard(
                borderColors = listOf(Color(chest.primaryHex), Color(chest.secondaryHex)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Brush.linearGradient(listOf(Color(chest.primaryHex), Color(chest.secondaryHex))))
                    ) {
                        Text(text = chest.emoji, fontSize = 32.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = chest.name(lang),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = chest.desc(lang),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PtGradientButton(
                            text = if (canUseOwnedKey) {
                                t("🗝️ เปิดฟรีด้วยกุญแจ! (มี ${user.mysteryBoxesOwned})", "🗝️ Open Free with Key! (${user.mysteryBoxesOwned})", "🗝️ 使用钥匙免费开启（${user.mysteryBoxesOwned}）")
                            } else {
                                t("🔓 เปิดหีบด้วย ${chest.costPt} PT", "🔓 Open for ${chest.costPt} PT", "🔓 花费 ${chest.costPt} PT 开启")
                            },
                            onClick = { vm.openMysteryChest(chest) },
                            enabled = canAfford,
                            gradientColors = listOf(Color(chest.primaryHex), PtSkyBlue),
                            testTag = "open_chest_${chest.id}"
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    val activeChestReward = chestOutcome
    if (activeChestReward != null) {
        AlertDialog(
            onDismissRequest = { vm.dismissChestOutcome() },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White,
            icon = {
                Text(text = activeChestReward.chest.emoji, fontSize = 48.sp)
            },
            title = {
                Text(
                    text = t("🎉 เปิดหีบสมบัติสำเร็จ!", "🎉 Treasure Unboxed!", "🎉 成功开启神秘宝箱！"),
                    fontWeight = FontWeight.ExtraBold,
                    color = PtRosePink,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = activeChestReward.title(lang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = PtSkyBlue
                    )
                    if (activeChestReward.unlockedFrameId != null) {
                        PlayerAvatarBadge(
                            avatarIndex = user.avatarIndex,
                            size = 76.dp,
                            frameId = activeChestReward.unlockedFrameId
                        )
                        Text(
                            text = t("ปลดล็อกและสวมใส่กรอบอวตารใหม่ให้อัตโนมัติแล้ว!", "New Avatar Frame unlocked & equipped!", "已自动解锁并佩戴新头像框！"),
                            style = MaterialTheme.typography.labelMedium,
                            color = PtEmerald,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                PtGradientButton(
                    text = t("รับรางวัลสุดปัง! ✨", "Collect Loot! ✨", "开心收下！✨"),
                    onClick = { vm.dismissChestOutcome() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

@Composable
fun BossRaidScreen(
    user: UserEntity,
    vm: GameViewModel,
    onOpenLeaderboards: () -> Unit,
    onBack: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val allQuestions by vm.allQuestions.collectAsState()
    val equippedPet = vm.getEquippedPetSpecies()

    var activeBoss by remember { mutableStateOf<BossSpec?>(null) }

    BackHandler {
        if (activeBoss != null) activeBoss = null else onBack()
    }

    if (activeBoss == null) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            item {
                HttpsPortalBar(
                    currentPath = "/boss",
                    shareMessageExtra = tr(
                        lang,
                        "มาท้าดวลปราบบอสข้อสอบเข้า ม.1 ชิงกรอบอวตารสุดเท่ที่ PTPreM1.online!",
                        "Challenge M.1 Subject Bosses & win exclusive Avatar Frames on PTPreM1.online!",
                        "快来 PTPreM1.online 讨伐科目大魔王，赢取限定头像框！"
                    ),
                    onCopiedToast = { vm.showToast(it) }
                )
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                        }
                        Column {
                            Text(
                                text = t("⚔️ โหมดท้าดวลบอสพิชิต ม.1", "⚔️ M.1 Boss Raid Arena", "⚔️ 初一科目魔王讨伐战"),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = t(
                                    "ปราบบอสสำเร็จแล้ว ${user.bossesDefeatedCount} ครั้ง • ตอบถูกรัวเพื่อทำ Combo Critical!",
                                    "Bosses Defeated: ${user.bossesDefeatedCount} • Chain correct answers for Criticals!",
                                    "已击败魔王 ${user.bossesDefeatedCount} 次 • 连续答对触发暴击伤害！"
                                ),
                                style = MaterialTheme.typography.labelSmall,
                                color = PtRosePink,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onOpenLeaderboards) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = t("อันดับปราบบอส", "Boss Leaderboard", "讨伐榜"), tint = PtSoftGold)
                    }
                }
            }

            items(GameCatalog.bosses, key = { it.id }) { boss ->
                val frameSpec = boss.rewardFrameId?.let { GameCatalog.getFrameById(it) }
                PtGlassCard(
                    borderColors = listOf(Color(boss.primaryHex), Color(boss.secondaryHex)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(66.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Brush.linearGradient(listOf(Color(boss.primaryHex), Color(boss.secondaryHex))))
                            ) {
                                Text(text = boss.emoji, fontSize = 36.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = boss.name(lang),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = boss.subtitle(lang),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "❤️ HP ${boss.maxHp} • ${boss.subject.title(lang)} • +${boss.rewardXp} XP & +${boss.rewardPt} PT",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(boss.primaryHex),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (frameSpec != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    PlayerAvatarBadge(
                                        avatarIndex = user.avatarIndex,
                                        size = 44.dp,
                                        frameId = frameSpec.id
                                    )
                                    Text(
                                        text = t("รางวัลกรอบ!", "Trophy!", "限定框！"),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PtRosePink
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        PtGradientButton(
                            text = t("⚔️ เข้าท้าดวล ${boss.nameTh}!", "⚔️ Challenge ${boss.nameEn}!", "⚔️ 立即讨伐 ${boss.nameZh}！"),
                            onClick = { activeBoss = boss },
                            gradientColors = listOf(Color(boss.primaryHex), Color(boss.secondaryHex)),
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "challenge_boss_${boss.id}"
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    } else {
        val boss = activeBoss!!
        val bossQuestions: List<QuestionEntity> = remember(boss.id, allQuestions) {
            val subjectMc = allQuestions.filter {
                it.subjectId == boss.subject.id &&
                    it.reviewStatus == "APPROVED" &&
                    it.correctAnswer.trim().uppercase() in setOf("A", "B", "C", "D")
            }.shuffled()
            if (subjectMc.size >= 6) subjectMc else {
                allQuestions.filter { it.correctAnswer.trim().uppercase() in setOf("A", "B", "C", "D") }.shuffled().take(10)
            }
        }

        var bossHp by remember(boss.id) { mutableIntStateOf(boss.maxHp) }
        var heartsLeft by remember(boss.id) { mutableIntStateOf(boss.playerHearts) }
        var combo by remember(boss.id) { mutableIntStateOf(0) }
        var maxCombo by remember(boss.id) { mutableIntStateOf(0) }
        var qIndex by remember(boss.id) { mutableIntStateOf(0) }
        var lastBattleLog by remember(boss.id) { mutableStateOf("") }
        var rewardRecorded by remember(boss.id) { mutableStateOf(false) }

        val isVictory = bossHp <= 0
        val isDefeat = heartsLeft <= 0 && !isVictory
        val currentQ = bossQuestions.getOrNull(qIndex % bossQuestions.size.coerceAtLeast(1))

        val hpRatio by animateFloatAsState(
            targetValue = (bossHp.toFloat() / boss.maxHp.toFloat()).coerceIn(0f, 1f),
            label = "boss_hp_bar"
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = { activeBoss = null }, shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(t("ถอยทัพ", "Retreat", "撤退"))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(boss.playerHearts) { idx ->
                            Icon(
                                imageVector = if (idx < heartsLeft) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (idx < heartsLeft) ShopNeonRed else Color(0xFFCBD5E1),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PtSoftGold.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, PtSoftGold)
                    ) {
                        Text(
                            text = "🔥 Combo ×$combo",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Boss Arena Stage Card
            item {
                PtGlassCard(
                    borderColors = listOf(Color(boss.primaryHex), Color(boss.secondaryHex)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                PlayerAvatarBadge(
                                    avatarIndex = user.avatarIndex,
                                    size = 54.dp,
                                    frameId = user.equippedFrameId
                                )
                                if (equippedPet != null) {
                                    PetCharacterCanvas(pet = equippedPet, reducedMotion = user.reducedMotion, size = 38.dp)
                                }
                            }
                            Text("⚡ VS ⚡", fontWeight = FontWeight.ExtraBold, color = ShopNeonRed, fontSize = 18.sp)
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(74.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(Color(boss.secondaryHex), Color(boss.primaryHex))))
                            ) {
                                Text(text = boss.emoji, fontSize = 40.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(boss.name(lang), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                        Text("HP: ${formatNumber(lang, bossHp.coerceAtLeast(0))} / ${formatNumber(lang, boss.maxHp)}", fontWeight = FontWeight.Bold, color = ShopNeonRed)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { hpRatio },
                            color = ShopNeonRed,
                            trackColor = Color(0xFFFEE2E2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(50))
                        )
                        if (lastBattleLog.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = lastBattleLog,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = PtSkyBlue,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            when {
                isVictory -> {
                    if (!rewardRecorded) {
                        rewardRecorded = true
                        vm.recordBossRaidVictory(boss, heartsLeft, maxCombo)
                    }
                    item {
                        PtGlassCard(
                            borderColors = listOf(PtEmerald, PtSoftGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(22.dp)
                            ) {
                                Text("🏆", fontSize = 54.sp)
                                Text(
                                    text = t("VICTORY! ปราบบอสสำเร็จ!", "VICTORY! Boss Defeated!", "讨伐大获全胜！"),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PtEmerald
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = t(
                                        "Max Combo ×$maxCombo • หัวใจคงเหลือ $heartsLeft ดวง!",
                                        "Max Combo ×$maxCombo • $heartsLeft Heart(s) Left!",
                                        "最高连击 ×$maxCombo • 剩余 $heartsLeft 颗红心！"
                                    ),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                PtGradientButton(
                                    text = t("เลือกบอสตัวถัดไป", "Choose Another Boss", "挑战下一位魔王"),
                                    onClick = { activeBoss = null },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                isDefeat -> {
                    item {
                        PtGlassCard(
                            borderColors = listOf(RainbowCoral, NegativeRedAlert),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(22.dp)
                            ) {
                                Text("🛡️", fontSize = 48.sp)
                                Text(
                                    text = t("เกือบชนะแล้ว! เหลือ HP บอสอีก $bossHp", "So Close! Boss had $bossHp HP left", "差一点就赢了！魔王仅剩 $bossHp HP"),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NegativeRedAlert,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                PtGradientButton(
                                    text = t("🔥 ลุกขึ้นสู้ใหม่อีกครั้ง!", "🔥 Rematch Boss Now!", "🔥 立即重新挑战！"),
                                    onClick = {
                                        bossHp = boss.maxHp
                                        heartsLeft = boss.playerHearts
                                        combo = 0
                                        maxCombo = 0
                                        qIndex++
                                        lastBattleLog = ""
                                    },
                                    gradientColors = listOf(ShopNeonRed, PtRosePink),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                currentQ != null -> {
                    item {
                        val qBody = when (lang) {
                            AppLanguage.TH -> currentQ.questionTh
                            AppLanguage.EN -> currentQ.questionEn
                            AppLanguage.ZH -> currentQ.questionZh
                        }
                        val choices = listOf(
                            "A" to when (lang) { AppLanguage.TH -> currentQ.choiceATh; AppLanguage.EN -> currentQ.choiceAEn; AppLanguage.ZH -> currentQ.choiceAZh },
                            "B" to when (lang) { AppLanguage.TH -> currentQ.choiceBTh; AppLanguage.EN -> currentQ.choiceBEn; AppLanguage.ZH -> currentQ.choiceBZh },
                            "C" to when (lang) { AppLanguage.TH -> currentQ.choiceCTh; AppLanguage.EN -> currentQ.choiceCEn; AppLanguage.ZH -> currentQ.choiceCZh },
                            "D" to when (lang) { AppLanguage.TH -> currentQ.choiceDTh; AppLanguage.EN -> currentQ.choiceDEn; AppLanguage.ZH -> currentQ.choiceDZh }
                        )

                        PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "⚔️ ${t("โจมตีบอสด้วยคำตอบที่ถูกต้อง!", "Strike the Boss with the Right Answer!", "选择正确答案重创魔王！")}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(boss.primaryHex),
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(qBody, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))

                                choices.forEach { (letter, cText) ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = Color(0xFFF8FAFC),
                                        border = BorderStroke(1.5.dp, Color(boss.primaryHex).copy(alpha = 0.45f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable {
                                                val ok = letter.equals(currentQ.correctAnswer.trim(), ignoreCase = true)
                                                if (ok) {
                                                    SoundEngine.playSfx(SfxType.COMBO)
                                                    val nextCombo = combo + 1
                                                    combo = nextCombo
                                                    if (nextCombo > maxCombo) maxCombo = nextCombo
                                                    val petBonus = (equippedPet?.powerLevel ?: 0) / 10
                                                    val dmg = 130 + nextCombo * 30 + petBonus
                                                    bossHp = (bossHp - dmg).coerceAtLeast(0)
                                                    lastBattleLog = tr(
                                                        lang,
                                                        "💥 CRITICAL HIT! โจมตีบอส -$dmg HP! (Combo ×$nextCombo)",
                                                        "💥 CRITICAL HIT! Dealt -$dmg HP! (Combo ×$nextCombo)",
                                                        "💥 暴击命中！对魔王造成 -$dmg 伤害！（连击 ×$nextCombo）"
                                                    )
                                                } else {
                                                    SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                                                    combo = 0
                                                    heartsLeft = (heartsLeft - 1).coerceAtLeast(0)
                                                    lastBattleLog = tr(
                                                        lang,
                                                        "🛡️ พลาดเป้า! เฉลยคือ ${currentQ.correctAnswer} (-1 ❤️)",
                                                        "🛡️ Missed! Correct was ${currentQ.correctAnswer} (-1 ❤️)",
                                                        "🛡️ 攻击落空！正确答案为 ${currentQ.correctAnswer} (-1 ❤️)"
                                                    )
                                                }
                                                qIndex++
                                            }
                                            .testTag("boss_choice_$letter")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(14.dp)
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .size(30.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(boss.primaryHex))
                                            ) {
                                                Text(letter, color = Color.White, fontWeight = FontWeight.ExtraBold)
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(cText, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
