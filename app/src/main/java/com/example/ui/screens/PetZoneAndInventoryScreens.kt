package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.PetInstanceEntity
import com.example.data.local.UserEntity
import com.example.i18n.LocalAppLanguage
import com.example.i18n.t
import com.example.model.EggRarity
import com.example.model.GameCatalog
import com.example.ui.components.HttpsPortalBar
import com.example.ui.components.NegativePtWarningBanner
import com.example.ui.components.PetCharacterCanvas
import com.example.ui.components.PtGlassCard
import com.example.ui.components.PtGradientButton
import com.example.ui.components.PtPointsCoinBadge
import com.example.ui.theme.NegativeRedAlert
import com.example.ui.theme.PtBubblegumPink
import com.example.ui.theme.PtEmerald
import com.example.ui.theme.PtLavender
import com.example.ui.theme.PtRosePink
import com.example.ui.theme.PtSkyBlue
import com.example.ui.theme.PtSoftGold
import com.example.util.SfxType
import com.example.util.SoundEngine
import com.example.viewmodel.EggOpeningState
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PetZoneScreen(
    user: UserEntity,
    vm: GameViewModel,
    onOpenInventory: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    val ownedPets by vm.userPets.collectAsState()
    val eggOpeningState by vm.eggOpeningState.collectAsState()
    val uniqueOwnedCount = remember(ownedPets) { ownedPets.map { it.speciesId }.distinct().size }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            item {
                HttpsPortalBar(
                    currentPath = "/pets",
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
                            text = "PT Pet Zone",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = PtLavender
                        )
                    }
                    PtPointsCoinBadge(ptPoints = user.ptPoints, reducedMotion = user.reducedMotion)
                }
            }

            item {
                NegativePtWarningBanner(ptPoints = user.ptPoints, reducedMotion = user.reducedMotion)
            }

            // Floating Sky-Island Hero Card + Egg Hatch Pedestal
            item {
                PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Box(
                            contentAlignment = Alignment.BottomStart,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(165.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_pet_zone_sky_1793378451345),
                                contentDescription = "PT Pet Zone Sky Island",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xAA0F172A))
                                        )
                                    )
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = t("เกาะลอยฟ้าแห่งคู่หูความรู้", "Floating Sanctuary of Study Companions", "浮空知识伙伴圣岛"),
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = t(
                                        "สะสมครบแล้ว $uniqueOwnedCount / ${GameCatalog.pets.size} สายพันธุ์ • ราคาเปิดไข่ 500 PT Points",
                                        "Collected $uniqueOwnedCount / ${GameCatalog.pets.size} Species • 500 PT Points per Egg",
                                        "已收集 $uniqueOwnedCount / ${GameCatalog.pets.size} 种伙伴 • 每次孵化 500 PT"
                                    ),
                                    color = Color.White.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Glowing Mystery Egg Preview Pedestal
                            Canvas(modifier = Modifier.size(110.dp)) {
                                val w = size.width
                                val h = size.height
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        listOf(PtBubblegumPink.copy(alpha = 0.35f), PtSkyBlue.copy(alpha = 0.15f), Color.Transparent)
                                    ),
                                    radius = w * 0.48f
                                )
                                drawOval(
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFFFDF2F8), PtBubblegumPink, PtSkyBlue)
                                    ),
                                    topLeft = Offset(w * 0.22f, h * 0.12f),
                                    size = Size(w * 0.56f, h * 0.74f)
                                )
                                drawOval(
                                    color = Color.White,
                                    topLeft = Offset(w * 0.22f, h * 0.12f),
                                    size = Size(w * 0.56f, h * 0.74f),
                                    style = Stroke(width = 3.dp.toPx())
                                )
                                drawCircle(Color.White.copy(alpha = 0.75f), w * 0.07f, Offset(w * 0.40f, h * 0.34f))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            PtGradientButton(
                                text = if (user.ptPoints >= 500) {
                                    t("🥚 เปิดไข่สุ่มคู่หูเลย! (500 PT)", "🥚 Hatch Mystery Egg! (500 PT)", "🥚 立即孵化神秘宠物蛋！(500 PT)")
                                } else {
                                    t("ต้องมี 500 PT เพื่อเปิดไข่ (มี ${user.ptPoints} PT)", "Need 500 PT to Hatch (${user.ptPoints} PT)", "需满 500 PT 才能孵化")
                                },
                                onClick = { vm.buyAndOpenEgg() },
                                enabled = user.ptPoints >= 500,
                                gradientColors = listOf(PtLavender, PtRosePink, PtSkyBlue),
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "hatch_egg_button"
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = onOpenInventory,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("open_pet_inventory_button")
                            ) {
                                Icon(Icons.Default.Backpack, contentDescription = null, tint = PtSkyBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = t("เปิดกระเป๋าสัตว์เลี้ยง & สารานุกรม (${ownedPets.size} ตัว)", "Open Pet Inventory & Codex (${ownedPets.size})", "打开宠物背包与图鉴（${ownedPets.size}只）"),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Transparent Drop Rate Table (100% Sum!)
            item {
                PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = t("📊 อัตราการดรอปไข่ทั้ง 7 ระดับ (โปร่งใส 100%)", "📊 Official 7-Tier Egg Drop Rates (100%)", "📊 7阶宠物蛋真实掉落概率公示（总计100%）"),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        EggRarity.entries.forEach { rarity ->
                            val countInTier = GameCatalog.pets.count { it.rarity == rarity }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(rarity.primaryColor)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${rarity.eggName(lang)} ($countInTier ${t("สายพันธุ์", "pets", "种")})",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = rarity.primaryColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${rarity.dropRatePercent}%",
                                        color = rarity.primaryColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Full 15-Step Egg Opening Overlay!
        val activeHatch = eggOpeningState
        if (activeHatch != null) {
            EggOpeningOverlay(
                hatch = activeHatch,
                reducedMotion = user.reducedMotion,
                onEquipAndClose = {
                    vm.equipPetInstance(activeHatch.instanceId)
                    vm.dismissEggOpeningOverlay()
                },
                onCollectOnly = {
                    vm.dismissEggOpeningOverlay()
                }
            )
        }
    }
}

/**
 * Section 13.4: Authentic 15-Step Egg Hatching Sequence!
 * 1: Appear -> 2: Dim backdrop -> 3: Gentle float -> 4: Rarity aura glow -> 5: Accelerating shake ->
 * 6: Crack lines -> 7: Light beams pierce cracks -> 8: Shell bursts -> 9: Rarity particles/petals ->
 * 10: Pet emerges -> 11: Smooth zoom -> 12: Pet name -> 13: Egg rarity badge -> 14: Skill card -> 15: Collect CTA!
 */
@Composable
fun EggOpeningOverlay(
    hatch: EggOpeningState,
    reducedMotion: Boolean,
    onEquipAndClose: () -> Unit,
    onCollectOnly: () -> Unit
) {
    val lang = LocalAppLanguage.current
    var step by remember(hatch.instanceId) { mutableIntStateOf(if (reducedMotion) 15 else 1) }

    LaunchedEffect(hatch.instanceId, reducedMotion) {
        if (reducedMotion) {
            SoundEngine.playSfx(SfxType.PET_REVEAL)
            step = 15
            return@LaunchedEffect
        }
        val delays = listOf(180L, 200L, 260L, 280L, 380L, 300L, 300L, 220L, 240L, 260L, 240L, 200L, 200L, 220L)
        for (i in 1..14) {
            delay(delays[i - 1])
            step = i + 1
            when (step) {
                5 -> SoundEngine.playSfx(SfxType.EGG_SHAKE)
                8 -> SoundEngine.playSfx(SfxType.EGG_BREAK)
                10 -> SoundEngine.playSfx(SfxType.PET_REVEAL)
            }
        }
    }

    val backdropAlpha by animateFloatAsState(
        targetValue = if (step >= 2) 0.88f else 0.4f,
        animationSpec = tween(250),
        label = "egg_dim"
    )
    val petScale by animateFloatAsState(
        targetValue = when {
            step < 10 -> 0.1f
            step == 10 -> 0.82f
            else -> 1.08f
        },
        animationSpec = tween(320, easing = FastOutSlowInEasing),
        label = "pet_zoom"
    )

    val shakeX = if (!reducedMotion && step in 5..7) {
        if (step % 2 == 0) -14 else 14
    } else 0

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A).copy(alpha = backdropAlpha))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(230.dp)
                    .offset { IntOffset(shakeX, if (step in 3..7) -10 else 0) }
            ) {
                // Rarity Aura, Light Beams, Burst Particles & Egg Shell Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val center = Offset(w * 0.5f, h * 0.5f)
                    val pri = hatch.rarity.primaryColor
                    val sec = hatch.rarity.secondaryColor

                    // Step 4+: Rarity Aura
                    if (step >= 4) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(pri.copy(alpha = 0.65f), sec.copy(alpha = 0.25f), Color.Transparent)
                            ),
                            radius = w * (if (step >= 8) 0.50f else 0.40f),
                            center = center
                        )
                    }

                    // Step 7+: Radiant Light Beams
                    if (step >= 7) {
                        for (ray in 0 until 8) {
                            val ang = ray * (2.0 * PI / 8.0)
                            val end = Offset(
                                center.x + cos(ang).toFloat() * w * 0.48f,
                                center.y + sin(ang).toFloat() * h * 0.48f
                            )
                            drawLine(
                                color = sec.copy(alpha = if (step in 7..9) 0.85f else 0.35f),
                                start = center,
                                end = end,
                                strokeWidth = 5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // Step 1..7: Intact / Cracking Egg Shell
                    if (step < 8) {
                        drawOval(
                            brush = Brush.verticalGradient(listOf(sec, pri)),
                            topLeft = Offset(w * 0.24f, h * 0.14f),
                            size = Size(w * 0.52f, h * 0.70f)
                        )
                        drawOval(
                            color = Color.White,
                            topLeft = Offset(w * 0.24f, h * 0.14f),
                            size = Size(w * 0.52f, h * 0.70f),
                            style = Stroke(width = 3.dp.toPx())
                        )
                        // Step 6..7: Glowing Zig-Zag Cracks across the shell!
                        if (step >= 6) {
                            val crack = Path().apply {
                                moveTo(w * 0.32f, h * 0.45f)
                                lineTo(w * 0.44f, h * 0.52f)
                                lineTo(w * 0.52f, h * 0.42f)
                                lineTo(w * 0.62f, h * 0.53f)
                                lineTo(w * 0.69f, h * 0.46f)
                            }
                            drawPath(
                                path = crack,
                                color = Color.White,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Step 9+: Bursting Rarity Particles / Stars / Rose Petals!
                    if (step >= 9) {
                        for (p in 0 until 14) {
                            val ang = p * (2.0 * PI / 14.0)
                            val dist = w * 0.43f
                            val px = center.x + cos(ang).toFloat() * dist
                            val py = center.y + sin(ang).toFloat() * dist
                            drawCircle(
                                color = if (p % 2 == 0) pri else PtSoftGold,
                                radius = 5.dp.toPx(),
                                center = Offset(px, py)
                            )
                        }
                    }
                }

                // Step 10+: Emerging Custom-Drawn Pet Companion!
                if (step >= 10) {
                    PetCharacterCanvas(
                        pet = hatch.pet,
                        reducedMotion = reducedMotion,
                        size = 136.dp,
                        modifier = Modifier.scale(petScale)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Step 12+: Pet Name
            if (step >= 12) {
                Text(
                    text = hatch.pet.name(lang),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            // Step 13+: Egg Rarity Badge
            if (step >= 13) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = hatch.rarity.primaryColor
                ) {
                    Text(
                        text = "✨ ${hatch.rarity.eggName(lang)} • Power ${hatch.pet.powerLevel}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Step 14+: Special Skill Card
            if (step >= 14) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = hatch.pet.desc(lang),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🌟 ${t("ความสามารถพิเศษ", "Special Skill", "专属技能")}: ${hatch.pet.skillText(lang)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = PtRosePink,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Step 15: Collect / Equip Actions!
            if (step >= 15) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onCollectOnly,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.5.dp, Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(t("เก็บเข้าคลัง", "Collect", "收入背包"), fontWeight = FontWeight.Bold)
                    }
                    PtGradientButton(
                        text = t("ติดตั้งคู่หูเลย!", "Equip Now!", "立即装备！"),
                        onClick = onEquipAndClose,
                        modifier = Modifier.weight(1f),
                        testTag = "egg_equip_now_button"
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { step = 15 }) {
                    Text(t("ข้ามแอนิเมชัน", "Skip Animation", "跳过动画"), color = Color.White.copy(alpha = 0.75f))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PetInventoryScreen(
    user: UserEntity,
    vm: GameViewModel,
    onGoHatch: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    val ownedInstances by vm.userPets.collectAsState()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var rarityFilter by rememberSaveable { mutableStateOf("ALL") }
    var sortBy by rememberSaveable { mutableStateOf("NEWEST") } // NEWEST, POWER, RARITY
    var discardCandidate by remember { mutableStateOf<PetInstanceEntity?>(null) }

    val filteredOwned = remember(ownedInstances, searchQuery, rarityFilter, sortBy) {
        ownedInstances
            .mapNotNull { inst ->
                val spec = GameCatalog.getPetById(inst.speciesId) ?: return@mapNotNull null
                inst to spec
            }
            .filter { (_, spec) ->
                (rarityFilter == "ALL" || spec.rarity.id == rarityFilter) &&
                    (searchQuery.isBlank() ||
                        spec.nameTh.contains(searchQuery, ignoreCase = true) ||
                        spec.nameEn.contains(searchQuery, ignoreCase = true) ||
                        spec.nameZh.contains(searchQuery, ignoreCase = true))
            }
            .let { list ->
                when (sortBy) {
                    "POWER" -> list.sortedByDescending { it.second.powerLevel }
                    "RARITY" -> list.sortedByDescending { it.second.rarity.ordinal }
                    else -> list.sortedByDescending { it.first.obtainedAtMillis }
                }
            }
    }

    val ownedSpeciesSet = remember(ownedInstances) { ownedInstances.map { it.speciesId }.toSet() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        item {
            HttpsPortalBar(
                currentPath = "/pets/codex",
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
                        text = t("คลังสัตว์เลี้ยง & สารานุกรม 28 สายพันธุ์", "Pet Inventory & 28-Species Codex", "宠物背包与28种全图鉴"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // Search, Filter & Sort Controls
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        label = { Text(t("ค้นหาชื่อสัตว์เลี้ยง...", "Search pet name...", "搜索宠物名称...")) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChip(
                                selected = rarityFilter == "ALL",
                                onClick = { rarityFilter = "ALL" },
                                label = { Text(t("ทุกระดับ", "All", "全部")) }
                            )
                        }
                        items(EggRarity.entries) { r ->
                            FilterChip(
                                selected = rarityFilter == r.id,
                                onClick = { rarityFilter = r.id },
                                label = { Text(r.id) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "NEWEST" to t("ล่าสุด", "Newest", "最新获得"),
                            "POWER" to t("ค่าพลัง", "Power", "战力排序"),
                            "RARITY" to t("ความหายาก", "Rarity", "稀有度")
                        ).forEach { (code, lbl) ->
                            FilterChip(
                                selected = sortBy == code,
                                onClick = { sortBy = code },
                                label = { Text(lbl) }
                            )
                        }
                    }
                }
            }
        }

        if (filteredOwned.isEmpty()) {
            item {
                PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Icon(Icons.Default.Pets, contentDescription = null, tint = PtBubblegumPink, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = t("ยังไม่มีสัตว์เลี้ยงในหมวดนี้ ไปเปิดไข่สุ่มที่ PT Pet Zone กันเลย!", "No pets match this filter yet. Visit PT Pet Zone to hatch!", "暂无符合条件的宠物，前往孵化岛开启宠物蛋吧！"),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        PtGradientButton(
                            text = t("ไปหน้าเปิดไข่สุ่ม", "Go to PT Pet Zone", "前往孵化岛"),
                            onClick = onGoHatch
                        )
                    }
                }
            }
        } else {
            items(filteredOwned, key = { it.first.instanceId }) { (inst, spec) ->
                val isEquipped = user.equippedPetInstanceId == inst.instanceId
                PtGlassCard(
                    borderColors = if (isEquipped) listOf(PtEmerald, PtSkyBlue) else listOf(spec.rarity.primaryColor, PtBubblegumPink),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        PetCharacterCanvas(pet = spec, reducedMotion = user.reducedMotion, size = 66.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(spec.name(lang), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(50), color = spec.rarity.primaryColor.copy(alpha = 0.16f)) {
                                    Text(
                                        text = spec.rarity.id,
                                        color = spec.rarity.primaryColor,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text("⚡ Power ${spec.powerLevel} • ${spec.skillText(lang)}", style = MaterialTheme.typography.bodySmall, color = PtRosePink)
                            Text(spec.desc(lang), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (isEquipped) {
                                    OutlinedButton(
                                        onClick = { vm.equipPetInstance(null) },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PtEmerald, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(t("ถอดคู่หู", "Unequip", "卸下"))
                                    }
                                } else {
                                    PtGradientButton(
                                        text = t("ติดตั้งคู่หู", "Equip", "出战"),
                                        onClick = { vm.equipPetInstance(inst.instanceId) }
                                    )
                                }

                                IconButton(onClick = { discardCandidate = inst }) {
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = t("ทิ้งสัตว์เลี้ยง", "Discard Pet", "放生宠物"),
                                        tint = NegativeRedAlert
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Complete 28-Pet Species Codex Overview
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = t("📖 สารานุกรมสัตว์เลี้ยงทั้ง ${GameCatalog.pets.size} สายพันธุ์ (${ownedSpeciesSet.size}/${GameCatalog.pets.size})", "📖 Complete ${GameCatalog.pets.size}-Pet Codex (${ownedSpeciesSet.size}/${GameCatalog.pets.size})", "📖 ${GameCatalog.pets.size}种伙伴全图鉴（${ownedSpeciesSet.size}/${GameCatalog.pets.size}）"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                GameCatalog.pets.forEach { sp ->
                    val unlocked = sp.id in ownedSpeciesSet
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.88f),
                        border = BorderStroke(1.dp, if (unlocked) sp.rarity.primaryColor else Color(0xFFCBD5E1)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            PetCharacterCanvas(
                                pet = sp,
                                reducedMotion = user.reducedMotion,
                                size = 50.dp,
                                modifier = Modifier.alpha(if (unlocked) 1f else 0.30f)
                            )
                            Text(
                                text = "#${sp.number} ${sp.name(lang)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = sp.skillText(lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (unlocked) PtSkyBlue else Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    val toDiscard = discardCandidate
    if (toDiscard != null) {
        val sp = GameCatalog.getPetById(toDiscard.speciesId)
        AlertDialog(
            onDismissRequest = { discardCandidate = null },
            shape = RoundedCornerShape(24.dp),
            title = { Text(t("ยืนยันการปล่อยสัตว์เลี้ยง?", "Confirm Discard Pet?", "确认放生该宠物？"), fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    t(
                        "ต้องการนำ '${sp?.nameTh.orEmpty()}' ออกจากคลังใช่หรือไม่? การกระทำนี้ไม่สามารถย้อนกลับได้",
                        "Remove '${sp?.nameEn.orEmpty()}' from your inventory? This cannot be undone.",
                        "确定要从背包中放生“${sp?.nameZh.orEmpty()}”吗？此操作无法撤销。"
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.discardPetInstance(toDiscard.instanceId)
                        discardCandidate = null
                    }
                ) {
                    Text(t("ยืนยันนำออก", "Discard", "确认放生"), color = NegativeRedAlert, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { discardCandidate = null }) {
                    Text(t("ยกเลิก", "Cancel", "取消"))
                }
            }
        )
    }
}
