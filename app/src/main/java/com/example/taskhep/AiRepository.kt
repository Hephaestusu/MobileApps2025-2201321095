package com.example.taskhep

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend

class AiRepository {

    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash-lite")

    suspend fun generateSubtasks(taskText: String): String {
        return try {
            val prompt = """
                Break the task into clear actionable subtasks.
                Return a clean numbered list.
                Do not add intro text.
                Do not add conclusion text.
                Task: $taskText
            """.trimIndent()

            val response = model.generateContent(prompt)
            response.text ?: "No response"
        } catch (e: Exception) {
            Log.e("AI", "Error: ${e.message}", e)
            "Generation error: ${e.message}"
        }
    }
}