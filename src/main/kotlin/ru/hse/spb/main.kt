package ru.hse.spb


private const val MATCH_FAILED: Char = '.'
private const val QUESTION_MARK: Char = '?'

private fun matchNonQuestionMark(x: Char, y: Char): Char =
    when {
        x == QUESTION_MARK -> y
        y == QUESTION_MARK -> x
        x == y -> x
        else -> MATCH_FAILED
    }

private fun fillQuestionMarksFromTheOtherPalindromeSide(str: List<Char>): List<Char> {
    return str.zip(str.reversed(), ::matchNonQuestionMark)
}

private fun fillQuestionMarksFromQueue(str: List<Char>,
                                       queue: MutableList<Char>,
                                       default: Char = 'a'): List<Char> {
    val result = str.toMutableList()
    val half = (result.size + 1) / 2 - 1

    for (idx in half downTo 0) {
        if (result[idx] == QUESTION_MARK)
            result[idx] = if (queue.isEmpty()) default else queue.removeAt(0)
    }

    return result
}

fun getTitle(lettersCount: Int, pattern: String): String? {
    val match = fillQuestionMarksFromTheOtherPalindromeSide(pattern.toList())

    val mustBeUsed = ('a'..'z').take(lettersCount)
    val leftToUse = (mustBeUsed - match).sortedDescending().toMutableList()
    val bestMatch = fillQuestionMarksFromQueue(match, leftToUse, default = 'a')

    val notAllWereUsed = leftToUse.isNotEmpty()
    val notAllMatched = MATCH_FAILED in bestMatch

    return if (notAllMatched || notAllWereUsed) null else
        fillQuestionMarksFromTheOtherPalindromeSide(bestMatch).joinToString("")
}

fun main(args: Array<String>) {
    val numOfLetters = readLine()?.toIntOrNull() ?: return
    val pattern = readLine() ?: return

    println(getTitle(numOfLetters, pattern) ?: "IMPOSSIBLE")
}
