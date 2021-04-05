package com.arist

import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils

/**
 * @author arist
 */

fun generate(values: List<Pair<String, String>>, maxStringLength: Int) =
    values.map {
        it.first to generateRandomType(it.second, maxStringLength)
    }

private fun generateRandomType(type: String, maxStringLength: Int) =
    when (DataTypes.valueOf(type.toUpperCase())) {
        DataTypes.INT -> generateRandomNumber()
        DataTypes.STRING -> generateRandomString(maxStringLength)
        DataTypes.FLOAT -> ("%.2f".format(generateRandomNumber() / 3.3)).toFloat()
    }

private fun generateRandomString(maxStringLength: Int): String {
    val randomLength = (1..maxStringLength).random()
    val randomAlphanumeric = RandomStringUtils.randomAlphanumeric(randomLength)
    return StringUtils.capitalize(randomAlphanumeric)
}

private fun generateRandomNumber() =
    (1..1000).random()