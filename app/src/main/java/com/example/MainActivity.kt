package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.formatDateTime
import com.example.i18n.t
import com.example.i18n.tr
import com.example.ui.components.MascotMood
import com.example.ui.components.PtGlassCard
import com.example.ui.components.PtOwlMascot
import com.example.ui.components.PtWorldBackground
import com.example.ui.screens.ActiveExamRoomScreen
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.AppRoute
import com.example.ui.screens.AuthGatewayScreen
import com.example.ui.screens.BackgroundCollectionScreen
import com.example.ui.screens.BossRaidScreen
import com.example.ui.screens.DailyRewardsPlazaScreen
import com.example.ui.screens.ExamResultScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.LeaderboardsScreen
import com.example.ui.screens.LevelUpCelebrationDialog
import com.example.ui.screens.MiniGamesHubScreen
import com.example.ui.screens.PetInventoryScreen
import com.example.ui.screens.PetZoneScreen
import com.example.ui.screens.ProfileAndStatsScreen
import com.example.ui.screens.PtShopScreen
import com.example.ui.screens.QuestsAndChallengesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartStudyHubScreen
import com.example.ui.screens.StudySheetLibraryScreen
import com.example.ui.screens.SubjectAndExamHubScreen
import com.example.ui.screens.SuspensionRecoveryScreen
import com.example.ui.screens.WelcomeCelebrationDialog
import com.example.ui.screens.parseHttpsDeepLinkRoute
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NegativeRedAlert
import com.example.ui.theme.PtSkyBlue
import com.example.util.BgmScene
import com.example.util.SoundEngine
import com.example.util.configureAppCheck
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val deepLinkUrlState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        deepLinkUrlState.value = intent?.data?.toString()
        enableEdgeToEdge()
        setContent {
            PtsEducationalGameRoot(
                deepLinkUrl = deepLinkUrlState.value,
                onDeepLinkConsumed = { deepLinkUrlState.value = null }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkUrlState.value = intent.data?.toString()
    }

    override fun onStop() {
        super.onStop()
        SoundEngine.setScene(BgmScene.NONE)
    }
}

private data class PrimaryNavItem(
    val route: AppRoute,
    val labelTh: String,
    val labelEn: String,
    val labelZh: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun PtsEducationalGameRoot(
    vm: GameViewModel = viewModel(),
    deepLinkUrl: String? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val isBootstrapping by vm.isBootstrapping.collectAsState()
    val currentUser by vm.currentUser.collectAsState()
    val guestLang by vm.guestLanguage.collectAsState()
    val toastMsg by vm.toastMessage.collectAsState()
    val welcomeCode by vm.welcomeCelebrationCode.collectAsState()
    val levelUpLvl by vm.levelUpCelebration.collectAsState()
    val allQuestions by vm.allQuestions.collectAsState()

    var currentRoute by rememberSaveable { mutableStateOf(AppRoute.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }

    val activeLanguage = remember(currentUser?.languageCode, guestLang) {
        if (currentUser != null) AppLanguage.fromCode(currentUser?.languageCode) else guestLang
    }

    // Official https://PTPreM1.online HTTPS App Link & Deep-Link routing
    LaunchedEffect(deepLinkUrl) {
        val targetRoute = parseHttpsDeepLinkRoute(deepLinkUrl)
        if (targetRoute != null) {
            currentRoute = targetRoute
            onDeepLinkConsumed()
        }
    }

    LaunchedEffect(toastMsg) {
        val m = toastMsg
        if (!m.isNullOrBlank()) {
            snackbarHostState.showSnackbar(m)
            vm.clearToast()
        }
    }

    // Scene-specific procedural BGM switching
    LaunchedEffect(currentUser?.id, currentRoute) {
        if (currentUser == null) {
            SoundEngine.setScene(BgmScene.NONE)
        } else {
            val scene = when (currentRoute) {
                AppRoute.ACTIVE_EXAM, AppRoute.BOSS_RAID -> BgmScene.EXAM
                AppRoute.PT_SHOP, AppRoute.DAILY_REWARDS_PLAZA -> BgmScene.SHOP
                AppRoute.PET_ZONE, AppRoute.PET_INVENTORY -> BgmScene.PET_ZONE
                AppRoute.EXAM_RESULT -> BgmScene.RESULT
                else -> BgmScene.HOME
            }
            SoundEngine.setScene(scene)
        }
    }

    DisposableEffect(Unit) {
        onDispose { SoundEngine.setScene(BgmScene.NONE) }
    }

    val navItems = remember {
        listOf(
            PrimaryNavItem(AppRoute.HOME, "หน้าหลัก", "Home", "主页", Icons.Default.Home, "bottom_nav_home"),
            PrimaryNavItem(AppRoute.SUBJECT_HUB, "ข้อสอบ", "Exam", "题库", Icons.Default.Quiz, "bottom_nav_exam"),
            PrimaryNavItem(AppRoute.PT_SHOP, "ร้านค้า", "Shop", "商店", Icons.Default.Storefront, "bottom_nav_shop"),
            PrimaryNavItem(AppRoute.PET_ZONE, "สัตว์เลี้ยง", "Pets", "伙伴", Icons.Default.Pets, "bottom_nav_pets"),
            PrimaryNavItem(AppRoute.PROFILE_STATS, "โปรไฟล์", "Profile", "我的", Icons.Default.Person, "bottom_nav_profile")
        )
    }

    val railNavItems = remember {
        listOf(
            PrimaryNavItem(AppRoute.HOME, "หน้าหลัก", "Home", "主页", Icons.Default.Home, "bottom_nav_home"),
            PrimaryNavItem(AppRoute.SUBJECT_HUB, "ข้อสอบ", "Exam", "题库", Icons.Default.Quiz, "bottom_nav_exam"),
            PrimaryNavItem(AppRoute.SMART_STUDY_HUB, "ทบทวน", "Study", "温习", Icons.Default.MenuBook, "bottom_nav_study"),
            PrimaryNavItem(AppRoute.BOSS_RAID, "ดวลบอส", "Boss", "魔王", Icons.Default.Whatshot, "bottom_nav_boss"),
            PrimaryNavItem(AppRoute.DAILY_REWARDS_PLAZA, "วงล้อ", "Wheel", "转盘", Icons.Default.Casino, "bottom_nav_wheel"),
            PrimaryNavItem(AppRoute.PT_SHOP, "ร้านค้า", "Shop", "商店", Icons.Default.Storefront, "bottom_nav_shop"),
            PrimaryNavItem(AppRoute.PET_ZONE, "สัตว์เลี้ยง", "Pets", "伙伴", Icons.Default.Pets, "bottom_nav_pets"),
            PrimaryNavItem(AppRoute.PROFILE_STATS, "โปรไฟล์", "Profile", "我的", Icons.Default.Person, "bottom_nav_profile")
        )
    }

    CompositionLocalProvider(LocalAppLanguage provides activeLanguage) {
        MyApplicationTheme(premiumUiEnabled = currentUser?.premiumUiEnabled == true) {
            PtWorldBackground(
                activeBackgroundId = currentUser?.activeBackgroundId ?: "BG_DEFAULT",
                reducedMotion = currentUser?.reducedMotion == true
            ) {
                if (isBootstrapping) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PtOwlMascot(mood = MascotMood.IDLE_WAVE, size = 96.dp)
                            Spacer(modifier = Modifier.height(14.dp))
                            CircularProgressIndicator(color = PtSkyBlue)
                        }
                    }
                } else {
                    val user = currentUser
                    val now = System.currentTimeMillis()
                    val isCurrentlyBanned = user != null && (
                        user.accountStatus == "DELETED" ||
                            (user.accountStatus == "BANNED" && (user.banUntilMillis == Long.MAX_VALUE || user.banUntilMillis > now))
                        )

                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val isExpandedScreen = maxWidth >= 600.dp
                        val showNavChrome = user != null &&
                            !isCurrentlyBanned &&
                            user.accountStatus != "SUSPENDED" &&
                            currentRoute != AppRoute.ACTIVE_EXAM &&
                            currentRoute != AppRoute.ADMIN_PANEL

                        Scaffold(
                            containerColor = Color.Transparent,
                            contentWindowInsets = WindowInsets(0, 0, 0, 0),
                            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                            bottomBar = {
                                if (showNavChrome && !isExpandedScreen) {
                                    NavigationBar(
                                        containerColor = Color.White.copy(alpha = 0.94f),
                                        modifier = Modifier.navigationBarsPadding()
                                    ) {
                                        navItems.forEach { item ->
                                            val selected = currentRoute == item.route ||
                                                (item.route == AppRoute.SUBJECT_HUB && currentRoute in setOf(
                                                    AppRoute.PRACTICE_CENTER,
                                                    AppRoute.EXAM_CENTER,
                                                    AppRoute.SMART_STUDY_HUB,
                                                    AppRoute.BOSS_RAID
                                                )) ||
                                                (item.route == AppRoute.PT_SHOP && currentRoute in setOf(
                                                    AppRoute.DAILY_REWARDS_PLAZA,
                                                    AppRoute.BACKGROUND_COLLECTION
                                                )) ||
                                                (item.route == AppRoute.PET_ZONE && currentRoute == AppRoute.PET_INVENTORY) ||
                                                (item.route == AppRoute.PROFILE_STATS && currentRoute == AppRoute.SETTINGS)
                                            NavigationBarItem(
                                                selected = selected,
                                                onClick = { currentRoute = item.route },
                                                icon = { Icon(item.icon, contentDescription = tr(activeLanguage, item.labelTh, item.labelEn, item.labelZh)) },
                                                label = { Text(tr(activeLanguage, item.labelTh, item.labelEn, item.labelZh)) },
                                                modifier = Modifier.testTag(item.tag)
                                            )
                                        }
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                // Wide-Screen / Web-Portal https://PTPreM1.online Adaptive NavigationRail
                                if (showNavChrome && isExpandedScreen) {
                                    NavigationRail(
                                        containerColor = Color.White.copy(alpha = 0.94f),
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .statusBarsPadding()
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        railNavItems.forEach { item ->
                                            val selected = currentRoute == item.route
                                            NavigationRailItem(
                                                selected = selected,
                                                onClick = { currentRoute = item.route },
                                                icon = { Icon(item.icon, contentDescription = tr(activeLanguage, item.labelTh, item.labelEn, item.labelZh)) },
                                                label = { Text(tr(activeLanguage, item.labelTh, item.labelEn, item.labelZh)) },
                                                modifier = Modifier.testTag("rail_${item.tag}")
                                            )
                                        }
                                    }
                                }

                                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                    when {
                                        currentRoute == AppRoute.ADMIN_PANEL -> {
                                            AdminPanelScreen(
                                                vm = vm,
                                                onBack = {
                                                    currentRoute = if (user == null) AppRoute.HOME else AppRoute.SETTINGS
                                                }
                                            )
                                        }
                                        user == null -> {
                                            AuthGatewayScreen(
                                                vm = vm,
                                                onAuthenticated = { currentRoute = AppRoute.HOME },
                                                onOpenAdminPanel = { currentRoute = AppRoute.ADMIN_PANEL }
                                            )
                                        }
                                        isCurrentlyBanned -> {
                                            BannedAccountScreen(
                                                username = user.username,
                                                banUntilMillis = user.banUntilMillis,
                                                reason = user.banReason,
                                                onLogout = { vm.logOut() }
                                            )
                                        }
                                        user.accountStatus == "SUSPENDED" -> {
                                            SuspensionRecoveryScreen(
                                                user = user,
                                                questions = allQuestions,
                                                vm = vm
                                            )
                                        }
                                        else -> {
                                            when (currentRoute) {
                                                AppRoute.HOME -> HomeDashboardScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onNavigate = { dest -> currentRoute = dest }
                                                )
                                                AppRoute.PRACTICE_CENTER -> SubjectAndExamHubScreen(
                                                    initialMode = "PRACTICE",
                                                    user = user,
                                                    vm = vm,
                                                    onStartActiveExam = { currentRoute = AppRoute.ACTIVE_EXAM },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.EXAM_CENTER -> SubjectAndExamHubScreen(
                                                    initialMode = "EXAM",
                                                    user = user,
                                                    vm = vm,
                                                    onStartActiveExam = { currentRoute = AppRoute.ACTIVE_EXAM },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.SUBJECT_HUB -> SubjectAndExamHubScreen(
                                                    initialMode = "PRACTICE",
                                                    user = user,
                                                    vm = vm,
                                                    onStartActiveExam = { currentRoute = AppRoute.ACTIVE_EXAM },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.ACTIVE_EXAM -> ActiveExamRoomScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onExamFinished = { currentRoute = AppRoute.EXAM_RESULT },
                                                    onExitToHome = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.EXAM_RESULT -> ExamResultScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onRetryAnotherSet = { currentRoute = AppRoute.SUBJECT_HUB },
                                                    onBackHome = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.PT_SHOP -> PtShopScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onNavigateBackgrounds = { currentRoute = AppRoute.BACKGROUND_COLLECTION },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.PET_ZONE -> PetZoneScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onOpenInventory = { currentRoute = AppRoute.PET_INVENTORY },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.PET_INVENTORY -> PetInventoryScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onGoHatch = { currentRoute = AppRoute.PET_ZONE },
                                                    onBack = { currentRoute = AppRoute.PET_ZONE }
                                                )
                                                AppRoute.BACKGROUND_COLLECTION -> BackgroundCollectionScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onGoShop = { currentRoute = AppRoute.PT_SHOP },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.QUESTS -> QuestsAndChallengesScreen(
                                                    vm = vm,
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.AI_TUTOR -> AiTutorScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.STUDY_SHEETS -> StudySheetLibraryScreen(
                                                    vm = vm,
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.SMART_STUDY_HUB -> SmartStudyHubScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onStartMistakeRetryExam = { currentRoute = AppRoute.ACTIVE_EXAM },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.DAILY_REWARDS_PLAZA -> DailyRewardsPlazaScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onOpenShop = { currentRoute = AppRoute.PT_SHOP },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.BOSS_RAID -> BossRaidScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onOpenLeaderboards = { currentRoute = AppRoute.LEADERBOARDS },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.MINI_GAMES -> MiniGamesHubScreen(
                                                    vm = vm,
                                                    onOpenLeaderboards = { currentRoute = AppRoute.LEADERBOARDS },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.LEADERBOARDS -> LeaderboardsScreen(
                                                    currentUsername = user.username,
                                                    vm = vm,
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.PROFILE_STATS -> ProfileAndStatsScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onOpenSettings = { currentRoute = AppRoute.SETTINGS },
                                                    onOpenBackgrounds = { currentRoute = AppRoute.BACKGROUND_COLLECTION },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.SETTINGS -> SettingsScreen(
                                                    user = user,
                                                    vm = vm,
                                                    onOpenBackgrounds = { currentRoute = AppRoute.BACKGROUND_COLLECTION },
                                                    onOpenProfile = { currentRoute = AppRoute.PROFILE_STATS },
                                                    onOpenAdminPanel = { currentRoute = AppRoute.ADMIN_PANEL },
                                                    onBack = { currentRoute = AppRoute.HOME }
                                                )
                                                AppRoute.ADMIN_PANEL -> AdminPanelScreen(
                                                    vm = vm,
                                                    onBack = { currentRoute = AppRoute.SETTINGS }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (welcomeCode != null) {
                            WelcomeCelebrationDialog(
                                recoveryCode = welcomeCode!!,
                                onDismiss = { vm.dismissWelcomeCelebration() }
                            )
                        }

                        if (levelUpLvl != null) {
                            LevelUpCelebrationDialog(
                                newLevel = levelUpLvl!!,
                                onDismiss = { vm.dismissLevelUpCelebration() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BannedAccountScreen(
    username: String,
    banUntilMillis: Long,
    reason: String,
    onLogout: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val untilLabel = if (banUntilMillis == Long.MAX_VALUE) {
        t("ถาวร (FOREVER)", "Permanent (FOREVER)", "永久封禁")
    } else {
        formatDateTime(lang, banUntilMillis)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        PtGlassCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(Icons.Default.Block, contentDescription = null, tint = NegativeRedAlert, modifier = Modifier.size(56.dp))
                Text(
                    text = t("บัญชี @$username ถูกระงับการใช้งาน", "Account @$username is Banned", "账号 @$username 已被限制访问"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = NegativeRedAlert,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${t("สิ้นสุดการระงับ", "Ban Expires", "解封时间")}: $untilLabel",
                    fontWeight = FontWeight.Bold
                )
                if (reason.isNotBlank()) {
                    Text("${t("เหตุผล", "Reason", "原因")}: $reason", style = MaterialTheme.typography.bodySmall)
                }
                OutlinedButton(
                    onClick = onLogout,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NegativeRedAlert)
                ) {
                    Text(t("ออกจากระบบ / สลับบัญชี", "Log Out / Switch Account", "退出登录 / 切换账号"))
                }
            }
        }
    }
}
