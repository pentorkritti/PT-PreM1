package com.example.data.seed

import com.example.data.local.AchievementDefEntity
import com.example.data.local.FlashcardEntity
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.QuestDefinitionEntity
import com.example.data.local.QuestionEntity
import com.example.domain.ScoringAndPetEngine
import com.example.model.MiniGameId
import com.example.model.SubjectId

object InitialQuestionSeed {

    fun defaultQuests(): List<QuestDefinitionEntity> = listOf(
        // 18 Rotating Daily Quests (ViewModel deterministically selects 5 fresh daily quests per Bangkok date + reroll offset!)
        QuestDefinitionEntity(
            questCode = "DAILY_Q10", questScope = "DAILY",
            titleTh = "ทำข้อสอบครบ 10 ข้อ", titleEn = "Answer 10 Questions", titleZh = "完成10道考题",
            descTh = "ฝึกทำข้อสอบหรือแบบฝึกหัดวิชาใดก็ได้รวม 10 ข้อในวันนี้",
            descEn = "Answer 10 practice or exam questions across any subject today.",
            descZh = "今日在任意科目累计作答10道题。",
            metricType = "QUESTIONS_DONE", targetCount = 10, rewardXp = 120, rewardPt = 25
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_Q20", questScope = "DAILY",
            titleTh = "ขยันลุยโจทย์ 20 ข้อ", titleEn = "Sprint 20 Questions", titleZh = "冲刺作答20题",
            descTh = "ตะลุยทำข้อสอบรวมให้ครบ 20 ข้อในวันนี้",
            descEn = "Answer 20 total exam or practice questions today.",
            descZh = "今日累计作答满20道题目。",
            metricType = "QUESTIONS_DONE", targetCount = 20, rewardXp = 220, rewardPt = 45
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_CORRECT_5", questScope = "DAILY",
            titleTh = "ตอบถูกแม่นยำ 5 ข้อ", titleEn = "Get 5 Correct Answers", titleZh = "答对5道题",
            descTh = "สะสมคำตอบที่ถูกต้องให้ครบ 5 ข้อในวันนี้",
            descEn = "Answer at least 5 questions correctly today.",
            descZh = "今日累计答对5道题目。",
            metricType = "CORRECT_ANSWERS", targetCount = 5, rewardXp = 100, rewardPt = 20
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_CORRECT_12", questScope = "DAILY",
            titleTh = "คมกริบ! ตอบถูก 12 ข้อ", titleEn = "Sharpshooter: 12 Correct", titleZh = "神准！答对12题",
            descTh = "สะสมคำตอบที่ถูกต้องรวม 12 ข้อในวันนี้",
            descEn = "Accumulate 12 correct answers today.",
            descZh = "今日累计答对12道题目。",
            metricType = "CORRECT_ANSWERS", targetCount = 12, rewardXp = 210, rewardPt = 45
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_SCI_1", questScope = "DAILY",
            titleTh = "สำรวจโลกวิทยาศาสตร์ 1 ชุด", titleEn = "Complete 1 Science Set", titleZh = "完成1套科学试卷",
            descTh = "เข้าฝึกหรือสอบวิชาวิทยาศาสตร์ให้จบ 1 ชุด",
            descEn = "Finish 1 full Science practice or exam session.",
            descZh = "完成1套完整的科学练习或考试。",
            metricType = "SCIENCE_EXAMS", targetCount = 1, rewardXp = 150, rewardPt = 35
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_MATH_1", questScope = "DAILY",
            titleTh = "พิชิตโจทย์คณิตศาสตร์ 1 ชุด", titleEn = "Complete 1 Math Set", titleZh = "完成1套数学试卷",
            descTh = "ฝึกหรือสอบวิชาคณิตศาสตร์ให้จบ 1 ชุดในวันนี้",
            descEn = "Complete 1 Mathematics practice or exam session today.",
            descZh = "今日完成1套数学练习或模拟考。",
            metricType = "MATH_EXAMS", targetCount = 1, rewardXp = 150, rewardPt = 35
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_THAI_1", questScope = "DAILY",
            titleTh = "ยอดกวีภาษาไทย 1 ชุด", titleEn = "Complete 1 Thai Set", titleZh = "完成1套泰语试卷",
            descTh = "ฝึกหรือสอบวิชาภาษาไทยให้จบ 1 ชุดในวันนี้",
            descEn = "Complete 1 Thai Language exam or practice session today.",
            descZh = "今日完成1套泰语科目练习或考试。",
            metricType = "THAI_EXAMS", targetCount = 1, rewardXp = 145, rewardPt = 32
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_ENG_1", questScope = "DAILY",
            titleTh = "อินเตอร์พิชิตอังกฤษ 1 ชุด", titleEn = "Complete 1 English Set", titleZh = "完成1套英语试卷",
            descTh = "ฝึกหรือสอบวิชาภาษาอังกฤษให้จบ 1 ชุดในวันนี้",
            descEn = "Complete 1 English exam or practice session today.",
            descZh = "今日完成1套英语科目练习或考试。",
            metricType = "ENGLISH_EXAMS", targetCount = 1, rewardXp = 150, rewardPt = 35
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_LOGIC_1", questScope = "DAILY",
            titleTh = "ลับเชาวน์ปัญญา IQ & Logic 1 ชุด", titleEn = "Complete 1 IQ & Logic Set", titleZh = "完成1套逻辑推理试卷",
            descTh = "ฝึกหรือสอบวิชาเชาวน์ปัญญาและตรรกะ 1 ชุด",
            descEn = "Finish 1 IQ & Logical Reasoning session today.",
            descZh = "今日完成1套智力与逻辑推理训练。",
            metricType = "LOGIC_EXAMS", targetCount = 1, rewardXp = 165, rewardPt = 40
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_EXAM_2", questScope = "DAILY",
            titleTh = "ส่งกระดาษคำตอบครบ 2 ชุด", titleEn = "Finish 2 Exam Sets", titleZh = "完成2套考试卷",
            descTh = "ทำชุดสอบหรือโหมดฝึกวิชาใดก็ได้ให้จบครบ 2 ชุด",
            descEn = "Complete 2 full exam or practice sets today.",
            descZh = "今日完成任意科目的2套试卷。",
            metricType = "EXAMS_COMPLETED", targetCount = 2, rewardXp = 200, rewardPt = 45
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_MINIGAME_1", questScope = "DAILY",
            titleTh = "ลับสมองด้วย Mini Game 1 รอบ", titleEn = "Play 1 Brain Mini-Game", titleZh = "完成1局益智小游戏",
            descTh = "เล่นมินิเกมใดก็ได้จนจบ 1 รอบเพื่อผ่อนคลายและฝึกไหวพริบ",
            descEn = "Finish 1 round of any educational Mini-Game.",
            descZh = "游玩并完成任意1局脑力小游戏。",
            metricType = "MINIGAMES_PLAYED", targetCount = 1, rewardXp = 95, rewardPt = 20
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_MINIGAME_2", questScope = "DAILY",
            titleTh = "แชมป์อาร์เคด Mini Game 2 รอบ", titleEn = "Play 2 Brain Mini-Games", titleZh = "游玩2局益智小游戏",
            descTh = "เล่นมินิเกมฝึกสมองครบ 2 รอบในวันนี้",
            descEn = "Complete 2 rounds in the Educational Mini-Game Arcade.",
            descZh = "今日累计完成2局益智小游戏。",
            metricType = "MINIGAMES_PLAYED", targetCount = 2, rewardXp = 160, rewardPt = 35
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_FLASHCARD_5", questScope = "DAILY",
            titleTh = "พลิกแฟลชการ์ดจำแม่น 5 ใบ", titleEn = "Review 5 Flashcards", titleZh = "复习5张知识闪卡",
            descTh = "เปิดทบทวนและกดจำแม่นในโหมดแฟลชการ์ดครบ 5 ใบ",
            descEn = "Flip and master 5 flashcards in the Smart Study Hub.",
            descZh = "在智慧学习中心翻转并掌握5张闪卡。",
            metricType = "FLASHCARDS_REVIEWED", targetCount = 5, rewardXp = 130, rewardPt = 30
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_MISTAKE_1", questScope = "DAILY",
            titleTh = "แก้จุดอ่อนในสมุดข้อผิด 1 ข้อ", titleEn = "Master 1 Past Mistake", titleZh = "攻克1道错题本题目",
            descTh = "เข้าสอบซ่อมข้อที่เคยผิดหรือเคลียร์ข้อผิดได้สำเร็จ 1 ข้อ",
            descEn = "Successfully master at least 1 question from your Mistake Notebook.",
            descZh = "在错题本重练模式中成功订正1道错题。",
            metricType = "MISTAKES_MASTERED", targetCount = 1, rewardXp = 160, rewardPt = 40
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_BOSS_1", questScope = "DAILY",
            titleTh = "ปราบบอสพิชิตข้อสอบ 1 ตัว", titleEn = "Defeat 1 Exam Boss", titleZh = "击败1只考题魔王",
            descTh = "เอาชนะบอสประจำวิชาในโหมดท้าดวลบอสสำเร็จ 1 ครั้ง",
            descEn = "Win 1 battle in Boss Raid Mode today.",
            descZh = "今日在魔王讨伐模式中获胜1次。",
            metricType = "BOSS_DEFEATED", targetCount = 1, rewardXp = 220, rewardPt = 55
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_PLAN_1", questScope = "DAILY",
            titleTh = "ทำตามตารางอ่านหนังสือ 1 เป้าหมาย", titleEn = "Complete 1 Study Plan Task", titleZh = "完成1项复习计划",
            descTh = "ติ๊กสำเร็จภารกิจอ่านหนังสือในตัววางแผนสอบ ม.1 จำนวน 1 รายการ",
            descEn = "Check off 1 study goal in your M.1 Study Planner.",
            descZh = "在备考计划表中完成1项学习目标。",
            metricType = "STUDY_PLAN_DONE", targetCount = 1, rewardXp = 140, rewardPt = 30
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_WHEEL_1", questScope = "DAILY",
            titleTh = "หมุนวงล้อนำโชครายวัน 1 ครั้ง", titleEn = "Spin the Lucky Wheel", titleZh = "转动1次幸运转盘",
            descTh = "เข้าไปหมุนวงล้อนำโชครายวันรับรางวัลฟรี 1 ครั้ง",
            descEn = "Spin the Daily Lucky Wheel once at Rewards Plaza.",
            descZh = "前往奖励广场转动1次每日幸运大转盘。",
            metricType = "LUCKY_WHEEL_SPUN", targetCount = 1, rewardXp = 90, rewardPt = 20
        ),
        QuestDefinitionEntity(
            questCode = "DAILY_CHEST_1", questScope = "DAILY",
            titleTh = "เปิดกล่องสมบัติปริศนา 1 กล่อง", titleEn = "Open 1 Mystery Chest", titleZh = "开启1个神秘宝箱",
            descTh = "เปิดหีบสมบัติปริศนาระดับใดก็ได้ 1 กล่อง",
            descEn = "Unbox 1 Mystery Treasure Chest of any tier.",
            descZh = "开启任意等级的神秘宝箱1个。",
            metricType = "CHEST_OPENED", targetCount = 1, rewardXp = 150, rewardPt = 35
        ),
        // 4 Weekly Quests
        QuestDefinitionEntity(
            questCode = "WEEKLY_CHAMPION", questScope = "WEEKLY",
            titleTh = "พิชิตสนามสอบ ม.1 ประจำสัปดาห์ (5 ชุด)", titleEn = "Weekly M.1 Arena Challenger (5 Sets)", titleZh = "每周初一冲刺挑战（完成5套试卷）",
            descTh = "ทำชุดข้อสอบวิชาใดก็ได้ให้ครบ 5 ชุดภายในสัปดาห์นี้เพื่อรับรางวัลใหญ่!",
            descEn = "Complete 5 exam sets across any subjects this week for a grand reward!",
            descZh = "本周累计完成5套考试卷即可领取丰厚大奖！",
            metricType = "EXAMS_COMPLETED", targetCount = 5, rewardXp = 600, rewardPt = 180
        ),
        QuestDefinitionEntity(
            questCode = "WEEKLY_CORRECT_50", questScope = "WEEKLY",
            titleTh = "สัปดาห์แห่งความแม่นยำ (ตอบถูก 50 ข้อ)", titleEn = "Weekly Precision (50 Correct)", titleZh = "每周神准学霸（答对50题）",
            descTh = "สะสมคำตอบที่ถูกต้องครบ 50 ข้อภายในสัปดาห์นี้",
            descEn = "Answer 50 questions correctly across the week.",
            descZh = "本周累计答对50道题目。",
            metricType = "CORRECT_ANSWERS", targetCount = 50, rewardXp = 750, rewardPt = 220
        ),
        QuestDefinitionEntity(
            questCode = "WEEKLY_BOSS_SLAYER_3", questScope = "WEEKLY",
            titleTh = "นักล่าบอสรายสัปดาห์ (ปราบบอส 3 ตัว)", titleEn = "Weekly Boss Hunter (3 Bosses)", titleZh = "每周魔王猎人（击败3次BOSS）",
            descTh = "ปราบบอสวิชาใดก็ได้รวม 3 ครั้งภายในสัปดาห์นี้",
            descEn = "Defeat Subject Bosses 3 times this week.",
            descZh = "本周在魔王讨伐模式中累计获胜3次。",
            metricType = "BOSS_DEFEATED", targetCount = 3, rewardXp = 680, rewardPt = 210
        ),
        QuestDefinitionEntity(
            questCode = "WEEKLY_SMART_SCHOLAR", questScope = "WEEKLY",
            titleTh = "ปราชญ์แฟลชการ์ดรายสัปดาห์ (15 ใบ)", titleEn = "Weekly Flashcard Scholar (15 Cards)", titleZh = "每周闪卡大师（掌握15张）",
            descTh = "ทบทวนแฟลชการ์ดครบ 15 ครั้งในสัปดาห์นี้",
            descEn = "Review 15 flashcards during this week.",
            descZh = "本周累计复习掌握15张知识闪卡。",
            metricType = "FLASHCARDS_REVIEWED", targetCount = 15, rewardXp = 520, rewardPt = 160
        )
    )

    // 34 Achievements with complete TH/EN/ZH text
    fun defaultAchievements(): List<AchievementDefEntity> = listOf(
        AchievementDefEntity("FIRST_EXAM", "ก้าวแรกสู่สนามสอบ", "First Exam Step", "初次登场", "ทำชุดข้อสอบสำเร็จเป็นครั้งแรก", "Complete your very first exam set.", "完成第一套试卷。", 80, 25),
        AchievementDefEntity("EXAM_5", "นักเรียนขยันสอบ (5 ชุด)", "5 Exams Completed", "五卷勤学", "ทำชุดข้อสอบสำเร็จครบ 5 ชุด", "Complete 5 exam sets.", "累计完成5套考试卷。", 180, 50),
        AchievementDefEntity("EXAM_15", "จอมยุทธ์ห้องสอบ (15 ชุด)", "15 Exams Veteran", "考场老将（15套）", "ทำชุดข้อสอบสำเร็จครบ 15 ชุด", "Complete 15 exam sets.", "累计完成15套考试卷。", 420, 120),
        AchievementDefEntity("EXAM_30", "ตำนานสนามสอบ ม.1 (30 ชุด)", "30 Exams Grand Scholar", "初一考霸（30套）", "ทำชุดข้อสอบสำเร็จครบ 30 ชุด", "Complete 30 exam sets!", "累计完成30套考试卷！", 900, 280),
        AchievementDefEntity("CORRECT_10", "แม่นยำ 10 ข้อ", "10 Correct Answers", "十题精准", "สะสมคำตอบที่ถูกต้องครบ 10 ข้อ", "Reach 10 cumulative correct answers.", "累计答对10道题目。", 120, 30),
        AchievementDefEntity("CORRECT_50", "เฉียบคม 50 ข้อ", "50 Correct Answers", "五十题连捷", "สะสมคำตอบที่ถูกต้องครบ 50 ข้อ", "Reach 50 cumulative correct answers.", "累计答对50道题目。", 280, 80),
        AchievementDefEntity("CORRECT_100", "ปรมาจารย์ 100 ข้อ", "100 Correct Answers", "百题大师", "สะสมคำตอบที่ถูกต้องครบ 100 ข้อ", "Reach 100 cumulative correct answers.", "累计答对100道题目。", 500, 150),
        AchievementDefEntity("CORRECT_250", "อัจฉริยะ 250 ข้อ", "250 Correct Genius", "二百五十题智多星", "สะสมคำตอบที่ถูกต้องครบ 250 ข้อ", "Reach 250 cumulative correct answers.", "累计答对250道题目。", 1000, 320),
        AchievementDefEntity("CORRECT_500", "มหาปราชญ์ 500 ข้อ", "500 Correct Sage", "五百题宗师", "สะสมคำตอบที่ถูกต้องครบ 500 ข้อ!", "Reach 500 cumulative correct answers!", "累计答对500道题目！", 2000, 650),
        AchievementDefEntity("PERFECT_EXAM", "เต็มร้อยไร้ที่ติ", "Perfect 100% Exam", "满分通关", "ตอบถูกครบทุกข้อในชุดสอบ (100%)", "Score 100% accuracy in an exam set.", "在一套试卷中全部答对（100%）。", 220, 100),
        AchievementDefEntity("STREAK_1", "จุดประกายไฟ (1 วัน)", "1 Day Streak", "点燃火种（1天连胜）", "เริ่มต้นเปลวไฟ Streak วันแรกสำเร็จ", "Ignite your first 1-day Streak Flame.", "点亮第1天学习连胜火焰。", 60, 20),
        AchievementDefEntity("STREAK_3", "ไฟอุ่นต่อเนื่อง (3 วัน)", "3 Days Streak", "三日热忱", "รักษา Streak ต่อเนื่องครบ 3 วัน", "Maintain a 3-day learning streak.", "连续学习达到3天。", 130, 40),
        AchievementDefEntity("STREAK_7", "ไฟแห่งวินัย (7 วัน)", "7 Days Streak", "七日坚持", "รักษา Streak ต่อเนื่องครบ 7 วัน", "Maintain a 7-day learning streak.", "连续学习达到7天。", 260, 85),
        AchievementDefEntity("STREAK_14", "เปลวไฟสีขาวสว่าง (14 วัน)", "14 Days Bright Flame", "十四日白焰", "รักษา Streak ต่อเนื่องครบ 14 วัน", "Maintain a 14-day learning streak.", "连续学习达到14天。", 450, 150),
        AchievementDefEntity("STREAK_30", "อัศวินเปลวเพลิง (30 วัน)", "30 Days Streak", "三十日烈焰骑士", "รักษา Streak ต่อเนื่องครบ 30 วัน", "Maintain a 30-day learning streak.", "连续学习达到30天。", 850, 300),
        AchievementDefEntity("STREAK_100", "ตำนานไร้พ่าย (100 วัน)", "100 Days Streak", "百日不灭传奇", "รักษา Streak ต่อเนื่องครบ 100 วัน", "Reach an extraordinary 100-day streak!", "连续学习达到100天！", 2500, 1000),
        AchievementDefEntity("LEVEL_5", "ก้าวสู่เลเวล 5", "Level 5 Explorer", "达到5级", "พัฒนาตัวละครถึง Level 5", "Reach Player Level 5.", "玩家等级达到5级。", 150, 45),
        AchievementDefEntity("LEVEL_10", "ดาวรุ่งเลเวล 10", "Level 10 Rising Star", "达到10级", "พัฒนาตัวละครถึง Level 10", "Reach Player Level 10.", "玩家等级达到10级。", 350, 110),
        AchievementDefEntity("LEVEL_25", "ยอดฝีมือเลเวล 25", "Level 25 Ace", "达到25级", "พัฒนาตัวละครถึง Level 25", "Reach Player Level 25.", "玩家等级达到25级。", 900, 300),
        AchievementDefEntity("PT_WEALTH_500", "กระปุกเหรียญฟ้า 500 PT", "500 PT Saver", "五百积分储蓄家", "มี PT Points สะสมแตะ 500 PT", "Hold at least 500 PT Points.", "持有达到500 PT积分。", 140, 40),
        AchievementDefEntity("PT_WEALTH_2000", "เศรษฐีร้านแดง-ขาว 2,000 PT", "2,000 PT Tycoon", "两千积分大亨", "มี PT Points สะสมแตะ 2,000 PT!", "Hold at least 2,000 PT Points!", "持有达到2,000 PT积分！", 500, 160),
        AchievementDefEntity("FIRST_PET", "เพื่อนซี้ตัวแรก", "First Pet Companion", "第一位伙伴", "สุ่มไข่และได้รับสัตว์เลี้ยงตัวแรก", "Hatch your very first Pet companion.", "孵化获得第一只宠物伙伴。", 100, 40),
        AchievementDefEntity("PET_SQUAD_5", "ครอบครัวสัตว์เลี้ยง (5 ตัว)", "5 Pets Collected", "五宠小队", "มีสัตว์เลี้ยงในคลังครบ 5 ตัว", "Own at least 5 Pets in your collection.", "宠物背包拥有5只宠物。", 260, 90),
        AchievementDefEntity("FIRST_RARE_PET", "ผู้ค้นพบความหายาก", "Rare+ Pet Finder", "稀有收藏家", "ได้รับสัตว์เลี้ยงระดับ Rare ขึ้นไปเป็นครั้งแรก", "Obtain a Rare or higher-tier Pet.", "首次获得稀有及以上品质宠物。", 300, 100),
        AchievementDefEntity("LEGENDARY_COLLECTOR", "นักสะสมระดับตำนาน", "Legendary Collector", "传说训练师", "ได้รับสัตว์เลี้ยงระดับ Legendary, Satriwit หรือ Suankularb", "Obtain a Legendary, Satriwit, or Suankularb Pet!", "获得传说级或名校神宠！", 1000, 500),
        AchievementDefEntity("BOSS_FIRST", "ผู้ปราบบอสคนแรก", "First Boss Victory", "初战屠龙", "เอาชนะบอสประจำวิชาสำเร็จเป็นครั้งแรก", "Defeat your first Subject Boss in Boss Raid!", "首次击败科目魔王！", 250, 90),
        AchievementDefEntity("BOSS_SLAYER_5", "จอมพิชิตบอสทั้งห้า", "5 Bosses Defeated", "五王讨伐者", "เอาชนะบอสครบ 5 ครั้ง", "Defeat Subject Bosses 5 times.", "累计击败科目魔王5次。", 650, 220),
        AchievementDefEntity("MISTAKE_CRUSHER_1", "เรียนรู้จากความผิดพลาด", "First Mistake Mastered", "知错能改", "ทบทวนและตอบถูกในข้อที่เคยผิดสำเร็จครั้งแรก", "Master your first past mistake!", "首次成功订正错题本中的题目！", 150, 45),
        AchievementDefEntity("MISTAKE_CRUSHER_10", "เปลี่ยนจุดอ่อนเป็นจุดแข็ง (10 ข้อ)", "10 Mistakes Mastered", "十错全消", "เคลียร์ข้อที่เคยตอบผิดสำเร็จครบ 10 ข้อ", "Master 10 past mistakes.", "累计攻克10道历史错题。", 450, 150),
        AchievementDefEntity("FLASHCARD_10", "ความจำติดสปีด (10 ใบ)", "10 Flashcards Mastered", "十卡速记", "ทบทวนแฟลชการ์ดครบ 10 ครั้ง", "Review 10 flashcards.", "累计复习10张知识闪卡。", 180, 55),
        AchievementDefEntity("PLANNER_HERO_3", "นักวางแผนตัวจริง (3 เป้าหมาย)", "3 Study Goals Completed", "自律规划师", "ทำภารกิจอ่านหนังสือใน Planner สำเร็จ 3 รายการ", "Complete 3 Study Planner tasks.", "完成3项学习规划任务。", 220, 70),
        AchievementDefEntity("LUCKY_SPINNER", "นักหมุนวงล้อสายรุ้ง", "Lucky Wheel Spinner", "幸运之星", "หมุนวงล้อนำโชครายวันสำเร็จครั้งแรก", "Spin the Daily Lucky Wheel.", "首次转动每日幸运大转盘。", 90, 30),
        AchievementDefEntity("TREASURE_HUNTER", "นักล่าหีบสมบัติปริศนา", "Mystery Chest Unboxer", "寻宝猎人", "เปิดหีบสมบัติปริศนาสำเร็จครบ 3 กล่อง", "Open 3 Mystery Treasure Chests.", "累计开启3个神秘宝箱。", 260, 85),
        AchievementDefEntity("CUSTOM_TITLE_SET", "ผู้มีฉายาประจำตัว", "Signature Title Bearer", "专属称号达人", "ตั้งฉายาผู้เล่นแบบพิมพ์เองหรือเปลี่ยนกรอบอวตาร", "Customize your Player Title or Avatar Frame.", "自定义专属玩家称号或更换头像框。", 100, 35)
    )

    fun defaultFlashcards(): List<FlashcardEntity> = listOf(
        // MATH (6)
        FlashcardEntity(subjectId = "MATH", frontTh = "สูตรพื้นที่รูปสามเหลี่ยม", frontEn = "Area of a Triangle", frontZh = "三角形面积公式", backTh = "1/2 × ฐาน × สูง", backEn = "1/2 × base × height", backZh = "1/2 × 底 × 高", exampleText = "ฐาน 10 ซม. สูง 6 ซม. → พื้นที่ = 30 ตร.ซม."),
        FlashcardEntity(subjectId = "MATH", frontTh = "ความสัมพันธ์ของ ห.ร.ม. และ ค.ร.น. ของเลข 2 จำนวน (A, B)", frontEn = "GCD & LCM Relation for Two Numbers (A, B)", frontZh = "两数最大公因数与最小公倍数关系", backTh = "A × B = ห.ร.ม. × ค.ร.น.", backEn = "A × B = GCD(A,B) × LCM(A,B)", backZh = "A × B = 最大公因数 × 最小公倍数", exampleText = "ถ้า ห.ร.ม.=6, ค.ร.น.=36 และ A=12 → B = (6×36)/12 = 18"),
        FlashcardEntity(subjectId = "MATH", frontTh = "สูตรพื้นที่วงกลม และ เส้นรอบวง", frontEn = "Circle Area & Circumference", frontZh = "圆的面积与周长公式", backTh = "พื้นที่ = πr² | เส้นรอบวง = 2πr", backEn = "Area = πr² | Circumference = 2πr", backZh = "面积 = πr² | 周长 = 2πr", exampleText = "เมื่อ r = 7 ซม. (π ≈ 22/7) → พื้นที่ = 154 ตร.ซม."),
        FlashcardEntity(subjectId = "MATH", frontTh = "ผลบวกมุมภายในของรูปหลายเหลี่ยม n ด้าน", frontEn = "Sum of Interior Angles of an n-gon", frontZh = "n边形内角和公式", backTh = "(n − 2) × 180 องศา", backEn = "(n − 2) × 180°", backZh = "(n − 2) × 180°", exampleText = "หกเหลี่ยม (n=6) → (6−2)×180° = 720°"),
        FlashcardEntity(subjectId = "MATH", frontTh = "สูตรกำไร / ขาดทุนร้อยละเทียบกับอะไรเสมอ?", frontEn = "Profit / Loss Percentage Base", frontZh = "利润率与亏损率的基准", backTh = "เทียบกับ 'ทุน (Cost)' เสมอ: (กำไร ÷ ทุน) × 100", backEn = "Always relative to Cost Price: (Profit ÷ Cost) × 100%", backZh = "始终以成本价为基准：(利润 ÷ 成本) × 100%", exampleText = "ทุน 200 ขาย 250 → กำไร 50 → ร้อยละ 25"),
        FlashcardEntity(subjectId = "MATH", frontTh = "ปริมาตรทรงกระบอก", frontEn = "Volume of a Cylinder", frontZh = "圆柱体体积公式", backTh = "πr²h (พื้นที่ฐาน × สูง)", backEn = "πr²h (Base Area × Height)", backZh = "πr²h（底面积 × 高）", exampleText = "r=7, h=10 → ปริมาตร = 1,540 ลบ.หน่วย"),
        // SCIENCE (6)
        FlashcardEntity(subjectId = "SCIENCE", frontTh = "สมการการสังเคราะห์ด้วยแสงของพืช", frontEn = "Photosynthesis Equation", frontZh = "植物光合作用核心过程", backTh = "น้ำ + คาร์บอนไดออกไซด์ (+ แสง & คลอโรฟิลล์) → น้ำตาลกลูโคส + ออกซิเจน + น้ำ", backEn = "Water + CO₂ (+ Light & Chlorophyll) → Glucose + Oxygen + Water", backZh = "水 + 二氧化碳（光与叶绿素）→ 葡萄糖 + 氧气 + 水", exampleText = "เก็บสะสมในรูปแป้ง ทดสอบด้วยสารละลายไอโอดีนเปลี่ยนเป็นสีน้ำเงินม่วง"),
        FlashcardEntity(subjectId = "SCIENCE", frontTh = "การต่อหลอดไฟแบบอนุกรม vs แบบขนาน", frontEn = "Series vs Parallel Circuits", frontZh = "串联电路 vs 并联电路", backTh = "อนุกรม: หลอดหนึ่งดับ ที่เหลือดับหมด | ขนาน: หลอดหนึ่งดับ ดวงอื่นยังสว่าง (ใช้ในบ้าน)", backEn = "Series: one bulb out breaks all | Parallel: independent branches (used in homes)", backZh = "串联：一处断开全部熄灭 | 并联：各支路独立工作（家庭电路采用）", exampleText = "ไฟบ้านต่อแบบขนานเพื่อให้เปิด-ปิดแยกดวงได้"),
        FlashcardEntity(subjectId = "SCIENCE", frontTh = "การแยกสารเนื้อผสม: การระเหิด (Sublimation)", frontEn = "Sublimation Separation", frontZh = "升华分离法", backTh = "ของแข็งเปลี่ยนสถานะเป็นแก๊สโดยไม่ผ่านของเหลว เช่น ลูกเหม็น การบูร เกล็ดไอโอดีน น้ำแข็งแห้ง", backEn = "Solid turns directly into gas: Naphthalene, Camphor, Iodine crystals, Dry Ice.", backZh = "固体直接变为气体：樟脑丸、冰片、碘晶体、干冰。", exampleText = "ใช้แยกเกล็ดพิมเสนออกจากเกลือแกงได้!"),
        FlashcardEntity(subjectId = "SCIENCE", frontTh = "เอนไซม์อะไมเลส (Amylase) เริ่มย่อยสารอาหารใด ที่อวัยวะใด?", frontEn = "Where does Amylase start digestion?", frontZh = "唾液淀粉酶在哪里开始消化？", backTh = "เริ่มย่อยคาร์โบไฮเดรต (แป้ง) ที่ 'ปาก' ให้เป็นน้ำตาลมอลโทส", backEn = "Starts digesting starch (carbohydrates) in the Mouth.", backZh = "在口腔中开始将淀粉分解为麦芽糖。", exampleText = "โปรตีนเริ่มย่อยที่กระเพาะอาหาร / ไขมันย่อยที่ลำไส้เล็กโดยมีน้ำดีช่วยทำให้แตกตัว"),
        FlashcardEntity(subjectId = "SCIENCE", frontTh = "สุริยุปราคา (Solar Eclipse) เกิดในวันใดและเรียงลำดับดาวอย่างไร?", frontEn = "Solar Eclipse Alignment", frontZh = "日食发生条件与排列顺序", backTh = "วันแรม 15 ค่ำ: ดวงอาทิตย์ — ดวงจันทร์ — โลก (ดวงจันทร์บังแสงอาทิตย์)", backEn = "New Moon: Sun — Moon — Earth (Moon casts shadow on Earth).", backZh = "朔日（新月）：太阳 — 月球 — 地球。", exampleText = "จันทรุปราคา = วันขึ้น 15 ค่ำ (อาทิตย์ — โลก — จันทร์)"),
        FlashcardEntity(subjectId = "SCIENCE", frontTh = "หินอัคนี หินตะกอน และหินแปร ต่างกันอย่างไร?", frontEn = "Igneous, Sedimentary & Metamorphic Rocks", frontZh = "火成岩、沉积岩与变质岩", backTh = "อัคนี: แกรนิต/บะซอลต์ | ตะกอน(มีฟอสซิล): หินทราย/หินปูน | หินแปร: หินอ่อน(แปรจากหินปูน)/หินชนวน", backEn = "Igneous: Granite/Basalt | Sedimentary (fossils): Limestone/Sandstone | Metamorphic: Marble/Slate.", backZh = "火成岩：花岗岩/玄武岩 | 沉积岩（含化石）：石灰岩/砂岩 | 变质岩：大理岩/板岩。", exampleText = "ฟอสซิลพบในหินตะกอนเท่านั้น!"),
        // ENGLISH (6)
        FlashcardEntity(subjectId = "ENGLISH", frontTh = "โครงสร้าง If-Clause แบบที่ 1 (First Conditional)", frontEn = "First Conditional Structure", frontZh = "第一条件句结构", backTh = "If + Present Simple (V.1), Subject + will + V.inf", backEn = "If + Subject + V.1(s/es), Subject + will + base verb", backZh = "If + 一般现在时，主语 + will + 动词原形", exampleText = "If it rains tomorrow, I will stay at home."),
        FlashcardEntity(subjectId = "ENGLISH", frontTh = "คำบอกเวลา Present Perfect Tense", frontEn = "Present Perfect Signal Words", frontZh = "现在完成时标志词", backTh = "since (+ จุดเริ่มต้นเวลา), for (+ ระยะเวลา), already, yet, just, ever, never → have/has + V.3", backEn = "since, for, already, yet, just, ever, never → have/has + Past Participle (V.3)", backZh = "since（自从时间点）、for（持续时间段）、already、yet → have/has + 过去分词", exampleText = "She has studied English for five years."),
        FlashcardEntity(subjectId = "ENGLISH", frontTh = "Essential M.1 Vocab: 'Diligent' & 'Persevere'", frontEn = "Vocab: Diligent & Persevere", frontZh = "核心词汇：Diligent & Persevere", backTh = "Diligent (adj.) = ขยันหมั่นเพียร | Persevere (v.) = พยายามอดทนไม่ย่อท้อ", backEn = "Diligent = hard-working | Persevere = keep trying despite difficulties", backZh = "Diligent = 勤奋刻苦的 | Persevere = 坚持不懈", exampleText = "A diligent student always perseveres until succeeding."),
        FlashcardEntity(subjectId = "ENGLISH", frontTh = "กฎ Subject-Verb Agreement: 'Neither A nor B ...'", frontEn = "Rule: Neither A nor B + Verb", frontZh = "主谓一致：Neither A nor B", backTh = "กริยาผันตามประธานตัวหลัง (B) ที่อยู่ใกล้กริยาที่สุด!", backEn = "The verb agrees with the closer subject (B)!", backZh = "谓语动词遵循就近原则，与靠近的B保持一致！", exampleText = "Neither the teacher nor the students ARE in the room."),
        FlashcardEntity(subjectId = "ENGLISH", frontTh = "Essential M.1 Vocab: 'Environment' & 'Conserve'", frontEn = "Vocab: Environment & Conserve", frontZh = "核心词汇：Environment & Conserve", backTh = "Environment (n.) = สิ่งแวดล้อม | Conserve (v.) = อนุรักษ์ รักษาไว้", backEn = "Environment = natural world | Conserve = protect from harm or waste", backZh = "Environment = 环境 | Conserve = 保护、节约", exampleText = "We should conserve energy to protect the environment."),
        FlashcardEntity(subjectId = "ENGLISH", frontTh = "การใช้ Question Tag พื้นฐาน", frontEn = "Question Tag Golden Rule", frontZh = "反意疑问句核心规则", backTh = "ประโยคหน้าบอกเล่า → Tag ปฏิเสธ | ประโยคหน้าปฏิเสธ → Tag บอกเล่า", backEn = "Positive statement → Negative tag | Negative statement → Positive tag", backZh = "前肯后否，前否后肯（时态与助动词一致）", exampleText = "You can swim, can't you? / Let's go, shall we?"),
        // THAI (6)
        FlashcardEntity(subjectId = "THAI", frontTh = "อักษรสามหมู่ (ไตรยางศ์): อักษรกลาง 9 ตัว", frontEn = "Thai Three Consonant Classes: Middle Class (9)", frontZh = "泰语中辅音9个字母口诀", backTh = "ก จ ด ต ด(ฎ) ต(ฏ) บ ป อ — ท่องจำง่าย: 'ไก่ จิก เด็ก ตาย บน ปาก โอ่ง' (ผันได้ครบ 5 เสียง)", backEn = "ก จ ฎ ฏ ด ต บ ป อ — conjugates across all 5 Thai tones.", backZh = "ก จ ฎ ฏ ด ต บ ป อ（共9个，可拼读全部5个声调）。", exampleText = "อักษรกลางคำเป็นผันวรรณยุกต์ได้ครบ 5 เสียงตรงตามรูปวรรณยุกต์"),
        FlashcardEntity(subjectId = "THAI", frontTh = "คำเป็น vs คำตาย ดูอย่างไรให้แม่นที่สุด?", frontEn = "Live Syllable vs Dead Syllable in Thai", frontZh = "泰语活音节与死音节判断法", backTh = "เช็กตัวสะกดก่อน! แม่ กง กน กม เกย เกอว (นมยวง) = คำเป็น | แม่ กก กด กบ (กบด) = คำตาย", backEn = "Sonorant finals (ง น ม ย ว) = Live | Stop finals (ก ด บ) or short open vowel = Dead.", backZh = "鼻音/半元音韵尾（นมยวง）为活音节；塞音韵尾（กบด）或短元音开音节为死音节。", exampleText = "ถ้าไม่มีตัวสะกด: สระเสียงยาว (+ อำ ใอ ไอ เอา) = คำเป็น"),
        FlashcardEntity(subjectId = "THAI", frontTh = "คำราชาศัพท์หมวดร่างกายที่ออกสอบบ่อย", frontEn = "High-Frequency Royal Thai Body Terms", frontZh = "高频泰语皇室词汇（身体部位）", backTh = "พระเนตร = ตา | พระกรรณ = หู | พระนาสิก = จมูก | พระโอษฐ์ = ปาก | พระหัตถ์ = มือ | พระบาท = เท้า", backEn = "พระเนตร=Eye, พระกรรณ=Ear, พระนาสิก=Nose, พระโอษฐ์=Mouth, พระหัตถ์=Hand", backZh = "พระเนตร=眼，พระกรรณ=耳，พระนาสิก=鼻，พระโอษฐ์=口，พระหัตถ์=手", exampleText = "ข้อควรระวัง: 'พระศอ' = คอ, 'พระเศียร' = ศีรษะ"),
        FlashcardEntity(subjectId = "THAI", frontTh = "สำนวนไทย: 'น้ำท่วมปาก' vs 'น้ำลดตอผุด'", frontEn = "Thai Idioms: Nam Thuam Pak vs Nam Lot To Phut", frontZh = "泰语成语辨析", backTh = "น้ำท่วมปาก = พูดความจริงไม่ได้เพราะเกรงจะมีภัย | น้ำลดตอผุด = เมื่อหมดอำนาจความชั่วที่ทำไว้ก็ปรากฏ", backEn = "น้ำท่วมปาก = unable to speak out | น้ำลดตอผุด = hidden misdeeds surface when power fades", backZh = "น้ำท่วมปาก = 有口难言 | น้ำลดตอผุด = 水落石出（失势后恶行败露）", exampleText = "มักออกข้อสอบเปรียบเทียบความหมายกับสถานการณ์"),
        FlashcardEntity(subjectId = "THAI", frontTh = "ชนิดของประโยค: สามัญ รวม ซ้อน", frontEn = "Thai Sentence Structures: Simple, Compound, Complex", frontZh = "泰语句子结构：简单句、并列句、主从复合句", backTh = "ความเดียว(สามัญ): ใจความเดียว | ความรวม: เชื่อมด้วย และ/แต่/หรือ/จึง | ความซ้อน: มีคำเชื่อม ที่/ซึ่ง/อัน/เพราะ/เมื่อ", backEn = "Simple (1 clause), Compound (และ/แต่/หรือ/จึง), Complex (ที่/ซึ่ง/อัน/ผู้/ว่า/เพราะ).", backZh = "简单句（单主谓）、并列复合句（และ/แต่/หรือ）、主从复合句（ที่/ซึ่ง/อัน/เพราะ）。", exampleText = "'นักเรียนที่ขยันย่อมสอบติด ม.1' = ประโยคความซ้อน"),
        FlashcardEntity(subjectId = "THAI", frontTh = "ฉันทลักษณ์ กาพย์ยานี ๑๑", frontEn = "Kap Yani 11 Poetic Meter", frontZh = "泰语诗歌体裁：กาพย์ยานี ๑๑", backTh = "วรรคหน้า 5 คำ วรรคหลัง 6 คำ (รวม 11 คำ) สัมผัสคำสุดท้ายวรรค 1 ไปคำที่ 3 วรรค 2", backEn = "5 syllables in first hemistich + 6 in second (= 11 syllables per line).", backZh = "前句5音节，后句6音节，每行共11音节。", exampleText = "วิชาเหมือนสินค้า (5) / อันมีค่าอยู่เมืองไกล (6)")
    )

    fun defaultQuestions(): List<QuestionEntity> {
        var qNum = 1
        fun q(
            sub: SubjectId,
            topic: String,
            diff: String,
            fmt: String,
            qTh: String, qEn: String, qZh: String,
            aTh: String, bTh: String, cTh: String, dTh: String,
            aEn: String, bEn: String, cEn: String, dEn: String,
            aZh: String, bZh: String, cZh: String, dZh: String,
            ans: String,
            textAns: String = "",
            expTh: String, expEn: String, expZh: String,
            h1: String, h2: String
        ) = QuestionEntity(
            questionNumber = qNum++,
            subjectId = sub.id,
            topic = topic,
            difficulty = diff,
            questionFormat = fmt,
            questionTh = qTh, questionEn = qEn, questionZh = qZh,
            choiceATh = aTh, choiceBTh = bTh, choiceCTh = cTh, choiceDTh = dTh,
            choiceAEn = aEn, choiceBEn = bEn, choiceCEn = cEn, choiceDEn = dEn,
            choiceAZh = aZh, choiceBZh = bZh, choiceCZh = cZh, choiceDZh = dZh,
            correctAnswer = ans,
            acceptableTextAnswers = textAns,
            explanationTh = expTh, explanationEn = expEn, explanationZh = expZh,
            hint1 = h1, hint2 = h2,
            curriculumStandard = sub.curriculumCode
        )

        return listOf(
            // 1. THAI (ภาษาไทย)
            q(
                SubjectId.THAI, "ชนิดของคำและประโยค", "EASY", "MULTIPLE_CHOICE",
                "ข้อใดมีคำนามบอกลักษณะ (ลักษณนาม) ที่ใช้ถูกต้องตามหลักภาษาไทย?",
                "Which Thai classifier phrase uses the grammatically correct classifier?",
                "下列哪一项使用了正确的泰语量词？",
                "ขลุ่ย 2 เลา", "เลื่อย 1 แผ่น", "แห 3 ผืน", "ช้างบ้าน 2 โขลง",
                "Khlui flutes: 2 Lao", "Saw: 1 Phaen", "Fishing net: 3 Phuen", "Domesticated elephants: 2 Khlong",
                "笛子：2管（เลา）", "锯子：1片", "渔网：3块", "家象：2群",
                "A", "",
                "ขลุ่ยและปี่ใช้ลักษณนามว่า 'เลา' ส่วนเลื่อยใช้ 'ปื้น' แหใช้ 'ปาก' และช้างบ้านรายตัวใช้ 'เชือก'",
                "Traditional Thai flutes (Khlui) take the classifier 'Lao'. Saws use 'Puen', nets use 'Pak', and tame elephants use 'Chueak'.",
                "泰语中笛子使用量词“เลา”，锯子用“ปื้น”，渔网用“ปาก”，家象用“เชือก”。",
                "นึกถึงลักษณนามของเครื่องดนตรีประเภทเป่า", "ขลุ่ยและปี่มีลักษณนามเฉพาะตัวคือคำว่า เลา"
            ),
            q(
                SubjectId.THAI, "การอ่านจับใจความและสำนวน", "ENTRANCE_EXAM", "READING",
                "สำนวน 'น้ำลดตอผุด' สื่อความหมายตรงกับสถานการณ์ใดมากที่สุด?",
                "Which situation best matches the Thai idiom 'Nam Lot To Phut' (when water recedes, stumps appear)?",
                "泰语成语“水落桩现”最符合以下哪种情境？",
                "การช่วยเหลือผู้อื่นโดยไม่หวังผลตอบแทน",
                "เมื่อหมดอำนาจหรือพ้นตำแหน่ง ความผิดที่เคยซ่อนไว้จึงปรากฏออกมา",
                "การทำงานเล็กน้อยแต่หวังผลตอบแทนมหาศาล",
                "การเตรียมตัวล่วงหน้าก่อนเกิดปัญหา",
                "Helping others selflessly",
                "Hidden wrongdoings are exposed once someone loses status or protection",
                "Investing tiny effort while expecting huge rewards",
                "Preparing ahead before trouble strikes",
                "无私帮助他人", "失去权势保护后，原先隐瞒的过错暴露出来", "付出极少却奢望巨大回报", "未雨绸缪提前准备",
                "B", "",
                "'น้ำลดตอผุด' หมายถึง เมื่อคนที่มีอำนาจหรือสิ่งที่คอยปกปิดหมดไป ความชั่วหรือความลับที่ซ่อนอยู่ก็เปิดเผยขึ้น",
                "The idiom means once protective power fades away, previously concealed faults become visible.",
                "该成语意指当庇护或权势消退时，原本隐藏的错误或真相便会显露。",
                "เปรียบ 'ตอไม้ใต้น้ำ' เป็นความผิดที่ถูกซ่อนอยู่", "เมื่อระดับน้ำ (อำนาจที่บดบัง) ลดลง สิ่งที่อยู่ใต้น้ำจึงโผล่ให้เห็น"
            ),
            q(
                SubjectId.THAI, "คำเป็นคำตายและประโยคซ้อน", "HARD", "MULTIPLE_CHOICE",
                "คำในข้อใดเป็น 'คำเป็น' ทุกคำ?",
                "Which choice contains ONLY 'live syllables' (Kham Pen) in Thai phonology?",
                "下列哪一组全部属于泰语“活音节词（คำเป็น）”？",
                "เก้าอี้ เวลา แม่น้ำ", "ก้อนเมฆ ดอกไม้ ความรัก", "กล้วยหอม นมสด กระดาษ", "เสือดาว รังนก วันพุธ",
                "Kao-i, We-la, Mae-nam", "Kon-mek, Dok-mai, Khwam-rak", "Kluai-hom, Nom-sot, Kra-dat", "Suea-dao, Rang-nok, Wan-phut",
                "เก้าอี้ เวลา แม่น้ำ", "ก้อนเมฆ ดอกไม้ ความรัก", "กล้วยหอม นมสด กระดาษ", "เสือดาว รังนก วันพุธ",
                "A", "",
                "คำเป็นประสมสระเสียงยาวในแม่ ก กา (เก้า-อี้, เว-ลา, แม่) หรือสะกดด้วยแม่ กน กม เกย เกอว กง และอำ ใอ ไอ เอา (น้ำ)",
                "'เก้าอี้ เวลา แม่น้ำ' all end in long open vowels or live diphthongs/sonorants, making every syllable a live syllable.",
                "เก้าอี้、เวลา、แม่น้ำ 均由长元音或活韵尾构成，全部是活音节。",
                "จำหลัก 'นมยวง' + สระเสียงยาวแม่ ก กา + อำ ใอ ไอ เอา", "ตัดข้อที่มีตัวสะกด กก กด กบ เช่น เมฆ ดอก สด นก พุธ"
            ),

            // 2. MATH (คณิตศาสตร์)
            q(
                SubjectId.MATH, "ห.ร.ม. และ ค.ร.น.", "NORMAL", "WORD_PROBLEM",
                "ระฆัง 3 ใบ ตีทุก ๆ 12 นาที, 18 นาที และ 24 นาที ถ้าตีพร้อมกันครั้งแรกเวลา 08:00 น. จะตีพร้อมกันครั้งต่อไปเวลาใด?",
                "Three bells ring every 12, 18, and 24 minutes. If they ring together at 08:00, when will they next ring simultaneously?",
                "三口钟分别每12分钟、18分钟、24分钟敲响一次。若08:00同时敲响，下次同时敲响是几点？",
                "08:48 น.", "09:12 น.", "09:18 น.", "09:24 น.",
                "08:48", "09:12", "09:18", "09:24",
                "08:48", "09:12", "09:18", "09:24",
                "B", "09:12|9:12",
                "หา ค.ร.น. ของ 12, 18 และ 24 ได้เท่ากับ 72 นาที (1 ชั่วโมง 12 นาที) ดังนั้น 08:00 + 1 ชม. 12 นาที = 09:12 น.",
                "LCM(12, 18, 24) = 72 minutes = 1 hour 12 minutes. Adding to 08:00 gives 09:12.",
                "12、18、24的最小公倍数是72分钟（1小时12分钟），08:00加上1小时12分钟为09:12。",
                "เหตุการณ์เกิดขึ้นพร้อมกันอีกครั้ง ให้หา ค.ร.น. (ตัวคูณร่วมน้อย)", "ค.ร.น. ของ 12, 18, 24 คือกี่นาที แล้วแปลงเป็นชั่วโมง"
            ),
            q(
                SubjectId.MATH, "ร้อยละและกำไรขาดทุน", "ENTRANCE_EXAM", "CALCULATION",
                "ร้านค้าติดราคากระเป๋านักเรียนสูงกว่าทุน 40% เมื่อขายจริงลดราคาให้ลูกค้า 20% จากป้าย ร้านค้ายังได้กำไรกี่เปอร์เซ็นต์? (พิมพ์เฉพาะตัวเลขได้)",
                "A shop marks a backpack 40% above cost, then gives a 20% discount off the tag price. What is the net profit percentage?",
                "商店将书包按高于成本40%标价，出售时按标价打八折（减20%），最终利润率是多少%？",
                "12", "15", "20", "28",
                "12", "15", "20", "28",
                "12", "15", "20", "28",
                "A", "12|12%",
                "สมมติทุน 100 บาท ติดราคา 140 บาท ลด 20% ของ 140 คือลด 28 บาท ขายจริง 140 - 28 = 112 บาท จึงได้กำไร 12%",
                "Let cost = 100. Marked price = 140. 20% discount = 28. Selling price = 140 − 28 = 112, yielding 12% profit.",
                "设成本100，标价140，减20%即减28，实售112，净赚12%。",
                "สมมติให้ราคาทุนเท่ากับ 100 บาทเสมอจะคิดง่ายที่สุด!", "ลด 20% ต้องคิดจากราคาป้าย 140 บาท ไม่ใช่จากทุน 100 บาท"
            ),
            q(
                SubjectId.MATH, "เรขาคณิตและพื้นที่", "HARD", "MULTIPLE_CHOICE",
                "รูปสามเหลี่ยมมุมฉากมีด้านประกอบมุมฉากยาว 9 ซม. และ 12 ซม. แนบในครึ่งวงกลมพอดีโดยด้านตรงข้ามมุมฉากเป็นเส้นผ่านศูนย์กลาง รัศมีวงกลมยาวกี่ ซม.?",
                "A right triangle with legs 9 cm and 12 cm has its hypotenuse as the diameter of a semicircle. What is the circle's radius?",
                "直角三角形两直角边为9厘米和12厘米，斜边恰为半圆直径，求半径是多少厘米？",
                "6.0 ซม.", "7.5 ซม.", "10.5 ซม.", "15.0 ซม.",
                "6.0 cm", "7.5 cm", "10.5 cm", "15.0 cm",
                "6.0厘米", "7.5厘米", "10.5厘米", "15.0厘米",
                "B", "7.5",
                "จากทฤษฎีบทพีทาโกรัส ด้านตรงข้ามมุมฉาก = √(9² + 12²) = √(81 + 144) = √225 = 15 ซม. รัศมีคือครึ่งหนึ่งของเส้นผ่านศูนย์กลาง = 15 ÷ 2 = 7.5 ซม.",
                "Hypotenuse = √(9² + 12²) = 15 cm. Since the hypotenuse is the diameter, radius = 15 / 2 = 7.5 cm.",
                "斜边长为 √(81+144)=15厘米，直径15厘米，故半径为7.5厘米。",
                "อัตราส่วนสามเหลี่ยมมุมฉาก 3 : 4 : 5 คูณด้วย 3 ได้ 9 : 12 : 15", "เส้นผ่านศูนย์กลางยาว 15 ซม. อย่าลืมหาร 2 เพื่อหารัศมี!"
            ),

            // 3. SCIENCE (วิทยาศาสตร์)
            q(
                SubjectId.SCIENCE, "ระบบร่างกายมนุษย์", "NORMAL", "MULTIPLE_CHOICE",
                "หลอดเลือดใดในร่างกายมนุษย์ที่มีเลือดที่มีปริมาณแก๊สออกซิเจนสูงไหลเวียนอยู่ แม้จะชื่อว่าหลอดเลือดเวน?",
                "Which blood vessel carries oxygen-rich blood back to the heart despite being classified as a vein?",
                "人体哪条血管虽然被称为静脉，却输送富含氧气的血液？",
                "ซุพีเรียเวนาคาวา", "พัลโมนารีเวน (หลอดเลือดดำปอด)", "พัลโมนารีอาร์เทอรี", "เอออร์ตา",
                "Superior Vena Cava", "Pulmonary Vein", "Pulmonary Artery", "Aorta",
                "上腔静脉", "肺静脉 (Pulmonary Vein)", "肺动脉", "主动脉",
                "B", "",
                "พัลโมนารีเวนรับเลือดที่ฟอกแล้วจากปอดเข้าสู่หัวใจห้องบนซ้าย จึงมีแก๊สออกซิเจนสูงต่างจากหลอดเลือดเวนอื่น ๆ",
                "The pulmonary vein carries freshly oxygenated blood from the lungs into the left atrium of the heart.",
                "肺静脉将肺部完成气体交换后的富氧血送回左心房。",
                "หลอดเลือดเวนหมายถึงหลอดเลือดที่นำเลือด 'เข้าสู่หัวใจ'", "เลือดที่เพิ่งออกจากปอดเข้าสู่หัวใจจะมีออกซิเจนสูง"
            ),
            q(
                SubjectId.SCIENCE, "สารและการแยกสาร", "ENTRANCE_EXAM", "SITUATIONAL",
                "นักเรียนทำผงตะไบเหล็ก เกลือแกง และทรายละเอียดปนกันในบีกเกอร์ ลำดับวิธีการแยกสารข้อใดเหมาะสมและได้สารครบทั้ง 3 ชนิด?",
                "Iron filings, table salt, and fine sand are mixed together. Which sequence cleanly separates all three substances?",
                "铁屑、食盐和细沙混在一起，哪种分离顺序能完整回收三种物质？",
                "ร่อนด้วยตะแกรง → กรอง → ตกผลึก",
                "ใช้แม่เหล็กดูด → เติมน้ำแล้วกรอง → นำของเหลวไประเหยแห้ง",
                "เติมน้ำแล้วระเหยแห้ง → กรอง → ใช้แม่เหล็กดูด",
                "การระเหิด → การกลั่นลำดับส่วน → การตกตะกอน",
                "Sieving → Filtration → Crystallization",
                "Magnet attraction → Dissolve in water & filter → Evaporate filtrate",
                "Dissolve & evaporate → Filter → Magnet",
                "Sublimation → Fractional distillation → Sedimentation",
                "筛分→过滤→结晶", "磁铁吸引铁屑→加水溶解并过滤出沙→蒸发滤液得食盐", "加水蒸发→过滤→磁铁", "升华→分馏→沉淀",
                "B", "",
                "1) แม่เหล็กดูดผงเหล็กออกก่อน 2) เติมน้ำละลายเกลือแล้วกรองแยกทรายค้างบนกระดาษกรอง 3) นำน้ำเกลือไประเหยแห้งได้เกลือแกง",
                "First attract iron filings with a magnet, then dissolve salt in water and filter out sand, finally evaporate water to recover salt.",
                "先用磁铁吸出铁屑，再加水溶解食盐后过滤分离细沙，最后蒸发食盐水结晶。",
                "ผงตะไบเหล็กมีสมบัติแม่เหล็ก ควรดึงออกตอนแห้งก่อนเติมน้ำ", "เกลือละลายน้ำได้แต่ทรายไม่ละลายน้ำ"
            ),
            q(
                SubjectId.SCIENCE, "ไฟฟ้าและวงจร", "HARD", "MULTIPLE_CHOICE",
                "หลอดไฟเหมือนกันทุกประการ 3 ดวง ต่อแบบขนานเข้ากับถ่านไฟฉาย หากหลอดดวงที่ 2 ไส้หลอดขาด หลอดที่เหลือจะเป็นอย่างไร?",
                "Three identical bulbs are connected in parallel to a battery. If bulb #2 burns out, what happens to bulbs #1 and #3?",
                "三个相同灯泡并联在电池上，若第2个灯丝烧断，其余两个灯泡会怎样？",
                "ดับทั้งหมดทันที", "สว่างเท่าเดิมทั้งสองดวง", "สว่างลดลงครึ่งหนึ่ง", "กะพริบสลับกัน",
                "All go out immediately", "Both remain lit at the same brightness", "Brightness drops by half", "They blink alternately",
                "全部熄灭", "其余两盏保持原有亮度正常发光", "亮度减半", "交替闪烁",
                "B", "",
                "การต่อแบบขนาน แรงดันไฟฟ้าตกคร่อมแต่ละสาขาเท่ากันและแยกทางเดินกระแสอิสระ เมื่อหลอดหนึ่งขาดอีกสองดวงจึงยังครบวงจรและสว่างเท่าเดิม",
                "In a parallel circuit each branch receives the full source voltage independently, so remaining branches stay lit unchanged.",
                "并联电路各支路独立承受相同电压，一支路断路不影响其他支路。",
                "นึกถึงไฟบ้านที่ต่อแบบขนาน เวลาปิดไฟห้องหนึ่ง อีกห้องดับหรือไม่", "แต่ละหลอดมีเส้นทางกระแสไฟฟ้าแยกเป็นของตนเอง"
            ),

            // 4. ENGLISH (ภาษาอังกฤษ)
            q(
                SubjectId.ENGLISH, "Grammar & Tenses", "NORMAL", "MULTIPLE_CHOICE",
                "Choose the correct form: 'Look at those dark clouds! It ______ heavily in a few minutes.'",
                "Choose the correct form: 'Look at those dark clouds! It ______ heavily in a few minutes.'",
                "选择正确时态：'Look at those dark clouds! It ______ heavily in a few minutes.'",
                "rains", "rained", "is going to rain", "has rained",
                "rains", "rained", "is going to rain", "has rained",
                "rains", "rained", "is going to rain", "has rained",
                "C", "",
                "ใช้ 'be going to + V.inf' เมื่อคาดการณ์สิ่งที่จะเกิดขึ้นในอนาคตอันใกล้โดยมีหลักฐานให้เห็นชัดเจนในปัจจุบัน (เมฆดำมืด)",
                "Use 'be going to' for predictions supported by clear present visual evidence ('Look at those dark clouds!').",
                "当有明显眼前迹象（乌云密布）预示即将发生某事时，使用 be going to + 动词原形。",
                "สังเกตประโยคแรกมีหลักฐานชัดเจนตรงหน้า (Look at those dark clouds!)", "การคาดการณ์จากหลักฐานที่เห็นตำตา ใช้ is going to + กริยาช่อง 1"
            ),
            q(
                SubjectId.ENGLISH, "Conditionals & Reading", "ENTRANCE_EXAM", "MULTIPLE_CHOICE",
                "If Ploy ______ earlier last night, she wouldn't feel sleepy during the morning exam today.",
                "If Ploy ______ earlier last night, she wouldn't feel sleepy during the morning exam today.",
                "选择最恰当的虚拟语气形式：If Ploy ______ earlier last night, she wouldn't feel sleepy today.",
                "sleeps", "slept", "had slept", "will sleep",
                "sleeps", "slept", "had slept", "will sleep",
                "sleeps", "slept", "had slept", "will sleep",
                "C", "",
                "เหตุการณ์สมมติตรงข้ามความจริงในอดีต ('last night') ใช้โครงสร้าง If + Past Perfect (had + V.3) คู่กับผลลัพธ์ปัจจุบัน (wouldn't + V.1)",
                "The condition refers to an unreal past event ('last night'), requiring Past Perfect ('had slept') in a Mixed Conditional.",
                "从句时间状语为 last night（与过去事实相反），需用过去完成时 had slept。",
                "ดูคำบอกเวลาใน If-clause คือคำว่า last night (อดีตที่ผ่านไปแล้ว)", "สมมติเรื่องในอดีตที่ไม่เป็นจริง ใช้ had + กริยาช่อง 3"
            ),

            // 5. SOCIAL STUDIES (สังคมศึกษาฯ)
            q(
                SubjectId.SOCIAL, "เศรษฐศาสตร์เบื้องต้น", "NORMAL", "SITUATIONAL",
                "ในช่วงหน้าร้อน ความต้องการซื้อทุเรียนเพิ่มขึ้นมากในขณะที่ผลผลิตออกสู่ตลาดน้อยลง ตามกลไกราคาจะเกิดผลอย่างไร?",
                "During summer, consumer demand for durian surges while harvest supply drops. According to price mechanism, what happens?",
                "夏季榴莲市场需求激增而产量减少，根据价格机制会发生什么？",
                "ราคาดุลยภาพลดลง", "ราคาทุเรียนในตลาดปรับตัวสูงขึ้น", "เกิดภาวะสินค้าล้นตลาด", "ผู้ผลิตลดการปลูกในระยะยาวทันที",
                "Equilibrium price falls", "Market price of durian rises", "Market surplus occurs", "Producers permanently quit growing",
                "均衡价格下降", "市场榴莲价格上涨", "出现供过于求过剩", "果农立即停止种植",
                "B", "",
                "เมื่ออุปสงค์ (ความต้องการซื้อ) มากกว่าอุปทาน (ปริมาณเสนอขาย) จะเกิดภาวะสินค้าขาดตลาด ทำให้ราคาปรับตัวสูงขึ้น",
                "Higher demand paired with lower supply creates excess demand (shortage), driving the market price upward.",
                "需求大于供给导致供不应求，市场价格随之上升。",
                "เปรียบเทียบฝั่งคนซื้อ (มาก) กับฝั่งของที่มีขาย (น้อย)", "เมื่อของหายากแต่คนอยากซื้อเยอะ ราคาจะขยับขึ้นหรือลง?"
            ),
            q(
                SubjectId.SOCIAL, "ภูมิศาสตร์ไทย", "HARD", "MULTIPLE_CHOICE",
                "ภาคตะวันตกของประเทศไทยมีลักษณะภูมิประเทศคล้ายคลึงกับภาคใดมากที่สุด เพราะมีทิวเขาถนนธงชัยและตะนาวศรีต่อเนื่องกัน?",
                "Which region of Thailand shares the most similar mountainous terrain with the Western region?",
                "泰国西部地区因山脉延续，其地形特征与哪个地区最为相似？",
                "ภาคเหนือ", "ภาคตะวันออกเฉียงเหนือ", "ภาคกลางตอนล่าง", "ภาคตะวันออก",
                "Northern Region", "Northeastern (Isan) Region", "Lower Central Plains", "Eastern Region",
                "北部地区", "东北部地区", "中部平原南段", "东部地区",
                "A", "",
                "ภาคตะวันตกมีทิวเขาสลับหุบเขาแคบต่อเนื่องลงมาจากทิวเขาภาคเหนือ จึงมีลักษณะภูมิประเทศคล้ายภาคเหนือมากที่สุด",
                "Western Thailand's fold mountain ranges continue directly southward from Northern Thailand's highlands.",
                "泰国西部山脉与狭谷自北部山系向南延伸，因此地形与北部最相似。",
                "ดูแนวทิวเขาถนนธงชัยที่ทอดยาวลงมาจากทางเหนือ", "เป็นภูเขาสูงสลับหุบเขาเหมือนภาคเหนือ"
            ),

            // 6. HISTORY (ประวัติศาสตร์)
            q(
                SubjectId.HISTORY, "วิธีการทางประวัติศาสตร์", "NORMAL", "ORDERING",
                "ข้อใดเรียงลำดับ 'วิธีการทางประวัติศาสตร์ 5 ขั้นตอน' ได้ถูกต้องตั้งแต่เริ่มต้นจนจบ?",
                "Which choice correctly orders the 5 steps of the Historical Method from start to finish?",
                "下列哪项正确排列了“历史研究方法五步骤”？",
                "รวบรวมหลักฐาน → กำหนดหัวข้อ → ประเมินหลักฐาน → วิเคราะห์สังเคราะห์ → เรียบเรียงนำเสนอ",
                "กำหนดหัวข้อ → รวบรวมหลักฐาน → ประเมินคุณค่าหลักฐาน → วิเคราะห์สังเคราะห์ → เรียบเรียงนำเสนอ",
                "กำหนดหัวข้อ → วิเคราะห์สังเคราะห์ → รวบรวมหลักฐาน → ประเมินหลักฐาน → เรียบเรียงนำเสนอ",
                "ประเมินหลักฐาน → กำหนดหัวข้อ → รวบรวมหลักฐาน → เรียบเรียงนำเสนอ → วิเคราะห์",
                "Collect → Topic → Evaluate → Analyze → Present",
                "Define Topic → Collect Evidence → Evaluate Evidence → Analyze/Synthesize → Present",
                "Define Topic → Analyze → Collect → Evaluate → Present",
                "Evaluate → Topic → Collect → Present → Analyze",
                "收集→选题→评估→分析→呈现", "确定主题→收集史料→考证评估史料→分析综合→整理呈现", "选题→分析→收集→评估→呈现", "评估→选题→收集→呈现→分析",
                "B", "",
                "ขั้นตอนทางประวัติศาสตร์: 1.กำหนดประเด็นศึกษา 2.รวบรวมหลักฐาน 3.ประเมินคุณค่าหลักฐาน(วิพากษ์วิธี) 4.วิเคราะห์ตีความสังเคราะห์ 5.เรียบเรียงนำเสนอ",
                "The 5 steps: 1) Define question, 2) Gather sources, 3) Authenticate/evaluate sources, 4) Analyze & synthesize, 5) Present findings.",
                "历史五步法：确定课题→搜集史料→考证史料真伪价值→分析综合→撰写呈现。",
                "ก่อนจะไปหาหลักฐาน ต้องรู้ก่อนว่าจะศึกษาเรื่องอะไร", "เมื่อได้หลักฐานมาแล้ว ต้องตรวจสอบความน่าเชื่อถือก่อนนำไปวิเคราะห์"
            ),
            q(
                SubjectId.HISTORY, "สมัยรัตนโกสินทร์", "ENTRANCE_EXAM", "MULTIPLE_CHOICE",
                "การทำสนธิสัญญาเบาว์ริงในสมัยรัชกาลที่ 4 ส่งผลให้เศรษฐกิจไทยเปลี่ยนแปลงอย่างไรเป็นสำคัญ?",
                "How did the Bowring Treaty during King Rama IV's reign fundamentally transform Siam's economy?",
                "拉玛四世时期的《鲍林条约》对暹罗经济产生了什么关键转变？",
                "เปลี่ยนจากเศรษฐกิจแบบพอยังชีพไปสู่เศรษฐกิจการค้าเสรีและส่งออกข้าวเป็นสินค้าหลัก",
                "ยกเลิกการค้าขายกับชาติตะวันตกทั้งหมด",
                "ผูกขาดการค้าต่างประเทศโดยพระคลังสินค้าเข้มงวดกว่าเดิม",
                "เปลี่ยนมาใช้ระบบแลกเปลี่ยนสิ่งของแทนเงินตรา",
                "Shifted from subsistence agriculture to free international trade centered on rice exports",
                "Ended all trade with Western nations",
                "Strengthened Royal Warehouse trade monopolies",
                "Replaced currency with barter",
                "从自给自足经济转向自由贸易并以大米出口为支柱", "停止与西方贸易", "加强皇家仓库垄断", "恢复以物易物",
                "A", "",
                "สนธิสัญญาเบาว์ริง (พ.ศ. 2398) ยกเลิกการผูกขาดของพระคลังสินค้า เก็บภาษีขาเข้าร้อยละ 3 ทำให้ไทยเข้าสู่ระบบการค้าเสรีและขยายการปลูกข้าวเพื่อส่งออก",
                "Signed in 1855, the Bowring Treaty abolished royal monopolies, fixed import tariffs at 3%, and propelled Siam into global rice export trade.",
                "1855年《鲍林条约》取消皇家贸易垄断，开启自由贸易，大米成为头号出口商品。",
                "นึกถึงบทบาทของพระคลังสินค้าที่เปลี่ยนไปในสมัยรัชกาลที่ 4", "ชาวนาขยายพื้นที่ปลูกข้าวเพื่อขายต่างประเทศมากขึ้น"
            ),

            // 7. HEALTH (สุขศึกษา)
            q(
                SubjectId.HEALTH, "การปฐมพยาบาลและความปลอดภัย", "EASY", "SITUATIONAL",
                "หากเพื่อนวิ่งหกล้มข้อเท้าแพลงและบวมแดงใหม่ ๆ ระหว่างเล่นกีฬา ควรปฐมพยาบาลเบื้องต้นอย่างไรใน 24 ชั่วโมงแรก?",
                "If a classmate sprains an ankle with fresh swelling during sports, what is the proper first aid within the first 24 hours?",
                "同学运动时不慎扭伤脚踝并出现红肿，前24小时内应如何正确急救？",
                "นวดบีบแรง ๆ ให้เส้นคลายทันที",
                "ประคบด้วยน้ำร้อนจัดทันที",
                "พักการใช้ข้อเท้า ประคบเย็น และยกปลายเท้าสูงเล็กน้อย (หลัก RICE)",
                "ให้วิ่งต่อเบา ๆ เพื่อให้เลือดสูบฉีด",
                "Massage vigorously right away",
                "Apply a hot compress immediately",
                "Rest the joint, apply a cold pack, and elevate the foot (RICE principle)",
                "Keep jogging to boost blood flow",
                "用力揉搓推拿", "立即热敷", "停止活动、冰敷并适当抬高患肢（RICE原则）", "继续慢跑促进血液循环",
                "C", "",
                "ข้อเท้าแพลงใหม่ ๆ ใน 24–48 ชม. แรกใช้หลัก RICE (Rest พัก, Ice ประคบเย็นให้หลอดเลือดหดตัวลดบวม, Compression พันผ้ายืด, Elevation ยกสูง)",
                "Fresh sprains use RICE: Rest, Ice (reduces bleeding/swelling), Compression, and Elevation during the first 24–48 hours.",
                "急性扭伤前24-48小时应遵循RICE原则：休息、冰敷消肿、加压包扎与抬高。",
                "แผลบวมฟกช้ำใหม่ ๆ ห้ามประคบร้อนหรือนวดแรงเด็ดขาด", "ความเย็นช่วยให้หลอดเลือดฝอยหดตัวและลดอาการบวม"
            ),

            // 8. ART (ศิลปะ)
            q(
                SubjectId.ART, "ทัศนศิลป์และทฤษฎีสี", "EASY", "MATCHING",
                "คู่สีใดต่อไปนี้จัดเป็น 'สีคู่ตรงข้าม (Complementary Colors)' ตามวงจรสีธรรมชาติอย่างถูกต้อง?",
                "Which color pair correctly represents true Complementary Colors on the standard color wheel?",
                "在标准色轮上，下列哪一组属于正确的“互补色”？",
                "สีเหลือง – สีม่วง", "สีแดง – สีส้ม", "สีน้ำเงิน – สีเขียว", "สีม่วง – สีชมพู",
                "Yellow – Violet (Purple)", "Red – Orange", "Blue – Green", "Purple – Pink",
                "黄色 – 紫色", "红色 – 橙色", "蓝色 – 绿色", "紫色 – 粉色",
                "A", "",
                "สีคู่ตรงข้ามในวงจรสีหลัก ได้แก่ แดง–เขียว, เหลือง–ม่วง และ น้ำเงิน–ส้ม ซึ่งให้ความตัดกันโดดเด่นที่สุด",
                "Primary-secondary complementary pairs on the color wheel are Yellow–Purple, Red–Green, and Blue–Orange.",
                "标准互补色对包括：黄—紫、红—绿、蓝—橙。",
                "สีคู่ตรงข้ามคือสีที่อยู่ตรงข้ามกันพอดีในวงจรสี 12 สี", "แม่สี 'เหลือง' อยู่ตรงข้ามกับสีขั้นที่สองที่เกิดจาก แดง+น้ำเงิน"
            ),

            // 9. GENERAL KNOWLEDGE & ANALYTICAL THINKING (ความรู้รอบตัวและการคิดวิเคราะห์)
            q(
                SubjectId.GENERAL_ANALYTICAL, "การวิเคราะห์เหตุผลและสิ่งแวดล้อม", "NORMAL", "SITUATIONAL",
                "การกระทำในข้อใดช่วยลดการปล่อยก๊าซเรือนกระจกตามหลักเศรษฐกิจหมุนเวียน (Circular Economy) ที่ต้นเหตุได้ดีที่สุด?",
                "Which daily habit best reduces greenhouse gas emissions at the source under Circular Economy principles?",
                "根据循环经济理念，哪项行为最能从源头减少温室气体排放？",
                "ซื้อน้ำดื่มขวดพลาสติกใหม่ทุกครั้งแล้วทิ้งลงถังทั่วไป",
                "พกกระบอกน้ำส่วนตัวไปเติมน้ำดื่มและใช้ถุงผ้าซ้ำแทนการใช้ครั้งเดียวทิ้ง",
                "เผาขยะพลาสติกหลังบ้านเพื่อลดปริมาณขยะ",
                "เปิดเครื่องปรับอากาศทิ้งไว้เพื่อให้ห้องเย็นตลอดวัน",
                "Buying single-use plastic bottles daily",
                "Carrying a reusable bottle and cloth bag to prevent single-use waste",
                "Burning plastic waste in the backyard",
                "Leaving air conditioning running in an empty room",
                "每天购买一次性塑料瓶装水", "自带水杯与环保袋重复使用，从源头减少一次性垃圾", "露天焚烧塑料垃圾", "全天开着空调不关",
                "B", "",
                "การลดการใช้ตั้งแต่ต้นทาง (Reduce) และการใช้ซ้ำ (Reuse) ใช้พลังงานน้อยกว่าการนำไปรีไซเคิลหรือกำจัดปลายทางมากที่สุด",
                "Source reduction (Reduce & Reuse) saves manufacturing and disposal emissions far more effectively than downstream waste handling.",
                "源头减量（Reduce）与重复使用（Reuse）是循环经济中最高效低碳的方式。",
                "หลัก 3Rs ข้อแรกสุดที่มีประสิทธิภาพสูงสุดคือ Reduce (ลดการสร้างขยะ)", "การพกภาชนะใช้ซ้ำตัดวงจรการผลิตพลาสติกแบบครั้งเดียวทิ้ง"
            ),

            // 10. IQ & LOGIC (เชาวน์ปัญญาและตรรกะ)
            q(
                SubjectId.IQ_LOGIC, "อนุกรมตัวเลขและแบบรูป", "ENTRANCE_EXAM", "CALCULATION",
                "จงหาจำนวนถัดไปของอนุกรม: 3, 7, 15, 31, 63, ___",
                "Find the next number in the sequence: 3, 7, 15, 31, 63, ___",
                "找出数列的下一个数字：3, 7, 15, 31, 63, ___",
                "95", "125", "127", "129",
                "95", "125", "127", "129",
                "95", "125", "127", "129",
                "C", "127",
                "ความสัมพันธ์คือ คูณ 2 แล้วบวก 1 (หรือผลต่างเพิ่มขึ้นเป็น 4, 8, 16, 32, 64) ดังนั้น 63 × 2 + 1 = 127",
                "Each term follows ×2 + 1 (differences: +4, +8, +16, +32, +64). Thus 63 × 2 + 1 = 127.",
                "规律为前一项×2+1（差值依次为4, 8, 16, 32, 64），因此 63×2+1 = 127。",
                "ลองหาผลต่างระหว่างพจน์ติดกัน: 7-3=4, 15-7=8, 31-15=16, 63-31=32", "ผลต่างเพิ่มเป็น 2 เท่าทุกครั้ง ถัดจาก +32 คือ +64"
            ),
            q(
                SubjectId.IQ_LOGIC, "ตรรกศาสตร์และเงื่อนไข", "EXPERT", "WORD_PROBLEM",
                "ในการวิ่งแข่ง มินเข้าเส้นชัยก่อนฟ้า, ก้องเข้าเส้นชัยหลังฟ้าแต่ก่อนต้น, และแพรเข้าเส้นชัยก่อนมิน ใครเข้าเส้นชัยเป็นอันดับที่ 2?",
                "In a race: Min finishes before Fah, Kong finishes after Fah but before Ton, and Prae finishes before Min. Who finishes 2nd?",
                "赛跑中：Min比Fah先到，Kong在Fah之后但在Ton之前到，Prae比Min先到。请问谁是第2名？",
                "แพร (Prae)", "มิน (Min)", "ฟ้า (Fah)", "ก้อง (Kong)",
                "Prae", "Min", "Fah", "Kong",
                "Prae", "Min", "Fah", "Kong",
                "B", "",
                "เรียงลำดับจากเร็วสุดไปช้าสุด: แพร → มิน → ฟ้า → ก้อง → ต้น ดังนั้นอันดับที่ 2 คือ มิน",
                "Order from 1st to 5th: Prae → Min → Fah → Kong → Ton. Therefore, 2nd place is Min.",
                "名次从第1到第5依次为：Prae → Min → Fah → Kong → Ton，第2名是Min。",
                "เขียนเรียงจากซ้ายไปขวา เริ่มจาก 'มิน → ฟ้า'", "แพรมาก่อนมิน จึงอยู่หน้าสุด ทำให้มินขยับเป็นอันดับ 2"
            )
        )
    }

    fun defaultRivalLeaderboardEntries(): List<LeaderboardEntryEntity> {
        val rivals = listOf(
            "SkyOwl_M1" to 0,
            "NeonMathPro" to 1,
            "PinkStarQuiz" to 2,
            "SciGalaxy99" to 3,
            "PixelScholar" to 4,
            "CrystalFox" to 5,
            "MintDragon" to 6,
            "LogicKnight" to 7,
            "SolarAce" to 8
        )
        val entries = mutableListOf<LeaderboardEntryEntity>()
        var fakeUid = -100L

        // Overall
        rivals.forEachIndexed { idx, (name, av) ->
            val sc = 4200 - idx * 390
            entries.add(
                LeaderboardEntryEntity(
                    boardId = "OVERALL_TOTAL",
                    userId = fakeUid--,
                    username = name,
                    avatarIndex = av,
                    score = sc,
                    rankTitle = ScoringAndPetEngine.rankTitleForScore(sc)
                )
            )
        }
        // Per Subject
        SubjectId.entries.forEachIndexed { sIdx, sub ->
            rivals.take(6).forEachIndexed { rIdx, (name, av) ->
                val sc = 950 - rIdx * 120 + (sIdx * 15)
                entries.add(
                    LeaderboardEntryEntity(
                        boardId = "SUBJECT_${sub.id}",
                        userId = fakeUid--,
                        username = "${name}_${sub.id.take(2)}",
                        avatarIndex = (av + sIdx) % 6,
                        score = sc,
                        rankTitle = ScoringAndPetEngine.rankTitleForScore(sc)
                    )
                )
            }
        }
        // Per Mode
        listOf("MODE_PRACTICE", "MODE_EXAM", "MODE_ENTRANCE", "MODE_BOSS_RAID").forEachIndexed { mIdx, modeId ->
            rivals.take(6).forEachIndexed { rIdx, (name, av) ->
                val sc = 1400 - rIdx * 170 + mIdx * 80
                entries.add(
                    LeaderboardEntryEntity(
                        boardId = modeId,
                        userId = fakeUid--,
                        username = name,
                        avatarIndex = av,
                        score = sc,
                        rankTitle = ScoringAndPetEngine.rankTitleForScore(sc)
                    )
                )
            }
        }
        // Per Mini-Game
        MiniGameId.entries.forEachIndexed { gIdx, mg ->
            rivals.take(6).forEachIndexed { rIdx, (name, av) ->
                val sc = 820 - rIdx * 95 + gIdx * 20
                entries.add(
                    LeaderboardEntryEntity(
                        boardId = "GAME_${mg.id}",
                        userId = fakeUid--,
                        username = name,
                        avatarIndex = (av + gIdx) % 6,
                        score = sc,
                        rankTitle = ScoringAndPetEngine.rankTitleForScore(sc)
                    )
                )
            }
        }
        return entries
    }
}
