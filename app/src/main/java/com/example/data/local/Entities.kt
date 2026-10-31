package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val username: String,
    val displayName: String,
    val gender: String = "UNSPECIFIED", // MALE, FEMALE, UNSPECIFIED
    val passwordHash: String,
    val salt: String,
    val recoveryCodeHash: String,
    val recoveryCodePlain: String = "", // Editable & viewable by logged-in account owner
    val avatarIndex: Int = 0,
    val equippedFrameId: String = "FRAME_DEFAULT",
    val customTitle: String = "",
    val titleColorIndex: Int = 0,
    val isGuest: Boolean = false,
    val level: Int = 1,
    val xp: Int = 0,
    val ptPoints: Int = 150, // Starting welcome coins; can go negative!
    val streakDays: Int = 0,
    val preMissStreakDays: Int = 0,
    val lastExamDateBangkok: String = "",
    val recoveredDateBangkok: String = "",
    val streakShieldCount: Int = 0,
    val totalCorrect: Int = 0,
    val totalWrong: Int = 0,
    val totalExamsCompleted: Int = 0,
    val totalQuestionsAnswered: Int = 0,
    val totalAnswerTimeSeconds: Int = 0,
    val equippedPetInstanceId: Long? = null,
    val activeBackgroundId: String = "BG_DEFAULT",
    val premiumUiUnlocked: Boolean = false,
    val premiumUiEnabled: Boolean = false,
    val removeWrongUses: Int = 0,
    val timePotionUses: Int = 0,
    val luckyWheelTickets: Int = 1,
    val lastFreeSpinDateBangkok: String = "",
    val mysteryBoxesOwned: Int = 1,
    val bossesDefeatedCount: Int = 0,
    val mistakesMasteredCount: Int = 0,
    val flashcardsReviewedCount: Int = 0,
    val studyTasksCompletedCount: Int = 0,
    val wheelsSpunCount: Int = 0,
    val chestsOpenedCount: Int = 0,
    val dailyQuestRerollDateBangkok: String = "",
    val dailyQuestRerollOffset: Int = 0,
    val targetExamDateBangkok: String = "2027-03-15",
    val targetDreamSchool: String = "โรงเรียนในฝัน ม.1",
    val easyBonusUnlocked: Boolean = false,
    val activeMultiplierItemId: String = "",
    val activeMultiplierGain: Double = 1.0,
    val activeMultiplierPenalty: Int = 1,
    val multiplierExpiresAtMillis: Long = 0L,
    val languageCode: String = "th",
    val bgmEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val bgmVolume: Float = 0.35f,
    val sfxVolume: Float = 0.65f,
    val muteAll: Boolean = false,
    val reducedMotion: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val accountStatus: String = "ACTIVE", // ACTIVE, SUSPENDED, BANNED, DELETED
    val banStartedAtMillis: Long = 0L,
    val banUntilMillis: Long = 0L, // Long.MAX_VALUE for FOREVER
    val banReason: String = "",
    val suspensionRecoveryCompletedCount: Int = 0,
    val suspensionRecoveryRequested: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "questions",
    indices = [Index(value = ["subjectId", "difficulty", "reviewStatus"])]
)
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val questionNumber: Int,
    val subjectId: String,
    val topic: String,
    val gradeLevel: String = "ป.4–ป.6 เตรียมสอบเข้า ม.1",
    val difficulty: String = "NORMAL",
    val questionFormat: String = "MULTIPLE_CHOICE",
    val questionTh: String,
    val questionEn: String,
    val questionZh: String,
    val choiceATh: String,
    val choiceBTh: String,
    val choiceCTh: String,
    val choiceDTh: String,
    val choiceAEn: String,
    val choiceBEn: String,
    val choiceCEn: String,
    val choiceDEn: String,
    val choiceAZh: String,
    val choiceBZh: String,
    val choiceCZh: String,
    val choiceDZh: String,
    val correctAnswer: String, // "A", "B", "C", "D" or exact text/number for fill-in
    val acceptableTextAnswers: String = "", // Pipe-separated acceptable answers for fill-in/calc
    val explanationTh: String,
    val explanationEn: String,
    val explanationZh: String,
    val hint1: String = "",
    val hint2: String = "",
    val curriculumStandard: String = "หลักสูตรแกนกลางฯ พ.ศ. 2551 (ปรับปรุง 2560)",
    val sourceReference: String = "คลังมาตรฐาน PT's Pre-M.1",
    val schoolTag: String = "ทั่วไป",
    val bankName: String = "คลังหลัก PT's",
    val questionVersion: Int = 1,
    val duplicateCheckStatus: String = "VERIFIED_UNIQUE",
    val reviewStatus: String = "APPROVED", // APPROVED, PENDING, REJECTED
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "user_question_history",
    indices = [Index(value = ["userId", "questionId"]), Index(value = ["userId", "dateBangkok"])]
)
data class UserQuestionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val questionId: Long,
    val subjectId: String = "MATH",
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val dateBangkok: String = "",
    val timeSpentSeconds: Int = 12,
    val isMasteredMistake: Boolean = false,
    val answeredAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "exam_sessions",
    indices = [Index(value = ["userId", "isCompleted"])]
)
data class ExamSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val mode: String, // PRACTICE, EXAM, ENTRANCE, MISTAKE_RETRY
    val subjectId: String,
    val difficulty: String,
    val questionIdsCsv: String,
    val currentQuestionIndex: Int = 0,
    val answersData: String = "", // Format: "qId=ans;qId=ans"
    val markedQuestionIdsCsv: String = "",
    val remainingSeconds: Int = 300,
    val totalAllocatedSeconds: Int = 300,
    val petSkillUsesCount: Int = 0,
    val isCompleted: Boolean = false,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val accuracyPercent: Int = 0,
    val maxCombo: Int = 0,
    val xpEarned: Int = 0,
    val ptPointsDelta: Int = 0,
    val timeSpentSeconds: Int = 0,
    val strengthSummary: String = "",
    val improvementSummary: String = "",
    val startedAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis(),
    val completedAtMillis: Long = 0L
)

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["txKey"], unique = true), Index(value = ["userId"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val txKey: String,
    val userId: Long,
    val txType: String,
    val xpDelta: Int = 0,
    val ptPointsDelta: Int = 0,
    val balanceAfter: Int = 0,
    val note: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "shop_purchases",
    indices = [Index(value = ["userId", "itemId"])]
)
data class ShopPurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val itemId: String,
    val pricePaid: Int,
    val purchasedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "owned_backgrounds",
    indices = [Index(value = ["userId", "backgroundId"], unique = true)]
)
data class OwnedBackgroundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val backgroundId: String,
    val acquiredAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "owned_frames",
    indices = [Index(value = ["userId", "frameId"], unique = true)]
)
data class OwnedFrameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val frameId: String,
    val acquiredAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "flashcards",
    indices = [Index(value = ["subjectId"])]
)
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long = 0L, // 0L = global built-in flashcard, >0L = user custom card
    val subjectId: String,
    val frontTh: String,
    val frontEn: String,
    val frontZh: String,
    val backTh: String,
    val backEn: String,
    val backZh: String,
    val exampleText: String = "",
    val masteredByUserIdsCsv: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "study_plan_tasks",
    indices = [Index(value = ["userId", "targetDateBangkok"])]
)
data class StudyPlanTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val subjectId: String,
    val title: String,
    val targetMinutes: Int = 25,
    val targetDateBangkok: String,
    val isCompleted: Boolean = false,
    val rewardClaimed: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "pet_inventory",
    indices = [Index(value = ["userId"])]
)
data class PetInstanceEntity(
    @PrimaryKey(autoGenerate = true) val instanceId: Long = 0L,
    val userId: Long,
    val speciesId: String,
    val rarityId: String,
    val obtainedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "egg_history",
    indices = [Index(value = ["userId"])]
)
data class EggHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val eggRarityId: String,
    val petSpeciesId: String,
    val costPt: Int = 500,
    val openedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "quest_definitions")
data class QuestDefinitionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val questCode: String,
    val questScope: String, // DAILY or WEEKLY
    val titleTh: String,
    val titleEn: String,
    val titleZh: String,
    val descTh: String,
    val descEn: String,
    val descZh: String,
    val metricType: String,
    val targetCount: Int,
    val rewardXp: Int,
    val rewardPt: Int,
    val isActive: Boolean = true
)

@Entity(
    tableName = "user_quest_progress",
    indices = [Index(value = ["userId", "questId", "periodKey"], unique = true)]
)
data class UserQuestProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val questId: Long,
    val periodKey: String,
    val currentCount: Int = 0,
    val isClaimed: Boolean = false,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievement_definitions")
data class AchievementDefEntity(
    @PrimaryKey val code: String,
    val titleTh: String,
    val titleEn: String,
    val titleZh: String,
    val descTh: String,
    val descEn: String,
    val descZh: String,
    val rewardXp: Int,
    val rewardPt: Int
)

@Entity(
    tableName = "user_achievements",
    indices = [Index(value = ["userId", "achievementCode"], unique = true)]
)
data class UserAchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val achievementCode: String,
    val unlockedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "leaderboard_entries",
    indices = [Index(value = ["boardId", "username"], unique = true), Index(value = ["boardId", "score"])]
)
data class LeaderboardEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val boardId: String,
    val userId: Long,
    val username: String,
    val avatarIndex: Int = 0,
    val score: Int = 0,
    val rankTitle: String = "Bronze",
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "notifications",
    indices = [Index(value = ["userId"])]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val category: String,
    val message: String,
    val isRead: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "study_sheets",
    indices = [Index(value = ["userId"])]
)
data class StudySheetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val subjectId: String,
    val title: String,
    val sourceType: String,
    val summaryContent: String,
    val keyPointsText: String,
    val sampleProblemText: String,
    val quizQuestionsJson: String,
    val quizCount: Int = 5,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "tutor_messages",
    indices = [Index(value = ["userId"])]
)
data class TutorMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val role: String,
    val content: String,
    val modelTag: String = "gemini-3.8-flash",
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val adminId: String = "SUPER_ADMIN",
    val targetUsername: String,
    val actionType: String,
    val oldValue: String,
    val newValue: String,
    val reason: String,
    val result: String = "SUCCESS",
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "feedback_reports")
data class FeedbackReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val username: String,
    val category: String,
    val message: String,
    val isResolved: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "system_config")
data class SystemConfigEntity(
    @PrimaryKey val configKey: String,
    val configValue: String
)
