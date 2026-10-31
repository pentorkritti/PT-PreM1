package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAiService
import com.example.data.local.AchievementDefEntity
import com.example.data.local.AdminAuditLogEntity
import com.example.data.local.ExamSessionEntity
import com.example.data.local.FeedbackReportEntity
import com.example.data.local.FlashcardEntity
import com.example.data.local.LeaderboardEntryEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.OwnedBackgroundEntity
import com.example.data.local.OwnedFrameEntity
import com.example.data.local.PetInstanceEntity
import com.example.data.local.QuestDefinitionEntity
import com.example.data.local.QuestionEntity
import com.example.data.local.StudyPlanTaskEntity
import com.example.data.local.StudySheetEntity
import com.example.data.local.SystemConfigEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.TutorMessageEntity
import com.example.data.local.UserAchievementEntity
import com.example.data.local.UserEntity
import com.example.data.local.UserQuestProgressEntity
import com.example.data.local.UserQuestionHistoryEntity
import com.example.data.repository.GameRepository
import com.example.data.repository.MysteryChestRewardOutcome
import com.example.domain.ExamOutcomeSummary
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
import com.example.model.PetSkillType
import com.example.model.PetSpecies
import com.example.model.ShopItemCatalog
import com.example.model.SubjectId
import com.example.util.SecurityUtils
import com.example.util.SfxType
import com.example.util.SoundEngine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.abs

enum class SaveIndicatorState { IDLE, SAVING, SAVED }

data class ActiveExamUiState(
    val session: ExamSessionEntity,
    val questions: List<QuestionEntity>,
    val currentIndex: Int = 0,
    val answers: Map<Long, String> = emptyMap(),
    val lockedQuestionIds: Set<Long> = emptySet(),
    val retriedQuestionIds: Set<Long> = emptySet(),
    val markedForReview: Set<Long> = emptySet(),
    val hiddenOptionsByQuestion: Map<Long, Set<String>> = emptyMap(),
    val unlockedHintsByQuestion: Map<Long, String> = emptyMap(),
    val remainingSeconds: Int = 300,
    val freezeRemainingSeconds: Int = 0,
    val petSkillUsesLeft: Int = 0,
    val lastInstantFeedback: Pair<Boolean, String>? = null
)

data class EggOpeningState(
    val rarity: EggRarity,
    val pet: PetSpecies,
    val instanceId: Long
)

data class DailyCorrectBarPoint(
    val dateBangkok: String,
    val shortLabel: String,
    val correctCount: Int,
    val wrongCount: Int
)

data class MistakeNotebookItem(
    val question: QuestionEntity,
    val lastWrongAnswer: String,
    val wrongTimes: Int,
    val lastAnsweredAtMillis: Long
)

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModel(application: Application) : AndroidViewModel(application) {

    val repo = GameRepository(application.applicationContext)

    private val _isBootstrapping = MutableStateFlow(true)
    val isBootstrapping: StateFlow<Boolean> = _isBootstrapping.asStateFlow()

    private val _saveState = MutableStateFlow(SaveIndicatorState.IDLE)
    val saveState: StateFlow<SaveIndicatorState> = _saveState.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _welcomeCelebrationCode = MutableStateFlow<String?>(null)
    val welcomeCelebrationCode: StateFlow<String?> = _welcomeCelebrationCode.asStateFlow()

    private val _levelUpCelebration = MutableStateFlow<Int?>(null)
    val levelUpCelebration: StateFlow<Int?> = _levelUpCelebration.asStateFlow()

    private val _guestLanguage = MutableStateFlow(AppLanguage.TH)
    val guestLanguage: StateFlow<AppLanguage> = _guestLanguage.asStateFlow()

    val activeUserId: StateFlow<Long?> = repo.activeUserIdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentUser: StateFlow<UserEntity?> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeUser(uid) else flowOf(null) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userPets: StateFlow<List<PetInstanceEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeUserPets(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ownedBackgrounds: StateFlow<List<OwnedBackgroundEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeOwnedBackgrounds(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ownedFrames: StateFlow<List<OwnedFrameEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeOwnedFrames(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashcards: StateFlow<List<FlashcardEntity>> = activeUserId
        .flatMapLatest { uid -> repo.observeFlashcardsForUser(uid ?: 0L) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyPlanTasks: StateFlow<List<StudyPlanTaskEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeStudyPlanTasks(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userQuestionHistory: StateFlow<List<UserQuestionHistoryEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeUserQuestionHistory(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedExams: StateFlow<List<ExamSessionEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeCompletedExams(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactions: StateFlow<List<TransactionEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeUserTransactions(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeQuests: StateFlow<List<QuestDefinitionEntity>> = repo.observeActiveQuests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Deterministically rotated 5 Daily Quests + all Weekly Quests so quests change every single day!
    val todaysQuests: StateFlow<List<QuestDefinitionEntity>> = combine(activeQuests, currentUser) { all, user ->
        val dailies = all.filter { it.questScope == "DAILY" }
        val weeklies = all.filter { it.questScope == "WEEKLY" }
        if (dailies.size <= 5) {
            dailies + weeklies
        } else {
            val todayStr = SecurityUtils.currentBangkokDateString()
            val reroll = if (user?.dailyQuestRerollDateBangkok == todayStr) (user.dailyQuestRerollOffset) else 0
            val daySeed = abs(todayStr.hashCode()) + reroll * 7
            val pickedDaily = (0 until 5).map { idx ->
                dailies[(daySeed + idx * 3) % dailies.size]
            }.distinctBy { it.id }.let { distinct ->
                if (distinct.size < 5) {
                    (distinct + dailies.filter { d -> d.id !in distinct.map { it.id } }).take(5)
                } else distinct
            }
            pickedDaily + weeklies
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userQuestProgress: StateFlow<List<UserQuestProgressEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeUserQuestProgress(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievementDefs: StateFlow<List<AchievementDefEntity>> = repo.observeAchievementDefs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userAchievements: StateFlow<List<UserAchievementEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeUserAchievements(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeUserNotifications(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studySheets: StateFlow<List<StudySheetEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeStudySheets(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tutorMessages: StateFlow<List<TutorMessageEntity>> = activeUserId
        .flatMapLatest { uid -> if (uid != null) repo.observeTutorMessages(uid) else flowOf(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuestions: StateFlow<List<QuestionEntity>> = repo.observeAllQuestions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mistake Review Notebook computed reactively!
    val mistakeNotebookItems: StateFlow<List<MistakeNotebookItem>> = combine(userQuestionHistory, allQuestions) { hist, qs ->
        val qMap = qs.associateBy { it.id }
        hist.filter { !it.isCorrect && !it.isMasteredMistake }
            .groupBy { it.questionId }
            .mapNotNull { (qId, rows) ->
                val q = qMap[qId] ?: return@mapNotNull null
                val latest = rows.maxByOrNull { it.answeredAtMillis } ?: rows.first()
                MistakeNotebookItem(
                    question = q,
                    lastWrongAnswer = latest.selectedAnswer,
                    wrongTimes = rows.size,
                    lastAnsweredAtMillis = latest.answeredAtMillis
                )
            }
            .sortedByDescending { it.lastAnsweredAtMillis }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 7-Day & 14-Day Daily Correct Answers Bar Chart data!
    val dailyCorrectChart14Days: StateFlow<List<DailyCorrectBarPoint>> = combine(userQuestionHistory, completedExams) { hist, exams ->
        val byDateCorrect = mutableMapOf<String, Int>()
        val byDateWrong = mutableMapOf<String, Int>()
        if (hist.isNotEmpty()) {
            hist.forEach { row ->
                val d = row.dateBangkok.ifBlank { SecurityUtils.currentBangkokDateString(row.answeredAtMillis) }
                if (row.isCorrect) {
                    byDateCorrect[d] = (byDateCorrect[d] ?: 0) + 1
                } else {
                    byDateWrong[d] = (byDateWrong[d] ?: 0) + 1
                }
            }
        } else {
            exams.forEach { ex ->
                val d = SecurityUtils.currentBangkokDateString(ex.completedAtMillis)
                byDateCorrect[d] = (byDateCorrect[d] ?: 0) + ex.correctCount
                byDateWrong[d] = (byDateWrong[d] ?: 0) + ex.wrongCount
            }
        }
        (13 downTo 0).map { daysAgo ->
            val dateStr = SecurityUtils.bangkokDateDaysAgo(daysAgo)
            val shortLabel = dateStr.takeLast(5).replace("-", "/") // MM/DD
            DailyCorrectBarPoint(
                dateBangkok = dateStr,
                shortLabel = shortLabel,
                correctCount = byDateCorrect[dateStr] ?: 0,
                wrongCount = byDateWrong[dateStr] ?: 0
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val systemConfigs: StateFlow<List<SystemConfigEntity>> = repo.observeSystemConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Exam State
    private val _activeExam = MutableStateFlow<ActiveExamUiState?>(null)
    val activeExam: StateFlow<ActiveExamUiState?> = _activeExam.asStateFlow()

    private val _lastExamOutcome = MutableStateFlow<Pair<ExamOutcomeSummary, List<Pair<QuestionEntity, String>>>?>(null)
    val lastExamOutcome: StateFlow<Pair<ExamOutcomeSummary, List<Pair<QuestionEntity, String>>>?> = _lastExamOutcome.asStateFlow()

    private var examTimerJob: Job? = null

    // AI Loading States
    private val _isAiGeneratingExam = MutableStateFlow(false)
    val isAiGeneratingExam: StateFlow<Boolean> = _isAiGeneratingExam.asStateFlow()

    private val _aiTutorStreamingText = MutableStateFlow<String?>(null)
    val aiTutorStreamingText: StateFlow<String?> = _aiTutorStreamingText.asStateFlow()

    private val _isAiTutorBusy = MutableStateFlow(false)
    val isAiTutorBusy: StateFlow<Boolean> = _isAiTutorBusy.asStateFlow()

    private val _isGeneratingStudySheet = MutableStateFlow(false)
    val isGeneratingStudySheet: StateFlow<Boolean> = _isGeneratingStudySheet.asStateFlow()

    // Egg, Wheel & Chest States
    private val _eggOpeningState = MutableStateFlow<EggOpeningState?>(null)
    val eggOpeningState: StateFlow<EggOpeningState?> = _eggOpeningState.asStateFlow()

    private val _lastChestOutcome = MutableStateFlow<MysteryChestRewardOutcome?>(null)
    val lastChestOutcome: StateFlow<MysteryChestRewardOutcome?> = _lastChestOutcome.asStateFlow()

    // Admin Session State
    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    val adminUsersList: StateFlow<List<UserEntity>> = repo.observeAllActiveUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val adminAuditLogs: StateFlow<List<AdminAuditLogEntity>> = repo.observeAdminAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val adminFeedbackReports: StateFlow<List<FeedbackReportEntity>> = repo.observeFeedbackReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val adminTotalExams: StateFlow<Int> = repo.observeTotalCompletedExamsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminTotalXp: StateFlow<Int> = repo.observeTotalXpDistributed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminTotalPt: StateFlow<Int> = repo.observeTotalPtInCirculation()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminTotalEggs: StateFlow<Int> = repo.observeTotalEggsOpenedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val adminTotalPurchases: StateFlow<Int> = repo.observeTotalShopPurchasesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repo.ensureInitialSeed()
            _isBootstrapping.value = false
        }
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    SoundEngine.updateSettings(
                        bgmOn = user.bgmEnabled,
                        sfxOn = user.sfxEnabled,
                        bgmVol = user.bgmVolume,
                        sfxVol = user.sfxVolume,
                        muted = user.muteAll
                    )
                }
            }
        }
    }

    fun activeLang(): AppLanguage =
        currentUser.value?.let { AppLanguage.fromCode(it.languageCode) } ?: _guestLanguage.value

    fun observeLeaderboard(boardId: String): Flow<List<LeaderboardEntryEntity>> =
        repo.observeTop10ForBoard(boardId)

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun dismissWelcomeCelebration() {
        _welcomeCelebrationCode.value = null
    }

    fun dismissLevelUpCelebration() {
        _levelUpCelebration.value = null
    }

    fun dismissChestOutcome() {
        _lastChestOutcome.value = null
    }

    private fun pulseAutoSave(block: suspend () -> Unit) {
        viewModelScope.launch {
            _saveState.value = SaveIndicatorState.SAVING
            try {
                block()
                _saveState.value = SaveIndicatorState.SAVED
                delay(1500L)
                if (_saveState.value == SaveIndicatorState.SAVED) {
                    _saveState.value = SaveIndicatorState.IDLE
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _saveState.value = SaveIndicatorState.IDLE
                showToast(e.localizedMessage ?: tr(activeLang(), "เกิดข้อผิดพลาดในการบันทึกข้อมูล", "Error saving data.", "保存数据时出错。"))
            }
        }
    }

    // --- Language & Settings ---
    fun setLanguage(lang: AppLanguage) {
        _guestLanguage.value = lang
        if (currentUser.value != null) {
            pulseAutoSave {
                repo.updateUserSettings { it.copy(languageCode = lang.code) }
            }
        }
    }

    fun updateAudioSettings(bgmOn: Boolean, sfxOn: Boolean, bgmVol: Float, sfxVol: Float, muteAll: Boolean) {
        SoundEngine.updateSettings(bgmOn, sfxOn, bgmVol, sfxVol, muteAll)
        pulseAutoSave {
            repo.updateUserSettings {
                it.copy(bgmEnabled = bgmOn, sfxEnabled = sfxOn, bgmVolume = bgmVol, sfxVolume = sfxVol, muteAll = muteAll)
            }
        }
    }

    fun updateReducedMotion(enabled: Boolean) {
        pulseAutoSave {
            repo.updateUserSettings { it.copy(reducedMotion = enabled) }
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        pulseAutoSave {
            repo.updateUserSettings { it.copy(notificationsEnabled = enabled) }
        }
    }

    fun togglePremiumUi(enabled: Boolean) {
        pulseAutoSave {
            repo.updateUserSettings { if (it.premiumUiUnlocked) it.copy(premiumUiEnabled = enabled) else it }
        }
    }

    fun equipBackground(backgroundId: String) {
        pulseAutoSave {
            repo.updateUserSettings { it.copy(activeBackgroundId = backgroundId) }
        }
    }

    fun updateCosmetics(customTitle: String, titleColorIndex: Int, equippedFrameId: String, avatarIndex: Int) {
        val uid = currentUser.value?.id ?: return
        pulseAutoSave {
            repo.updateCosmetics(uid, customTitle, titleColorIndex, equippedFrameId, avatarIndex)
            SoundEngine.playSfx(SfxType.ACHIEVEMENT)
            showToast(tr(activeLang(), "บันทึกฉายาและกรอบอวตารเรียบร้อย! ✨", "Title & Avatar Frame saved! ✨", "专属称号与头像框已保存！✨"))
        }
    }

    fun updateProfileDisplay(displayName: String, avatarIndex: Int) {
        val clean = displayName.trim()
        if (clean.isEmpty()) return
        pulseAutoSave {
            repo.updateUserSettings { it.copy(displayName = clean, avatarIndex = avatarIndex) }
            showToast(tr(activeLang(), "บันทึกโปรไฟล์เรียบร้อยแล้ว!", "Profile updated!", "个人资料已更新！"))
        }
    }

    // --- 5-Field Authentication & Full Account Mutability ---
    fun signUpFiveFields(
        gender: PlayerGender,
        username: String,
        displayName: String,
        passwordOrPin: String,
        recoveryCode: String,
        avatarIndex: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val lang = activeLang()
            val res = repo.signUp(
                gender = gender,
                username = username,
                displayName = displayName,
                passwordOrPin = passwordOrPin,
                customRecoveryCode = recoveryCode,
                avatarIndex = avatarIndex,
                langCode = lang.code
            )
            res.onSuccess { outcome ->
                SoundEngine.playSfx(SfxType.LEVEL_UP)
                _welcomeCelebrationCode.value = outcome.recoveryCodePlain
                onSuccess()
            }.onFailure { err ->
                SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                showToast(err.message ?: tr(lang, "สมัครสมาชิกไม่สำเร็จ", "Sign up failed.", "注册失败。"))
            }
        }
    }

    fun updateFullFiveFieldAccount(
        gender: PlayerGender,
        newUsername: String,
        newDisplayName: String,
        newPasswordOrBlank: String,
        newRecoveryCode: String,
        avatarIndex: Int
    ) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        pulseAutoSave {
            repo.updateAllAccountFields(
                userId = uid,
                gender = gender,
                newUsername = newUsername,
                newDisplayName = newDisplayName,
                newPasswordOrBlank = newPasswordOrBlank,
                newRecoveryCode = newRecoveryCode,
                avatarIndex = avatarIndex,
                lang = lang
            ).onSuccess {
                SoundEngine.playSfx(SfxType.ACHIEVEMENT)
                showToast(
                    tr(
                        lang,
                        "อัปเดตข้อมูลบัญชีทั้ง 5 ช่องสำเร็จแล้ว! 🎉",
                        "All 5 account fields updated successfully! 🎉",
                        "全部5项账号资料已成功更新！🎉"
                    )
                )
            }.onFailure { err ->
                SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                showToast(err.message ?: tr(lang, "ไม่สามารถอัปเดตบัญชีได้", "Failed to update account.", "更新账号失败。"))
            }
        }
    }

    fun logIn(username: String, passwordOrPin: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val lang = activeLang()
            val res = repo.logIn(username, passwordOrPin, lang)
            res.onSuccess {
                SoundEngine.playSfx(SfxType.CORRECT_ANSWER)
                onSuccess()
            }.onFailure { err ->
                SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                showToast(err.message ?: tr(lang, "เข้าสู่ระบบไม่สำเร็จ", "Login failed.", "登录失败。"))
            }
        }
    }

    fun startGuestMode(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val lang = activeLang()
            repo.startGuestTrial(lang.code).onSuccess {
                SoundEngine.playSfx(SfxType.BUTTON_CLICK)
                onSuccess()
            }.onFailure {
                showToast(it.message ?: tr(lang, "ไม่สามารถเริ่มโหมดทดลองได้", "Cannot start guest mode.", "无法启动游客模式。"))
            }
        }
    }

    fun logOut() {
        examTimerJob?.cancel()
        _activeExam.value = null
        _isAdminUnlocked.value = false
        viewModelScope.launch {
            repo.setSessionUserId(null)
        }
    }

    fun recoverAccount(username: String, recoveryCode: String, newPin: String, onRecovered: () -> Unit) {
        viewModelScope.launch {
            val lang = activeLang()
            repo.recoverAccountWithCode(username, recoveryCode, newPin, lang)
                .onSuccess {
                    SoundEngine.playSfx(SfxType.ACHIEVEMENT)
                    showToast(
                        tr(
                            lang,
                            "กู้คืนบัญชีและตั้งรหัสใหม่สำเร็จ! กรุณากดเข้าสู่ระบบ",
                            "Account recovered & new password set! Please log in.",
                            "账号已恢复并设置新密码！请点击登录。"
                        )
                    )
                    onRecovered()
                }
                .onFailure {
                    SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                    showToast(it.message ?: tr(lang, "กู้คืนบัญชีไม่สำเร็จ", "Account recovery failed.", "账号恢复失败。"))
                }
        }
    }

    fun changePinOrPassword(oldPin: String, newPin: String) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        viewModelScope.launch {
            repo.changePasswordOrPin(uid, oldPin, newPin, lang)
                .onSuccess {
                    SoundEngine.playSfx(SfxType.CORRECT_ANSWER)
                    showToast(tr(lang, "เปลี่ยนรหัสบัญชีสำเร็จแล้ว!", "Password updated!", "账号密码已更新！"))
                }
                .onFailure {
                    SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                    showToast(it.message ?: tr(lang, "เปลี่ยนรหัสไม่สำเร็จ", "Password change failed.", "密码修改失败。"))
                }
        }
    }

    fun completeOneSuspensionExercise(isCorrect: Boolean) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        if (!isCorrect) {
            SoundEngine.playSfx(SfxType.WRONG_ANSWER)
            showToast(tr(lang, "คำตอบยังไม่ถูก ลองคิดทบทวนอีกครั้งนะ!", "Not quite right — try again!", "答案不对，再思考一下吧！"))
            return
        }
        SoundEngine.playSfx(SfxType.CORRECT_ANSWER)
        pulseAutoSave {
            val updated = repo.recordSuspensionExerciseProgress(uid, 1)
            if (updated?.accountStatus == "ACTIVE") {
                SoundEngine.playSfx(SfxType.LEVEL_UP)
                showToast(tr(lang, "กู้คืนบัญชีสำเร็จครบ 5 ข้อ! ยินดีต้อนรับกลับ!", "Account restored! Welcome back!", "完成5题恢复成功！欢迎回来！"))
            }
        }
    }

    // --- Exam, Mistake Retry & AI Exam Generation ---
    private fun initialUsesForPet(equippedSpecies: PetSpecies?): Int = when (equippedSpecies?.skillType) {
        PetSkillType.REMOVE_1_WRONG_ONCE,
        PetSkillType.HINT_1_ONCE,
        PetSkillType.FREEZE_TIME_5S,
        PetSkillType.CHANGE_ANSWER_ONCE,
        PetSkillType.REMOVE_2_WRONG_ONCE,
        PetSkillType.RETRY_WRONG_NO_REWARD,
        PetSkillType.HINT_RETRY_WRONG_ONCE,
        PetSkillType.XP_15_AND_SKIP_RETURN -> 1
        PetSkillType.HINT_2_TWICE,
        PetSkillType.TIME_10_REMOVE_1_TWICE -> 2
        PetSkillType.MARK_REVIEW_3 -> 3
        else -> 0
    }

    fun startExamSession(
        mode: String,
        subject: SubjectId,
        difficulty: DifficultyLevel,
        questionCount: Int,
        onReady: () -> Unit
    ) {
        val user = currentUser.value ?: return
        val lang = activeLang()
        viewModelScope.launch {
            val res = repo.createExamSession(user.id, mode, subject, difficulty, questionCount)
            res.onSuccess { (session, questions) ->
                _activeExam.value = ActiveExamUiState(
                    session = session,
                    questions = questions,
                    remainingSeconds = session.remainingSeconds,
                    petSkillUsesLeft = initialUsesForPet(getEquippedPetSpecies())
                )
                startExamTimer()
                onReady()
            }.onFailure {
                showToast(it.message ?: tr(lang, "เริ่มข้อสอบไม่สำเร็จ", "Failed to start exam.", "无法开始考试。"))
            }
        }
    }

    fun startMistakeRetryExam(onReady: () -> Unit) {
        val user = currentUser.value ?: return
        val lang = activeLang()
        val ids = mistakeNotebookItems.value.map { it.question.id }
        if (ids.isEmpty()) {
            showToast(tr(lang, "ยอดเยี่ยม! ไม่มีข้อที่ค้างในสมุดข้อผิดเลย 🎉", "Awesome! Your Mistake Notebook is completely clear! 🎉", "太棒了！错题本已全部清空！🎉"))
            return
        }
        viewModelScope.launch {
            repo.createMistakeRetryExamSession(user.id, ids).onSuccess { (session, questions) ->
                _activeExam.value = ActiveExamUiState(
                    session = session,
                    questions = questions,
                    remainingSeconds = session.remainingSeconds,
                    petSkillUsesLeft = initialUsesForPet(getEquippedPetSpecies())
                )
                startExamTimer()
                onReady()
            }.onFailure {
                showToast(it.message ?: tr(lang, "เริ่มสอบซ่อมไม่สำเร็จ", "Cannot start mistake retry.", "无法启动错题重练。"))
            }
        }
    }

    fun generateNewAiExamAndStart(
        subject: SubjectId,
        difficulty: DifficultyLevel,
        count: Int,
        mode: String,
        onReady: () -> Unit
    ) {
        if (_isAiGeneratingExam.value) return
        val lang = activeLang()
        viewModelScope.launch {
            _isAiGeneratingExam.value = true
            try {
                val existing = allQuestions.value
                val nextNum = existing.size + 1
                val result = GeminiAiService.generateVerifiedQuestions(
                    subject = subject,
                    difficulty = difficulty,
                    count = count.coerceIn(3, 10),
                    existingQuestions = existing,
                    nextQuestionNumberStart = nextNum
                )
                result.onSuccess { generatedList ->
                    generatedList.forEach { q -> repo.adminSaveQuestion(q, isNew = true) }
                    SoundEngine.playSfx(SfxType.ACHIEVEMENT)
                    showToast(
                        tr(
                            lang,
                            "AI สร้างและตรวจสอบข้อสอบใหม่สำเร็จ +${generatedList.size} ข้อ!",
                            "AI generated & verified +${generatedList.size} new questions!",
                            "AI已生成并验证 +${generatedList.size} 道新考题！"
                        )
                    )
                    startExamSession(mode, subject, difficulty, count, onReady)
                }.onFailure { err ->
                    SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                    showToast(err.message ?: tr(lang, "ไม่สามารถสร้างข้อสอบด้วย AI ได้ในขณะนี้", "AI question generation unavailable.", "当前无法通过AI生成题目。"))
                }
            } finally {
                _isAiGeneratingExam.value = false
            }
        }
    }

    fun getEquippedPetSpecies(): PetSpecies? {
        val user = currentUser.value ?: return null
        val instId = user.equippedPetInstanceId ?: return null
        val inst = userPets.value.firstOrNull { it.instanceId == instId } ?: return null
        return GameCatalog.getPetById(inst.speciesId)
    }

    private fun startExamTimer() {
        examTimerJob?.cancel()
        examTimerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val state = _activeExam.value ?: break
                if (state.freezeRemainingSeconds > 0) {
                    _activeExam.value = state.copy(freezeRemainingSeconds = state.freezeRemainingSeconds - 1)
                } else if (state.remainingSeconds > 1) {
                    _activeExam.value = state.copy(remainingSeconds = state.remainingSeconds - 1)
                } else {
                    _activeExam.value = state.copy(remainingSeconds = 0)
                    submitActiveExam(onSubmitted = {})
                    break
                }
            }
        }
    }

    fun selectAnswerInActiveExam(questionId: Long, answer: String, lockImmediatelyInPractice: Boolean) {
        val state = _activeExam.value ?: return
        val q = state.questions.firstOrNull { it.id == questionId } ?: return
        if (questionId in state.lockedQuestionIds) return

        val instantMode = state.session.mode == "PRACTICE" || state.session.mode == "MISTAKE_RETRY"
        val nextAnswers = state.answers + (questionId to answer)
        val nextLocked = if (lockImmediatelyInPractice && instantMode) {
            state.lockedQuestionIds + questionId
        } else {
            state.lockedQuestionIds
        }

        val lang = activeLang()
        val expText = tr(lang, q.explanationTh, q.explanationEn, q.explanationZh)
        val feedback: Pair<Boolean, String>? = if (lockImmediatelyInPractice && instantMode) {
            val ok = repo.checkAnswerCorrect(q, answer)
            if (ok) SoundEngine.playSfx(SfxType.CORRECT_ANSWER) else SoundEngine.playSfx(SfxType.WRONG_ANSWER)
            ok to expText
        } else {
            SoundEngine.playSfx(SfxType.BUTTON_CLICK)
            state.lastInstantFeedback
        }

        val updatedState = state.copy(
            answers = nextAnswers,
            lockedQuestionIds = nextLocked,
            lastInstantFeedback = feedback
        )
        _activeExam.value = updatedState

        pulseAutoSave {
            repo.autoSaveExamProgress(
                sessionId = updatedState.session.id,
                currentIndex = updatedState.currentIndex,
                answersMap = updatedState.answers,
                markedSet = updatedState.markedForReview,
                remainingSeconds = updatedState.remainingSeconds,
                petSkillUsesCount = updatedState.petSkillUsesLeft
            )
        }
    }

    fun navigateExamQuestion(newIndex: Int) {
        val state = _activeExam.value ?: return
        val clamped = newIndex.coerceIn(0, (state.questions.size - 1).coerceAtLeast(0))
        _activeExam.value = state.copy(currentIndex = clamped, lastInstantFeedback = null)
        pulseAutoSave {
            repo.autoSaveExamProgress(
                sessionId = state.session.id,
                currentIndex = clamped,
                answersMap = state.answers,
                markedSet = state.markedForReview,
                remainingSeconds = state.remainingSeconds,
                petSkillUsesCount = state.petSkillUsesLeft
            )
        }
    }

    fun toggleMarkQuestionForReview(questionId: Long) {
        val state = _activeExam.value ?: return
        val nextSet = if (questionId in state.markedForReview) {
            state.markedForReview - questionId
        } else {
            state.markedForReview + questionId
        }
        _activeExam.value = state.copy(markedForReview = nextSet)
        pulseAutoSave {
            repo.autoSaveExamProgress(
                sessionId = state.session.id,
                currentIndex = state.currentIndex,
                answersMap = state.answers,
                markedSet = nextSet,
                remainingSeconds = state.remainingSeconds,
                petSkillUsesCount = state.petSkillUsesLeft
            )
        }
    }

    fun useShopRemoveTwoWrongOnCurrentQuestion() {
        val state = _activeExam.value ?: return
        val user = currentUser.value ?: return
        val lang = activeLang()
        val q = state.questions.getOrNull(state.currentIndex) ?: return
        if (q.correctAnswer.uppercase() !in setOf("A", "B", "C", "D")) {
            showToast(tr(lang, "ไอเทมนี้ใช้ได้เฉพาะปรนัย 4 ตัวเลือก", "Only usable on 4-choice questions.", "该道具仅限四选一题目使用。"))
            return
        }
        if ((state.hiddenOptionsByQuestion[q.id]?.size ?: 0) >= 2) {
            showToast(tr(lang, "ตัดตัวเลือกในข้อนี้ไปแล้ว", "Already eliminated options on this question.", "本题已使用过排错道具。"))
            return
        }
        viewModelScope.launch {
            val consumed = repo.consumeRemoveWrongItem(user.id)
            if (!consumed) {
                showToast(tr(lang, "สิทธิ์ตัดตัวเลือกหมดแล้ว ซื้อเพิ่มได้ที่ร้านแดง-ขาว!", "No uses left — visit PT Shop!", "排错次数已用完，可前往商店购买！"))
                return@launch
            }
            val wrongs = listOf("A", "B", "C", "D").filter { it != q.correctAnswer.uppercase() }.shuffled().take(2).toSet()
            val currentState = _activeExam.value ?: return@launch
            _activeExam.value = currentState.copy(
                hiddenOptionsByQuestion = currentState.hiddenOptionsByQuestion + (q.id to wrongs)
            )
            SoundEngine.playSfx(SfxType.PURCHASE)
            showToast(tr(lang, "ตัดตัวเลือกผิดออก 2 ข้อเรียบร้อย!", "Eliminated 2 wrong choices!", "成功排除2个错误选项！"))
        }
    }

    fun useShopTimePotionInExam() {
        val user = currentUser.value ?: return
        val lang = activeLang()
        viewModelScope.launch {
            val consumed = repo.consumeTimePotionItem(user.id)
            if (!consumed) {
                showToast(tr(lang, "ขวดยาเพิ่มเวลาหมดแล้ว ซื้อได้ที่ร้านแดง-ขาว!", "No Time Potions left — buy more in PT Shop!", "延时药水已用完，可在商店购买！"))
                return@launch
            }
            val cur = _activeExam.value ?: return@launch
            _activeExam.value = cur.copy(remainingSeconds = cur.remainingSeconds + 30)
            SoundEngine.playSfx(SfxType.STREAK_MILESTONE)
            showToast(tr(lang, "⏱️ เพิ่มเวลาสอบ +30 วินาทีสำเร็จ!", "⏱️ Added +30 seconds!", "⏱️ 成功延长30秒考试时间！"))
        }
    }

    fun useEquippedPetSkillInExam(lang: AppLanguage) {
        val state = _activeExam.value ?: return
        val pet = getEquippedPetSpecies() ?: return
        if (state.petSkillUsesLeft <= 0) {
            showToast(tr(lang, "ใช้สกิลคู่หู ${pet.name(lang)} ครบโควตาของชุดสอบนี้แล้ว", "${pet.name(lang)} skill quota used for this exam.", "${pet.name(lang)} 本场技能次数已用完。"))
            return
        }
        val q = state.questions.getOrNull(state.currentIndex) ?: return

        when (pet.skillType) {
            PetSkillType.REMOVE_1_WRONG_ONCE, PetSkillType.TIME_10_REMOVE_1_TWICE -> {
                if (q.correctAnswer.uppercase() !in setOf("A", "B", "C", "D")) return
                val existingHidden = state.hiddenOptionsByQuestion[q.id].orEmpty()
                val wrongPick = listOf("A", "B", "C", "D")
                    .filter { it != q.correctAnswer.uppercase() && it !in existingHidden }
                    .randomOrNull() ?: return
                _activeExam.value = state.copy(
                    hiddenOptionsByQuestion = state.hiddenOptionsByQuestion + (q.id to (existingHidden + wrongPick)),
                    petSkillUsesLeft = state.petSkillUsesLeft - 1
                )
                SoundEngine.playSfx(SfxType.COMBO)
                showToast(tr(lang, "${pet.nameTh} ช่วยตัดตัวเลือกผิดออก 1 ข้อ!", "${pet.nameEn} removed 1 wrong choice!", "${pet.nameZh} 排除了1个错误选项！"))
            }
            PetSkillType.REMOVE_2_WRONG_ONCE -> {
                if (q.correctAnswer.uppercase() !in setOf("A", "B", "C", "D")) return
                val wrongs = listOf("A", "B", "C", "D").filter { it != q.correctAnswer.uppercase() }.shuffled().take(2).toSet()
                _activeExam.value = state.copy(
                    hiddenOptionsByQuestion = state.hiddenOptionsByQuestion + (q.id to wrongs),
                    petSkillUsesLeft = state.petSkillUsesLeft - 1
                )
                SoundEngine.playSfx(SfxType.COMBO)
                showToast(tr(lang, "${pet.nameTh} ปัดตัวเลือกผิดกระเด็น 2 ข้อ!", "${pet.nameEn} swept away 2 wrong choices!", "${pet.nameZh} 扫除了2个错误选项！"))
            }
            PetSkillType.HINT_1_ONCE -> {
                val hintText = q.hint1.ifBlank { q.topic }
                _activeExam.value = state.copy(
                    unlockedHintsByQuestion = state.unlockedHintsByQuestion + (q.id to "💡 Hint: $hintText"),
                    petSkillUsesLeft = state.petSkillUsesLeft - 1
                )
                SoundEngine.playSfx(SfxType.PET_REVEAL)
            }
            PetSkillType.HINT_2_TWICE -> {
                val h1 = q.hint1.ifBlank { q.topic }
                val h2 = q.hint2.ifBlank { tr(lang, "ตัดตัวเลือกที่ขัดแย้งกับเงื่อนไข", "Eliminate options that contradict conditions.", "排除与条件矛盾的选项。") }
                _activeExam.value = state.copy(
                    unlockedHintsByQuestion = state.unlockedHintsByQuestion + (q.id to "✨ Hint 1: $h1\n🌟 Hint 2: $h2"),
                    petSkillUsesLeft = state.petSkillUsesLeft - 1
                )
                SoundEngine.playSfx(SfxType.PET_REVEAL)
            }
            PetSkillType.FREEZE_TIME_5S -> {
                _activeExam.value = state.copy(
                    freezeRemainingSeconds = state.freezeRemainingSeconds + 5,
                    petSkillUsesLeft = state.petSkillUsesLeft - 1
                )
                SoundEngine.playSfx(SfxType.STREAK_MILESTONE)
                showToast(tr(lang, "หยุดเวลาข้อสอบให้ 5 วินาที!", "Exam timer frozen for 5 seconds!", "考试计时已暂停5秒！"))
            }
            PetSkillType.CHANGE_ANSWER_ONCE, PetSkillType.RETRY_WRONG_NO_REWARD, PetSkillType.HINT_RETRY_WRONG_ONCE -> {
                val hintAdd = if (pet.skillType == PetSkillType.HINT_RETRY_WRONG_ONCE) {
                    val h = q.hint1.ifBlank { q.topic }
                    state.unlockedHintsByQuestion + (q.id to "🦄 Hint: $h")
                } else state.unlockedHintsByQuestion
                _activeExam.value = state.copy(
                    lockedQuestionIds = state.lockedQuestionIds - q.id,
                    retriedQuestionIds = state.retriedQuestionIds + q.id,
                    unlockedHintsByQuestion = hintAdd,
                    lastInstantFeedback = null,
                    petSkillUsesLeft = state.petSkillUsesLeft - 1
                )
                SoundEngine.playSfx(SfxType.PET_REVEAL)
                showToast(tr(lang, "ปลดล็อกให้เลือกคำตอบข้อนี้ใหม่อีกครั้ง!", "Unlocked answer retry for this question!", "已解锁本题重新作答机会！"))
            }
            PetSkillType.MARK_REVIEW_3 -> {
                val nextMarked = state.markedForReview + q.id
                _activeExam.value = state.copy(
                    markedForReview = nextMarked,
                    petSkillUsesLeft = state.petSkillUsesLeft - 1
                )
                showToast(tr(lang, "ปักหมุดข้อนี้ไว้สรุปหลังสอบแล้ว!", "Pinned question for post-exam summary!", "已标记本题用于考后复盘！"))
            }
            PetSkillType.XP_15_AND_SKIP_RETURN -> {
                val nextIdx = (state.currentIndex + 1) % state.questions.size
                _activeExam.value = state.copy(
                    currentIndex = nextIdx,
                    markedForReview = state.markedForReview + q.id,
                    petSkillUsesLeft = state.petSkillUsesLeft - 1
                )
                showToast(tr(lang, "ข้ามข้อนี้ชั่วคราวและปักหมุดให้กลับมาทำ!", "Skipped & pinned question for later!", "已暂时跳过并标记本题！"))
            }
            else -> {
                showToast(tr(lang, "สกิลติดตัวของ ${pet.nameTh} ทำงานให้อัตโนมัติ!", "${pet.nameEn}'s passive skill activates automatically!", "${pet.nameZh} 的被动技能将自动生效！"))
            }
        }
    }

    fun requestAiSmartHintForCurrentQuestion(lang: AppLanguage) {
        val state = _activeExam.value ?: return
        val q = state.questions.getOrNull(state.currentIndex) ?: return
        viewModelScope.launch {
            val res = GeminiAiService.generateQuickSmartHint(tr(lang, q.questionTh, q.questionEn, q.questionZh), lang)
            res.onSuccess { hint ->
                val cur = _activeExam.value ?: return@onSuccess
                _activeExam.value = cur.copy(
                    unlockedHintsByQuestion = cur.unlockedHintsByQuestion + (q.id to "🤖 AI Hint: $hint")
                )
                SoundEngine.playSfx(SfxType.BUTTON_CLICK)
            }.onFailure {
                val fallback = q.hint1.ifBlank { q.topic }
                val cur = _activeExam.value ?: return@onFailure
                _activeExam.value = cur.copy(
                    unlockedHintsByQuestion = cur.unlockedHintsByQuestion + (q.id to "💡 Hint: $fallback")
                )
            }
        }
    }

    fun submitActiveExam(onSubmitted: () -> Unit) {
        val state = _activeExam.value ?: return
        examTimerJob?.cancel()
        val spent = (state.session.totalAllocatedSeconds - state.remainingSeconds).coerceAtLeast(1)
        viewModelScope.launch {
            _saveState.value = SaveIndicatorState.SAVING
            val res = repo.finalizeExamSession(
                sessionId = state.session.id,
                questions = state.questions,
                answersMap = state.answers,
                timeSpentSeconds = spent
            )
            _saveState.value = SaveIndicatorState.SAVED
            res.onSuccess { summary ->
                val paired = state.questions.map { q -> q to state.answers[q.id].orEmpty() }
                _lastExamOutcome.value = summary to paired
                _activeExam.value = null
                if (summary.newLevel > summary.oldLevel) {
                    SoundEngine.playSfx(SfxType.LEVEL_UP)
                    _levelUpCelebration.value = summary.newLevel
                } else if (summary.streakAfter > summary.streakBefore) {
                    SoundEngine.playSfx(SfxType.STREAK_MILESTONE)
                } else {
                    SoundEngine.playSfx(SfxType.ACHIEVEMENT)
                }
                onSubmitted()
            }.onFailure { err ->
                showToast(err.message ?: tr(activeLang(), "ส่งข้อสอบไม่สำเร็จ", "Failed to submit exam.", "交卷失败。"))
            }
        }
    }

    // --- Quests & Daily Reroll ---
    fun claimQuest(quest: QuestDefinitionEntity, periodKey: String) {
        val uid = currentUser.value?.id ?: return
        val oldLvl = currentUser.value?.level ?: 1
        val lang = activeLang()
        pulseAutoSave {
            repo.claimQuestReward(uid, quest, periodKey).onSuccess { xpWon ->
                SoundEngine.playSfx(SfxType.ACHIEVEMENT)
                showToast(
                    tr(
                        lang,
                        "รับรางวัลภารกิจสำเร็จ! +$xpWon XP & +${quest.rewardPt} PT!",
                        "Quest claimed! +$xpWon XP & +${quest.rewardPt} PT!",
                        "任务奖励已领取！+$xpWon XP 与 +${quest.rewardPt} PT！"
                    )
                )
                val newLvl = currentUser.value?.level ?: oldLvl
                if (newLvl > oldLvl) _levelUpCelebration.value = newLvl
            }.onFailure {
                showToast(it.message ?: tr(lang, "ไม่สามารถรับรางวัลได้", "Cannot claim reward.", "无法领取奖励。"))
            }
        }
    }

    fun rerollDailyQuests() {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        pulseAutoSave {
            repo.rerollDailyQuestsForToday(uid, lang).onSuccess {
                SoundEngine.playSfx(SfxType.COMBO)
                showToast(tr(lang, "🔄 สุ่มเปลี่ยนชุดภารกิจรายวันใหม่เรียบร้อย!", "🔄 Daily quests refreshed!", "🔄 今日日常任务已刷新！"))
            }.onFailure {
                SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                showToast(it.message ?: "")
            }
        }
    }

    // --- Lucky Wheel, Mystery Chests & Boss Raid ---
    fun spinDailyWheel(onResult: (LuckyWheelSlice) -> Unit) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        val nonce = UUID.randomUUID().toString()
        pulseAutoSave {
            repo.spinLuckyWheel(uid, nonce, lang).onSuccess { slice ->
                SoundEngine.playSfx(SfxType.PET_REVEAL)
                onResult(slice)
                showToast(
                    tr(
                        lang,
                        "🎡 ยินดีด้วย! หมุนได้ ${slice.label(lang)}",
                        "🎡 Lucky Spin won: ${slice.label(lang)}!",
                        "🎡 恭喜抽中：${slice.label(lang)}！"
                    )
                )
            }.onFailure {
                SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                showToast(it.message ?: "")
            }
        }
    }

    fun openMysteryChest(chest: MysteryChestSpec) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        val nonce = UUID.randomUUID().toString()
        pulseAutoSave {
            repo.openMysteryChest(uid, chest, nonce, lang).onSuccess { outcome ->
                SoundEngine.playSfx(SfxType.LEVEL_UP)
                _lastChestOutcome.value = outcome
            }.onFailure {
                SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                showToast(it.message ?: "")
            }
        }
    }

    fun recordBossRaidVictory(boss: BossSpec, remainingHearts: Int, maxCombo: Int) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        val oldLvl = currentUser.value?.level ?: 1
        val nonce = UUID.randomUUID().toString()
        pulseAutoSave {
            repo.completeBossRaidVictory(uid, boss, remainingHearts, maxCombo, nonce, lang)
                .onSuccess { (xp, pt, frameId) ->
                    SoundEngine.playSfx(SfxType.LEVEL_UP)
                    val frameBonus = if (frameId != null) {
                        val fName = GameCatalog.getFrameById(frameId).name(lang)
                        " + $fName!"
                    } else ""
                    showToast(
                        tr(
                            lang,
                            "⚔️ ปราบ ${boss.nameTh} สำเร็จ! +$xp XP, +$pt PT$frameBonus",
                            "⚔️ Defeated ${boss.nameEn}! +$xp XP, +$pt PT$frameBonus",
                            "⚔️ 成功击败 ${boss.nameZh}！+$xp XP, +$pt PT$frameBonus"
                        )
                    )
                    val newLvl = currentUser.value?.level ?: oldLvl
                    if (newLvl > oldLvl) _levelUpCelebration.value = newLvl
                }
        }
    }

    // --- Smart Study Hub: Flashcards & Study Planner ---
    fun markFlashcardReviewed(card: FlashcardEntity) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        pulseAutoSave {
            repo.markFlashcardReviewed(uid, card).onSuccess { (xp, pt) ->
                SoundEngine.playSfx(SfxType.CORRECT_ANSWER)
                showToast(tr(lang, "จำแม่นแล้ว! +$xp XP & +$pt PT ✨", "Mastered! +$xp XP & +$pt PT ✨", "已掌握！+$xp XP 与 +$pt PT ✨"))
            }
        }
    }

    fun addCustomFlashcard(subject: SubjectId, front: String, back: String, example: String) {
        val uid = currentUser.value?.id ?: return
        if (front.isBlank() || back.isBlank()) return
        val lang = activeLang()
        pulseAutoSave {
            repo.createCustomFlashcard(uid, subject, front, back, example)
            SoundEngine.playSfx(SfxType.ACHIEVEMENT)
            showToast(tr(lang, "เพิ่มแฟลชการ์ดใบใหม่สำเร็จ!", "Custom flashcard added!", "已添加自定义闪卡！"))
        }
    }

    fun addStudyPlanGoal(subject: SubjectId, title: String, minutes: Int, targetDateBangkok: String) {
        val uid = currentUser.value?.id ?: return
        if (title.isBlank()) return
        val lang = activeLang()
        pulseAutoSave {
            repo.addStudyPlanTask(uid, subject, title, minutes, targetDateBangkok)
            SoundEngine.playSfx(SfxType.BUTTON_CLICK)
            showToast(tr(lang, "เพิ่มเป้าหมายอ่านหนังสือแล้ว!", "Study goal added!", "学习目标已添加！"))
        }
    }

    fun toggleStudyPlanGoal(taskId: Long) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        pulseAutoSave {
            repo.toggleCompleteStudyPlanTask(uid, taskId).onSuccess { (xp, pt) ->
                if (xp > 0) {
                    SoundEngine.playSfx(SfxType.ACHIEVEMENT)
                    showToast(tr(lang, "ทำตามแผนสำเร็จ! +$xp XP & +$pt PT 🎯", "Goal completed! +$xp XP & +$pt PT 🎯", "完成计划目标！+$xp XP 与 +$pt PT 🎯"))
                }
            }
        }
    }

    fun deleteStudyPlanGoal(taskId: Long) {
        val uid = currentUser.value?.id ?: return
        pulseAutoSave {
            repo.deleteStudyPlanTask(uid, taskId)
        }
    }

    fun updateDreamSchoolCountdown(targetDateBangkok: String, dreamSchool: String) {
        pulseAutoSave {
            repo.updateUserSettings {
                it.copy(
                    targetExamDateBangkok = targetDateBangkok.trim().ifBlank { "2027-03-15" },
                    targetDreamSchool = dreamSchool.trim().ifBlank { "โรงเรียนในฝัน ม.1" }
                )
            }
            showToast(tr(activeLang(), "บันทึกวันสอบและโรงเรียนในฝันแล้ว!", "Exam countdown updated!", "目标名校与考试倒计时已更新！"))
        }
    }

    // --- Shop & Egg Hatching ---
    fun buyShopItem(item: ShopItemCatalog, onBought: () -> Unit = {}) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        val nonce = UUID.randomUUID().toString()
        pulseAutoSave {
            repo.purchaseShopItem(uid, item, nonce, lang).onSuccess {
                SoundEngine.playSfx(SfxType.PURCHASE)
                showToast(
                    tr(
                        lang,
                        "ซื้อ '${item.name(lang)}' สำเร็จแล้ว!",
                        "Purchased '${item.name(lang)}'!",
                        "已成功购买「${item.name(lang)}」！"
                    )
                )
                onBought()
            }.onFailure {
                SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                showToast(it.message ?: tr(lang, "ซื้อไอเทมไม่สำเร็จ", "Purchase failed.", "购买失败。"))
            }
        }
    }

    fun buyAndOpenEgg() {
        val uid = currentUser.value?.id ?: return
        if (_eggOpeningState.value != null) return
        val lang = activeLang()
        val nonce = UUID.randomUUID().toString()
        pulseAutoSave {
            repo.purchaseAndHatchEgg(uid, nonce, lang).onSuccess { (rarity, pet, instId) ->
                SoundEngine.playSfx(SfxType.EGG_SHAKE)
                _eggOpeningState.value = EggOpeningState(rarity, pet, instId)
            }.onFailure {
                SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                showToast(it.message ?: tr(lang, "เปิดไข่ไม่สำเร็จ", "Egg hatch failed.", "孵化失败。"))
            }
        }
    }

    fun dismissEggOpeningOverlay() {
        _eggOpeningState.value = null
    }

    fun equipPetInstance(instanceId: Long?) {
        val uid = currentUser.value?.id ?: return
        pulseAutoSave {
            repo.equipPet(uid, instanceId)
            SoundEngine.playSfx(SfxType.PET_REVEAL)
        }
    }

    fun discardPetInstance(instanceId: Long) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        pulseAutoSave {
            repo.discardPet(uid, instanceId)
            showToast(tr(lang, "นำสัตว์เลี้ยงออกจากคลังเรียบร้อยแล้ว", "Removed pet from inventory.", "已将宠物移出背包。"))
        }
    }

    // --- Mini-Games ---
    fun completeMiniGame(game: MiniGameId, score: Int) {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        val nonce = UUID.randomUUID().toString()
        pulseAutoSave {
            repo.recordMiniGameScore(uid, game, score, nonce).onSuccess { (xp, pt) ->
                SoundEngine.playSfx(SfxType.ACHIEVEMENT)
                showToast(
                    tr(
                        lang,
                        "${game.title(lang)} จบเกม! ได้รับ +$xp XP และ +$pt PT!",
                        "${game.title(lang)} complete! Earned +$xp XP & +$pt PT!",
                        "${game.title(lang)} 结算！获得 +$xp XP 与 +$pt PT！"
                    )
                )
            }
        }
    }

    // --- AI Tutor & Study Sheet Library ---
    fun sendTutorPrompt(prompt: String, useHighThinking: Boolean, lang: AppLanguage) {
        val user = currentUser.value ?: return
        val clean = prompt.trim()
        if (clean.isEmpty() || _isAiTutorBusy.value) return
        viewModelScope.launch {
            _isAiTutorBusy.value = true
            _aiTutorStreamingText.value = ""
            val priorHistory = tutorMessages.value
            repo.addTutorMessage(user.id, "user", clean, if (useHighThinking) "gemini-3.5-pro" else "gemini-3.5-flash")
            var latestReply = ""
            var usedModel = if (useHighThinking) "gemini-3.5-pro (High Thinking)" else "gemini-3.5-flash"
            try {
                GeminiAiService.streamTutorReply(priorHistory, clean, useHighThinking, lang)
                    .collect { (partial, modelLabel) ->
                        latestReply = partial
                        usedModel = modelLabel
                        _aiTutorStreamingText.value = partial
                    }
                if (latestReply.isNotBlank()) {
                    repo.addTutorMessage(user.id, "model", latestReply, usedModel)
                } else {
                    showToast(tr(lang, "AI Tutor ไม่ได้ส่งข้อความตอบกลับ กรุณาลองอีกครั้ง", "AI Tutor returned an empty response.", "AI导师未返回内容，请重试。"))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showToast("AI Tutor: ${e.localizedMessage ?: "Error"}")
            } finally {
                _aiTutorStreamingText.value = null
                _isAiTutorBusy.value = false
            }
        }
    }

    fun clearAiTutorChat() {
        val uid = currentUser.value?.id ?: return
        val lang = activeLang()
        pulseAutoSave {
            repo.clearTutorHistory(uid)
            showToast(tr(lang, "ล้างประวัติแชท AI Tutor เรียบร้อยแล้ว", "AI Tutor chat cleared.", "AI导师聊天记录已清空。"))
        }
    }

    fun createAiStudySheet(
        subject: SubjectId,
        sourceType: String,
        sourceText: String,
        bitmap: Bitmap?,
        quizSize: Int,
        lang: AppLanguage,
        onCreated: (StudySheetEntity) -> Unit
    ) {
        val uid = currentUser.value?.id ?: return
        if (_isGeneratingStudySheet.value) return
        viewModelScope.launch {
            _isGeneratingStudySheet.value = true
            try {
                val res = GeminiAiService.generateStudySheet(uid, subject, sourceType, sourceText, bitmap, quizSize, lang)
                res.onSuccess { sheet ->
                    val newId = repo.saveStudySheet(sheet)
                    SoundEngine.playSfx(SfxType.ACHIEVEMENT)
                    showToast(tr(lang, "สร้างและบันทึกชีทสรุป A4 ลงคลังเรียบร้อยแล้ว!", "A4 Study Sheet saved to library!", "A4复习讲义已生成并保存！"))
                    onCreated(sheet.copy(id = newId))
                }.onFailure { err ->
                    SoundEngine.playSfx(SfxType.WRONG_ANSWER)
                    showToast(err.message ?: tr(lang, "สร้างชีทสรุปไม่สำเร็จ", "Failed to generate Study Sheet.", "讲义生成失败。"))
                }
            } finally {
                _isGeneratingStudySheet.value = false
            }
        }
    }

    fun deleteStudySheet(sheetId: Long) {
        val uid = currentUser.value?.id ?: return
        pulseAutoSave {
            repo.deleteStudySheet(sheetId, uid)
        }
    }

    fun markNotificationsAllRead() {
        val uid = currentUser.value?.id ?: return
        viewModelScope.launch {
            repo.markNotificationsRead(uid)
        }
    }

    fun submitReport(category: String, message: String) {
        val u = currentUser.value ?: return
        val lang = activeLang()
        pulseAutoSave {
            repo.submitFeedbackReport(u.id, u.username, category, message)
                .onSuccess { showToast(tr(lang, "ส่งรายงานถึงผู้ดูแลระบบเรียบร้อยแล้ว", "Report sent to Admin.", "反馈已提交给管理员。")) }
                .onFailure { showToast(it.message ?: tr(lang, "ไม่สามารถส่งข้อความได้", "Cannot submit report.", "无法提交反馈。")) }
        }
    }

    // --- Admin Panel Security & Commands ---
    fun unlockAdminPanel(passcode: String): Boolean {
        val lang = activeLang()
        val lockoutSec = SecurityUtils.isAdminLockedOut()
        if (lockoutSec > 0) {
            showToast(tr(lang, "กรอกรหัสผิดเกินกำหนด กรุณารอ ${lockoutSec} วินาที", "Too many attempts — wait ${lockoutSec}s.", "密码尝试次数过多，请等待 ${lockoutSec} 秒。"))
            return false
        }
        val customHash = systemConfigs.value.firstOrNull { it.configKey == "ADMIN_PASSCODE_HASH" }?.configValue
        val ok = SecurityUtils.verifyAdminPasscode(passcode, customHash)
        if (ok) {
            _isAdminUnlocked.value = true
            SoundEngine.playSfx(SfxType.LEVEL_UP)
            showToast(tr(lang, "🛡️ ยืนยันสิทธิ์ผู้ดูแลระบบ (Admin) สำเร็จ!", "🛡️ Admin Panel unlocked!", "🛡️ 管理员权限验证成功！"))
        } else {
            SoundEngine.playSfx(SfxType.WRONG_ANSWER)
            showToast(tr(lang, "รหัสผ่าน Admin ไม่ถูกต้อง กรุณาตรวจสอบอีกครั้ง", "Incorrect Admin passcode.", "管理员密码不正确。"))
        }
        return ok
    }

    fun lockAdminPanel() {
        _isAdminUnlocked.value = false
    }

    fun adminSetCustomAdminPasscode(newPasscode: String) {
        if (!_isAdminUnlocked.value) return
        val clean = newPasscode.trim()
        if (clean.length < 6) {
            showToast(tr(activeLang(), "รหัส Admin ใหม่ต้องยาวอย่างน้อย 6 ตัวอักษร", "Admin code must be at least 6 chars.", "新管理员密码至少需6位。"))
            return
        }
        val hashed = SecurityUtils.hashAdminPasscode(clean)
        adminUpdateSystemConfig("ADMIN_PASSCODE_HASH", hashed)
    }

    fun adminQuickSelfReward(xpAdd: Int = 0, levelAdd: Int = 0, streakAdd: Int = 0, ptAdd: Int = 0) {
        if (!_isAdminUnlocked.value) return
        val me = currentUser.value ?: return
        val lang = activeLang()
        pulseAutoSave {
            repo.adminGrantReward(me.username, xpAdd, levelAdd, streakAdd, ptAdd, "Admin Quick Action")
                .onSuccess {
                    SoundEngine.playSfx(SfxType.ACHIEVEMENT)
                    showToast(tr(lang, "เพิ่มรางวัลให้ ${me.username} สำเร็จ!", "Granted reward to ${me.username}!", "已为 ${me.username} 发放奖励！"))
                }
                .onFailure { showToast(it.message ?: "") }
        }
    }

    fun adminGiveToUsername(username: String, xpAdd: Int = 0, levelAdd: Int = 0, streakAdd: Int = 0, ptAdd: Int = 0) {
        if (!_isAdminUnlocked.value) return
        val lang = activeLang()
        pulseAutoSave {
            repo.adminGrantReward(username, xpAdd, levelAdd, streakAdd, ptAdd, "Admin Targeted Grant")
                .onSuccess {
                    SoundEngine.playSfx(SfxType.ACHIEVEMENT)
                    showToast(tr(lang, "มอบรางวัลให้ $username เรียบร้อยแล้ว!", "Granted reward to $username!", "已向 $username 发放奖励！"))
                }
                .onFailure { showToast(it.message ?: "") }
        }
    }

    fun adminBanUsername(username: String, durationMillis: Long?, durationLabel: String, reason: String) {
        if (!_isAdminUnlocked.value) return
        val lang = activeLang()
        pulseAutoSave {
            repo.adminBanUser(username, durationMillis, durationLabel, reason)
                .onSuccess { showToast(tr(lang, "แบนผู้ใช้ $username ($durationLabel) แล้ว", "Banned $username ($durationLabel).", "已封禁用户 $username ($durationLabel)。")) }
                .onFailure { showToast(it.message ?: "") }
        }
    }

    fun adminSuspendUsername(username: String, reason: String) {
        if (!_isAdminUnlocked.value) return
        val lang = activeLang()
        pulseAutoSave {
            repo.adminTemporarySuspendUser(username, reason)
                .onSuccess { showToast(tr(lang, "พักบัญชี $username ชั่วคราวแล้ว", "Suspended $username.", "已暂时冻结 $username。")) }
                .onFailure { showToast(it.message ?: "") }
        }
    }

    fun adminRestoreUsername(username: String) {
        if (!_isAdminUnlocked.value) return
        val lang = activeLang()
        pulseAutoSave {
            repo.adminUnbanOrRestoreUser(username)
                .onSuccess { showToast(tr(lang, "ปลดแบน/กู้คืนบัญชี $username แล้ว", "Restored $username.", "已解封并恢复 $username。")) }
                .onFailure { showToast(it.message ?: "") }
        }
    }

    fun adminDeleteUsernameForever(username: String, reason: String) {
        if (!_isAdminUnlocked.value) return
        val lang = activeLang()
        pulseAutoSave {
            repo.adminDeleteUserForever(username, reason)
                .onSuccess { showToast(tr(lang, "ลบบัญชี $username ถาวรแล้ว", "Permanently deleted $username.", "已永久删除 $username。")) }
                .onFailure { showToast(it.message ?: "") }
        }
    }

    fun adminSaveQuestion(q: QuestionEntity, isNew: Boolean) {
        if (!_isAdminUnlocked.value) return
        pulseAutoSave {
            repo.adminSaveQuestion(q, isNew)
            showToast(tr(activeLang(), "บันทึกข้อมูลข้อสอบสำเร็จ!", "Question saved!", "考题已保存！"))
        }
    }

    fun adminDeleteQuestion(questionId: Long) {
        if (!_isAdminUnlocked.value) return
        pulseAutoSave {
            repo.adminDeleteQuestion(questionId)
            showToast(tr(activeLang(), "ลบข้อสอบเรียบร้อยแล้ว", "Question deleted.", "考题已删除。"))
        }
    }

    fun adminImportCsv(csvText: String) {
        if (!_isAdminUnlocked.value) return
        pulseAutoSave {
            val count = repo.adminImportQuestionsCsv(csvText)
            showToast(tr(activeLang(), "นำเข้าข้อสอบ CSV สำเร็จ $count ข้อ!", "Imported $count CSV questions!", "成功导入 $count 道CSV考题！"))
        }
    }

    fun adminExportCsv(onExported: (String) -> Unit) {
        if (!_isAdminUnlocked.value) return
        viewModelScope.launch {
            onExported(repo.adminExportQuestionsCsv())
        }
    }

    fun adminCreateQuest(quest: QuestDefinitionEntity) {
        if (!_isAdminUnlocked.value) return
        pulseAutoSave {
            repo.adminAddQuest(quest)
            showToast(tr(activeLang(), "สร้างภารกิจใหม่เรียบร้อยแล้ว!", "New quest created!", "新任务已创建！"))
        }
    }

    fun adminUpdateSystemConfig(key: String, value: String) {
        if (!_isAdminUnlocked.value) return
        pulseAutoSave {
            repo.adminUpdateConfig(key, value)
            showToast(tr(activeLang(), "อัปเดตค่า $key สำเร็จ!", "Updated $key!", "已更新系统配置 $key！"))
        }
    }

    fun adminResolveReport(id: Long) {
        if (!_isAdminUnlocked.value) return
        pulseAutoSave {
            repo.adminResolveFeedback(id)
        }
    }
}
