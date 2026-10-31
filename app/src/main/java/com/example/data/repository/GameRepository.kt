package com.example.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.withTransaction
import com.example.data.local.AchievementDefEntity
import com.example.data.local.AdminAuditLogEntity
import com.example.data.local.EggHistoryEntity
import com.example.data.local.ExamSessionEntity
import com.example.data.local.FeedbackReportEntity
import com.example.data.local.FlashcardEntity
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.OwnedBackgroundEntity
import com.example.data.local.OwnedFrameEntity
import com.example.data.local.PetInstanceEntity
import com.example.data.local.PtDatabase
import com.example.data.local.QuestDefinitionEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.ShopPurchaseEntity
import com.example.data.local.StudyPlanTaskEntity
import com.example.data.local.StudySheetEntity
import com.example.data.local.SystemConfigEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.TutorMessageEntity
import com.example.data.local.UserAchievementEntity
import com.example.data.local.UserEntity
import com.example.data.local.UserQuestProgressEntity
import com.example.data.local.UserQuestionHistoryEntity
import com.example.data.seed.InitialQuestionSeed
import com.example.domain.ExamOutcomeSummary
import com.example.domain.ScoringAndPetEngine
import com.example.i18n.AppLanguage
import com.example.i18n.PlayerGender
import com.example.i18n.tr
import com.example.model.BossSpec
import com.example.model.DifficultyLevel
import com.example.model.EggRarity
import com.example.model.GameCatalog
import com.example.model.LuckyWheelSlice
import com.example.model.MiniGameId
import com.example.model.MysteryChestSpec
import com.example.model.PetSpecies
import com.example.model.ShopItemCatalog
import com.example.model.SubjectId
import com.example.util.SecurityUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.SecureRandom
import java.util.Calendar
import java.util.TimeZone

private val Context.sessionDataStore by preferencesDataStore(name = "pts_session_prefs")

data class SignUpOutcome(
    val userId: Long,
    val recoveryCodePlain: String
)

data class MysteryChestRewardOutcome(
    val chest: MysteryChestSpec,
    val titleTh: String,
    val titleEn: String,
    val titleZh: String,
    val ptWon: Int,
    val xpWon: Int,
    val unlockedFrameId: String? = null,
    val bonusItemNote: String = ""
) {
    fun title(lang: AppLanguage) = tr(lang, titleTh, titleEn, titleZh)
}

class GameRepository(
    private val context: Context,
    private val db: PtDatabase = PtDatabase.getInstance(context)
) {
    private val dao = db.ptDao()
    private val rng = SecureRandom()
    private val activeUserIdKey = longPreferencesKey("active_user_id")

    val activeUserIdFlow: Flow<Long?> = context.sessionDataStore.data.map { prefs ->
        val uid = prefs[activeUserIdKey] ?: -1L
        if (uid > 0L) uid else null
    }

    fun observeUser(userId: Long): Flow<UserEntity?> = dao.observeUserById(userId)
    fun observeAllActiveUsers(): Flow<List<UserEntity>> = dao.observeAllActiveUsers()
    fun observeTotalUsersCount(): Flow<Int> = dao.observeTotalUsersCount()
    fun observeApprovedQuestions(): Flow<List<QuestionEntity>> = dao.observeApprovedQuestions()
    fun observeAllQuestions(): Flow<List<QuestionEntity>> = dao.observeAllQuestions()
    fun observeUserQuestionHistory(userId: Long): Flow<List<UserQuestionHistoryEntity>> = dao.observeUserQuestionHistory(userId)
    fun observeCompletedExams(userId: Long): Flow<List<ExamSessionEntity>> = dao.observeCompletedExams(userId)
    fun observeTotalCompletedExamsCount(): Flow<Int> = dao.observeTotalCompletedExamsCount()
    fun observeUserTransactions(userId: Long): Flow<List<TransactionEntity>> = dao.observeUserTransactions(userId)
    fun observeTotalXpDistributed(): Flow<Int> = dao.observeTotalXpDistributed()
    fun observeTotalPtInCirculation(): Flow<Int> = dao.observeTotalPtInCirculation()
    fun observeTotalShopPurchasesCount(): Flow<Int> = dao.observeTotalShopPurchasesCount()
    fun observeOwnedBackgrounds(userId: Long): Flow<List<OwnedBackgroundEntity>> = dao.observeOwnedBackgrounds(userId)
    fun observeOwnedFrames(userId: Long): Flow<List<OwnedFrameEntity>> = dao.observeOwnedFrames(userId)
    fun observeFlashcardsForUser(userId: Long): Flow<List<FlashcardEntity>> = dao.observeFlashcardsForUser(userId)
    fun observeStudyPlanTasks(userId: Long): Flow<List<StudyPlanTaskEntity>> = dao.observeStudyPlanTasks(userId)
    fun observeUserPets(userId: Long): Flow<List<PetInstanceEntity>> = dao.observeUserPets(userId)
    fun observeTotalEggsOpenedCount(): Flow<Int> = dao.observeTotalEggsOpenedCount()
    fun observeActiveQuests(): Flow<List<QuestDefinitionEntity>> = dao.observeActiveQuests()
    fun observeUserQuestProgress(userId: Long): Flow<List<UserQuestProgressEntity>> = dao.observeUserQuestProgress(userId)
    fun observeAchievementDefs(): Flow<List<AchievementDefEntity>> = dao.observeAchievementDefs()
    fun observeUserAchievements(userId: Long): Flow<List<UserAchievementEntity>> = dao.observeUserAchievements(userId)
    fun observeTop10ForBoard(boardId: String): Flow<List<LeaderboardEntryEntity>> = dao.observeTop10ForBoard(boardId)
    fun observeUserNotifications(userId: Long): Flow<List<NotificationEntity>> = dao.observeUserNotifications(userId)
    fun observeStudySheets(userId: Long): Flow<List<StudySheetEntity>> = dao.observeStudySheets(userId)
    fun observeTutorMessages(userId: Long): Flow<List<TutorMessageEntity>> = dao.observeTutorMessages(userId)
    fun observeAdminAuditLogs(): Flow<List<AdminAuditLogEntity>> = dao.observeAdminAuditLogs()
    fun observeFeedbackReports(): Flow<List<FeedbackReportEntity>> = dao.observeFeedbackReports()
    fun observeSystemConfig(): Flow<List<SystemConfigEntity>> = dao.observeSystemConfig()

    suspend fun ensureInitialSeed() {
        db.withTransaction {
            if (dao.getQuestionCount() == 0) {
                dao.insertQuestions(InitialQuestionSeed.defaultQuestions())
            }
            if (dao.getQuestCount() < 15) {
                dao.insertQuests(InitialQuestionSeed.defaultQuests())
            }
            val seededAchievements = InitialQuestionSeed.defaultAchievements()
            if (dao.getAchievementDefsOnce().size < seededAchievements.size) {
                dao.insertAchievementDefs(seededAchievements)
            }
            if (dao.getFlashcardCount() == 0) {
                dao.insertFlashcards(InitialQuestionSeed.defaultFlashcards())
            }
            if (dao.getSystemConfigValue("XP_BASE_FACTOR") == null) {
                dao.setSystemConfig(SystemConfigEntity("XP_BASE_FACTOR", "100"))
                dao.setSystemConfig(
                    SystemConfigEntity(
                        "HOME_NEWS",
                        "🌈 ยินดีต้อนรับสู่ https://PTPreM1.online ซีซันสายรุ้ง! สะสมสัตว์เลี้ยง 28 ชนิด ลุยบอส หมุนวงล้อ และฝึกสอบเข้า ม.1!"
                    )
                )
                dao.setSystemConfig(
                    SystemConfigEntity("BANNED_WORDS", "คำหยาบ,โกงข้อสอบ,spam")
                )
                dao.insertLeaderboardEntries(InitialQuestionSeed.defaultRivalLeaderboardEntries())
            }
        }
    }

    suspend fun getXpBaseFactor(): Int =
        dao.getSystemConfigValue("XP_BASE_FACTOR")?.toIntOrNull()?.coerceIn(50, 1000) ?: 100

    suspend fun setSessionUserId(userId: Long?) {
        context.sessionDataStore.edit { prefs ->
            prefs[activeUserIdKey] = userId ?: -1L
        }
    }

    private fun isValidUsernameFormat(cleanUser: String): Boolean =
        cleanUser.length in 3..20 && cleanUser.matches(Regex("^[A-Za-z0-9_ก-๙\\u4e00-\\u9fa5]+$"))

    suspend fun isUsernameAvailable(username: String, excludeUserId: Long? = null): Boolean {
        val clean = username.trim()
        if (!isValidUsernameFormat(clean)) return false
        val found = dao.getUserByUsername(clean) ?: return true
        return excludeUserId != null && found.id == excludeUserId
    }

    // --- 5-Field Unified Registration ---
    suspend fun signUp(
        gender: PlayerGender,
        username: String,
        displayName: String,
        passwordOrPin: String,
        customRecoveryCode: String,
        avatarIndex: Int,
        langCode: String
    ): Result<SignUpOutcome> {
        val lang = AppLanguage.fromCode(langCode)
        val cleanUser = username.trim()
        val cleanDisplay = displayName.trim().ifBlank { cleanUser }
        if (!isValidUsernameFormat(cleanUser)) {
            return Result.failure(
                IllegalArgumentException(
                    tr(
                        lang,
                        "ชื่อบัญชีต้องยาว 3–20 ตัวอักษร (ตัวอักษร ตัวเลข หรือ _)",
                        "Account Name must be 3–20 characters (letters, digits, or _).",
                        "账号名称需为 3–20 个字符（字母、数字或下划线）。"
                    )
                )
            )
        }
        if (passwordOrPin.trim().length < 4) {
            return Result.failure(
                IllegalArgumentException(
                    tr(
                        lang,
                        "รหัสบัญชีต้องมีอย่างน้อย 4 ตัวอักษร",
                        "Account Password must be at least 4 characters.",
                        "账号密码至少需要 4 个字符。"
                    )
                )
            )
        }
        val chosenRecovery = customRecoveryCode.trim().uppercase().let { raw ->
            if (raw.length >= 4) raw else SecurityUtils.generateRecoveryCode()
        }

        return db.withTransaction {
            if (dao.getUserByUsername(cleanUser) != null) {
                return@withTransaction Result.failure(
                    IllegalArgumentException(
                        tr(
                            lang,
                            "ชื่อบัญชีนี้ถูกใช้งานแล้ว กรุณาเลือกชื่ออื่น",
                            "This Account Name is already taken. Please choose another.",
                            "该账号名称已被使用，请更换其他名称。"
                        )
                    )
                )
            }
            val salt = SecurityUtils.generateSalt()
            val pwHash = SecurityUtils.hashSecret(passwordOrPin.trim(), salt)
            val recHash = SecurityUtils.hashSecret(chosenRecovery, salt)

            val newUser = UserEntity(
                username = cleanUser,
                displayName = cleanDisplay,
                gender = gender.code,
                passwordHash = pwHash,
                salt = salt,
                recoveryCodeHash = recHash,
                recoveryCodePlain = chosenRecovery,
                avatarIndex = avatarIndex,
                equippedFrameId = "FRAME_DEFAULT",
                isGuest = false,
                ptPoints = 250,
                luckyWheelTickets = 1,
                mysteryBoxesOwned = 1,
                languageCode = langCode
            )
            val userId = dao.insertUser(newUser)
            dao.insertOwnedBackground(OwnedBackgroundEntity(userId = userId, backgroundId = "BG_DEFAULT"))
            dao.insertOwnedFrame(OwnedFrameEntity(userId = userId, frameId = "FRAME_DEFAULT"))
            dao.insertNotification(
                NotificationEntity(
                    userId = userId,
                    category = "SYSTEM",
                    message = tr(
                        lang,
                        "ยินดีต้อนรับสู่ PTPreM1.online! รับขวัญ +250 PT, ตั๋ววงล้อฟรี 1 ใบ และกล่องสมบัติ 1 กล่อง!",
                        "Welcome to PTPreM1.online! Received +250 PT, 1 Lucky Ticket & 1 Mystery Chest!",
                        "欢迎来到 PTPreM1.online！获赠 +250 PT、1张转盘券与1个神秘宝箱！"
                    )
                )
            )
            setSessionUserId(userId)
            Result.success(SignUpOutcome(userId, chosenRecovery))
        }
    }

    suspend fun startGuestTrial(langCode: String): Result<Long> {
        val lang = AppLanguage.fromCode(langCode)
        return db.withTransaction {
            val suffix = (1000..9999).random()
            val username = "Guest_$suffix"
            val salt = SecurityUtils.generateSalt()
            val rec = SecurityUtils.generateRecoveryCode()
            val guest = UserEntity(
                username = username,
                displayName = tr(lang, "นักสำรวจ #$suffix", "Explorer #$suffix", "见习生 #$suffix"),
                gender = PlayerGender.UNSPECIFIED.code,
                passwordHash = SecurityUtils.hashSecret("0000", salt),
                salt = salt,
                recoveryCodeHash = SecurityUtils.hashSecret(rec, salt),
                recoveryCodePlain = rec,
                isGuest = true,
                ptPoints = 200,
                luckyWheelTickets = 1,
                mysteryBoxesOwned = 1,
                languageCode = langCode
            )
            val uid = dao.insertUser(guest)
            dao.insertOwnedBackground(OwnedBackgroundEntity(userId = uid, backgroundId = "BG_DEFAULT"))
            dao.insertOwnedFrame(OwnedFrameEntity(userId = uid, frameId = "FRAME_DEFAULT"))
            setSessionUserId(uid)
            Result.success(uid)
        }
    }

    suspend fun logIn(username: String, passwordOrPin: String, lang: AppLanguage = AppLanguage.TH): Result<UserEntity> {
        val user = dao.getUserByUsername(username.trim())
            ?: return Result.failure(
                IllegalArgumentException(
                    tr(lang, "ไม่พบชื่อบัญชีนี้ในระบบ", "Account Name not found.", "未找到该账号。")
                )
            )

        if (user.accountStatus == "DELETED") {
            return Result.failure(IllegalStateException("Deleted your account by Admin... [Farewell 😞]"))
        }

        val now = System.currentTimeMillis()
        val checkedUser = if (user.accountStatus == "BANNED" && user.banUntilMillis in 1..now) {
            val unbanned = user.copy(
                accountStatus = "ACTIVE",
                banStartedAtMillis = 0L,
                banUntilMillis = 0L,
                banReason = "",
                updatedAtMillis = now
            )
            dao.updateUser(unbanned)
            unbanned
        } else {
            user
        }

        if (!SecurityUtils.verifySecret(passwordOrPin.trim(), checkedUser.salt, checkedUser.passwordHash)) {
            return Result.failure(
                IllegalArgumentException(
                    tr(lang, "รหัสบัญชีไม่ถูกต้อง", "Incorrect Account Password.", "账号密码不正确。")
                )
            )
        }

        if (checkedUser.accountStatus == "BANNED") {
            val durationDesc = if (checkedUser.banUntilMillis == Long.MAX_VALUE) {
                "FOREVER"
            } else {
                val remainSec = ((checkedUser.banUntilMillis - now) / 1000L).coerceAtLeast(1L)
                "${remainSec}s remaining"
            }
            return Result.failure(
                IllegalStateException(
                    "Banned ($durationDesc) by Admin... ${checkedUser.banReason.ifBlank { "Policy violation" }}"
                )
            )
        }

        setSessionUserId(checkedUser.id)
        return Result.success(checkedUser)
    }

    suspend fun recoverAccountWithCode(
        username: String,
        recoveryCode: String,
        newPasswordOrPin: String,
        lang: AppLanguage = AppLanguage.TH
    ): Result<Unit> {
        if (newPasswordOrPin.trim().length < 4) {
            return Result.failure(
                IllegalArgumentException(
                    tr(lang, "รหัสบัญชีใหม่ต้องมีอย่างน้อย 4 ตัวอักษร", "New password must be at least 4 characters.", "新密码至少需要4个字符。")
                )
            )
        }
        val user = dao.getUserByUsername(username.trim())
            ?: return Result.failure(
                IllegalArgumentException(
                    tr(lang, "ไม่พบชื่อบัญชีนี้", "Account Name not found.", "未找到该账号。")
                )
            )
        val cleanRec = recoveryCode.trim().uppercase()
        val matchesHash = SecurityUtils.verifySecret(cleanRec, user.salt, user.recoveryCodeHash)
        val matchesPlain = user.recoveryCodePlain.isNotBlank() && user.recoveryCodePlain.equals(cleanRec, ignoreCase = true)
        if (!matchesHash && !matchesPlain) {
            return Result.failure(
                IllegalArgumentException(
                    tr(lang, "รหัสกู้คืนบัญชีไม่ถูกต้อง", "Invalid Recovery Code.", "恢复码不正确。")
                )
            )
        }
        val newSalt = SecurityUtils.generateSalt()
        val newPwHash = SecurityUtils.hashSecret(newPasswordOrPin.trim(), newSalt)
        val newRecHash = SecurityUtils.hashSecret(cleanRec, newSalt)
        dao.updateUser(
            user.copy(
                salt = newSalt,
                passwordHash = newPwHash,
                recoveryCodeHash = newRecHash,
                recoveryCodePlain = cleanRec,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
        return Result.success(Unit)
    }

    /**
     * Full 5-Field Editable Account Updater:
     * Allows changing Gender, Account Name (username), Display Name, Account Password, and Recovery Code anytime!
     */
    suspend fun updateAllAccountFields(
        userId: Long,
        gender: PlayerGender,
        newUsername: String,
        newDisplayName: String,
        newPasswordOrBlank: String,
        newRecoveryCode: String,
        avatarIndex: Int,
        lang: AppLanguage
    ): Result<UserEntity> {
        val cleanUser = newUsername.trim()
        val cleanDisplay = newDisplayName.trim().ifBlank { cleanUser }
        val cleanRec = newRecoveryCode.trim().uppercase()

        if (!isValidUsernameFormat(cleanUser)) {
            return Result.failure(
                IllegalArgumentException(
                    tr(
                        lang,
                        "ชื่อบัญชีต้องยาว 3–20 ตัวอักษร (ตัวอักษร ตัวเลข หรือ _)",
                        "Account Name must be 3–20 characters.",
                        "账号名称需为 3–20 个字符。"
                    )
                )
            )
        }
        if (newPasswordOrBlank.isNotBlank() && newPasswordOrBlank.trim().length < 4) {
            return Result.failure(
                IllegalArgumentException(
                    tr(
                        lang,
                        "รหัสบัญชีใหม่ต้องยาวอย่างน้อย 4 ตัวอักษร",
                        "New Account Password must be at least 4 characters.",
                        "新账号密码至少需要 4 个字符。"
                    )
                )
            )
        }
        if (cleanRec.length < 4) {
            return Result.failure(
                IllegalArgumentException(
                    tr(
                        lang,
                        "รหัสกู้คืนบัญชีต้องยาวอย่างน้อย 4 ตัวอักษร",
                        "Recovery Code must be at least 4 characters.",
                        "账号恢复码至少需要 4 个字符。"
                    )
                )
            )
        }

        return db.withTransaction {
            val current = dao.getUserById(userId)
                ?: return@withTransaction Result.failure(IllegalStateException("User not found"))

            val conflict = dao.getUserByUsername(cleanUser)
            if (conflict != null && conflict.id != userId) {
                return@withTransaction Result.failure(
                    IllegalArgumentException(
                        tr(
                            lang,
                            "ชื่อบัญชี '$cleanUser' มีผู้อื่นใช้แล้ว กรุณาเปลี่ยนใหม่",
                            "Account Name '$cleanUser' is already in use.",
                            "账号名称 '$cleanUser' 已被占用。"
                        )
                    )
                )
            }

            val now = System.currentTimeMillis()
            val nextSalt: String
            val nextPwHash: String
            if (newPasswordOrBlank.isNotBlank()) {
                nextSalt = SecurityUtils.generateSalt()
                nextPwHash = SecurityUtils.hashSecret(newPasswordOrBlank.trim(), nextSalt)
            } else {
                nextSalt = current.salt
                nextPwHash = current.passwordHash
            }
            val nextRecHash = SecurityUtils.hashSecret(cleanRec, nextSalt)

            val updated = current.copy(
                gender = gender.code,
                username = cleanUser,
                displayName = cleanDisplay,
                salt = nextSalt,
                passwordHash = nextPwHash,
                recoveryCodeHash = nextRecHash,
                recoveryCodePlain = cleanRec,
                avatarIndex = avatarIndex,
                isGuest = false,
                updatedAtMillis = now
            )
            dao.updateUser(updated)

            // Sync Leaderboard entries if username or avatar changed!
            if (current.username != cleanUser || current.avatarIndex != avatarIndex) {
                val boards = dao.getLeaderboardEntriesForUser(userId)
                boards.forEach { entry ->
                    dao.upsertLeaderboardEntry(
                        entry.copy(
                            username = cleanUser,
                            avatarIndex = avatarIndex,
                            updatedAtMillis = now
                        )
                    )
                }
            }
            Result.success(updated)
        }
    }

    suspend fun updateCosmetics(
        userId: Long,
        customTitle: String,
        titleColorIndex: Int,
        equippedFrameId: String,
        avatarIndex: Int
    ) {
        db.withTransaction {
            val user = dao.getUserById(userId) ?: return@withTransaction
            val cleanTitle = customTitle.trim().take(24)
            val now = System.currentTimeMillis()
            val updated = user.copy(
                customTitle = cleanTitle,
                titleColorIndex = titleColorIndex.coerceIn(0, GameCatalog.titleStyles.lastIndex),
                equippedFrameId = equippedFrameId,
                avatarIndex = avatarIndex,
                updatedAtMillis = now
            )
            dao.updateUser(updated)
            if (cleanTitle.isNotBlank() || equippedFrameId != "FRAME_DEFAULT") {
                evaluateAndUnlockAchievements(updated, customCosmeticSet = true, nowMillis = now)
            }
        }
    }

    suspend fun changePasswordOrPin(
        userId: Long,
        currentPassword: String,
        newPassword: String,
        lang: AppLanguage = AppLanguage.TH
    ): Result<Unit> {
        val user = dao.getUserById(userId) ?: return Result.failure(IllegalStateException("User not found"))
        if (!user.isGuest && !SecurityUtils.verifySecret(currentPassword.trim(), user.salt, user.passwordHash)) {
            return Result.failure(
                IllegalArgumentException(
                    tr(lang, "รหัสบัญชีปัจจุบันไม่ถูกต้อง", "Current password is incorrect.", "当前密码不正确。")
                )
            )
        }
        if (newPassword.trim().length < 4) {
            return Result.failure(
                IllegalArgumentException(
                    tr(lang, "รหัสบัญชีใหม่ต้องยาวอย่างน้อย 4 ตัวอักษร", "New password must be at least 4 characters.", "新密码至少需要4个字符。")
                )
            )
        }
        val newSalt = SecurityUtils.generateSalt()
        val recPlain = user.recoveryCodePlain.ifBlank { SecurityUtils.generateRecoveryCode() }
        dao.updateUser(
            user.copy(
                salt = newSalt,
                passwordHash = SecurityUtils.hashSecret(newPassword.trim(), newSalt),
                recoveryCodeHash = SecurityUtils.hashSecret(recPlain, newSalt),
                recoveryCodePlain = recPlain,
                isGuest = false,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
        return Result.success(Unit)
    }

    suspend fun updateUserSettings(transform: (UserEntity) -> UserEntity) {
        val uid = activeUserIdFlow.first() ?: return
        val current = dao.getUserById(uid) ?: return
        dao.updateUser(transform(current).copy(updatedAtMillis = System.currentTimeMillis()))
    }

    // --- Suspension 5-Exercise Recovery ---
    suspend fun recordSuspensionExerciseProgress(userId: Long, correctIncrement: Int): UserEntity? {
        val user = dao.getUserById(userId) ?: return null
        val newCount = (user.suspensionRecoveryCompletedCount + correctIncrement).coerceAtMost(5)
        val restored = if (newCount >= 5) {
            dao.insertNotification(
                NotificationEntity(
                    userId = userId,
                    category = "SYSTEM",
                    message = "ทำแบบฝึกหัดครบ 5 ข้อสำเร็จ! บัญชีของคุณได้รับการปลดพักชั่วคราวเรียบร้อยแล้ว"
                )
            )
            user.copy(
                accountStatus = "ACTIVE",
                suspensionRecoveryCompletedCount = 0,
                suspensionRecoveryRequested = false,
                updatedAtMillis = System.currentTimeMillis()
            )
        } else {
            user.copy(
                suspensionRecoveryCompletedCount = newCount,
                suspensionRecoveryRequested = true,
                updatedAtMillis = System.currentTimeMillis()
            )
        }
        dao.updateUser(restored)
        return restored
    }

    // --- Start Exam Session or Mistake Retry Session ---
    suspend fun createExamSession(
        userId: Long,
        mode: String,
        subject: SubjectId,
        difficulty: DifficultyLevel,
        requestedCount: Int
    ): Result<Pair<ExamSessionEntity, List<QuestionEntity>>> {
        val user = dao.getUserById(userId) ?: return Result.failure(IllegalStateException("Please sign in"))
        val allSubjectQuestions = dao.getApprovedQuestionsBySubject(subject.id)
        val seenIds = dao.getSeenQuestionIds(userId).toSet()

        val targetCount = if (mode == "PRACTICE" && difficulty == DifficultyLevel.EASY && user.easyBonusUnlocked) {
            requestedCount + 20
        } else {
            requestedCount
        }

        val unseenPreferred = allSubjectQuestions.filter { it.id !in seenIds && it.difficulty == difficulty.id }
        val unseenOtherDiff = allSubjectQuestions.filter { it.id !in seenIds && it.difficulty != difficulty.id }
        val seenPool = allSubjectQuestions.filter { it.id in seenIds }
        val fallbackGlobal = if (allSubjectQuestions.size < targetCount) {
            dao.getAllApprovedQuestionsOnce().filter { it.subjectId != subject.id }
        } else emptyList()

        val selected = (unseenPreferred.shuffled() + unseenOtherDiff.shuffled() + seenPool.shuffled() + fallbackGlobal.shuffled())
            .distinctBy { it.id }
            .take(targetCount.coerceAtLeast(1))

        if (selected.isEmpty()) {
            return Result.failure(IllegalStateException("No questions available"))
        }

        val equippedPetSpecies = user.equippedPetInstanceId?.let { instId ->
            val inst = dao.getPetInstanceById(instId)
            GameCatalog.getPetById(inst?.speciesId)
        }
        val timeBonusFactor = ScoringAndPetEngine.examTimeMultiplierForPet(equippedPetSpecies)
        val baseSeconds = selected.size * difficulty.secondsPerQuestion
        val finalSeconds = (baseSeconds * timeBonusFactor).toInt().coerceAtLeast(30)

        val session = ExamSessionEntity(
            userId = userId,
            mode = mode,
            subjectId = subject.id,
            difficulty = difficulty.id,
            questionIdsCsv = selected.joinToString(",") { it.id.toString() },
            remainingSeconds = finalSeconds,
            totalAllocatedSeconds = finalSeconds
        )
        val sessionId = dao.upsertExamSession(session)
        return Result.success(session.copy(id = sessionId) to selected)
    }

    suspend fun createMistakeRetryExamSession(
        userId: Long,
        questionIds: List<Long>
    ): Result<Pair<ExamSessionEntity, List<QuestionEntity>>> {
        val user = dao.getUserById(userId) ?: return Result.failure(IllegalStateException("Please sign in"))
        val selected = dao.getQuestionsByIds(questionIds.distinct().take(15))
        if (selected.isEmpty()) {
            return Result.failure(IllegalStateException("No mistake questions to retry"))
        }
        val equippedPetSpecies = user.equippedPetInstanceId?.let { instId ->
            val inst = dao.getPetInstanceById(instId)
            GameCatalog.getPetById(inst?.speciesId)
        }
        val timeBonusFactor = ScoringAndPetEngine.examTimeMultiplierForPet(equippedPetSpecies)
        val finalSeconds = (selected.size * 55 * timeBonusFactor).toInt().coerceAtLeast(45)

        val session = ExamSessionEntity(
            userId = userId,
            mode = "MISTAKE_RETRY",
            subjectId = selected.first().subjectId,
            difficulty = DifficultyLevel.NORMAL.id,
            questionIdsCsv = selected.joinToString(",") { it.id.toString() },
            remainingSeconds = finalSeconds,
            totalAllocatedSeconds = finalSeconds
        )
        val sessionId = dao.upsertExamSession(session)
        return Result.success(session.copy(id = sessionId) to selected)
    }

    suspend fun getQuestionsForBoss(subject: SubjectId, count: Int = 8): List<QuestionEntity> {
        val subQs = dao.getApprovedQuestionsBySubject(subject.id).filter {
            it.correctAnswer.uppercase() in setOf("A", "B", "C", "D")
        }
        val globalQs = dao.getAllApprovedQuestionsOnce().filter {
            it.correctAnswer.uppercase() in setOf("A", "B", "C", "D")
        }
        return (subQs.shuffled() + globalQs.shuffled()).distinctBy { it.id }.take(count)
    }

    suspend fun autoSaveExamProgress(
        sessionId: Long,
        currentIndex: Int,
        answersMap: Map<Long, String>,
        markedSet: Set<Long>,
        remainingSeconds: Int,
        petSkillUsesCount: Int
    ) {
        val current = dao.getExamSessionById(sessionId) ?: return
        if (current.isCompleted) return
        val serializedAnswers = answersMap.entries.joinToString(";") { "${it.key}=${it.value.replace(";", ",")}" }
        val markedCsv = markedSet.joinToString(",")
        dao.upsertExamSession(
            current.copy(
                currentQuestionIndex = currentIndex,
                answersData = serializedAnswers,
                markedQuestionIdsCsv = markedCsv,
                remainingSeconds = remainingSeconds,
                petSkillUsesCount = petSkillUsesCount,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun consumeRemoveWrongItem(userId: Long): Boolean {
        return db.withTransaction {
            val user = dao.getUserById(userId) ?: return@withTransaction false
            if (user.removeWrongUses <= 0) return@withTransaction false
            dao.updateUser(user.copy(removeWrongUses = user.removeWrongUses - 1, updatedAtMillis = System.currentTimeMillis()))
            true
        }
    }

    suspend fun consumeTimePotionItem(userId: Long): Boolean {
        return db.withTransaction {
            val user = dao.getUserById(userId) ?: return@withTransaction false
            if (user.timePotionUses <= 0) return@withTransaction false
            dao.updateUser(user.copy(timePotionUses = user.timePotionUses - 1, updatedAtMillis = System.currentTimeMillis()))
            true
        }
    }

    suspend fun finalizeExamSession(
        sessionId: Long,
        questions: List<QuestionEntity>,
        answersMap: Map<Long, String>,
        timeSpentSeconds: Int
    ): Result<ExamOutcomeSummary> {
        return db.withTransaction {
            val session = dao.getExamSessionById(sessionId)
                ?: return@withTransaction Result.failure(IllegalStateException("Exam session not found"))
            val txKey = "EXAM_COMPLETE_$sessionId"
            if (dao.getTransactionByKey(txKey) != null) {
                return@withTransaction Result.failure(IllegalStateException("Already submitted"))
            }
            val user = dao.getUserById(session.userId)
                ?: return@withTransaction Result.failure(IllegalStateException("User not found"))

            val now = System.currentTimeMillis()
            val todayBangkok = SecurityUtils.currentBangkokDateString(now)
            val multActive = user.multiplierExpiresAtMillis > now && user.activeMultiplierGain > 1.0
            val activeGain = if (multActive) user.activeMultiplierGain else 1.0
            val activePenalty = if (multActive) user.activeMultiplierPenalty else 1

            val equippedPet: PetSpecies? = user.equippedPetInstanceId?.let { instId ->
                val inst = dao.getPetInstanceById(instId)
                GameCatalog.getPetById(inst?.speciesId)
            }

            val priorHistory = dao.getUserQuestionHistoryOnce(user.id)
            val unmasteredWrongIds = priorHistory
                .filter { !it.isCorrect && !it.isMasteredMistake }
                .map { it.questionId }
                .toSet()

            val perQuestionSecs = (timeSpentSeconds.coerceAtLeast(questions.size) / questions.size.coerceAtLeast(1)).coerceAtLeast(1)
            var newlyMasteredMistakes = 0

            val outcomes = questions.map { q ->
                val rawAns = answersMap[q.id]?.trim().orEmpty()
                val isCorrect = checkAnswerCorrect(q, rawAns)
                dao.insertQuestionHistory(
                    UserQuestionHistoryEntity(
                        userId = user.id,
                        questionId = q.id,
                        subjectId = q.subjectId,
                        selectedAnswer = rawAns,
                        isCorrect = isCorrect,
                        dateBangkok = todayBangkok,
                        timeSpentSeconds = perQuestionSecs,
                        isMasteredMistake = isCorrect,
                        answeredAtMillis = now
                    )
                )
                if (isCorrect && q.id in unmasteredWrongIds) {
                    dao.markQuestionMistakeMastered(user.id, q.id)
                    newlyMasteredMistakes++
                }
                isCorrect
            }

            val xpFactor = getXpBaseFactor()
            val diff = DifficultyLevel.fromId(session.difficulty)
            val summary = ScoringAndPetEngine.evaluateExamOutcome(
                answerOutcomes = outcomes,
                difficulty = diff,
                currentPtBalance = user.ptPoints,
                currentTotalXp = user.xp,
                xpBaseFactor = xpFactor,
                equippedPet = equippedPet,
                activeMultiplierGain = activeGain,
                activeMultiplierPenalty = activePenalty,
                currentStreakDays = user.streakDays,
                preMissStreakDays = user.preMissStreakDays,
                lastExamDateBangkok = user.lastExamDateBangkok,
                recoveredDateBangkok = user.recoveredDateBangkok,
                nowMillis = now
            )

            val streakUpdate = ScoringAndPetEngine.updateStreakOnCompletedExam(
                currentStreakDays = user.streakDays,
                preMissStreakDays = user.preMissStreakDays,
                lastExamDateBangkok = user.lastExamDateBangkok,
                recoveredDateBangkok = user.recoveredDateBangkok,
                nowMillis = now,
                streakShieldCount = user.streakShieldCount
            )

            val masteryBonusXp = newlyMasteredMistakes * 20
            val finalTotalXp = summary.newXpTotal + masteryBonusXp
            val finalLevel = ScoringAndPetEngine.computeLevelProgress(finalTotalXp, xpFactor).first
            val nextShieldCount = if (streakUpdate.shieldConsumed) {
                (user.streakShieldCount - 1).coerceAtLeast(0)
            } else {
                user.streakShieldCount
            }

            val updatedUser = user.copy(
                xp = finalTotalXp,
                level = finalLevel,
                ptPoints = summary.newPtBalance,
                streakDays = streakUpdate.newStreakDays,
                preMissStreakDays = streakUpdate.newPreMissStreakDays,
                lastExamDateBangkok = streakUpdate.lastExamDateBangkok,
                recoveredDateBangkok = streakUpdate.recoveredDateBangkok,
                streakShieldCount = nextShieldCount,
                totalCorrect = user.totalCorrect + summary.correctCount,
                totalWrong = user.totalWrong + summary.wrongCount,
                totalExamsCompleted = user.totalExamsCompleted + 1,
                totalQuestionsAnswered = user.totalQuestionsAnswered + summary.totalQuestions,
                totalAnswerTimeSeconds = user.totalAnswerTimeSeconds + timeSpentSeconds.coerceAtLeast(1),
                mistakesMasteredCount = user.mistakesMasteredCount + newlyMasteredMistakes,
                activeMultiplierItemId = if (multActive) user.activeMultiplierItemId else "",
                activeMultiplierGain = if (multActive) user.activeMultiplierGain else 1.0,
                activeMultiplierPenalty = if (multActive) user.activeMultiplierPenalty else 1,
                updatedAtMillis = now
            )
            dao.updateUser(updatedUser)

            if (updatedUser.level >= 10) {
                dao.insertOwnedFrame(OwnedFrameEntity(userId = user.id, frameId = "FRAME_GOLDEN_SOVEREIGN", acquiredAtMillis = now))
            }

            val strength = if (summary.accuracyPercent >= 80) {
                "แม่นยำยอดเยี่ยมในหัวข้อ ${questions.firstOrNull()?.topic.orEmpty()}"
            } else {
                "มีความตั้งใจทำข้อสอบจนจบชุด"
            }
            val improve = if (summary.wrongCount > 0) {
                "ทบทวนข้อที่ผิด ${summary.wrongCount} ข้อในสมุดทบทวนข้อผิด (Mistake Notebook)"
            } else {
                "รักษามาตรฐานระดับเต็มร้อยนี้ต่อไปและลองโหมดปราบบอส!"
            }

            dao.upsertExamSession(
                session.copy(
                    isCompleted = true,
                    correctCount = summary.correctCount,
                    wrongCount = summary.wrongCount,
                    accuracyPercent = summary.accuracyPercent,
                    maxCombo = summary.maxCombo,
                    xpEarned = summary.finalXp + masteryBonusXp,
                    ptPointsDelta = summary.netPtDelta,
                    timeSpentSeconds = timeSpentSeconds,
                    strengthSummary = strength,
                    improvementSummary = improve,
                    updatedAtMillis = now,
                    completedAtMillis = now
                )
            )

            dao.insertTransaction(
                TransactionEntity(
                    txKey = txKey,
                    userId = user.id,
                    txType = "EXAM_REWARD",
                    xpDelta = summary.finalXp + masteryBonusXp,
                    ptPointsDelta = summary.netPtDelta,
                    balanceAfter = summary.newPtBalance,
                    note = "${session.subjectId} (${summary.correctCount}/${summary.totalQuestions})",
                    createdAtMillis = now
                )
            )

            incrementQuestMetrics(
                userId = user.id,
                questionsDone = summary.totalQuestions,
                correctAnswers = summary.correctCount,
                subjectCompleted = session.subjectId,
                examsCompleted = 1,
                mistakesMastered = newlyMasteredMistakes,
                nowMillis = now
            )

            val examScorePoints = summary.correctCount * 25 + summary.maxCombo * 10 + (if (summary.accuracyPercent == 100) 80 else 0)
            addScoreToLeaderboard("SUBJECT_${session.subjectId}", updatedUser, examScorePoints, now)
            val modeBoard = when (session.mode) {
                "PRACTICE", "MISTAKE_RETRY" -> "MODE_PRACTICE"
                "ENTRANCE" -> "MODE_ENTRANCE"
                else -> "MODE_EXAM"
            }
            addScoreToLeaderboard(modeBoard, updatedUser, examScorePoints, now)
            addScoreToLeaderboard("OVERALL_TOTAL", updatedUser, examScorePoints, now)

            evaluateAndUnlockAchievements(
                updatedUser,
                isPerfectExam = summary.correctCount == summary.totalQuestions,
                nowMillis = now
            )

            Result.success(
                summary.copy(
                    finalXp = summary.finalXp + masteryBonusXp,
                    newXpTotal = finalTotalXp,
                    newLevel = finalLevel,
                    streakAfter = streakUpdate.newStreakDays
                )
            )
        }
    }

    fun checkAnswerCorrect(question: QuestionEntity, rawAnswer: String): Boolean {
        val trimmed = rawAnswer.trim()
        if (trimmed.isEmpty()) return false
        if (trimmed.equals(question.correctAnswer.trim(), ignoreCase = true)) return true
        if (question.acceptableTextAnswers.isNotBlank()) {
            val accepted = question.acceptableTextAnswers.split("|").map { it.trim() }.filter { it.isNotEmpty() }
            if (accepted.any { it.equals(trimmed, ignoreCase = true) }) return true
        }
        return false
    }

    private suspend fun addScoreToLeaderboard(
        boardId: String,
        user: UserEntity,
        deltaScore: Int,
        nowMillis: Long
    ) {
        if (deltaScore <= 0) return
        val existing = dao.getLeaderboardEntry(boardId, user.username)
        val newScore = (existing?.score ?: 0) + deltaScore
        val entry = LeaderboardEntryEntity(
            id = existing?.id ?: 0L,
            boardId = boardId,
            userId = user.id,
            username = user.username,
            avatarIndex = user.avatarIndex,
            score = newScore,
            rankTitle = ScoringAndPetEngine.rankTitleForScore(newScore),
            updatedAtMillis = nowMillis
        )
        dao.upsertLeaderboardEntry(entry)
    }

    private suspend fun incrementQuestMetrics(
        userId: Long,
        questionsDone: Int = 0,
        correctAnswers: Int = 0,
        subjectCompleted: String? = null,
        examsCompleted: Int = 0,
        miniGamesPlayed: Int = 0,
        flashcardsReviewed: Int = 0,
        mistakesMastered: Int = 0,
        bossDefeated: Int = 0,
        studyPlanDone: Int = 0,
        luckyWheelSpun: Int = 0,
        chestOpened: Int = 0,
        nowMillis: Long = System.currentTimeMillis()
    ) {
        val dayKey = SecurityUtils.currentBangkokDateString(nowMillis)
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok")).apply { timeInMillis = nowMillis }
        val weekKey = "${cal.get(Calendar.YEAR)}-W${cal.get(Calendar.WEEK_OF_YEAR)}"

        val quests = dao.getActiveQuestsOnce()
        for (q in quests) {
            val periodKey = if (q.questScope == "WEEKLY") weekKey else dayKey
            val add = when (q.metricType) {
                "QUESTIONS_DONE" -> questionsDone
                "CORRECT_ANSWERS" -> correctAnswers
                "SCIENCE_EXAMS" -> if (subjectCompleted == SubjectId.SCIENCE.id) 1 else 0
                "MATH_EXAMS" -> if (subjectCompleted == SubjectId.MATH.id) 1 else 0
                "THAI_EXAMS" -> if (subjectCompleted == SubjectId.THAI.id) 1 else 0
                "ENGLISH_EXAMS" -> if (subjectCompleted == SubjectId.ENGLISH.id) 1 else 0
                "LOGIC_EXAMS" -> if (subjectCompleted == SubjectId.IQ_LOGIC.id) 1 else 0
                "EXAMS_COMPLETED" -> examsCompleted
                "MINIGAMES_PLAYED" -> miniGamesPlayed
                "FLASHCARDS_REVIEWED" -> flashcardsReviewed
                "MISTAKES_MASTERED" -> mistakesMastered
                "BOSS_DEFEATED" -> bossDefeated
                "STUDY_PLAN_DONE" -> studyPlanDone
                "LUCKY_WHEEL_SPUN" -> luckyWheelSpun
                "CHEST_OPENED" -> chestOpened
                else -> 0
            }
            if (add > 0) {
                val cur = dao.getQuestProgress(userId, q.id, periodKey)
                val nextCount = ((cur?.currentCount ?: 0) + add).coerceAtMost(q.targetCount)
                dao.upsertQuestProgress(
                    UserQuestProgressEntity(
                        id = cur?.id ?: 0L,
                        userId = userId,
                        questId = q.id,
                        periodKey = periodKey,
                        currentCount = nextCount,
                        isClaimed = cur?.isClaimed ?: false,
                        updatedAtMillis = nowMillis
                    )
                )
            }
        }
    }

    suspend fun rerollDailyQuestsForToday(userId: Long, lang: AppLanguage): Result<Int> {
        return db.withTransaction {
            val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            val today = SecurityUtils.currentBangkokDateString()
            if (user.dailyQuestRerollDateBangkok == today) {
                return@withTransaction Result.failure(
                    IllegalStateException(
                        tr(
                            lang,
                            "วันนี้ใช้สิทธิ์สุ่มเปลี่ยนภารกิจรายวันฟรีไปแล้ว! รอรีเซ็ตพรุ่งนี้นะ",
                            "Free daily quest reroll already used today!",
                            "今日免费刷新日常任务次数已用完！"
                        )
                    )
                )
            }
            val nextOffset = user.dailyQuestRerollOffset + 3
            dao.updateUser(
                user.copy(
                    dailyQuestRerollDateBangkok = today,
                    dailyQuestRerollOffset = nextOffset,
                    updatedAtMillis = System.currentTimeMillis()
                )
            )
            Result.success(nextOffset)
        }
    }

    suspend fun claimQuestReward(userId: Long, quest: QuestDefinitionEntity, periodKey: String): Result<Int> {
        return db.withTransaction {
            val prog = dao.getQuestProgress(userId, quest.id, periodKey)
                ?: return@withTransaction Result.failure(IllegalStateException("Quest not started"))
            if (prog.isClaimed) {
                return@withTransaction Result.failure(IllegalStateException("Already claimed"))
            }
            if (prog.currentCount < quest.targetCount) {
                return@withTransaction Result.failure(IllegalStateException("Quest incomplete"))
            }
            val txKey = "QUEST_${userId}_${quest.id}_$periodKey"
            if (dao.getTransactionByKey(txKey) != null) {
                return@withTransaction Result.failure(IllegalStateException("Already claimed"))
            }
            val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            val factor = getXpBaseFactor()
            val newXp = user.xp + quest.rewardXp
            val newLvl = ScoringAndPetEngine.computeLevelProgress(newXp, factor).first
            val newPt = user.ptPoints + quest.rewardPt
            val now = System.currentTimeMillis()

            dao.upsertQuestProgress(prog.copy(isClaimed = true, updatedAtMillis = now))
            val updated = user.copy(xp = newXp, level = newLvl, ptPoints = newPt, updatedAtMillis = now)
            dao.updateUser(updated)
            dao.insertTransaction(
                TransactionEntity(
                    txKey = txKey,
                    userId = userId,
                    txType = "QUEST_REWARD",
                    xpDelta = quest.rewardXp,
                    ptPointsDelta = quest.rewardPt,
                    balanceAfter = newPt,
                    note = quest.titleTh
                )
            )
            evaluateAndUnlockAchievements(updated, nowMillis = now)
            Result.success(quest.rewardXp)
        }
    }

    private suspend fun evaluateAndUnlockAchievements(
        user: UserEntity,
        isPerfectExam: Boolean = false,
        hatchedRarity: EggRarity? = null,
        customCosmeticSet: Boolean = false,
        nowMillis: Long = System.currentTimeMillis()
    ) {
        val defs = dao.getAchievementDefsOnce().associateBy { it.code }
        val petCount = dao.getUserPetsOnce(user.id).size
        val toUnlock = mutableListOf<String>()

        if (user.totalExamsCompleted >= 1) toUnlock.add("FIRST_EXAM")
        if (user.totalExamsCompleted >= 5) toUnlock.add("EXAM_5")
        if (user.totalExamsCompleted >= 15) toUnlock.add("EXAM_15")
        if (user.totalExamsCompleted >= 30) toUnlock.add("EXAM_30")

        if (user.totalCorrect >= 10) toUnlock.add("CORRECT_10")
        if (user.totalCorrect >= 50) toUnlock.add("CORRECT_50")
        if (user.totalCorrect >= 100) toUnlock.add("CORRECT_100")
        if (user.totalCorrect >= 250) toUnlock.add("CORRECT_250")
        if (user.totalCorrect >= 500) toUnlock.add("CORRECT_500")
        if (isPerfectExam) toUnlock.add("PERFECT_EXAM")

        if (user.streakDays >= 1) toUnlock.add("STREAK_1")
        if (user.streakDays >= 3) toUnlock.add("STREAK_3")
        if (user.streakDays >= 7) toUnlock.add("STREAK_7")
        if (user.streakDays >= 14) toUnlock.add("STREAK_14")
        if (user.streakDays >= 30) toUnlock.add("STREAK_30")
        if (user.streakDays >= 100) toUnlock.add("STREAK_100")

        if (user.level >= 5) toUnlock.add("LEVEL_5")
        if (user.level >= 10) toUnlock.add("LEVEL_10")
        if (user.level >= 25) toUnlock.add("LEVEL_25")

        if (user.ptPoints >= 500) toUnlock.add("PT_WEALTH_500")
        if (user.ptPoints >= 2000) toUnlock.add("PT_WEALTH_2000")

        if (petCount >= 1 || hatchedRarity != null) toUnlock.add("FIRST_PET")
        if (petCount >= 5) toUnlock.add("PET_SQUAD_5")
        if (hatchedRarity != null) {
            if (hatchedRarity in setOf(EggRarity.RARE, EggRarity.EPIC, EggRarity.LEGENDARY, EggRarity.SATRIWIT, EggRarity.SUANKULARB)) {
                toUnlock.add("FIRST_RARE_PET")
            }
            if (hatchedRarity in setOf(EggRarity.LEGENDARY, EggRarity.SATRIWIT, EggRarity.SUANKULARB)) {
                toUnlock.add("LEGENDARY_COLLECTOR")
            }
        }

        if (user.bossesDefeatedCount >= 1) toUnlock.add("BOSS_FIRST")
        if (user.bossesDefeatedCount >= 5) toUnlock.add("BOSS_SLAYER_5")
        if (user.mistakesMasteredCount >= 1) toUnlock.add("MISTAKE_CRUSHER_1")
        if (user.mistakesMasteredCount >= 10) toUnlock.add("MISTAKE_CRUSHER_10")
        if (user.flashcardsReviewedCount >= 10) toUnlock.add("FLASHCARD_10")
        if (user.studyTasksCompletedCount >= 3) toUnlock.add("PLANNER_HERO_3")
        if (user.wheelsSpunCount >= 1) toUnlock.add("LUCKY_SPINNER")
        if (user.chestsOpenedCount >= 3) toUnlock.add("TREASURE_HUNTER")
        if (customCosmeticSet || user.customTitle.isNotBlank() || user.equippedFrameId != "FRAME_DEFAULT") {
            toUnlock.add("CUSTOM_TITLE_SET")
        }

        var bonusXp = 0
        var bonusPt = 0
        val lang = AppLanguage.fromCode(user.languageCode)
        for (code in toUnlock) {
            if (dao.getUserAchievement(user.id, code) == null) {
                val rowId = dao.insertUserAchievement(UserAchievementEntity(userId = user.id, achievementCode = code, unlockedAtMillis = nowMillis))
                if (rowId > 0) {
                    val def = defs[code]
                    if (def != null) {
                        bonusXp += def.rewardXp
                        bonusPt += def.rewardPt
                        val achTitle = tr(lang, def.titleTh, def.titleEn, def.titleZh)
                        dao.insertNotification(
                            NotificationEntity(
                                userId = user.id,
                                category = "ACHIEVEMENT",
                                message = tr(
                                    lang,
                                    "🏆 ปลดล็อกความสำเร็จ: $achTitle! (+${def.rewardXp} XP, +${def.rewardPt} PT)",
                                    "🏆 Achievement Unlocked: $achTitle! (+${def.rewardXp} XP, +${def.rewardPt} PT)",
                                    "🏆 解锁成就：$achTitle！（+${def.rewardXp} XP，+${def.rewardPt} PT）"
                                )
                            )
                        )
                    }
                }
            }
        }
        if (bonusXp > 0 || bonusPt > 0) {
            val fresh = dao.getUserById(user.id) ?: return
            val factor = getXpBaseFactor()
            val updatedXp = fresh.xp + bonusXp
            val updatedLvl = ScoringAndPetEngine.computeLevelProgress(updatedXp, factor).first
            dao.updateUser(
                fresh.copy(
                    xp = updatedXp,
                    level = updatedLvl,
                    ptPoints = fresh.ptPoints + bonusPt,
                    updatedAtMillis = nowMillis
                )
            )
        }
    }

    // --- PT Shop ("ร้านแดง-ขาว") Atomic Purchase (18 Items) ---
    suspend fun purchaseShopItem(
        userId: Long,
        item: ShopItemCatalog,
        requestNonce: String,
        lang: AppLanguage = AppLanguage.TH
    ): Result<UserEntity> {
        return db.withTransaction {
            val txKey = "SHOP_${userId}_${item.id}_$requestNonce"
            if (dao.getTransactionByKey(txKey) != null) {
                return@withTransaction Result.failure(IllegalStateException("Already processed"))
            }
            val user = dao.getUserById(userId)
                ?: return@withTransaction Result.failure(IllegalStateException("User not found"))

            if (user.ptPoints < item.pricePt) {
                return@withTransaction Result.failure(
                    IllegalArgumentException(
                        tr(
                            lang,
                            "PT Points ไม่เพียงพอ (ต้องการ ${item.pricePt} PT)",
                            "Not enough PT Points (needs ${item.pricePt} PT).",
                            "PT积分不足（需要 ${item.pricePt} PT）。"
                        )
                    )
                )
            }

            if (item.id == "SHOP_PREMIUM_UI" && user.premiumUiUnlocked) {
                return@withTransaction Result.failure(
                    IllegalStateException(tr(lang, "คุณปลดล็อก Premium UI ถาวรไปแล้ว", "Premium UI already unlocked.", "您已永久解锁水晶UI主题。"))
                )
            }
            if (item.id == "SHOP_EASY_PLUS_20" && user.easyBonusUnlocked) {
                return@withTransaction Result.failure(
                    IllegalStateException(tr(lang, "คุณปลดล็อกชุดข้อสอบง่าย +20 ไปแล้ว", "Easy +20 Pack already unlocked.", "您已解锁简单题+20训练包。"))
                )
            }
            if (item.backgroundId != null) {
                val owned = dao.observeOwnedBackgrounds(userId).first()
                if (owned.any { it.backgroundId == item.backgroundId }) {
                    return@withTransaction Result.failure(
                        IllegalStateException(tr(lang, "คุณเป็นเจ้าของพื้นหลังนี้แล้ว", "Background already owned.", "您已拥有该背景。"))
                    )
                }
            }
            if (item.frameId != null) {
                val ownedFrames = dao.getOwnedFramesOnce(userId)
                if (ownedFrames.any { it.frameId == item.frameId }) {
                    return@withTransaction Result.failure(
                        IllegalStateException(tr(lang, "คุณเป็นเจ้าของกรอบอวตารนี้แล้ว", "Avatar Frame already owned.", "您已拥有该头像框。"))
                    )
                }
            }

            val now = System.currentTimeMillis()
            val newBalance = user.ptPoints - item.pricePt

            val updatedUser = when (item.category) {
                "THEME" -> user.copy(
                    ptPoints = newBalance,
                    premiumUiUnlocked = true,
                    premiumUiEnabled = true,
                    updatedAtMillis = now
                )
                "BACKGROUND" -> {
                    item.backgroundId?.let { bgId ->
                        dao.insertOwnedBackground(OwnedBackgroundEntity(userId = userId, backgroundId = bgId, acquiredAtMillis = now))
                    }
                    user.copy(
                        ptPoints = newBalance,
                        activeBackgroundId = item.backgroundId ?: user.activeBackgroundId,
                        updatedAtMillis = now
                    )
                }
                "AVATAR_FRAME" -> {
                    item.frameId?.let { fId ->
                        dao.insertOwnedFrame(OwnedFrameEntity(userId = userId, frameId = fId, acquiredAtMillis = now))
                    }
                    user.copy(
                        ptPoints = newBalance,
                        equippedFrameId = item.frameId ?: user.equippedFrameId,
                        updatedAtMillis = now
                    )
                }
                "CONSUMABLE" -> when (item.id) {
                    "SHOP_TIME_POTION" -> user.copy(ptPoints = newBalance, timePotionUses = user.timePotionUses + 2, updatedAtMillis = now)
                    "SHOP_STREAK_SHIELD" -> user.copy(ptPoints = newBalance, streakShieldCount = user.streakShieldCount + 1, updatedAtMillis = now)
                    "SHOP_LUCKY_TICKET" -> user.copy(ptPoints = newBalance, luckyWheelTickets = user.luckyWheelTickets + 2, updatedAtMillis = now)
                    "SHOP_MYSTERY_BOX_KEY" -> user.copy(ptPoints = newBalance, mysteryBoxesOwned = user.mysteryBoxesOwned + 1, updatedAtMillis = now)
                    else -> user.copy(ptPoints = newBalance, removeWrongUses = user.removeWrongUses + 2, updatedAtMillis = now)
                }
                "PERMANENT" -> user.copy(
                    ptPoints = newBalance,
                    easyBonusUnlocked = true,
                    updatedAtMillis = now
                )
                "MULTIPLIER" -> user.copy(
                    ptPoints = newBalance,
                    activeMultiplierItemId = item.id,
                    activeMultiplierGain = item.multiplierGain,
                    activeMultiplierPenalty = item.multiplierPenalty,
                    multiplierExpiresAtMillis = now + item.durationHours * 3600_000L,
                    updatedAtMillis = now
                )
                else -> user.copy(ptPoints = newBalance, updatedAtMillis = now)
            }

            dao.updateUser(updatedUser)
            dao.insertShopPurchase(ShopPurchaseEntity(userId = userId, itemId = item.id, pricePaid = item.pricePt, purchasedAtMillis = now))
            dao.insertTransaction(
                TransactionEntity(
                    txKey = txKey,
                    userId = userId,
                    txType = "SHOP_PURCHASE",
                    xpDelta = 0,
                    ptPointsDelta = -item.pricePt,
                    balanceAfter = newBalance,
                    note = item.name(lang),
                    createdAtMillis = now
                )
            )
            evaluateAndUnlockAchievements(updatedUser, customCosmeticSet = item.category == "AVATAR_FRAME", nowMillis = now)
            Result.success(updatedUser)
        }
    }

    // --- Daily Lucky Wheel & Mystery Chests ---
    suspend fun spinLuckyWheel(
        userId: Long,
        requestNonce: String,
        lang: AppLanguage
    ): Result<LuckyWheelSlice> {
        return db.withTransaction {
            val txKey = "WHEEL_${userId}_$requestNonce"
            if (dao.getTransactionByKey(txKey) != null) {
                return@withTransaction Result.failure(IllegalStateException("Already spun"))
            }
            val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            val now = System.currentTimeMillis()
            val today = SecurityUtils.currentBangkokDateString(now)

            val hasFreeDaily = user.lastFreeSpinDateBangkok != today
            val hasTicket = user.luckyWheelTickets > 0
            val spinPtCost = if (!hasFreeDaily && !hasTicket) 60 else 0

            if (spinPtCost > 0 && user.ptPoints < spinPtCost) {
                return@withTransaction Result.failure(
                    IllegalArgumentException(
                        tr(
                            lang,
                            "สิทธิ์หมุนฟรีวันนี้ใช้แล้ว ต้องใช้ตั๋วหรือ 60 PT Points เพื่อหมุนเพิ่ม!",
                            "Free daily spin used! Need 1 Ticket or 60 PT Points.",
                            "今日免费次数已用！额外旋转需1张奖券或60 PT积分。"
                        )
                    )
                )
            }

            // Weighted slice selection
            val totalWeight = GameCatalog.luckyWheelSlices.sumOf { it.weight }
            var pick = rng.nextInt(totalWeight)
            var chosen = GameCatalog.luckyWheelSlices.first()
            for (slice in GameCatalog.luckyWheelSlices) {
                if (pick < slice.weight) {
                    chosen = slice
                    break
                }
                pick -= slice.weight
            }

            val xpFactor = getXpBaseFactor()
            val xpAdd = if (chosen.rewardType == "XP") chosen.amount else 0
            val ptAdd = when (chosen.rewardType) {
                "PT", "JACKPOT_PT" -> chosen.amount
                else -> 0
            }
            val nextXp = user.xp + xpAdd
            val nextLvl = ScoringAndPetEngine.computeLevelProgress(nextXp, xpFactor).first
            val nextPt = user.ptPoints - spinPtCost + ptAdd
            val nextTickets = if (!hasFreeDaily && hasTicket) user.luckyWheelTickets - 1 else user.luckyWheelTickets

            val updated = user.copy(
                xp = nextXp,
                level = nextLvl,
                ptPoints = nextPt,
                lastFreeSpinDateBangkok = if (hasFreeDaily) today else user.lastFreeSpinDateBangkok,
                luckyWheelTickets = nextTickets,
                timePotionUses = user.timePotionUses + (if (chosen.rewardType == "TIME_POTION") chosen.amount else 0),
                removeWrongUses = user.removeWrongUses + (if (chosen.rewardType == "REMOVE_WRONG") chosen.amount else 0),
                mysteryBoxesOwned = user.mysteryBoxesOwned + (if (chosen.rewardType == "MYSTERY_BOX") chosen.amount else 0),
                wheelsSpunCount = user.wheelsSpunCount + 1,
                updatedAtMillis = now
            )
            dao.updateUser(updated)
            dao.insertTransaction(
                TransactionEntity(
                    txKey = txKey,
                    userId = userId,
                    txType = "LUCKY_WHEEL",
                    xpDelta = xpAdd,
                    ptPointsDelta = ptAdd - spinPtCost,
                    balanceAfter = nextPt,
                    note = "Wheel: ${chosen.label(lang)}",
                    createdAtMillis = now
                )
            )
            incrementQuestMetrics(userId = userId, luckyWheelSpun = 1, nowMillis = now)
            evaluateAndUnlockAchievements(updated, nowMillis = now)
            Result.success(chosen)
        }
    }

    suspend fun openMysteryChest(
        userId: Long,
        chest: MysteryChestSpec,
        requestNonce: String,
        lang: AppLanguage
    ): Result<MysteryChestRewardOutcome> {
        return db.withTransaction {
            val txKey = "CHEST_${userId}_${chest.id}_$requestNonce"
            if (dao.getTransactionByKey(txKey) != null) {
                return@withTransaction Result.failure(IllegalStateException("Already opened"))
            }
            val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            val now = System.currentTimeMillis()

            val useFreeBoxKey = chest.canUseKey && user.mysteryBoxesOwned > 0
            val ptCost = if (useFreeBoxKey) 0 else chest.costPt
            if (ptCost > 0 && user.ptPoints < ptCost) {
                return@withTransaction Result.failure(
                    IllegalArgumentException(
                        tr(lang, "PT Points ไม่พอเปิดหีบ (${chest.costPt} PT)", "Need ${chest.costPt} PT Points to open this chest.", "开启该宝箱需要 ${chest.costPt} PT积分。")
                    )
                )
            }

            val ownedFrames = dao.getOwnedFramesOnce(userId).map { it.frameId }.toSet()
            var ptWon = 0
            var xpWon = 0
            var unlockedFrame: String? = null
            var extraTimePotion = 0
            var extraShield = 0
            val roll = rng.nextInt(100)

            val outcome: MysteryChestRewardOutcome = when (chest.id) {
                "CHEST_BRONZE" -> when {
                    roll < 45 -> {
                        ptWon = (45..95).random()
                        xpWon = 60
                        MysteryChestRewardOutcome(chest, "เหรียญสายรุ้ง +$ptWon PT & +60 XP!", "+$ptWon PT & +60 XP!", "获得 +$ptWon PT 与 +60 XP！", ptWon, xpWon)
                    }
                    roll < 80 -> {
                        xpWon = 130
                        extraTimePotion = 1
                        MysteryChestRewardOutcome(chest, "+130 XP & ขวดยาเพิ่มเวลาสอบ +1!", "+130 XP & +1 Time Potion!", "+130 XP 与 1瓶延时药水！", 0, xpWon, bonusItemNote = "Time Potion +1")
                    }
                    else -> {
                        ptWon = 110
                        xpWon = 90
                        MysteryChestRewardOutcome(chest, "โบนัสผู้กล้า +110 PT & +90 XP!", "Lucky Bonus +110 PT & +90 XP!", "幸运大奖 +110 PT 与 +90 XP！", ptWon, xpWon)
                    }
                }
                "CHEST_RAINBOW" -> when {
                    roll < 35 && "FRAME_GOLDEN_SOVEREIGN" !in ownedFrames -> {
                        unlockedFrame = "FRAME_GOLDEN_SOVEREIGN"
                        xpWon = 180
                        ptWon = 120
                        MysteryChestRewardOutcome(chest, "👑 ปลดล็อกกรอบราชันย์ทองคำ + 120 PT!", "👑 Golden Sovereign Frame + 120 PT!", "👑 解锁黄金王者头像框 + 120 PT！", ptWon, xpWon, unlockedFrame)
                    }
                    roll < 70 -> {
                        extraShield = 1
                        ptWon = (140..240).random()
                        xpWon = 160
                        MysteryChestRewardOutcome(chest, "🛡️ โล่ Streak Shield +1 & +$ptWon PT!", "🛡️ Streak Shield +1 & +$ptWon PT!", "🛡️ 连胜护盾 +1 与 +$ptWon PT！", ptWon, xpWon, bonusItemNote = "Streak Shield +1")
                    }
                    else -> {
                        ptWon = 290
                        xpWon = 220
                        MysteryChestRewardOutcome(chest, "🌈 แจ็กพอตคริสตัล +290 PT & +220 XP!", "🌈 Crystal Jackpot +290 PT & +220 XP!", "🌈 水晶大奖 +290 PT 与 +220 XP！", ptWon, xpWon)
                    }
                }
                else -> { // CHEST_GRAND_MASTER
                    if ("FRAME_DIAMOND_SCHOLAR" !in ownedFrames && roll < 55) {
                        unlockedFrame = "FRAME_DIAMOND_SCHOLAR"
                        ptWon = 300
                        xpWon = 350
                        MysteryChestRewardOutcome(chest, "💎 ปลดล็อกกรอบปราชญ์เพชร ม.1 + 300 PT!", "💎 Diamond Scholar Frame + 300 PT!", "💎 解锁初一钻石学者头像框 + 300 PT！", ptWon, xpWon, unlockedFrame)
                    } else {
                        ptWon = (380..620).random()
                        xpWon = 420
                        extraShield = 1
                        MysteryChestRewardOutcome(chest, "👑 มหาสมบัติปราชญ์ +$ptWon PT, +420 XP & โล่ไฟ!", "👑 Grand Master Treasure +$ptWon PT, +420 XP & Shield!", "👑 至尊秘宝 +$ptWon PT、+420 XP 与护盾！", ptWon, xpWon)
                    }
                }
            }

            if (unlockedFrame != null) {
                dao.insertOwnedFrame(OwnedFrameEntity(userId = userId, frameId = unlockedFrame, acquiredAtMillis = now))
            }

            val xpFactor = getXpBaseFactor()
            val nextXp = user.xp + xpWon
            val nextLvl = ScoringAndPetEngine.computeLevelProgress(nextXp, xpFactor).first
            val nextPt = user.ptPoints - ptCost + ptWon

            val updated = user.copy(
                xp = nextXp,
                level = nextLvl,
                ptPoints = nextPt,
                mysteryBoxesOwned = if (useFreeBoxKey) (user.mysteryBoxesOwned - 1).coerceAtLeast(0) else user.mysteryBoxesOwned,
                timePotionUses = user.timePotionUses + extraTimePotion,
                streakShieldCount = user.streakShieldCount + extraShield,
                equippedFrameId = unlockedFrame ?: user.equippedFrameId,
                chestsOpenedCount = user.chestsOpenedCount + 1,
                updatedAtMillis = now
            )
            dao.updateUser(updated)
            dao.insertTransaction(
                TransactionEntity(
                    txKey = txKey,
                    userId = userId,
                    txType = "MYSTERY_CHEST",
                    xpDelta = xpWon,
                    ptPointsDelta = ptWon - ptCost,
                    balanceAfter = nextPt,
                    note = outcome.title(lang),
                    createdAtMillis = now
                )
            )
            incrementQuestMetrics(userId = userId, chestOpened = 1, nowMillis = now)
            evaluateAndUnlockAchievements(updated, nowMillis = now)
            Result.success(outcome)
        }
    }

    // --- Boss Raid Victory ---
    suspend fun completeBossRaidVictory(
        userId: Long,
        boss: BossSpec,
        remainingHearts: Int,
        maxCombo: Int,
        requestNonce: String,
        lang: AppLanguage
    ): Result<Triple<Int, Int, String?>> {
        return db.withTransaction {
            val txKey = "BOSS_WIN_${userId}_${boss.id}_$requestNonce"
            if (dao.getTransactionByKey(txKey) != null) {
                return@withTransaction Result.failure(IllegalStateException("Already recorded"))
            }
            val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            val now = System.currentTimeMillis()

            val comboXpBonus = maxCombo * 12 + remainingHearts * 20
            val xpWon = boss.rewardXp + comboXpBonus
            val ptWon = boss.rewardPt + remainingHearts * 10
            val xpFactor = getXpBaseFactor()
            val nextXp = user.xp + xpWon
            val nextLvl = ScoringAndPetEngine.computeLevelProgress(nextXp, xpFactor).first
            val nextPt = user.ptPoints + ptWon

            var newlyUnlockedFrame: String? = null
            if (boss.rewardFrameId != null) {
                val owned = dao.getOwnedFramesOnce(userId).any { it.frameId == boss.rewardFrameId }
                if (!owned) {
                    dao.insertOwnedFrame(OwnedFrameEntity(userId = userId, frameId = boss.rewardFrameId, acquiredAtMillis = now))
                    newlyUnlockedFrame = boss.rewardFrameId
                }
            }

            val updated = user.copy(
                xp = nextXp,
                level = nextLvl,
                ptPoints = nextPt,
                bossesDefeatedCount = user.bossesDefeatedCount + 1,
                equippedFrameId = newlyUnlockedFrame ?: user.equippedFrameId,
                updatedAtMillis = now
            )
            dao.updateUser(updated)
            dao.insertTransaction(
                TransactionEntity(
                    txKey = txKey,
                    userId = userId,
                    txType = "BOSS_RAID",
                    xpDelta = xpWon,
                    ptPointsDelta = ptWon,
                    balanceAfter = nextPt,
                    note = "Defeated ${boss.name(lang)}",
                    createdAtMillis = now
                )
            )
            val raidScore = boss.maxHp / 2 + maxCombo * 25 + remainingHearts * 40
            addScoreToLeaderboard("MODE_BOSS_RAID", updated, raidScore, now)
            addScoreToLeaderboard("SUBJECT_${boss.subject.id}", updated, raidScore / 2, now)
            addScoreToLeaderboard("OVERALL_TOTAL", updated, raidScore, now)

            incrementQuestMetrics(userId = userId, bossDefeated = 1, nowMillis = now)
            evaluateAndUnlockAchievements(updated, nowMillis = now)
            Result.success(Triple(xpWon, ptWon, newlyUnlockedFrame))
        }
    }

    // --- Flashcards & Study Planner ---
    suspend fun markFlashcardReviewed(userId: Long, card: FlashcardEntity): Result<Pair<Int, Int>> {
        return db.withTransaction {
            val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            val now = System.currentTimeMillis()
            val currentIds = card.masteredByUserIdsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
            val firstTimeMaster = currentIds.add(userId.toString())
            dao.upsertFlashcard(card.copy(masteredByUserIdsCsv = currentIds.joinToString(",")))

            val xpAdd = if (firstTimeMaster) 20 else 8
            val ptAdd = if (firstTimeMaster) 5 else 1
            val factor = getXpBaseFactor()
            val nextXp = user.xp + xpAdd
            val nextLvl = ScoringAndPetEngine.computeLevelProgress(nextXp, factor).first
            val nextPt = user.ptPoints + ptAdd
            val updated = user.copy(
                xp = nextXp,
                level = nextLvl,
                ptPoints = nextPt,
                flashcardsReviewedCount = user.flashcardsReviewedCount + 1,
                updatedAtMillis = now
            )
            dao.updateUser(updated)
            incrementQuestMetrics(userId = userId, flashcardsReviewed = 1, nowMillis = now)
            evaluateAndUnlockAchievements(updated, nowMillis = now)
            Result.success(xpAdd to ptAdd)
        }
    }

    suspend fun createCustomFlashcard(
        userId: Long,
        subject: SubjectId,
        frontText: String,
        backText: String,
        exampleText: String
    ): Long {
        return dao.upsertFlashcard(
            FlashcardEntity(
                userId = userId,
                subjectId = subject.id,
                frontTh = frontText.trim(),
                frontEn = frontText.trim(),
                frontZh = frontText.trim(),
                backTh = backText.trim(),
                backEn = backText.trim(),
                backZh = backText.trim(),
                exampleText = exampleText.trim()
            )
        )
    }

    suspend fun addStudyPlanTask(
        userId: Long,
        subject: SubjectId,
        title: String,
        targetMinutes: Int,
        targetDateBangkok: String
    ): Long {
        return dao.upsertStudyPlanTask(
            StudyPlanTaskEntity(
                userId = userId,
                subjectId = subject.id,
                title = title.trim(),
                targetMinutes = targetMinutes.coerceIn(5, 180),
                targetDateBangkok = targetDateBangkok
            )
        )
    }

    suspend fun toggleCompleteStudyPlanTask(userId: Long, taskId: Long): Result<Pair<Int, Int>> {
        return db.withTransaction {
            val task = dao.getStudyPlanTaskById(taskId, userId)
                ?: return@withTransaction Result.failure(IllegalStateException("Task not found"))
            val user = dao.getUserById(userId)
                ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            val now = System.currentTimeMillis()

            if (!task.isCompleted && !task.rewardClaimed) {
                val xpAdd = 40
                val ptAdd = 15
                val factor = getXpBaseFactor()
                val nextXp = user.xp + xpAdd
                val nextLvl = ScoringAndPetEngine.computeLevelProgress(nextXp, factor).first
                val nextPt = user.ptPoints + ptAdd

                dao.upsertStudyPlanTask(task.copy(isCompleted = true, rewardClaimed = true))
                val updated = user.copy(
                    xp = nextXp,
                    level = nextLvl,
                    ptPoints = nextPt,
                    studyTasksCompletedCount = user.studyTasksCompletedCount + 1,
                    updatedAtMillis = now
                )
                dao.updateUser(updated)
                dao.insertTransaction(
                    TransactionEntity(
                        txKey = "STUDY_TASK_${userId}_${taskId}",
                        userId = userId,
                        txType = "STUDY_PLANNER",
                        xpDelta = xpAdd,
                        ptPointsDelta = ptAdd,
                        balanceAfter = nextPt,
                        note = task.title,
                        createdAtMillis = now
                    )
                )
                incrementQuestMetrics(userId = userId, studyPlanDone = 1, nowMillis = now)
                evaluateAndUnlockAchievements(updated, nowMillis = now)
                Result.success(xpAdd to ptAdd)
            } else {
                dao.upsertStudyPlanTask(task.copy(isCompleted = !task.isCompleted))
                Result.success(0 to 0)
            }
        }
    }

    suspend fun deleteStudyPlanTask(userId: Long, taskId: Long) {
        dao.deleteStudyPlanTask(taskId, userId)
    }

    // --- PT Pet Zone: Atomic Egg Purchase & Hatch (500 PT Points, 28 Species) ---
    suspend fun purchaseAndHatchEgg(
        userId: Long,
        requestNonce: String,
        lang: AppLanguage = AppLanguage.TH
    ): Result<Triple<EggRarity, PetSpecies, Long>> {
        return db.withTransaction {
            val txKey = "EGG_HATCH_${userId}_$requestNonce"
            if (dao.getTransactionByKey(txKey) != null) {
                return@withTransaction Result.failure(IllegalStateException("Already hatched"))
            }
            val user = dao.getUserById(userId)
                ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            if (user.ptPoints < 500) {
                return@withTransaction Result.failure(
                    IllegalArgumentException(
                        tr(
                            lang,
                            "PT Points ไม่เพียงพอ (ต้องใช้ 500 PT Points ต่อ 1 ฟอง)",
                            "Not enough PT Points (500 PT required per Egg).",
                            "PT积分不足（孵化1颗蛋需要 500 PT）。"
                        )
                    )
                )
            }

            val now = System.currentTimeMillis()
            val (rarity, species) = ScoringAndPetEngine.rollEggAndPet()
            val newBalance = user.ptPoints - 500

            val instanceId = dao.insertPetInstance(
                PetInstanceEntity(
                    userId = userId,
                    speciesId = species.id,
                    rarityId = rarity.id,
                    obtainedAtMillis = now
                )
            )
            dao.insertEggHistory(
                EggHistoryEntity(
                    userId = userId,
                    eggRarityId = rarity.id,
                    petSpeciesId = species.id,
                    costPt = 500,
                    openedAtMillis = now
                )
            )

            val autoEquipId = user.equippedPetInstanceId ?: instanceId
            val updatedUser = user.copy(
                ptPoints = newBalance,
                equippedPetInstanceId = autoEquipId,
                updatedAtMillis = now
            )
            dao.updateUser(updatedUser)
            dao.insertTransaction(
                TransactionEntity(
                    txKey = txKey,
                    userId = userId,
                    txType = "EGG_OPEN",
                    xpDelta = 0,
                    ptPointsDelta = -500,
                    balanceAfter = newBalance,
                    note = "Hatched ${species.name(lang)} (${rarity.id})",
                    createdAtMillis = now
                )
            )
            evaluateAndUnlockAchievements(updatedUser, hatchedRarity = rarity, nowMillis = now)
            Result.success(Triple(rarity, species, instanceId))
        }
    }

    suspend fun equipPet(userId: Long, instanceId: Long?) {
        val user = dao.getUserById(userId) ?: return
        dao.updateUser(user.copy(equippedPetInstanceId = instanceId, updatedAtMillis = System.currentTimeMillis()))
    }

    suspend fun discardPet(userId: Long, instanceId: Long) {
        db.withTransaction {
            val user = dao.getUserById(userId) ?: return@withTransaction
            if (user.equippedPetInstanceId == instanceId) {
                dao.updateUser(user.copy(equippedPetInstanceId = null, updatedAtMillis = System.currentTimeMillis()))
            }
            dao.deletePetInstance(instanceId, userId)
        }
    }

    // --- Mini-Games Score & Reward ---
    suspend fun recordMiniGameScore(
        userId: Long,
        game: MiniGameId,
        score: Int,
        requestNonce: String
    ): Result<Pair<Int, Int>> {
        return db.withTransaction {
            val txKey = "MINIGAME_${userId}_${game.id}_$requestNonce"
            if (dao.getTransactionByKey(txKey) != null) {
                return@withTransaction Result.failure(IllegalStateException("Already saved"))
            }
            val user = dao.getUserById(userId) ?: return@withTransaction Result.failure(IllegalStateException("User not found"))
            val now = System.currentTimeMillis()
            val earnedXp = (score / 4).coerceIn(10, 150)
            val earnedPt = (score / 20).coerceIn(2, 35)
            val factor = getXpBaseFactor()
            val newXp = user.xp + earnedXp
            val newLvl = ScoringAndPetEngine.computeLevelProgress(newXp, factor).first
            val newPt = user.ptPoints + earnedPt

            val updated = user.copy(xp = newXp, level = newLvl, ptPoints = newPt, updatedAtMillis = now)
            dao.updateUser(updated)
            dao.insertTransaction(
                TransactionEntity(
                    txKey = txKey,
                    userId = userId,
                    txType = "MINIGAME",
                    xpDelta = earnedXp,
                    ptPointsDelta = earnedPt,
                    balanceAfter = newPt,
                    note = "${game.titleTh}: $score pts",
                    createdAtMillis = now
                )
            )
            addScoreToLeaderboard("GAME_${game.id}", updated, score.coerceAtLeast(5), now)
            addScoreToLeaderboard("OVERALL_TOTAL", updated, (score / 2).coerceAtLeast(5), now)
            incrementQuestMetrics(userId = userId, miniGamesPlayed = 1, nowMillis = now)
            evaluateAndUnlockAchievements(updated, nowMillis = now)
            Result.success(earnedXp to earnedPt)
        }
    }

    // --- Study Sheet & AI Tutor Persistence ---
    suspend fun saveStudySheet(sheet: StudySheetEntity): Long = dao.insertStudySheet(sheet)
    suspend fun deleteStudySheet(sheetId: Long, userId: Long) = dao.deleteStudySheet(sheetId, userId)
    suspend fun addTutorMessage(userId: Long, role: String, content: String, modelTag: String) {
        dao.insertTutorMessage(TutorMessageEntity(userId = userId, role = role, content = content, modelTag = modelTag))
    }
    suspend fun clearTutorHistory(userId: Long) = dao.clearTutorMessages(userId)

    suspend fun submitFeedbackReport(userId: Long, username: String, category: String, message: String): Result<Unit> {
        val bannedRaw = dao.getSystemConfigValue("BANNED_WORDS").orEmpty()
        val bannedList = bannedRaw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (bannedList.any { message.contains(it, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("Message contains restricted words"))
        }
        dao.insertFeedbackReport(FeedbackReportEntity(userId = userId, username = username, category = category, message = message.trim()))
        return Result.success(Unit)
    }

    suspend fun markNotificationsRead(userId: Long) = dao.markAllNotificationsRead(userId)

    // =========================================================================
    // ADMIN PANEL OPERATIONS (Server/Engine Validated + Immutable Audit Logs)
    // =========================================================================

    suspend fun adminGrantReward(
        targetUsername: String,
        xpAdd: Int = 0,
        levelAdd: Int = 0,
        streakAdd: Int = 0,
        ptAdd: Int = 0,
        reason: String = "Admin Grant"
    ): Result<UserEntity> {
        return db.withTransaction {
            val target = dao.getUserByUsername(targetUsername.trim())
                ?: return@withTransaction Result.failure(IllegalArgumentException("User '$targetUsername' not found"))
            val factor = getXpBaseFactor()
            val now = System.currentTimeMillis()

            val oldSummary = "Lv.${target.level}, XP=${target.xp}, PT=${target.ptPoints}, Streak=${target.streakDays}"
            var nextXp = target.xp + xpAdd
            if (levelAdd > 0) {
                val targetLvl = (target.level + levelAdd).coerceAtMost(999)
                val minXpForLvl = ScoringAndPetEngine.xpToReachLevel(targetLvl, factor)
                if (nextXp < minXpForLvl) nextXp = minXpForLvl
            }
            val nextLvl = ScoringAndPetEngine.computeLevelProgress(nextXp, factor).first
            val nextStreak = (target.streakDays + streakAdd).coerceAtLeast(0)
            val nextPt = target.ptPoints + ptAdd

            val updated = target.copy(
                xp = nextXp,
                level = nextLvl,
                ptPoints = nextPt,
                streakDays = nextStreak,
                updatedAtMillis = now
            )
            dao.updateUser(updated)

            val rewardLabel = buildList {
                if (xpAdd > 0) add("+$xpAdd XP")
                if (levelAdd > 0) add("+$levelAdd Level")
                if (streakAdd > 0) add("+$streakAdd Day Streak")
                if (ptAdd != 0) add("${if (ptAdd > 0) "+$ptAdd" else "$ptAdd"} PT")
            }.joinToString(", ")

            dao.insertNotification(
                NotificationEntity(
                    userId = target.id,
                    category = "ADMIN_REWARD",
                    message = "Get $rewardLabel by Admin!"
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    txKey = "ADMIN_${target.id}_${now}_${(100..999).random()}",
                    userId = target.id,
                    txType = "ADMIN_GRANT",
                    xpDelta = (nextXp - target.xp).coerceAtLeast(0),
                    ptPointsDelta = ptAdd,
                    balanceAfter = nextPt,
                    note = "Get $rewardLabel by Admin!",
                    createdAtMillis = now
                )
            )
            dao.insertAdminAuditLog(
                AdminAuditLogEntity(
                    targetUsername = target.username,
                    actionType = "GRANT_REWARD",
                    oldValue = oldSummary,
                    newValue = "Lv.${updated.level}, XP=${updated.xp}, PT=${updated.ptPoints}, Streak=${updated.streakDays}",
                    reason = reason,
                    timestampMillis = now
                )
            )
            Result.success(updated)
        }
    }

    suspend fun adminBanUser(
        targetUsername: String,
        durationMillis: Long?,
        durationLabel: String,
        reason: String
    ): Result<Unit> {
        return db.withTransaction {
            val target = dao.getUserByUsername(targetUsername.trim())
                ?: return@withTransaction Result.failure(IllegalArgumentException("User '$targetUsername' not found"))
            val now = System.currentTimeMillis()
            val until = durationMillis?.let { now + it } ?: Long.MAX_VALUE
            val cleanReason = reason.trim().ifBlank { "Community policy" }

            dao.updateUser(
                target.copy(
                    accountStatus = "BANNED",
                    banStartedAtMillis = now,
                    banUntilMillis = until,
                    banReason = cleanReason,
                    updatedAtMillis = now
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    userId = target.id,
                    category = "ADMIN_BAN",
                    message = "Banned $durationLabel by Admin... ($cleanReason)"
                )
            )
            dao.insertAdminAuditLog(
                AdminAuditLogEntity(
                    targetUsername = target.username,
                    actionType = "BAN_$durationLabel",
                    oldValue = target.accountStatus,
                    newValue = "BANNED ($durationLabel)",
                    reason = cleanReason,
                    timestampMillis = now
                )
            )
            Result.success(Unit)
        }
    }

    suspend fun adminUnbanOrRestoreUser(targetUsername: String, reason: String = "Admin Restore"): Result<Unit> {
        return db.withTransaction {
            val target = dao.getUserByUsername(targetUsername.trim())
                ?: return@withTransaction Result.failure(IllegalArgumentException("User '$targetUsername' not found"))
            val now = System.currentTimeMillis()
            dao.updateUser(
                target.copy(
                    accountStatus = "ACTIVE",
                    banStartedAtMillis = 0L,
                    banUntilMillis = 0L,
                    banReason = "",
                    suspensionRecoveryCompletedCount = 0,
                    suspensionRecoveryRequested = false,
                    updatedAtMillis = now
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    userId = target.id,
                    category = "SYSTEM",
                    message = "Account restored to ACTIVE by Admin!"
                )
            )
            dao.insertAdminAuditLog(
                AdminAuditLogEntity(
                    targetUsername = target.username,
                    actionType = "RESTORE_ACCOUNT",
                    oldValue = target.accountStatus,
                    newValue = "ACTIVE",
                    reason = reason,
                    timestampMillis = now
                )
            )
            Result.success(Unit)
        }
    }

    suspend fun adminTemporarySuspendUser(targetUsername: String, reason: String): Result<Unit> {
        return db.withTransaction {
            val target = dao.getUserByUsername(targetUsername.trim())
                ?: return@withTransaction Result.failure(IllegalArgumentException("User '$targetUsername' not found"))
            val now = System.currentTimeMillis()
            dao.updateUser(
                target.copy(
                    accountStatus = "SUSPENDED",
                    banReason = reason.ifBlank { "Temporary suspension" },
                    suspensionRecoveryCompletedCount = 0,
                    suspensionRecoveryRequested = false,
                    updatedAtMillis = now
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    userId = target.id,
                    category = "ADMIN_SUSPEND",
                    message = "Suspended temporarily by Admin — complete 5 exercises to restore."
                )
            )
            dao.insertAdminAuditLog(
                AdminAuditLogEntity(
                    targetUsername = target.username,
                    actionType = "TEMPORARY_SUSPENSION",
                    oldValue = target.accountStatus,
                    newValue = "SUSPENDED",
                    reason = reason,
                    timestampMillis = now
                )
            )
            Result.success(Unit)
        }
    }

    suspend fun adminDeleteUserForever(targetUsername: String, reason: String): Result<Unit> {
        return db.withTransaction {
            val target = dao.getUserByUsername(targetUsername.trim())
                ?: return@withTransaction Result.failure(IllegalArgumentException("User '$targetUsername' not found"))
            val now = System.currentTimeMillis()
            val activeUid = activeUserIdFlow.first()
            if (activeUid == target.id) {
                setSessionUserId(null)
            }
            dao.deleteLeaderboardsForUser(target.id)
            dao.updateUser(
                target.copy(
                    username = "deleted_${target.id}_$now",
                    displayName = "[Deleted Account]",
                    passwordHash = "",
                    recoveryCodeHash = "",
                    recoveryCodePlain = "",
                    accountStatus = "DELETED",
                    updatedAtMillis = now
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    userId = target.id,
                    category = "SYSTEM",
                    message = "Deleted your account by Admin... [Farewell 😞]"
                )
            )
            dao.insertAdminAuditLog(
                AdminAuditLogEntity(
                    targetUsername = targetUsername,
                    actionType = "DELETE_FOREVER",
                    oldValue = target.accountStatus,
                    newValue = "DELETED_ANONYMIZED",
                    reason = reason.ifBlank { "Permanent deletion confirmed by Admin" },
                    timestampMillis = now
                )
            )
            Result.success(Unit)
        }
    }

    suspend fun adminSaveQuestion(question: QuestionEntity, isNew: Boolean) {
        val now = System.currentTimeMillis()
        if (isNew) {
            val count = dao.getQuestionCount() + 1
            dao.insertQuestion(question.copy(questionNumber = count, createdAtMillis = now))
        } else {
            dao.updateQuestion(question.copy(questionVersion = question.questionVersion + 1))
        }
        dao.insertAdminAuditLog(
            AdminAuditLogEntity(
                targetUsername = "QUESTION_BANK",
                actionType = if (isNew) "ADD_QUESTION" else "UPDATE_QUESTION",
                oldValue = question.id.toString(),
                newValue = "${question.subjectId}: ${question.questionTh.take(40)}",
                reason = "Question Bank Management",
                timestampMillis = now
            )
        )
    }

    suspend fun adminDeleteQuestion(questionId: Long) {
        dao.deleteQuestion(questionId)
        dao.insertAdminAuditLog(
            AdminAuditLogEntity(
                targetUsername = "QUESTION_BANK",
                actionType = "DELETE_QUESTION",
                oldValue = "ID=$questionId",
                newValue = "DELETED",
                reason = "Admin removed question"
            )
        )
    }

    suspend fun adminImportQuestionsCsv(csvContent: String): Int {
        val lines = csvContent.lines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("SubjectId", ignoreCase = true) }
        var imported = 0
        var qNum = dao.getQuestionCount() + 1
        for (line in lines) {
            val parts = line.split(",").map { it.trim() }
            if (parts.size >= 10) {
                val sub = SubjectId.fromId(parts[0].uppercase())
                val ans = parts[8].uppercase()
                if (ans in setOf("A", "B", "C", "D")) {
                    dao.insertQuestion(
                        QuestionEntity(
                            questionNumber = qNum++,
                            subjectId = sub.id,
                            topic = parts[1],
                            difficulty = DifficultyLevel.fromId(parts[2].uppercase()).id,
                            questionTh = parts[3],
                            questionEn = parts[3],
                            questionZh = parts[3],
                            choiceATh = parts[4], choiceBTh = parts[5], choiceCTh = parts[6], choiceDTh = parts[7],
                            choiceAEn = parts[4], choiceBEn = parts[5], choiceCEn = parts[6], choiceDEn = parts[7],
                            choiceAZh = parts[4], choiceBZh = parts[5], choiceCZh = parts[6], choiceDZh = parts[7],
                            correctAnswer = ans,
                            explanationTh = parts[9],
                            explanationEn = parts[9],
                            explanationZh = parts[9],
                            reviewStatus = "APPROVED"
                        )
                    )
                    imported++
                }
            }
        }
        if (imported > 0) {
            dao.insertAdminAuditLog(
                AdminAuditLogEntity(
                    targetUsername = "QUESTION_BANK",
                    actionType = "IMPORT_CSV",
                    oldValue = "-",
                    newValue = "Imported $imported questions",
                    reason = "CSV Batch Import"
                )
            )
        }
        return imported
    }

    suspend fun adminExportQuestionsCsv(): String {
        val all = dao.getAllApprovedQuestionsOnce()
        val header = "SubjectId,Topic,Difficulty,QuestionTh,ChoiceA,ChoiceB,ChoiceC,ChoiceD,CorrectAnswer,ExplanationTh"
        val rows = all.map { q ->
            listOf(
                q.subjectId, q.topic, q.difficulty,
                q.questionTh.replace(",", " "),
                q.choiceATh.replace(",", " "),
                q.choiceBTh.replace(",", " "),
                q.choiceCTh.replace(",", " "),
                q.choiceDTh.replace(",", " "),
                q.correctAnswer,
                q.explanationTh.replace(",", " ")
            ).joinToString(",")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    suspend fun adminAddQuest(quest: QuestDefinitionEntity) {
        dao.insertQuest(quest)
        dao.insertAdminAuditLog(
            AdminAuditLogEntity(
                targetUsername = "QUEST_SYSTEM",
                actionType = "CREATE_QUEST",
                oldValue = "-",
                newValue = "${quest.questScope}: ${quest.titleTh}",
                reason = "Content Management"
            )
        )
    }

    suspend fun adminUpdateConfig(key: String, value: String) {
        val prev = dao.getSystemConfigValue(key).orEmpty()
        dao.setSystemConfig(SystemConfigEntity(key, value))
        dao.insertAdminAuditLog(
            AdminAuditLogEntity(
                targetUsername = "SYSTEM_CONFIG",
                actionType = "SET_$key",
                oldValue = prev,
                newValue = if (key == "ADMIN_PASSCODE_HASH") "[SHA256_HASH]" else value,
                reason = "System Configuration Update"
            )
        )
    }

    suspend fun adminResolveFeedback(reportId: Long) {
        dao.resolveFeedbackReport(reportId)
    }
}
