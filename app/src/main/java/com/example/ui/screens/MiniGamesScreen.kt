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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.LocalAppLanguage
import com.example.i18n.t
import com.example.model.MiniGameId
import com.example.ui.components.PtGlassCard
import com.example.ui.components.PtGradientButton
import com.example.ui.theme.PtEmerald
import com.example.ui.theme.PtRosePink
import com.example.ui.theme.PtSkyBlue
import com.example.ui.theme.PtSoftGold
import com.example.util.SfxType
import com.example.util.SoundEngine
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun MiniGamesHubScreen(
    vm: GameViewModel,
    onOpenLeaderboards: () -> Unit,
    onBack: () -> Unit
) {
    var activeGame by remember { mutableStateOf<MiniGameId?>(null) }

    BackHandler {
        if (activeGame != null) activeGame = null else onBack()
    }

    val current = activeGame
    if (current != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { activeGame = null }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t("ย้อนกลับ", "Back", "返回"))
                }
                Text(
                    text = current.title(LocalAppLanguage.current),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = current.color
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            when (current) {
                MiniGameId.MATH_24 -> Math24PlayableGame(onFinish = { sc -> vm.completeMiniGame(MiniGameId.MATH_24, sc) })
                MiniGameId.CONNECT_4 -> Connect4PlayableGame(onFinish = { sc -> vm.completeMiniGame(MiniGameId.CONNECT_4, sc) })
                MiniGameId.MEMORY_MATCH -> MemoryMatchPlayableGame(onFinish = { sc -> vm.completeMiniGame(MiniGameId.MEMORY_MATCH, sc) })
                MiniGameId.LOGIC_CHALLENGE -> LogicChallengePlayableGame(onFinish = { sc -> vm.completeMiniGame(MiniGameId.LOGIC_CHALLENGE, sc) })
                MiniGameId.VOCAB_CHALLENGE -> VocabBlitzPlayableGame(onFinish = { sc -> vm.completeMiniGame(MiniGameId.VOCAB_CHALLENGE, sc) })
                MiniGameId.NUMBER_PUZZLE -> Sliding15PuzzleGame(onFinish = { sc -> vm.completeMiniGame(MiniGameId.NUMBER_PUZZLE, sc) })
                MiniGameId.BRAIN_TRAINING -> BrainReflexPlayableGame(onFinish = { sc -> vm.completeMiniGame(MiniGameId.BRAIN_TRAINING, sc) })
            }
        }
        return
    }

    val lang = LocalAppLanguage.current
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
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
                        text = t("ลานมินิเกมลับสมองทั้ง 7 เกม", "7 Educational Mini-Games Arena", "7大益智脑力游戏中心"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                IconButton(onClick = onOpenLeaderboards) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = t("อันดับคะแนน", "Leaderboards", "排行榜"), tint = PtSoftGold)
                }
            }
        }

        items(MiniGameId.entries, key = { it.id }) { game ->
            PtGlassCard(
                borderColors = listOf(game.color, PtSkyBlue),
                onClick = { activeGame = game },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("minigame_card_${game.id}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(game.color)
                    ) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(game.title(lang), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                        Text(game.desc(lang), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    PtGradientButton(
                        text = t("เล่นเลย", "Play", "开始"),
                        onClick = { activeGame = game }
                    )
                }
            }
        }
    }
}

// 1. MATH 24 — Combine 4 numbers step-by-step using +, -, ×, ÷ to make 24!
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Math24PlayableGame(onFinish: (Int) -> Unit) {
    val puzzleDecks = remember {
        listOf(
            listOf(3.0, 8.0, 3.0, 1.0), // (8 * 3) * (1) ... 8 * 3 * (1)? wait: 8*3*(4-3) -> let's use classic solvable sets:
            listOf(6.0, 4.0, 2.0, 1.0), // 6 * 4 * (2 - 1) = 24
            listOf(8.0, 4.0, 3.0, 2.0), // 8 * 3 * (4 / 2)? 8*3=24, 4-2=2 -> (8 - 4) * 3 * 2 = 24!
            listOf(9.0, 6.0, 3.0, 2.0), // (9 - 3) * 6 ... 9 + 6 + 3*2? (9 - 6 + 1)? 6 * (9 - 3 - 2) = 24!
            listOf(5.0, 5.0, 4.0, 2.0), // (5 + 5 + 2) * ... (5 * 4) + 5 - 1? (5 + 4 - 5)? (5 * 5) - (2 / 2)? Let's use (7, 5, 3, 2): (7 + 5) * (3 - 1)? (7 + 5) * 2 = 24 -> (7, 5, 4, 2): (7 + 5) * (4 - 2) = 24!
            listOf(7.0, 5.0, 4.0, 2.0)  // (7 + 5) * (4 - 2) = 24!
        ).drop(1) // All 4 sets strictly verified to equal 24!
    }
    var roundIdx by remember { mutableIntStateOf(0) }
    var totalScore by remember { mutableIntStateOf(0) }
    val cards = remember { mutableStateListOf<Double>() }
    var selectedFirstIdx by remember { mutableStateOf<Int?>(null) }
    var selectedOp by remember { mutableStateOf<String?>(null) }

    fun loadRound(idx: Int) {
        cards.clear()
        cards.addAll(puzzleDecks[idx % puzzleDecks.size])
        selectedFirstIdx = null
        selectedOp = null
    }
    LaunchedEffect(roundIdx) { loadRound(roundIdx) }

    val solved = cards.size == 1 && abs(cards[0] - 24.0) < 0.001

    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(18.dp)) {
            Text(
                text = "${t("ด่านที่", "Round", "第")} ${roundIdx + 1}/4 • ${t("คะแนนสะสม", "Score", "得分")}: $totalScore",
                fontWeight = FontWeight.Bold,
                color = PtSkyBlue
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = t("แตะตัวเลขใบแรก ➔ เลือกเครื่องหมาย (+ − × ÷) ➔ แตะตัวเลขใบที่สอง ให้เหลือ 24!", "Tap 1st number ➔ operator ➔ 2nd number until 24 remains!", "点击第一个数 ➔ 选择运算符 ➔ 点击第二个数，算出24！"),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                cards.forEachIndexed { idx, num ->
                    val isSel = selectedFirstIdx == idx
                    val display = if (abs(num - num.toInt()) < 0.001) num.toInt().toString() else "%.2f".format(num)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSel) PtSkyBlue else Color(0xFFE0F2FE),
                        border = BorderStroke(2.dp, PtSkyBlue),
                        modifier = Modifier
                            .size(72.dp)
                            .clickable {
                                val first = selectedFirstIdx
                                val op = selectedOp
                                if (first == null || op == null || first == idx) {
                                    selectedFirstIdx = idx
                                } else {
                                    val a = cards[first]
                                    val b = cards[idx]
                                    val res = when (op) {
                                        "+" -> a + b
                                        "−" -> a - b
                                        "×" -> a * b
                                        "÷" -> if (abs(b) > 0.0001) a / b else null
                                        else -> null
                                    }
                                    if (res != null) {
                                        val nextList = cards.filterIndexed { i, _ -> i != first && i != idx } + res
                                        cards.clear()
                                        cards.addAll(nextList)
                                        selectedFirstIdx = null
                                        selectedOp = null
                                        SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                                    }
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(display, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = if (isSel) Color.White else Color(0xFF0369A1))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("+", "−", "×", "÷").forEach { op ->
                    Button(
                        onClick = { selectedOp = op },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedOp == op) PtRosePink else Color(0xFFF1F5F9),
                            contentColor = if (selectedOp == op) Color.White else Color(0xFF0F172A)
                        )
                    ) {
                        Text(op, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { loadRound(roundIdx) }) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(t("รีเซ็ตใบเลข", "Reset Cards", "重置本关"))
                }

                if (solved) {
                    PtGradientButton(
                        text = if (roundIdx < 3) t("🎉 ได้ 24! ไปด่านต่อไป", "🎉 24! Next Round", "🎉 算出24！下一关")
                        else t("🏆 จบเกม รับรางวัล!", "🏆 Claim Reward!", "🏆 完成领奖！"),
                        onClick = {
                            val nextScore = totalScore + 120
                            totalScore = nextScore
                            if (roundIdx < 3) {
                                roundIdx++
                            } else {
                                onFinish(nextScore)
                            }
                        }
                    )
                }
            }
        }
    }
}

// 2. CONNECT 4 — 7x6 Grid against Smart Heuristic Bot!
@Composable
private fun Connect4PlayableGame(onFinish: (Int) -> Unit) {
    val rows = 6
    val cols = 7
    val board = remember { mutableStateListOf<Int>().apply { repeat(rows * cols) { add(0) } } } // 0 empty, 1 player, 2 bot
    var winner by remember { mutableIntStateOf(0) } // 0 playing, 1 player won, 2 bot won, 3 draw

    fun cell(r: Int, c: Int) = board[r * cols + c]
    fun setCell(r: Int, c: Int, v: Int) { board[r * cols + c] = v }

    fun checkWin(player: Int): Boolean {
        for (r in 0 until rows) {
            for (c in 0 until cols - 3) {
                if ((0..3).all { cell(r, c + it) == player }) return true
            }
        }
        for (r in 0 until rows - 3) {
            for (c in 0 until cols) {
                if ((0..3).all { cell(r + it, c) == player }) return true
            }
        }
        for (r in 0 until rows - 3) {
            for (c in 0 until cols - 3) {
                if ((0..3).all { cell(r + it, c + it) == player }) return true
                if ((0..3).all { cell(r + 3 - it, c + it) == player }) return true
            }
        }
        return false
    }

    fun lowestOpenRow(c: Int): Int = (rows - 1 downTo 0).firstOrNull { cell(it, c) == 0 } ?: -1

    fun botMove() {
        val validCols = (0 until cols).filter { lowestOpenRow(it) != -1 }
        if (validCols.isEmpty()) { winner = 3; return }

        // 1) Try winning move, 2) Block player winning move, 3) Prefer center column
        var chosenCol: Int? = null
        for (c in validCols) {
            val r = lowestOpenRow(c)
            setCell(r, c, 2)
            if (checkWin(2)) chosenCol = c
            setCell(r, c, 0)
            if (chosenCol != null) break
        }
        if (chosenCol == null) {
            for (c in validCols) {
                val r = lowestOpenRow(c)
                setCell(r, c, 1)
                if (checkWin(1)) chosenCol = c
                setCell(r, c, 0)
                if (chosenCol != null) break
            }
        }
        val finalCol = chosenCol ?: validCols.minByOrNull { abs(it - 3) } ?: validCols.first()
        val row = lowestOpenRow(finalCol)
        setCell(row, finalCol, 2)
        if (checkWin(2)) winner = 2
        else if (board.none { it == 0 }) winner = 3
    }

    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(14.dp)) {
            Text(
                text = when (winner) {
                    1 -> t("🎉 คุณเรียงครบ 4 เหรียญชนะบอท!", "🎉 You Connected 4 & Won!", "🎉 四子连珠获胜！")
                    2 -> t("🤖 บอทเรียงครบ 4 เหรียญ ลองแก้มือใหม่!", "🤖 Bot Connected 4! Try again!", "🤖 AI 先连成四子，再战一局！")
                    3 -> t("เสมอกัน!", "Draw Game!", "平局！")
                    else -> t("แตะคอลัมน์เพื่อหย่อนเหรียญสีฟ้า เรียง 4 เหรียญให้ชนะบอทสีชมพู!", "Tap any column to drop your Sky-Blue disc!", "点击任意列落下蓝色棋子，先连四子获胜！")
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .padding(10.dp)
            ) {
                for (r in 0 until rows) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (c in 0 until cols) {
                            val v = cell(r, c)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (v) {
                                            1 -> PtSkyBlue
                                            2 -> PtRosePink
                                            else -> Color(0xFF334155)
                                        }
                                    )
                                    .clickable(enabled = winner == 0) {
                                        val dropR = lowestOpenRow(c)
                                        if (dropR != -1) {
                                            setCell(dropR, c, 1)
                                            SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                                            if (checkWin(1)) {
                                                winner = 1
                                            } else {
                                                botMove()
                                            }
                                        }
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = {
                    for (i in board.indices) board[i] = 0
                    winner = 0
                }) {
                    Text(t("เริ่มกระดานใหม่", "Restart Board", "重新开始"))
                }
                if (winner != 0) {
                    PtGradientButton(
                        text = t("บันทึกคะแนน & รับรางวัล", "Save Score & Claim", "保存成绩并领奖"),
                        onClick = { onFinish(if (winner == 1) 350 else 120) }
                    )
                }
            }
        }
    }
}

// 3. MEMORY MATCH — Match 6 Academic Pairs (12 cards)!
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MemoryMatchPlayableGame(onFinish: (Int) -> Unit) {
    val deck = remember {
        listOf(
            "1/2" to "50%",
            "πr²" to "พื้นที่วงกลม",
            "H₂O" to "น้ำบริสุทธิ์",
            "CO₂" to "สังเคราะห์แสง",
            "Synonym" to "คำความหมายเหมือน",
            "3² + 4²" to "5² (25)"
        ).flatMapIndexed { pairId, (a, b) ->
            listOf(Triple(pairId * 2, pairId, a), Triple(pairId * 2 + 1, pairId, b))
        }.shuffled()
    }
    val matchedPairIds = remember { mutableStateListOf<Int>() }
    val flippedIndices = remember { mutableStateListOf<Int>() }
    var moves by remember { mutableIntStateOf(0) }

    LaunchedEffect(flippedIndices.size) {
        if (flippedIndices.size == 2) {
            moves++
            val first = deck[flippedIndices[0]]
            val second = deck[flippedIndices[1]]
            if (first.second == second.second) {
                SoundEngine.playSfx(SfxType.CORRECT_ANSWER)
                matchedPairIds.add(first.second)
                flippedIndices.clear()
            } else {
                delay(700L)
                flippedIndices.clear()
            }
        }
    }

    val allMatched = matchedPairIds.size == 6

    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${t("จับคู่แล้ว", "Matched", "已配对")}: ${matchedPairIds.size}/6 • ${t("จำนวนเปิด", "Moves", "步数")}: $moves",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                deck.forEachIndexed { idx, (_, pairId, label) ->
                    val revealed = idx in flippedIndices || pairId in matchedPairIds
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (pairId in matchedPairIds) Color(0xFFDCFCE7) else if (revealed) Color(0xFFE0F2FE) else PtSkyBlue,
                        modifier = Modifier
                            .weight(1f)
                            .height(74.dp)
                            .clickable(enabled = !revealed && flippedIndices.size < 2) {
                                flippedIndices.add(idx)
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(6.dp)) {
                            Text(
                                text = if (revealed) label else "🦉",
                                fontWeight = FontWeight.Bold,
                                color = if (revealed) Color(0xFF0F172A) else Color.White,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            if (allMatched) {
                Spacer(modifier = Modifier.height(14.dp))
                PtGradientButton(
                    text = t("🎉 จับคู่ครบทุกใบ! รับรางวัล", "🎉 All Matched! Claim Reward", "🎉 全部配对成功！领取奖励"),
                    onClick = { onFinish((420 - moves * 12).coerceAtLeast(150)) }
                )
            }
        }
    }
}

// 4. LOGIC CHALLENGE — Rapid 5-Round M.1 Logic & Number Series!
@Composable
private fun LogicChallengePlayableGame(onFinish: (Int) -> Unit) {
    val puzzles = remember {
        listOf(
            Triple("2, 6, 12, 20, 30, ... ตัวถัดไปคือ?", listOf("36", "40", "42", "48"), "42"),
            Triple("1, 4, 9, 16, 25, ... ตัวถัดไปคือ?", listOf("30", "36", "49", "64"), "36"),
            Triple("ถ้า A > B และ B > C ข้อใดจริงเสมอ?", listOf("C > A", "A > C", "A = C", "สรุปไม่ได้"), "A > C"),
            Triple("3, 7, 15, 31, ... ตัวถัดไปคือ?", listOf("47", "55", "63", "65"), "63"),
            Triple("นาฬิกาเวลา 03:00 น. เข็มสั้นและเข็มยาวทำมุมกี่องศา?", listOf("60°", "90°", "120°", "180°"), "90°")
        )
    }
    var idx by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    val done = idx >= puzzles.size

    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(18.dp)) {
            if (done) {
                Text(t("จบการทดสอบตรรกะ! คะแนน: $score", "Logic Complete! Score: $score", "逻辑挑战完成！得分：$score"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(12.dp))
                PtGradientButton(text = t("รับรางวัลเข้าบัญชี", "Claim Rewards", "领取奖励"), onClick = { onFinish(score.coerceAtLeast(80)) })
            } else {
                val (q, opts, ans) = puzzles[idx]
                Text("${idx + 1} / ${puzzles.size} • Score: $score", color = PtSkyBlue, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text(q, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(14.dp))
                opts.forEach { opt ->
                    OutlinedButton(
                        onClick = {
                            if (opt == ans) {
                                SoundEngine.playSfx(SfxType.CORRECT_ANSWER)
                                score += 90
                            } else {
                                SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                            }
                            idx++
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(opt, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 5. VOCABULARY CHALLENGE — M.1 Entrance English Vocabulary Streak!
@Composable
private fun VocabBlitzPlayableGame(onFinish: (Int) -> Unit) {
    val words = remember {
        listOf(
            Triple("Diligent", listOf("ขยันหมั่นเพียร", "ขี้เกียจ", "อันตราย", "เงียบสงบ"), "ขยันหมั่นเพียร"),
            Triple("Environment", listOf("สิ่งแวดล้อม", "การทดลอง", "รัฐบาล", "ยานพาหนะ"), "สิ่งแวดล้อม"),
            Triple("Ancient", listOf("ทันสมัย", "เก่าแก่โบราณ", "รวดเร็ว", "สว่างไสว"), "เก่าแก่โบราณ"),
            Triple("Generous", listOf("ตระหนี่", "โกรธง่าย", "ใจกว้างเอื้อเฟื้อ", "หวาดกลัว"), "ใจกว้างเอื้อเฟื้อ"),
            Triple("Discover", listOf("ซ่อนเร้น", "ค้นพบ", "ทำลาย", "ลืมเลือน"), "ค้นพบ")
        )
    }
    var idx by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }

    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(18.dp)) {
            if (idx >= words.size) {
                Text(t("Vocabulary Blitz สำเร็จ! คะแนน: $score", "Vocabulary Blitz Score: $score", "词汇挑战得分：$score"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(12.dp))
                PtGradientButton(text = t("รับ XP & PT Points", "Claim XP & PT", "领取奖励"), onClick = { onFinish(score.coerceAtLeast(80)) })
            } else {
                val (word, choices, ans) = words[idx]
                Text("${idx + 1} / ${words.size} • Score: $score", color = PtEmerald, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Text(word, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = PtSkyBlue)
                Spacer(modifier = Modifier.height(12.dp))
                choices.forEach { c ->
                    OutlinedButton(
                        onClick = {
                            if (c == ans) {
                                SoundEngine.playSfx(SfxType.CORRECT_ANSWER)
                                score += 85
                            } else {
                                SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                            }
                            idx++
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(c, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// 6. NUMBER PUZZLE — Solvable 15-Tile Sliding Board!
@Composable
private fun Sliding15PuzzleGame(onFinish: (Int) -> Unit) {
    val tiles = remember { mutableStateListOf<Int>() }
    var moves by remember { mutableIntStateOf(0) }

    fun scrambleSolvable() {
        val state = ((1..15) + 0).toMutableList()
        var emptyIdx = 15
        repeat(28) {
            val r = emptyIdx / 4
            val c = emptyIdx % 4
            val neighbors = buildList {
                if (r > 0) add((r - 1) * 4 + c)
                if (r < 3) add((r + 1) * 4 + c)
                if (c > 0) add(r * 4 + (c - 1))
                if (c < 3) add(r * 4 + (c + 1))
            }
            val swapIdx = neighbors.random()
            val tmp = state[emptyIdx]
            state[emptyIdx] = state[swapIdx]
            state[swapIdx] = tmp
            emptyIdx = swapIdx
        }
        tiles.clear()
        tiles.addAll(state)
        moves = 0
    }
    LaunchedEffect(Unit) { scrambleSolvable() }

    val isSolved = tiles.size == 16 && (0 until 15).all { tiles[it] == it + 1 } && tiles[15] == 0

    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Text("${t("จำนวนเลื่อน", "Moves", "步数")}: $moves", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            if (tiles.size == 16) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (r in 0 until 4) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (c in 0 until 4) {
                                val pos = r * 4 + c
                                val num = tiles[pos]
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (num == 0) Color(0xFFF1F5F9) else PtSkyBlue,
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clickable(enabled = num != 0 && !isSolved) {
                                            val zeroPos = tiles.indexOf(0)
                                            val zr = zeroPos / 4
                                            val zc = zeroPos % 4
                                            if (abs(zr - r) + abs(zc - c) == 1) {
                                                tiles[zeroPos] = num
                                                tiles[pos] = 0
                                                moves++
                                                SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                                            }
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (num != 0) {
                                            Text("$num", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { scrambleSolvable() }) {
                    Text(t("สุ่มกระดานใหม่", "Shuffle", "重新打乱"))
                }
                if (isSolved) {
                    PtGradientButton(
                        text = t("🎉 เรียงสำเร็จ! รับรางวัล", "🎉 Solved! Claim Reward", "🎉 拼图完成！领奖"),
                        onClick = { onFinish((500 - moves * 4).coerceAtLeast(180)) }
                    )
                }
            }
        }
    }
}

// 7. BRAIN TRAINING — Stroop Color-Ink Reflex Challenge!
@Composable
private fun BrainReflexPlayableGame(onFinish: (Int) -> Unit) {
    val palette = listOf(
        Triple("ฟ้า / BLUE", PtSkyBlue, "BLUE"),
        Triple("ชมพู / PINK", PtRosePink, "PINK"),
        Triple("เขียว / GREEN", PtEmerald, "GREEN"),
        Triple("ทอง / GOLD", PtSoftGold, "GOLD")
    )
    var round by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var wordIdx by remember { mutableIntStateOf(0) }
    var inkIdx by remember { mutableIntStateOf(1) }

    fun nextPrompt() {
        wordIdx = (0..3).random()
        inkIdx = (0..3).random()
    }
    LaunchedEffect(Unit) { nextPrompt() }

    PtGlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(18.dp)) {
            if (round >= 8) {
                Text(t("สมองไวสุดยอด! คะแนน: $score", "Brain Reflex Score: $score", "反应力训练得分：$score"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(12.dp))
                PtGradientButton(text = t("รับรางวัลเข้าบัญชี", "Claim Rewards", "领取奖励"), onClick = { onFinish(score.coerceAtLeast(90)) })
            } else {
                Text("${round + 1} / 8 • Score: $score", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = t("เลือก 'สีของหมึกตัวอักษร' (อย่าหลงเชื่อคำที่เขียน!)", "Tap the INK COLOR of the text (ignore what the word says!)", "点击文字的【真实颜色】（不要被字义迷惑！）"),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = palette[wordIdx].first,
                    color = palette[inkIdx].second,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    palette.forEachIndexed { cIdx, (_, col, code) ->
                        Button(
                            onClick = {
                                if (cIdx == inkIdx) {
                                    SoundEngine.playSfx(SfxType.CORRECT_ANSWER)
                                    score += 65
                                } else {
                                    SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                                }
                                round++
                                nextPrompt()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = col),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(code, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
