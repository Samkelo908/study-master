package com.example.studymaster

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

class TriviaApiService {
    private val baseUrl = "https://opentdb.com/api.php"

    suspend fun getQuestions(categoryId: Int, amount: Int = 10): List<Question> {
        return withContext(Dispatchers.IO) {
            try {
                val urlString = "$baseUrl?amount=$amount&category=$categoryId&type=multiple"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    parseQuestionsResponse(response)
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    private fun parseQuestionsResponse(jsonString: String): List<Question> {
        val questions = mutableListOf<Question>()

        try {
            val jsonObject = JSONObject(jsonString)
            val results = jsonObject.getJSONArray("results")

            for (i in 0 until results.length()) {
                val item = results.getJSONObject(i)

                val questionText = decodeHtml(item.getString("question"))
                val correctAnswer = decodeHtml(item.getString("correct_answer"))

                val incorrectAnswers = mutableListOf<String>()
                val incorrectArray = item.getJSONArray("incorrect_answers")
                for (j in 0 until incorrectArray.length()) {
                    incorrectAnswers.add(decodeHtml(incorrectArray.getString(j)))
                }

                val allAnswers = (incorrectAnswers + correctAnswer).shuffled()

                questions.add(
                    Question(
                        question = questionText,
                        correctAnswer = correctAnswer,
                        answers = allAnswers
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return questions
    }

    private fun decodeHtml(text: String): String {
        return try {
            URLDecoder.decode(
                text.replace("&quot;", "\"")
                    .replace("&amp;", "&")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&#039;", "'")
                    .replace("&rsquo;", "'")
                    .replace("&ldquo;", "\"")
                    .replace("&rdquo;", "\""),
                "UTF-8"
            )
        } catch (e: Exception) {
            text
        }
    }
}