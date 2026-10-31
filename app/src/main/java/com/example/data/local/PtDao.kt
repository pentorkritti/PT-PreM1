package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PtDao {
    // --- Users ---
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUserById(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) AND accountStatus != 'DELETED' LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE accountStatus != 'DELETED' ORDER BY createdAtMillis DESC")
    fun observeAllActiveUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users WHERE accountStatus != 'DELETED'")
    fun observeTotalUsersCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Questions & History ---
    @Query("SELECT * FROM questions WHERE reviewStatus = 'APPROVED' ORDER BY id ASC")
    fun observeApprovedQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY createdAtMillis DESC")
    fun observeAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE reviewStatus = 'APPROVED' AND subjectId = :subjectId")
    suspend fun getApprovedQuestionsBySubject(subjectId: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE reviewStatus = 'APPROVED'")
    suspend fun getAllApprovedQuestionsOnce(): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    suspend fun getQuestionsByIds(ids: List<Long>): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE id = :questionId")
    suspend fun deleteQuestion(questionId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionHistory(entry: UserQuestionHistoryEntity)

    @Query("SELECT questionId FROM user_question_history WHERE userId = :userId")
    suspend fun getSeenQuestionIds(userId: Long): List<Long>

    @Query("SELECT * FROM user_question_history WHERE userId = :userId ORDER BY answeredAtMillis DESC")
    fun observeUserQuestionHistory(userId: Long): Flow<List<UserQuestionHistoryEntity>>

    @Query("SELECT * FROM user_question_history WHERE userId = :userId ORDER BY answeredAtMillis DESC")
    suspend fun getUserQuestionHistoryOnce(userId: Long): List<UserQuestionHistoryEntity>

    @Query("UPDATE user_question_history SET isMasteredMistake = 1 WHERE userId = :userId AND questionId = :questionId AND isCorrect = 0")
    suspend fun markQuestionMistakeMastered(userId: Long, questionId: Long)

    // --- Exam Sessions (Auto-Save & Results) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExamSession(session: ExamSessionEntity): Long

    @Query("SELECT * FROM exam_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getExamSessionById(sessionId: Long): ExamSessionEntity?

    @Query("SELECT * FROM exam_sessions WHERE userId = :userId AND isCompleted = 0 ORDER BY updatedAtMillis DESC LIMIT 1")
    suspend fun getLatestIncompleteSession(userId: Long): ExamSessionEntity?

    @Query("SELECT * FROM exam_sessions WHERE userId = :userId AND isCompleted = 1 ORDER BY completedAtMillis DESC")
    fun observeCompletedExams(userId: Long): Flow<List<ExamSessionEntity>>

    @Query("SELECT COUNT(*) FROM exam_sessions WHERE isCompleted = 1")
    fun observeTotalCompletedExamsCount(): Flow<Int>

    // --- Atomic Ledger Transactions ---
    @Query("SELECT * FROM transactions WHERE txKey = :txKey LIMIT 1")
    suspend fun getTransactionByKey(txKey: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(tx: TransactionEntity): Long

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAtMillis DESC LIMIT 80")
    fun observeUserTransactions(userId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT COALESCE(SUM(xpDelta), 0) FROM transactions WHERE xpDelta > 0")
    fun observeTotalXpDistributed(): Flow<Int>

    @Query("SELECT COALESCE(SUM(ptPoints), 0) FROM users WHERE accountStatus != 'DELETED'")
    fun observeTotalPtInCirculation(): Flow<Int>

    // --- Shop, Backgrounds & Avatar Frames ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopPurchase(purchase: ShopPurchaseEntity)

    @Query("SELECT COUNT(*) FROM shop_purchases")
    fun observeTotalShopPurchasesCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOwnedBackground(bg: OwnedBackgroundEntity)

    @Query("SELECT * FROM owned_backgrounds WHERE userId = :userId ORDER BY acquiredAtMillis ASC")
    fun observeOwnedBackgrounds(userId: Long): Flow<List<OwnedBackgroundEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOwnedFrame(frame: OwnedFrameEntity): Long

    @Query("SELECT * FROM owned_frames WHERE userId = :userId ORDER BY acquiredAtMillis ASC")
    fun observeOwnedFrames(userId: Long): Flow<List<OwnedFrameEntity>>

    @Query("SELECT * FROM owned_frames WHERE userId = :userId")
    suspend fun getOwnedFramesOnce(userId: Long): List<OwnedFrameEntity>

    // --- Flashcards & Study Planner ---
    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun getFlashcardCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(cards: List<FlashcardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFlashcard(card: FlashcardEntity): Long

    @Query("SELECT * FROM flashcards WHERE userId = 0 OR userId = :userId ORDER BY id ASC")
    fun observeFlashcardsForUser(userId: Long): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM study_plan_tasks WHERE userId = :userId ORDER BY isCompleted ASC, targetDateBangkok ASC, createdAtMillis DESC")
    fun observeStudyPlanTasks(userId: Long): Flow<List<StudyPlanTaskEntity>>

    @Query("SELECT * FROM study_plan_tasks WHERE id = :taskId AND userId = :userId LIMIT 1")
    suspend fun getStudyPlanTaskById(taskId: Long, userId: Long): StudyPlanTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudyPlanTask(task: StudyPlanTaskEntity): Long

    @Query("DELETE FROM study_plan_tasks WHERE id = :taskId AND userId = :userId")
    suspend fun deleteStudyPlanTask(taskId: Long, userId: Long)

    // --- Pets & Eggs ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPetInstance(pet: PetInstanceEntity): Long

    @Query("SELECT * FROM pet_inventory WHERE userId = :userId ORDER BY obtainedAtMillis DESC")
    fun observeUserPets(userId: Long): Flow<List<PetInstanceEntity>>

    @Query("SELECT * FROM pet_inventory WHERE userId = :userId")
    suspend fun getUserPetsOnce(userId: Long): List<PetInstanceEntity>

    @Query("SELECT * FROM pet_inventory WHERE instanceId = :instanceId LIMIT 1")
    suspend fun getPetInstanceById(instanceId: Long): PetInstanceEntity?

    @Query("DELETE FROM pet_inventory WHERE instanceId = :instanceId AND userId = :userId")
    suspend fun deletePetInstance(instanceId: Long, userId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEggHistory(history: EggHistoryEntity)

    @Query("SELECT COUNT(*) FROM egg_history")
    fun observeTotalEggsOpenedCount(): Flow<Int>

    // --- Quests ---
    @Query("SELECT * FROM quest_definitions WHERE isActive = 1 ORDER BY id ASC")
    fun observeActiveQuests(): Flow<List<QuestDefinitionEntity>>

    @Query("SELECT * FROM quest_definitions WHERE isActive = 1")
    suspend fun getActiveQuestsOnce(): List<QuestDefinitionEntity>

    @Query("SELECT COUNT(*) FROM quest_definitions")
    suspend fun getQuestCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<QuestDefinitionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestDefinitionEntity): Long

    @Query("SELECT * FROM user_quest_progress WHERE userId = :userId")
    fun observeUserQuestProgress(userId: Long): Flow<List<UserQuestProgressEntity>>

    @Query("SELECT * FROM user_quest_progress WHERE userId = :userId AND questId = :questId AND periodKey = :periodKey LIMIT 1")
    suspend fun getQuestProgress(userId: Long, questId: Long, periodKey: String): UserQuestProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuestProgress(progress: UserQuestProgressEntity)

    // --- Achievements ---
    @Query("SELECT * FROM achievement_definitions")
    fun observeAchievementDefs(): Flow<List<AchievementDefEntity>>

    @Query("SELECT * FROM achievement_definitions")
    suspend fun getAchievementDefsOnce(): List<AchievementDefEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievementDefs(defs: List<AchievementDefEntity>)

    @Query("SELECT * FROM user_achievements WHERE userId = :userId")
    fun observeUserAchievements(userId: Long): Flow<List<UserAchievementEntity>>

    @Query("SELECT * FROM user_achievements WHERE userId = :userId AND achievementCode = :code LIMIT 1")
    suspend fun getUserAchievement(userId: Long, code: String): UserAchievementEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserAchievement(ua: UserAchievementEntity): Long

    // --- Leaderboards (Top 10 per Subject, Mode, Game, and Overall) ---
    @Query("SELECT * FROM leaderboard_entries WHERE boardId = :boardId ORDER BY score DESC, updatedAtMillis ASC LIMIT 10")
    fun observeTop10ForBoard(boardId: String): Flow<List<LeaderboardEntryEntity>>

    @Query("SELECT * FROM leaderboard_entries ORDER BY score DESC")
    fun observeAllLeaderboardEntries(): Flow<List<LeaderboardEntryEntity>>

    @Query("SELECT * FROM leaderboard_entries WHERE boardId = :boardId AND username = :username LIMIT 1")
    suspend fun getLeaderboardEntry(boardId: String, username: String): LeaderboardEntryEntity?

    @Query("SELECT * FROM leaderboard_entries WHERE userId = :userId")
    suspend fun getLeaderboardEntriesForUser(userId: Long): List<LeaderboardEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLeaderboardEntry(entry: LeaderboardEntryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLeaderboardEntries(entries: List<LeaderboardEntryEntity>)

    @Query("DELETE FROM leaderboard_entries WHERE userId = :userId")
    suspend fun deleteLeaderboardsForUser(userId: Long)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAtMillis DESC LIMIT 50")
    fun observeUserNotifications(userId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsRead(userId: Long)

    // --- Study Sheets ---
    @Query("SELECT * FROM study_sheets WHERE userId = :userId ORDER BY createdAtMillis DESC")
    fun observeStudySheets(userId: Long): Flow<List<StudySheetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySheet(sheet: StudySheetEntity): Long

    @Query("DELETE FROM study_sheets WHERE id = :sheetId AND userId = :userId")
    suspend fun deleteStudySheet(sheetId: Long, userId: Long)

    // --- AI Tutor Chat ---
    @Query("SELECT * FROM tutor_messages WHERE userId = :userId ORDER BY createdAtMillis ASC")
    fun observeTutorMessages(userId: Long): Flow<List<TutorMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTutorMessage(msg: TutorMessageEntity)

    @Query("DELETE FROM tutor_messages WHERE userId = :userId")
    suspend fun clearTutorMessages(userId: Long)

    // --- Admin Audit Logs, Feedback & System Config ---
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestampMillis DESC LIMIT 100")
    fun observeAdminAuditLogs(): Flow<List<AdminAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAdminAuditLog(log: AdminAuditLogEntity)

    @Query("SELECT * FROM feedback_reports ORDER BY createdAtMillis DESC")
    fun observeFeedbackReports(): Flow<List<FeedbackReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedbackReport(report: FeedbackReportEntity)

    @Query("UPDATE feedback_reports SET isResolved = 1 WHERE id = :reportId")
    suspend fun resolveFeedbackReport(reportId: Long)

    @Query("SELECT * FROM system_config")
    fun observeSystemConfig(): Flow<List<SystemConfigEntity>>

    @Query("SELECT configValue FROM system_config WHERE configKey = :key LIMIT 1")
    suspend fun getSystemConfigValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSystemConfig(config: SystemConfigEntity)
}
