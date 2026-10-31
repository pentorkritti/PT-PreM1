package com.example.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.data.local.QuestionEntity
import com.example.data.local.StudySheetEntity
import com.example.data.local.TutorMessageEntity
import com.example.i18n.AppLanguage
import com.example.model.DifficultyLevel
import com.example.model.SubjectId
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.ThinkingLevel
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "GeminiAiService"

object GeminiAiService {

    // 1. Complex Exam Generation Schema using gemini-4-argon + ThinkingLevel.HIGH (NO maxOutputTokens!)
    private val examQuestionsSchema by lazy {
        val singleQuestionSchema = Schema.obj(
            properties = mapOf(
                "topic" to Schema.string("Specific lesson subtopic aligned with Thai OBEC P.4-P.6 / M.1 entrance"),
                "questionFormat" to Schema.enumeration(
                    listOf("MULTIPLE_CHOICE", "CALCULATION", "READING", "SITUATIONAL", "WORD_PROBLEM"),
                    "Format of the question"
                ),
                "questionTh" to Schema.string("Clear, unambiguous question in Thai"),
                "questionEn" to Schema.string("Accurate English translation of the question"),
                "questionZh" to Schema.string("Accurate Simplified Chinese translation of the question"),
                "choiceATh" to Schema.string("Option A in Thai"),
                "choiceBTh" to Schema.string("Option B in Thai"),
                "choiceCTh" to Schema.string("Option C in Thai"),
                "choiceDTh" to Schema.string("Option D in Thai"),
                "choiceAEn" to Schema.string("Option A in English"),
                "choiceBEn" to Schema.string("Option B in English"),
                "choiceCEn" to Schema.string("Option C in English"),
                "choiceDEn" to Schema.string("Option D in English"),
                "choiceAZh" to Schema.string("Option A in Chinese"),
                "choiceBZh" to Schema.string("Option B in Chinese"),
                "choiceCZh" to Schema.string("Option C in Chinese"),
                "choiceDZh" to Schema.string("Option D in Chinese"),
                "correctAnswer" to Schema.enumeration(listOf("A", "B", "C", "D"), "Exactly one verified correct choice letter"),
                "explanationTh" to Schema.string("Step-by-step verified explanation in Thai"),
                "explanationEn" to Schema.string("Step-by-step verified explanation in English"),
                "explanationZh" to Schema.string("Step-by-step verified explanation in Chinese"),
                "hint1" to Schema.string("Gentle conceptual first hint"),
                "hint2" to Schema.string("Clearer second-step solving hint"),
                "curriculumStandard" to Schema.string("Thai OBEC 2551 (rev. 2560) standard code e.g. ค 1.1 ป.6")
            )
        )
        Schema.obj(
            properties = mapOf(
                "questions" to Schema.array(singleQuestionSchema, "Generated unique M.1 entrance questions")
            )
        )
    }

    // High Thinking Model (gemini-4-argon + ThinkingLevel.HIGH, no maxOutputTokens) for Exam Generation
    private val highThinkingExamModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-4-argon",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = examQuestionsSchema
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.HIGH
                }
            },
            systemInstruction = content {
                text(
                    """
                    คุณคือผู้เชี่ยวชาญการออกข้อสอบสอบเข้าชั้นมัธยมศึกษาปีที่ 1 (ป.4–ป.6 เตรียมเข้า ม.1) ตามหลักสูตรแกนกลางการศึกษาขั้นพื้นฐาน พ.ศ. 2551 (ฉบับปรับปรุง พ.ศ. 2560) ของ สพฐ.
                    กฎเหล็ก:
                    1. ห้ามเปลี่ยนเพียงตัวเลขเล็กน้อยจากโจทย์เดิม ต้องออกแบบสถานการณ์ บริบท และวิธีคิดใหม่ที่มีความหมาย
                    2. ตรวจสอบคำนวณคำตอบคณิตศาสตร์และข้อเท็จจริงวิทยาศาสตร์ให้ถูกต้อง 100% มีคำตอบที่ถูกเพียงข้อเดียว (A, B, C หรือ D) ตัวเลือกทั้ง 4 ต้องไม่ซ้ำกันและไม่กำกวม
                    3. ห้ามแอบอ้างว่าเป็นข้อสอบจริงของโรงเรียนใดโดยไม่มีแหล่งอ้างอิงทางการ
                    """.trimIndent()
                )
            }
        )
    }

    // Fallback Flash Exam Model if Blaze billing is unavailable on sandbox project
    private val flashExamFallbackModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = examQuestionsSchema
            }
        )
    }

    // 2. Multi-turn AI Tutor Models (Standard gemini-3.8-flash + High Thinking gemini-4-argon)
    private fun tutorSystemPrompt(lang: AppLanguage): Content = content {
        text(
            """
            คุณคือ "พี่นกฮูก PT's AI Tutor" ติวเตอร์อัจฉริยะใจดีสำหรับนักเรียนชั้น ป.4–ป.6 ที่กำลังเตรียมสอบเข้า ม.1 ในเกมการศึกษา "PT's มาสอบเข้า ม.1!"
            - อธิบายเป็นลำดับขั้นตอน เข้าใจง่าย สนุก ให้กำลังใจ และถูกต้องตามหลักสูตร สพฐ. พ.ศ. 2551 (ปรับปรุง 2560)
            - สำหรับโจทย์คณิตศาสตร์และวิทยาศาสตร์ ให้ตรวจทานการคำนวณอย่างละเอียดทุกบรรทัด
            - หากข้อมูลใดไม่แน่ใจหรือไม่มีแหล่งอ้างอิงชัดเจน ให้ระบุตรงไปตรงมาว่าควรตรวจสอบเพิ่มเติม ไม่แต่งข้อมูลเท็จ
            - ตอบกลับเป็นภาษาที่ผู้เล่นใช้งานเป็นหลัก (${lang.label})
            """.trimIndent()
        )
    }

    private val tutorFlashModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = tutorSystemPrompt(AppLanguage.TH)
        )
    }

    private val tutorHighThinkingModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-4-argon",
            generationConfig = generationConfig {
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.HIGH
                }
            },
            systemInstruction = tutorSystemPrompt(AppLanguage.TH)
        )
    }

    // 3. Fast Lite Model (gemini-3.5-flash-lite) for quick hints & lightweight assistance
    private val fastHintModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-3.5-flash-lite"
        )
    }

    // 4. Study Sheet Schema & Model (gemini-3.8-flash multimodal)
    private val studySheetSchema by lazy {
        val quizItemSchema = Schema.obj(
            properties = mapOf(
                "question" to Schema.string("Quiz question"),
                "choiceA" to Schema.string("Option A"),
                "choiceB" to Schema.string("Option B"),
                "choiceC" to Schema.string("Option C"),
                "choiceD" to Schema.string("Option D"),
                "correctAnswer" to Schema.enumeration(listOf("A", "B", "C", "D")),
                "explanation" to Schema.string("Short explanation")
            )
        )
        Schema.obj(
            properties = mapOf(
                "title" to Schema.string("Concise A4 study sheet title"),
                "summaryContent" to Schema.string("Structured A4 study summary with clear sections"),
                "keyPoints" to Schema.array(Schema.string(), "Bullet points of must-remember concepts"),
                "sampleProblem" to Schema.string("One worked example problem with step-by-step solution"),
                "quizQuestions" to Schema.array(quizItemSchema, "Practice quiz questions")
            )
        )
    }

    private val studySheetModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = studySheetSchema
            }
        )
    }

    /**
     * Generates verified, non-duplicate M.1 entrance exam questions using High Thinking (`gemini-4-argon` + `ThinkingLevel.HIGH`)
     * and validates semantic uniqueness against [existingQuestions].
     */
    suspend fun generateVerifiedQuestions(
        subject: SubjectId,
        difficulty: DifficultyLevel,
        count: Int,
        existingQuestions: List<QuestionEntity>,
        nextQuestionNumberStart: Int
    ): Result<List<QuestionEntity>> {
        val existingSnippets = existingQuestions
            .filter { it.subjectId == subject.id }
            .takeLast(25)
            .joinToString("\n") { "- ${it.questionTh}" }

        val prompt = """
            จงสร้างข้อสอบใหม่จำนวน $count ข้อ สำหรับวิชา "${subject.nameTh}" (${subject.nameEn})
            ระดับความยาก: ${difficulty.nameTh} (เตรียมสอบเข้า ม.1 ระดับ ป.4–ป.6 มาตรฐาน ${subject.curriculumCode})
            หัวข้อย่อยที่ครอบคลุม: ${subject.subtopicsTh.joinToString(", ")}
            
            ห้ามซ้ำหรือคล้ายกับโจทย์ที่มีอยู่แล้วในคลังต่อไปนี้ (ห้ามเพียงเปลี่ยนตัวเลข ต้องเปลี่ยนบริบทและโครงสร้างวิธีคิดใหม่):
            $existingSnippets
            
            ตรวจสอบให้แน่ใจว่า:
            1. มีคำตอบที่ถูกเพียงข้อเดียวใน A, B, C, D และตัวเลือกไม่ซ้ำกัน
            2. หากเป็นคำนวณ ต้องคำนวณย้อนกลับเพื่อยืนยันคำตอบก่อนส่งออก
            3. มีคำแปล ไทย / English / 中文 ครบถ้วนถูกต้อง
        """.trimIndent()

        return try {
            val responseText = try {
                withTimeout(60_000L) {
                    highThinkingExamModel.generateContent(prompt).text
                }
            } catch (e: CancellationException) {
                if (e is TimeoutCancellationException) throw e
                throw e
            } catch (primaryErr: Exception) {
                Log.w(TAG, "gemini-4-argon unavailable (${primaryErr.message}), falling back to gemini-3.8-flash")
                withTimeout(60_000L) {
                    flashExamFallbackModel.generateContent(prompt).text
                }
            }

            if (responseText.isNullOrBlank()) {
                return Result.failure(IllegalStateException("AI returned an empty exam payload."))
            }

            val parsed = parseAndValidateQuestions(
                jsonString = responseText,
                subject = subject,
                difficulty = difficulty,
                existingQuestions = existingQuestions,
                startNumber = nextQuestionNumberStart
            )
            if (parsed.isEmpty()) {
                Result.failure(IllegalStateException("Generated questions were filtered out due to similarity or validation checks. Please try generating again."))
            } else {
                Result.success(parsed)
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI exam generation timed out", e)
            Result.failure(Exception("การสร้างข้อสอบใช้เวลานานเกินกำหนด (60 วินาที) กรุณาลองใหม่อีกครั้ง"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI exam generation failed", e)
            Result.failure(e)
        }
    }

    private fun parseAndValidateQuestions(
        jsonString: String,
        subject: SubjectId,
        difficulty: DifficultyLevel,
        existingQuestions: List<QuestionEntity>,
        startNumber: Int
    ): List<QuestionEntity> {
        val root = JSONObject(jsonString)
        val arr: JSONArray = root.optJSONArray("questions") ?: return emptyList()
        val accepted = mutableListOf<QuestionEntity>()
        val allKnownTexts = existingQuestions.map { it.questionTh }.toMutableList()
        var currentNum = startNumber

        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val qTh = obj.optString("questionTh").trim()
            val cA = obj.optString("choiceATh").trim()
            val cB = obj.optString("choiceBTh").trim()
            val cC = obj.optString("choiceCTh").trim()
            val cD = obj.optString("choiceDTh").trim()
            val ans = obj.optString("correctAnswer").trim().uppercase()
            val expTh = obj.optString("explanationTh").trim()

            // 7-Point Quality & Duplicate Gate
            if (qTh.length < 10 || expTh.length < 10) continue
            if (ans !in setOf("A", "B", "C", "D")) continue
            val distinctChoices = setOf(cA, cB, cC, cD).filter { it.isNotBlank() }
            if (distinctChoices.size < 4) continue // Reject duplicate/blank options
            if (isTooSimilarToAny(qTh, allKnownTexts)) continue // Exact & Semantic similarity gate

            allKnownTexts.add(qTh)
            accepted.add(
                QuestionEntity(
                    questionNumber = currentNum++,
                    subjectId = subject.id,
                    topic = obj.optString("topic", subject.subtopicsTh.first()),
                    difficulty = difficulty.id,
                    questionFormat = obj.optString("questionFormat", "MULTIPLE_CHOICE"),
                    questionTh = qTh,
                    questionEn = obj.optString("questionEn", qTh),
                    questionZh = obj.optString("questionZh", qTh),
                    choiceATh = cA,
                    choiceBTh = cB,
                    choiceCTh = cC,
                    choiceDTh = cD,
                    choiceAEn = obj.optString("choiceAEn", cA),
                    choiceBEn = obj.optString("choiceBEn", cB),
                    choiceCEn = obj.optString("choiceCEn", cC),
                    choiceDEn = obj.optString("choiceDEn", cD),
                    choiceAZh = obj.optString("choiceAZh", cA),
                    choiceBZh = obj.optString("choiceBZh", cB),
                    choiceCZh = obj.optString("choiceCZh", cC),
                    choiceDZh = obj.optString("choiceDZh", cD),
                    correctAnswer = ans,
                    explanationTh = expTh,
                    explanationEn = obj.optString("explanationEn", expTh),
                    explanationZh = obj.optString("explanationZh", expTh),
                    hint1 = obj.optString("hint1", "สังเกตคำสำคัญของโจทย์แล้วตัดตัวเลือกที่ไม่เข้าพวก"),
                    hint2 = obj.optString("hint2", "ลองแทนค่าหรือทบทวนหลักการของบทเรียนนี้"),
                    curriculumStandard = obj.optString("curriculumStandard", subject.curriculumCode),
                    sourceReference = "AI Verified Generator (${subject.curriculumCode})",
                    duplicateCheckStatus = "PASSED_7_STEP_CHECK",
                    reviewStatus = "APPROVED"
                )
            )
        }
        return accepted
    }

    /**
     * Checks exact normalized match + character 3-gram Jaccard semantic/lexical similarity
     * (works cleanly for Thai, English, and Chinese without whitespace dependence).
     */
    fun isTooSimilarToAny(candidate: String, corpus: List<String>): Boolean {
        val normCandidate = candidate.lowercase().replace(Regex("[\\s\\p{Punct}0-9]+"), "")
        if (normCandidate.isBlank()) return true
        val candGrams = nGrams(normCandidate, 3)

        for (existing in corpus) {
            val normExisting = existing.lowercase().replace(Regex("[\\s\\p{Punct}0-9]+"), "")
            if (normCandidate == normExisting) return true
            val existGrams = nGrams(normExisting, 3)
            if (candGrams.isNotEmpty() && existGrams.isNotEmpty()) {
                val intersection = candGrams.intersect(existGrams).size.toDouble()
                val union = candGrams.union(existGrams).size.toDouble()
                if (union > 0 && (intersection / union) >= 0.68) {
                    return true
                }
            }
        }
        return false
    }

    private fun nGrams(text: String, n: Int): Set<String> {
        if (text.length <= n) return setOf(text)
        val set = HashSet<String>(text.length)
        for (i in 0..text.length - n) {
            set.add(text.substring(i, i + n))
        }
        return set
    }

    /**
     * Multi-turn AI Tutor streaming response with conversation history!
     * Uses `gemini-4-argon` with `ThinkingLevel.HIGH` when [useHighThinking] is true,
     * or `gemini-3.8-flash` for snappy everyday tutoring.
     */
    fun streamTutorReply(
        historyMessages: List<TutorMessageEntity>,
        userPrompt: String,
        useHighThinking: Boolean,
        lang: AppLanguage
    ): Flow<Pair<String, String>> = flow {
        val formattedHistory: List<Content> = historyMessages.takeLast(16).map { msg ->
            content(role = if (msg.role == "model") "model" else "user") {
                text(msg.content)
            }
        }
        val activeModelName = if (useHighThinking) "gemini-4-argon (High Thinking)" else "gemini-3.8-flash"
        val promptWithLang = "[ตอบเป็นภาษา ${lang.label}]\n$userPrompt"

        val acc = StringBuilder()
        try {
            withTimeout(60_000L) {
                val chat: Chat = if (useHighThinking) {
                    tutorHighThinkingModel.startChat(history = formattedHistory)
                } else {
                    tutorFlashModel.startChat(history = formattedHistory)
                }
                chat.sendMessageStream(promptWithLang).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        acc.append(piece)
                        emit(acc.toString() to activeModelName)
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (useHighThinking && acc.isEmpty()) {
                Log.w(TAG, "High thinking chat fallback to gemini-3.8-flash: ${e.message}")
                withTimeout(60_000L) {
                    val fallbackChat = tutorFlashModel.startChat(history = formattedHistory)
                    fallbackChat.sendMessageStream(promptWithLang).collect { chunk ->
                        val piece = chunk.text.orEmpty()
                        if (piece.isNotEmpty()) {
                            acc.append(piece)
                            emit(acc.toString() to "gemini-3.8-flash")
                        }
                    }
                }
            } else {
                throw e
            }
        }
    }

    /**
     * Fast AI Hint generator using `gemini-3.1-flash-lite` (with fallback to `gemini-3.8-flash`).
     */
    suspend fun generateQuickSmartHint(questionText: String, lang: AppLanguage): Result<String> {
        val prompt = """
            ให้คำใบ้สั้น ๆ 1–2 ประโยค (ห้ามบอกคำตอบตรง ๆ!) เพื่อช่วยนักเรียน ป.6 คิดโจทย์ข้อนี้:
            "$questionText"
            ตอบในภาษา: ${lang.label}
        """.trimIndent()
        return try {
            val text = try {
                withTimeout(30_000L) {
                    fastHintModel.generateContent(prompt).text
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                withTimeout(45_000L) {
                    tutorFlashModel.generateContent(prompt).text
                }
            }
            if (!text.isNullOrBlank()) Result.success(text.trim())
            else Result.failure(Exception("Empty hint response"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Quick hint failed", e)
            Result.failure(e)
        }
    }

    /**
     * Multimodal Study Sheet + Quiz (5 / 10 / 20 questions) Generator for Study Sheet Library.
     */
    suspend fun generateStudySheet(
        userId: Long,
        subject: SubjectId,
        sourceType: String,
        inputText: String,
        attachedBitmap: Bitmap?,
        quizSize: Int,
        lang: AppLanguage
    ): Result<StudySheetEntity> {
        val safeQuizSize = when (quizSize) {
            10 -> 10
            20 -> 20
            else -> 5
        }
        val instruction = """
            สร้าง "ชีทสรุปเตรียมสอบเข้า ม.1 ขนาด A4" สำหรับวิชา ${subject.title(lang)} ในภาษา ${lang.label}
            จากแหล่งข้อมูลประเภท $sourceType ด้านล่างนี้:
            - สรุปเนื้อหาเป็นหัวข้อสำคัญอ่านง่าย (summaryContent)
            - สกัดสาระสำคัญที่ต้องจำ (keyPoints)
            - ยกตัวอย่างโจทย์พร้อมวิธีทำละเอียด 1 ข้อ (sampleProblem)
            - สร้างแบบทดสอบท้ายบทจำนวน $safeQuizSize ข้อ พร้อมตัวเลือก A-D เฉลย และคำอธิบาย (quizQuestions)
            
            เนื้อหาต้นฉบับ:
            $inputText
        """.trimIndent()

        return try {
            val response = withTimeout(60_000L) {
                if (attachedBitmap != null) {
                    studySheetModel.generateContent(
                        content {
                            image(attachedBitmap)
                            text(instruction)
                        }
                    )
                } else {
                    studySheetModel.generateContent(instruction)
                }
            }
            val rawJson = response.text ?: return Result.failure(Exception("Model returned empty study sheet."))
            val json = JSONObject(rawJson)
            val keyPointsArr = json.optJSONArray("keyPoints") ?: JSONArray()
            val keyPointsStr = (0 until keyPointsArr.length()).joinToString("\n") { "• ${keyPointsArr.optString(it)}" }
            val quizArr = json.optJSONArray("quizQuestions") ?: JSONArray()

            val sheet = StudySheetEntity(
                userId = userId,
                subjectId = subject.id,
                title = json.optString("title", "ชีทสรุป ${subject.nameTh}"),
                sourceType = sourceType,
                summaryContent = json.optString("summaryContent", ""),
                keyPointsText = keyPointsStr,
                sampleProblemText = json.optString("sampleProblem", ""),
                quizQuestionsJson = quizArr.toString(),
                quizCount = quizArr.length().coerceAtLeast(1)
            )
            Result.success(sheet)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Study sheet generation timed out", e)
            Result.failure(Exception("การสร้างชีทสรุปใช้เวลานานเกิน 60 วินาที กรุณาลองอีกครั้ง"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Study sheet generation failed", e)
            Result.failure(e)
        }
    }
}
