package com.example.studymaster

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GiphyApiService {
    // Public Giphy API key for development (get your own from developers.giphy.com)
    private val apiKey = "your_giphy_api_key_here" // Replace with actual key
    private val baseUrl = "https://api.giphy.com/v1/gifs"

    suspend fun getCelebrationGif(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val searchTerms = listOf(
                    "celebration",
                    "success",
                    "winner",
                    "congratulations",
                    "party",
                    "awesome"
                ).random()

                val urlString = "$baseUrl/search?api_key=$apiKey&q=$searchTerms&limit=20&rating=g"
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

                    parseGifResponse(response)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun parseGifResponse(jsonString: String): String? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val data = jsonObject.getJSONArray("data")

            if (data.length() > 0) {
                val randomIndex = (0 until data.length()).random()
                val gif = data.getJSONObject(randomIndex)
                val images = gif.getJSONObject("images")
                val original = images.getJSONObject("original")
                original.getString("url")
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}