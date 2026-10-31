package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        QuestionEntity::class,
        UserQuestionHistoryEntity::class,
        ExamSessionEntity::class,
        TransactionEntity::class,
        ShopPurchaseEntity::class,
        OwnedBackgroundEntity::class,
        OwnedFrameEntity::class,
        FlashcardEntity::class,
        StudyPlanTaskEntity::class,
        PetInstanceEntity::class,
        EggHistoryEntity::class,
        QuestDefinitionEntity::class,
        UserQuestProgressEntity::class,
        AchievementDefEntity::class,
        UserAchievementEntity::class,
        LeaderboardEntryEntity::class,
        NotificationEntity::class,
        StudySheetEntity::class,
        TutorMessageEntity::class,
        AdminAuditLogEntity::class,
        FeedbackReportEntity::class,
        SystemConfigEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class PtDatabase : RoomDatabase() {
    abstract fun ptDao(): PtDao

    companion object {
        @Volatile
        private var INSTANCE: PtDatabase? = null

        fun getInstance(context: Context): PtDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PtDatabase::class.java,
                    "pts_prem1_game.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
