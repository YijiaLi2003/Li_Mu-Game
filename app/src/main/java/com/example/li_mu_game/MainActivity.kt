package com.example.li_mu_game

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HangmanGameTheme {
                HangmanGameApp()
            }
        }
    }

    @Composable
    fun HangmanGameTheme(content: @Composable () -> Unit) {
        MaterialTheme(
            colorScheme = lightColorScheme(),
            typography = Typography(),
            content = content
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HangmanGameApp() {
        val viewModel: HangmanViewModel = viewModel()
        val context = LocalContext.current

        val configuration = LocalConfiguration.current
        val orientation = configuration.orientation

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Hangman Game") },
                    actions = {
                        Button(onClick = {
                            viewModel.startNewGame()
                        }) {
                            Text(text = "New Game")
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // Landscape mode
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Panel 1: Letter buttons
                    PanelLetters(
                        guessedLetters = viewModel.guessedLetters,
                        availableLetters = viewModel.availableLetters,
                        disabledLetters = viewModel.disabledLetters,
                        onLetterSelected = viewModel::onLetterSelected,
                        modifier = Modifier.weight(1f)
                    )
                    // Panel 2: Hint Button
                    PanelHint(
                        hintClicks = viewModel.hintClicks,
                        onHintClicked = {
                            viewModel.onHintClicked(
                                onHintUnavailable = {
                                    Toast.makeText(context, "Hint not available", Toast.LENGTH_SHORT).show()
                                },
                                showHintMessage = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(100.dp)
                    )
                    // Panel 3: Main Game Play Screen
                    PanelGamePlay(
                        currentWord = viewModel.currentWord,
                        guessedLetters = viewModel.guessedLetters,
                        incorrectGuesses = viewModel.incorrectGuesses,
                        gameOver = viewModel.gameOver,
                        gameWon = viewModel.gameWon,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // Portrait mode
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Panel 3: Main Game Play Screen
                    PanelGamePlay(
                        currentWord = viewModel.currentWord,
                        guessedLetters = viewModel.guessedLetters,
                        incorrectGuesses = viewModel.incorrectGuesses,
                        gameOver = viewModel.gameOver,
                        gameWon = viewModel.gameWon,
                        modifier = Modifier.weight(1f)
                    )
                    // Panel 1: Letter buttons
                    PanelLetters(
                        guessedLetters = viewModel.guessedLetters,
                        availableLetters = viewModel.availableLetters,
                        disabledLetters = viewModel.disabledLetters,
                        onLetterSelected = viewModel::onLetterSelected,
                        modifier = Modifier.weight(1f)
                    )
                    // Hint panel for tablets
                    if (isTablet()) {
                        PanelHint(
                            hintClicks = viewModel.hintClicks,
                            onHintClicked = {
                                viewModel.onHintClicked(
                                    onHintUnavailable = {
                                        Toast.makeText(context, "Hint not available", Toast.LENGTH_SHORT).show()
                                    },
                                    showHintMessage = { message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            modifier = Modifier.wrapContentHeight()
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun PanelLetters(
        guessedLetters: Set<Char>,
        availableLetters: List<Char>,
        disabledLetters: Set<Char>,
        onLetterSelected: (Char) -> Unit,
        modifier: Modifier = Modifier
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(availableLetters) { letter ->
                Button(
                    onClick = { onLetterSelected(letter) },
                    enabled = !guessedLetters.contains(letter) && !disabledLetters.contains(letter),
                    modifier = Modifier
                        .padding(2.dp)
                        .size(40.dp)
                ) {
                    Text(text = "$letter")
                }
            }
        }
    }

    @Composable
    fun PanelHint(
        hintClicks: Int,
        onHintClicked: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onHintClicked) {
                Text(text = "Hint")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Hints used: $hintClicks")
        }
    }

    @Composable
    fun PanelGamePlay(
        currentWord: Word,
        guessedLetters: Set<Char>,
        incorrectGuesses: Int,
        gameOver: Boolean,
        gameWon: Boolean,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imageResource = when (incorrectGuesses) {
                0 -> R.drawable.hangman_1
                1 -> R.drawable.hangman_2
                2 -> R.drawable.hangman_3
                3 -> R.drawable.hangman_4
                4 -> R.drawable.hangman_5
                5 -> R.drawable.hangman_6
                6 -> R.drawable.hangman_7
                7 -> R.drawable.hangman_8
                8 -> R.drawable.hangman_9
                9 -> R.drawable.hangman_10
                else -> R.drawable.hangman_11
            }
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = "Hangman Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.wrapContentHeight(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (char in currentWord.text) {
                    val displayChar = if (guessedLetters.contains(char)) char else '_'
                    Text(
                        text = "$displayChar ",
                        fontSize = 24.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (gameOver) {
                if (gameWon) {
                    Text(text = "You Won!", color = Color.Green, fontSize = 24.sp)
                } else {
                    Text(text = "You Lost! Word was ${currentWord.text}", color = Color.Red, fontSize = 24.sp)
                }
            }
        }
    }

    @Composable
    fun isTablet(): Boolean {
        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp
        return screenWidthDp >= 600
    }
}
