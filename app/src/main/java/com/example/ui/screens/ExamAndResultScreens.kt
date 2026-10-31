package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.local.QuestionEntity
import com.example.data.local.UserEntity
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.formatNumber
import com.example.i18n.t
import com.example.i18n.tr
import com.example.model.DifficultyLevel
import com.example.model.QuestionFormat
import com.example.model.SubjectId
import com.example.ui.components.AnimatedStreakFlameBadge
import com.example.ui.components.HttpsPortalBar
import com.example.ui.components.MascotMood
import com.example.ui.components.NegativePtWarningBanner
import com.example.ui.components.PetCharacterCanvas
import com.example.ui.components.PtGlassCard
import com.example.ui.components.PtGradientButton
import com.example.ui.components.PtOwlMascot
import com.example.ui.components.PtPointsCoinBadge
import com.example.ui.theme.NegativeRedAlert
import com.example.ui.theme.PtBubblegumPink
import com.example.ui.theme.PtElectricCyan
import com.example.ui.theme.PtEmerald
import com.example.ui.theme.PtLavender
import com.example.ui.theme.PtRosePink
import com.example.ui.theme.PtSkyBlue
import com.example.ui.theme.PtSoftGold
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubjectAndExamHubScreen(
    initialMode: String,
    user: UserEntity,
    vm: GameViewModel,
    onStartActiveExam: () -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    val allQuestions by vm.allQuestions.collectAsState()
    val completedExams by vm.completedExams.collectAsState()
    val isAiGenerating by vm.isAiGeneratingExam.collectAsState()
    val mistakeItems by vm.mistakeNotebookItems.collectAsState()

    var selectedMode by rememberSaveable(initialMode) { mutableStateOf(initialMode) }
    var selectedSubject by rememberSaveable { mutableStateOf(SubjectId.MATH) }
    var selectedDifficulty by rememberSaveable {
        mutableStateOf(if (initialMode == "ENTRANCE") DifficultyLevel.ENTRANCE_EXAM else DifficultyLevel.NORMAL)
    }
    var selectedCount by rememberSaveable { mutableIntStateOf(5) }

    val availableCounts = remember(user.easyBonusUnlocked, selectedDifficulty, selectedMode) {
        buildList {
            add(5)
            add(10)
            add(15)
            if (user.easyBonusUnlocked && selectedDifficulty == DifficultyLevel.EASY && selectedMode == "PRACTICE") {
                add(25) // +20 Easy Bonus Pack unlocked from PT Shop!
            }
        }
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
                currentPath = if (selectedMode == "EXAM") "/exam" else "/practice",
                onCopiedToast = { vm.showToast(it) }
            )
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                }
                Text(
                    text = t("เลือกวิชา & โหมดสอบเข้า ม.1", "Subjects & Exam Modes", "选择科目与考试模式"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        if (mistakeItems.isNotEmpty()) {
            item {
                PtGlassCard(
                    borderColors = listOf(PtRosePink, PtSoftGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = t("📓 มีโจทย์รอสอบซ่อม ${mistakeItems.size} ข้อ!", "📓 ${mistakeItems.size} Mistake(s) Ready to Retry!", "📓 错题本有 ${mistakeItems.size} 题待攻克！"),
                                fontWeight = FontWeight.ExtraBold,
                                color = PtRosePink
                            )
                            Text(
                                text = t("ตอบถูกในโหมดสอบซ่อมจะเคลียร์ออกจากสมุดข้อผิดและรับโบนัส Mastery!", "Answer correctly to master them and earn bonus XP!", "重练答对即可移出错题本并赢取专精奖励！"),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        PtGradientButton(
                            text = t("สอบซ่อมเลย!", "Retry!", "立即重练"),
                            onClick = { vm.startMistakeRetryExam(onReady = onStartActiveExam) },
                            gradientColors = listOf(PtRosePink, PtSoftGold),
                            testTag = "hub_mistake_retry_button"
                        )
                    }
                }
            }
        }

        // 1. Mode Switcher (Practice / Exam / Entrance Intensive)
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = t("1. เลือกโหมดการทำข้อสอบ", "1. Choose Learning Mode", "1. 选择答题模式"),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf(
                            "PRACTICE" to t("ฝึกฝน (เฉลยทันที)", "Practice (Instant)", "日常练习"),
                            "EXAM" to t("สอบจริง (จับเวลา)", "Timed Exam", "正式模拟"),
                            "ENTRANCE" to t("สอบเข้า ม.1 เข้มข้น", "M.1 Intensive", "初一冲刺")
                        ).forEach { (mCode, label) ->
                            val selected = selectedMode == mCode
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) PtSkyBlue else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedMode = mCode
                                        if (mCode == "ENTRANCE") selectedDifficulty = DifficultyLevel.ENTRANCE_EXAM
                                    }
                                    .testTag("mode_chip_$mCode")
                            ) {
                                Text(
                                    text = label,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. All 10 Core Subjects Grid
        item {
            Text(
                text = t("2. เลือกวิชาหลัก (ครบทั้ง 10 วิชา)", "2. Select Core Subject (All 10 Subjects)", "2. 选择核心科目（全部10科）"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SubjectId.entries.forEach { sub ->
                    val isSel = selectedSubject == sub
                    val qCountInBank = allQuestions.count { it.subjectId == sub.id && it.reviewStatus == "APPROVED" }
                    val doneCount = completedExams.count { it.subjectId == sub.id }
                    PtGlassCard(
                        borderColors = if (isSel) listOf(sub.color, PtRosePink) else listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0)),
                        containerColor = if (isSel) sub.color.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.92f),
                        onClick = { selectedSubject = sub },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("subject_card_${sub.id}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(sub.color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = sub.title(lang),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sub.curriculumCode,
                                style = MaterialTheme.typography.labelSmall,
                                color = sub.color
                            )
                            Text(
                                text = "${t("คลัง", "Bank", "题库")}: $qCountInBank • ${t("ผ่านแล้ว", "Done", "已完成")}: $doneCount",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Subtopics & Supported Formats for Selected Subject
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "${selectedSubject.title(lang)} — ${t("บทเรียนย่อย & รูปแบบโจทย์", "Subtopics & Formats", "核心单元与题型")}",
                        fontWeight = FontWeight.Bold,
                        color = selectedSubject.color
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    selectedSubject.subtopics(lang).forEach { topic ->
                        Text("• $topic", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(QuestionFormat.entries) { fmt ->
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = fmt.label(lang),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Difficulty & Question Count Configuration
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = t("3. ระดับความยาก (5 ระดับ)", "3. Difficulty Level (5 Tiers)", "3. 选择难度等级（5级）"),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyLevel.entries.forEach { diff ->
                            FilterChip(
                                selected = selectedDifficulty == diff,
                                onClick = { selectedDifficulty = diff },
                                label = { Text("${diff.title(lang)} (+${diff.xpPerCorrect} XP)") },
                                modifier = Modifier.testTag("diff_chip_${diff.id}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = t("จำนวนข้อสอบต่อชุด", "Questions per Set", "每套题量"),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        availableCounts.forEach { cnt ->
                            FilterChip(
                                selected = selectedCount == cnt,
                                onClick = { selectedCount = cnt },
                                label = {
                                    Text(
                                        if (cnt == 25) "25 (${t("โบนัสร้านค้า +20!", "+20 Shop Pack!", "+20商店扩展包")})"
                                        else "$cnt ${t("ข้อ", "Qs", "题")}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // 4. Launch Buttons: Instant Verified Bank OR Gemini AI High-Thinking New Exam Generation!
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PtGradientButton(
                    text = t(
                        "▶️ เริ่มทำข้อสอบทันที (${selectedSubject.title(lang)})",
                        "▶️ Start Exam Now (${selectedSubject.title(lang)})",
                        "▶️ 立即开始答题（${selectedSubject.title(lang)}）"
                    ),
                    onClick = {
                        vm.startExamSession(
                            mode = selectedMode,
                            subject = selectedSubject,
                            difficulty = selectedDifficulty,
                            questionCount = selectedCount,
                            onReady = onStartActiveExam
                        )
                    },
                    enabled = !isAiGenerating,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "launch_exam_button"
                )

                OutlinedButton(
                    onClick = {
                        vm.generateNewAiExamAndStart(
                            subject = selectedSubject,
                            difficulty = selectedDifficulty,
                            count = selectedCount.coerceAtMost(8),
                            mode = selectedMode,
                            onReady = onStartActiveExam
                        )
                    },
                    enabled = !isAiGenerating,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.5.dp, PtLavender),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("ai_generate_exam_button")
                ) {
                    if (isAiGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = PtLavender)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = t(
                                "AI High-Thinking กำลังออกข้อสอบใหม่และตรวจทานความถูกต้อง...",
                                "Gemini High-Thinking is crafting & verifying unique questions...",
                                "Gemini 深度思考正在生成并校验全新试题..."
                            ),
                            color = PtLavender,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PtLavender)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = t(
                                "✨ ให้ AI ออกข้อสอบชุดใหม่ไม่ซ้ำเดิม (High Thinking)",
                                "✨ Generate Brand-New Verified Exam with AI",
                                "✨ 使用 AI 深度思考生成全新不重复试卷"
                            ),
                            color = PtLavender,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ActiveExamRoomScreen(
    user: UserEntity,
    vm: GameViewModel,
    onExamFinished: () -> Unit,
    onExitToHome: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val examState by vm.activeExam.collectAsState()
    var showSubmitConfirm by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }

    BackHandler { showExitConfirm = true }

    val state = examState
    if (state == null || state.questions.isEmpty()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(color = PtSkyBlue)
        }
        return
    }

    val currentQ = state.questions[state.currentIndex]
    val selectedAns = state.answers[currentQ.id].orEmpty()
    val isLocked = currentQ.id in state.lockedQuestionIds
    val isMarked = currentQ.id in state.markedForReview
    val hiddenSet = state.hiddenOptionsByQuestion[currentQ.id].orEmpty()
    val unlockedHint = state.unlockedHintsByQuestion[currentQ.id]
    val equippedPet = vm.getEquippedPetSpecies()
    val isMcQuestion = currentQ.correctAnswer.trim().uppercase() in setOf("A", "B", "C", "D")

    val mins = state.remainingSeconds / 60
    val secs = state.remainingSeconds % 60

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
    ) {
        // Top Exam Bar: Exit, Progress Counter, Timer (with Freeze Visual), Mark for Review
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { showExitConfirm = true }) {
                    Icon(Icons.Default.Close, contentDescription = t("ออกจากการสอบ", "Exit Exam", "退出考试"))
                }

                Text(
                    text = "${t("ข้อที่", "Q", "第")} ${state.currentIndex + 1} / ${state.questions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = when {
                        state.freezeRemainingSeconds > 0 -> Color(0xFFCCFBF1)
                        state.remainingSeconds <= 30 -> Color(0xFFFFE4E6)
                        else -> Color.White
                    },
                    border = BorderStroke(
                        1.5.dp,
                        when {
                            state.freezeRemainingSeconds > 0 -> PtEmerald
                            state.remainingSeconds <= 30 -> NegativeRedAlert
                            else -> PtSkyBlue
                        }
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (state.freezeRemainingSeconds > 0) Icons.Default.AcUnit else Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (state.freezeRemainingSeconds > 0) PtEmerald else PtSkyBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (state.freezeRemainingSeconds > 0) {
                                "❄️ %02d:%02d (${state.freezeRemainingSeconds}s)".format(mins, secs)
                            } else {
                                "%02d:%02d".format(mins, secs)
                            },
                            fontWeight = FontWeight.Bold,
                            color = if (state.remainingSeconds <= 30) NegativeRedAlert else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                IconButton(onClick = { vm.toggleMarkQuestionForReview(currentQ.id) }) {
                    Icon(
                        imageVector = if (isMarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = t("ปักหมุดทบทวน", "Mark for Review", "标记复盘"),
                        tint = if (isMarked) PtSoftGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (state.currentIndex + 1).toFloat() / state.questions.size.toFloat() },
                color = PtSkyBlue,
                trackColor = Color(0xFFE0F2FE),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
            )
        }

        // Question Navigator Pill Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(state.questions) { idx, q ->
                    val answered = !state.answers[q.id].isNullOrBlank()
                    val marked = q.id in state.markedForReview
                    val active = idx == state.currentIndex
                    Surface(
                        shape = CircleShape,
                        color = when {
                            active -> PtSkyBlue
                            marked -> PtSoftGold
                            answered -> PtEmerald.copy(alpha = 0.22f)
                            else -> Color.White.copy(alpha = 0.85f)
                        },
                        border = BorderStroke(1.dp, if (active) PtSkyBlue else Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { vm.navigateExamQuestion(idx) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${idx + 1}",
                                fontWeight = FontWeight.Bold,
                                color = if (active) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Companion & Power-up Assist Bar (Pet Skill + Shop Remove-2-Wrong + AI Smart Hint)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (equippedPet != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, equippedPet.rarity.primaryColor),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { vm.useEquippedPetSkillInExam(lang) }
                            .testTag("exam_use_pet_skill_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            PetCharacterCanvas(pet = equippedPet, reducedMotion = user.reducedMotion, size = 34.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = equippedPet.name(lang),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (state.petSkillUsesLeft > 0) {
                                        t("กดใช้สกิล (${state.petSkillUsesLeft})", "Use Skill (${state.petSkillUsesLeft})", "使用技能(${state.petSkillUsesLeft})")
                                    } else {
                                        t("สกิลติดตัว/ครบแล้ว", "Passive/Used", "被动生效中")
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PtRosePink
                                )
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = { vm.useShopRemoveTwoWrongOnCurrentQuestion() },
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("exam_remove_2_wrong_button")
                ) {
                    Icon(Icons.Default.ContentCut, contentDescription = null, modifier = Modifier.size(15.dp), tint = PtRosePink)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("-2 (${user.removeWrongUses})", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = { vm.useShopTimePotionInExam() },
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("exam_time_potion_button")
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(15.dp), tint = PtEmerald)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("+30s (${user.timePotionUses})", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = { vm.requestAiSmartHintForCurrentQuestion(lang) },
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("exam_ai_hint_button")
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(15.dp), tint = PtSoftGold)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(t("ใบ้ AI", "Hint", "提示"), style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Main Question Card
        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${currentQ.topic} • ${currentQ.curriculumStandard}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PtSkyBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "#${currentQ.questionNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val qBody = when (lang) {
                        AppLanguage.TH -> currentQ.questionTh
                        AppLanguage.EN -> currentQ.questionEn
                        AppLanguage.ZH -> currentQ.questionZh
                    }
                    Text(
                        text = qBody,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (!unlockedHint.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFFBEB),
                            border = BorderStroke(1.dp, PtSoftGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = unlockedHint,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isMcQuestion) {
                        val choices = listOf(
                            "A" to when (lang) { AppLanguage.TH -> currentQ.choiceATh; AppLanguage.EN -> currentQ.choiceAEn; AppLanguage.ZH -> currentQ.choiceAZh },
                            "B" to when (lang) { AppLanguage.TH -> currentQ.choiceBTh; AppLanguage.EN -> currentQ.choiceBEn; AppLanguage.ZH -> currentQ.choiceBZh },
                            "C" to when (lang) { AppLanguage.TH -> currentQ.choiceCTh; AppLanguage.EN -> currentQ.choiceCEn; AppLanguage.ZH -> currentQ.choiceCZh },
                            "D" to when (lang) { AppLanguage.TH -> currentQ.choiceDTh; AppLanguage.EN -> currentQ.choiceDEn; AppLanguage.ZH -> currentQ.choiceDZh }
                        )

                        val isInstantFeedbackMode = state.session.mode == "PRACTICE" || state.session.mode == "MISTAKE_RETRY"
                        choices.forEach { (letter, text) ->
                            val eliminated = letter in hiddenSet
                            val chosen = selectedAns.equals(letter, ignoreCase = true)
                            val showPracticeCorrect = isLocked && isInstantFeedbackMode && letter.equals(currentQ.correctAnswer, ignoreCase = true)
                            val showPracticeWrong = isLocked && isInstantFeedbackMode && chosen && !letter.equals(currentQ.correctAnswer, ignoreCase = true)

                            if (!eliminated) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = when {
                                        showPracticeCorrect -> Color(0xFFDCFCE7)
                                        showPracticeWrong -> Color(0xFFFFE4E6)
                                        chosen -> Color(0xFFE0F2FE)
                                        else -> Color(0xFFF8FAFC)
                                    },
                                    border = BorderStroke(
                                        width = if (chosen || showPracticeCorrect) 2.dp else 1.dp,
                                        color = when {
                                            showPracticeCorrect -> PtEmerald
                                            showPracticeWrong -> NegativeRedAlert
                                            chosen -> PtSkyBlue
                                            else -> Color(0xFFCBD5E1)
                                        }
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clickable(enabled = !isLocked) {
                                            vm.selectAnswerInActiveExam(
                                                questionId = currentQ.id,
                                                answer = letter,
                                                lockImmediatelyInPractice = isInstantFeedbackMode
                                            )
                                        }
                                        .testTag("choice_option_$letter")
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
                                                .background(if (chosen) PtSkyBlue else Color(0xFFE2E8F0))
                                        ) {
                                            Text(
                                                text = letter,
                                                fontWeight = FontWeight.Bold,
                                                color = if (chosen) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = text,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        val isInstantFeedbackMode = state.session.mode == "PRACTICE" || state.session.mode == "MISTAKE_RETRY"
                        // Fill-in / Calculation input
                        OutlinedTextField(
                            value = selectedAns,
                            onValueChange = { typed ->
                                vm.selectAnswerInActiveExam(currentQ.id, typed, lockImmediatelyInPractice = false)
                            },
                            enabled = !isLocked,
                            label = { Text(t("พิมพ์คำตอบหรือผลลัพธ์การคำนวณ", "Type your answer", "输入答案或计算结果")) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (isInstantFeedbackMode && !isLocked && selectedAns.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    vm.selectAnswerInActiveExam(currentQ.id, selectedAns, lockImmediatelyInPractice = true)
                                }
                            ) {
                                Text(t("ตรวจคำตอบข้อนี้", "Check Answer", "检查本题答案"))
                            }
                        }
                    }

                    // Instant Feedback Card in Practice / Mistake Retry Mode
                    if ((state.session.mode == "PRACTICE" || state.session.mode == "MISTAKE_RETRY") && state.lastInstantFeedback != null && isLocked) {
                        val (ok, _) = state.lastInstantFeedback
                        val exp = when (lang) {
                            AppLanguage.TH -> currentQ.explanationTh
                            AppLanguage.EN -> currentQ.explanationEn
                            AppLanguage.ZH -> currentQ.explanationZh
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (ok) Color(0xFFECFDF5) else Color(0xFFFFF1F2),
                            border = BorderStroke(1.5.dp, if (ok) PtEmerald else NegativeRedAlert),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                PtOwlMascot(
                                    mood = if (ok) MascotMood.JUMP_CORRECT else MascotMood.ENCOURAGE_WRONG,
                                    reducedMotion = user.reducedMotion,
                                    size = 54.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (ok) {
                                            t("🎉 ถูกต้องยอดเยี่ยม!", "🎉 Correct!", "🎉 回答正确！")
                                        } else {
                                            t("💪 ไม่เป็นไรนะ มาดูวิธีคิดกัน! (เฉลย: ${currentQ.correctAnswer})", "💪 Let's see how it works! (Ans: ${currentQ.correctAnswer})", "💪 没关系，来看看解析！（答案：${currentQ.correctAnswer}）")
                                        },
                                        fontWeight = FontWeight.Bold,
                                        color = if (ok) Color(0xFF065F46) else Color(0xFF991B1B)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = exp, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation Row: Previous, Next, Submit Exam
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { vm.navigateExamQuestion(state.currentIndex - 1) },
                    enabled = state.currentIndex > 0,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(t("ข้อก่อนหน้า", "Previous", "上一题"))
                }

                if (state.currentIndex < state.questions.lastIndex) {
                    PtGradientButton(
                        text = t("ข้อถัดไป ➡️", "Next ➡️", "下一题 ➡️"),
                        onClick = { vm.navigateExamQuestion(state.currentIndex + 1) },
                        modifier = Modifier.weight(1f),
                        testTag = "exam_next_button"
                    )
                } else {
                    PtGradientButton(
                        text = t("✅ ส่งกระดาษคำตอบ", "✅ Submit Exam", "✅ 提交试卷"),
                        onClick = { showSubmitConfirm = true },
                        gradientColors = listOf(PtEmerald, PtSkyBlue),
                        modifier = Modifier.weight(1f),
                        testTag = "exam_submit_button"
                    )
                }
            }

            if (state.currentIndex < state.questions.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { showSubmitConfirm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(t("ส่งกระดาษคำตอบทันที (${state.answers.size}/${state.questions.size} ข้อ)", "Submit Exam Early (${state.answers.size}/${state.questions.size})", "提前交卷（已答 ${state.answers.size}/${state.questions.size}）"))
                }
            }
        }
    }

    if (showSubmitConfirm) {
        val unanswered = state.questions.size - state.answers.count { it.value.isNotBlank() }
        AlertDialog(
            onDismissRequest = { showSubmitConfirm = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text(t("ยืนยันการส่งข้อสอบ?", "Submit Your Exam?", "确认提交试卷？"), fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    if (unanswered > 0) {
                        t("คุณยังไม่ได้ตอบอีก $unanswered ข้อ ต้องการส่งกระดาษคำตอบเลยหรือไม่?", "You still have $unanswered unanswered question(s). Submit anyway?", "还有 $unanswered 道题未作答，确定现在交卷吗？")
                    } else {
                        t("ตอบครบทุกข้อเรียบร้อย! พร้อมตรวจคะแนนและรับ XP + PT Points แล้วหรือยัง?", "All questions answered! Ready to see your score and rewards?", "已全部作答！准备好查看成绩与奖励了吗？")
                    }
                )
            },
            confirmButton = {
                PtGradientButton(
                    text = t("ยืนยันส่งคำตอบ", "Confirm Submit", "确认交卷"),
                    onClick = {
                        showSubmitConfirm = false
                        vm.submitActiveExam(onSubmitted = onExamFinished)
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirm = false }) {
                    Text(t("กลับไปทบทวน", "Review More", "继续检查"))
                }
            }
        )
    }

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text(t("ออกจากห้องสอบ?", "Leave Exam Room?", "离开考场？"), fontWeight = FontWeight.Bold) },
            text = {
                Text(t("ความคืบหน้าข้อสอบถูกบันทึกอัตโนมัติแล้ว ต้องการกลับหน้าหลักหรือไม่?", "Progress is auto-saved. Return to the Home screen?", "当前进度已自动保存，是否返回主页？"))
            },
            confirmButton = {
                TextButton(onClick = {
                    showExitConfirm = false
                    onExitToHome()
                }) {
                    Text(t("กลับหน้าหลัก", "Return Home", "返回主页"), color = NegativeRedAlert)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) {
                    Text(t("ทำข้อสอบต่อ", "Continue Exam", "继续答题"))
                }
            }
        )
    }
}

@Composable
fun ExamResultScreen(
    user: UserEntity,
    vm: GameViewModel,
    onRetryAnotherSet: () -> Unit,
    onBackHome: () -> Unit
) {
    BackHandler(onBack = onBackHome)
    val lang = LocalAppLanguage.current
    val outcomePair by vm.lastExamOutcome.collectAsState()
    var showExplanations by rememberSaveable { mutableStateOf(true) }

    val (summary, questionPairs) = outcomePair ?: run {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            PtGradientButton(text = t("กลับหน้าหลัก", "Back Home", "返回主页"), onClick = onBackHome)
        }
        return
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
                currentPath = "/exam/result",
                shareMessageExtra = tr(
                    lang,
                    "ฉันทำได้ ${summary.correctCount}/${summary.totalQuestions} (${summary.accuracyPercent}%) ได้รับ +${summary.finalXp} XP ใน PT's มาสอบเข้า ม.1!",
                    "I scored ${summary.correctCount}/${summary.totalQuestions} (${summary.accuracyPercent}%) and earned +${summary.finalXp} XP on PT's Pre-M.1!",
                    "我在 PT's 初一入学模拟考获得了 ${summary.correctCount}/${summary.totalQuestions} (${summary.accuracyPercent}%)，赢得 +${summary.finalXp} XP！"
                ),
                onCopiedToast = { vm.showToast(it) }
            )
        }

        item {
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    PtOwlMascot(
                        mood = if (summary.accuracyPercent >= 60) MascotMood.CELEBRATE_REWARD else MascotMood.ENCOURAGE_WRONG,
                        reducedMotion = user.reducedMotion,
                        size = 92.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (summary.accuracyPercent == 100) {
                            t("🏆 PERFECT 100%! ยอดอัจฉริยะสอบเข้า ม.1!", "🏆 PERFECT 100%! M.1 Grand Champion!", "🏆 满分通关！初一学霸诞生！")
                        } else {
                            t("สรุปผลการทำข้อสอบของคุณ", "Your Exam Performance Summary", "本次考试成绩总结")
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = PtSkyBlue
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${summary.correctCount} / ${summary.totalQuestions} (${summary.accuracyPercent}%)",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (summary.accuracyPercent >= 60) PtEmerald else PtRosePink
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Reward & Streak Badges Row
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(t("XP ที่ได้รับ", "XP Earned", "获得经验"), style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "+${formatNumber(lang, summary.finalXp)} XP",
                                fontWeight = FontWeight.ExtraBold,
                                color = PtRosePink,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(t("PT Points สุทธิ", "Net PT Points", "净得PT积分"), style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = if (summary.netPtDelta >= 0) "+${summary.netPtDelta} PT" else "${summary.netPtDelta} PT",
                                fontWeight = FontWeight.ExtraBold,
                                color = if (summary.netPtDelta >= 0) PtSkyBlue else NegativeRedAlert,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(t("Combo สูงสุด", "Max Combo", "最高连击"), style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "🔥 ×${summary.maxCombo}",
                                fontWeight = FontWeight.ExtraBold,
                                color = PtSoftGold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AnimatedStreakFlameBadge(streakDays = summary.streakAfter, reducedMotion = user.reducedMotion)
                        PtPointsCoinBadge(ptPoints = summary.newPtBalance, reducedMotion = user.reducedMotion)
                    }

                    if (summary.recoveredMissedDay) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(1.dp, PtEmerald)
                        ) {
                            Text(
                                text = t("✨ กู้คืนไฟ Streak ที่ขาดไป 1 วันสำเร็จ!", "✨ 1-Day Missed Streak Recovered!", "✨ 成功找回中断1天的连胜火焰！"),
                                color = PtEmerald,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (summary.comboShieldTriggered || summary.solarShieldTriggered) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = PtLavender, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = t("สกิลเกราะสัตว์เลี้ยงคู่หูทำงานปกป้องคุณในรอบนี้!", "Your Companion Pet Shield protected you in this exam!", "宠物护盾技能在本场考试中成功触发！"),
                                style = MaterialTheme.typography.labelMedium,
                                color = PtLavender,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            NegativePtWarningBanner(ptPoints = user.ptPoints, reducedMotion = user.reducedMotion)
        }

        // Action Buttons
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onRetryAnotherSet,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(t("ฝึกชุดต่อไป", "Practice Again", "再练一套"))
                }
                PtGradientButton(
                    text = t("กลับหน้าหลัก", "Back to Home", "返回主页"),
                    onClick = onBackHome,
                    modifier = Modifier.weight(1f),
                    testTag = "result_home_button"
                )
            }
        }

        // Detailed Explanations Review Section
        item {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = t("เฉลยและคำอธิบายละเอียดทุกข้อ", "Step-by-Step Explanations", "逐题详细解析"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                TextButton(onClick = { showExplanations = !showExplanations }) {
                    Text(if (showExplanations) t("ซ่อนเฉลย", "Hide", "收起") else t("แสดงเฉลย", "Show", "展开"))
                }
            }
        }

        if (showExplanations) {
            itemsIndexed(questionPairs) { idx, (q, userAns) ->
                val isCorrect = vm.repo.checkAnswerCorrect(q, userAns)
                val qText = when (lang) { AppLanguage.TH -> q.questionTh; AppLanguage.EN -> q.questionEn; AppLanguage.ZH -> q.questionZh }
                val expText = when (lang) { AppLanguage.TH -> q.explanationTh; AppLanguage.EN -> q.explanationEn; AppLanguage.ZH -> q.explanationZh }

                PtGlassCard(
                    borderColors = if (isCorrect) listOf(PtEmerald, PtSkyBlue) else listOf(NegativeRedAlert, PtRosePink),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (isCorrect) PtEmerald else NegativeRedAlert
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${t("ข้อ", "Q", "第")} ${idx + 1}: $qText",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${t("คำตอบของคุณ", "Your Answer", "你的答案")}: ${userAns.ifBlank { "-" }}  |  ${t("เฉลยที่ถูกต้อง", "Correct", "正确答案")}: ${q.correctAnswer}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isCorrect) PtEmerald else NegativeRedAlert,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = expText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
