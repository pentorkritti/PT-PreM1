package com.example.domain

import com.example.model.DifficultyLevel
import com.example.model.EggRarity
import com.example.model.GameCatalog
import com.example.model.PetSkillType
import com.example.model.PetSpecies
import com.example.util.SecurityUtils
import java.security.SecureRandom
import kotlin.math.floor
import kotlin.math.roundToInt

data class ExamOutcomeSummary(
    val totalQuestions: Int,
    val correctCount: Int,
    val wrongCount: Int,
    val accuracyPercent: Int,
    val maxCombo: Int,
    val maxWrongStreak: Int,
    val baseXp: Int,
    val finalXp: Int,
    val ptGainRaw: Int,
    val ptPenaltyRaw: Int,
    val netPtDelta: Int,
    val newPtBalance: Int,
    val oldLevel: Int,
    val newLevel: Int,
    val newXpTotal: Int,
    val comboShieldTriggered: Boolean,
    val solarShieldTriggered: Boolean,
    val streakBefore: Int,
    val streakAfter: Int,
    val recoveredMissedDay: Boolean
)

data class StreakUpdateResult(
    val newStreakDays: Int,
    val newPreMissStreakDays: Int,
    val lastExamDateBangkok: String,
    val recoveredDateBangkok: String,
    val recoveredMissedDay: Boolean,
    val shieldConsumed: Boolean = false
)

object ScoringAndPetEngine {
    private val secureRandom = SecureRandom()

    /**
     * XP required to advance from [level] to [level + 1].
     * Adjustable via [baseFactor] from Admin Panel (default = 100).
     */
    fun xpRequiredForNextLevel(level: Int, baseFactor: Int = 100): Int {
        val safeBase = baseFactor.coerceIn(50, 1000)
        val safeLevel = level.coerceAtLeast(1)
        return safeBase + (safeLevel - 1) * (safeBase / 2)
    }

    /**
     * Computes level and XP progress inside the current level from cumulative [totalXp].
     */
    fun computeLevelProgress(totalXp: Int, baseFactor: Int = 100): Triple<Int, Int, Int> {
        var remaining = totalXp.coerceAtLeast(0)
        var lvl = 1
        while (lvl < 999) {
            val needed = xpRequiredForNextLevel(lvl, baseFactor)
            if (remaining >= needed) {
                remaining -= needed
                lvl++
            } else {
                return Triple(lvl, remaining, needed)
            }
        }
        return Triple(lvl, remaining, xpRequiredForNextLevel(lvl, baseFactor))
    }

    fun xpToReachLevel(targetLevel: Int, baseFactor: Int = 100): Int {
        var sum = 0
        for (l in 1 until targetLevel.coerceAtLeast(1)) {
            sum += xpRequiredForNextLevel(l, baseFactor)
        }
        return sum
    }

    /**
     * Evaluates sequential answer correctness list [answerOutcomes] (true = correct, false = wrong)
     * strictly according to Sections 8, 9, 11, and 13!
     */
    fun evaluateExamOutcome(
        answerOutcomes: List<Boolean>,
        difficulty: DifficultyLevel,
        currentPtBalance: Int,
        currentTotalXp: Int,
        xpBaseFactor: Int,
        equippedPet: PetSpecies?,
        activeMultiplierGain: Double,
        activeMultiplierPenalty: Int,
        currentStreakDays: Int,
        preMissStreakDays: Int,
        lastExamDateBangkok: String,
        recoveredDateBangkok: String,
        nowMillis: Long = System.currentTimeMillis()
    ): ExamOutcomeSummary {
        val total = answerOutcomes.size.coerceAtLeast(1)
        val correctCount = answerOutcomes.count { it }
        val wrongCount = total - correctCount
        val accuracyPct = ((correctCount * 100.0) / total).roundToInt()

        val petSkill = equippedPet?.skillType
        var comboShieldAvailable = (petSkill == PetSkillType.COMBO_SHIELD_ONCE)
        var comboShieldUsed = false
        var solarShieldAvailable = (petSkill == PetSkillType.WRONG_STREAK_SHIELD)
        var solarShieldUsed = false

        var currentCombo = 0
        var maxCombo = 0
        var currentWrongStreak = 0
        var maxWrongStreak = 0

        val awardedComboMilestones = mutableSetOf<Int>()
        val awardedWrongMilestones = mutableSetOf<Int>()

        var rawCorrectPoints = 0
        var rawComboBonusPoints = 0
        var rawMilestoneDeductions = 0

        for (isCorrect in answerOutcomes) {
            if (isCorrect) {
                rawCorrectPoints += 1
                currentCombo += 1
                currentWrongStreak = 0
                if (currentCombo > maxCombo) maxCombo = currentCombo

                // Combo Milestones (each awarded at most once per exam set)
                if (currentCombo >= 5 && awardedComboMilestones.add(5)) rawComboBonusPoints += 10
                if (currentCombo >= 10 && awardedComboMilestones.add(10)) rawComboBonusPoints += 20
                if (currentCombo >= 25 && awardedComboMilestones.add(25)) rawComboBonusPoints += 50
                if (currentCombo >= 100 && awardedComboMilestones.add(100)) rawComboBonusPoints += 200
            } else {
                if (comboShieldAvailable) {
                    comboShieldAvailable = false
                    comboShieldUsed = true
                    // Combo does NOT break, though wrong streak still counts per spec
                } else {
                    currentCombo = 0
                }
                currentWrongStreak += 1
                if (currentWrongStreak > maxWrongStreak) maxWrongStreak = currentWrongStreak

                val milestoneHit: Pair<Int, Int>? = when {
                    currentWrongStreak == 2 && awardedWrongMilestones.add(2) -> 2 to 2
                    currentWrongStreak == 5 && awardedWrongMilestones.add(5) -> 5 to 15
                    currentWrongStreak == 10 && awardedWrongMilestones.add(10) -> 10 to 50
                    currentWrongStreak == 15 && awardedWrongMilestones.add(15) -> 15 to 70
                    else -> null
                }
                if (milestoneHit != null) {
                    if (solarShieldAvailable) {
                        solarShieldAvailable = false
                        solarShieldUsed = true
                    } else {
                        rawMilestoneDeductions += milestoneHit.second
                    }
                }
            }
        }

        val perfectBonus = if (correctCount == total && total > 0) 100 else 0
        val totalRawGain = rawCorrectPoints + rawComboBonusPoints + perfectBonus

        // Pet PT Gain bonus (+2% Bubble Whale, +10% Thunder Dragon)
        val petPtFactor = when (petSkill) {
            PetSkillType.PT_GAIN_BONUS_2 -> 1.02
            PetSkillType.PT_GAIN_BONUS_10 -> 1.10
            else -> 1.0
        }

        val effectiveMultGain = if (activeMultiplierGain > 1.0) activeMultiplierGain else 1.0
        val effectiveMultPenalty = if (activeMultiplierPenalty > 1) activeMultiplierPenalty else 1

        val scaledGain = (totalRawGain * petPtFactor * effectiveMultGain).roundToInt()

        // Rule 9.2: If ALL answers in the exam set are wrong, deduct 50% of existing PT Points
        // INSTEAD of accumulating milestone deductions (to prevent accidental double punishment).
        val scaledDeduction: Int = if (wrongCount == total && total > 0) {
            val baseHalf = if (currentPtBalance > 0) {
                floor(currentPtBalance / 2.0).toInt().coerceAtLeast(1)
            } else {
                // If already <= 0, apply base wrong penalty so all-wrong still carries weight
                (2 * effectiveMultPenalty)
            }
            baseHalf
        } else {
            rawMilestoneDeductions * effectiveMultPenalty
        }

        val netPtDelta = scaledGain - scaledDeduction
        val newPtBalance = currentPtBalance + netPtDelta

        // XP calculation (strictly separated from PT Points!)
        val baseXp = correctCount * difficulty.xpPerCorrect + (if (correctCount == total) 30 else 5)
        val petXpFactor = when (petSkill) {
            PetSkillType.XP_BONUS_2 -> 1.02
            PetSkillType.XP_BONUS_5 -> 1.05
            PetSkillType.XP_15_AND_SKIP_RETURN -> 1.15
            else -> 1.0
        }
        val finalXp = (baseXp * petXpFactor).roundToInt().coerceAtLeast(1)
        val oldLevel = computeLevelProgress(currentTotalXp, xpBaseFactor).first
        val newXpTotal = currentTotalXp + finalXp
        val newLevel = computeLevelProgress(newXpTotal, xpBaseFactor).first

        // Streak Flame update (only updates upon completing a full exam set, using Asia/Bangkok)
        val streakResult = updateStreakOnCompletedExam(
            currentStreakDays = currentStreakDays,
            preMissStreakDays = preMissStreakDays,
            lastExamDateBangkok = lastExamDateBangkok,
            recoveredDateBangkok = recoveredDateBangkok,
            nowMillis = nowMillis
        )

        return ExamOutcomeSummary(
            totalQuestions = total,
            correctCount = correctCount,
            wrongCount = wrongCount,
            accuracyPercent = accuracyPct,
            maxCombo = maxCombo,
            maxWrongStreak = maxWrongStreak,
            baseXp = baseXp,
            finalXp = finalXp,
            ptGainRaw = scaledGain,
            ptPenaltyRaw = scaledDeduction,
            netPtDelta = netPtDelta,
            newPtBalance = newPtBalance,
            oldLevel = oldLevel,
            newLevel = newLevel,
            newXpTotal = newXpTotal,
            comboShieldTriggered = comboShieldUsed,
            solarShieldTriggered = solarShieldUsed,
            streakBefore = currentStreakDays,
            streakAfter = streakResult.newStreakDays,
            recoveredMissedDay = streakResult.recoveredMissedDay
        )
    }

    /**
     * Asia/Bangkok Streak & 1-Day Recovery Engine (Section 10.3):
     * - Same day: keep current streak
     * - Consecutive next day (diff == 1): streak + 1
     * - Missed exactly 1 day (diff == 2) and not yet recovered for this gap:
     *   restores previous streak + today = currentStreakDays + 1 (once per missed date!)
     * - Missed > 1 day (diff >= 3): resets to 1 day for today's completed exam.
     */
    fun updateStreakOnCompletedExam(
        currentStreakDays: Int,
        preMissStreakDays: Int,
        lastExamDateBangkok: String,
        recoveredDateBangkok: String,
        nowMillis: Long = System.currentTimeMillis(),
        streakShieldCount: Int = 0
    ): StreakUpdateResult {
        val today = SecurityUtils.currentBangkokDateString(nowMillis)
        if (lastExamDateBangkok.isBlank()) {
            return StreakUpdateResult(
                newStreakDays = 1.coerceAtLeast(currentStreakDays + 1),
                newPreMissStreakDays = 0,
                lastExamDateBangkok = today,
                recoveredDateBangkok = recoveredDateBangkok,
                recoveredMissedDay = false,
                shieldConsumed = false
            )
        }
        val diff = SecurityUtils.daysBetweenBangkokDates(lastExamDateBangkok, today)
        return when {
            diff <= 0 -> StreakUpdateResult(
                newStreakDays = currentStreakDays.coerceAtLeast(1),
                newPreMissStreakDays = preMissStreakDays,
                lastExamDateBangkok = today,
                recoveredDateBangkok = recoveredDateBangkok,
                recoveredMissedDay = false,
                shieldConsumed = false
            )
            diff == 1 -> StreakUpdateResult(
                newStreakDays = currentStreakDays + 1,
                newPreMissStreakDays = currentStreakDays + 1,
                lastExamDateBangkok = today,
                recoveredDateBangkok = recoveredDateBangkok,
                recoveredMissedDay = false,
                shieldConsumed = false
            )
            diff == 2 && recoveredDateBangkok != lastExamDateBangkok && currentStreakDays > 0 -> {
                val restored = currentStreakDays + 1
                StreakUpdateResult(
                    newStreakDays = restored,
                    newPreMissStreakDays = restored,
                    lastExamDateBangkok = today,
                    recoveredDateBangkok = lastExamDateBangkok,
                    recoveredMissedDay = true,
                    shieldConsumed = false
                )
            }
            streakShieldCount > 0 && currentStreakDays > 0 -> {
                val shielded = currentStreakDays + 1
                StreakUpdateResult(
                    newStreakDays = shielded,
                    newPreMissStreakDays = shielded,
                    lastExamDateBangkok = today,
                    recoveredDateBangkok = recoveredDateBangkok,
                    recoveredMissedDay = true,
                    shieldConsumed = true
                )
            }
            else -> StreakUpdateResult(
                newStreakDays = 1,
                newPreMissStreakDays = 1,
                lastExamDateBangkok = today,
                recoveredDateBangkok = recoveredDateBangkok,
                recoveredMissedDay = false,
                shieldConsumed = false
            )
        }
    }

    /**
     * Exact Server-Grade Weighted Egg RNG (Section 13.2):
     * Common 50%, Uncommon 25%, Rare 13%, Epic 6%, Legendary 3%, Satriwit 2%, Suankularb 1% = 100%
     */
    fun rollEggAndPet(rollOverrideZeroTo99: Int? = null): Pair<EggRarity, PetSpecies> {
        val roll = (rollOverrideZeroTo99 ?: secureRandom.nextInt(100)).coerceIn(0, 99)
        val rarity = when (roll) {
            in 0..49 -> EggRarity.COMMON        // 50 values (0..49) = 50%
            in 50..74 -> EggRarity.UNCOMMON     // 25 values (50..74) = 25%
            in 75..87 -> EggRarity.RARE         // 13 values (75..87) = 13%
            in 88..93 -> EggRarity.EPIC         // 6 values (88..93) = 6%
            in 94..96 -> EggRarity.LEGENDARY    // 3 values (94..96) = 3%
            in 97..98 -> EggRarity.SATRIWIT     // 2 values (97..98) = 2%
            else -> EggRarity.SUANKULARB        // 1 value (99) = 1%
        }
        val pool = GameCatalog.pets.filter { it.rarity == rarity }
        val chosen = pool[secureRandom.nextInt(pool.size)]
        return rarity to chosen
    }

    fun examTimeMultiplierForPet(pet: PetSpecies?): Double = when (pet?.skillType) {
        PetSkillType.TIME_BONUS_3 -> 1.03
        PetSkillType.TIME_BONUS_6 -> 1.06
        PetSkillType.TIME_BONUS_10, PetSkillType.TIME_10_REMOVE_1_TWICE -> 1.10
        else -> 1.0
    }

    fun rankTitleForScore(score: Int): String = when {
        score >= 5000 -> "Grandmaster"
        score >= 2500 -> "Challenger"
        score >= 1200 -> "Diamond"
        score >= 600 -> "Platinum"
        score >= 250 -> "Gold"
        score >= 100 -> "Silver"
        else -> "Bronze"
    }
}
