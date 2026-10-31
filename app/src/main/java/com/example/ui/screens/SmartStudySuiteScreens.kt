package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.t
import com.example.i18n.tr
import com.example.model.SubjectId
import com.example.ui.components.HttpsPortalBar
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
import com.example.util.SecurityUtils
import com.example.util.SfxType
import com.example.util.SoundEngine
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.MistakeNotebookItem
import kotlinx.coroutines.delay

@Composable
fun SmartStudyHubScreen(
    user: UserEntity,
    vm: GameViewModel,
    onStartMistakeRetryExam: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    val mistakes by vm.mistakeNotebookItems.collectAsState()
    val flashcards by vm.flashcards.collectAsState()
    val studyTasks by vm.studyPlanTasks.collectAsState()

    var activeTab by rememberSaveable { mutableIntStateOf(0) } // 0: Mistakes, 1: Flashcards, 2: Study Planner

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
                currentPath = "/study",
                shareMessageExtra = tr(
                    lang,
                    "มาทบทวนข้อสอบและท่องศัพท์เตรียมเข้า ม.1 ด้วยกันที่ PT's Smart Study Hub!",
                    "Reviewing M.1 Entrance Mistakes & 3D Flashcards on PT's Pre-M.1!",
                    "正在 PT's 智能复习中心攻克错题与3D闪卡！"
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
                            text = t("ศูนย์ทบทวนอัจฉริยะ ม.1", "Smart M.1 Study Suite", "初一智能温习中心"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = t(
                                "สมุดข้อที่เคยผิด • แฟลชการ์ด 3 มิติ • ตารางวางแผนสอบ",
                                "Mistake Notebook • 3D Flashcards • Exam Planner",
                                "错题本重练 • 3D词汇公式闪卡 • 备考倒计时计划"
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                PtPointsCoinBadge(ptPoints = user.ptPoints, reducedMotion = user.reducedMotion)
            }
        }

        // Colorful 3-Tab Switcher
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val tabs = listOf(
                    Triple(0, "📕", t("สมุดข้อผิด (${mistakes.size})", "Mistakes (${mistakes.size})", "错题本(${mistakes.size})")),
                    Triple(1, "🃏", t("แฟลชการ์ด (${flashcards.size})", "Flashcards (${flashcards.size})", "3D闪卡(${flashcards.size})")),
                    Triple(2, "📅", t("แผนอ่านสอบ", "Planner", "备考计划"))
                )
                tabs.forEach { (idx, emoji, label) ->
                    val selected = activeTab == idx
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (selected) Color.Transparent else Color.White.copy(alpha = 0.88f),
                        border = BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) PtRosePink else Color(0xFFCBD5E1)
                        ),
                        shadowElevation = if (selected) 5.dp else 1.dp,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                                activeTab = idx
                            }
                            .testTag("study_tab_$idx")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(
                                    if (selected) Brush.horizontalGradient(listOf(PtSkyBlue, PtRosePink))
                                    else Brush.horizontalGradient(listOf(Color.White, Color.White))
                                )
                                .padding(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Text(
                                text = "$emoji $label",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (selected) Color.White else Color(0xFF334155),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        when (activeTab) {
            0 -> {
                item {
                    MistakeNotebookTabContent(
                        user = user,
                        mistakes = mistakes,
                        onStartRetryExam = {
                            vm.startMistakeRetryExam(onReady = onStartMistakeRetryExam)
                        }
                    )
                }
            }
            1 -> {
                item {
                    Flashcards3DTabContent(
                        user = user,
                        vm = vm
                    )
                }
            }
            else -> {
                item {
                    StudyPlannerAndTimerTabContent(
                        user = user,
                        tasksCount = studyTasks.size,
                        vm = vm
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun MistakeNotebookTabContent(
    user: UserEntity,
    mistakes: List<MistakeNotebookItem>,
    onStartRetryExam: () -> Unit
) {
    val lang = LocalAppLanguage.current
    var subjectFilter by rememberSaveable { mutableStateOf("ALL") }
    var expandedQuestionId by remember { mutableStateOf<Long?>(null) }

    val filtered = remember(mistakes, subjectFilter) {
        if (subjectFilter == "ALL") mistakes
        else mistakes.filter { it.question.subjectId == subjectFilter }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PtSectionRibbonHeader(
            emoji = "📕",
            title = t("สมุดทบทวนข้อที่ตอบผิด (Mistake Review Notebook)", "Mistake Review Notebook", "错题智能复盘本"),
            subtitle = t(
                "ซ่อมผ่านแล้ว ${user.mistakesMasteredCount} ข้อ • ตอบถูกซ้ำรับโบนัส +15 XP ต่อข้อ!",
                "Mastered ${user.mistakesMasteredCount} mistakes • +15 Mastery XP per cleared item!",
                "已攻克 ${user.mistakesMasteredCount} 道错题 • 重练答对每题额外奖 +15 XP！"
            ),
            colors = listOf(RainbowCoral, PtRosePink)
        )

        PtGlassCard(
            borderColors = listOf(RainbowCoral, PtSkyBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (mistakes.isEmpty()) {
                        t(
                            "🎉 สุดยอดมาก! ตอนนี้ไม่มีข้อผิดค้างในสมุดทบทวนเลย!",
                            "🎉 Amazing! You have zero pending mistakes right now!",
                            "🎉 太棒了！当前错题本已全部清空！"
                        )
                    } else {
                        t(
                            "มีโจทย์ที่รอให้คุณกลับมาพิชิต ${mistakes.size} ข้อ กดปุ่มด้านล่างเพื่อจัดชุดสอบซ่อมทันที!",
                            "You have ${mistakes.size} question(s) waiting to be mastered. Launch a custom retry set now!",
                            "共有 ${mistakes.size} 道待攻克错题，点击下方按钮立即开始专属重练！"
                        )
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                PtGradientButton(
                    text = t(
                        "🚀 เริ่มสอบซ่อมข้อที่เคยผิดทันที (${mistakes.size.coerceAtMost(15)} ข้อ)",
                        "🚀 Start Mistake Retry Exam (${mistakes.size.coerceAtMost(15)} Qs)",
                        "🚀 立即开始错题重练（${mistakes.size.coerceAtMost(15)} 题）"
                    ),
                    onClick = onStartRetryExam,
                    enabled = mistakes.isNotEmpty(),
                    gradientColors = listOf(RainbowCoral, PtRosePink, PtSkyBlue),
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "start_mistake_retry_button"
                )
            }
        }

        // Subject filter row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                FilterChip(
                    selected = subjectFilter == "ALL",
                    onClick = { subjectFilter = "ALL" },
                    label = { Text(t("ทุกวิชา (${mistakes.size})", "All (${mistakes.size})", "全部(${mistakes.size})")) }
                )
            }
            items(SubjectId.entries) { sub ->
                val cnt = mistakes.count { it.question.subjectId == sub.id }
                if (cnt > 0) {
                    FilterChip(
                        selected = subjectFilter == sub.id,
                        onClick = { subjectFilter = sub.id },
                        label = { Text("${sub.title(lang)} ($cnt)") }
                    )
                }
            }
        }

        filtered.forEach { item ->
            val q = item.question
            val sub = SubjectId.fromId(q.subjectId)
            val isExpanded = expandedQuestionId == q.id
            val qText = when (lang) {
                AppLanguage.TH -> q.questionTh
                AppLanguage.EN -> q.questionEn
                AppLanguage.ZH -> q.questionZh
            }
            val expText = when (lang) {
                AppLanguage.TH -> q.explanationTh
                AppLanguage.EN -> q.explanationEn
                AppLanguage.ZH -> q.explanationZh
            }

            PtGlassCard(
                borderColors = listOf(sub.color, RainbowCoral),
                onClick = { expandedQuestionId = if (isExpanded) null else q.id },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = sub.color.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${sub.title(lang)} • ${q.topic}",
                                color = sub.color,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFFFE4E6)
                        ) {
                            Text(
                                text = t("ผิด ${item.wrongTimes} ครั้ง", "Missed ${item.wrongTimes}x", "错 ${item.wrongTimes} 次"),
                                color = NegativeRedAlert,
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = qText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${t("คำตอบที่เคยเลือก", "Last Pick", "上次选择")}: ${item.lastWrongAnswer.ifBlank { "-" }}",
                            style = MaterialTheme.typography.labelMedium,
                            color = NegativeRedAlert,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isExpanded) t("ซ่อนเฉลย", "Hide Solution", "收起解析") else t("ดูเฉลยละเอียด", "View Solution", "查看解析"),
                                style = MaterialTheme.typography.labelMedium,
                                color = PtSkyBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = PtSkyBlue
                            )
                        }
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFECFDF5),
                                border = BorderStroke(1.dp, PtEmerald),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "✅ ${t("เฉลยที่ถูกต้อง", "Correct Answer", "正确答案")}: ${q.correctAnswer}",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PtEmerald
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = expText,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF065F46)
                                    )
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
private fun Flashcards3DTabContent(
    user: UserEntity,
    vm: GameViewModel
) {
    val lang = LocalAppLanguage.current
    val allCards by vm.flashcards.collectAsState()

    var selectedSubject by rememberSaveable { mutableStateOf("ALL") }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var isFlipped by rememberSaveable { mutableStateOf(false) }
    var shuffleSalt by rememberSaveable { mutableIntStateOf(0) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filteredCards = remember(allCards, selectedSubject, shuffleSalt) {
        val base = if (selectedSubject == "ALL") allCards else allCards.filter { it.subjectId == selectedSubject }
        if (shuffleSalt == 0) base else base.shuffled()
    }

    val safeIndex = if (filteredCards.isEmpty()) 0 else currentIndex.coerceIn(0, filteredCards.lastIndex)
    val currentCard = filteredCards.getOrNull(safeIndex)

    val flipRotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = if (user.reducedMotion) 80 else 420, easing = FastOutSlowInEasing),
        label = "flashcard_3d_flip"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PtSectionRibbonHeader(
            emoji = "🃏",
            title = t("แฟลชการ์ดจำศัพท์ & สูตร 3 มิติ", "3D Vocabulary & Formula Flashcards", "3D 核心词汇与公式闪卡"),
            subtitle = t(
                "แตะที่การ์ดเพื่อพลิกดูคำแปล/สูตร • ท่องจำแล้ว ${user.flashcardsReviewedCount} ครั้ง",
                "Tap card to flip • Reviewed ${user.flashcardsReviewedCount} times",
                "点击卡片翻转查看释义 • 已累计复习 ${user.flashcardsReviewedCount} 次"
            ),
            colors = listOf(RainbowIris, RainbowAqua),
            trailingContent = {
                OutlinedButton(
                    onClick = { showCreateDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_custom_flashcard_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(t("สร้างเอง", "New", "新建"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                FilterChip(
                    selected = selectedSubject == "ALL",
                    onClick = {
                        selectedSubject = "ALL"
                        currentIndex = 0
                        isFlipped = false
                    },
                    label = { Text(t("ทุกหมวด (${allCards.size})", "All (${allCards.size})", "全部(${allCards.size})")) }
                )
            }
            items(SubjectId.entries) { sub ->
                val cnt = allCards.count { it.subjectId == sub.id }
                if (cnt > 0) {
                    FilterChip(
                        selected = selectedSubject == sub.id,
                        onClick = {
                            selectedSubject = sub.id
                            currentIndex = 0
                            isFlipped = false
                        },
                        label = { Text("${sub.title(lang)} ($cnt)") }
                    )
                }
            }
        }

        if (currentCard != null) {
            val subSpec = SubjectId.fromId(currentCard.subjectId)
            val isMastered = remember(currentCard.masteredByUserIdsCsv, user.id) {
                user.id.toString() in currentCard.masteredByUserIdsCsv.split(",").map { it.trim() }
            }

            // 3D Flip Card Box
            Surface(
                shape = RoundedCornerShape(26.dp),
                shadowElevation = 10.dp,
                border = BorderStroke(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(listOf(subSpec.color, PtBubblegumPink, PtSkyBlue))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .graphicsLayer {
                        rotationY = flipRotation
                        cameraDistance = 14f * density
                    }
                    .clickable {
                        SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                        isFlipped = !isFlipped
                    }
                    .testTag("flashcard_3d_surface")
            ) {
                val showingBack = flipRotation > 90f
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            if (showingBack) rotationY = 180f
                        }
                        .background(
                            if (!showingBack) {
                                Brush.verticalGradient(listOf(Color.White, subSpec.color.copy(alpha = 0.12f)))
                            } else {
                                Brush.verticalGradient(listOf(Color(0xFFF0FDF4), Color(0xFFE0F2FE)))
                            }
                        )
                        .padding(22.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = subSpec.color.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = "${subSpec.title(lang)} • ${safeIndex + 1}/${filteredCards.size}",
                                    color = subSpec.color,
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            if (isMastered) {
                                Text(
                                    text = "✅ ${t("จำแม่นแล้ว", "Mastered", "已掌握")}",
                                    color = PtEmerald,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            } else {
                                Icon(Icons.Default.Flip, contentDescription = null, tint = PtSkyBlue)
                            }
                        }

                        if (!showingBack) {
                            Text(
                                text = tr(lang, currentCard.frontTh, currentCard.frontEn, currentCard.frontZh),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = t("👆 แตะเพื่อพลิกดูคำอธิบายและสูตร", "👆 Tap card to flip & reveal answer", "👆 点击卡片翻转查看背面解析"),
                                style = MaterialTheme.typography.labelMedium,
                                color = PtSkyBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = tr(lang, currentCard.backTh, currentCard.backEn, currentCard.backZh),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF065F46)
                                )
                                if (currentCard.exampleText.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White.copy(alpha = 0.85f)
                                    ) {
                                        Text(
                                            text = "💡 ${currentCard.exampleText}",
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            color = Color(0xFF334155),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = t("👆 แตะเพื่อพลิกกลับด้านหน้า", "👆 Tap to flip back", "👆 点击翻回正面"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Flashcard Controls Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        isFlipped = false
                        currentIndex = if (safeIndex > 0) safeIndex - 1 else filteredCards.lastIndex
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ใบก่อนหน้า", "Previous", "上一张"))
                }

                PtGradientButton(
                    text = t("✅ จำแม่นแล้ว! (+XP & PT)", "✅ Got It! (+XP & PT)", "✅ 已记住！(+XP & PT)"),
                    onClick = {
                        vm.markFlashcardReviewed(currentCard)
                        isFlipped = false
                        currentIndex = (safeIndex + 1) % filteredCards.size
                    },
                    gradientColors = listOf(PtEmerald, PtSkyBlue),
                    modifier = Modifier.weight(1f),
                    testTag = "flashcard_master_btn"
                )

                IconButton(
                    onClick = {
                        isFlipped = false
                        shuffleSalt++
                        currentIndex = 0
                    }
                ) {
                    Icon(Icons.Default.Shuffle, contentDescription = t("สลับการ์ด", "Shuffle", "随机洗牌"), tint = PtLavender)
                }

                OutlinedButton(
                    onClick = {
                        isFlipped = false
                        currentIndex = (safeIndex + 1) % filteredCards.size
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = t("ใบถัดไป", "Next", "下一张"))
                }
            }
        }
    }

    if (showCreateDialog) {
        var newSub by remember { mutableStateOf(SubjectId.ENGLISH) }
        var frontInput by remember { mutableStateOf("") }
        var backInput by remember { mutableStateOf("") }
        var exampleInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text(t("สร้างแฟลชการ์ดของฉัน", "Create Custom Flashcard", "新建自定义闪卡"), fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(SubjectId.ENGLISH, SubjectId.MATH, SubjectId.SCIENCE, SubjectId.THAI, SubjectId.SOCIAL).forEach { s ->
                            FilterChip(
                                selected = newSub == s,
                                onClick = { newSub = s },
                                label = { Text(s.title(lang), fontSize = 11.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = frontInput,
                        onValueChange = { frontInput = it },
                        label = { Text(t("ด้านหน้า (คำศัพท์ / หัวข้อสูตร)", "Front (Term / Formula Name)", "正面（词汇/公式名称）")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = backInput,
                        onValueChange = { backInput = it },
                        label = { Text(t("ด้านหลัง (ความหมาย / สูตรคำนวณ)", "Back (Meaning / Formula)", "背面（释义/公式内容）")) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = exampleInput,
                        onValueChange = { exampleInput = it },
                        label = { Text(t("ตัวอย่างประโยคหรือวิธีใช้ (ไม่บังคับ)", "Example (Optional)", "例句或用法（选填）")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                PtGradientButton(
                    text = t("บันทึกการ์ด", "Save Card", "保存闪卡"),
                    enabled = frontInput.isNotBlank() && backInput.isNotBlank(),
                    onClick = {
                        vm.addCustomFlashcard(newSub, frontInput, backInput, exampleInput)
                        showCreateDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text(t("ยกเลิก", "Cancel", "取消")) }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StudyPlannerAndTimerTabContent(
    user: UserEntity,
    tasksCount: Int,
    vm: GameViewModel
) {
    val lang = LocalAppLanguage.current
    val tasks by vm.studyPlanTasks.collectAsState()
    val todayBangkok = remember { SecurityUtils.currentBangkokDateString() }
    val daysRemaining = remember(user.targetExamDateBangkok, todayBangkok) {
        SecurityUtils.daysBetweenBangkokDates(todayBangkok, user.targetExamDateBangkok).coerceAtLeast(0)
    }

    var showCountdownDialog by remember { mutableStateOf(false) }

    // Built-in Focus Pomodoro Timer
    var selectedPresetMin by rememberSaveable { mutableIntStateOf(25) }
    var remainingTimerSec by rememberSaveable { mutableIntStateOf(25 * 60) }
    var timerRunning by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(timerRunning) {
        while (timerRunning && remainingTimerSec > 0) {
            delay(1000L)
            remainingTimerSec--
            if (remainingTimerSec == 0) {
                timerRunning = false
                SoundEngine.playSfx(SfxType.LEVEL_UP)
                vm.showToast(
                    tr(
                        lang,
                        "⏰ ครบเวลาโฟกัสอ่านหนังสือ $selectedPresetMin นาทีแล้ว! เก่งมาก!",
                        "⏰ Completed $selectedPresetMin-minute focus session! Great job!",
                        "⏰ 已完成 $selectedPresetMin 分钟专注学习！太棒了！"
                    )
                )
            }
        }
    }

    // Add Goal State
    var goalSubject by rememberSaveable { mutableStateOf(SubjectId.MATH) }
    var goalTitle by rememberSaveable { mutableStateOf("") }
    var goalMinutes by rememberSaveable { mutableIntStateOf(25) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 1. Dream School Exam Countdown Banner
        PtGlassCard(
            borderColors = listOf(PtSkyBlue, PtRosePink, PtSoftGold),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            listOf(PtSkyBlue.copy(alpha = 0.14f), PtBubblegumPink.copy(alpha = 0.14f))
                        )
                    )
                    .padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.linearGradient(listOf(PtSkyBlue, PtRosePink)))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$daysRemaining",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        )
                        Text(
                            text = t("วัน", "Days", "天"),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🎯 ${user.targetDreamSchool}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${t("นับถอยหลังวันสอบ", "Exam Date", "目标考试日期")}: ${user.targetExamDateBangkok}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { showCountdownDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = t("แก้ไขวันสอบ", "Edit Exam Date", "编辑目标"), tint = PtSkyBlue)
                }
            }
        }

        // 2. Focus Study Pomodoro Timer Card
        PtGlassCard(
            borderColors = listOf(RainbowMint, RainbowAqua),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = PtEmerald)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = t("นาฬิกาจับเวลาโฟกัสอ่านสอบ", "Focus Pomodoro Timer", "专注复习番茄钟"),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(15, 25, 45).forEach { minOpt ->
                            FilterChip(
                                selected = selectedPresetMin == minOpt,
                                onClick = {
                                    timerRunning = false
                                    selectedPresetMin = minOpt
                                    remainingTimerSec = minOpt * 60
                                },
                                label = { Text("${minOpt}m", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "%02d:%02d".format(remainingTimerSec / 60, remainingTimerSec % 60),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = PtSkyBlue
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = {
                        (remainingTimerSec.toFloat() / (selectedPresetMin * 60).coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                    },
                    color = PtEmerald,
                    trackColor = Color(0xFFE2E8F0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PtGradientButton(
                        text = if (timerRunning) t("⏸️ พักเวลา", "⏸️ Pause", "⏸️ 暂停") else t("▶️ เริ่มจับเวลาโฟกัส", "▶️ Start Focus", "▶️ 开始专注"),
                        onClick = { timerRunning = !timerRunning },
                        gradientColors = if (timerRunning) listOf(PtSoftGold, RainbowCoral) else listOf(PtEmerald, PtSkyBlue)
                    )
                    OutlinedButton(
                        onClick = {
                            timerRunning = false
                            remainingTimerSec = selectedPresetMin * 60
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = t("รีเซ็ต", "Reset", "重置"))
                    }
                }
            }
        }

        // 3. Add Study Plan Goal Card
        PtGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = t("📝 เพิ่มเป้าหมายอ่านหนังสือวันนี้ (+35 XP & +10 PT เมื่อทำสำเร็จ)", "📝 Add Study Goal (+35 XP & +10 PT on completion)", "📝 添加复习目标（完成后奖 +35 XP 与 +10 PT）"),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(SubjectId.entries) { sub ->
                        FilterChip(
                            selected = goalSubject == sub,
                            onClick = { goalSubject = sub },
                            label = { Text(sub.title(lang), fontSize = 11.sp) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text(t("เช่น สรุปสูตรร้อยละ 20 ข้อ", "e.g., Practice 20 Fraction Qs", "例如：完成20道分数应用题")) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("study_goal_title_input")
                    )
                    PtGradientButton(
                        text = "+ ${t("เพิ่ม", "Add", "添加")}",
                        enabled = goalTitle.isNotBlank(),
                        onClick = {
                            vm.addStudyPlanGoal(goalSubject, goalTitle, goalMinutes, todayBangkok)
                            goalTitle = ""
                        },
                        testTag = "study_goal_add_btn"
                    )
                }
            }
        }

        // 4. Checklist of Study Goals
        if (tasks.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = t("ยังไม่มีรายการวางแผน ลองตั้งเป้าหมายอ่านสอบเข้า ม.1 วันละ 1–2 หัวข้อดูนะ! ✨", "No study goals yet — add 1–2 daily topics above to stay on track! ✨", "暂无计划，在上方添加1–2个每日冲刺小目标吧！✨"),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(18.dp)
                )
            }
        } else {
            tasks.forEach { task ->
                val sub = SubjectId.fromId(task.subjectId)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (task.isCompleted) Color(0xFFECFDF5) else Color.White.copy(alpha = 0.94f),
                    border = BorderStroke(1.2.dp, if (task.isCompleted) PtEmerald else sub.color.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { vm.toggleStudyPlanGoal(task.id) }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                            Text(
                                text = "${sub.title(lang)} • ${task.targetMinutes} ${t("นาที", "min", "分钟")} • ${task.targetDateBangkok}",
                                style = MaterialTheme.typography.labelSmall,
                                color = sub.color
                            )
                        }
                        if (task.isCompleted) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PtEmerald, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = { vm.deleteStudyPlanGoal(task.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = t("ลบ", "Delete", "删除"), tint = NegativeRedAlert)
                        }
                    }
                }
            }
        }
    }

    if (showCountdownDialog) {
        var schoolText by remember { mutableStateOf(user.targetDreamSchool) }
        var dateText by remember { mutableStateOf(user.targetExamDateBangkok) }

        AlertDialog(
            onDismissRequest = { showCountdownDialog = false },
            shape = RoundedCornerShape(24.dp),
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = PtSkyBlue) },
            title = { Text(t("ตั้งค่าโรงเรียนในฝัน & วันสอบ ม.1", "Set Dream School & Exam Date", "设置目标名校与考试日期"), fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = schoolText,
                        onValueChange = { schoolText = it },
                        label = { Text(t("ชื่อโรงเรียนในฝัน", "Dream M.1 School", "目标初一学校")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text(t("วันสอบ (YYYY-MM-DD)", "Exam Date (YYYY-MM-DD)", "考试日期 (YYYY-MM-DD)")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                PtGradientButton(
                    text = t("บันทึกเป้าหมาย", "Save Target", "保存目标"),
                    onClick = {
                        vm.updateDreamSchoolCountdown(dateText, schoolText)
                        showCountdownDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showCountdownDialog = false }) { Text(t("ยกเลิก", "Cancel", "取消")) }
            }
        )
    }
}
