package com.akunne.aiassistant

import kotlin.math.sqrt
import kotlin.random.Random

class UtilityService {

    private val jokes = listOf(
        "Why did the scarecrow win an award? He was outstanding in his field!",
        "What do you call a fake noodle? An impasta!",
        "Why don't scientists trust atoms? Because they make up everything!",
        "What did one ocean say to the other? Nothing, they just waved.",
        "Why did the math book look sad? Because it had too many problems!",
        "What's orange and sounds like a parrot? A carrot!",
        "Why don't eggs tell jokes? They'd crack each other up!",
        "What do you call a bear with no teeth? A gummy bear!",
        "Why did the coffee file a police report? It got mugged!",
        "How does a penguin build its house? Igloos it together!"
    )

    private val funFacts = listOf(
        "Honey never spoils. Archaeologists have found pots of honey in ancient tombs that are over 3000 years old and still perfectly edible.",
        "A group of flamingos is called a 'flamboyance'.",
        "Bananas are berries, but strawberries are not.",
        "Octopuses have three hearts.",
        "The shortest war in history lasted only 38 to 45 minutes.",
        "Cleopatra lived closer to the invention of the iPhone than to the building of the Great Pyramid.",
        "A day on Venus is longer than a year on Venus.",
        "Wombats have cube-shaped poop.",
        "Scotland's national animal is a unicorn.",
        "Carrots were originally purple, not orange."
    )

    fun getRandomJoke(): String = jokes.random()

    fun getRandomFact(): String = funFacts.random()

    fun calculate(expression: String): String {
        return try {
            val result = eval(expression)
            String.format("%.2f", result)
        } catch (e: Exception) {
            "Invalid expression"
        }
    }

    private fun eval(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                return if (pos < expression.length) throw RuntimeException("Unexpected: " + ch.toChar()) else x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        ch == '+'.code -> { nextChar(); x += parseTerm() }
                        ch == '-'.code -> { nextChar(); x -= parseTerm() }
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        ch == '*'.code -> { nextChar(); x *= parseFactor() }
                        ch == '/'.code -> { nextChar(); x /= parseFactor() }
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                return when {
                    ch == '-'.code -> { nextChar(); -parseFactor() }
                    ch == '+'.code -> { nextChar(); parseFactor() }
                    ch == '('.code -> { nextChar(); val x = parseExpression(); nextChar(); x }
                    ch in '0'.code..'9'.code || ch == '.'.code -> parseNumber()
                    else -> throw RuntimeException("Unexpected: " + ch.toChar())
                }
            }

            fun parseNumber(): Double {
                val startPos = pos
                if (ch == '.'.code) nextChar() else while (ch in '0'.code..'9'.code) nextChar()
                if (ch == '.'.code) {
                    nextChar()
                    while (ch in '0'.code..'9'.code) nextChar()
                }
                return expression.substring(startPos, pos).toDouble()
            }

            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].code else 0
            }
        }.parse()
    }

    fun launchApp(appName: String): String {
        return "Launching $appName (requires accessibility service)"
    }
}
