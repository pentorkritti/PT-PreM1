package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.StudySheetEntity
import com.example.data.local.UserEntity
import com.example.i18n.LocalAppLanguage
import com.example.i18n.formatDateTime
import com.example.i18n.t
import com.example.model.SubjectId
import com.example.ui.components.MascotMood
import com.example.ui.components.PtGlassCard
import com.example.ui.components.PtGradientButton
import com.example.ui.components.PtOwlMascot
import com.example.ui.theme.NegativeRedAlert
import com.example.ui.theme.PtEmerald
import com.example.ui.theme.PtLavender
import com.example.ui.theme.PtRosePink
import com.example.ui.theme.PtSkyBlue
import com.example.util.SfxType
import com.example.util.SoundEngine
import com.example.viewmodel.GameViewModel
import org.json.JSONArray

@Composable
fun AiTutorScreen(
    user: UserEntity,
    vm: GameViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val lang = LocalAppLanguage.current
    val messages by vm.tutorMessages.collectAsState()
    val streamingText by vm.aiTutorStreamingText.collectAsState()
    val isBusy by vm.isAiTutorBusy.collectAsState()

    var input by rememberSaveable { mutableStateOf("") }
    var useHighThinking by rememberSaveable { mutableStateOf(true) }
    var showClearConfirm by remember { mutableStateOf(false) }

    val quickPrompts = listOf(
        t("สอนเทคนิคหา ห.ร.ม. และ ค.ร.น. โจทย์สอบเข้า ม.1", "Explain GCD & LCM tricks for M.1 entrance", "讲解初一入学最大公因数与最小公倍数技巧"),
        t("สรุปเรื่องแรงและไฟฟ้า ป.6 แบบเข้าใจง่าย", "Summarize Grade 6 Forces & Electricity", "简明总结六年级力学与电路核心考点"),
        t("อธิบาย Present Perfect กับ Past Simple ต่างกันอย่างไร", "Difference between Present Perfect & Past Simple", "解释现在完成时与一般过去时的区别"),
        t("ตัวอย่างโจทย์อนุกรมเชาวน์ปัญญาพร้อมวิธีคิด", "Give a sample IQ number series problem & steps", "给出一道智力数列推理例题与解析")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
    ) {
        // Header + High Thinking Toggle + Clear Chat
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                }
                PtOwlMascot(mood = MascotMood.IDLE_WAVE, reducedMotion = user.reducedMotion, size = 42.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = t("พี่นกฮูก PT's AI Tutor", "PT's Owl AI Tutor", "PT 猫头鹰 AI 导师"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (useHighThinking) {
                            t("โหมดคิดวิเคราะห์ลึก (High Thinking)", "High-Thinking Reasoning Mode", "深度推理模式（High Thinking）")
                        } else {
                            t("โหมดตอบไว (Fast Flash)", "Fast Flash Mode", "快速问答模式")
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (useHighThinking) PtLavender else PtSkyBlue
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = if (useHighThinking) PtLavender else Color.Gray)
                Switch(
                    checked = useHighThinking,
                    onCheckedChange = { useHighThinking = it },
                    modifier = Modifier.testTag("ai_tutor_thinking_switch")
                )
                IconButton(onClick = { showClearConfirm = true }) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = t("ล้างแชท", "Clear Chat", "清空对话"), tint = NegativeRedAlert)
                }
            }
        }

        // Quick Academic Topic Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { qp ->
                FilterChip(
                    selected = false,
                    onClick = {
                        if (!isBusy) vm.sendTutorPrompt(qp, useHighThinking, lang)
                    },
                    label = { Text(qp, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        // Scrollable Multi-Turn Conversation Area
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty() && streamingText == null) {
                item {
                    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            PtOwlMascot(mood = MascotMood.CELEBRATE_REWARD, reducedMotion = user.reducedMotion, size = 76.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = t(
                                    "สวัสดีจ้า ${user.displayName}! ถามโจทย์คณิต วิทย์ ไทย อังกฤษ สังคม หรือเชาวน์ปัญญาได้เลย พี่นกฮูกพร้อมอธิบายละเอียดทีละขั้น!",
                                    "Hi ${user.displayName}! Ask me any M.1 entrance topic or tricky problem for step-by-step explanations!",
                                    "你好 ${user.displayName}！任何初一入学难题都可以问我，我会一步步为你讲解！"
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                val isUser = msg.role == "user"
                Row(
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        ),
                        color = if (isUser) PtSkyBlue else Color.White.copy(alpha = 0.95f),
                        border = if (isUser) null else BorderStroke(1.dp, PtSkyBlue.copy(alpha = 0.35f)),
                        shadowElevation = 3.dp,
                        modifier = Modifier.fillMaxWidth(0.88f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (!isUser) {
                                Text(
                                    text = "🦉 AI Tutor • ${msg.modelTag}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PtRosePink,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = msg.content,
                                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (streamingText != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        border = BorderStroke(1.5.dp, PtLavender),
                        modifier = Modifier.fillMaxWidth(0.90f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = PtLavender)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = t("พี่นกฮูกกำลังคิดวิเคราะห์คำตอบ...", "Owl Tutor is thinking...", "猫头鹰导师正在思考..."),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PtLavender,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (streamingText!!.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = streamingText!!, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        // Composer Bar
        Surface(
            color = Color.White.copy(alpha = 0.94f),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text(t("พิมพ์คำถามหรือวางโจทย์ที่สงสัย...", "Ask a study question...", "输入你的学习问题...")) },
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 4,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_tutor_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val toSend = input
                        input = ""
                        vm.sendTutorPrompt(toSend, useHighThinking, lang)
                    },
                    enabled = input.isNotBlank() && !isBusy,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (input.isNotBlank() && !isBusy) PtSkyBlue else Color(0xFFCBD5E1))
                        .testTag("ai_tutor_send_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = t("ส่งข้อความ", "Send", "发送"), tint = Color.White)
                }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(t("ล้างประวัติแชท AI Tutor?", "Clear Chat History?", "清空对话记录？"), fontWeight = FontWeight.Bold) },
            text = { Text(t("ข้อความสนทนาทั้งหมดจะถูกลบออกจากเครื่อง", "All chat messages will be cleared.", "所有历史对话将被清除。")) },
            confirmButton = {
                TextButton(onClick = {
                    showClearConfirm = false
                    vm.clearAiTutorChat()
                }) {
                    Text(t("ล้างข้อมูล", "Clear", "确认清空"), color = NegativeRedAlert)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text(t("ยกเลิก", "Cancel", "取消"))
                }
            }
        )
    }
}

private data class SheetQuizQuestion(
    val question: String,
    val choiceA: String,
    val choiceB: String,
    val choiceC: String,
    val choiceD: String,
    val correctAnswer: String,
    val explanation: String
)

private fun parseSheetQuizJson(jsonStr: String): List<SheetQuizQuestion> {
    return try {
        val arr = JSONArray(jsonStr)
        buildList {
            for (i in 0 until arr.length()) {
                val o = arr.optJSONObject(i) ?: continue
                add(
                    SheetQuizQuestion(
                        question = o.optString("question"),
                        choiceA = o.optString("choiceA"),
                        choiceB = o.optString("choiceB"),
                        choiceC = o.optString("choiceC"),
                        choiceD = o.optString("choiceD"),
                        correctAnswer = o.optString("correctAnswer", "A").uppercase(),
                        explanation = o.optString("explanation")
                    )
                )
            }
        }
    } catch (_: Exception) {
        emptyList()
    }
}

@Composable
fun StudySheetLibraryScreen(
    vm: GameViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lang = LocalAppLanguage.current
    val sheets by vm.studySheets.collectAsState()
    val isGenerating by vm.isGeneratingStudySheet.collectAsState()

    var activeSheet by remember { mutableStateOf<StudySheetEntity?>(null) }
    var showCreator by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // Creator Form States
    var subject by rememberSaveable { mutableStateOf(SubjectId.SCIENCE) }
    var sourceType by rememberSaveable { mutableStateOf("TEXT") }
    var sourceText by rememberSaveable { mutableStateOf("") }
    var quizSize by rememberSaveable { mutableIntStateOf(5) }
    var attachedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Zero-Permission Android Photo Picker for Multimodal Study Sheet Generation!
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val decoded = BitmapFactory.decodeStream(stream)
                    if (decoded != null) {
                        attachedBitmap = decoded
                        sourceType = "PHOTO"
                    }
                }
            } catch (_: Exception) {
                vm.showToast("ไม่สามารถอ่านไฟล์รูปภาพที่เลือกได้")
            }
        }
    }

    BackHandler {
        if (activeSheet != null) activeSheet = null
        else if (showCreator) showCreator = false
        else onBack()
    }

    val viewing = activeSheet
    if (viewing != null) {
        StudySheetDetailAndQuizView(
            sheet = viewing,
            onClose = { activeSheet = null }
        )
        return
    }

    val filteredSheets = remember(sheets, searchQuery) {
        sheets.filter {
            searchQuery.isBlank() ||
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.summaryContent.contains(searchQuery, ignoreCase = true)
        }
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
                        text = t("คลังชีทสรุป A4 & สร้างควิซ AI", "A4 Study Sheet Library & AI Quiz", "A4 智能总结库与测验"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                PtGradientButton(
                    text = if (showCreator) t("ปิดฟอร์ม", "Close", "收起") else t("+ สร้างชีทใหม่", "+ Create Sheet", "+ 新建笔记"),
                    onClick = { showCreator = !showCreator },
                    testTag = "toggle_create_sheet_button"
                )
            }
        }

        if (showCreator) {
            item {
                PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = t("✨ สร้างชีทสรุป A4 + แบบทดสอบอัตโนมัติ (Multimodal AI)", "✨ Create Multimodal A4 Summary + Quiz", "✨ AI 多模态生成 A4 复习总结与测验"),
                            fontWeight = FontWeight.ExtraBold,
                            color = PtSkyBlue
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Subject Selector
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(SubjectId.entries) { sub ->
                                FilterChip(
                                    selected = subject == sub,
                                    onClick = { subject = sub },
                                    label = { Text(sub.title(lang)) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Source Type Tabs (Text / Photo / PDF-Doc Text / YouTube Captions)
                        val sourceTypes = listOf(
                            "TEXT" to t("ข้อความบทเรียน", "Lesson Text", "课文笔记"),
                            "PHOTO" to t("รูปถ่ายสมุด/หนังสือ", "Notebook Photo", "拍照上传"),
                            "PDF_DOC" to t("เนื้อหาจากเอกสาร/PDF", "Doc/PDF Notes", "文档摘录"),
                            "YOUTUBE_CAPTIONS" to t("คำบรรยายคลิปติว", "Video Transcript", "视频字幕")
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(sourceTypes) { (code, lbl) ->
                                FilterChip(
                                    selected = sourceType == code,
                                    onClick = { sourceType = code },
                                    label = { Text(lbl) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = PtRosePink)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(t("แนบรูปภาพหน้าหนังสือ/โจทย์ (ไม่ขอสิทธิ์เข้าถึงคลังภาพทั้งหมด)", "Attach Notebook/Textbook Photo (Zero-Permission Picker)", "选择课本或笔记照片"))
                        }

                        if (attachedBitmap != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(contentAlignment = Alignment.TopEnd) {
                                Image(
                                    bitmap = attachedBitmap!!.asImageBitmap(),
                                    contentDescription = "Attached study note",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                )
                                IconButton(onClick = { attachedBitmap = null }) {
                                    Icon(Icons.Default.Close, contentDescription = t("ลบรูป", "Remove image", "移除图片"), tint = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = sourceText,
                            onValueChange = { sourceText = it },
                            label = { Text(t("วางเนื้อหา หัวข้อที่ต้องการสรุป หรือคำอธิบายเพิ่มเติม...", "Paste lesson text, topic, or notes...", "粘贴需要总结的知识点或课文内容...")) },
                            minLines = 3,
                            maxLines = 6,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sheet_source_text_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = t("จำนวนข้อสอบท้ายชีทสรุป (5 / 10 / 20 ข้อ)", "Embedded Quiz Questions (5 / 10 / 20)", "随堂测验题量（5 / 10 / 20 题）"),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(5, 10, 20).forEach { qCnt ->
                                FilterChip(
                                    selected = quizSize == qCnt,
                                    onClick = { quizSize = qCnt },
                                    label = { Text("$qCnt ${t("ข้อ", "Qs", "题")}") }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        PtGradientButton(
                            text = if (isGenerating) {
                                t("กำลังสรุปชีท A4 และออกควิซ...", "Generating A4 Sheet & Quiz...", "正在生成 A4 笔记与测验...")
                            } else {
                                t("🚀 สร้างและบันทึกเข้าคลังชีทสรุป", "🚀 Generate & Save to Library", "🚀 立即生成并保存至笔记库")
                            },
                            onClick = {
                                if (sourceText.isBlank() && attachedBitmap == null) {
                                    vm.showToast("กรุณาพิมพ์หัวข้อ/เนื้อหา หรือแนบรูปภาพก่อนสร้างชีทสรุป")
                                } else {
                                    vm.createAiStudySheet(
                                        subject = subject,
                                        sourceType = sourceType,
                                        sourceText = sourceText.ifBlank { "สรุปเนื้อหาสำคัญจากภาพแนบสำหรับเตรียมสอบเข้า ม.1" },
                                        bitmap = attachedBitmap,
                                        quizSize = quizSize,
                                        lang = lang,
                                        onCreated = { created ->
                                            showCreator = false
                                            sourceText = ""
                                            attachedBitmap = null
                                            activeSheet = created
                                        }
                                    )
                                }
                            },
                            enabled = !isGenerating,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "submit_create_sheet_button"
                        )
                    }
                }
            }
        }

        // Search Saved Study Sheets
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text(t("ค้นหาชีทสรุปในคลังของคุณ...", "Search your study sheets...", "搜索已保存的复习笔记...")) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (filteredSheets.isEmpty()) {
            item {
                PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = PtSkyBlue, modifier = Modifier.size(46.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = t(
                                "คลังชีทสรุปยังว่างอยู่ กด '+ สร้างชีทใหม่' เพื่อเปลี่ยนบทเรียนหรือภาพถ่ายเป็นชีทสรุป A4 พร้อมควิซ 5/10/20 ข้อ!",
                                "Your library is empty! Tap '+ Create Sheet' to turn notes or photos into an A4 summary + quiz!",
                                "笔记库暂无内容，点击右上角即可用文字或照片生成 A4 复习总结与测验！"
                            )
                        )
                    }
                }
            }
        } else {
            items(filteredSheets, key = { it.id }) { sheet ->
                val sub = SubjectId.fromId(sheet.subjectId)
                PtGlassCard(
                    onClick = { activeSheet = sheet },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = sub.color, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sheet.title, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "${sub.title(lang)} • ${sheet.quizCount} ${t("ข้อควิซ", "Quiz Qs", "道测验")} • ${formatDateTime(lang, sheet.createdAtMillis)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { vm.deleteStudySheet(sheet.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = t("ลบชีท", "Delete", "删除"), tint = NegativeRedAlert)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudySheetDetailAndQuizView(
    sheet: StudySheetEntity,
    onClose: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val sub = SubjectId.fromId(sheet.subjectId)
    val quizList = remember(sheet.quizQuestionsJson) { parseSheetQuizJson(sheet.quizQuestionsJson) }
    val selectedAnswers = remember(sheet.id) { mutableStateMapOf<Int, String>() }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                }
                Text(
                    text = sheet.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Styled A4 Paper Summary Sheet
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(2.dp, sub.color),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "📄 A4 STUDY SHEET • ${sub.title(lang)}",
                        color = sub.color,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(sheet.summaryContent, style = MaterialTheme.typography.bodyMedium)

                    if (sheet.keyPointsText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF0F9FF),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(t("📌 จุดเน้นออกสอบที่ต้องจำ", "📌 Key Takeaways", "📌 核心必背考点"), fontWeight = FontWeight.Bold, color = PtSkyBlue)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(sheet.keyPointsText, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    if (sheet.sampleProblemText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFDF2F8),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(t("💡 ตัวอย่างโจทย์และวิธีทำ", "💡 Worked Example", "💡 经典例题解析"), fontWeight = FontWeight.Bold, color = PtRosePink)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(sheet.sampleProblemText, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        // Interactive Embedded Quiz
        item {
            Text(
                text = t("📝 แบบทดสอบท้ายชีท (${quizList.size} ข้อ)", "📝 Interactive Sheet Quiz (${quizList.size} Qs)", "📝 随堂互动测验（${quizList.size}题）"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        items(quizList.size) { index ->
            val q = quizList[index]
            val picked = selectedAnswers[index]
            PtGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("${index + 1}. ${q.question}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("A" to q.choiceA, "B" to q.choiceB, "C" to q.choiceC, "D" to q.choiceD).forEach { (let, txt) ->
                        val isChosen = picked == let
                        val isRight = let == q.correctAnswer
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when {
                                picked != null && isRight -> Color(0xFFDCFCE7)
                                isChosen && !isRight -> Color(0xFFFFE4E6)
                                else -> Color(0xFFF8FAFC)
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    picked != null && isRight -> PtEmerald
                                    isChosen && !isRight -> NegativeRedAlert
                                    else -> Color(0xFFCBD5E1)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable(enabled = picked == null) {
                                    selectedAnswers[index] = let
                                    if (let == q.correctAnswer) SoundEngine.playSfx(SfxType.CORRECT_ANSWER)
                                    else SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Text("$let. $txt", modifier = Modifier.weight(1f))
                                if (picked != null && isRight) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PtEmerald, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                    if (picked != null && q.explanation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "💡 ${q.explanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF0369A1)
                        )
                    }
                }
            }
        }
    }
}
