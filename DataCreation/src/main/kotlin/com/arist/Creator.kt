package com.arist

import picocli.CommandLine.*
import java.io.File

/**
 * @author arist
 */
@Command(
    name = "createData",
    version = ["createData v0.1a"],
    mixinStandardHelpOptions = true,
    showDefaultValues = true
)
class Creator : Runnable {
    @Option(names = ["-d"], description = ["Maximum nesting level, '0' for no nesting"])
    private var maxNestingLevel = 3

    @Option(names = ["-k"], required = true,
        description = ["File containing a space-separated list of key names and their data types"])
    private lateinit var keyFile: String

    @Option(names = ["-l"], description = ["Maximum length of a string value"])
    private var maxStringLength = 4

    @Option(names = ["-m"], description = ["Maximum number of keys inside each value."])
    private var maxKeysNum = 5

    @Option(names = ["-n"], description = ["Number of lines that we would like to generate"])
    private var numLines = 1000

    override fun run() {
        val types = parse(keyFile)
        val list = ArrayList<String>()

        println("Random data creation... starts!")
        (1..numLines).forEach {
            val values = generate(types, maxStringLength)
            list.add(generate(it, maxKeysNum, maxNestingLevel, values))
        }

        File("dataToIndex.txt").bufferedWriter().use { out ->
            list.forEach {
                out.write("$it\n")
            }
        }
        println("Random data creation... ends!")
        println("File dataToIndex.txt has been created.")
    }
}