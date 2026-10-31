package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.LocalAppLanguage
import com.example.i18n.PlayerGender
import com.example.i18n.buildOfficialHttpsUrl
import com.example.i18n.formatNumber
import com.example.i18n.t
import com.example.i18n.tr
import com.example.model.GameCatalog
import com.example.model.PetSpecies
import com.example.ui.theme.NegativeRedAlert
import com.example.ui.theme.NegativeRedGlow
import com.example.ui.theme.NegativeRedSurface
import com.example.ui.theme.PastelSkyMid
import com.example.ui.theme.PtBubblegumPink
import com.example.ui.theme.PtElectricCyan
import com.example.ui.theme.PtEmerald
import com.example.ui.theme.PtNeonBlue
import com.example.ui.theme.PtRosePink
import com.example.ui.theme.PtSkyBlue
import com.example.ui.theme.PtSoftGold
import com.example.ui.theme.RainbowAqua
import com.example.ui.theme.RainbowCoral
import com.example.ui.theme.RainbowIris
import com.example.ui.theme.RainbowMint
import com.example.ui.theme.RainbowSunny
import com.example.util.SfxType
import com.example.util.SoundEngine
import com.example.viewmodel.SaveIndicatorState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

enum class MascotMood {
    IDLE_WAVE,
    JUMP_CORRECT,
    ENCOURAGE_WRONG,
    CELEBRATE_REWARD
}

/**
 * Full-screen Rainbow Pastel Futuristic backdrop with floating clouds, rainbow aura orbs,
 * twinkling stars, and smooth Background Collection switching!
 */
@Composable
fun PtWorldBackground(
    activeBackgroundId: String,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val bgSpec = remember(activeBackgroundId) { GameCatalog.getBackgroundById(activeBackgroundId) }
    val infinite = rememberInfiniteTransition(label = "rainbow_sky_bg")
    val drift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0f else 1f,
        animationSpec = infiniteRepeatable(tween(11000, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(bgSpec.topColorHex),
                        PastelSkyMid,
                        Color(bgSpec.bottomColorHex)
                    )
                )
            )
    ) {
        if (bgSpec.drawableRes != null) {
            Image(
                painter = painterResource(id = bgSpec.drawableRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.28f)
            )
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Rainbow Pastel Aura Orbs
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(RainbowAqua.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(w * (0.16f + drift * 0.08f), h * 0.12f),
                    radius = w * 0.46f
                ),
                radius = w * 0.46f,
                center = Offset(w * (0.16f + drift * 0.08f), h * 0.12f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(RainbowCoral.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(w * (0.86f - drift * 0.07f), h * 0.24f),
                    radius = w * 0.44f
                ),
                radius = w * 0.44f,
                center = Offset(w * (0.86f - drift * 0.07f), h * 0.24f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(RainbowMint.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(w * (0.20f + drift * 0.05f), h * 0.72f),
                    radius = w * 0.45f
                ),
                radius = w * 0.45f,
                center = Offset(w * (0.20f + drift * 0.05f), h * 0.72f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(RainbowIris.copy(alpha = 0.16f), Color.Transparent),
                    center = Offset(w * (0.82f - drift * 0.05f), h * 0.85f),
                    radius = w * 0.42f
                ),
                radius = w * 0.42f,
                center = Offset(w * (0.82f - drift * 0.05f), h * 0.85f)
            )

            // Soft Floating Pastel Clouds (Floating Decorations!)
            val cloudSpots = listOf(
                Triple(0.14f + drift * 0.05f, 0.07f, 0.09f),
                Triple(0.78f - drift * 0.05f, 0.15f, 0.08f),
                Triple(0.10f + drift * 0.04f, 0.56f, 0.07f),
                Triple(0.86f - drift * 0.04f, 0.64f, 0.08f)
            )
            cloudSpots.forEach { (cx, cy, rScale) ->
                val r = w * rScale
                val center = Offset(w * cx, h * cy)
                drawCircle(Color.White.copy(alpha = 0.52f), r * 0.75f, Offset(center.x - r * 0.65f, center.y + r * 0.15f))
                drawCircle(Color.White.copy(alpha = 0.52f), r * 0.75f, Offset(center.x + r * 0.65f, center.y + r * 0.15f))
                drawCircle(Color.White.copy(alpha = 0.62f), r, center)
            }

            // Rainbow Twinkling Stars & Sparkle Diamonds
            val starColors = listOf(RainbowCoral, RainbowSunny, RainbowMint, RainbowAqua, RainbowIris, PtBubblegumPink)
            val starOffsets = listOf(
                0.08f to 0.05f, 0.52f to 0.04f, 0.91f to 0.08f,
                0.06f to 0.34f, 0.93f to 0.40f, 0.18f to 0.48f,
                0.84f to 0.52f, 0.12f to 0.82f, 0.62f to 0.91f, 0.90f to 0.78f
            )
            starOffsets.forEachIndexed { idx, (sx, sy) ->
                val pulse = if (reducedMotion) 0.65f else 0.35f + 0.55f * sin((drift + idx * 0.19f) * PI.toFloat()).let { kotlin.math.abs(it) }
                val starColor = starColors[idx % starColors.size].copy(alpha = pulse)
                val c = Offset(w * sx, h * sy)
                val rad = if (idx % 2 == 0) 4.5.dp.toPx() else 3.2.dp.toPx()
                drawCircle(color = starColor, radius = rad, center = c)
                // Cross sparkle glint
                drawLine(starColor, Offset(c.x - rad * 1.8f, c.y), Offset(c.x + rad * 1.8f, c.y), strokeWidth = 1.2.dp.toPx())
                drawLine(starColor, Offset(c.x, c.y - rad * 1.8f), Offset(c.x, c.y + rad * 1.8f), strokeWidth = 1.2.dp.toPx())
            }
        }
        content()
    }
}

/**
 * Official HTTPS Website Portal Bar (`https://PTPreM1.online`) with TLS Lock Badge,
 * 1-tap Copy Link, Android Native Share Sheet, and Quick URL Jump Dialog!
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HttpsPortalBar(
    currentPath: String,
    shareMessageExtra: String = "",
    onJumpToPath: ((String) -> Unit)? = null,
    onCopiedToast: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val fullHttpsUrl = remember(currentPath) { buildOfficialHttpsUrl(currentPath) }
    var showPortalDialog by remember { mutableStateOf(false) }
    var customPathInput by remember(fullHttpsUrl) { mutableStateOf(fullHttpsUrl) }

    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.94f),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(listOf(PtEmerald, PtSkyBlue, PtRosePink))
        ),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .testTag("https_portal_bar")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { showPortalDialog = true }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFDCFCE7)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "HTTPS Verified",
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "HTTPS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF059669)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = fullHttpsUrl,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = {
                    SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                    clipboard.setText(AnnotatedString(fullHttpsUrl))
                    onCopiedToast?.invoke(
                        tr(lang, "คัดลอกลิงก์ $fullHttpsUrl แล้ว! 🔗", "Copied $fullHttpsUrl! 🔗", "已复制链接 $fullHttpsUrl！🔗")
                    )
                },
                modifier = Modifier
                    .size(34.dp)
                    .testTag("https_copy_url_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = t("คัดลอกลิงก์ HTTPS", "Copy HTTPS Link", "复制HTTPS链接"),
                    tint = PtSkyBlue,
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = {
                    SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                    val textToShare = if (shareMessageExtra.isNotBlank()) {
                        "$shareMessageExtra\n🌐 $fullHttpsUrl"
                    } else {
                        tr(
                            lang,
                            "มาฝึกทำข้อสอบและสะสมสัตว์เลี้ยงสอบเข้า ม.1 ด้วยกันที่ PT's มาสอบเข้า ม.1!\n🌐 $fullHttpsUrl",
                            "Join me on PT's Pre-M.1 Educational Game!\n🌐 $fullHttpsUrl",
                            "一起来 PT's 初一入学教育游戏挑战冲刺吧！\n🌐 $fullHttpsUrl"
                        )
                    }
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, textToShare)
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "PTPreM1.online"))
                },
                modifier = Modifier
                    .size(34.dp)
                    .testTag("https_share_url_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = t("แชร์ลิงก์เว็บไซต์", "Share HTTPS Link", "分享网站链接"),
                    tint = PtRosePink,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    if (showPortalDialog) {
        AlertDialog(
            onDismissRequest = { showPortalDialog = false },
            shape = RoundedCornerShape(26.dp),
            containerColor = Color.White,
            icon = {
                Icon(Icons.Default.Public, contentDescription = null, tint = PtSkyBlue, modifier = Modifier.size(40.dp))
            },
            title = {
                Text(
                    text = "🔒 https://PTPreM1.online",
                    fontWeight = FontWeight.ExtraBold,
                    color = PtSkyBlue,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = t(
                            "เชื่อมต่อปลอดภัยด้วย HTTPS แท้ 100% (ไม่อนุญาต HTTP ไม่เข้ารหัส) รองรับการเปิดลิงก์ตรงเข้าสู่ทุกโหมดของเกม!",
                            "100% Strict TLS/HTTPS verified connection (cleartext HTTP blocked). Supports direct deep-linking to any game zone!",
                            "100% 纯正 HTTPS 加密安全连接（已禁用明文 HTTP），支持直达任意游戏专区！"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (onJumpToPath != null) {
                        OutlinedTextField(
                            value = customPathInput,
                            onValueChange = { customPathInput = it },
                            label = { Text(t("ไปที่ลิงก์ PTPreM1.online", "Navigate HTTPS URL", "跳转站内链接")) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "/home" to t("หน้าหลัก", "Home", "主页"),
                                "/exam" to t("ห้องสอบ", "Exam", "考场"),
                                "/study" to t("สมุดทบทวน", "Study Hub", "智能温习"),
                                "/wheel" to t("วงล้อโชคดี", "Lucky Wheel", "幸运转盘"),
                                "/boss" to t("ท้าดวลบอส", "Boss Raid", "讨伐魔王"),
                                "/shop" to t("ร้านแดง-ขาว", "PT Shop", "红白商店"),
                                "/leaderboard" to t("อันดับ", "Rankings", "排行榜"),
                                "/profile" to t("โปรไฟล์", "Profile", "档案")
                            ).forEach { (p, lbl) ->
                                FilterChip(
                                    selected = currentPath == p,
                                    onClick = {
                                        showPortalDialog = false
                                        onJumpToPath(p)
                                    },
                                    label = { Text(lbl, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (onJumpToPath != null) {
                    PtGradientButton(
                        text = t("ไปที่ลิงก์นี้", "Go to URL", "立即前往"),
                        onClick = {
                            showPortalDialog = false
                            onJumpToPath(customPathInput)
                        }
                    )
                } else {
                    TextButton(onClick = { showPortalDialog = false }) {
                        Text(t("ปิด", "Close", "关闭"))
                    }
                }
            },
            dismissButton = {
                if (onJumpToPath != null) {
                    TextButton(onClick = { showPortalDialog = false }) {
                        Text(t("ปิด", "Close", "关闭"))
                    }
                }
            }
        )
    }
}

/**
 * Colorful Category Section Ribbon Header for clean visual hierarchy!
 */
@Composable
fun PtSectionRibbonHeader(
    emoji: String,
    title: String,
    subtitle: String? = null,
    colors: List<Color> = listOf(PtSkyBlue, PtRosePink),
    trailingContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.92f),
        border = BorderStroke(1.5.dp, Brush.horizontalGradient(colors)),
        shadowElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(colors.first().copy(alpha = 0.14f), colors.last().copy(alpha = 0.06f))
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(colors))
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

/**
 * Decorated Interactive Gender Buttons (`ชาย / หญิง / ไม่ระบุ`) with pastel gradients,
 * floating glow border, icon badge, and tactile press bounce!
 */
@Composable
fun DecoratedGenderSelector(
    selectedGender: PlayerGender,
    onSelectGender: (PlayerGender) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        PlayerGender.entries.forEach { gender ->
            val isSelected = selectedGender == gender
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = when {
                    isPressed -> 0.94f
                    isSelected -> 1.03f
                    else -> 1f
                },
                label = "gender_btn_${gender.code}"
            )
            val gradientBrush = Brush.linearGradient(listOf(gender.primaryColor, gender.secondaryColor))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected) Color.Transparent else Color(0xFFF8FAFC),
                border = BorderStroke(
                    width = if (isSelected) 2.2.dp else 1.2.dp,
                    brush = if (isSelected) gradientBrush else Brush.linearGradient(
                        listOf(gender.primaryColor.copy(alpha = 0.40f), gender.secondaryColor.copy(alpha = 0.40f))
                    )
                ),
                shadowElevation = if (isSelected) 6.dp else 1.dp,
                modifier = Modifier
                    .weight(1f)
                    .scale(scale)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(interactionSource = interactionSource, indication = null) {
                        SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                        onSelectGender(gender)
                    }
                    .testTag("gender_btn_${gender.code}")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(
                            if (isSelected) gradientBrush
                            else Brush.verticalGradient(
                                listOf(Color.White, gender.primaryColor.copy(alpha = 0.08f))
                            )
                        )
                        .padding(vertical = 10.dp, horizontal = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = gender.emoji, fontSize = 20.sp)
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = gender.label(lang),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) Color.White else gender.primaryColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Printable Player Title Pill Badge with 6 Rainbow Gradient Styles!
 */
@Composable
fun CustomTitleBadge(
    customTitle: String,
    styleIndex: Int,
    modifier: Modifier = Modifier
) {
    if (customTitle.isBlank()) return
    val spec = remember(styleIndex) { GameCatalog.getTitleStyle(styleIndex) }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Brush.horizontalGradient(listOf(Color(spec.startHex), Color(spec.endHex))))
            .border(1.dp, Color.White.copy(alpha = 0.85f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = "✨ $customTitle",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Glassmorphic 3D Game Card with gradient border and press bounce feedback!
 */
@Composable
fun PtGlassCard(
    modifier: Modifier = Modifier,
    borderColors: List<Color> = listOf(PtSkyBlue.copy(alpha = 0.48f), PtBubblegumPink.copy(alpha = 0.48f)),
    containerColor: Color = Color.White.copy(alpha = 0.92f),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed && onClick != null) 0.97f else 1f, label = "card_scale")

    Card(
        modifier = modifier
            .scale(scale)
            .shadow(7.dp, RoundedCornerShape(22.dp), ambientColor = PtSkyBlue.copy(alpha = 0.22f))
            .border(1.6.dp, Brush.linearGradient(borderColors), RoundedCornerShape(22.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable(interactionSource = interactionSource, indication = null) {
                        SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                        onClick()
                    }
                } else Modifier
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        content = content
    )
}

/**
 * Signature Gradient Button with 48.dp touch height, press bounce, and SFX!
 */
@Composable
fun PtGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    gradientColors: List<Color> = listOf(PtSkyBlue, PtElectricCyan, PtRosePink),
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed && enabled) 0.95f else 1f, label = "btn_scale")

    val bgBrush = if (enabled) {
        Brush.horizontalGradient(gradientColors)
    } else {
        Brush.horizontalGradient(listOf(Color(0xFFCBD5E1), Color(0xFF94A3B8)))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .scale(scale)
            .shadow(if (enabled) 5.dp else 0.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(bgBrush)
            .clickable(enabled = enabled, interactionSource = interactionSource, indication = null) {
                SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                onClick()
            }
            .padding(horizontal = 20.dp, vertical = 13.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 13-Tier Animated Streak Flame Badge
 */
@Composable
fun AnimatedStreakFlameBadge(
    streakDays: Int,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val tier = remember(streakDays) { GameCatalog.getStreakTier(streakDays) }
    val lang = LocalAppLanguage.current
    val infinite = rememberInfiniteTransition(label = "flame_anim")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0f else (2f * PI.toFloat()),
        animationSpec = infiniteRepeatable(
            tween(durationMillis = (1800 / tier.speedFactor).toInt().coerceAtLeast(450), easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "flame_phase"
    )

    val primaryColor = Color(tier.colorsHex.first())
    val secondaryColor = Color(tier.colorsHex.getOrElse(1) { tier.colorsHex.first() })

    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.94f),
        border = BorderStroke(
            width = if (tier.tierIndex >= 7) 2.dp else 1.5.dp,
            brush = Brush.horizontalGradient(tier.colorsHex.map { Color(it) }.let { if (it.size == 1) it + it else it })
        ),
        shadowElevation = 4.dp,
        modifier = modifier.testTag("streak_flame_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Canvas(modifier = Modifier.size(26.dp)) {
                val w = size.width
                val h = size.height
                val sway = sin(phase) * (w * 0.08f)

                drawCircle(
                    color = primaryColor.copy(alpha = 0.28f),
                    radius = w * 0.48f,
                    center = Offset(w * 0.5f, h * 0.56f)
                )

                val outerPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.92f)
                    cubicTo(w * 0.12f, h * 0.85f, w * 0.15f, h * 0.42f, w * 0.5f + sway, h * 0.08f)
                    cubicTo(w * 0.85f, h * 0.42f, w * 0.88f, h * 0.85f, w * 0.5f, h * 0.92f)
                    close()
                }
                drawPath(
                    path = outerPath,
                    brush = Brush.verticalGradient(
                        colors = tier.colorsHex.map { Color(it) }.let { if (it.size == 1) listOf(it[0], it[0]) else it }
                    )
                )

                val innerPath = Path().apply {
                    moveTo(w * 0.5f, h * 0.88f)
                    cubicTo(w * 0.30f, h * 0.80f, w * 0.32f, h * 0.52f, w * 0.5f - sway * 0.5f, h * 0.28f)
                    cubicTo(w * 0.68f, h * 0.52f, w * 0.70f, h * 0.80f, w * 0.5f, h * 0.88f)
                    close()
                }
                drawPath(path = innerPath, color = secondaryColor.copy(alpha = 0.9f))

                if (!reducedMotion && tier.tierIndex >= 2) {
                    val count = (tier.particleCount / 4).coerceIn(2, 8)
                    for (i in 0 until count) {
                        val angle = phase + (i * 2f * PI.toFloat() / count)
                        val px = w * 0.5f + cos(angle) * (w * 0.38f)
                        val py = h * 0.5f + sin(angle) * (h * 0.34f)
                        drawCircle(
                            color = Color(tier.colorsHex[i % tier.colorsHex.size]),
                            radius = 2.dp.toPx(),
                            center = Offset(px, py)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${formatNumber(lang, streakDays)} ${t("วัน", "d", "天")}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * PT Points Blue Coin Pill with Automatic Negative Balance Red Border, Red Glow & Shake!
 */
@Composable
fun PtPointsCoinBadge(
    ptPoints: Int,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val isNegative = ptPoints < 0
    val infinite = rememberInfiniteTransition(label = "neg_pt_shake")
    val shakeOffset by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(140, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "shake_x"
    )
    val shakePx = if (isNegative && !reducedMotion) (shakeOffset * 6f).roundToInt() else 0

    Surface(
        shape = RoundedCornerShape(50),
        color = if (isNegative) NegativeRedSurface else Color.White.copy(alpha = 0.94f),
        border = BorderStroke(
            width = if (isNegative) 2.2.dp else 1.5.dp,
            color = if (isNegative) NegativeRedAlert else PtSkyBlue
        ),
        shadowElevation = if (isNegative) 8.dp else 4.dp,
        modifier = modifier
            .offset { IntOffset(shakePx, 0) }
            .then(
                if (isNegative) Modifier.shadow(10.dp, RoundedCornerShape(50), spotColor = NegativeRedGlow)
                else Modifier
            )
            .testTag("pt_points_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
        ) {
            Canvas(modifier = Modifier.size(20.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF7DD3FC), PtSkyBlue, Color(0xFF0284C7))
                    )
                )
                drawCircle(
                    color = if (isNegative) NegativeRedAlert else Color.White,
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(color = Color.White.copy(alpha = 0.7f), radius = size.minDimension * 0.18f)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${formatNumber(lang, ptPoints)} PT",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isNegative) NegativeRedAlert else Color(0xFF0369A1)
            )
        }
    }
}

@Composable
fun NegativePtWarningBanner(ptPoints: Int, reducedMotion: Boolean) {
    AnimatedVisibility(visible = ptPoints < 0) {
        Surface(
            color = NegativeRedSurface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, NegativeRedAlert),
            shadowElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .testTag("negative_pt_warning_banner")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = NegativeRedAlert)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = t(
                        "PT Points ของคุณติดลบ ($ptPoints PT)! ฝึกฝนเพิ่มเติมเพื่อกลับมาเป็นบวกอีกครั้ง",
                        "Your PT Points are negative ($ptPoints PT)! Practice more to return to positive!",
                        "您的 PT 积分为负数（$ptPoints PT）！继续练习恢复正分吧！"
                    ),
                    color = Color(0xFF991B1B),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AutoSaveIndicatorChip(saveState: SaveIndicatorState) {
    AnimatedVisibility(visible = saveState != SaveIndicatorState.IDLE) {
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.White.copy(alpha = 0.88f),
            border = BorderStroke(1.dp, PtSkyBlue.copy(alpha = 0.4f))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = if (saveState == SaveIndicatorState.SAVING) Icons.Default.Sync else Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = PtSkyBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (saveState == SaveIndicatorState.SAVING) {
                        t("กำลังบันทึก...", "Saving...", "保存中...")
                    } else {
                        t("บันทึกสำเร็จ", "Auto-Saved", "已自动保存")
                    },
                    fontSize = 11.sp,
                    color = Color(0xFF0369A1)
                )
            }
        }
    }
}

/**
 * Animated PT's Knowledge Owl Mascot!
 */
@Composable
fun PtOwlMascot(
    mood: MascotMood = MascotMood.IDLE_WAVE,
    reducedMotion: Boolean = false,
    size: Dp = 84.dp,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "owl_mascot")
    val bob by infinite.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0f else 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "owl_bob"
    )
    val blinkCycle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0.5f else 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Restart),
        label = "owl_blink"
    )
    val jumpOffset = when (mood) {
        MascotMood.JUMP_CORRECT, MascotMood.CELEBRATE_REWARD -> (-10f * bob).dp
        else -> (-4f * bob).dp
    }

    Canvas(
        modifier = modifier
            .size(size)
            .offset(y = jumpOffset)
    ) {
        val w = this.size.width
        val h = this.size.height

        drawOval(
            color = PtSkyBlue.copy(alpha = 0.20f),
            topLeft = Offset(w * 0.22f, h * 0.88f),
            size = Size(w * 0.56f, h * 0.08f)
        )

        val wingWave = if (reducedMotion) 0f else sin(bob * PI.toFloat()) * (h * 0.07f)
        drawOval(
            color = Color(0xFF0284C7),
            topLeft = Offset(w * 0.06f, h * 0.42f),
            size = Size(w * 0.22f, h * 0.34f)
        )
        drawOval(
            color = Color(0xFF0284C7),
            topLeft = Offset(w * 0.72f, h * 0.38f - wingWave),
            size = Size(w * 0.22f, h * 0.34f)
        )

        drawOval(
            brush = Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF0EA5E9))),
            topLeft = Offset(w * 0.16f, h * 0.16f),
            size = Size(w * 0.68f, h * 0.72f)
        )

        val leftTuft = Path().apply {
            moveTo(w * 0.24f, h * 0.24f)
            lineTo(w * 0.15f, h * 0.08f)
            lineTo(w * 0.36f, h * 0.18f)
            close()
        }
        val rightTuft = Path().apply {
            moveTo(w * 0.76f, h * 0.24f)
            lineTo(w * 0.85f, h * 0.08f)
            lineTo(w * 0.64f, h * 0.18f)
            close()
        }
        drawPath(leftTuft, Color(0xFF38BDF8))
        drawPath(rightTuft, Color(0xFF38BDF8))

        drawOval(
            color = Color(0xFFE0F2FE),
            topLeft = Offset(w * 0.28f, h * 0.48f),
            size = Size(w * 0.44f, h * 0.34f)
        )

        val isBlinking = blinkCycle > 0.93f
        val eyeCenters = listOf(Offset(w * 0.38f, h * 0.37f), Offset(w * 0.62f, h * 0.37f))
        eyeCenters.forEach { center ->
            drawCircle(color = Color.White, radius = w * 0.12f, center = center)
            if (isBlinking) {
                drawLine(
                    color = Color(0xFF0F172A),
                    start = Offset(center.x - w * 0.07f, center.y),
                    end = Offset(center.x + w * 0.07f, center.y),
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            } else {
                drawCircle(color = Color(0xFF0F172A), radius = w * 0.06f, center = center)
                drawCircle(
                    color = Color.White,
                    radius = w * 0.022f,
                    center = Offset(center.x - w * 0.02f, center.y - w * 0.02f)
                )
            }
            drawCircle(
                color = PtRosePink,
                radius = w * 0.13f,
                center = center,
                style = Stroke(width = 2.2.dp.toPx())
            )
        }

        val beak = Path().apply {
            moveTo(w * 0.45f, h * 0.43f)
            lineTo(w * 0.55f, h * 0.43f)
            lineTo(w * 0.50f, h * 0.51f)
            close()
        }
        drawPath(beak, PtSoftGold)

        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(PtRosePink, PtSkyBlue)),
            topLeft = Offset(w * 0.33f, h * 0.64f),
            size = Size(w * 0.34f, h * 0.16f),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        drawLine(
            color = Color.White,
            start = Offset(w * 0.50f, h * 0.64f),
            end = Offset(w * 0.50f, h * 0.80f),
            strokeWidth = 1.5.dp.toPx()
        )

        if (mood == MascotMood.CELEBRATE_REWARD || mood == MascotMood.JUMP_CORRECT) {
            drawCircle(color = PtSoftGold, radius = w * 0.05f, center = Offset(w * 0.86f, h * 0.18f))
            drawCircle(color = PtBubblegumPink, radius = w * 0.04f, center = Offset(w * 0.14f, h * 0.18f))
        }
    }
}

/**
 * Custom-Drawn Distinct Silhouette & Expression Canvas for All 28 Pet Species (#1..#28)!
 */
@Composable
fun PetCharacterCanvas(
    pet: PetSpecies,
    reducedMotion: Boolean = false,
    size: Dp = 76.dp,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "pet_${pet.id}")
    val anim by infinite.animateFloat(
        initialValue = 0f,
        targetValue = if (reducedMotion) 0f else 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pet_float"
    )
    val bodyColor = Color(pet.bodyColorHex)
    val accentColor = Color(pet.accentColorHex)

    Canvas(
        modifier = modifier
            .size(size)
            .offset(y = (-4f * anim).dp)
    ) {
        val w = this.size.width
        val h = this.size.height

        drawCircle(
            color = pet.rarity.primaryColor.copy(alpha = 0.20f),
            radius = w * 0.47f,
            center = Offset(w * 0.5f, h * 0.54f)
        )

        // Species-specific distinct silhouette accessories (behind body)
        when (pet.number) {
            2 -> { // Peach Bunny long ears
                drawOval(bodyColor, Offset(w * 0.28f, h * 0.06f), Size(w * 0.14f, h * 0.36f))
                drawOval(bodyColor, Offset(w * 0.58f, h * 0.06f), Size(w * 0.14f, h * 0.36f))
                drawOval(accentColor.copy(alpha = 0.5f), Offset(w * 0.31f, h * 0.11f), Size(w * 0.08f, h * 0.24f))
                drawOval(accentColor.copy(alpha = 0.5f), Offset(w * 0.61f, h * 0.11f), Size(w * 0.08f, h * 0.24f))
            }
            4, 7, 19, 24 -> { // Cat / Fox / Leopard / Corgi pointed ears + tail
                val leftEar = Path().apply {
                    moveTo(w * 0.24f, h * 0.36f); lineTo(w * 0.18f, h * 0.12f); lineTo(w * 0.40f, h * 0.26f); close()
                }
                val rightEar = Path().apply {
                    moveTo(w * 0.76f, h * 0.36f); lineTo(w * 0.82f, h * 0.12f); lineTo(w * 0.60f, h * 0.26f); close()
                }
                drawPath(leftEar, accentColor)
                drawPath(rightEar, accentColor)
                drawOval(accentColor, Offset(w * 0.70f, h * 0.52f), Size(w * 0.24f, h * 0.18f))
            }
            6 -> { // Mint Turtle crystal shell dome
                drawArc(accentColor, 180f, 180f, true, Offset(w * 0.14f, h * 0.32f), Size(w * 0.72f, h * 0.52f))
            }
            9, 21, 23 -> { // Panda / Hamster / Koala round fuzzy ears
                val earRad = if (pet.number == 23) w * 0.16f else w * 0.11f
                drawCircle(accentColor, earRad, Offset(w * 0.24f, h * 0.26f))
                drawCircle(accentColor, earRad, Offset(w * 0.76f, h * 0.26f))
            }
            10, 20 -> { // Starlight Deer & Suankularb Silver Deer glowing antlers
                drawLine(accentColor, Offset(w * 0.34f, h * 0.28f), Offset(w * 0.22f, h * 0.08f), 3.dp.toPx(), StrokeCap.Round)
                drawLine(accentColor, Offset(w * 0.26f, h * 0.16f), Offset(w * 0.36f, h * 0.12f), 2.5.dp.toPx(), StrokeCap.Round)
                drawLine(accentColor, Offset(w * 0.66f, h * 0.28f), Offset(w * 0.78f, h * 0.08f), 3.dp.toPx(), StrokeCap.Round)
                drawLine(accentColor, Offset(w * 0.74f, h * 0.16f), Offset(w * 0.64f, h * 0.12f), 2.5.dp.toPx(), StrokeCap.Round)
            }
            11, 13, 16, 27, 28 -> { // Dragon / Phoenix / Griffin / Rainbow Pegasus spread wings!
                val lWing = Path().apply {
                    moveTo(w * 0.26f, h * 0.52f); lineTo(w * 0.03f, h * 0.24f); lineTo(w * 0.13f, h * 0.66f); close()
                }
                val rWing = Path().apply {
                    moveTo(w * 0.74f, h * 0.52f); lineTo(w * 0.97f, h * 0.24f); lineTo(w * 0.87f, h * 0.66f); close()
                }
                drawPath(lWing, accentColor)
                drawPath(rWing, accentColor)
                if (pet.number == 28) { // Pegasus unicorn horn!
                    val horn = Path().apply {
                        moveTo(w * 0.45f, h * 0.26f); lineTo(w * 0.50f, h * 0.03f); lineTo(w * 0.55f, h * 0.26f); close()
                    }
                    drawPath(horn, PtSoftGold)
                }
            }
            12 -> { // Meteor Squirrel giant fluffy star-tail
                drawOval(accentColor, Offset(w * 0.64f, h * 0.22f), Size(w * 0.30f, h * 0.54f))
            }
            17 -> { // Nebula Unicorn spiral crystal horn
                val horn = Path().apply {
                    moveTo(w * 0.44f, h * 0.28f); lineTo(w * 0.50f, h * 0.04f); lineTo(w * 0.56f, h * 0.28f); close()
                }
                drawPath(horn, accentColor)
            }
            18 -> { // Solar Lion radiant golden sun-mane!
                for (ray in 0 until 12) {
                    val ang = ray * (2.0 * PI / 12.0)
                    val cx = w * 0.5f + cos(ang).toFloat() * (w * 0.32f)
                    val cy = h * 0.54f + sin(ang).toFloat() * (h * 0.32f)
                    drawCircle(accentColor, w * 0.08f, Offset(cx, cy))
                }
            }
            22 -> { // Scholar Chick cute graduation cap!
                val capTop = Path().apply {
                    moveTo(w * 0.50f, h * 0.08f)
                    lineTo(w * 0.74f, h * 0.18f)
                    lineTo(w * 0.50f, h * 0.26f)
                    lineTo(w * 0.26f, h * 0.18f)
                    close()
                }
                drawPath(capTop, accentColor)
            }
            25 -> { // Axolotl coral feathery external gills (3 on each side!)
                for (g in 0..2) {
                    val gy = h * (0.36f + g * 0.09f)
                    drawLine(accentColor, Offset(w * 0.24f, gy), Offset(w * 0.08f, gy - h * 0.04f), 3.dp.toPx(), StrokeCap.Round)
                    drawLine(accentColor, Offset(w * 0.76f, gy), Offset(w * 0.92f, gy - h * 0.04f), 3.dp.toPx(), StrokeCap.Round)
                }
            }
            26 -> { // Nine-Tailed Kitsune fan of mystic tails!
                for (tIdx in -2..2) {
                    val tx = w * (0.50f + tIdx * 0.12f)
                    drawOval(accentColor.copy(alpha = 0.85f), Offset(tx - w * 0.06f, h * 0.08f), Size(w * 0.12f, h * 0.34f))
                }
            }
        }

        // Main Character Body Shape
        when (pet.number) {
            1 -> { // Fluffy Cloud
                drawCircle(bodyColor, w * 0.22f, Offset(w * 0.34f, h * 0.56f))
                drawCircle(bodyColor, w * 0.22f, Offset(w * 0.66f, h * 0.56f))
                drawCircle(bodyColor, w * 0.26f, Offset(w * 0.50f, h * 0.46f))
            }
            5, 14 -> { // Whale body + tail fluke
                drawOval(bodyColor, Offset(w * 0.16f, h * 0.36f), Size(w * 0.64f, h * 0.44f))
                val fluke = Path().apply {
                    moveTo(w * 0.76f, h * 0.58f); lineTo(w * 0.94f, h * 0.44f); lineTo(w * 0.92f, h * 0.70f); close()
                }
                drawPath(fluke, accentColor)
                drawCircle(accentColor.copy(alpha = 0.7f), w * 0.05f, Offset(w * 0.44f, h * 0.20f))
            }
            else -> {
                drawOval(
                    brush = Brush.verticalGradient(listOf(bodyColor, bodyColor.copy(alpha = 0.88f))),
                    topLeft = Offset(w * 0.22f, h * 0.26f),
                    size = Size(w * 0.56f, h * 0.56f)
                )
            }
        }

        // Face Details
        val eyeY = h * 0.50f
        if (pet.number == 9) {
            drawCircle(accentColor, w * 0.08f, Offset(w * 0.39f, eyeY))
            drawCircle(accentColor, w * 0.08f, Offset(w * 0.61f, eyeY))
        }
        drawCircle(Color(0xFF0F172A), w * 0.045f, Offset(w * 0.39f, eyeY))
        drawCircle(Color(0xFF0F172A), w * 0.045f, Offset(w * 0.61f, eyeY))
        drawCircle(Color.White, w * 0.018f, Offset(w * 0.38f, eyeY - w * 0.015f))
        drawCircle(Color.White, w * 0.018f, Offset(w * 0.60f, eyeY - w * 0.015f))

        // Rosy Cheeks (puffy cheeks for #21 Hamster!)
        val cheekRad = if (pet.number == 21) w * 0.075f else w * 0.045f
        drawCircle(PtBubblegumPink.copy(alpha = 0.60f), cheekRad, Offset(w * 0.31f, eyeY + h * 0.07f))
        drawCircle(PtBubblegumPink.copy(alpha = 0.60f), cheekRad, Offset(w * 0.69f, eyeY + h * 0.07f))

        if (pet.number == 22 || pet.number == 27) { // Chick / Griffin golden beak
            val beak = Path().apply {
                moveTo(w * 0.46f, eyeY + h * 0.03f)
                lineTo(w * 0.54f, eyeY + h * 0.03f)
                lineTo(w * 0.50f, eyeY + h * 0.11f)
                close()
            }
            drawPath(beak, PtSoftGold)
        }
        if (pet.number == 3 || pet.number == 15) {
            drawRoundRect(
                color = accentColor.copy(alpha = 0.45f),
                topLeft = Offset(w * 0.28f, eyeY - h * 0.06f),
                size = Size(w * 0.44f, h * 0.12f),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
        if (pet.number == 8) {
            drawRoundRect(
                color = accentColor,
                topLeft = Offset(w * 0.28f, h * 0.64f),
                size = Size(w * 0.44f, h * 0.09f),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
        if (pet.number == 19) {
            drawCircle(accentColor, w * 0.025f, Offset(w * 0.50f, h * 0.36f))
            drawCircle(accentColor, w * 0.022f, Offset(w * 0.35f, h * 0.66f))
            drawCircle(accentColor, w * 0.022f, Offset(w * 0.65f, h * 0.66f))
        }
    }
}
