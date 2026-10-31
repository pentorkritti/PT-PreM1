package com.example

import com.example.domain.ScoringAndPetEngine
import com.example.i18n.OFFICIAL_HTTPS_ORIGIN
import com.example.i18n.PlayerGender
import com.example.i18n.buildOfficialHttpsUrl
import com.example.model.DifficultyLevel
import com.example.model.EggRarity
import com.example.model.GameCatalog
import com.example.ui.screens.AppRoute
import com.example.ui.screens.parseHttpsDeepLinkRoute
import com.example.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `egg drop rates sum to 100 and map all 28 pets and expanded catalog`() {
        val totalRate = EggRarity.entries.sumOf { it.dropRatePercent }
        assertEquals(100, totalRate)
        assertEquals(28, GameCatalog.pets.size)
        assertEquals(18, GameCatalog.shopItems.size)
        assertEquals(10, GameCatalog.avatarFrames.size)
        assertEquals(5, GameCatalog.bosses.size)
        assertEquals(8, GameCatalog.luckyWheelSlices.size)
        assertEquals(3, GameCatalog.mysteryChests.size)

        assertEquals(EggRarity.COMMON, ScoringAndPetEngine.rollEggAndPet(0).first)
        assertEquals(EggRarity.COMMON, ScoringAndPetEngine.rollEggAndPet(49).first)
        assertEquals(EggRarity.UNCOMMON, ScoringAndPetEngine.rollEggAndPet(50).first)
        assertEquals(EggRarity.RARE, ScoringAndPetEngine.rollEggAndPet(75).first)
        assertEquals(EggRarity.EPIC, ScoringAndPetEngine.rollEggAndPet(88).first)
        assertEquals(EggRarity.LEGENDARY, ScoringAndPetEngine.rollEggAndPet(94).first)
        assertEquals(EggRarity.SATRIWIT, ScoringAndPetEngine.rollEggAndPet(97).first)
        assertEquals(EggRarity.SUANKULARB, ScoringAndPetEngine.rollEggAndPet(99).first)
    }

    @Test
    fun `admin passcode verification succeeds for configured passcode and rejects invalid input`() {
        val expectedAdminChars = charArrayOf('P', 'e', 'n', 't', 'o', 'r', 'A', 'd', 'm', 'i', 'n', '1', '2')
        val validCode = String(expectedAdminChars)
        assertTrue(SecurityUtils.verifyAdminPasscode(validCode))
        assertTrue(SecurityUtils.verifyAdminPasscode("  $validCode  "))
        assertFalse(SecurityUtils.verifyAdminPasscode("WrongPass123"))
        assertFalse(SecurityUtils.verifyAdminPasscode(""))
    }

    @Test
    fun `official https PTPreM1 online URLs and deep links resolve accurately`() {
        assertEquals("https://PTPreM1.online", OFFICIAL_HTTPS_ORIGIN)
        assertEquals("https://PTPreM1.online/boss", buildOfficialHttpsUrl("/boss"))
        assertEquals("https://PTPreM1.online/@pentor", buildOfficialHttpsUrl("@pentor"))

        assertEquals(AppRoute.SMART_STUDY_HUB, parseHttpsDeepLinkRoute("https://PTPreM1.online/study"))
        assertEquals(AppRoute.BOSS_RAID, parseHttpsDeepLinkRoute("https://www.PTPreM1.online/boss"))
        assertEquals(AppRoute.DAILY_REWARDS_PLAZA, parseHttpsDeepLinkRoute("https://PTPreM1.online/wheel"))
        assertEquals(AppRoute.PROFILE_STATS, parseHttpsDeepLinkRoute("https://PTPreM1.online/@student1"))
        assertEquals(AppRoute.ADMIN_PANEL, parseHttpsDeepLinkRoute("https://PTPreM1.online/admin"))
    }

    @Test
    fun `streak shield protects multi day gap without resetting streak`() {
        val threeDaysAgo = SecurityUtils.bangkokDateDaysAgo(4)
        val shieldedResult = ScoringAndPetEngine.updateStreakOnCompletedExam(
            currentStreakDays = 12,
            preMissStreakDays = 12,
            lastExamDateBangkok = threeDaysAgo,
            recoveredDateBangkok = "",
            streakShieldCount = 1
        )
        assertEquals(13, shieldedResult.newStreakDays)
        assertTrue(shieldedResult.shieldConsumed)

        val unshieldedResult = ScoringAndPetEngine.updateStreakOnCompletedExam(
            currentStreakDays = 12,
            preMissStreakDays = 12,
            lastExamDateBangkok = threeDaysAgo,
            recoveredDateBangkok = "",
            streakShieldCount = 0
        )
        assertEquals(1, unshieldedResult.newStreakDays)
        assertFalse(unshieldedResult.shieldConsumed)
    }

    @Test
    fun `player gender mapping covers male female and unspecified`() {
        assertEquals(PlayerGender.MALE, PlayerGender.fromCode("MALE"))
        assertEquals(PlayerGender.FEMALE, PlayerGender.fromCode("FEMALE"))
        assertEquals(PlayerGender.UNSPECIFIED, PlayerGender.fromCode("UNSPECIFIED"))
        assertEquals(PlayerGender.UNSPECIFIED, PlayerGender.fromCode(null))
    }

    @Test
    fun `perfect 5 question exam awards combo and 100 percent bonus`() {
        val res = ScoringAndPetEngine.evaluateExamOutcome(
            answerOutcomes = listOf(true, true, true, true, true),
            difficulty = DifficultyLevel.NORMAL,
            currentPtBalance = 150,
            currentTotalXp = 0,
            xpBaseFactor = 100,
            equippedPet = null,
            activeMultiplierGain = 1.0,
            activeMultiplierPenalty = 1,
            currentStreakDays = 0,
            preMissStreakDays = 0,
            lastExamDateBangkok = "",
            recoveredDateBangkok = ""
        )
        // 5 correct (+5) + Combo 5 (+10) + Perfect 100% (+100) = +115 PT
        assertEquals(115, res.netPtDelta)
        assertEquals(265, res.newPtBalance)
        assertTrue(res.finalXp > 0)
    }

    @Test
    fun `wrong streak penalty can drive PT points negative without lowering XP`() {
        val res = ScoringAndPetEngine.evaluateExamOutcome(
            answerOutcomes = listOf(true, false, false, false, false, false),
            difficulty = DifficultyLevel.EASY,
            currentPtBalance = 5,
            currentTotalXp = 250,
            xpBaseFactor = 100,
            equippedPet = null,
            activeMultiplierGain = 1.5,
            activeMultiplierPenalty = 3,
            currentStreakDays = 2,
            preMissStreakDays = 2,
            lastExamDateBangkok = "2026-10-30",
            recoveredDateBangkok = ""
        )
        assertTrue(res.newPtBalance < 0)
        assertTrue(res.newXpTotal > 250)
    }

    @Test
    fun `all 13 streak flame tiers map cleanly`() {
        assertEquals(13, GameCatalog.streakTiers.size)
        assertEquals(0, GameCatalog.getStreakTier(0).tierIndex)
        assertEquals(1, GameCatalog.getStreakTier(3).tierIndex)
        assertEquals(8, GameCatalog.getStreakTier(120).tierIndex)
        assertEquals(12, GameCatalog.getStreakTier(300).tierIndex)
    }
}
