package com.arist

import java.io.File

/**
 * @author arist
 */

fun parse(fileName: String) =
    File(fileName)
        .useLines { it.toList() }
        .map {
            val (key, type) = it.split(" ")
            key to type
        }.toList()