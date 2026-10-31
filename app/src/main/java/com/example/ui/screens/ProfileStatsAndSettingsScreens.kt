package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.PlayerGender
import com.example.i18n.formatDateTime
import com.example.i18n.formatNumber
import com.example.i18n.t
import com.example.i18n.tr
import com.example.model.GameCatalog
import com.example.model.SubjectId
import com.example.ui.components.AnimatedStreakFlameBadge
import com.example.ui.components.CustomTitleBadge
import com.example.ui.components.DecoratedGenderSelector
import com.example.ui.components.HttpsPortalBar
import com.example.ui.components.NegativePtWarningBanner
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
import com.example.ui.theme.RainbowAqua
import com.example.ui.theme.RainbowCoral
import com.example.ui.theme.RainbowIris
import com.example.ui.theme.RainbowMint
import com.example.ui.theme.RainbowOrchid
import com.example.ui.theme.RainbowSunny
import com.example.util.SecurityUtils
import com.example.viewmodel.GameViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BackgroundCollectionScreen(
    user: UserEntity,
    vm: GameViewModel,
    onGoShop: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    val owned by vm.ownedBackgrounds.collectAsState()
    val ownedFrames by vm.ownedFrames.collectAsState()
    val ownedSet = remember(owned) { (owned.map { it.backgroundId } + "BG_DEFAULT").toSet() }
    val ownedFrameSet = remember(ownedFrames) { (ownedFrames.map { it.frameId } + "FRAME_DEFAULT").toSet() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        item {
            HttpsPortalBar(
                currentPath = "/wardrobe",
                onCopiedToast = { vm.showToast(it) }
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                }
                Icon(Icons.Default.Wallpaper, contentDescription = null, tint = PtRosePink)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = t("คลังพื้นหลัง & กรอบอวตาร", "Backgrounds & Avatar Frames", "学院背景与头像框收藏"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Section A: 10 Avatar Frames Wardrobe
        item {
            PtSectionRibbonHeader(
                emoji = "👑",
                title = t("กรอบอวตารทั้ง ${GameCatalog.avatarFrames.size} แบบ (${ownedFrameSet.size}/${GameCatalog.avatarFrames.size})", "Avatar Frames (${ownedFrameSet.size}/${GameCatalog.avatarFrames.size})", "专属头像框（${ownedFrameSet.size}/${GameCatalog.avatarFrames.size}）"),
                subtitle = t("ปลดล็อกจากร้านแดง-ขาว หีบสมบัติ และโหมดท้าดวลบอส!", "Unlock via PT Shop, Mystery Chests & Boss Raids!", "通过商店、神秘宝箱或讨伐魔王解锁！"),
                colors = listOf(RainbowCoral, RainbowIris)
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                GameCatalog.avatarFrames.forEach { frame ->
                    val frameUnlocked = frame.id in ownedFrameSet
                    val frameActive = user.equippedFrameId == frame.id
                    PtGlassCard(
                        borderColors = if (frameActive) listOf(PtEmerald, PtSkyBlue) else frame.colorsHex.map { Color(it).copy(alpha = 0.55f) }.let { if (it.size < 2) it + it else it },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            PlayerAvatarBadge(
                                avatarIndex = user.avatarIndex,
                                size = 58.dp,
                                frameId = frame.id
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = frame.name(lang),
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = frame.unlockHint(lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            when {
                                frameActive -> {
                                    Surface(shape = RoundedCornerShape(12.dp), color = PtEmerald.copy(alpha = 0.14f)) {
                                        Text(
                                            text = t("✅ กำลังสวมใส่", "✅ Equipped", "✅ 使用中"),
                                            color = PtEmerald,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                                frameUnlocked -> {
                                    PtGradientButton(
                                        text = t("สวมใส่", "Equip", "佩戴"),
                                        onClick = {
                                            vm.updateCosmetics(
                                                customTitle = user.customTitle,
                                                titleColorIndex = user.titleColorIndex,
                                                equippedFrameId = frame.id,
                                                avatarIndex = user.avatarIndex
                                            )
                                        }
                                    )
                                }
                                else -> {
                                    OutlinedButton(onClick = onGoShop, shape = RoundedCornerShape(12.dp)) {
                                        Text(t("ดูวิธีรับ/ร้านค้า", "Unlock", "去获取"), fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section B: Background Collection
        item {
            PtSectionRibbonHeader(
                emoji = "🌈",
                title = t("ฉากพื้นหลังโรงเรียนในฝัน", "Dream School Backgrounds", "梦幻学院场景背景"),
                subtitle = t("เปลี่ยนบรรยากาศของโลกเกมได้ทันที", "Switch your active world backdrop anytime", "随时切换全屏梦幻背景"),
                colors = listOf(PtSkyBlue, PtRosePink)
            )
        }

        items(GameCatalog.backgrounds, key = { it.id }) { bg ->
            val unlocked = bg.id in ownedSet
            val active = user.activeBackgroundId == bg.id
            PtGlassCard(
                borderColors = if (active) listOf(PtEmerald, PtSkyBlue) else listOf(PtSkyBlue.copy(alpha = 0.4f), PtBubblegumPink.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.verticalGradient(listOf(Color(bg.topColorHex), Color(bg.bottomColorHex))))
                    ) {
                        if (bg.drawableRes != null) {
                            Image(
                                painter = painterResource(id = bg.drawableRes),
                                contentDescription = bg.name(lang),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("☁️ PT's Rainbow Pastel Sky ✨", fontWeight = FontWeight.ExtraBold, color = PtSkyBlue)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(bg.name(lang), fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                        when {
                            active -> {
                                OutlinedButton(
                                    onClick = { if (bg.id != "BG_DEFAULT") vm.equipBackground("BG_DEFAULT") },
                                    enabled = bg.id != "BG_DEFAULT",
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PtEmerald, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (bg.id == "BG_DEFAULT") t("กำลังใช้งาน", "Active", "使用中") else t("ถอดพื้นหลัง", "Unequip", "取消装备"))
                                }
                            }
                            unlocked -> {
                                PtGradientButton(
                                    text = t("ติดตั้งพื้นหลัง", "Equip", "立即使用"),
                                    onClick = { vm.equipBackground(bg.id) }
                                )
                            }
                            else -> {
                                OutlinedButton(onClick = onGoShop, shape = RoundedCornerShape(14.dp)) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = PtRosePink, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(t("แลกที่ร้านค้า (75 PT)", "Unlock in Shop (75 PT)", "前往商店解锁(75 PT)"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileAndStatsScreen(
    user: UserEntity,
    vm: GameViewModel,
    onOpenSettings: () -> Unit,
    onOpenBackgrounds: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    val completedExams by vm.completedExams.collectAsState()
    val achievementDefs by vm.achievementDefs.collectAsState()
    val userAchievements by vm.userAchievements.collectAsState()
    val transactions by vm.userTransactions.collectAsState()
    val pets by vm.userPets.collectAsState()
    val ownedFrames by vm.ownedFrames.collectAsState()
    val dailyChart14 by vm.dailyCorrectChart14Days.collectAsState()

    val ownedFrameIds = remember(ownedFrames) { (ownedFrames.map { it.frameId } + "FRAME_DEFAULT").toSet() }

    // Cosmetics State (Custom Printable Title + Style + Frame + Avatar)
    var editingTitle by rememberSaveable(user.customTitle) { mutableStateOf(user.customTitle) }
    var editingTitleStyle by rememberSaveable(user.titleColorIndex) { mutableIntStateOf(user.titleColorIndex) }
    var editingFrameId by rememberSaveable(user.equippedFrameId) { mutableStateOf(user.equippedFrameId) }
    var editingAvatar by rememberSaveable(user.avatarIndex) { mutableIntStateOf(user.avatarIndex) }

    // All 5 Account Fields Editable State!
    var editingGenderCode by rememberSaveable(user.gender) { mutableStateOf(user.gender) }
    var editingUsername by rememberSaveable(user.username) { mutableStateOf(user.username) }
    var editingDisplayName by rememberSaveable(user.displayName) { mutableStateOf(user.displayName) }
    var editingNewPassword by rememberSaveable { mutableStateOf("") }
    var editingRecoveryCode by rememberSaveable(user.recoveryCodePlain) {
        mutableStateOf(user.recoveryCodePlain.ifBlank { SecurityUtils.generateRecoveryCode() })
    }
    var showPasswordPlain by rememberSaveable { mutableStateOf(false) }
    var showChart14Days by rememberSaveable { mutableStateOf(false) }

    val selectedGender = PlayerGender.fromCode(editingGenderCode)
    val totalAnswered = user.totalCorrect + user.totalWrong
    val overallAccuracy = if (totalAnswered > 0) ((user.totalCorrect * 100.0) / totalAnswered).roundToInt() else 0
    val avgSecPerQuestion = if (user.totalQuestionsAnswered > 0) {
        user.totalAnswerTimeSeconds.toDouble() / user.totalQuestionsAnswered.toDouble()
    } else 0.0
    val distinctPets = pets.map { it.speciesId }.distinct().size
    val m1ReadinessScore = (
        (overallAccuracy * 0.55) +
            (user.totalExamsCompleted.coerceAtMost(20) * 1.5) +
            (user.streakDays.coerceAtMost(15) * 1.0)
        ).roundToInt().coerceIn(0, 100)

    val unlockedCodes = remember(userAchievements) { userAchievements.map { it.achievementCode }.toSet() }
    val currentTier = GameCatalog.getStreakTier(user.streakDays)
    val displayedBars = remember(dailyChart14, showChart14Days) {
        if (showChart14Days) dailyChart14 else dailyChart14.takeLast(7)
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
    ) {
        item {
            HttpsPortalBar(
                currentPath = "/@${user.username}",
                shareMessageExtra = tr(
                    lang,
                    "โปรไฟล์ของ ${user.displayName} (@${user.username}) • Lv.${user.level} • ความแม่นยำ $overallAccuracy% บน PT's มาสอบเข้า ม.1!",
                    "${user.displayName} (@${user.username}) • Lv.${user.level} • $overallAccuracy% Accuracy on PT's Pre-M.1!",
                    "${user.displayName} (@${user.username}) • Lv.${user.level} • 正确率 $overallAccuracy% — PT's 初一冲刺！"
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                    }
                    Text(
                        text = t("โปรไฟล์ บัญชี 5 ช่อง & สถิติเชิงลึก", "Profile, 5-Field Account & Deep Stats", "个人档案、五项资料与深度统计"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                IconButton(onClick = onOpenSettings, modifier = Modifier.testTag("profile_open_settings_btn")) {
                    Icon(Icons.Default.Settings, contentDescription = t("ตั้งค่า", "Settings", "设置"), tint = PtSkyBlue)
                }
            }
        }

        item {
            NegativePtWarningBanner(ptPoints = user.ptPoints, reducedMotion = user.reducedMotion)
        }

        // 1. Player Identity + Custom Printable Title & Avatar Frame Studio
        item {
            PtSectionRibbonHeader(
                emoji = "👑",
                title = t("ฉายาผู้เล่นที่พิมพ์ได้ & กรอบอวตาร", "Custom Printable Title & Avatar Frames", "自定义玩家称号与专属头像框"),
                subtitle = t("พิมพ์ฉายาเท่ๆ ของคุณเอง เลือกสีป้าย และสวมกรอบอวตาร!", "Type any custom title, pick a rainbow badge & equip frames!", "自由输入专属称号、选择渐变色并穿戴头像框！"),
                colors = listOf(RainbowOrchid, PtSkyBlue)
            )
            Spacer(modifier = Modifier.height(8.dp))
            PtGlassCard(
                borderColors = listOf(RainbowOrchid, RainbowAqua, RainbowCoral),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PlayerAvatarBadge(
                            avatarIndex = editingAvatar,
                            size = 70.dp,
                            frameId = editingFrameId
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("@${user.username}", style = MaterialTheme.typography.labelLarge, color = PtSkyBlue, fontWeight = FontWeight.ExtraBold)
                                Surface(shape = RoundedCornerShape(50), color = selectedGender.primaryColor.copy(alpha = 0.14f)) {
                                    Text(
                                        text = "${selectedGender.emoji} ${selectedGender.label(lang)}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = selectedGender.primaryColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(user.displayName, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                            if (editingTitle.isNotBlank()) {
                                Spacer(modifier = Modifier.height(3.dp))
                                CustomTitleBadge(customTitle = editingTitle, styleIndex = editingTitleStyle)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AnimatedStreakFlameBadge(streakDays = user.streakDays, reducedMotion = user.reducedMotion)
                                PtPointsCoinBadge(ptPoints = user.ptPoints, reducedMotion = user.reducedMotion)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editingTitle,
                        onValueChange = { if (it.length <= 28) editingTitle = it },
                        label = { Text(t("ฉายาผู้เล่นของคุณ (พิมพ์เองได้สูงสุด 28 ตัวอักษร)", "Custom Player Title (Type up to 28 chars)", "自定义玩家称号（最多28字）")) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_custom_title_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(GameCatalog.presetTitles(lang)) { preset ->
                            FilterChip(
                                selected = editingTitle == preset,
                                onClick = { editingTitle = preset },
                                label = { Text("✨ $preset", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(t("เลือกโทนสีป้ายฉายา:", "Title Badge Gradient:", "称号徽章渐变色："), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(GameCatalog.titleStyles) { style ->
                            val isSelected = editingTitleStyle == style.index
                            Surface(
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(if (isSelected) 2.5.dp else 1.dp, if (isSelected) Color(0xFF0F172A) else Color.White),
                                modifier = Modifier.clickable { editingTitleStyle = style.index }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Brush.horizontalGradient(listOf(Color(style.startHex), Color(style.endHex))))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(style.name(lang), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(t("เลือกกรอบอวตารที่ปลดล็อกแล้ว:", "Equipped Avatar Frame:", "选择已解锁头像框："), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(GameCatalog.avatarFrames.filter { it.id in ownedFrameIds }) { frame ->
                            val active = editingFrameId == frame.id
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (active) PtSkyBlue.copy(alpha = 0.12f) else Color.Transparent)
                                    .clickable { editingFrameId = frame.id }
                                    .padding(6.dp)
                            ) {
                                PlayerAvatarBadge(avatarIndex = editingAvatar, size = 46.dp, frameId = frame.id)
                                Text(frame.badgeEmoji, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = onOpenBackgrounds,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Wallpaper, contentDescription = null, tint = PtSkyBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(t("ดูกรอบทั้งหมด", "All Frames", "全部边框"))
                        }
                        PtGradientButton(
                            text = t("บันทึกฉายา & กรอบ", "Save Cosmetics", "保存装扮"),
                            onClick = {
                                vm.updateCosmetics(editingTitle, editingTitleStyle, editingFrameId, editingAvatar)
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "profile_save_cosmetics_button"
                        )
                    }
                }
            }
        }

        // 2. Complete 5-Field Mutable Account Editor ("แล้วทุกอย่างสามารถเปลี่ยนไปได้!")
        item {
            PtSectionRibbonHeader(
                emoji = "🔐",
                title = t("แก้ไขข้อมูลบัญชีครบทั้ง 5 ช่อง (เปลี่ยนได้ทุกอย่าง!)", "Edit All 5 Account Fields Anytime", "随时修改全部5项账号资料"),
                subtitle = t("1.เพศ • 2.ชื่อบัญชี • 3.ชื่อแสดง • 4.รหัสบัญชี • 5.รหัสกู้คืนบัญชี", "1.Gender • 2.Username • 3.Display Name • 4.Password • 5.Recovery Code", "1.性别 • 2.账号名 • 3.显示昵称 • 4.密码 • 5.恢复码"),
                colors = listOf(PtSkyBlue, PtEmerald)
            )
            Spacer(modifier = Modifier.height(8.dp))
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = t("เลือกอวตารประจำตัว (10 แบบ):", "Choose Avatar (10 Styles):", "选择头像（10款）："),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlayerAvatarCatalog.avatars.indices.forEach { idx ->
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .border(if (editingAvatar == idx) 2.5.dp else 0.dp, PtRosePink, CircleShape)
                                    .clickable { editingAvatar = idx }
                                    .padding(2.dp)
                            ) {
                                PlayerAvatarBadge(avatarIndex = idx, size = 38.dp, frameId = editingFrameId)
                            }
                        }
                    }

                    // Field 1: Decorated Gender Selector
                    Text(
                        text = t("1. เพศ (ชาย / หญิง / ไม่ระบุ)", "1. Gender (Male / Female / Unspecified)", "1. 性别（男 / 女 / 不指定）"),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = PtSkyBlue
                    )
                    DecoratedGenderSelector(
                        selectedGender = selectedGender,
                        onSelectGender = { editingGenderCode = it.code }
                    )

                    // Field 2: Account Name (Username)
                    OutlinedTextField(
                        value = editingUsername,
                        onValueChange = { editingUsername = it },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PtSkyBlue) },
                        label = { Text(t("2. ชื่อบัญชี (Username 3–20 ตัวอักษร)", "2. Account Name (Username)", "2. 账号名称 (Username)")) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_edit_username_input")
                    )

                    // Field 3: Display Name
                    OutlinedTextField(
                        value = editingDisplayName,
                        onValueChange = { editingDisplayName = it },
                        leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PtRosePink) },
                        label = { Text(t("3. ชื่อแสดงในเกม (Display Name)", "3. Display Name", "3. 显示昵称")) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_edit_display_name_input")
                    )

                    // Field 4: Account Password (leave blank to keep current)
                    OutlinedTextField(
                        value = editingNewPassword,
                        onValueChange = { editingNewPassword = it },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PtLavender) },
                        trailingIcon = {
                            IconButton(onClick = { showPasswordPlain = !showPasswordPlain }) {
                                Icon(
                                    imageVector = if (showPasswordPlain) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (showPasswordPlain) VisualTransformation.None else PasswordVisualTransformation(),
                        label = {
                            Text(
                                t(
                                    "4. รหัสบัญชีใหม่ (เว้นว่างหากใช้รหัสเดิม)",
                                    "4. New Account Password (blank = keep current)",
                                    "4. 新账号密码（留空则保持原密码）"
                                )
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_edit_password_input")
                    )

                    // Field 5: Account Recovery Code
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = editingRecoveryCode,
                            onValueChange = { editingRecoveryCode = it.uppercase() },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = PtEmerald) },
                            label = { Text(t("5. รหัสกู้คืนบัญชี (Recovery Code)", "5. Account Recovery Code", "5. 账号恢复码")) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_edit_recovery_input")
                        )
                        OutlinedButton(
                            onClick = { editingRecoveryCode = SecurityUtils.generateRecoveryCode() },
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp)
                        ) {
                            Icon(Icons.Default.Casino, contentDescription = t("สุ่มรหัสกู้คืนใหม่", "Randomize", "随机生成"), tint = PtEmerald)
                        }
                    }

                    PtGradientButton(
                        text = t("💾 บันทึกการเปลี่ยนแปลงทั้ง 5 ช่องทันที!", "💾 Save All 5 Account Fields!", "💾 保存全部5项账号资料！"),
                        onClick = {
                            vm.updateFullFiveFieldAccount(
                                gender = selectedGender,
                                newUsername = editingUsername,
                                newDisplayName = editingDisplayName,
                                newPasswordOrBlank = editingNewPassword,
                                newRecoveryCode = editingRecoveryCode,
                                avatarIndex = editingAvatar
                            )
                            editingNewPassword = ""
                        },
                        gradientColors = listOf(PtEmerald, PtSkyBlue),
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "profile_save_five_fields_button"
                    )
                }
            }
        }

        // 3. Deep Analytics + Daily Correct Answers Bar Chart (7 / 14 Days!)
        item {
            PtSectionRibbonHeader(
                emoji = "📊",
                title = t("สถิติเชิงลึก & กราฟแท่งข้อถูกต่อ 1 วัน", "Deep Analytics & Daily Correct Bar Chart", "深度统计与每日答对题数柱状图"),
                subtitle = t("วิเคราะห์จำนวนข้อถูกแต่ละวัน เวลาเฉลี่ยต่อข้อ และความพร้อมสอบ ม.1", "Daily correct bars, avg seconds/question & M.1 readiness", "每日答对趋势、平均用时与初一入学准备度"),
                colors = listOf(RainbowAqua, RainbowIris),
                trailingContent = {
                    FilterChip(
                        selected = showChart14Days,
                        onClick = { showChart14Days = !showChart14Days },
                        label = { Text(if (showChart14Days) t("14 วัน", "14D", "14天") else t("7 วัน", "7D", "7天"), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = t("📈 กราฟแท่งจำนวนข้อที่ตอบถูกต่อ 1 วัน (${displayedBars.size} วันล่าสุด)", "📈 Daily Correct Answers (${displayedBars.size}-Day Bar Chart)", "📈 每日答对题数柱状图（近${displayedBars.size}天）"),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val maxVal = (displayedBars.maxOfOrNull { it.correctCount } ?: 0).coerceAtLeast(5)
                    val barPalette = listOf(RainbowCoral, RainbowSunny, RainbowMint, RainbowAqua, RainbowIris, RainbowOrchid, PtSkyBlue)

                    // Counts row above bars
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        displayedBars.forEach { pt ->
                            Text(
                                text = "${pt.correctCount}",
                                fontSize = if (showChart14Days) 9.sp else 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (pt.correctCount > 0) PtEmerald else Color(0xFF94A3B8),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        val count = displayedBars.size.coerceAtLeast(1)
                        val slotW = size.width / count
                        val barW = (slotW * 0.58f).coerceAtMost(28.dp.toPx())

                        // Subtle horizontal grid lines
                        listOf(0.25f, 0.5f, 0.75f, 1f).forEach { frac ->
                            val y = size.height * frac
                            drawLine(
                                color = Color(0xFFE2E8F0),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        displayedBars.forEachIndexed { idx, pt ->
                            val ratio = (pt.correctCount.toFloat() / maxVal.toFloat()).coerceIn(0.05f, 1f)
                            val barH = size.height * ratio
                            val left = idx * slotW + (slotW - barW) / 2f
                            val top = size.height - barH
                            val c1 = barPalette[idx % barPalette.size]
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    if (pt.correctCount > 0) listOf(c1, PtSkyBlue)
                                    else listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                                ),
                                topLeft = Offset(left, top),
                                size = Size(barW, barH),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        displayedBars.forEach { pt ->
                            Text(
                                text = if (showChart14Days) pt.shortLabel.takeLast(2) else pt.shortLabel,
                                fontSize = if (showChart14Days) 9.sp else 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Deep Analytics Grid (8 Key Metrics!)
                    FlowRow(
                        maxItemsInEachRow = 2,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            Triple(t("ความแม่นยำรวม", "Overall Accuracy", "总正确率"), "$overallAccuracy%", PtEmerald),
                            Triple(t("เวลาเฉลี่ยต่อข้อ", "Avg Time / Question", "平均每题用时"), "%.1f ${t("วินาที", "sec", "秒")}".format(avgSecPerQuestion), PtSkyBlue),
                            Triple(t("ดัชนีพร้อมสอบ ม.1", "M.1 Readiness", "初一准备度指数"), "$m1ReadinessScore / 100", PtRosePink),
                            Triple(t("คู่หูที่สะสมครบ", "Unique Pets", "已收集伙伴"), "$distinctPets / ${GameCatalog.pets.size}", PtLavender),
                            Triple(t("ปราบบอสสำเร็จ", "Bosses Defeated", "已击败魔王"), "${user.bossesDefeatedCount}", RainbowCoral),
                            Triple(t("พิชิตข้อที่เคยผิด", "Mistakes Mastered", "已攻克错题"), "${user.mistakesMasteredCount}", RainbowIris),
                            Triple(t("ทบทวนแฟลชการ์ด", "Cards Reviewed", "已复习闪卡"), "${user.flashcardsReviewedCount}", PtSoftGold),
                            Triple(t("โล่ป้องกันไฟ Streak", "Streak Shields", "连胜护盾"), "🛡️ ${user.streakShieldCount}", PtEmerald)
                        ).forEach { (label, valueText, accent) ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(valueText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = accent)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "🔥 ${t("ระดับเปลวไฟปัจจุบัน", "Current Streak Flame Tier", "当前连胜火焰阶级")}: ${currentTier.label(lang)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PtRosePink
                    )
                }
            }
        }

        // Subject Accuracy Breakdown
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(t("📚 วิเคราะห์ความแม่นยำแยกรายวิชา (10 วิชา)", "📚 10-Subject Mastery Breakdown", "📚 10科掌握度深度分析"), fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    SubjectId.entries.forEach { sub ->
                        val examsForSub = completedExams.filter { it.subjectId == sub.id }
                        val avgAcc = if (examsForSub.isNotEmpty()) examsForSub.map { it.accuracyPercent }.average().roundToInt() else 0
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(sub.title(lang), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                                Text("$avgAcc% (${examsForSub.size} ${t("ชุด", "sets", "套")})", style = MaterialTheme.typography.labelSmall)
                            }
                            LinearProgressIndicator(
                                progress = { avgAcc / 100f },
                                color = sub.color,
                                trackColor = Color(0xFFE2E8F0),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50))
                            )
                        }
                    }
                }
            }
        }

        // Achievements Showcase (All 34 Badges!)
        item {
            PtSectionRibbonHeader(
                emoji = "🏅",
                title = t("เหรียญตราความสำเร็จ (${unlockedCodes.size}/${achievementDefs.size})", "Achievements (${unlockedCodes.size}/${achievementDefs.size})", "成就徽章（${unlockedCodes.size}/${achievementDefs.size}）"),
                colors = listOf(PtSoftGold, PtRosePink)
            )
        }
        items(achievementDefs, key = { it.code }) { ach ->
            val unlocked = ach.code in unlockedCodes
            PtGlassCard(
                borderColors = if (unlocked) listOf(PtSoftGold, PtRosePink) else listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(14.dp)) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = if (unlocked) PtSoftGold else Color(0xFFCBD5E1),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tr(lang, ach.titleTh, ach.titleEn, ach.titleZh), fontWeight = FontWeight.Bold)
                        Text(tr(lang, ach.descTh, ach.descEn, ach.descZh), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        text = if (unlocked) "✅" else "🔒",
                        fontSize = 18.sp
                    )
                }
            }
        }

        // Recent XP & PT Points Ledger
        if (transactions.isNotEmpty()) {
            item {
                Text(
                    text = t("📜 ประวัติธุรกรรม XP & PT Points ล่าสุด", "📜 Recent XP & PT Points Ledger", "📜 最近积分与经验流水"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            items(transactions.take(10), key = { "tx_${it.id}" }) { tx ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.88f),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${tx.txType} • ${tx.note}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                            Text(formatDateTime(lang, tx.createdAtMillis), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            if (tx.xpDelta != 0) {
                                Text("+${tx.xpDelta} XP", color = PtRosePink, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                            if (tx.ptPointsDelta != 0) {
                                Text(
                                    text = if (tx.ptPointsDelta > 0) "+${tx.ptPointsDelta} PT" else "${tx.ptPointsDelta} PT",
                                    color = if (tx.ptPointsDelta > 0) PtSkyBlue else NegativeRedAlert,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section 16: Complete 13-Section Settings Screen + Admin Entry +
 * Mandatory bottom-most glowing Sky Blue-to-Pink gradient signature: "Made by Pentor!"
 */
@Composable
fun SettingsScreen(
    user: UserEntity,
    vm: GameViewModel,
    onOpenBackgrounds: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current

    var bgmVol by remember(user.bgmVolume) { mutableFloatStateOf(user.bgmVolume) }
    var sfxVol by remember(user.sfxVolume) { mutableFloatStateOf(user.sfxVolume) }
    var oldPin by rememberSaveable { mutableStateOf("") }
    var newPin by rememberSaveable { mutableStateOf("") }
    var reportCategory by rememberSaveable { mutableStateOf("QUESTION_ISSUE") }
    var reportMessage by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
    ) {
        item {
            HttpsPortalBar(
                currentPath = "/settings",
                onCopiedToast = { vm.showToast(it) }
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                }
                Text(
                    text = t("การตั้งค่าระบบ (Settings)", "System Settings", "系统设置"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // 1 & 2. Account & Profile
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(t("บัญชีผู้ใช้ 5 ช่อง & โปรไฟล์", "5-Field Account & Profile", "五项账号资料与档案"), fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Username: @${user.username} • Display: ${user.displayName}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (user.isGuest) t("ประเภทบัญชี: โหมดทดลองเล่น (แก้ไขเป็นบัญชีถาวรได้ที่หน้าโปรไฟล์)", "Account Type: Guest Trial (Editable in Profile)", "账号类型：游客模式（可在档案页修改）")
                        else t("ประเภทบัญชี: สมาชิกถาวรบันทึกอัตโนมัติ", "Account Type: Registered Player", "账号类型：正式玩家"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onOpenProfile, modifier = Modifier.weight(1f)) {
                            Text(t("แก้ไขบัญชี 5 ช่อง", "Edit 5 Fields", "编辑五项资料"))
                        }
                        OutlinedButton(
                            onClick = { vm.logOut() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NegativeRedAlert),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_logout_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(t("ออกจากระบบ", "Log Out", "退出登录"))
                        }
                    }
                }
            }
        }

        // 3. Change Languages (TH / EN / ZH Instant Live Switch!)
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(t("เปลี่ยนภาษา 3 ภาษา (ไทย 🇹🇭 / English 🇬🇧 / 中文 🇨🇳)", "Change Language (TH 🇹🇭 / EN 🇬🇧 / ZH 🇨🇳)", "切换界面语言（泰语 🇹🇭 / 英语 🇬🇧 / 中文 🇨🇳）"), fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        AppLanguage.entries.forEach { option ->
                            FilterChip(
                                selected = lang == option,
                                onClick = { vm.setLanguage(option) },
                                label = { Text("${option.flag} ${option.label}", fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("settings_lang_${option.code}")
                            )
                        }
                    }
                }
            }
        }

        // 4 & 8. Background Collection + Animation & Premium UI Settings
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(t("พื้นหลัง กรอบอวตาร ธีมพรีเมียม & แอนิเมชัน", "Backgrounds, Frames, Premium UI & Motion", "背景、头像框、水晶主题与动画"), fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onOpenBackgrounds, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Wallpaper, contentDescription = null, tint = PtSkyBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t("เปิดคลังพื้นหลัง & กรอบอวตาร", "Open Backgrounds & Frames Wardrobe", "打开背景与头像框收藏馆"))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(t("ธีม UI พรีเมียม (Premium UI Theme)", "Premium Crystal UI Theme", "高级水晶 UI 主题"), fontWeight = FontWeight.SemiBold)
                            Text(
                                if (user.premiumUiUnlocked) t("ปลดล็อกแล้ว สลับเปิด-ปิดได้ทันที", "Unlocked! Toggle anytime", "已解锁，可随时切换")
                                else t("ปลดล็อกได้ที่ร้านแดง-ขาว (50 PT)", "Unlock in PT Shop for 50 PT", "可在红白商店花费50 PT解锁"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = user.premiumUiEnabled,
                            enabled = user.premiumUiUnlocked,
                            onCheckedChange = { vm.togglePremiumUi(it) }
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(t("โหมดลดการเคลื่อนไหว (Reduced Motion)", "Reduced Motion Mode", "减少动态效果模式"), fontWeight = FontWeight.SemiBold)
                            Text(t("ลดการสั่นและพาร์ติเคิลสำหรับผู้ไม่ชอบภาพไหวเร็ว", "Calms shake & particle effects", "减弱震动与粒子特效"), style = MaterialTheme.typography.labelSmall)
                        }
                        Switch(
                            checked = user.reducedMotion,
                            onCheckedChange = { vm.updateReducedMotion(it) },
                            modifier = Modifier.testTag("settings_reduced_motion_switch")
                        )
                    }
                }
            }
        }

        // 5, 6 & 7. Music, Sound Effects & Notifications
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = PtRosePink)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(t("เพลงประกอบ เอฟเฟกต์เสียง & การแจ้งเตือน", "Music, Sound Effects & Notifications", "背景音乐、音效与通知"), fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(t("ปิดเสียงทั้งหมด (Mute All)", "Mute All Audio", "全部静音"))
                        Switch(
                            checked = user.muteAll,
                            onCheckedChange = { vm.updateAudioSettings(user.bgmEnabled, user.sfxEnabled, bgmVol, sfxVol, it) }
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(t("เพลงประกอบฉาก (BGM)", "Background Music (BGM)", "背景音乐"))
                        Switch(
                            checked = user.bgmEnabled,
                            onCheckedChange = { vm.updateAudioSettings(it, user.sfxEnabled, bgmVol, sfxVol, user.muteAll) }
                        )
                    }
                    Text("${t("ความดังเพลงประกอบ", "BGM Volume", "音乐音量")}: ${(bgmVol * 100).roundToInt()}%", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = bgmVol,
                        onValueChange = {
                            bgmVol = it
                            vm.updateAudioSettings(user.bgmEnabled, user.sfxEnabled, it, sfxVol, user.muteAll)
                        }
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(t("เอฟเฟกต์เสียงเกม (SFX)", "Game Sound Effects (SFX)", "游戏音效"))
                        Switch(
                            checked = user.sfxEnabled,
                            onCheckedChange = { vm.updateAudioSettings(user.bgmEnabled, it, bgmVol, sfxVol, user.muteAll) }
                        )
                    }
                    Text("${t("ความดังเอฟเฟกต์", "SFX Volume", "音效音量")}: ${(sfxVol * 100).roundToInt()}%", style = MaterialTheme.typography.labelSmall)
                    Slider(
                        value = sfxVol,
                        onValueChange = {
                            sfxVol = it
                            vm.updateAudioSettings(user.bgmEnabled, user.sfxEnabled, bgmVol, it, user.muteAll)
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(t("การแจ้งเตือนในเกม (Notifications)", "In-App Notifications", "应用内通知"))
                        Switch(
                            checked = user.notificationsEnabled,
                            onCheckedChange = { vm.updateNotificationsEnabled(it) }
                        )
                    }
                }
            }
        }

        // 9 & 10. Privacy & Security Settings (Change Password)
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = PtSkyBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(t("ความปลอดภัย HTTPS & เปลี่ยนรหัสบัญชี", "HTTPS Security & Change Password", "HTTPS安全与修改密码"), fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = t(
                            "เว็บไซต์ทางการ https://PTPreM1.online บังคับ HTTPS แท้ 100% รหัสผ่านเข้ารหัสแบบ Salted SHA-256 และ Leaderboard แสดงเฉพาะชื่อบัญชีกับอวตารเท่านั้น",
                            "Official portal https://PTPreM1.online enforces strict HTTPS. Passwords use Salted SHA-256 hashing.",
                            "官方门户 https://PTPreM1.online 强制纯正 HTTPS，密码采用加盐 SHA-256 加密存储。"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!user.isGuest) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = oldPin,
                            onValueChange = { oldPin = it },
                            label = { Text(t("รหัสบัญชีเดิม", "Current Password", "当前密码")) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPin,
                            onValueChange = { newPin = it },
                            label = { Text(t("รหัสบัญชีใหม่ (4+ ตัวอักษร)", "New Password (4+ chars)", "新密码（至少4位）")) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        PtGradientButton(
                            text = t("อัปเดตรหัสบัญชีใหม่", "Update Password", "确认更新密码"),
                            onClick = {
                                vm.changePinOrPassword(oldPin, newPin)
                                oldPin = ""
                                newPin = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // 11. Help Center / Report Question or Bug
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(t("ศูนย์ช่วยเหลือ & แจ้งปัญหาโจทย์ข้อสอบ", "Help Center & Report Issue", "帮助中心与问题反馈"), fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "QUESTION_ISSUE" to t("แจ้งข้อสอบผิดพลาด", "Question Error", "试题纠错"),
                            "BUG_REPORT" to t("แจ้งบั๊ก/ข้อเสนอแนะ", "Bug / Feedback", "建议反馈")
                        ).forEach { (cat, lbl) ->
                            FilterChip(selected = reportCategory == cat, onClick = { reportCategory = cat }, label = { Text(lbl) })
                        }
                    }
                    OutlinedTextField(
                        value = reportMessage,
                        onValueChange = { reportMessage = it },
                        label = { Text(t("อธิบายรายละเอียดให้ทีมงานตรวจสอบ...", "Describe the issue...", "请描述具体问题...")) },
                        minLines = 2,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            vm.submitReport(reportCategory, reportMessage)
                            reportMessage = ""
                        },
                        enabled = reportMessage.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(t("ส่งรายงานถึงผู้ดูแลระบบ", "Submit Report to Admin", "提交给管理员"))
                    }
                }
            }
        }

        // 12. About PT's & Admin Panel Secure Gateway
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(t("เกี่ยวกับ PT's มาสอบเข้า ม.1! & ผู้ดูแลระบบ", "About PT's & Admin Gateway", "关于 PT's 与管理员入口"), fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = t(
                            "แพลตฟอร์มเกมการศึกษาแห่งอนาคต https://PTPreM1.online สำหรับนักเรียน ป.4–ป.6 เตรียมสอบเข้าชั้นมัธยมศึกษาปีที่ 1 อ้างอิงหลักสูตรแกนกลางการศึกษาขั้นพื้นฐาน พ.ศ. 2551 (ฉบับปรับปรุง พ.ศ. 2560)",
                            "Futuristic M.1 Entrance Educational Game (https://PTPreM1.online) aligned with the Thai OBEC Core Curriculum B.E. 2551 (Revised B.E. 2560).",
                            "面向小学四至六年级升初一的未来教育游戏平台（https://PTPreM1.online），严格遵循泰国教育部核心课程标准。"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onOpenAdminPanel,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_admin_panel_button")
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = PtLavender)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t("เข้าสู่ระบบจัดการผู้ดูแล (Admin Panel)", "Open Admin Panel (Passcode Required)", "进入管理员后台（需密码验证）"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 13. Absolute Bottom of Settings: Mandatory Signature "Made by Pentor!" in Sky Blue-to-Pink Gradient!
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 32.dp)
                    .testTag("made_by_pentor_credit")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PtSkyBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Made by Pentor!",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(PtSkyBlue, PtElectricCyan, PtBubblegumPink, PtRosePink)
                            ),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PtRosePink,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
