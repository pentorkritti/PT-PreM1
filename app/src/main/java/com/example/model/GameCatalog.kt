package com.example.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.i18n.AppLanguage
import com.example.i18n.tr

enum class SubjectId(
    val id: String,
    val nameTh: String,
    val nameEn: String,
    val nameZh: String,
    val subtopicsTh: List<String>,
    val subtopicsEn: List<String>,
    val subtopicsZh: List<String>,
    val curriculumCode: String,
    val accentHex: Long
) {
    THAI(
        "THAI", "ภาษาไทย", "Thai Language", "泰语",
        listOf("ชนิดของคำและประโยค", "คำราชาศัพท์และสำนวนไทย", "การอ่านจับใจความและวรรณคดี"),
        listOf("Parts of Speech & Sentences", "Royal Vocabulary & Idioms", "Reading Comprehension & Literature"),
        listOf("词类与句型", "皇室用语与成语", "阅读理解与文学"),
        "ท 1.1 / ท 4.1 ป.6", 0xFFEC4899
    ),
    MATH(
        "MATH", "คณิตศาสตร์", "Mathematics", "数学",
        listOf("ห.ร.ม. ค.ร.น. และเศษส่วน", "ร้อยละ อัตราส่วน และสมการ", "เรขาคณิต พื้นที่ และปริมาตร"),
        listOf("GCD, LCM & Fractions", "Percentages, Ratios & Equations", "Geometry, Area & Volume"),
        listOf("最大公因数与分数", "百分比、比例与方程", "几何面积与体积"),
        "ค 1.1 / ค 2.1 ป.6", 0xFF0EA5E9
    ),
    SCIENCE(
        "SCIENCE", "วิทยาศาสตร์", "Science", "科学",
        listOf("ระบบร่างกายและสิ่งมีชีวิต", "สารและการแยกสาร", "แรง ไฟฟ้า ดาราศาสตร์ และหิน"),
        listOf("Human Body & Ecosystems", "Matter & Separation", "Forces, Electricity & Astronomy"),
        listOf("人体与生态系统", "物质与分离", "力、电学与天文学"),
        "ว 1.2 / ว 2.1 / ว 3.1 ป.6", 0xFF10B981
    ),
    ENGLISH(
        "ENGLISH", "ภาษาอังกฤษ", "English", "英语",
        listOf("Grammar & Tenses", "Vocabulary & Conversation", "Reading Passages"),
        listOf("Grammar & Tenses", "Vocabulary & Conversation", "Reading Passages"),
        listOf("语法与时态", "词汇与日常对话", "英语阅读理解"),
        "ต 1.1 / ต 1.2 ป.6", 0xFF6366F1
    ),
    SOCIAL(
        "SOCIAL", "สังคมศึกษา ศาสนา และวัฒนธรรม", "Social Studies, Religion & Culture", "社会、宗教与文化",
        listOf("หน้าที่พลเมืองและกฎหมาย", "เศรษฐศาสตร์เบื้องต้น", "ภูมิศาสตร์ไทยและสิ่งแวดล้อม"),
        listOf("Civics & Basic Law", "Basic Economics", "Geography & Environment"),
        listOf("公民义务与法律", "基础经济学", "地理与环境"),
        "ส 2.1 / ส 3.1 / ส 5.1 ป.6", 0xFFF59E0B
    ),
    HISTORY(
        "HISTORY", "ประวัติศาสตร์", "History", "历史",
        listOf("สมัยรัตนโกสินทร์", "วิธีการทางประวัติศาสตร์", "อาเซียนและเพื่อนบ้าน"),
        listOf("Rattanakosin Era", "Historical Method", "ASEAN & Neighbors"),
        listOf("曼谷王朝历史", "历史研究方法", "东盟与邻国"),
        "ส 4.1 / ส 4.3 ป.6", 0xFFD97706
    ),
    HEALTH(
        "HEALTH", "สุขศึกษา", "Health Education", "健康教育",
        listOf("ระบบไหลเวียนโลหิตและโภชนาการ", "ความปลอดภัยและการปฐมพยาบาล", "การสร้างเสริมสมรรถภาพกาย"),
        listOf("Circulatory System & Nutrition", "Safety & First Aid", "Physical Fitness"),
        listOf("循环系统与营养", "安全与急救", "体能健康"),
        "พ 1.1 / พ 4.1 ป.6", 0xFF14B8A6
    ),
    ART(
        "ART", "ศิลปะ", "Arts", "艺术",
        listOf("ทัศนศิลป์และสีคู่ตรงข้าม", "ดนตรีไทยและดนตรีสากล", "นาฏศิลป์ไทย"),
        listOf("Visual Arts & Complementary Colors", "Thai & Western Music", "Thai Dramatic Arts"),
        listOf("视觉艺术与互补色", "泰国与西方音乐", "传统舞蹈艺术"),
        "ศ 1.1 / ศ 2.1 ป.6", 0xFFA855F7
    ),
    GENERAL_ANALYTICAL(
        "GENERAL_ANALYTICAL", "ความรู้รอบตัวและการคิดวิเคราะห์", "General Knowledge & Critical Thinking", "综合常识与分析思维",
        listOf("ข่าวสารเทคโนโลยีและสิ่งแวดล้อม", "การวิเคราะห์เหตุและผล", "การแก้ปัญหาในชีวิตประจำวัน"),
        listOf("Tech & Environmental Awareness", "Cause & Effect Analysis", "Real-World Problem Solving"),
        listOf("科技与环境常识", "因果关系分析", "日常问题解决"),
        "สมรรถนะการคิด ป.6-ม.1", 0xFF06B6D4
    ),
    IQ_LOGIC(
        "IQ_LOGIC", "เชาวน์ปัญญาและตรรกะ", "IQ & Logical Reasoning", "智力与逻辑推理",
        listOf("อนุกรมตัวเลขและแบบรูป", "มิติสัมพันธ์และการพับกล่อง", "ตรรกศาสตร์และเงื่อนไข"),
        listOf("Number Series & Patterns", "Spatial Reasoning", "Logic Puzzles & Conditions"),
        listOf("数字数列与规律", "空间推理", "逻辑条件推理"),
        "แนวสอบเข้า ม.1 ห้องพิเศษ", 0xFFF43F5E
    );

    fun title(lang: AppLanguage): String = tr(lang, nameTh, nameEn, nameZh)
    fun subtopics(lang: AppLanguage): List<String> = when (lang) {
        AppLanguage.TH -> subtopicsTh
        AppLanguage.EN -> subtopicsEn
        AppLanguage.ZH -> subtopicsZh
    }
    val color: Color get() = Color(accentHex)

    companion object {
        fun fromId(id: String): SubjectId = entries.firstOrNull { it.id == id } ?: MATH
    }
}

enum class DifficultyLevel(
    val id: String,
    val nameTh: String,
    val nameEn: String,
    val nameZh: String,
    val secondsPerQuestion: Int,
    val xpPerCorrect: Int,
    val colorHex: Long
) {
    EASY("EASY", "Easy (ง่าย)", "Easy", "简单", 60, 15, 0xFF10B981),
    NORMAL("NORMAL", "Normal (ปานกลาง)", "Normal", "普通", 55, 25, 0xFF0EA5E9),
    HARD("HARD", "Hard (ยาก)", "Hard", "困难", 50, 40, 0xFFF59E0B),
    EXPERT("EXPERT", "Expert (เชี่ยวชาญ)", "Expert", "专家", 45, 60, 0xFFA855F7),
    ENTRANCE_EXAM("ENTRANCE_EXAM", "Entrance Exam (สอบเข้า ม.1)", "M.1 Entrance Exam", "初一入学模拟", 45, 85, 0xFFEC4899);

    fun title(lang: AppLanguage): String = tr(lang, nameTh, nameEn, nameZh)
    val color: Color get() = Color(colorHex)

    companion object {
        fun fromId(id: String): DifficultyLevel = entries.firstOrNull { it.id == id } ?: NORMAL
    }
}

enum class QuestionFormat(
    val id: String,
    val labelTh: String,
    val labelEn: String,
    val labelZh: String
) {
    MULTIPLE_CHOICE("MULTIPLE_CHOICE", "ปรนัย 4 ตัวเลือก", "4-Choice MC", "四选一选择题"),
    FILL_IN("FILL_IN", "แบบเติมคำ", "Fill in the Blank", "填空题"),
    CALCULATION("CALCULATION", "แบบคำนวณ", "Calculation", "计算题"),
    READING("READING", "อ่านจับใจความ", "Reading Comprehension", "阅读理解"),
    SITUATIONAL("SITUATIONAL", "วิเคราะห์สถานการณ์", "Situational Analysis", "情境分析"),
    MATCHING("MATCHING", "แบบจับคู่", "Matching", "配对题"),
    ORDERING("ORDERING", "แบบเรียงลำดับ", "Ordering", "排序题"),
    WORD_PROBLEM("WORD_PROBLEM", "โจทย์ปัญหา", "Word Problem", "应用题");

    fun label(lang: AppLanguage): String = tr(lang, labelTh, labelEn, labelZh)
}

data class AvatarFrameSpec(
    val id: String,
    val nameTh: String,
    val nameEn: String,
    val nameZh: String,
    val unlockHintTh: String,
    val unlockHintEn: String,
    val unlockHintZh: String,
    val badgeEmoji: String,
    val colorsHex: List<Long>
) {
    fun name(lang: AppLanguage) = tr(lang, nameTh, nameEn, nameZh)
    fun unlockHint(lang: AppLanguage) = tr(lang, unlockHintTh, unlockHintEn, unlockHintZh)
}

data class TitleStyleSpec(
    val index: Int,
    val nameTh: String,
    val nameEn: String,
    val nameZh: String,
    val startHex: Long,
    val endHex: Long
) {
    fun name(lang: AppLanguage) = tr(lang, nameTh, nameEn, nameZh)
}

data class ShopItemCatalog(
    val id: String,
    val nameTh: String,
    val nameEn: String,
    val nameZh: String,
    val descTh: String,
    val descEn: String,
    val descZh: String,
    val pricePt: Int,
    val category: String, // THEME, BACKGROUND, AVATAR_FRAME, CONSUMABLE, PERMANENT, MULTIPLIER
    val multiplierGain: Double = 1.0,
    val multiplierPenalty: Int = 1,
    val durationHours: Int = 0,
    val backgroundId: String? = null,
    val frameId: String? = null
) {
    fun name(lang: AppLanguage) = tr(lang, nameTh, nameEn, nameZh)
    fun desc(lang: AppLanguage) = tr(lang, descTh, descEn, descZh)
}

data class BackgroundThemeSpec(
    val id: String,
    val nameTh: String,
    val nameEn: String,
    val nameZh: String,
    @DrawableRes val drawableRes: Int?,
    val topColorHex: Long,
    val bottomColorHex: Long
) {
    fun name(lang: AppLanguage) = tr(lang, nameTh, nameEn, nameZh)
}

enum class EggRarity(
    val id: String,
    val eggNameTh: String,
    val eggNameEn: String,
    val eggNameZh: String,
    val dropRatePercent: Int,
    val primaryHex: Long,
    val secondaryHex: Long
) {
    COMMON("COMMON", "ไข่ธรรมดา (Common)", "Common Egg", "普通蛋", 50, 0xFF38BDF8, 0xFFBAE6FD),
    UNCOMMON("UNCOMMON", "ไข่พิเศษ (Uncommon)", "Uncommon Egg", "优秀蛋", 25, 0xFF10B981, 0xFFA7F3D0),
    RARE("RARE", "ไข่หายาก (Rare)", "Rare Egg", "稀有蛋", 13, 0xFF6366F1, 0xFFC7D2FE),
    EPIC("EPIC", "ไข่มหากาพย์ (Epic)", "Epic Egg", "史诗蛋", 6, 0xFFA855F7, 0xFFF5D0FE),
    LEGENDARY("LEGENDARY", "ไข่ตำนานทอง (Legendary)", "Legendary Egg", "传说蛋", 3, 0xFFF59E0B, 0xFFFEF08A),
    SATRIWIT("SATRIWIT", "ไข่ดาวตกสตรีวิทย์ (Satriwit)", "Satriwit Egg", "萨特里维特星辰蛋", 2, 0xFFFF244C, 0xFFFFCDD2),
    SUANKULARB("SUANKULARB", "ไข่กุหลาบสวนกุหลาบ (Suankularb)", "Suankularb Egg", "玫瑰天蓝圣蛋", 1, 0xFFEC4899, 0xFF38BDF8);

    fun eggName(lang: AppLanguage) = tr(lang, eggNameTh, eggNameEn, eggNameZh)
    val primaryColor: Color get() = Color(primaryHex)
    val secondaryColor: Color get() = Color(secondaryHex)
}

enum class PetSkillType {
    TIME_BONUS_3,
    REMOVE_1_WRONG_ONCE,
    HINT_1_ONCE,
    XP_BONUS_2,
    PT_GAIN_BONUS_2,
    FREEZE_TIME_5S,
    CHANGE_ANSWER_ONCE,
    TIME_BONUS_6,
    REMOVE_2_WRONG_ONCE,
    HINT_2_TWICE,
    COMBO_SHIELD_ONCE,
    XP_BONUS_5,
    RETRY_WRONG_NO_REWARD,
    TIME_BONUS_10,
    MARK_REVIEW_3,
    PT_GAIN_BONUS_10,
    HINT_RETRY_WRONG_ONCE,
    WRONG_STREAK_SHIELD,
    XP_15_AND_SKIP_RETURN,
    TIME_10_REMOVE_1_TWICE
}

data class PetSpecies(
    val id: String,
    val number: Int,
    val nameTh: String,
    val nameEn: String,
    val nameZh: String,
    val rarity: EggRarity,
    val bodyColorHex: Long,
    val accentColorHex: Long,
    val powerLevel: Int,
    val skillType: PetSkillType,
    val descTh: String,
    val descEn: String,
    val descZh: String,
    val skillTh: String,
    val skillEn: String,
    val skillZh: String
) {
    fun name(lang: AppLanguage) = tr(lang, "$nameTh ($nameEn)", nameEn, "$nameZh ($nameEn)")
    fun desc(lang: AppLanguage) = tr(lang, descTh, descEn, descZh)
    fun skillText(lang: AppLanguage) = tr(lang, skillTh, skillEn, skillZh)
}

data class StreakFlameTier(
    val tierIndex: Int,
    val minDays: Int,
    val maxDays: Int,
    val labelTh: String,
    val labelEn: String,
    val labelZh: String,
    val colorsHex: List<Long>,
    val particleCount: Int,
    val speedFactor: Float
) {
    fun label(lang: AppLanguage) = tr(lang, labelTh, labelEn, labelZh)
}

enum class MiniGameId(
    val id: String,
    val titleTh: String,
    val titleEn: String,
    val titleZh: String,
    val descTh: String,
    val descEn: String,
    val descZh: String,
    val accentHex: Long
) {
    MATH_24(
        "MATH_24", "Math 24 คิดเร็ว 24", "Math 24 Arena", "24点速算",
        "ใช้ตัวเลข 4 ตัว บวก ลบ คูณ หาร ให้ได้ผลลัพธ์เท่ากับ 24",
        "Combine 4 numbers using +, −, ×, ÷ to make exactly 24.",
        "用四个数字通过加减乘除算出24。", 0xFF0EA5E9
    ),
    CONNECT_4(
        "CONNECT_4", "Connect 4 เรียงหมาก 4", "Connect 4 Duel", "四子连珠",
        "วางเหรียญสลับกับบอทอัจฉริยะ เรียงให้ครบ 4 เหรียญก่อนชนะ!",
        "Drop discs against Smart Bot — connect 4 in a row to win!",
        "与智能AI对战，先连成四子即可获胜！", 0xFFEC4899
    ),
    MEMORY_MATCH(
        "MEMORY_MATCH", "Memory Match จับคู่ความจำ", "Memory Match", "记忆翻牌",
        "จับคู่การ์ดคำศัพท์และสูตรคณิตศาสตร์ให้ครบในเวลาที่เร็วที่สุด",
        "Match pairs of academic symbols & formulas in record time.",
        "在最短时间内配对知识卡片。", 0xFFA855F7
    ),
    LOGIC_CHALLENGE(
        "LOGIC_CHALLENGE", "Logic Challenge ตรรกะพิชิต ม.1", "Logic Challenge", "逻辑推理挑战",
        "ไขปริศนาอนุกรมและเงื่อนไขตรรกะแบบจับเวลา",
        "Solve rapid-fire number patterns and logic sequences.",
        "限时解开数列规律与逻辑谜题。", 0xFFF59E0B
    ),
    VOCAB_CHALLENGE(
        "VOCAB_CHALLENGE", "Vocabulary Challenge ศัพท์สายฟ้า", "Vocabulary Blitz", "闪电词汇王",
        "ทายคำศัพท์ภาษาอังกฤษ ป.6 สอบเข้า ม.1 แบบต่อเนื่องทำคะแนนสูงสุด",
        "High-speed M.1 entrance English vocabulary streak battle.",
        "初一入学核心英语词汇极速连击挑战。", 0xFF10B981
    ),
    NUMBER_PUZZLE(
        "NUMBER_PUZZLE", "Number Puzzle สไลด์ตัวเลข 15", "Sliding Number Puzzle", "数字华容道",
        "เลื่อนแผ่นตัวเลขเรียงลำดับ 1–15 ให้ถูกต้องด้วยจำนวนครั้งน้อยที่สุด",
        "Slide tiles to order 1–15 in the fewest moves.",
        "滑动数字方块按顺序排列。", 0xFF06B6D4
    ),
    BRAIN_TRAINING(
        "BRAIN_TRAINING", "Brain Training สมองไวไฟแลบ", "Brain Reflex Trainer", "最强大脑反应力",
        "ทดสอบสมาธิ สี คำศัพท์ และการคำนวณสลับสมองซ้าย-ขวา",
        "Train focus, Stroop color-word reflex, and mental speed.",
        "训练专注力、颜色反应与心算速度。", 0xFFF43F5E
    );

    fun title(lang: AppLanguage) = tr(lang, titleTh, titleEn, titleZh)
    fun desc(lang: AppLanguage) = tr(lang, descTh, descEn, descZh)
    val color: Color get() = Color(accentHex)
}

data class BossSpec(
    val id: String,
    val nameTh: String,
    val nameEn: String,
    val nameZh: String,
    val titleTh: String,
    val titleEn: String,
    val titleZh: String,
    val emoji: String,
    val subject: SubjectId,
    val difficulty: DifficultyLevel,
    val maxHp: Int,
    val playerHearts: Int,
    val rewardXp: Int,
    val rewardPt: Int,
    val rewardFrameId: String?,
    val primaryHex: Long,
    val secondaryHex: Long
) {
    fun name(lang: AppLanguage) = tr(lang, nameTh, nameEn, nameZh)
    fun subtitle(lang: AppLanguage) = tr(lang, titleTh, titleEn, titleZh)
}

data class LuckyWheelSlice(
    val index: Int,
    val labelTh: String,
    val labelEn: String,
    val labelZh: String,
    val rewardType: String, // PT, XP, MYSTERY_BOX, TIME_POTION, REMOVE_WRONG, JACKPOT_PT
    val amount: Int,
    val weight: Int,
    val colorHex: Long
) {
    fun label(lang: AppLanguage) = tr(lang, labelTh, labelEn, labelZh)
}

data class MysteryChestSpec(
    val id: String,
    val nameTh: String,
    val nameEn: String,
    val nameZh: String,
    val descTh: String,
    val descEn: String,
    val descZh: String,
    val costPt: Int,
    val canUseKey: Boolean,
    val emoji: String,
    val primaryHex: Long,
    val secondaryHex: Long
) {
    fun name(lang: AppLanguage) = tr(lang, nameTh, nameEn, nameZh)
    fun desc(lang: AppLanguage) = tr(lang, descTh, descEn, descZh)
}

object GameCatalog {

    val avatarFrames: List<AvatarFrameSpec> = listOf(
        AvatarFrameSpec(
            "FRAME_DEFAULT",
            "กรอบฟ้าใสเริ่มต้น", "Pastel Sky Starter", "晴空初始头像框",
            "ปลดล็อกเริ่มต้นสำหรับทุกคน", "Unlocked by default for all players", "所有玩家默认解锁",
            "☁️", listOf(0xFF38BDF8, 0xFFF472B6)
        ),
        AvatarFrameSpec(
            "FRAME_RAINBOW_PRISM",
            "กรอบปริซึมสายรุ้ง", "Rainbow Prism Aura", "彩虹棱镜光环框",
            "ซื้อได้จากร้านแดง-ขาว PT Shop", "Available in Red-White PT Shop", "可在红白PT商店购买",
            "🌈", listOf(0xFFFF6B8B, 0xFFFFD166, 0xFF4ADE80, 0xFF38BDF8, 0xFFA855F7)
        ),
        AvatarFrameSpec(
            "FRAME_SAKURA_CROWN",
            "กรอบมงกุฎซากุระ", "Sakura Blossom Crown", "樱花花冠头像框",
            "ซื้อได้จากร้านแดง-ขาว PT Shop", "Available in Red-White PT Shop", "可在红白PT商店购买",
            "🌸", listOf(0xFFF472B6, 0xFFFBCFE8, 0xFFEC4899)
        ),
        AvatarFrameSpec(
            "FRAME_CYBER_NEON",
            "กรอบไซเบอร์นีออน", "Cyber Neon Pulse", "赛博霓虹脉冲框",
            "ซื้อได้จากร้านแดง-ขาว PT Shop", "Available in Red-White PT Shop", "可在红白PT商店购买",
            "⚡", listOf(0xFF00F5D4, 0xFF38BDF8, 0xFF6366F1)
        ),
        AvatarFrameSpec(
            "FRAME_GALAXY_STAR",
            "กรอบดาราจักรเนบิวลา", "Nebula Starlight", "星云璀璨头像框",
            "ซื้อได้จากร้านแดง-ขาว PT Shop", "Available in Red-White PT Shop", "可在红白PT商店购买",
            "🌌", listOf(0xFFA855F7, 0xFFEC4899, 0xFF38BDF8)
        ),
        AvatarFrameSpec(
            "FRAME_GOLDEN_SOVEREIGN",
            "กรอบราชันย์ทองคำ", "Golden Sovereign", "黄金王者头像框",
            "สุ่มรับจากกล่องสมบัติปริศนา หรือ เลเวล 10+", "Found in Mystery Chests or Level 10+", "神秘宝箱掉落或达到10级解锁",
            "👑", listOf(0xFFFBBF24, 0xFFFEF08A, 0xFFD97706)
        ),
        AvatarFrameSpec(
            "FRAME_SUANKULARB",
            "กรอบเกียรติยศชมพู-ฟ้า", "Rose-Sky Honor Frame", "玫瑰天蓝荣耀框",
            "ชนะบอสมังกรคณิตศาสตร์ หรือ กล่องสมบัติใหญ่", "Defeat Math Dragon Boss or Grand Chest", "击败数学巨龙BOSS或开启至尊宝箱",
            "🌹", listOf(0xFFEC4899, 0xFF38BDF8, 0xFFF9A8D4)
        ),
        AvatarFrameSpec(
            "FRAME_DEBSIRIN",
            "กรอบมรกตทองคำ", "Emerald-Gold Crest", "翡翠金徽头像框",
            "ชนะบอสสฟิงซ์วิทยาศาสตร์", "Defeat Science Sphinx Boss", "击败科学斯芬克斯BOSS解锁",
            "🍀", listOf(0xFF10B981, 0xFFFBBF24, 0xFF34D399)
        ),
        AvatarFrameSpec(
            "FRAME_BOSS_SLAYER",
            "กรอบผู้พิชิตบอส", "Crimson Boss Slayer", "讨伐魔王烈焰框",
            "ปราบบอสจอมเวทดาราจักรสำเร็จ", "Defeat Astral Logic Archmage Boss", "击败星界逻辑大法师BOSS解锁",
            "🔥", listOf(0xFFFF244C, 0xFFFF9F43, 0xFFFBBF24)
        ),
        AvatarFrameSpec(
            "FRAME_DIAMOND_SCHOLAR",
            "กรอบปราชญ์เพชร ม.1", "Diamond M.1 Scholar", "初一钻石学者框",
            "ลุ้นรับจากกล่องสมบัติ Grand Master", "Grand Master Mystery Chest exclusive", "至尊神秘宝箱限定掉落",
            "💎", listOf(0xFF67E8F9, 0xFFE0F2FE, 0xFF818CF8, 0xFFF472B6)
        )
    )

    val titleStyles: List<TitleStyleSpec> = listOf(
        TitleStyleSpec(0, "สายรุ้งพาสเทล", "Rainbow Pastel", "梦幻彩虹", 0xFF0EA5E9, 0xFFEC4899),
        TitleStyleSpec(1, "ทองคำจักรพรรดิ", "Royal Gold", "皇家曜金", 0xFFF59E0B, 0xFFEA580C),
        TitleStyleSpec(2, "มรกตไซเบอร์", "Emerald Mint", "翡翠薄荷", 0xFF10B981, 0xFF06B6D4),
        TitleStyleSpec(3, "ม่วงดาราจักร", "Cosmic Violet", "星河紫罗兰", 0xFF8B5CF6, 0xFFEC4899),
        TitleStyleSpec(4, "แดงเพลิงแชมเปียน", "Champion Crimson", "冠军赤焰", 0xFFFF244C, 0xFFF97316),
        TitleStyleSpec(5, "คริสตัลไอซ์บลู", "Crystal Ice", "冰晶湛蓝", 0xFF2563EB, 0xFF2EC4B6)
    )

    fun presetTitles(lang: AppLanguage): List<String> = when (lang) {
        AppLanguage.TH -> listOf(
            "ว่าที่เด็ก ม.1 ห้องพิเศษ",
            "เทพคณิตคิดไว",
            "นักล่า PT Points",
            "ปราชญ์สายรุ้งไร้พ่าย",
            "ผู้พิชิตบอสข้อสอบ",
            "เด็กเก่งเตรียมสอบเข้า ม.1"
        )
        AppLanguage.EN -> listOf(
            "Future Gifted M.1 Star",
            "Lightning Math Ace",
            "PT Points Hunter",
            "Undefeated Rainbow Scholar",
            "Exam Boss Slayer",
            "PTPreM1 Champion"
        )
        AppLanguage.ZH -> listOf(
            "初一重点班准新生",
            "速算数学达人",
            "PT积分猎手",
            "彩虹不败学霸",
            "考题魔王讨伐者",
            "名校冲刺冠军"
        )
    }

    val backgrounds: List<BackgroundThemeSpec> = listOf(
        BackgroundThemeSpec(
            "BG_DEFAULT",
            "ท้องฟ้าสายรุ้งพาสเทล PT's (เริ่มต้น)",
            "PT's Rainbow Pastel Sky (Default)",
            "PT彩虹粉蓝天空（默认）",
            null,
            0xFFEAF7FF, 0xFFFFEEF8
        ),
        BackgroundThemeSpec(
            "BG_SUANKULARB",
            "Suankularb Background (ชมพู-ฟ้า)",
            "Suankularb Rose-Sky Courtyard",
            "玫瑰天蓝学院背景",
            R.drawable.img_bg_rose_sky_1793378452900,
            0xFFFDF2F8, 0xFFE0F2FE
        ),
        BackgroundThemeSpec(
            "BG_DEBSIRIN",
            "Debsirin Background (เขียว-ทอง)",
            "Debsirin Emerald-Gold Clocktower",
            "翡翠金钟楼学院背景",
            R.drawable.img_bg_emerald_gold_1793378454540,
            0xFFECFDF5, 0xFFFEF3C7
        ),
        BackgroundThemeSpec(
            "BG_SUEKSANARI",
            "Sueksanari Background (เหลือง-ชมพูพาสเทล)",
            "Sueksanari Starlight Blossom",
            "星光樱花学院背景",
            R.drawable.img_bg_crimson_starlight_1793378455059,
            0xFFFFFBEB, 0xFFFCE7F3
        ),
        BackgroundThemeSpec(
            "BG_SW3",
            "SW3 (Satriwit 3) Background",
            "SW3 Starlight Horizon Campus",
            "SW3星际地平线学院背景",
            R.drawable.img_pet_zone_sky_1793378451345,
            0xFFEFF6FF, 0xFFF5F3FF
        )
    )

    // 18 Shop Items (10 Classic + 8 New Frames & Power-Ups)
    val shopItems: List<ShopItemCatalog> = listOf(
        ShopItemCatalog(
            id = "SHOP_PREMIUM_UI",
            nameTh = "UI ดี / Premium UI Theme",
            nameEn = "Premium Crystal UI Theme",
            nameZh = "高级水晶UI主题",
            descTh = "ปลดล็อกธีม UI พิเศษระดับพรีเมียมถาวร เปลี่ยนโทนการ์ดและแสงคริสตัลสุดหรูทั้งแอป",
            descEn = "Permanently unlocks the Royal Crystal UI Theme across the entire game.",
            descZh = "永久解锁全应用尊贵水晶UI视觉主题。",
            pricePt = 50,
            category = "THEME"
        ),
        ShopItemCatalog(
            id = "SHOP_BG_SUANKULARB",
            nameTh = "พื้นหลังชมพู-ฟ้า Suankularb",
            nameEn = "Suankularb Background",
            nameZh = "玫瑰天蓝学院背景",
            descTh = "ภาพพื้นหลังสไตล์เกมโทนชมพู-ฟ้าสุดละมุน เพิ่มเข้า Background Collection อัตโนมัติ",
            descEn = "Game-styled pink & sky-blue academy background added to your Background Collection.",
            descZh = "粉蓝学院风游戏背景，自动加入背景收藏。",
            pricePt = 75,
            category = "BACKGROUND",
            backgroundId = "BG_SUANKULARB"
        ),
        ShopItemCatalog(
            id = "SHOP_BG_DEBSIRIN",
            nameTh = "พื้นหลังเขียว-ทอง Debsirin",
            nameEn = "Debsirin Background",
            nameZh = "翡翠金学院背景",
            descTh = "ภาพพื้นหลังสไตล์เกมโทนเขียว-ทองหอนาฬิกา เพิ่มเข้า Background Collection อัตโนมัติ",
            descEn = "Game-styled emerald & gold clocktower background added to your collection.",
            descZh = "翡翠金钟楼学院游戏背景，自动加入背景收藏。",
            pricePt = 75,
            category = "BACKGROUND",
            backgroundId = "BG_DEBSIRIN"
        ),
        ShopItemCatalog(
            id = "SHOP_BG_SUEKSANARI",
            nameTh = "พื้นหลังพาสเทล Sueksanari",
            nameEn = "Sueksanari Background",
            nameZh = "星光樱花学院背景",
            descTh = "ภาพพื้นหลังสไตล์เกมโทนพาสเทลอบอุ่น เพิ่มเข้า Background Collection อัตโนมัติ",
            descEn = "Game-styled warm blossom academy background added to your collection.",
            descZh = "温馨花瓣学院风游戏背景，自动加入背景收藏。",
            pricePt = 75,
            category = "BACKGROUND",
            backgroundId = "BG_SUEKSANARI"
        ),
        ShopItemCatalog(
            id = "SHOP_BG_SW3",
            nameTh = "พื้นหลังดวงดาว SW3 (Satriwit 3)",
            nameEn = "SW3 (Satriwit 3) Background",
            nameZh = "SW3星辰学院背景",
            descTh = "ภาพพื้นหลังสไตล์เกมโทนปราสาทลอยฟ้าและดวงดาว เพิ่มเข้า Background Collection อัตโนมัติ",
            descEn = "Game-styled floating starlight academy background added to your collection.",
            descZh = "浮空星辰学院游戏背景，自动加入背景收藏。",
            pricePt = 75,
            category = "BACKGROUND",
            backgroundId = "BG_SW3"
        ),
        ShopItemCatalog(
            id = "SHOP_FRAME_RAINBOW",
            nameTh = "กรอบอวตารปริซึมสายรุ้ง 🌈",
            nameEn = "Rainbow Prism Avatar Frame 🌈",
            nameZh = "彩虹棱镜头像框 🌈",
            descTh = "ปลดล็อกกรอบอวตารสายรุ้งพาสเทลเรืองแสงถาวร สวมใส่โชว์ในหน้าแรกและโปรไฟล์!",
            descEn = "Permanently unlocks the glowing Rainbow Prism avatar frame.",
            descZh = "永久解锁炫彩彩虹棱镜头像框，可在主页与个人资料展示！",
            pricePt = 120,
            category = "AVATAR_FRAME",
            frameId = "FRAME_RAINBOW_PRISM"
        ),
        ShopItemCatalog(
            id = "SHOP_FRAME_SAKURA",
            nameTh = "กรอบอวตารมงกุฎซากุระ 🌸",
            nameEn = "Sakura Crown Avatar Frame 🌸",
            nameZh = "樱花花冠头像框 🌸",
            descTh = "ปลดล็อกกรอบอวตารกลีบซากุระสีชมพูหวานแหววถาวร",
            descEn = "Permanently unlocks the sweet pink Sakura Blossom avatar frame.",
            descZh = "永久解锁甜美粉色樱花花冠头像框。",
            pricePt = 110,
            category = "AVATAR_FRAME",
            frameId = "FRAME_SAKURA_CROWN"
        ),
        ShopItemCatalog(
            id = "SHOP_FRAME_CYBER",
            nameTh = "กรอบอวตารไซเบอร์นีออน ⚡",
            nameEn = "Cyber Neon Avatar Frame ⚡",
            nameZh = "赛博霓虹头像框 ⚡",
            descTh = "ปลดล็อกกรอบอวตารแสงนีออนฟ้า-มินต์สุดล้ำอนาคตถาวร",
            descEn = "Permanently unlocks the futuristic cyan-mint Cyber Neon avatar frame.",
            descZh = "永久解锁未来科技感青蓝赛博霓虹头像框。",
            pricePt = 140,
            category = "AVATAR_FRAME",
            frameId = "FRAME_CYBER_NEON"
        ),
        ShopItemCatalog(
            id = "SHOP_FRAME_GALAXY",
            nameTh = "กรอบอวตารดาราจักรเนบิวลา 🌌",
            nameEn = "Nebula Starlight Avatar Frame 🌌",
            nameZh = "星云璀璨头像框 🌌",
            descTh = "ปลดล็อกกรอบอวตารประกายดาวเนบิวลาสีม่วง-ชมพูสุดอลังการถาวร",
            descEn = "Permanently unlocks the cosmic violet-pink Nebula Starlight avatar frame.",
            descZh = "永久解锁紫粉宇宙星云璀璨头像框。",
            pricePt = 180,
            category = "AVATAR_FRAME",
            frameId = "FRAME_GALAXY_STAR"
        ),
        ShopItemCatalog(
            id = "SHOP_REMOVE_WRONG_2",
            nameTh = "ตัดคำตอบผิด 2 ตัวเลือก (×2 ครั้ง)",
            nameEn = "Remove 2 Wrong Choices (2 Uses)",
            nameZh = "排除2个错误选项（2次）",
            descTh = "ได้รับสิทธิ์ใช้งาน 2 ครั้ง ใช้ตัดตัวเลือกที่ผิดออก 2 ข้อระหว่างสอบปรนัย 4 ตัวเลือก",
            descEn = "Grants 2 uses. Eliminates 2 incorrect options on 4-choice questions.",
            descZh = "获得2次使用机会，在四选一题目中排除2个错误答案。",
            pricePt = 100,
            category = "CONSUMABLE"
        ),
        ShopItemCatalog(
            id = "SHOP_TIME_POTION",
            nameTh = "ขวดยาเพิ่มเวลาสอบ +30 วินาที (×2 ขวด)",
            nameEn = "Exam Time Potion +30s (2 Uses)",
            nameZh = "考试延时药水 +30秒（2瓶）",
            descTh = "กดใช้ระหว่างสอบเพื่อเพิ่มเวลาทันที +30 วินาทีต่อครั้ง ได้รับ 2 ขวด!",
            descEn = "Use mid-exam to instantly add +30 seconds to the countdown timer (2 uses).",
            descZh = "考试中点击立即增加30秒作答时间（共获得2瓶）！",
            pricePt = 90,
            category = "CONSUMABLE"
        ),
        ShopItemCatalog(
            id = "SHOP_STREAK_SHIELD",
            nameTh = "โล่พิทักษ์ไฟ Streak Shield 🛡️",
            nameEn = "Streak Flame Shield 🛡️ (+1 Shield)",
            nameZh = "连胜火焰护盾 🛡️（+1个）",
            descTh = "ปกป้องเปลวไฟ Streak อัตโนมัติแม้ขาดสอบเกิน 1 วัน (เก็บสะสมไว้ใช้ยามฉุกเฉินได้)",
            descEn = "Automatically protects your Streak Flame even if you miss multiple days!",
            descZh = "即使中断学习多天也能自动保护连胜火焰不归零！",
            pricePt = 150,
            category = "CONSUMABLE"
        ),
        ShopItemCatalog(
            id = "SHOP_LUCKY_TICKET",
            nameTh = "ตั๋วหมุนวงล้อนำโชค 🎡 (×2 ใบ)",
            nameEn = "Lucky Wheel Spin Ticket 🎡 (×2)",
            nameZh = "幸运转盘抽奖券 🎡（2张）",
            descTh = "รับตั๋วหมุนวงล้อนำโชคเพิ่ม 2 ใบ ลุ้นแจ็กพอต PT Points, XP และกล่องสมบัติ!",
            descEn = "Receive 2 extra spins on the Daily Lucky Wheel for jackpots & chests!",
            descZh = "获得2张幸运大转盘抽奖券，赢取积分大奖与宝箱！",
            pricePt = 85,
            category = "CONSUMABLE"
        ),
        ShopItemCatalog(
            id = "SHOP_MYSTERY_BOX_KEY",
            nameTh = "กุญแจคริสตัลเปิดกล่องสมบัติ 🗝️",
            nameEn = "Crystal Mystery Chest Key 🗝️",
            nameZh = "神秘宝箱水晶钥匙 🗝️",
            descTh = "เพิ่มกล่องสมบัติปริศนา 1 กล่อง พร้อมเปิดรับไอเทม กรอบอวตาร หรือโบนัสก้อนโตทันที!",
            descEn = "Grants +1 Mystery Treasure Chest ready to unbox rare frames, items & PT!",
            descZh = "立即获得1个神秘宝箱，可开出稀有头像框、道具或丰厚积分！",
            pricePt = 160,
            category = "CONSUMABLE"
        ),
        ShopItemCatalog(
            id = "SHOP_EASY_PLUS_20",
            nameTh = "เพิ่มข้อสอบระดับง่าย +20 ข้อ",
            nameEn = "Easy Questions +20 Pack",
            nameZh = "解锁20道简单练习题",
            descTh = "ปลดล็อกโหมดฝึกชุดใหญ่ เพิ่มจำนวนข้อสอบระดับง่ายให้ฝึกต่อเนื่องอีก +20 ข้อในโหมดฝึก!",
            descEn = "Unlocks +20 extra Easy practice questions per session in Practice Mode!",
            descZh = "在练习模式中额外解锁20道简单题训练包！",
            pricePt = 400,
            category = "PERMANENT"
        ),
        ShopItemCatalog(
            id = "SHOP_MULT_1_5",
            nameTh = "ตัวคูณ PT Points ×1.5 (1 ชม.)",
            nameEn = "PT Points Multiplier ×1.5 (1 Hr)",
            nameZh = "PT积分倍率 ×1.5（1小时）",
            descTh = "ใช้งานได้ 1 ชั่วโมง! คะแนนจากคำตอบถูก ×1.5 คำเตือน: ค่าหักจากคำตอบผิด ×3!",
            descEn = "Active for 1 hour! Correct PT rewards ×1.5. Warning: Wrong streak deductions ×3!",
            descZh = "持续1小时！答对获得积分×1.5。警告：答错扣分×3！",
            pricePt = 200,
            category = "MULTIPLIER",
            multiplierGain = 1.5,
            multiplierPenalty = 3,
            durationHours = 1
        ),
        ShopItemCatalog(
            id = "SHOP_MULT_4_5",
            nameTh = "ตัวคูณ PT Points ×4.5 (3 ชม.)",
            nameEn = "PT Points Multiplier ×4.5 (3 Hrs)",
            nameZh = "PT积分倍率 ×4.5（3小时）",
            descTh = "ใช้งานได้ 3 ชั่วโมง! คะแนนจากคำตอบถูก ×4.5 คำเตือนสำคัญ: ค่าหักจากคำตอบผิด ×10!",
            descEn = "Active for 3 hours! Correct PT rewards ×4.5. Warning: Wrong streak deductions ×10!",
            descZh = "持续3小时！答对获得积分×4.5。重要警告：答错扣分×10！",
            pricePt = 1000,
            category = "MULTIPLIER",
            multiplierGain = 4.5,
            multiplierPenalty = 10,
            durationHours = 3
        ),
        ShopItemCatalog(
            id = "SHOP_MULT_10",
            nameTh = "ตัวคูณ PT Points ×10 (10 ชม.)",
            nameEn = "PT Points Multiplier ×10 (10 Hrs)",
            nameZh = "PT积分倍率 ×10（10小时）",
            descTh = "ใช้งานได้ 10 ชั่วโมง! คะแนนจากคำตอบถูก ×10 คำเตือนสูงสุด: ค่าหักจากคำตอบผิด ×30!",
            descEn = "Active for 10 hours! Correct PT rewards ×10. Extreme Warning: Wrong deductions ×30!",
            descZh = "持续10小时！答对获得积分×10。最高警告：答错扣分×30！",
            pricePt = 2000,
            category = "MULTIPLIER",
            multiplierGain = 10.0,
            multiplierPenalty = 30,
            durationHours = 10
        )
    )

    // 28 Unique Pet Species (Common 9, Uncommon 7, Rare 4, Epic 3, Legendary 2, Satriwit 2, Suankularb 1)
    val pets: List<PetSpecies> = listOf(
        // COMMON (9)
        PetSpecies(
            "pet_01", 1, "เมฆน้อย", "Cloudy", "小云朵", EggRarity.COMMON,
            0xFFBAE6FD, 0xFF0284C7, 120, PetSkillType.TIME_BONUS_3,
            "ก้อนเมฆปุยฝ้ายยิ้มหวานจากเกาะลอยฟ้า ช่วยให้ใจเย็นเวลาทำข้อสอบ",
            "A fluffy smiling sky cloud that keeps you calm under pressure.",
            "来自浮空岛的微笑棉花云，让你考试时保持冷静。",
            "เพิ่มเวลาทำข้อสอบ +3%", "Exam Time +3%", "考试时间 +3%"
        ),
        PetSpecies(
            "pet_02", 2, "กระต่ายพีช", "Peach Bunny", "蜜桃兔", EggRarity.COMMON,
            0xFFFBCFE8, 0xFFEC4899, 135, PetSkillType.REMOVE_1_WRONG_ONCE,
            "กระต่ายหูยาวสีพีชแสนว่องไว ชอบกระโดดชี้ตัวเลือกที่หลอกตา",
            "Quick-eared peach bunny that sniffs out tricky wrong answers.",
            "敏捷的长耳蜜桃兔，擅长识破干扰选项。",
            "ตัดตัวเลือกผิดออก 1 ตัวเลือกได้ 1 ครั้งต่อชุดสอบ",
            "Remove 1 wrong choice (1x per exam)",
            "每套试卷可排除1个错误选项（限1次）"
        ),
        PetSpecies(
            "pet_03", 3, "นกฮูกพิกเซล", "Pixel Owl", "像素猫头鹰", EggRarity.COMMON,
            0xFF38BDF8, 0xFF6366F1, 140, PetSkillType.HINT_1_ONCE,
            "นกฮูกไซเบอร์ติดจอโฮโลแกรมสแกนแนวคิดสำคัญของโจทย์",
            "Cyber owl projecting holographic study clues.",
            "搭载全息眼镜的赛博猫头鹰，可扫描解题线索。",
            "ขอคำใบ้ได้ 1 ครั้งต่อชุดสอบ",
            "Unlock 1 Hint per exam set",
            "每套试卷可获取1次提示"
        ),
        PetSpecies(
            "pet_04", 4, "แมวลาเต้", "Latte Cat", "拿铁猫", EggRarity.COMMON,
            0xFFD6BCFA, 0xFFB45309, 125, PetSkillType.XP_BONUS_2,
            "เหมียวสีน้ำตาลฟองนุ่ม ส่งเสียงเพอร์เติมพลังการเรียนรู้",
            "Cozy milk-tea kitten whose purrs boost learning focus.",
            "奶咖色温柔小猫，呼噜声能提升学习经验。",
            "เพิ่ม XP ที่ได้รับ +2%",
            "XP Earned +2%",
            "获得XP +2%"
        ),
        PetSpecies(
            "pet_05", 5, "ปลาวาฬฟอง", "Bubble Whale", "泡泡鲸", EggRarity.COMMON,
            0xFF67E8F9, 0xFF0891B2, 130, PetSkillType.PT_GAIN_BONUS_2,
            "วาฬจิ๋วลอยตัวท่ามกลางฟองอากาศสีรุ้งที่ซ่อนเหรียญฟ้าไว้ข้างใน",
            "Tiny sky-whale blowing sparkling PT coin bubbles.",
            "漂浮在彩虹气泡中的迷你小鲸鱼。",
            "เพิ่ม PT Points จากการตอบถูก +2%",
            "PT Points from correct answers +2%",
            "答对获得的PT积分 +2%"
        ),
        PetSpecies(
            "pet_06", 6, "เต่ามินต์", "Mint Turtle", "薄荷龟", EggRarity.COMMON,
            0xFF6EE7B7, 0xFF059669, 135, PetSkillType.FREEZE_TIME_5S,
            "เต่ากระดองคริสตัลสีเขียวมิ้นต์ สามารถชะลอกระแสเวลาได้ชั่วขณะ",
            "Crystal-shelled mint turtle able to pause the clock briefly.",
            "薄荷水晶壳小乌龟，能短暂冻结时间流逝。",
            "หยุดเวลาข้อสอบได้ 5 วินาที จำนวน 1 ครั้ง",
            "Freeze exam timer for 5s (1x per exam)",
            "暂停考试计时5秒（限1次）"
        ),
        PetSpecies(
            "pet_07", 7, "จิ้งจอกคาราเมล", "Caramel Fox", "焦糖狐", EggRarity.COMMON,
            0xFFFDBA74, 0xFFEA580C, 145, PetSkillType.CHANGE_ANSWER_ONCE,
            "จิ้งจอกหางฟูสีส้มคาราเมล ช่างสังเกตและช่วยทบทวนการตัดสินใจ",
            "Clever caramel-tailed fox that spots hasty mistakes.",
            "蓬松尾巴的聪明焦糖狐，帮你修正粗心选择。",
            "เปลี่ยนคำตอบได้ 1 ครั้งก่อนส่งคำตอบของชุดสอบ",
            "Change locked answer 1x before final submit",
            "交卷前可修改已锁定答案1次"
        ),
        PetSpecies(
            "pet_21", 21, "แฮมสเตอร์สายรุ้ง", "Rainbow Hammy", "彩虹仓鼠", EggRarity.COMMON,
            0xFFFFD166, 0xFFFF6B8B, 148, PetSkillType.XP_BONUS_2,
            "แฮมสเตอร์แก้มป่องถือเมล็ดทานตะวันสีรุ้ง เติมความสดชื่นทุกข้อ",
            "Chubby rainbow hamster cheering on every correct answer.",
            "鼓着腮帮子的彩虹小仓鼠，为每一次作答加油打气。",
            "เพิ่ม XP ที่ได้รับ +2%",
            "XP Earned +2%",
            "获得XP +2%"
        ),
        PetSpecies(
            "pet_22", 22, "ลูกเจี๊ยบซันนี่", "Sunny Chick", "阳光小鸡", EggRarity.COMMON,
            0xFFFEF08A, 0xFFF59E0B, 150, PetSkillType.HINT_1_ONCE,
            "ลูกเจี๊ยบสีเหลืองทองสวมหมวกบัณฑิตจิ๋ว คอยจิกชี้คำสำคัญในโจทย์",
            "Scholarly golden chick pecking right at key exam clues.",
            "戴着迷你学士帽的金色小鸡，帮你指出题干关键词。",
            "ขอคำใบ้ได้ 1 ครั้งต่อชุดสอบ",
            "Unlock 1 Hint per exam set",
            "每套试卷可获取1次提示"
        ),
        // UNCOMMON (7)
        PetSpecies(
            "pet_08", 8, "เพนกวินน้ำแข็ง", "Ice Penguin", "冰晶企鹅", EggRarity.UNCOMMON,
            0xFF7DD3FC, 0xFF0369A1, 220, PetSkillType.TIME_BONUS_6,
            "เพนกวินสวมผ้าพันคอเกล็ดหิมะ แผ่ไอเย็นขยายเวลาคิดคำนวณ",
            "Frost-scarf penguin radiating cool focus to extend exam time.",
            "戴着雪花围巾的冰晶企鹅，为你延长思考时间。",
            "เพิ่มเวลาทำข้อสอบ +6%",
            "Exam Time +6%",
            "考试时间 +6%"
        ),
        PetSpecies(
            "pet_09", 9, "แพนด้ามินต์", "Mint Panda", "薄荷熊猫", EggRarity.UNCOMMON,
            0xFFA7F3D0, 0xFF065F46, 240, PetSkillType.REMOVE_2_WRONG_ONCE,
            "แพนด้าตัวกลมถือใบไผ่เรืองแสง ปัดตัวเลือกหลอกกระเด็นทีเดียวสองข้อ!",
            "Bamboo-wielding mint panda sweeping away two wrong choices at once.",
            "手持发光竹叶的薄荷熊猫，一次扫除两个错误选项！",
            "ตัดตัวเลือกผิดออก 2 ตัวเลือกได้ 1 ครั้งต่อชุดสอบ",
            "Remove 2 wrong choices (1x per exam)",
            "每套试卷可排除2个错误选项（限1次）"
        ),
        PetSpecies(
            "pet_10", 10, "กวางแสงดาว", "Starlight Deer", "星光小鹿", EggRarity.UNCOMMON,
            0xFFE9D5FF, 0xFF7E22CE, 250, PetSkillType.HINT_2_TWICE,
            "กวางน้อยเขาดาวประกาย ส่องทางสว่างให้เห็นวิธีคิดสองระดับ",
            "Glowing-antler fawn illuminating two-tier solving hints.",
            "鹿角闪烁星光的小鹿，照亮双层解题思路。",
            "เปิดคำใบ้สองระดับได้ 2 ครั้งต่อชุดสอบ",
            "Unlock 2-level deep hints (2x per exam)",
            "每套试卷可开启2次双层提示"
        ),
        PetSpecies(
            "pet_11", 11, "มังกรพีช", "Peach Dragon", "蜜桃幼龙", EggRarity.UNCOMMON,
            0xFFF9A8D4, 0xFFDB2777, 265, PetSkillType.COMBO_SHIELD_ONCE,
            "มังกรจิ๋วปีกชมพูพ่นเกราะหัวใจ ปกป้องสถิติตอบถูกต่อเนื่องไม่ให้ขาด!",
            "Tiny pink-winged dragon breathing a heart shield over your combo.",
            "粉色翅膀的小幼龙，张开爱心护盾保护连击不断！",
            "ป้องกัน Combo แตกจากการตอบผิดได้ 1 ครั้ง (ไม่คืนคะแนนที่ถูกหัก)",
            "Shields Combo from breaking once (does not refund PT deduction)",
            "防止答错中断连击1次（不退还已扣积分）"
        ),
        PetSpecies(
            "pet_12", 12, "กระรอกดาวตก", "Meteor Squirrel", "流星松鼠", EggRarity.UNCOMMON,
            0xFFFDE047, 0xFFCA8A04, 235, PetSkillType.XP_BONUS_5,
            "กระรอกหางประกายดาวตก วิ่งเก็บละอองประสบการณ์ให้พุ่งไว",
            "Starry-tailed squirrel gathering cosmic XP stardust.",
            "尾巴闪烁流星光芒的小松鼠，加速收集经验星尘。",
            "เพิ่ม XP ที่ได้รับ +5%",
            "XP Earned +5%",
            "获得XP +5%"
        ),
        PetSpecies(
            "pet_23", 23, "โคอาล่าเมฆฝัน", "Dreamy Koala", "云梦考拉", EggRarity.UNCOMMON,
            0xFFC7D2FE, 0xFF4F46E5, 255, PetSkillType.TIME_BONUS_6,
            "โคอาล่าสีพาสเทลกอดหมอนเมฆ ช่วยสร้างสมาธินิ่งสงบตลอดการสอบ",
            "Pastel koala hugging a cloud pillow that deepens concentration.",
            "抱着云朵抱枕的粉紫考拉，让你全程保持深度专注。",
            "เพิ่มเวลาทำข้อสอบ +6%",
            "Exam Time +6%",
            "考试时间 +6%"
        ),
        PetSpecies(
            "pet_24", 24, "คอร์กี้สายฟ้า", "Spark Corgi", "闪电柯基", EggRarity.UNCOMMON,
            0xFFFED7AA, 0xFFF97316, 260, PetSkillType.REMOVE_2_WRONG_ONCE,
            "น้องหมาคอร์กี้ขาสั้นพลังสปาร์ค เห่าไล่ตัวเลือกผิดกระเจิง 2 ข้อ!",
            "Energetic spark corgi barking away two decoy answers!",
            "活力满满的闪电小柯基，汪汪叫着赶走2个错误选项！",
            "ตัดตัวเลือกผิดออก 2 ตัวเลือกได้ 1 ครั้งต่อชุดสอบ",
            "Remove 2 wrong choices (1x per exam)",
            "每套试卷可排除2个错误选项（限1次）"
        ),
        // RARE (4)
        PetSpecies(
            "pet_13", 13, "ฟีนิกซ์คริสตัล", "Crystal Phoenix", "水晶凤凰", EggRarity.RARE,
            0xFF60A5FA, 0xFFF43F5E, 390, PetSkillType.RETRY_WRONG_NO_REWARD,
            "วิหคผลึกแก้วแห่งการเริ่มต้นใหม่ มอบโอกาสแก้ตัวเมื่อพลาดพลั้ง",
            "Crystalline bird of rebirth granting a second chance to learn.",
            "象征重生的水晶凤凰，在答错反馈后给予一次订正机会。",
            "แก้ตัวเลือกของข้อที่ผิดหลังได้รับ Feedback ได้ 1 ครั้ง (ไม่รับรางวัลซ้ำ)",
            "Retry 1 wrong question after feedback (no duplicate reward)",
            "获得反馈后可重答1道错题（不重复发放奖励）"
        ),
        PetSpecies(
            "pet_14", 14, "วาฬกาแล็กซี", "Galaxy Whale", "星河巨鲸", EggRarity.RARE,
            0xFF818CF8, 0xFF312E81, 380, PetSkillType.TIME_BONUS_10,
            "วาฬยักษ์ลายเนบิวลาแหวกว่ายเหนือกาลเวลา ขยายเวลาสอบอย่างเต็มอิ่ม",
            "Nebula-patterned sky leviathan warping space-time for calm thinking.",
            "身披星云纹路的太空巨鲸，大幅延展考试时间。",
            "เพิ่มเวลาทำข้อสอบ +10%",
            "Exam Time +10%",
            "考试时间 +10%"
        ),
        PetSpecies(
            "pet_15", 15, "แรคคูนโฮโล", "Holo Raccoon", "全息浣熊", EggRarity.RARE,
            0xFF2DD4BF, 0xFF4F46E5, 375, PetSkillType.MARK_REVIEW_3,
            "แรคคูนสวมแว่นโฮโลแกรม ช่วยปักหมุดและสรุปข้อที่ต้องทบทวนหลังสอบ",
            "Hologram-visor raccoon organizing up to 3 priority review cards.",
            "佩戴全息护目镜的小浣熊，帮你标记并汇总最多3道重点复盘题。",
            "ช่วยทำเครื่องหมายข้อทบทวนพิเศษสูงสุด 3 ข้อและรวบรวมสรุปหลังสอบ",
            "Smart-pin up to 3 review questions with post-exam summary",
            "智能标记最多3道复盘题并在考后集中解析"
        ),
        PetSpecies(
            "pet_25", 25, "แอกโซลอเติลปริซึม", "Prism Axolotl", "棱镜美西螈", EggRarity.RARE,
            0xFFF9A8D4, 0xFF06B6D4, 410, PetSkillType.HINT_2_TWICE,
            "ซาลาแมนเดอร์น้ำสีชมพู-ฟ้าคริสตัล ส่องประกายคำใบ้สองขั้นสุดชัดเจน",
            "Iridescent pink-cyan axolotl projecting dual crystal hints.",
            "闪耀粉蓝棱镜光芒的水晶美西螈，释放双层解题灵感。",
            "เปิดคำใบ้สองระดับได้ 2 ครั้งต่อชุดสอบ",
            "Unlock 2-level deep hints (2x per exam)",
            "每套试卷可开启2次双层提示"
        ),
        // EPIC (3)
        PetSpecies(
            "pet_16", 16, "มังกรสายฟ้า", "Thunder Dragon", "雷霆飞龙", EggRarity.EPIC,
            0xFF38BDF8, 0xFFFACC15, 560, PetSkillType.PT_GAIN_BONUS_10,
            "มังกรประจุสายฟ้าสีคราม-ทอง ชาร์จพลังเหรียญ PT Points ทุกครั้งที่ตอบถูก!",
            "Azure-gold storm dragon supercharging every PT Point reward!",
            "蓝金雷电飞龙，为每次正确作答充能更多PT积分！",
            "เพิ่ม PT Points จากการตอบถูก +10%",
            "PT Points from correct answers +10%",
            "答对获得的PT积分 +10%"
        ),
        PetSpecies(
            "pet_17", 17, "ยูนิคอร์นเนบิวลา", "Nebula Unicorn", "星云独角兽", EggRarity.EPIC,
            0xFFE879F9, 0xFF6366F1, 580, PetSkillType.HINT_RETRY_WRONG_ONCE,
            "ยูนิคอร์นเขาคริสตัลจักรวาล ส่องคำใบ้พร้อมให้โอกาสแก้ไขคำตอบที่ผิด",
            "Cosmic horn unicorn revealing guidance and allowing one correction.",
            "宇宙水晶独角兽，降下星云提示并允许修正1道错题。",
            "แก้ไขข้อที่ตอบผิดได้ 1 ข้อหลังได้รับคำใบ้ (จำกัด 1 ครั้งต่อชุดสอบ)",
            "Receive a hint and retry 1 wrong question once per exam",
            "答错后获得提示并重答1次（每套试卷限1次）"
        ),
        PetSpecies(
            "pet_26", 26, "จิ้งจอกเก้าหางซากุระ", "Sakura Kitsune", "樱华九尾狐", EggRarity.EPIC,
            0xFFFBCFE8, 0xFFE11D48, 600, PetSkillType.PT_GAIN_BONUS_10,
            "จิ้งจอกวิญญาณกลีบซากุระเก้าหาง เรียกพายุเหรียญ PT Points เพิ่มขึ้น!",
            "Nine-tailed sakura spirit fox summoning +10% bonus PT Points!",
            "九尾樱华灵狐，舞动花瓣为你额外提升10%积分收益！",
            "เพิ่ม PT Points จากการตอบถูก +10%",
            "PT Points from correct answers +10%",
            "答对获得的PT积分 +10%"
        ),
        // LEGENDARY (2)
        PetSpecies(
            "pet_18", 18, "สิงโตสุริยะ", "Solar Lion", "太阳神狮", EggRarity.LEGENDARY,
            0xFFFBBF24, 0xFFEA580C, 820, PetSkillType.WRONG_STREAK_SHIELD,
            "ราชสีห์แผงคอเปลวสุริยะสีทอง กางโล่แสงอาทิตย์ป้องกันการหักแต้มจากสถิติผิดสะสม!",
            "Golden-maned solar guardian shielding your PT Points from a Wrong Streak penalty!",
            "金色日冕鬃毛的太阳神狮，抵挡一次连错里程碑扣分！",
            "เกราะป้องกันการหักคะแนนจาก Wrong Streak Milestone 1 ครั้งต่อชุดสอบ",
            "Blocks 1 Wrong Streak Milestone PT deduction per exam",
            "每套试卷免疫1次连错里程碑PT扣分"
        ),
        PetSpecies(
            "pet_27", 27, "กริฟฟินมรกตทอง", "Debsirin Emerald Griffin", "翡翠金狮鹫", EggRarity.LEGENDARY,
            0xFF34D399, 0xFFFBBF24, 860, PetSkillType.WRONG_STREAK_SHIELD,
            "กริฟฟินปีกทองมรกตผู้พิทักษ์หอนาฬิกา ป้องกันการถูกหักแต้มผิดต่อเนื่องอย่างสง่างาม",
            "Majestic emerald-gold clocktower griffin blocking one Wrong Streak penalty!",
            "守护钟楼的翡翠金翼狮鹫，优雅抵御1次连错扣分惩罚！",
            "เกราะป้องกันการหักคะแนนจาก Wrong Streak Milestone 1 ครั้งต่อชุดสอบ",
            "Blocks 1 Wrong Streak Milestone PT deduction per exam",
            "每套试卷免疫1次连错里程碑PT扣分"
        ),
        // SATRIWIT (2)
        PetSpecies(
            "pet_19", 19, "เสือดาวดาวตก", "Satriwit Meteor Leopard", "星陨猎豹", EggRarity.SATRIWIT,
            0xFFFF244C, 0xFFFDE047, 940, PetSkillType.XP_15_AND_SKIP_RETURN,
            "เสือดาวแห่งจักรวาลสีแดงดาวตก พุ่งทะยานเก็บ XP มหาศาลและกระโดดข้ามโจทย์ยากไว้ทำทีหลังอย่างอิสระ",
            "Cosmic meteor leopard boosting XP +15% with tactical Skip & Return agility.",
            "宇宙红金星陨猎豹，大幅提升15%经验并可战术性跳过难题稍后作答。",
            "เพิ่ม XP +15% และข้ามข้อชั่วคราวแล้วย้อนกลับมาทำได้โดยไม่เสียสิทธิ์",
            "XP +15% & Tactical Skip-and-Return without penalty",
            "XP +15%，且可暂时跳过题目稍后返回作答"
        ),
        PetSpecies(
            "pet_28", 28, "เพกาซัสสายรุ้งสวรรค์", "Celestial Rainbow Pegasus", "天界彩虹飞马", EggRarity.SATRIWIT,
            0xFFA5F3FC, 0xFFF472B6, 965, PetSkillType.XP_15_AND_SKIP_RETURN,
            "อาชาสวรรค์ปีกสายรุ้งแห่ง PTPreM1.online พาโบยบินเก็บ XP +15% พร้อมสกิลข้ามข้ออัจฉริยะ",
            "Winged rainbow pegasus of PTPreM1.online granting +15% XP & Tactical Skip!",
            "展翅翱翔的天界彩虹飞马，提供15%经验加成与战术跳题特权！",
            "เพิ่ม XP +15% และข้ามข้อชั่วคราวแล้วย้อนกลับมาทำได้โดยไม่เสียสิทธิ์",
            "XP +15% & Tactical Skip-and-Return without penalty",
            "XP +15%，且可暂时跳过题目稍后返回作答"
        ),
        // SUANKULARB (1)
        PetSpecies(
            "pet_20", 20, "กวางกุหลาบเงิน", "Suankularb Silver Deer", "银玫圣鹿", EggRarity.SUANKULARB,
            0xFFF472B6, 0xFF38BDF8, 999, PetSkillType.TIME_10_REMOVE_1_TWICE,
            "กวางศักดิ์สิทธิ์สีเงินประกายชมพูกุหลาบและฟ้าคราม ตำนานสูงสุดแห่งดินแดน PT's",
            "Mythic silver-rose & sky-blue guardian deer — pinnacle companion of PT's.",
            "闪耀银色与玫瑰天蓝光芒的传说圣鹿，PT大陆至高伙伴。",
            "เพิ่มเวลาทำข้อสอบ +10% และตัดตัวเลือกผิดออก 1 ตัวเลือกได้ใน 2 ข้อ",
            "Exam Time +10% & Remove 1 wrong choice across 2 questions",
            "考试时间 +10%，并可在2道题中各排除1个错误选项"
        )
    )

    val bosses: List<BossSpec> = listOf(
        BossSpec(
            id = "BOSS_MATH_DRAGON",
            nameTh = "มังกรเรขาคณิตอัจฉริยะ",
            nameEn = "Prism Geometry Dragon",
            nameZh = "棱镜几何巨龙",
            titleTh = "ผู้พิทักษ์ด่านคณิตศาสตร์ ม.1",
            titleEn = "Guardian of M.1 Mathematics",
            titleZh = "初一数学圣殿守卫",
            emoji = "🐉",
            subject = SubjectId.MATH,
            difficulty = DifficultyLevel.HARD,
            maxHp = 500,
            playerHearts = 3,
            rewardXp = 280,
            rewardPt = 95,
            rewardFrameId = "FRAME_SUANKULARB",
            primaryHex = 0xFF0EA5E9,
            secondaryHex = 0xFFEC4899
        ),
        BossSpec(
            id = "BOSS_SCI_SPHINX",
            nameTh = "สฟิงซ์ควอนตัมชีวภาพ",
            nameEn = "Bio-Quantum Sphinx",
            nameZh = "量子生命斯芬克斯",
            titleTh = "จ้าวแห่งปริศนาวิทยาศาสตร์",
            titleEn = "Master of Scientific Riddles",
            titleZh = "科学谜题领主",
            emoji = "🦁",
            subject = SubjectId.SCIENCE,
            difficulty = DifficultyLevel.HARD,
            maxHp = 550,
            playerHearts = 3,
            rewardXp = 310,
            rewardPt = 110,
            rewardFrameId = "FRAME_DEBSIRIN",
            primaryHex = 0xFF10B981,
            secondaryHex = 0xFFFBBF24
        ),
        BossSpec(
            id = "BOSS_THAI_GARUDA",
            nameTh = "พญาครุฑอักษรวรรณศิลป์",
            nameEn = "Royal Verse Garuda",
            nameZh = "皇家诗韵金翅鸟",
            titleTh = "จอมปราชญ์ภาษาไทยและราชาศัพท์",
            titleEn = "Sovereign of Thai Literature",
            titleZh = "泰语文学与皇室用语宗师",
            emoji = "🦅",
            subject = SubjectId.THAI,
            difficulty = DifficultyLevel.HARD,
            maxHp = 500,
            playerHearts = 3,
            rewardXp = 290,
            rewardPt = 100,
            rewardFrameId = null,
            primaryHex = 0xFFEC4899,
            secondaryHex = 0xFFF59E0B
        ),
        BossSpec(
            id = "BOSS_ENG_KNIGHT",
            nameTh = "อัศวินไซเบอร์อ็อกซ์ฟอร์ด",
            nameEn = "Cyber Oxford Knight",
            nameZh = "赛博牛津骑士",
            titleTh = "ผู้พิชิต Grammar & Reading",
            titleEn = "Commander of Grammar & Reading",
            titleZh = "英语语法与阅读统帅",
            emoji = "⚔️",
            subject = SubjectId.ENGLISH,
            difficulty = DifficultyLevel.EXPERT,
            maxHp = 600,
            playerHearts = 3,
            rewardXp = 360,
            rewardPt = 135,
            rewardFrameId = null,
            primaryHex = 0xFF6366F1,
            secondaryHex = 0xFF06B6D4
        ),
        BossSpec(
            id = "BOSS_LOGIC_ARCHMAGE",
            nameTh = "จอมเวทดาราจักรตรรกะสูงสุด",
            nameEn = "Astral Logic Archmage",
            nameZh = "星界逻辑大法师",
            titleTh = "บอสใหญ่ห้องเรียนพิเศษ Gifted ม.1",
            titleEn = "Final Boss of Gifted M.1 Entrance",
            titleZh = "初一重点天才班终极魔王",
            emoji = "🧙‍♂️",
            subject = SubjectId.IQ_LOGIC,
            difficulty = DifficultyLevel.ENTRANCE_EXAM,
            maxHp = 750,
            playerHearts = 3,
            rewardXp = 500,
            rewardPt = 220,
            rewardFrameId = "FRAME_BOSS_SLAYER",
            primaryHex = 0xFFA855F7,
            secondaryHex = 0xFFFF244C
        )
    )

    val luckyWheelSlices: List<LuckyWheelSlice> = listOf(
        LuckyWheelSlice(0, "+25 PT", "+25 PT", "+25 PT", "PT", 25, 24, 0xFF38BDF8),
        LuckyWheelSlice(1, "+60 XP", "+60 XP", "+60 XP", "XP", 60, 22, 0xFFF472B6),
        LuckyWheelSlice(2, "+50 PT", "+50 PT", "+50 PT", "PT", 50, 16, 0xFF4ADE80),
        LuckyWheelSlice(3, "ยาเวลา +1", "Time Potion +1", "延时药水 +1", "TIME_POTION", 1, 12, 0xFFFFD166),
        LuckyWheelSlice(4, "+120 XP", "+120 XP", "+120 XP", "XP", 120, 10, 0xFFA855F7),
        LuckyWheelSlice(5, "ตัดช้อยส์ +1", "Remove Wrong +1", "排错道具 +1", "REMOVE_WRONG", 1, 8, 0xFF2EC4B6),
        LuckyWheelSlice(6, "กล่องสมบัติ 🎁", "Mystery Chest 🎁", "神秘宝箱 🎁", "MYSTERY_BOX", 1, 5, 0xFFFF9F43),
        LuckyWheelSlice(7, "แจ็กพอต +200 PT!", "Jackpot +200 PT!", "头奖 +200 PT!", "JACKPOT_PT", 200, 3, 0xFFFF244C)
    )

    val mysteryChests: List<MysteryChestSpec> = listOf(
        MysteryChestSpec(
            "CHEST_BRONZE",
            "หีบทองแดงนักสำรวจ", "Explorer Bronze Chest", "探险家青铜宝箱",
            "สุ่มรับ 30–90 PT Points, 80 XP หรือไอเทมช่วยสอบ",
            "Contains 30–90 PT Points, 80 XP, or exam power-ups.",
            "随机开出 30–90 PT积分、80 XP 或考试辅助道具。",
            costPt = 80,
            canUseKey = true,
            emoji = "🧰",
            primaryHex = 0xFFF59E0B,
            secondaryHex = 0xFFFDE68A
        ),
        MysteryChestSpec(
            "CHEST_RAINBOW",
            "หีบคริสตัลสายรุ้ง", "Rainbow Crystal Chest", "彩虹水晶宝箱",
            "การันตีของดี! ลุ้น 120–300 PT, โล่ Streak หรือกรอบราชันย์ทองคำ!",
            "High-tier loot! 120–300 PT, Streak Shield, or Golden Sovereign Frame!",
            "高品质掉落！可开出 120–300 PT、连胜护盾或黄金王者头像框！",
            costPt = 200,
            canUseKey = true,
            emoji = "🎁",
            primaryHex = 0xFFEC4899,
            secondaryHex = 0xFF38BDF8
        ),
        MysteryChestSpec(
            "CHEST_GRAND_MASTER",
            "หีบปราชญ์เพชร ม.1", "Grand Master Diamond Chest", "初一至尊钻石宝箱",
            "รางวัลระดับสูงสุด! ลุ้นกรอบปราชญ์เพชร 💎, 250–600 PT หรือสัตว์เลี้ยงหายาก!",
            "Supreme loot! Diamond Scholar Frame 💎, 250–600 PT, or Rare+ Pet!",
            "至尊大奖！有机会开出钻石学者头像框 💎、250–600 PT 或稀有神宠！",
            costPt = 420,
            canUseKey = false,
            emoji = "👑",
            primaryHex = 0xFF8B5CF6,
            secondaryHex = 0xFF67E8F9
        )
    )

    val streakTiers: List<StreakFlameTier> = listOf(
        StreakFlameTier(0, 0, 0, "เปลวไฟน้ำแข็ง (0 วัน)", "Ice Flame (0 Days)", "冰晶冷焰（0天）", listOf(0xFFBAE6FD, 0xFF38BDF8), 3, 0.5f),
        StreakFlameTier(1, 1, 5, "เปลวไฟส้มอ่อน (1–5 วัน)", "Soft Amber Flame (1–5d)", "浅橙火苗（1–5天）", listOf(0xFFFED7AA, 0xFFFB923C), 5, 0.8f),
        StreakFlameTier(2, 6, 10, "เปลวไฟสีส้ม (6–10 วัน)", "Orange Flame (6–10d)", "活力橙焰（6–10天）", listOf(0xFFFB923C, 0xFFEA580C), 7, 1.0f),
        StreakFlameTier(3, 11, 20, "เปลวไฟสีขาวสว่าง (11–20 วัน)", "Bright White Flame (11–20d)", "纯白圣焰（11–20天）", listOf(0xFFFFFFFF, 0xFFE2E8F0, 0xFF93C5FD), 10, 1.25f),
        StreakFlameTier(4, 21, 50, "เปลวไฟสีฟ้าสว่าง (21–50 วัน)", "Sky Cyan Flame (21–50d)", "天青烈焰（21–50天）", listOf(0xFF38BDF8, 0xFF06B6D4, 0xFFBAE6FD), 13, 1.5f),
        StreakFlameTier(5, 51, 75, "เปลวไฟสีน้ำเงินเข้มสว่างมาก (51–75 วัน)", "Sapphire Blaze (51–75d)", "深蓝星焰（51–75天）", listOf(0xFF2563EB, 0xFF1D4ED8, 0xFF60A5FA), 16, 1.75f),
        StreakFlameTier(6, 76, 100, "เปลวไฟสีม่วงสว่างมาก (76–100 วัน)", "Violet Nova (76–100d)", "紫罗兰星焰（76–100天）", listOf(0xFFA855F7, 0xFF7E22CE, 0xFFE9D5FF), 18, 1.95f),
        StreakFlameTier(7, 101, 110, "เปลวไฟสีชมพูพิเศษ (101–110 วัน)", "Radiant Pink Flame (101–110d)", "璀璨粉焰（101–110天）", listOf(0xFFF472B6, 0xFFEC4899, 0xFFFBCFE8), 20, 2.15f),
        StreakFlameTier(8, 111, 125, "เปลวไฟแดง-ขาวคู่ (111–125 วัน)", "Crimson & White Twin Flame (111–125d)", "红白双生焰（111–125天）", listOf(0xFFEF4444, 0xFFFFFFFF, 0xFFFCA5A5), 22, 2.3f),
        StreakFlameTier(9, 126, 150, "เปลวไฟฟ้า-ชมพู PT's (126–150 วัน)", "PT's Cyan-Pink Supreme (126–150d)", "PT蓝粉至尊焰（126–150天）", listOf(0xFF06B6D4, 0xFFF472B6, 0xFFFFFFFF), 25, 2.5f),
        StreakFlameTier(10, 151, 200, "เปลวไฟสายรุ้งประกายดาว (151–200 วัน)", "Rainbow Starlight Flame (151–200d)", "彩虹星光神焰（151–200天）", listOf(0xFFEF4444, 0xFFF59E0B, 0xFF10B981, 0xFF38BDF8, 0xFFA855F7, 0xFFEC4899), 28, 2.75f),
        StreakFlameTier(11, 201, 250, "เปลวไฟสายรุ้งนีออนสูงสุด (201–250 วัน)", "Hyper Neon Rainbow Aura (201–250d)", "超维霓虹彩虹焰（201–250天）", listOf(0xFFFF007F, 0xFF00F5D4, 0xFFFEE440, 0xFF9B5DE5, 0xFF00BBF9), 32, 3.0f),
        StreakFlameTier(12, 251, Int.MAX_VALUE, "เปลวไฟสีทองนีออนนิรันดร์ (>250 วัน)", "Eternal Neon Gold Sovereign (>250d)", "永恒霓虹黄金神焰（>250天）", listOf(0xFFFFEA00, 0xFFF59E0B, 0xFFFFFFFF, 0xFFFFB703), 36, 3.2f)
    )

    fun getStreakTier(days: Int): StreakFlameTier {
        val safeDays = days.coerceAtLeast(0)
        return streakTiers.firstOrNull { safeDays in it.minDays..it.maxDays } ?: streakTiers.last()
    }

    fun getPetById(speciesId: String?): PetSpecies? = pets.firstOrNull { it.id == speciesId }
    fun getBackgroundById(bgId: String?): BackgroundThemeSpec =
        backgrounds.firstOrNull { it.id == bgId } ?: backgrounds.first()
    fun getFrameById(frameId: String?): AvatarFrameSpec =
        avatarFrames.firstOrNull { it.id == frameId } ?: avatarFrames.first()
    fun getTitleStyle(index: Int): TitleStyleSpec =
        titleStyles.getOrElse(index.coerceIn(0, titleStyles.lastIndex)) { titleStyles.first() }

    data class LeaderboardBoardSpec(
        val boardId: String,
        val group: String, // OVERALL, SUBJECT, MODE, GAME
        val nameTh: String,
        val nameEn: String,
        val nameZh: String
    ) {
        fun name(lang: AppLanguage) = tr(lang, nameTh, nameEn, nameZh)
    }

    val allLeaderboardBoards: List<LeaderboardBoardSpec> by lazy {
        buildList {
            add(LeaderboardBoardSpec("OVERALL_TOTAL", "OVERALL", "รวมทุกหมวด (Overall Rank)", "Overall Combined Rank", "全服综合总榜"))
            SubjectId.entries.forEach { sub ->
                add(LeaderboardBoardSpec("SUBJECT_${sub.id}", "SUBJECT", "วิชา: ${sub.nameTh}", "Subject: ${sub.nameEn}", "科目：${sub.nameZh}"))
            }
            add(LeaderboardBoardSpec("MODE_PRACTICE", "MODE", "โหมดฝึกฝน (Practice Mode)", "Practice Mode", "日常练习模式"))
            add(LeaderboardBoardSpec("MODE_EXAM", "MODE", "โหมดสอบจริง (Exam Center)", "Exam Mode", "正式模拟考模式"))
            add(LeaderboardBoardSpec("MODE_ENTRANCE", "MODE", "โหมดสอบเข้า ม.1 เข้มข้น", "M.1 Entrance Intensive", "初一冲刺特训模式"))
            add(LeaderboardBoardSpec("MODE_BOSS_RAID", "MODE", "โหมดปราบบอส (Boss Raid)", "Boss Raid Arena", "魔王讨伐战模式"))
            MiniGameId.entries.forEach { mg ->
                add(LeaderboardBoardSpec("GAME_${mg.id}", "GAME", "เกม: ${mg.titleTh}", "Game: ${mg.titleEn}", "小游戏：${mg.titleZh}"))
            }
        }
    }
}
