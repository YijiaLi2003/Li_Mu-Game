package com.example.li_mu_game

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import kotlin.random.Random

data class Word(val text: String, val category: String)

class HangmanViewModel : ViewModel() {

    private val words = listOf(
        Word("ANDROID", "Technology"),
        Word("COMPOSE", "Technology"),
        Word("KOTLIN", "Programming Language"),
        Word("HANGMAN", "Game"),
        Word("DEVELOPER", "Profession"),
        Word("VARIABLE", "Programming Term"),
        Word("FUNCTION", "Programming Term"),
        Word("LAYOUT", "UI Design"),
        Word("BUTTON", "UI Element"),
        Word("DATABASE", "Technology")
    )

    var currentWord by mutableStateOf(getRandomWord())
        private set

    var guessedLetters by mutableStateOf(setOf<Char>())
        private set

    var incorrectGuesses by mutableIntStateOf(0)
        private set

    private val maxIncorrectGuesses = 10

    var gameOver by mutableStateOf(false)
        private set

    var gameWon by mutableStateOf(false)
        private set

    var availableLetters by mutableStateOf(('A'..'Z').toList())
        private set

    var hintClicks by mutableIntStateOf(0)
        private set

    var disabledLetters by mutableStateOf(setOf<Char>())
        private set

    private var vowelsRevealed by mutableStateOf(false)

    fun onLetterSelected(letter: Char) {
        if (!gameOver) {
            guessedLetters = guessedLetters + letter
            availableLetters = availableLetters - letter
            if (currentWord.text.contains(letter)) {
                if ((currentWord.text.toSet() - guessedLetters).isEmpty()) {
                    gameWon = true
                    gameOver = true
                }
            } else {
                incorrectGuesses++
                if (incorrectGuesses >= maxIncorrectGuesses) {
                    gameOver = true
                }
            }
        }
    }

    fun onHintClicked(onHintUnavailable: () -> Unit, showHintMessage: (String) -> Unit) {
        if (gameOver) {
            onHintUnavailable()
        } else {
            if (incorrectGuesses >= maxIncorrectGuesses - 1) {
                onHintUnavailable()
            } else {
                when (hintClicks) {
                    0 -> {
                        // First click, display the category
                        showHintMessage("Hint: Category is ${currentWord.category}.")
                        hintClicks++
                    }
                    1 -> {
                        // Second click, disable half of the remaining letters (not in word)
                        val remainingLetters = availableLetters - currentWord.text.toSet()
                        val lettersToDisable = remainingLetters.shuffled().take(remainingLetters.size / 2)
                        disabledLetters = disabledLetters + lettersToDisable
                        incorrectGuesses++
                        if (incorrectGuesses >= maxIncorrectGuesses) {
                            gameOver = true
                        }
                        hintClicks++
                    }
                    2 -> {
                        // Third click, show all vowels
                        val vowels = setOf('A', 'E', 'I', 'O', 'U')
                        val vowelsInWord = currentWord.text.toSet().intersect(vowels)
                        guessedLetters = guessedLetters + vowelsInWord
                        availableLetters = availableLetters - vowels
                        vowelsRevealed = true
                        incorrectGuesses++
                        if (incorrectGuesses >= maxIncorrectGuesses) {
                            gameOver = true
                        }
                        hintClicks++
                    }
                    else -> {
                        onHintUnavailable()
                    }
                }
            }
        }
    }

    fun startNewGame() {
        currentWord = getRandomWord()
        guessedLetters = setOf()
        incorrectGuesses = 0
        gameOver = false
        gameWon = false
        availableLetters = ('A'..'Z').toList()
        hintClicks = 0
        disabledLetters = setOf()
        vowelsRevealed = false
    }

    private fun getRandomWord(): Word {
        return words[Random.nextInt(words.size)]
    }
}
