package com.example.studymaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Screen {
    CATEGORY_SELECTION,
    QUIZ,
    RESULTS
}

enum class QuizCategory(val displayName: String, val emoji: String, val apiId: Int) {
    GENERAL("General Knowledge", "🧠", 9),
    SCIENCE("Science & Nature", "🔬", 17),
    COMPUTERS("Computers", "💻", 18),
    MATHEMATICS("Mathematics", "🔢", 19),
    SPORTS("Sports", "⚽", 21),
    GEOGRAPHY("Geography", "🌍", 22),
    HISTORY("History", "📜", 23),
    ANIMALS("Animals", "🐾", 27)
}

data class Question(
    val question: String,
    val correctAnswer: String,
    val answers: List<String>
)

data class UiState(
    val screen: Screen = Screen.CATEGORY_SELECTION,
    val currentCategory: QuizCategory? = null,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val currentQuestion: Question? = null,
    val score: Int = 0,
    val isLoading: Boolean = false,
    val celebrationGif: String? = null
)

class QuizViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val triviaApi = TriviaApiService()
    private val giphyApi = GiphyApiService()

    fun startQuiz(category: QuizCategory) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                currentCategory = category
            )

            try {
                val questions = triviaApi.getQuestions(category.apiId)
                _uiState.value = _uiState.value.copy(
                    screen = Screen.QUIZ,
                    questions = questions,
                    currentQuestionIndex = 0,
                    currentQuestion = questions.firstOrNull(),
                    score = 0,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
        }
    }

    fun submitAnswer(answer: String) {
        val currentState = _uiState.value
        val isCorrect = answer == currentState.currentQuestion?.correctAnswer

        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex < currentState.questions.size) {
            _uiState.value = currentState.copy(
                currentQuestionIndex = nextIndex,
                currentQuestion = currentState.questions[nextIndex],
                score = newScore
            )
        } else {
            finishQuiz(newScore, currentState.questions.size)
        }
    }

    private fun finishQuiz(finalScore: Int, totalQuestions: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val percentage = (finalScore.toFloat() / totalQuestions * 100).toInt()
            var gifUrl: String? = null

            if (percentage >= 70) {
                try {
                    gifUrl = giphyApi.getCelebrationGif()
                } catch (e: Exception) {
                    // Continue without GIF if API fails
                }
            }

            _uiState.value = _uiState.value.copy(
                screen = Screen.RESULTS,
                score = finalScore,
                celebrationGif = gifUrl,
                isLoading = false
            )
        }
    }

    fun resetQuiz() {
        _uiState.value = UiState()
    }
}