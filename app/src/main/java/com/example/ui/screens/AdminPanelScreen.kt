package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.local.QuestDefinitionEntity
import com.example.data.local.QuestionEntity
import com.example.i18n.LocalAppLanguage
import com.example.i18n.formatDateTime
import com.example.i18n.formatNumber
import com.example.i18n.t
import com.example.model.DifficultyLevel
import com.example.model.SubjectId
import com.example.ui.components.HttpsPortalBar
import com.example.ui.components.PtGlassCard
import com.example.ui.components.PtGradientButton
import com.example.ui.theme.NegativeRedAlert
import com.example.ui.theme.PtEmerald
import com.example.ui.theme.PtLavender
import com.example.ui.theme.PtRosePink
import com.example.ui.theme.PtSkyBlue
import com.example.ui.theme.PtSoftGold
import com.example.ui.theme.ShopDeepCrimson
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminPanelScreen(
    vm: GameViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    val clipboard = LocalClipboardManager.current
    val isUnlocked by vm.isAdminUnlocked.collectAsState()

    var passcodeInput by rememberSaveable { mutableStateOf("") }

    if (!isUnlocked) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .padding(24.dp)
        ) {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(22.dp)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = PtLavender, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = t("ยืนยันรหัสลับผู้ดูแลระบบ (Admin Gate)", "Admin Secret Passcode Verification", "管理员密钥安全验证"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = t(
                            "ป้อนรหัสลับผู้ดูแลระบบ (Admin Passcode) เพื่อเปิดแผงควบคุมเต็มรูปแบบ (เข้าหัส Salted SHA-256 ปลอดภัย)",
                            "Enter the Admin Secret Passcode to unlock full management controls (Salted SHA-256 verified).",
                            "请输入管理员专属密钥以解锁完整后台控制中心（加盐 SHA-256 安全验证）。"
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = passcodeInput,
                        onValueChange = { passcodeInput = it },
                        label = { Text(t("รหัสลับ Admin", "Admin Passcode", "管理员密钥")) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_passcode_input")
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                            Text(t("ย้อนกลับ", "Back", "返回"))
                        }
                        PtGradientButton(
                            text = t("ปลดล็อก Admin", "Unlock", "验证进入"),
                            onClick = {
                                val ok = vm.unlockAdminPanel(passcodeInput)
                                passcodeInput = ""
                                if (!ok) return@PtGradientButton
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "admin_unlock_button"
                        )
                    }
                }
            }
        }
        return
    }

    // Authenticated Admin Panel State
    val users by vm.adminUsersList.collectAsState()
    val totalExams by vm.adminTotalExams.collectAsState()
    val totalXp by vm.adminTotalXp.collectAsState()
    val totalPt by vm.adminTotalPt.collectAsState()
    val totalEggs by vm.adminTotalEggs.collectAsState()
    val totalPurchases by vm.adminTotalPurchases.collectAsState()
    val allQuestions by vm.allQuestions.collectAsState()
    val auditLogs by vm.adminAuditLogs.collectAsState()
    val reports by vm.adminFeedbackReports.collectAsState()
    val configs by vm.systemConfigs.collectAsState()

    var adminTab by rememberSaveable { mutableStateOf("DASHBOARD") } // DASHBOARD, USERS, QUESTIONS, SYSTEM
    var targetUsername by rememberSaveable { mutableStateOf("") }
    var moderationReason by rememberSaveable { mutableStateOf("การจัดการโดยผู้ดูแลระบบ") }

    // Confirmation Dialog States
    var showBanForeverConfirm by remember { mutableStateOf(false) }
    var deleteForeverStep by remember { mutableStateOf(0) } // 0 none, 1 first confirm, 2 final confirm

    // Question Management States
    var qSearch by rememberSaveable { mutableStateOf("") }
    var editingQuestion by remember { mutableStateOf<QuestionEntity?>(null) }
    var showNewQuestionDialog by remember { mutableStateOf(false) }
    var csvBoxText by rememberSaveable { mutableStateOf("") }

    // System Config & Quest Creator States
    var xpFactorInput by rememberSaveable { mutableStateOf(configs.firstOrNull { it.configKey == "XP_BASE_FACTOR" }?.configValue ?: "100") }
    var newsBannerInput by rememberSaveable { mutableStateOf(configs.firstOrNull { it.configKey == "NEWS_BANNER" }?.configValue.orEmpty()) }
    var newQuestTitle by rememberSaveable { mutableStateOf("") }
    var newAdminCodeInput by rememberSaveable { mutableStateOf("") }
    val loggedInUser by vm.currentUser.collectAsState()

    val matchedUser = remember(users, targetUsername) {
        users.firstOrNull { it.username.equals(targetUsername.trim(), ignoreCase = true) }
    }

    val xpAmounts = listOf(100, 1_000, 10_000, 100_000, 1_000_000)
    val ptAmounts = listOf(100, 1_000, 10_000, 100_000)
    val levelAmounts = listOf(1, 2, 5, 10, 20, 50, 100)
    val streakAmounts = listOf(1, 2, 5, 10, 20, 50, 100)

    val banDurations: List<Pair<String, Long?>> = listOf(
        "Ban 10 Seconds" to 10_000L,
        "Ban 1 Minute" to 60_000L,
        "Ban 1 Hour" to 3_600_000L,
        "Ban 6 Hours" to 21_600_000L,
        "Ban 12 Hours" to 43_200_000L,
        "Ban 1 Day" to 86_400_000L,
        "Ban 1 Week" to 7 * 86_400_000L,
        "Ban 1 Month" to 30L * 86_400_000L,
        "Ban 6 Months" to 180L * 86_400_000L,
        "Ban 1 Year" to 365L * 86_400_000L,
        "Ban 10 Years" to 3650L * 86_400_000L,
        "Ban FOREVER..." to null
    )

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
                currentPath = "/admin",
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
                        text = "🛡️ PT's Admin Panel",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = PtLavender
                    )
                }
                OutlinedButton(onClick = {
                    vm.lockAdminPanel()
                    onBack()
                }) {
                    Text(t("ล็อก Admin", "Lock Admin", "锁定后台"))
                }
            }
        }

        // Sub-Tab Selector
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "DASHBOARD" to "1. Dashboard & Self Actions",
                    "USERS" to "2. Users, Rewards & Bans",
                    "QUESTIONS" to "3. Question Bank & CSV",
                    "SYSTEM" to "4. Quests, Config & Audit Logs"
                ).forEach { (code, label) ->
                    item {
                        FilterChip(
                            selected = adminTab == code,
                            onClick = { adminTab = code },
                            label = { Text(label, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        }

        when (adminTab) {
            "DASHBOARD" -> {
                // Section 15.2: Admin Dashboard Metrics
                item {
                    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📊 Admin Dashboard Overview", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(10.dp))
                            FlowRow(
                                maxItemsInEachRow = 2,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf(
                                    "ผู้เล่นทั้งหมด (Players)" to formatNumber(lang, users.size),
                                    "ชุดสอบที่เสร็จสิ้น (Exams)" to formatNumber(lang, totalExams),
                                    "XP รวมในระบบ" to formatNumber(lang, totalXp),
                                    "PT Points หมุนเวียน" to formatNumber(lang, totalPt),
                                    "จำนวนเปิดไข่สุ่ม (Eggs)" to formatNumber(lang, totalEggs),
                                    "ยอดซื้อร้านแดง-ขาว" to formatNumber(lang, totalPurchases)
                                ).forEach { (k, v) ->
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(k, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(v, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = PtSkyBlue)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 15.3: Admin Quick Actions (Self Account)
                item {
                    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("⚡ Admin Quick Actions (เพิ่มให้บัญชีตัวเองทันที)", fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("เพิ่ม XP:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = PtRosePink)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                xpAmounts.forEach { amt ->
                                    OutlinedButton(onClick = { vm.adminQuickSelfReward(xpAdd = amt) }) {
                                        Text("+${formatNumber(lang, amt)} XP")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("เพิ่ม Level:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = PtSkyBlue)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                levelAmounts.forEach { lvl ->
                                    OutlinedButton(onClick = { vm.adminQuickSelfReward(levelAdd = lvl) }) {
                                        Text("+$lvl Level${if (lvl > 1) "s" else ""}")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("เพิ่ม Streak:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = PtEmerald)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                streakAmounts.forEach { st ->
                                    OutlinedButton(
                                        onClick = { vm.adminQuickSelfReward(streakAdd = st) },
                                        enabled = loggedInUser != null
                                    ) {
                                        Text("+$st Day${if (st > 1) "s" else ""}")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("เพิ่ม PT Points:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = PtSoftGold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                ptAmounts.forEach { pt ->
                                    OutlinedButton(
                                        onClick = { vm.adminQuickSelfReward(ptAdd = pt) },
                                        enabled = loggedInUser != null
                                    ) {
                                        Text("+${formatNumber(lang, pt)} PT")
                                    }
                                }
                            }
                            if (loggedInUser == null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = t("💡 หากเปิด Admin จากหน้า Login สามารถไปที่แท็บ '2. Users' เพื่อแจกรางวัลให้ชื่อบัญชีใดก็ได้!", "💡 Opened from Login screen: use Tab 2 (Users) to grant rewards to any username!", "💡 从登录页进入时，可前往第2标签页向任意账号发放奖励！"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PtSkyBlue
                                )
                            }
                        }
                    }
                }
            }

            "USERS" -> {
                // Section 15.4–15.7: Search Username, Give Rewards, Ban 12 Durations, Suspend, Delete FOREVER!
                item {
                    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🎯 ค้นหาและเลือกผู้เล่นเป้าหมาย (Target Username)", fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = targetUsername,
                                onValueChange = { targetUsername = it },
                                label = { Text("Username เป้าหมาย") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_target_username_input")
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(users, key = { it.id }) { u ->
                                    FilterChip(
                                        selected = targetUsername.equals(u.username, ignoreCase = true),
                                        onClick = { targetUsername = u.username },
                                        label = { Text("@${u.username} (${u.accountStatus})") }
                                    )
                                }
                            }

                            if (matchedUser != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFF0F9FF),
                                    border = BorderStroke(1.dp, PtSkyBlue),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "ข้อมูลก่อนทำรายการ: @${matchedUser.username} (${matchedUser.displayName})",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Status: ${matchedUser.accountStatus} • Lv.${matchedUser.level} • ${matchedUser.xp} XP • ${matchedUser.ptPoints} PT • Streak ${matchedUser.streakDays}d",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Give Rewards to Username
                item {
                    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🎁 Give Rewards to (${targetUsername.ifBlank { "Username" }})", fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Give XP:", fontWeight = FontWeight.Bold, color = PtRosePink)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                xpAmounts.forEach { amt ->
                                    OutlinedButton(
                                        onClick = { vm.adminGiveToUsername(targetUsername, xpAdd = amt) },
                                        enabled = targetUsername.isNotBlank()
                                    ) {
                                        Text("+${formatNumber(lang, amt)} XP")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Give Level:", fontWeight = FontWeight.Bold, color = PtSkyBlue)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                levelAmounts.forEach { lvl ->
                                    OutlinedButton(
                                        onClick = { vm.adminGiveToUsername(targetUsername, levelAdd = lvl) },
                                        enabled = targetUsername.isNotBlank()
                                    ) {
                                        Text("+$lvl Level${if (lvl > 1) "s" else ""}")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Give Streak:", fontWeight = FontWeight.Bold, color = PtEmerald)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                streakAmounts.forEach { st ->
                                    OutlinedButton(
                                        onClick = { vm.adminGiveToUsername(targetUsername, streakAdd = st) },
                                        enabled = targetUsername.isNotBlank()
                                    ) {
                                        Text("+$st Day${if (st > 1) "s" else ""}")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Give PT Points:", fontWeight = FontWeight.Bold, color = PtSoftGold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                ptAmounts.forEach { pt ->
                                    OutlinedButton(
                                        onClick = { vm.adminGiveToUsername(targetUsername, ptAdd = pt) },
                                        enabled = targetUsername.isNotBlank()
                                    ) {
                                        Text("+${formatNumber(lang, pt)} PT")
                                    }
                                }
                            }
                        }
                    }
                }

                // Ban, Suspend, Restore & Delete FOREVER
                item {
                    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("⛔ Ban / Temporary Suspension / Delete Account", fontWeight = FontWeight.ExtraBold, color = NegativeRedAlert)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = moderationReason,
                                onValueChange = { moderationReason = it },
                                label = { Text("เหตุผลการดำเนินการ (Reason)") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                banDurations.forEach { (label, durMs) ->
                                    OutlinedButton(
                                        onClick = {
                                            if (durMs == null) {
                                                showBanForeverConfirm = true
                                            } else {
                                                vm.adminBanUsername(targetUsername, durMs, label, moderationReason)
                                            }
                                        },
                                        enabled = targetUsername.isNotBlank(),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ShopDeepCrimson)
                                    ) {
                                        Text(label)
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { vm.adminSuspendUsername(targetUsername, moderationReason) },
                                    enabled = targetUsername.isNotBlank(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("พักบัญชีชั่วคราว (5 ข้อกู้คืน)")
                                }
                                OutlinedButton(
                                    onClick = { vm.adminRestoreUsername(targetUsername) },
                                    enabled = targetUsername.isNotBlank(),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PtEmerald),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("ปลดแบน / กู้คืนทันที")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { deleteForeverStep = 1 },
                                enabled = targetUsername.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = NegativeRedAlert),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete (${targetUsername.ifBlank { "Username" }}) FOREVER...", color = Color.White, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }

            "QUESTIONS" -> {
                item {
                    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("📚 คลังข้อสอบทั้งหมด (${allQuestions.size} ข้อ)", fontWeight = FontWeight.ExtraBold)
                                PtGradientButton(
                                    text = "+ เพิ่มข้อสอบใหม่",
                                    onClick = { showNewQuestionDialog = true }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = qSearch,
                                onValueChange = { qSearch = it },
                                label = { Text("ค้นหาด้วยเลขข้อ ข้อความ วิชา โรงเรียน หรือชื่อคลัง...") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("CSV Import / Export (รูปแบบ: subjectId,difficulty,topic,questionTh,A,B,C,D,correctAnswer,explanationTh)", style = MaterialTheme.typography.labelSmall)
                            OutlinedTextField(
                                value = csvBoxText,
                                onValueChange = { csvBoxText = it },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { vm.adminImportCsv(csvBoxText) }, enabled = csvBoxText.isNotBlank()) {
                                    Text("Import CSV")
                                }
                                OutlinedButton(onClick = {
                                    vm.adminExportCsv { exported ->
                                        csvBoxText = exported
                                        clipboard.setText(AnnotatedString(exported))
                                        vm.showToast("ส่งออก CSV และคัดลอกไปยังคลิปบอร์ดแล้ว!")
                                    }
                                }) {
                                    Text("Export CSV")
                                }
                            }
                        }
                    }
                }

                val filteredQs = allQuestions.filter { q ->
                    qSearch.isBlank() ||
                        q.questionNumber.toString() == qSearch.trim() ||
                        q.questionTh.contains(qSearch, ignoreCase = true) ||
                        q.subjectId.contains(qSearch, ignoreCase = true) ||
                        q.schoolTag.contains(qSearch, ignoreCase = true) ||
                        q.bankName.contains(qSearch, ignoreCase = true)
                }

                items(filteredQs.take(40), key = { it.id }) { q ->
                    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("#${q.questionNumber} [${q.subjectId} • ${q.difficulty}] (${q.reviewStatus})", fontWeight = FontWeight.Bold, color = PtSkyBlue, style = MaterialTheme.typography.labelMedium)
                                Text(q.questionTh, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                Text("Ans: ${q.correctAnswer} • ${q.schoolTag} • ${q.bankName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { editingQuestion = q }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PtSkyBlue)
                            }
                            IconButton(onClick = { vm.adminDeleteQuestion(q.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = NegativeRedAlert)
                            }
                        }
                    }
                }
            }

            "SYSTEM" -> {
                // Config, Quest Creator, Feedback Reports & Immutable Audit Logs
                item {
                    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("⚙️ ตั้งค่าสูตร XP, แบนเนอร์ข่าวสาร & สร้างภารกิจใหม่", fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = xpFactorInput,
                                    onValueChange = { xpFactorInput = it },
                                    label = { Text("XP Base Factor (ค่าเริ่มต้น 100)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedButton(onClick = { vm.adminUpdateSystemConfig("XP_BASE_FACTOR", xpFactorInput) }) {
                                    Text("บันทึกสูตร XP")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newsBannerInput,
                                    onValueChange = { newsBannerInput = it },
                                    label = { Text("ประกาศหน้าหลัก (News Banner)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedButton(onClick = { vm.adminUpdateSystemConfig("NEWS_BANNER", newsBannerInput) }) {
                                    Text("อัปเดตประกาศ")
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newQuestTitle,
                                    onValueChange = { newQuestTitle = it },
                                    label = { Text("ชื่อภารกิจรายวันใหม่") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedButton(
                                    onClick = {
                                        if (newQuestTitle.isNotBlank()) {
                                            vm.adminCreateQuest(
                                                QuestDefinitionEntity(
                                                    questCode = "CUSTOM_${System.currentTimeMillis()}",
                                                    questScope = "DAILY",
                                                    titleTh = newQuestTitle,
                                                    titleEn = newQuestTitle,
                                                    titleZh = newQuestTitle,
                                                    descTh = "ทำข้อสอบให้ครบ 5 ข้อ",
                                                    descEn = "Complete 5 questions",
                                                    descZh = "完成 5 道试题",
                                                    metricType = "QUESTIONS_DONE",
                                                    targetCount = 5,
                                                    rewardXp = 80,
                                                    rewardPt = 40
                                                )
                                            )
                                            newQuestTitle = ""
                                        }
                                    }
                                ) {
                                    Text("+ เพิ่มภารกิจ")
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newAdminCodeInput,
                                    onValueChange = { newAdminCodeInput = it },
                                    label = { Text(t("ตั้งรหัส Admin เสริมใหม่ (6+ ตัว)", "Additional Custom Admin Code (6+)", "新增自定义管理员密码(6+位)")) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedButton(
                                    onClick = {
                                        vm.adminSetCustomAdminPasscode(newAdminCodeInput)
                                        newAdminCodeInput = ""
                                    },
                                    enabled = newAdminCodeInput.trim().length >= 6
                                ) {
                                    Text(t("บันทึกรหัส", "Save Code", "保存密码"))
                                }
                            }
                        }
                    }
                }

                if (reports.isNotEmpty()) {
                    item {
                        Text("📩 รายงานแจ้งปัญหาจากผู้เล่น (${reports.size})", fontWeight = FontWeight.ExtraBold)
                    }
                    items(reports.take(15), key = { "rep_${it.id}" }) { r ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("@${r.username} [${r.category}]", fontWeight = FontWeight.Bold)
                                    Text(r.message, style = MaterialTheme.typography.bodySmall)
                                }
                                if (!r.isResolved) {
                                    OutlinedButton(onClick = { vm.adminResolveReport(r.id) }) {
                                        Text("แก้ไขแล้ว")
                                    }
                                } else {
                                    Text("✅", color = PtEmerald)
                                }
                            }
                        }
                    }
                }

                item {
                    Text("🔒 Admin Audit Logs (บันทึกถาวรห้ามลบ • ${auditLogs.size} รายการ)", fontWeight = FontWeight.ExtraBold)
                }
                items(auditLogs.take(30), key = { "log_${it.id}" }) { log ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("${log.actionType} ➔ @${log.targetUsername} [${log.result}]", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            Text("Before: ${log.oldValue} | After: ${log.newValue}", style = MaterialTheme.typography.labelSmall)
                            Text("Reason: ${log.reason} • ${formatDateTime(lang, log.timestampMillis)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    // Ban FOREVER Confirmation Dialog
    if (showBanForeverConfirm) {
        AlertDialog(
            onDismissRequest = { showBanForeverConfirm = false },
            title = { Text("ยืนยันการแบนถาวร (Ban FOREVER)?", color = NegativeRedAlert, fontWeight = FontWeight.ExtraBold) },
            text = { Text("ต้องการระงับสิทธิ์บัญชี @$targetUsername แบบถาวรใช่หรือไม่? เหตุผล: $moderationReason") },
            confirmButton = {
                Button(
                    onClick = {
                        showBanForeverConfirm = false
                        vm.adminBanUsername(targetUsername, null, "FOREVER", moderationReason)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NegativeRedAlert)
                ) {
                    Text("ยืนยันแบนถาวร", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanForeverConfirm = false }) { Text("ยกเลิก") }
            }
        )
    }

    // Delete (Username) FOREVER Two-Step Double Confirmation Dialogs!
    if (deleteForeverStep == 1) {
        AlertDialog(
            onDismissRequest = { deleteForeverStep = 0 },
            title = { Text("⚠️ ยืนยันขั้นที่ 1/2: ลบบัญชี @$targetUsername ถาวร?", color = NegativeRedAlert, fontWeight = FontWeight.ExtraBold) },
            text = { Text("คำสั่งนี้จะล้างสัตว์เลี้ยง พื้นหลัง คะแนน และอันดับของ @$targetUsername ออกจากระบบทั้งหมด กรุณากดยืนยันเพื่อไปขั้นที่ 2") },
            confirmButton = {
                Button(onClick = { deleteForeverStep = 2 }, colors = ButtonDefaults.buttonColors(containerColor = NegativeRedAlert)) {
                    Text("ไปขั้นยืนยันสุดท้าย (2/2)", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteForeverStep = 0 }) { Text("ยกเลิก") }
            }
        )
    } else if (deleteForeverStep == 2) {
        AlertDialog(
            onDismissRequest = { deleteForeverStep = 0 },
            title = { Text("🚨 ยืนยันขั้นสุดท้าย (2/2): Delete ($targetUsername) FOREVER!", color = NegativeRedAlert, fontWeight = FontWeight.ExtraBold) },
            text = { Text("ไม่สามารถกู้คืนได้อีก! ยืนยันลบบัญชี @$targetUsername ถาวรเดี๋ยวนี้หรือไม่?") },
            confirmButton = {
                Button(
                    onClick = {
                        val target = targetUsername
                        deleteForeverStep = 0
                        targetUsername = ""
                        vm.adminDeleteUsernameForever(target, moderationReason)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NegativeRedAlert)
                ) {
                    Text("ยืนยันลบถาวรทันที!", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteForeverStep = 0 }) { Text("ยกเลิก") }
            }
        )
    }

    // Add / Edit Question Dialog
    if (showNewQuestionDialog || editingQuestion != null) {
        val initial = editingQuestion
        QuestionEditorDialog(
            initial = initial,
            nextQuestionNumber = allQuestions.size + 1,
            onDismiss = {
                showNewQuestionDialog = false
                editingQuestion = null
            },
            onSave = { entity, isNew ->
                vm.adminSaveQuestion(entity, isNew)
                showNewQuestionDialog = false
                editingQuestion = null
            }
        )
    }
}

@Composable
private fun QuestionEditorDialog(
    initial: QuestionEntity?,
    nextQuestionNumber: Int,
    onDismiss: () -> Unit,
    onSave: (QuestionEntity, Boolean) -> Unit
) {
    var subjectId by rememberSaveable { mutableStateOf(initial?.subjectId ?: SubjectId.MATH.id) }
    var difficulty by rememberSaveable { mutableStateOf(initial?.difficulty ?: DifficultyLevel.NORMAL.id) }
    var topic by rememberSaveable { mutableStateOf(initial?.topic ?: "แนวข้อสอบเข้า ม.1") }
    var qTh by rememberSaveable { mutableStateOf(initial?.questionTh ?: "") }
    var cA by rememberSaveable { mutableStateOf(initial?.choiceATh ?: "") }
    var cB by rememberSaveable { mutableStateOf(initial?.choiceBTh ?: "") }
    var cC by rememberSaveable { mutableStateOf(initial?.choiceCTh ?: "") }
    var cD by rememberSaveable { mutableStateOf(initial?.choiceDTh ?: "") }
    var correct by rememberSaveable { mutableStateOf(initial?.correctAnswer ?: "A") }
    var expTh by rememberSaveable { mutableStateOf(initial?.explanationTh ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "เพิ่มข้อสอบใหม่" else "แก้ไขข้อสอบ #${initial.questionNumber}", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(380.dp)) {
                item {
                    OutlinedTextField(value = subjectId, onValueChange = { subjectId = it.uppercase() }, label = { Text("Subject ID (เช่น MATH, SCIENCE, THAI)") }, singleLine = true)
                }
                item {
                    OutlinedTextField(value = difficulty, onValueChange = { difficulty = it.uppercase() }, label = { Text("Difficulty (EASY, NORMAL, HARD, EXPERT, ENTRANCE_EXAM)") }, singleLine = true)
                }
                item {
                    OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("หัวข้อบทเรียน") }, singleLine = true)
                }
                item {
                    OutlinedTextField(value = qTh, onValueChange = { qTh = it }, label = { Text("คำถาม (ภาษาไทย)") })
                }
                item { OutlinedTextField(value = cA, onValueChange = { cA = it }, label = { Text("ตัวเลือก A") }, singleLine = true) }
                item { OutlinedTextField(value = cB, onValueChange = { cB = it }, label = { Text("ตัวเลือก B") }, singleLine = true) }
                item { OutlinedTextField(value = cC, onValueChange = { cC = it }, label = { Text("ตัวเลือก C") }, singleLine = true) }
                item { OutlinedTextField(value = cD, onValueChange = { cD = it }, label = { Text("ตัวเลือก D") }, singleLine = true) }
                item { OutlinedTextField(value = correct, onValueChange = { correct = it.uppercase() }, label = { Text("คำตอบที่ถูก (A/B/C/D)") }, singleLine = true) }
                item { OutlinedTextField(value = expTh, onValueChange = { expTh = it }, label = { Text("คำอธิบายเฉลยละเอียด") }) }
            }
        },
        confirmButton = {
            PtGradientButton(
                text = "บันทึกข้อสอบ",
                onClick = {
                    val saved = (initial ?: QuestionEntity(
                        questionNumber = nextQuestionNumber,
                        subjectId = subjectId,
                        topic = topic,
                        questionTh = qTh,
                        questionEn = qTh,
                        questionZh = qTh,
                        choiceATh = cA, choiceBTh = cB, choiceCTh = cC, choiceDTh = cD,
                        choiceAEn = cA, choiceBEn = cB, choiceCEn = cC, choiceDEn = cD,
                        choiceAZh = cA, choiceBZh = cB, choiceCZh = cC, choiceDZh = cD,
                        correctAnswer = correct.trim(),
                        explanationTh = expTh,
                        explanationEn = expTh,
                        explanationZh = expTh
                    )).copy(
                        subjectId = subjectId,
                        difficulty = difficulty,
                        topic = topic,
                        questionTh = qTh,
                        choiceATh = cA, choiceBTh = cB, choiceCTh = cC, choiceDTh = cD,
                        correctAnswer = correct.trim(),
                        explanationTh = expTh,
                        questionVersion = (initial?.questionVersion ?: 0) + 1
                    )
                    onSave(saved, initial == null)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("ยกเลิก") }
        }
    )
}
