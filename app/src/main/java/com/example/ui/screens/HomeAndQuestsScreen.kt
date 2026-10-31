package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuestDefinitionEntity
import com.example.data.local.UserEntity
import com.example.domain.ScoringAndPetEngine
import com.example.i18n.LocalAppLanguage
import com.example.i18n.PlayerGender
import com.example.i18n.dynamicGreeting
import com.example.i18n.formatDateTime
import com.example.i18n.formatNumber
import com.example.i18n.t
import com.example.i18n.tr
import com.example.ui.components.AnimatedStreakFlameBadge
import com.example.ui.components.AutoSaveIndicatorChip
import com.example.ui.components.CustomTitleBadge
import com.example.ui.components.HttpsPortalBar
import com.example.ui.components.MascotMood
import com.example.ui.components.NegativePtWarningBanner
import com.example.ui.components.PetCharacterCanvas
import com.example.ui.components.PtGlassCard
import com.example.ui.components.PtGradientButton
import com.example.ui.components.PtOwlMascot
import com.example.ui.components.PtPointsCoinBadge
import com.example.ui.components.PtSectionRibbonHeader
import com.example.ui.theme.PtBubblegumPink
import com.example.ui.theme.PtElectricCyan
import com.example.ui.theme.PtEmerald
import com.example.ui.theme.PtLavender
import com.example.ui.theme.PtRosePink
import com.example.ui.theme.PtSkyBlue
import com.example.ui.theme.PtSoftGold
import com.example.ui.theme.RainbowAqua
import com.example.ui.theme.RainbowCoral
import com.example.ui.theme.RainbowIris
import com.example.ui.theme.RainbowMint
import com.example.ui.theme.RainbowOrchid
import com.example.ui.theme.RainbowPeach
import com.example.ui.theme.RainbowSunny
import com.example.ui.theme.ShopNeonRed
import com.example.util.SecurityUtils
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone

private data class QuickMenuTile(
    val route: AppRoute,
    val titleTh: String,
    val titleEn: String,
    val titleZh: String,
    val subTh: String,
    val subEn: String,
    val subZh: String,
    val icon: ImageVector,
    val colors: List<Color>,
    val tag: String,
    val badgeText: String? = null
)

private data class HubCategorySection(
    val emoji: String,
    val titleTh: String,
    val titleEn: String,
    val titleZh: String,
    val subTh: String,
    val subEn: String,
    val subZh: String,
    val ribbonColors: List<Color>,
    val tiles: List<QuickMenuTile>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeDashboardScreen(
    user: UserEntity,
    vm: GameViewModel,
    onNavigate: (AppRoute) -> Unit
) {
    val lang = LocalAppLanguage.current
    val saveState by vm.saveState.collectAsState()
    val notifications by vm.notifications.collectAsState()
    val quests by vm.todaysQuests.collectAsState()
    val questProgress by vm.userQuestProgress.collectAsState()
    val systemConfigs by vm.systemConfigs.collectAsState()
    val mistakes by vm.mistakeNotebookItems.collectAsState()
    val equippedPet = vm.getEquippedPetSpecies()
    val playerGender = remember(user.gender) { PlayerGender.fromCode(user.gender) }

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(user.multiplierExpiresAtMillis) {
        while (user.multiplierExpiresAtMillis > System.currentTimeMillis()) {
            nowMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val xpBaseFactor = remember(systemConfigs) {
        systemConfigs.firstOrNull { it.configKey == "XP_BASE_FACTOR" }?.configValue?.toIntOrNull() ?: 100
    }
    val newsBanner = remember(systemConfigs) {
        systemConfigs.firstOrNull { it.configKey == "NEWS_BANNER" }?.configValue.orEmpty()
    }
    val (_, xpInLevel, xpNeeded) = ScoringAndPetEngine.computeLevelProgress(user.xp, xpBaseFactor)
    val animatedXpProgress by animateFloatAsState(
        targetValue = (xpInLevel.toFloat() / xpNeeded.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f),
        label = "xp_bar"
    )

    val unreadCount = notifications.count { !it.isRead }
    val (greetTitle, greetSub) = remember(lang, user.displayName) {
        dynamicGreeting(lang, user.displayName, SecurityUtils.bangkokHourOfDay())
    }

    val dayKey = SecurityUtils.currentBangkokDateString(nowMillis)
    val cal = remember(nowMillis) {
        Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok")).apply { timeInMillis = nowMillis }
    }
    val weekKey = "${cal.get(Calendar.YEAR)}-W${cal.get(Calendar.WEEK_OF_YEAR)}"
    val hasFreeDailySpin = user.lastFreeSpinDateBangkok != dayKey

    val categorySections = remember(mistakes.size, hasFreeDailySpin, user.luckyWheelTickets, user.mysteryBoxesOwned) {
        listOf(
            HubCategorySection(
                emoji = "🎯",
                titleTh = "โซนฝึกสอบ & สมุดอัจฉริยะ",
                titleEn = "Exam & Smart Study Suite",
                titleZh = "模拟考与智能复习专区",
                subTh = "จำลองสอบ 10 วิชา สมุดข้อที่เคยผิด แฟลชการ์ด และ AI Tutor",
                subEn = "10-subject exams, Mistake Notebook, 3D Flashcards & AI Tutor",
                subZh = "10科模拟考、错题本、3D闪卡与AI导师",
                ribbonColors = listOf(PtSkyBlue, RainbowAqua),
                tiles = listOf(
                    QuickMenuTile(
                        AppRoute.EXAM_CENTER, "ศูนย์สอบจริง", "Exam Center", "模拟考中心",
                        "จำลองสอบเข้า ม.1 จับเวลา", "Timed M.1 entrance exam", "限时初一入学模拟",
                        Icons.Default.Quiz, listOf(PtSkyBlue, PtElectricCyan), "nav_exam_center"
                    ),
                    QuickMenuTile(
                        AppRoute.PRACTICE_CENTER, "ฝึกฝนรายวิชา", "Practice Center", "科目练习",
                        "เฉลยละเอียดทันทีทีละข้อ", "Instant step-by-step feedback", "逐题即时解析",
                        Icons.AutoMirrored.Filled.MenuBook, listOf(PtEmerald, RainbowMint), "nav_practice_center"
                    ),
                    QuickMenuTile(
                        AppRoute.SMART_STUDY_HUB, "สมุดทบทวน & แผนสอบ", "Smart Study Hub", "智能温习与计划",
                        "สมุดข้อผิด แฟลชการ์ด & นับถอยหลัง", "Mistakes, Flashcards & Planner", "错题本、3D闪卡与计划",
                        Icons.Default.Psychology, listOf(RainbowIris, PtSkyBlue), "nav_smart_study_hub",
                        badgeText = if (mistakes.isNotEmpty()) "${mistakes.size}" else "NEW"
                    ),
                    QuickMenuTile(
                        AppRoute.AI_TUTOR, "พี่นกฮูก AI Tutor", "AI Owl Tutor", "猫头鹰AI导师",
                        "ถาม-ตอบโจทย์ยากคิดวิเคราะห์ลึก", "Multi-turn High-Thinking tutor", "多轮深度推理辅导",
                        Icons.Default.AutoAwesome, listOf(Color(0xFF6366F1), PtSkyBlue), "nav_ai_tutor"
                    ),
                    QuickMenuTile(
                        AppRoute.STUDY_SHEETS, "คลังชีทสรุป A4", "Study Sheets", "A4智能笔记库",
                        "สร้างชีทสรุป+ควิซจากรูป/ข้อความ", "Multimodal A4 sheet & quiz", "图文生成A4总结与测验",
                        Icons.Default.Description, listOf(Color(0xFF0EA5E9), Color(0xFF38BDF8)), "nav_study_sheets"
                    )
                )
            ),
            HubCategorySection(
                emoji = "🎡",
                titleTh = "โซนลุ้นโชค & ท้าดวลบอส",
                titleEn = "Boss Raid & Lucky Plaza",
                titleZh = "魔王讨伐与幸运广场",
                subTh = "ดวลบอส 5 วิชาชิงกรอบอวตาร หมุนวงล้อรายวัน & เปิดหีบสมบัติ!",
                subEn = "Battle 5 Subject Bosses, Daily Lucky Wheel & Mystery Chests!",
                subZh = "挑战5大学科魔王、每日幸运转盘与神秘宝箱！",
                ribbonColors = listOf(RainbowCoral, RainbowSunny),
                tiles = listOf(
                    QuickMenuTile(
                        AppRoute.BOSS_RAID, "ท้าดวลบอส 5 วิชา", "Boss Raid Arena", "五科魔王讨伐战",
                        "โจมตีบอสด้วยคอมโบ ชิงกรอบอวตาร!", "Combo-strike bosses for frames!", "连击讨伐赢限定头像框！",
                        Icons.Default.Whatshot, listOf(ShopNeonRed, RainbowPeach), "nav_boss_raid",
                        badgeText = "HOT"
                    ),
                    QuickMenuTile(
                        AppRoute.DAILY_REWARDS_PLAZA, "วงล้อโชคดี & หีบสมบัติ", "Lucky Wheel & Chests", "幸运转盘与宝箱",
                        "หมุนฟรีทุกวัน & เปิดหีบสุ่มรางวัลใหญ่", "Daily free spin & 3-tier chests", "每日免费转盘与三阶宝箱",
                        Icons.Default.CardGiftcard, listOf(PtRosePink, PtSoftGold), "nav_daily_rewards_plaza",
                        badgeText = if (hasFreeDailySpin) "FREE" else "${user.luckyWheelTickets + user.mysteryBoxesOwned}"
                    ),
                    QuickMenuTile(
                        AppRoute.MINI_GAMES, "มินิเกมลับสมอง (7)", "Mini-Games (7)", "益智小游戏(7)",
                        "Math 24, Connect 4 และอีกเพียบ", "7 brain games for XP & PT", "7款脑力训练赢积分",
                        Icons.Default.SportsEsports, listOf(Color(0xFFF59E0B), PtSoftGold), "nav_mini_games"
                    )
                )
            ),
            HubCategorySection(
                emoji = "🐾",
                titleTh = "โซนร้านค้า สัตว์เลี้ยง & แฟชั่น",
                titleEn = "Shop, 28 Pets & Wardrobe",
                titleZh = "红白商店、28宠与装扮馆",
                subTh = "ร้านแดง-ขาว 18 ไอเทม สุ่มไข่คู่หู 28 สายพันธุ์ & กรอบอวตาร",
                subEn = "18 shop items, 28 companion pets & avatar frames",
                subZh = "18种商店道具、28种伙伴孵化与头像框",
                ribbonColors = listOf(PtRosePink, RainbowOrchid),
                tiles = listOf(
                    QuickMenuTile(
                        AppRoute.PT_SHOP, "ร้านแดง-ขาว (18)", "PT Shop (18)", "红白霓虹商店(18)",
                        "กรอบอวตาร ยาเพิ่มเวลา โล่ไฟ & ตัวคูณ", "Frames, potions, shields & boosts", "头像框、延时药水与护盾",
                        Icons.Default.Storefront, listOf(ShopNeonRed, PtRosePink), "nav_pt_shop"
                    ),
                    QuickMenuTile(
                        AppRoute.PET_ZONE, "PT Pet Zone (28)", "PT Pet Zone (28)", "宠物孵化岛(28)",
                        "เปิดไข่สุ่มคู่หูครบ 28 สายพันธุ์!", "Hatch all 28 companion pets!", "孵化收集全部28种伙伴！",
                        Icons.Default.Pets, listOf(PtLavender, PtBubblegumPink), "nav_pet_zone"
                    ),
                    QuickMenuTile(
                        AppRoute.BACKGROUND_COLLECTION, "พื้นหลัง & กรอบอวตาร", "Themes & Frames", "背景与头像框馆",
                        "สลับฉากโรงเรียน & กรอบโปรไฟล์", "Equip school scenes & frames", "切换学院背景与头像框",
                        Icons.Default.Wallpaper, listOf(PtBubblegumPink, PtLavender), "nav_backgrounds"
                    )
                )
            ),
            HubCategorySection(
                emoji = "📊",
                titleTh = "โซนภารกิจ อันดับ & บัญชี",
                titleEn = "Quests, Rankings & Account",
                titleZh = "任务、排行榜与账号档案",
                subTh = "เควสต์เปลี่ยนใหม่ทุกวัน อันดับ Top 10 กราฟสถิติ & แก้ไขบัญชี 5 ช่อง",
                subEn = "Rotating daily quests, Top 10 boards, charts & 5-field account",
                subZh = "每日轮换任务、排行榜、图表与5项资料编辑",
                ribbonColors = listOf(PtEmerald, PtSkyBlue),
                tiles = listOf(
                    QuickMenuTile(
                        AppRoute.QUESTS, "ภารกิจเปลี่ยนทุกวัน", "Rotating Quests", "每日轮换任务",
                        "สุ่มเควสต์ใหม่ทุกวัน + รีโรลฟรี!", "Fresh daily quests + free reroll!", "每日新任务+免费刷新！",
                        Icons.Default.TaskAlt, listOf(PtEmerald, PtSkyBlue), "nav_quests"
                    ),
                    QuickMenuTile(
                        AppRoute.LEADERBOARDS, "จัดอันดับ Top 10", "Leaderboards", "全服排行榜",
                        "อันดับรวม รายวิชา บอส & มินิเกม", "Overall, Subject, Boss & Games", "总榜、科目、魔王与游戏榜",
                        Icons.Default.EmojiEvents, listOf(PtRosePink, PtSoftGold), "nav_leaderboards"
                    ),
                    QuickMenuTile(
                        AppRoute.PROFILE_STATS, "โปรไฟล์ & กราฟสถิติ", "Profile & Analytics", "档案与深度图表",
                        "ฉายา กราฟข้อถูกรายวัน & บัญชี 5 ช่อง", "Custom title, daily chart & 5 fields", "自定义称号、每日柱状图与账号",
                        Icons.Default.BarChart, listOf(Color(0xFF0284C7), PtElectricCyan), "nav_stats"
                    ),
                    QuickMenuTile(
                        AppRoute.SETTINGS, "ตั้งค่าระบบ & Admin", "Settings & Admin", "系统设置与管理",
                        "3 ภาษา เสียง ความปลอดภัย & Admin", "TH/EN/ZH, audio & Admin Panel", "三语切换、音效与管理后台",
                        Icons.Default.Settings, listOf(Color(0xFF475569), Color(0xFF64748B)), "nav_settings"
                    )
                )
            )
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        val gridColumns = if (maxWidth >= 720.dp) 4 else 2

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 0. Official HTTPS Website Bar (`https://PTPreM1.online/home`)
            item {
                HttpsPortalBar(
                    currentPath = "/home",
                    onJumpToPath = { path -> parseHttpsDeepLinkRoute(path)?.let(onNavigate) },
                    onCopiedToast = { vm.showToast(it) }
                )
            }

            // 1. Top Status Bar: Streak Flame + Streak Shield + AutoSave + PT Points Coin + Notification Bell
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AnimatedStreakFlameBadge(
                            streakDays = user.streakDays,
                            reducedMotion = user.reducedMotion
                        )
                        if (user.streakShieldCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFECFDF5),
                                border = BorderStroke(1.dp, PtEmerald)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = PtEmerald, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${user.streakShieldCount}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PtEmerald
                                    )
                                }
                            }
                        }
                        AutoSaveIndicatorChip(saveState = saveState)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PtPointsCoinBadge(
                            ptPoints = user.ptPoints,
                            reducedMotion = user.reducedMotion,
                            modifier = Modifier.clickable { onNavigate(AppRoute.PT_SHOP) }
                        )
                        IconButton(
                            onClick = {
                                showNotificationsDialog = true
                                vm.markNotificationsAllRead()
                            },
                            modifier = Modifier.testTag("home_notifications_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(containerColor = ShopNeonRed) {
                                            Text("$unreadCount", color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = t("การแจ้งเตือน", "Notifications", "通知"),
                                    tint = PtSkyBlue
                                )
                            }
                        }
                    }
                }
            }

            // Negative PT Points Red Alert Banner (if < 0)
            item {
                NegativePtWarningBanner(ptPoints = user.ptPoints, reducedMotion = user.reducedMotion)
            }

            // Active Shop Multiplier Live Countdown Banner
            if (user.multiplierExpiresAtMillis > nowMillis && user.activeMultiplierGain > 1.0) {
                item {
                    val remainSec = ((user.multiplierExpiresAtMillis - nowMillis) / 1000L).coerceAtLeast(0L)
                    val hrs = remainSec / 3600
                    val mins = (remainSec % 3600) / 60
                    val secs = remainSec % 60
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFFFF1F2),
                        border = BorderStroke(1.5.dp, ShopNeonRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = ShopNeonRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = t(
                                        "ตัวคูณ PT ×${user.activeMultiplierGain} กำลังทำงาน! (หักผิด ×${user.activeMultiplierPenalty})",
                                        "Active PT Multiplier ×${user.activeMultiplierGain}! (Wrong penalty ×${user.activeMultiplierPenalty})",
                                        "PT积分倍率 ×${user.activeMultiplierGain} 生效中！（答错扣分 ×${user.activeMultiplierPenalty}）"
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = ShopNeonRed,
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Text(
                                    text = t(
                                        "เวลาคงเหลือ: %02d:%02d:%02d".format(hrs, mins, secs),
                                        "Remaining: %02d:%02d:%02d".format(hrs, mins, secs),
                                        "剩余时间：%02d:%02d:%02d".format(hrs, mins, secs)
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF991B1B)
                                )
                            }
                        }
                    }
                }
            }

            // Optional News Banner from Admin Config
            if (newsBanner.isNotBlank()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.90f),
                        border = BorderStroke(1.dp, PtSkyBlue.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📢 $newsBanner",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF0369A1),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // 2. Rainbow Pastel Hero Greeting + Avatar Frame + Custom Title + Level XP Bar + Active Pet Companion
            item {
                PtGlassCard(
                    borderColors = listOf(RainbowAqua, RainbowCoral, RainbowSunny, RainbowIris),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PlayerAvatarBadge(
                                avatarIndex = user.avatarIndex,
                                size = 58.dp,
                                frameId = user.equippedFrameId,
                                modifier = Modifier.clickable { onNavigate(AppRoute.PROFILE_STATS) }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = playerGender.primaryColor.copy(alpha = 0.14f)
                                    ) {
                                        Text(
                                            text = "${playerGender.emoji} ${playerGender.label(lang)}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = playerGender.primaryColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    CustomTitleBadge(
                                        customTitle = user.customTitle,
                                        styleIndex = user.titleColorIndex
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = greetTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = greetSub,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            PtOwlMascot(
                                mood = MascotMood.IDLE_WAVE,
                                reducedMotion = user.reducedMotion,
                                size = 68.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Level & Animated XP Progress Bar (Strictly XP only — separate from PT Points!)
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = PtSkyBlue.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = "⭐ Level ${formatNumber(lang, user.level)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PtSkyBlue,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "🎯 ${user.targetDreamSchool} • ${formatNumber(lang, xpInLevel)} / ${formatNumber(lang, xpNeeded)} XP",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { animatedXpProgress },
                            color = PtRosePink,
                            trackColor = Color(0xFFE0F2FE),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Equipped Pet Companion Mini Showcase
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, PtBubblegumPink.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(if (equippedPet != null) AppRoute.PET_INVENTORY else AppRoute.PET_ZONE) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                if (equippedPet != null) {
                                    PetCharacterCanvas(
                                        pet = equippedPet,
                                        reducedMotion = user.reducedMotion,
                                        size = 52.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${t("คู่หูที่ติดตั้ง", "Equipped Pet", "当前出战伙伴")}: ${equippedPet.name(lang)}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                        Text(
                                            text = "✨ ${equippedPet.skillText(lang)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PtRosePink
                                        )
                                    }
                                } else {
                                    Icon(Icons.Default.Pets, contentDescription = null, tint = PtBubblegumPink, modifier = Modifier.size(34.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = t("ยังไม่ได้ติดตั้งสัตว์เลี้ยงคู่หู (มี 28 สายพันธุ์!)", "No Companion Pet Equipped (28 Species!)", "尚未装备宠物伙伴（共28种！）"),
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                        Text(
                                            text = t("แตะเพื่อไปเปิดไข่สุ่มใน PT Pet Zone (500 PT)!", "Tap to hatch a companion in PT Pet Zone!", "点击前往宠物岛孵化伙伴！"),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Hero CTA: Start Exam Immediately
                        PtGradientButton(
                            text = t("🚀 เริ่มทำข้อสอบเตรียมเข้า ม.1 เลย!", "🚀 Start M.1 Entrance Practice Now!", "🚀 立即开始初一模拟特训！"),
                            onClick = { onNavigate(AppRoute.SUBJECT_HUB) },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "home_primary_start_exam_button"
                        )
                    }
                }
            }

            // 3. Organized 4-Zone Rainbow Pastel Command Hub!
            categorySections.forEach { section ->
                item(key = "sec_${section.titleEn}") {
                    PtSectionRibbonHeader(
                        emoji = section.emoji,
                        title = tr(lang, section.titleTh, section.titleEn, section.titleZh),
                        subtitle = tr(lang, section.subTh, section.subEn, section.subZh),
                        colors = section.ribbonColors
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        maxItemsInEachRow = gridColumns,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        section.tiles.forEach { tile ->
                            PtGlassCard(
                                borderColors = tile.colors.map { it.copy(alpha = 0.55f) },
                                onClick = { onNavigate(tile.route) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag(tile.tag)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(Brush.linearGradient(tile.colors))
                                        ) {
                                            Icon(tile.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                        }
                                        if (!tile.badgeText.isNullOrBlank()) {
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = tile.colors.first()
                                            ) {
                                                Text(
                                                    text = tile.badgeText,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = tr(lang, tile.titleTh, tile.titleEn, tile.titleZh),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = tr(lang, tile.subTh, tile.subEn, tile.subZh),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Today's Rotating Daily Quests Preview
            item {
                PtSectionRibbonHeader(
                    emoji = "🌟",
                    title = t("ภารกิจประจำวันนี้ (เปลี่ยนใหม่ทุกวัน!)", "Today's Rotating Quests", "今日轮换任务"),
                    subtitle = t("ทำภารกิจรับ XP & PT Points เพิ่มทุกวัน", "Complete quests for bonus XP & PT Points", "完成每日任务赢取额外经验与积分"),
                    colors = listOf(PtEmerald, PtSkyBlue),
                    trailingContent = {
                        TextButton(onClick = { onNavigate(AppRoute.QUESTS) }) {
                            Text(t("ดูทั้งหมด", "See All", "全部"), fontWeight = FontWeight.ExtraBold)
                        }
                    }
                )
            }

            items(quests.take(3), key = { it.id }) { q ->
                val pKey = if (q.questScope == "WEEKLY") weekKey else dayKey
                val prog = questProgress.firstOrNull { it.questId == q.id && it.periodKey == pKey }
                QuestItemCard(
                    quest = q,
                    currentCount = prog?.currentCount ?: 0,
                    isClaimed = prog?.isClaimed == true,
                    onClaim = { vm.claimQuest(q, pKey) }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = t("ศูนย์การแจ้งเตือน", "Notification Center", "消息通知中心"),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (notifications.isEmpty()) {
                    Text(t("ยังไม่มีข้อความแจ้งเตือนใหม่", "No notifications yet.", "暂无新通知。"))
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(300.dp)
                    ) {
                        items(notifications, key = { it.id }) { n ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = n.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = formatDateTime(lang, n.createdAtMillis),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text(t("ปิด", "Close", "关闭"))
                }
            }
        )
    }
}

@Composable
fun QuestItemCard(
    quest: QuestDefinitionEntity,
    currentCount: Int,
    isClaimed: Boolean,
    onClaim: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val readyToClaim = currentCount >= quest.targetCount && !isClaimed
    val ratio = (currentCount.toFloat() / quest.targetCount.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (quest.questScope == "WEEKLY") PtLavender.copy(alpha = 0.16f) else PtSkyBlue.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = if (quest.questScope == "WEEKLY") t("รายสัปดาห์", "WEEKLY", "每周") else t("รายวัน", "DAILY", "每日"),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (quest.questScope == "WEEKLY") PtLavender else PtSkyBlue,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tr(lang, quest.titleTh, quest.titleEn, quest.titleZh),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tr(lang, quest.descTh, quest.descEn, quest.descZh),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "+${quest.rewardXp} XP",
                        color = PtRosePink,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "+${quest.rewardPt} PT",
                        color = PtSkyBlue,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    LinearProgressIndicator(
                        progress = { ratio },
                        color = if (readyToClaim || isClaimed) PtEmerald else PtSkyBlue,
                        trackColor = Color(0xFFE2E8F0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currentCount / ${quest.targetCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                when {
                    isClaimed -> {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PtEmerald.copy(alpha = 0.14f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PtEmerald, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(t("รับแล้ว", "Claimed", "已领取"), color = PtEmerald, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    readyToClaim -> {
                        PtGradientButton(
                            text = t("รับรางวัล!", "Claim!", "领取！"),
                            onClick = onClaim,
                            gradientColors = listOf(PtEmerald, PtSkyBlue),
                            testTag = "claim_quest_${quest.id}"
                        )
                    }
                    else -> {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                Text(t("กำลังทำ", "In Progress", "进行中"), color = Color(0xFF64748B), style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestsAndChallengesScreen(
    vm: GameViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val quests by vm.todaysQuests.collectAsState()
    val progress by vm.userQuestProgress.collectAsState()
    val user by vm.currentUser.collectAsState()
    val nowMillis = System.currentTimeMillis()
    val dayKey = SecurityUtils.currentBangkokDateString(nowMillis)
    val cal = remember(nowMillis) {
        Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok")).apply { timeInMillis = nowMillis }
    }
    val weekKey = "${cal.get(Calendar.YEAR)}-W${cal.get(Calendar.WEEK_OF_YEAR)}"

    val dailyList = quests.filter { it.questScope != "WEEKLY" }
    val weeklyList = quests.filter { it.questScope == "WEEKLY" }
    val alreadyRerolledToday = user?.dailyQuestRerollDateBangkok == dayKey && (user?.dailyQuestRerollOffset ?: 0) > 0

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        item {
            HttpsPortalBar(
                currentPath = "/quests",
                onCopiedToast = { vm.showToast(it) }
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                }
                Text(
                    text = t("ภารกิจรายวัน & ความท้าทายรายสัปดาห์", "Daily Quests & Weekly Challenges", "每日轮换任务与每周挑战"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        item {
            PtSectionRibbonHeader(
                emoji = "🌟",
                title = t("ภารกิจประจำวัน (สุ่มชุดใหม่ทุกวันจากคลัง 18 ภารกิจ)", "Daily Rotating Quests (5 of 18 Pool)", "每日轮换任务（18选5每日更新）"),
                subtitle = t("รีเซ็ตอัตโนมัติตามเวลาไทย (Asia/Bangkok)", "Auto-rotates daily on Asia/Bangkok time", "按曼谷时间每日自动轮换"),
                colors = listOf(PtSkyBlue, PtEmerald),
                trailingContent = {
                    OutlinedButton(
                        onClick = { vm.rerollDailyQuests() },
                        enabled = !alreadyRerolledToday,
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("reroll_daily_quests_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (alreadyRerolledToday) t("รีโรลแล้ว", "Used", "已刷新") else t("สุ่มใหม่ฟรี", "Reroll", "免费刷新"),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
        items(dailyList, key = { "d_${it.id}" }) { q ->
            val prog = progress.firstOrNull { it.questId == q.id && it.periodKey == dayKey }
            QuestItemCard(
                quest = q,
                currentCount = prog?.currentCount ?: 0,
                isClaimed = prog?.isClaimed == true,
                onClaim = { vm.claimQuest(q, dayKey) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            PtSectionRibbonHeader(
                emoji = "🏆",
                title = t("ความท้าทายประจำสัปดาห์ (Weekly Challenges)", "Weekly Challenges", "每周挑战"),
                subtitle = t("รางวัลใหญ่ประจำสัปดาห์สำหรับผู้พิชิต ม.1", "High-value weekly rewards", "每周高额冲刺奖励"),
                colors = listOf(PtLavender, PtRosePink)
            )
        }
        items(weeklyList, key = { "w_${it.id}" }) { q ->
            val prog = progress.firstOrNull { it.questId == q.id && it.periodKey == weekKey }
            QuestItemCard(
                quest = q,
                currentCount = prog?.currentCount ?: 0,
                isClaimed = prog?.isClaimed == true,
                onClaim = { vm.claimQuest(q, weekKey) }
            )
        }
    }
}
