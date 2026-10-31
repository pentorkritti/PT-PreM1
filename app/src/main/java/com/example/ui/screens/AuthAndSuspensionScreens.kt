package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.QuestionEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.PlayerGender
import com.example.i18n.t
import com.example.ui.components.DecoratedGenderSelector
import com.example.ui.components.HttpsPortalBar
import com.example.ui.components.MascotMood
import com.example.ui.components.PtGlassCard
import com.example.ui.components.PtGradientButton
import com.example.ui.components.PtOwlMascot
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
import com.example.util.SecurityUtils
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthGatewayScreen(
    vm: GameViewModel,
    onAuthenticated: () -> Unit,
    onOpenAdminPanel: (() -> Unit)? = null
) {
    val lang = LocalAppLanguage.current
    var selectedGenderCode by rememberSaveable { mutableStateOf(PlayerGender.UNSPECIFIED.code) }
    val selectedGender = PlayerGender.fromCode(selectedGenderCode)

    var username by rememberSaveable { mutableStateOf("") }
    var displayName by rememberSaveable { mutableStateOf("") }
    var passwordOrPin by rememberSaveable { mutableStateOf("") }
    var recoveryCodeInput by rememberSaveable { mutableStateOf(SecurityUtils.generateRecoveryCode()) }
    var avatarIndex by rememberSaveable { mutableIntStateOf(0) }
    var showSecret by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        // Official HTTPS Portal Header Bar (`https://PTPreM1.online`)
        HttpsPortalBar(
            currentPath = "/login",
            onCopiedToast = { vm.showToast(it) },
            modifier = Modifier.widthIn(max = 620.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Top Bar: Instant 3-Language Switcher (TH / EN / ZH) + Quick Admin Access Shield
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppLanguage.entries.forEach { option ->
                FilterChip(
                    selected = lang == option,
                    onClick = { vm.setLanguage(option) },
                    label = { Text("${option.flag} ${option.label}", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("auth_lang_${option.code}")
                )
            }
            if (onOpenAdminPanel != null) {
                IconButton(
                    onClick = onOpenAdminPanel,
                    modifier = Modifier.testTag("auth_quick_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = t("ผู้ดูแลระบบ Admin", "Admin Panel", "管理员入口"),
                        tint = PtLavender
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Animated Owl Mascot & Rainbow Gradient Hero Title
        PtOwlMascot(mood = MascotMood.CELEBRATE_REWARD, size = 96.dp)

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "PT's มาสอบเข้า ม.1!",
            style = TextStyle(
                brush = Brush.horizontalGradient(
                    listOf(PtSkyBlue, RainbowMint, PtRosePink, RainbowIris)
                ),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = t(
                "กรอกข้อมูลบัญชีครบทั้ง 5 ช่องในหน้าเดียวเพื่อสมัครฟรี (+250 PT) หรือเข้าสู่ระบบได้ทันที! (แก้ไขได้ทุกช่องภายหลัง)",
                "All 5 account fields on one unified screen! Sign up free (+250 PT) or log in instantly — every field is editable anytime!",
                "单页完整展示5项账号资料！免费注册立领 +250 PT 或直接登录，所有资料随时可修改！"
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(max = 560.dp)
                .padding(top = 4.dp, bottom = 14.dp)
        )

        // Unified 5-Field Rainbow Pastel Account Card
        PtGlassCard(
            borderColors = listOf(RainbowCoral, RainbowAqua, RainbowMint, RainbowIris),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 580.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Rainbow Header Strip
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PtSkyBlue.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(PtSkyBlue, PtRosePink))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = "🌈", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = t(
                                "ศูนย์บัญชีผู้เล่น PTPreM1.online (ครบ 5 ข้อมูล)",
                                "PTPreM1.online 5-Field Player Account",
                                "PTPreM1.online 玩家五项全能账号中心"
                            ),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Avatar Picker Strip
                Text(
                    text = t("🎨 เลือกอวตารคู่ใจ", "🎨 Choose Your Avatar", "🎨 选择专属头像"),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlayerAvatarCatalog.avatars.indices.forEach { idx ->
                        val selected = avatarIndex == idx
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .border(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) PtRosePink else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { avatarIndex = idx }
                                .padding(2.dp)
                        ) {
                            PlayerAvatarBadge(avatarIndex = idx, size = 40.dp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // FIELD 1: เพศ (ชาย / หญิง / ไม่ระบุ) — Decorated Interactive Buttons!
                Text(
                    text = t(
                        "1. เพศ (ชาย / หญิง / ไม่ระบุ) ✨",
                        "1. Gender (Male / Female / Unspecified) ✨",
                        "1. 性别（男 / 女 / 不透露）✨"
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = PtRosePink
                )
                Spacer(modifier = Modifier.height(6.dp))
                DecoratedGenderSelector(
                    selectedGender = selectedGender,
                    onSelectGender = { selectedGenderCode = it.code }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // FIELD 2: ชื่อบัญชี (Account Name / Username)
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = {
                        Text(
                            t(
                                "2. ชื่อบัญชี (Account Name / Username)",
                                "2. Account Name (Username)",
                                "2. 账号名称（Username 用户名）"
                            )
                        )
                    },
                    placeholder = { Text("e.g. pentor_m1") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PtSkyBlue) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_username_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // FIELD 3: ชื่อแสดง (Display Name)
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = {
                        Text(
                            t(
                                "3. ชื่อแสดง (Display Name ในเกม)",
                                "3. Display Name (Shown in game)",
                                "3. 显示昵称（游戏内展示名称）"
                            )
                        )
                    },
                    placeholder = { Text(t("เช่น น้องสายรุ้ง ม.1", "e.g. Rainbow Scholar", "例如：彩虹学霸")) },
                    leadingIcon = { Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = RainbowCoral) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_displayname_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // FIELD 4: รหัสบัญชี (Account Password)
                OutlinedTextField(
                    value = passwordOrPin,
                    onValueChange = { passwordOrPin = it },
                    label = {
                        Text(
                            t(
                                "4. รหัสบัญชี (Account Password อย่างน้อย 4 ตัว)",
                                "4. Account Password (min 4 chars)",
                                "4. 账号密码（至少4位）"
                            )
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = RainbowIris) },
                    trailingIcon = {
                        IconButton(onClick = { showSecret = !showSecret }) {
                            Icon(
                                if (showSecret) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = t("แสดงรหัสบัญชี", "Show password", "显示密码")
                            )
                        }
                    },
                    visualTransformation = if (showSecret) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_password_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // FIELD 5: รหัสกู้คืนบัญชี (Account Recovery Code) — Custom or 1-Tap Randomizer!
                OutlinedTextField(
                    value = recoveryCodeInput,
                    onValueChange = { recoveryCodeInput = it.uppercase() },
                    label = {
                        Text(
                            t(
                                "5. รหัสกู้คืนบัญชี (ตั้งเองได้ หรือกดสุ่ม 🎲)",
                                "5. Account Recovery Code (Custom or Random 🎲)",
                                "5. 账号恢复码（可自定义或点击随机生成 🎲）"
                            )
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = PtEmerald) },
                    trailingIcon = {
                        IconButton(
                            onClick = { recoveryCodeInput = SecurityUtils.generateRecoveryCode() },
                            modifier = Modifier.testTag("randomize_recovery_code_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = t("สุ่มรหัสกู้คืนใหม่", "Generate Random Recovery Code", "随机生成恢复码"),
                                tint = PtEmerald
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_recovery_input")
                )

                Text(
                    text = t(
                        "💡 เคล็ดลับ: ตอนเข้าสู่ระบบใช้แค่ช่อง 2 + 4 ได้เลย! และเมื่อเข้าเกมแล้วสามารถแก้ไขทั้ง 5 ช่องได้ตลอดเวลาในหน้าโปรไฟล์",
                        "💡 Tip: To Log In, only Fields 2 & 4 are needed! Once inside, all 5 fields can be edited anytime in Profile.",
                        "💡 提示：老玩家登录只需填写第 2 和第 4 项！进入游戏后可在个人档案随时修改全部 5 项！"
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                )

                // PRIMARY ACTION 1: Sign Up with all 5 fields (+250 PT!)
                PtGradientButton(
                    text = t(
                        "✨ สมัครบัญชีใหม่ (บันทึกครบทั้ง 5 ช่อง • รับฟรี +250 PT!)",
                        "✨ Sign Up New Account (Save All 5 Fields • +250 PT!)",
                        "✨ 注册新账号（保存全部5项 • 立领 +250 PT！）"
                    ),
                    onClick = {
                        vm.signUpFiveFields(
                            gender = selectedGender,
                            username = username,
                            displayName = displayName.ifBlank { username },
                            passwordOrPin = passwordOrPin,
                            recoveryCode = recoveryCodeInput,
                            avatarIndex = avatarIndex,
                            onSuccess = onAuthenticated
                        )
                    },
                    gradientColors = listOf(RainbowCoral, PtRosePink, RainbowIris),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "signup_submit_button"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // PRIMARY ACTION 2: Log In (Uses Account Name #2 + Account Password #4)
                PtGradientButton(
                    text = t(
                        "🚀 เข้าสู่ระบบทันที (ใช้ชื่อบัญชี #2 + รหัสบัญชี #4)",
                        "🚀 Log In Existing Account (Fields #2 + #4)",
                        "🚀 直接登录已有账号（使用第2项 + 第4项）"
                    ),
                    onClick = { vm.logIn(username, passwordOrPin, onAuthenticated) },
                    gradientColors = listOf(PtSkyBlue, PtElectricCyan, RainbowMint),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "login_submit_button"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // SECONDARY ROW: Recover Password using #2 + #5 + New #4  AND  Guest Mode
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            vm.recoverAccount(username, recoveryCodeInput, passwordOrPin) {}
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, PtEmerald),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("recover_submit_button")
                    ) {
                        Text(
                            text = t("🔑 กู้รหัส (#2+#5➔#4)", "🔑 Recover (#2+#5)", "🔑 恢复密码"),
                            fontWeight = FontWeight.Bold,
                            color = PtEmerald,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }

                    OutlinedButton(
                        onClick = { vm.startGuestMode(onAuthenticated) },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, PtSkyBlue),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("guest_mode_button")
                    ) {
                        Text(
                            text = t("🎮 ทดลองเล่น Guest", "🎮 Guest Mode", "🎮 游客试玩"),
                            fontWeight = FontWeight.Bold,
                            color = PtSkyBlue,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                }

                if (onOpenAdminPanel != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    TextButton(
                        onClick = onOpenAdminPanel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_bottom_admin_button")
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = PtLavender, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = t(
                                "🛡️ เข้าสู่แผงควบคุม Admin ด้วยรหัสผู้ดูแล",
                                "🛡️ Enter Admin Panel with Admin Passcode",
                                "🛡️ 使用管理员密码进入后台"
                            ),
                            color = PtLavender,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun WelcomeCelebrationDialog(
    recoveryCode: String,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        icon = {
            PtOwlMascot(mood = MascotMood.CELEBRATE_REWARD, size = 76.dp)
        },
        title = {
            Text(
                text = t(
                    "🌈 ยินดีต้อนรับสู่ PTPreM1.online!",
                    "🌈 Welcome to PTPreM1.online!",
                    "🌈 欢迎来到 PTPreM1.online！"
                ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = PtSkyBlue
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = t(
                        "คุณได้รับของขวัญต้อนรับ +250 PT Points + ตั๋วหมุนวงล้อฟรี + กล่องสมบัติ 1 กล่อง! รหัสกู้คืนบัญชีของคุณคือ:",
                        "You received +250 PT Points + Free Lucky Wheel Spin + 1 Mystery Chest! Your Account Recovery Code is:",
                        "您获得了 +250 PT 欢迎积分、免费转盘券与1个神秘宝箱！您的账号恢复码如下："
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE0F2FE),
                    border = BorderStroke(1.5.dp, PtSkyBlue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            text = recoveryCode,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0369A1)
                        )
                        TextButton(
                            onClick = { clipboard.setText(AnnotatedString(recoveryCode)) }
                        ) {
                            Text(t("คัดลอกรหัสกู้คืนบัญชี", "Copy Recovery Code", "复制账号恢复码"))
                        }
                    }
                }
            }
        },
        confirmButton = {
            PtGradientButton(
                text = t("เริ่มการผจญภัยเลย!", "Start Adventure!", "立即开启冒险！"),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                testTag = "welcome_dialog_confirm"
            )
        }
    )
}

@Composable
fun LevelUpCelebrationDialog(
    newLevel: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        icon = {
            PtOwlMascot(mood = MascotMood.CELEBRATE_REWARD, size = 84.dp)
        },
        title = {
            Text(
                text = t(
                    "🎉 เลเวลอัป! ก้าวสู่ Level $newLevel!",
                    "🎉 LEVEL UP! Welcome to Level $newLevel!",
                    "🎉 恭喜升级！达到 Level $newLevel！"
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = PtRosePink
            )
        },
        text = {
            Text(
                text = t(
                    "สุดยอดมาก! พลังความรู้ของคุณเพิ่มขึ้นอีกขั้น ก้าวใกล้โรงเรียน ม.1 ในฝันเข้าไปทุกทีแล้ว!",
                    "Awesome work! Your academic power just soared closer to your dream M.1 school!",
                    "太棒了！你的知识能量再度进化，离梦想初一更近一步！"
                ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            PtGradientButton(
                text = t("ลุยต่อเลย!", "Awesome!", "继续前进！"),
                onClick = onDismiss,
                gradientColors = listOf(PtRosePink, PtSoftGold, PtSkyBlue),
                modifier = Modifier.fillMaxWidth()
            )
        }
    )
}

@Composable
fun SuspensionRecoveryScreen(
    user: UserEntity,
    questions: List<QuestionEntity>,
    vm: GameViewModel
) {
    val lang = LocalAppLanguage.current
    val completed = user.suspensionRecoveryCompletedCount.coerceIn(0, 5)
    val pool = remember(questions) {
        questions.filter { it.correctAnswer.uppercase() in setOf("A", "B", "C", "D") }.ifEmpty { questions }
    }
    var qCursor by rememberSaveable { mutableIntStateOf(0) }
    val currentQuestion = pool.getOrNull(qCursor % pool.size.coerceAtLeast(1))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = NegativeRedAlert,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = t(
                "บัญชีของคุณถูกพักชั่วคราวโดย Admin",
                "Temporary Account Suspension",
                "您的账号已被管理员暂时停用"
            ),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = NegativeRedAlert,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = t(
                "คุณสามารถทำแบบฝึกหัดให้ถูกครบ 5 ข้อเพื่อปลดล็อกบัญชีอัตโนมัติได้ทันที!",
                "Answer 5 practice questions correctly to automatically restore your account!",
                "正确完成 5 道练习题即可自动恢复账号！"
            ),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 520.dp)
        )
        if (user.banReason.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${t("เหตุผล", "Reason", "原因")}: ${user.banReason}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        PtGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 540.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = t("ความคืบหน้าการกู้คืนบัญชี", "Recovery Progress", "恢复进度"),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$completed / 5",
                        color = PtEmerald,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { completed / 5f },
                    color = PtEmerald,
                    trackColor = Color(0xFFE2E8F0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(50))
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (currentQuestion != null) {
                    val qText = when (lang) {
                        AppLanguage.TH -> currentQuestion.questionTh
                        AppLanguage.EN -> currentQuestion.questionEn
                        AppLanguage.ZH -> currentQuestion.questionZh
                    }
                    Text(
                        text = qText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val options = listOf(
                        "A" to when (lang) { AppLanguage.TH -> currentQuestion.choiceATh; AppLanguage.EN -> currentQuestion.choiceAEn; AppLanguage.ZH -> currentQuestion.choiceAZh },
                        "B" to when (lang) { AppLanguage.TH -> currentQuestion.choiceBTh; AppLanguage.EN -> currentQuestion.choiceBEn; AppLanguage.ZH -> currentQuestion.choiceBZh },
                        "C" to when (lang) { AppLanguage.TH -> currentQuestion.choiceCTh; AppLanguage.EN -> currentQuestion.choiceCEn; AppLanguage.ZH -> currentQuestion.choiceCZh },
                        "D" to when (lang) { AppLanguage.TH -> currentQuestion.choiceDTh; AppLanguage.EN -> currentQuestion.choiceDEn; AppLanguage.ZH -> currentQuestion.choiceDZh }
                    )

                    options.forEach { (letter, label) ->
                        OutlinedButton(
                            onClick = {
                                val ok = letter.equals(currentQuestion.correctAnswer.trim(), ignoreCase = true)
                                vm.completeOneSuspensionExercise(ok)
                                if (ok) qCursor++
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PtSkyBlue)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("$letter. $label", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedButton(
            onClick = { vm.logOut() },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NegativeRedAlert)
        ) {
            Text(t("ออกจากระบบ", "Log Out", "退出登录"))
        }
    }
}
