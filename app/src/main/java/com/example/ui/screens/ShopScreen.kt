package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.UserEntity
import com.example.i18n.LocalAppLanguage
import com.example.i18n.formatNumber
import com.example.i18n.t
import com.example.model.GameCatalog
import com.example.model.ShopItemCatalog
import com.example.ui.components.HttpsPortalBar
import com.example.ui.components.NegativePtWarningBanner
import com.example.ui.components.PtPointsCoinBadge
import com.example.ui.theme.PtEmerald
import com.example.ui.theme.PtSkyBlue
import com.example.ui.theme.ShopDeepCrimson
import com.example.ui.theme.ShopFrostWhite
import com.example.ui.theme.ShopNeonRed
import com.example.ui.theme.ShopPureWhite
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun PtShopScreen(
    user: UserEntity,
    vm: GameViewModel,
    onNavigateBackgrounds: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    val ownedBgs by vm.ownedBackgrounds.collectAsState()
    val ownedFrames by vm.ownedFrames.collectAsState()
    val ownedBgIds = remember(ownedBgs) { ownedBgs.map { it.backgroundId }.toSet() }
    val ownedFrameIds = remember(ownedFrames) { (ownedFrames.map { it.frameId } + "FRAME_DEFAULT").toSet() }

    var selectedCategory by rememberSaveable { mutableStateOf("ALL") }
    var pendingMultiplierItem by remember { mutableStateOf<ShopItemCatalog?>(null) }
    var nowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(user.multiplierExpiresAtMillis) {
        while (user.multiplierExpiresAtMillis > System.currentTimeMillis()) {
            nowMillis = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val filteredItems = remember(selectedCategory) {
        if (selectedCategory == "ALL") GameCatalog.shopItems
        else GameCatalog.shopItems.filter { it.category == selectedCategory }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Signature Neon Split Red-White Storefront Backdrop ("ร้านแดง-ขาว ครึ่งแดงครึ่งขาวแบบร้านนีออน")
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Pure Frost White base
            drawRect(color = ShopFrostWhite)

            // Diagonal / Split Neon Crimson Red Half (Top-Left Neon Canopy)
            val redSplitPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.34f)
                lineTo(0f, h * 0.50f)
                close()
            }
            drawPath(
                path = redSplitPath,
                brush = Brush.linearGradient(
                    colors = listOf(ShopDeepCrimson, ShopNeonRed, Color(0xFFFF4D6D)),
                    start = Offset(0f, 0f),
                    end = Offset(w, h * 0.45f)
                )
            )

            // Glowing White Neon Divider Strip between Red and White halves!
            drawLine(
                color = Color.White,
                start = Offset(0f, h * 0.50f),
                end = Offset(w, h * 0.34f),
                strokeWidth = 6.dp.toPx()
            )
            drawLine(
                color = ShopNeonRed.copy(alpha = 0.35f),
                start = Offset(0f, h * 0.50f + 5.dp.toPx()),
                end = Offset(w, h * 0.34f + 5.dp.toPx()),
                strokeWidth = 8.dp.toPx()
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            item {
                HttpsPortalBar(
                    currentPath = "/shop",
                    onCopiedToast = { vm.showToast(it) }
                )
            }

            // Neon Storefront Header
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"), tint = Color.White)
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = t("PT Shop • ร้านแดง-ขาว", "PT Shop • Neon Red-White", "PT 红白霓虹商店"),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = t(
                                    "ครบ 18 ไอเทม! กรอบอวตาร ยาเพิ่มเวลา โล่รักษาไฟ ตั๋ววงล้อ & พื้นหลัง!",
                                    "18 Items! Avatar frames, time potions, streak shields, tickets & themes!",
                                    "共18款精品！头像框、延时药水、连胜护盾、转盘券与学院背景！"
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }
                    PtPointsCoinBadge(ptPoints = user.ptPoints, reducedMotion = user.reducedMotion)
                }
            }

            item {
                NegativePtWarningBanner(ptPoints = user.ptPoints, reducedMotion = user.reducedMotion)
            }

            // Active Multiplier Neon Banner
            if (user.multiplierExpiresAtMillis > nowMillis && user.activeMultiplierGain > 1.0) {
                item {
                    val remSec = ((user.multiplierExpiresAtMillis - nowMillis) / 1000L).coerceAtLeast(0L)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = ShopPureWhite,
                        border = BorderStroke(2.dp, ShopNeonRed),
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = ShopNeonRed, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = t(
                                        "⚡ ตัวคูณกำลังทำงาน: ×${user.activeMultiplierGain} (เตือน: หักผิด ×${user.activeMultiplierPenalty})",
                                        "⚡ Active Multiplier: ×${user.activeMultiplierGain} (Wrong Penalty ×${user.activeMultiplierPenalty})",
                                        "⚡ 当前生效倍率：×${user.activeMultiplierGain}（答错扣分 ×${user.activeMultiplierPenalty}）"
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    color = ShopNeonRed
                                )
                                Text(
                                    text = "%02d:%02d:%02d".format(remSec / 3600, (remSec % 3600) / 60, remSec % 60),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ShopDeepCrimson
                                )
                            }
                        }
                    }
                }
            }

            // Category Filter Strip (All 18 Items)
            item {
                val cats = listOf(
                    "ALL" to t("ทั้งหมด (${GameCatalog.shopItems.size})", "All (${GameCatalog.shopItems.size})", "全部(${GameCatalog.shopItems.size})"),
                    "AVATAR_FRAME" to t("กรอบอวตาร", "Avatar Frames", "专属头像框"),
                    "CONSUMABLE" to t("ไอเทมช่วยสอบ & ตั๋ว", "Items & Tickets", "考试道具与礼券"),
                    "BACKGROUND" to t("พื้นหลังโรงเรียน", "Backgrounds", "学院背景"),
                    "THEME" to t("ธีมพรีเมียม", "Theme", "高级主题"),
                    "MULTIPLIER" to t("ตัวคูณ PT", "Multipliers", "积分倍率"),
                    "PERMANENT" to t("ปลดล็อกถาวร", "Permanent", "永久扩展")
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cats) { (code, label) ->
                        FilterChip(
                            selected = selectedCategory == code,
                            onClick = { selectedCategory = code },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedCategory == code) ShopNeonRed else Color(0xFF1E293B)
                                )
                            }
                        )
                    }
                }
            }

            // All 18 Neon Split Red-White Shop Item Cards
            items(filteredItems, key = { it.id }) { item ->
                val isOwnedPermanent = when {
                    item.id == "SHOP_PREMIUM_UI" -> user.premiumUiUnlocked
                    item.id == "SHOP_EASY_PLUS_20" -> user.easyBonusUnlocked
                    item.backgroundId != null -> item.backgroundId in ownedBgIds
                    item.frameId != null -> item.frameId in ownedFrameIds
                    else -> false
                }
                val canAfford = user.ptPoints >= item.pricePt
                val bgPreviewSpec = item.backgroundId?.let { GameCatalog.getBackgroundById(it) }

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = ShopPureWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = ShopNeonRed.copy(alpha = 0.28f))
                        .border(
                            width = 2.dp,
                            brush = Brush.horizontalGradient(listOf(ShopNeonRed, Color(0xFFFF8FA3), ShopPureWhite)),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .testTag("shop_item_${item.id}")
                ) {
                    Column {
                        // Top Red Neon Ribbon Header on each card (half-red / half-white aesthetic!)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Brush.horizontalGradient(listOf(ShopDeepCrimson, ShopNeonRed)))
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = when (item.category) {
                                        "THEME" -> Icons.Default.AutoAwesome
                                        "BACKGROUND" -> Icons.Default.Wallpaper
                                        "AVATAR_FRAME" -> Icons.Default.AccountCircle
                                        "CONSUMABLE" -> Icons.Default.ContentCut
                                        "MULTIPLIER" -> Icons.Default.Bolt
                                        else -> Icons.Default.LibraryAdd
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.name(lang),
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White
                            ) {
                                Text(
                                    text = "${formatNumber(lang, item.pricePt)} PT",
                                    color = ShopDeepCrimson,
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Lower Pure White Showcase Body
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (bgPreviewSpec?.drawableRes != null) {
                                Image(
                                    painter = painterResource(id = bgPreviewSpec.drawableRes),
                                    contentDescription = item.name(lang),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            if (item.frameId != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(bottom = 10.dp)
                                ) {
                                    PlayerAvatarBadge(
                                        avatarIndex = user.avatarIndex,
                                        size = 62.dp,
                                        frameId = item.frameId
                                    )
                                    Text(
                                        text = t("ตัวอย่างกรอบอวตารบนโปรไฟล์ของคุณ ✨", "Live preview on your avatar ✨", "头像框穿戴预览 ✨"),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ShopDeepCrimson
                                    )
                                }
                            }

                            Text(
                                text = item.desc(lang),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1E293B)
                            )

                            val ownedStockText: String? = when (item.id) {
                                "SHOP_REMOVE_WRONG_2" -> t("คงเหลือ: ${user.removeWrongUses} ครั้ง", "Owned: ${user.removeWrongUses} uses", "当前拥有：${user.removeWrongUses} 次")
                                "SHOP_TIME_POTION" -> t("คงเหลือ: ${user.timePotionUses} ขวด (+30s)", "Owned: ${user.timePotionUses} potions (+30s)", "当前拥有：${user.timePotionUses} 瓶")
                                "SHOP_STREAK_SHIELD" -> t("โล่รักษาไฟคงเหลือ: ${user.streakShieldCount} ชิ้น", "Streak Shields owned: ${user.streakShieldCount}", "当前连胜护盾：${user.streakShieldCount} 个")
                                "SHOP_LUCKY_TICKET" -> t("ตั๋วหมุนวงล้อคงเหลือ: ${user.luckyWheelTickets} ใบ", "Wheel Tickets owned: ${user.luckyWheelTickets}", "当前转盘券：${user.luckyWheelTickets} 张")
                                "SHOP_MYSTERY_BOX_KEY" -> t("กุญแจหีบสมบัติคงเหลือ: ${user.mysteryBoxesOwned} ดอก", "Chest Keys owned: ${user.mysteryBoxesOwned}", "当前宝箱钥匙：${user.mysteryBoxesOwned} 把")
                                else -> null
                            }

                            if (ownedStockText != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "🎒 $ownedStockText",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = PtSkyBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                when {
                                    isOwnedPermanent && item.id == "SHOP_PREMIUM_UI" -> {
                                        Button(
                                            onClick = { vm.togglePremiumUi(!user.premiumUiEnabled) },
                                            colors = ButtonDefaults.buttonColors(containerColor = PtSkyBlue),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                if (user.premiumUiEnabled) t("กำลังใช้งาน Premium UI (แตะเพื่อสลับ)", "Premium UI Active (Tap to Toggle)", "已启用水晶UI（点击切换）")
                                                else t("เปิดใช้งาน Premium UI", "Activate Premium UI", "启用高级水晶UI")
                                            )
                                        }
                                    }
                                    isOwnedPermanent && item.backgroundId != null -> {
                                        val isEquipped = user.activeBackgroundId == item.backgroundId
                                        Button(
                                            onClick = {
                                                if (isEquipped) onNavigateBackgrounds()
                                                else vm.equipBackground(item.backgroundId)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isEquipped) PtEmerald else PtSkyBlue
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                if (isEquipped) t("กำลังติดตั้งพื้นหลังนี้", "Equipped Background", "当前使用中")
                                                else t("ติดตั้งพื้นหลังทันที", "Equip Background", "立即设为背景")
                                            )
                                        }
                                    }
                                    isOwnedPermanent && item.frameId != null -> {
                                        val isFrameEquipped = user.equippedFrameId == item.frameId
                                        Button(
                                            onClick = {
                                                vm.updateCosmetics(
                                                    customTitle = user.customTitle,
                                                    titleColorIndex = user.titleColorIndex,
                                                    equippedFrameId = if (isFrameEquipped) "FRAME_DEFAULT" else item.frameId,
                                                    avatarIndex = user.avatarIndex
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isFrameEquipped) PtEmerald else PtSkyBlue
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                if (isFrameEquipped) t("กำลังสวมกรอบนี้ (แตะเพื่อถอด)", "Equipped Frame (Tap to Unequip)", "已佩戴该头像框")
                                                else t("สวมใส่กรอบอวตารนี้", "Equip Avatar Frame", "立即佩戴头像框")
                                            )
                                        }
                                    }
                                    isOwnedPermanent -> {
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = PtEmerald.copy(alpha = 0.14f)
                                        ) {
                                            Text(
                                                text = t("✅ ปลดล็อกถาวรเรียบร้อยแล้ว", "✅ Permanently Unlocked", "✅ 已永久解锁"),
                                                color = PtEmerald,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                    else -> {
                                        Button(
                                            onClick = {
                                                if (item.category == "MULTIPLIER") {
                                                    pendingMultiplierItem = item
                                                } else {
                                                    vm.buyShopItem(item)
                                                }
                                            },
                                            enabled = canAfford,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = ShopNeonRed,
                                                disabledContainerColor = Color(0xFFCBD5E1)
                                            ),
                                            shape = RoundedCornerShape(14.dp),
                                            modifier = Modifier.testTag("buy_btn_${item.id}")
                                        ) {
                                            Text(
                                                text = if (canAfford) {
                                                    t("ซื้อด้วย ${item.pricePt} PT", "Buy for ${item.pricePt} PT", "花费 ${item.pricePt} PT 购买")
                                                } else {
                                                    t("PT Points ไม่เพียงพอ", "Insufficient PT", "PT 积分不足")
                                                },
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Multiplier High-Risk / High-Reward Confirmation Modal (Section 12.2)
    val multTarget = pendingMultiplierItem
    if (multTarget != null) {
        AlertDialog(
            onDismissRequest = { pendingMultiplierItem = null },
            shape = RoundedCornerShape(26.dp),
            containerColor = ShopPureWhite,
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = ShopNeonRed, modifier = Modifier.size(44.dp))
            },
            title = {
                Text(
                    text = t("คำเตือนความเสี่ยงตัวคูณ PT Points!", "PT Multiplier Risk Warning!", "PT 积分倍率风险确认！"),
                    color = ShopDeepCrimson,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = multTarget.name(lang),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("• ${t("ระยะเวลาทำงาน", "Active Duration", "生效时长")}: ${multTarget.durationHours} ${t("ชั่วโมง", "hour(s)", "小时")}")
                    Text(
                        text = "• ${t("คะแนน PT เมื่อตอบถูก", "Correct PT Gain", "答对获得积分")}: ×${multTarget.multiplierGain}",
                        color = PtEmerald,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• ${t("คำเตือน! ค่าหักแต้มเมื่อผิดสะสม", "WARNING! Wrong Streak Penalty", "警告！连错扣分倍率")}: ×${multTarget.multiplierPenalty}",
                        color = ShopNeonRed,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = t(
                            "หากคุณมีตัวคูณอื่นเปิดอยู่ ตัวคูณใหม่นี้จะแทนที่ตัวเดิมทันที ยืนยันการแลกซื้อหรือไม่?",
                            "Activating this multiplier immediately replaces any existing active multiplier. Confirm purchase?",
                            "启用新倍率将立即替换当前已有倍率，确定购买并激活吗？"
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val chosen = multTarget
                        pendingMultiplierItem = null
                        vm.buyShopItem(chosen)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShopNeonRed)
                ) {
                    Text(t("ยอมรับความเสี่ยง & ซื้อเลย!", "Accept Risk & Activate!", "确认风险并购买！"), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingMultiplierItem = null }) {
                    Text(t("ยกเลิก", "Cancel", "取消"))
                }
            }
        )
    }
}
