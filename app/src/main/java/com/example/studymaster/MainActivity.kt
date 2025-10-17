package com.example.studymaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.studymaster.ui.theme.StudyMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyMasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudyMasterApp()
                }
            }
        }
    }
}

@Composable
fun StudyMasterApp() {
    val viewModel: QuizViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        when (uiState.screen) {
            Screen.CATEGORY_SELECTION -> CategorySelectionScreen(
                onCategorySelected = { category -> viewModel.startQuiz(category) },
                isLoading = uiState.isLoading
            )
            Screen.QUIZ -> QuizScreen(
                question = uiState.currentQuestion,
                questionNumber = uiState.currentQuestionIndex + 1,
                totalQuestions = uiState.questions.size,
                onAnswerSelected = { answer -> viewModel.submitAnswer(answer) },
                isLoading = uiState.isLoading
            )
            Screen.RESULTS -> ResultsScreen(
                score = uiState.score,
                totalQuestions = uiState.questions.size,
                gifUrl = uiState.celebrationGif,
                onRetry = { viewModel.resetQuiz() }
            )
        }
    }
}

@Composable
fun CategorySelectionScreen(
    onCategorySelected: (QuizCategory) -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎓 Study Master",
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Choose Your Challenge",
            fontSize = 20.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(QuizCategory.values()) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategorySelected(category) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(category: QuizCategory, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = category.emoji,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = category.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF667eea)
                )
            }
        }
    }
}

@Composable
fun QuizScreen(
    question: Question?,
    questionNumber: Int,
    totalQuestions: Int,
    onAnswerSelected: (String) -> Unit,
    isLoading: Boolean
) {
    if (question == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Question $questionNumber of $totalQuestions",
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.9f),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = question.question,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2d3748),
                    lineHeight = 32.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                question.answers.forEach { answer ->
                    AnswerButton(
                        answer = answer,
                        onClick = { onAnswerSelected(answer) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnswerButton(answer: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF667eea)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(
            text = answer,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ResultsScreen(
    score: Int,
    totalQuestions: Int,
    gifUrl: String?,
    onRetry: () -> Unit
) {
    val percentage = (score.toFloat() / totalQuestions * 100).toInt()
    val passed = percentage >= 70

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (passed) "🎉 Congratulations!" else "📚 Keep Studying!",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Score",
                    fontSize = 20.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$score / $totalQuestions",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (passed) Color(0xFF48bb78) else Color(0xFFf56565)
                )

                Text(
                    text = "$percentage%",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                if (passed && gifUrl != null) {
                    Spacer(modifier = Modifier.height(24.dp))

                    AsyncImage(
                        model = gifUrl,
                        contentDescription = "Celebration",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF667eea)
            )
        ) {
            Text(
                text = "Try Another Quiz",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}