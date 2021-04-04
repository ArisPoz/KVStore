package com.arist

import java.lang.Integer.min

/**
 * @author arist
 */

fun generate(lineNum: Int, maxKeysNum: Int, maxNestingLevel: Int, values: List<Pair<String, Any>>): String {
    val outputBuilder = StringBuilder()
    outputBuilder.append("${"key$lineNum".toStringWithQuotes()} : { ")
    outputBuilder.append(generateComplexObject(maxKeysNum, maxNestingLevel, values))
    return outputBuilder.append(" }").toString()
}

fun generateComplexObject(maxKeysNum: Int, maxNestingLevel: Int, values: List<Pair<String, Any>>): String {
    val outputBuilder = StringBuilder()
    val keySet = HashSet<String>()
    val randomKeyNum = min((1 until maxKeysNum).random(), values.size)

    (0 until randomKeyNum).forEach {
        val randomKey = (0..randomKeyNum).random()
        if (keySet.add(values[randomKey].first)) {
            if (it % 2 == 0 && maxNestingLevel > 0)
                outputBuilder.append(generateSingleObject(values[randomKey].first, maxNestingLevel, values)).append(" ; ")
            else
                outputBuilder.append(generateSingePair(values[randomKey].first, values[randomKey].second)).append(" ; ")
        }
    }

    return outputBuilder.toString().dropLast(3)
}

fun generateSingleObject(key: String, maxNestingLevel: Int, values: List<Pair<String, Any>>): String {
    val outputBuilder = StringBuilder()
    val randomNesting = if (maxNestingLevel > 0) (1..maxNestingLevel).random() else 0
    val min = min(randomNesting, values.size)
    outputBuilder.append("${key.toStringWithQuotes()} : { ")

    if (randomNesting == 0) {
        outputBuilder.append(generateSingePair(values[randomNesting].first, values[randomNesting].second)).append(" ; ")
        return outputBuilder.toString().dropLast(3) + " } "
    }

    (randomNesting..min).forEach {
        if (it % 2 == 0) {
            outputBuilder.append(generateSingleObject(values[it].first, randomNesting - 1, values)).append(" ; ")
        } else {
            outputBuilder.append(generateSingePair(values[it].first, values[it].second)).append(" ; ")

        }
    }

    return outputBuilder.toString().dropLast(3) + " } "
}

fun generateSingePair(key: String, value: Any) =
    if (value is String)
        "${key.toStringWithQuotes()} : ${value.toStringWithQuotes()}"
    else
        "${key.toStringWithQuotes()} : $value"