package com.arist

import picocli.CommandLine

fun main(args: Array<String>) {
   CommandLine(Creator()).execute(*args)
}


