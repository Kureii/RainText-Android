package cz.kureii.raintext.utils

import java.security.SecureRandom

class PasswordGenerator(
    private val length: Int,
    private val useSpecialChars: Boolean,
    private val specialChars: String
) {
    private val random = SecureRandom()
    private val lowerCaseChars = "abcdefghijklmnopqrstuvwxyz"
    private val upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val numbers = "0123456789"

    fun generate(): String {
        val result = StringBuilder()

        result.append(lowerCaseChars[random.nextInt(lowerCaseChars.length)])
        result.append(upperCaseChars[random.nextInt(upperCaseChars.length)])
        result.append(numbers[random.nextInt(numbers.length)])

        if (useSpecialChars) {
            result.append(specialChars[random.nextInt(specialChars.length)])
        }

        val allChars = StringBuilder().apply {
            append(lowerCaseChars)
            append(upperCaseChars)
            append(numbers)
            if (useSpecialChars) {
                append(specialChars)
            }
        }.toString()

        for (i in 1..(length - result.length)) {
            result.append(allChars[random.nextInt(allChars.length)])
        }

        val shuffledResult = result.toString().toList().shuffled(random).joinToString("")

        var finalResult = shuffledResult
        if (shuffledResult.first().isUpperCase()) {
            finalResult = lowerCaseChars[random.nextInt(lowerCaseChars.length)] + shuffledResult.drop(1)
        }

        if (shuffledResult.takeLast(4).all { it.isDigit() }) {
            finalResult = finalResult.dropLast(1) + lowerCaseChars[random.nextInt(lowerCaseChars.length)]
        }

        return finalResult
    }
}

