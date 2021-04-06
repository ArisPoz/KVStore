package com.arist

import org.json.JSONObject
import org.slf4j.LoggerFactory

/**
 * @author arist
 */
private val log = LoggerFactory.getLogger("Service")

fun executeCommand(trie: Trie, message: String): Pair<Type, String> {
    try {
        val splitMessage = message.split(" ", limit = 2)
        val command = splitMessage[0]
        val data = splitMessage[1]

        return when(command.toUpperCase()) {
            "GET" -> {
                val pair = trie.get(data)
                Pair(pair.first, pair.second)
            }
            "QUERY" -> {
                val pair = trie.query(data)
                Pair(pair.first, pair.second)
            }
            "DELETE" -> {
                val pair = trie.delete(data)
                Pair(pair.first, pair.second)
            }
            "PUT" -> {
                val split = data.split(":", limit = 2)
                val key = split[0].replace("\"", "").trim()
                val value = split[1].replace(";", ",").trim()
                calculateKeyValues(trie, key, JSONObject(value))
                Pair(Type.OK, Type.OK.msg)
            } else ->
                Pair(Type.ERROR, Type.ERROR.msg)

        }
    } catch (e: Exception) {
        log.error(e.message)
    }

    return Pair(Type.ERROR, Type.ERROR.msg)
}

private fun calculateKeyValues(trie: Trie, superKey: String, jsonObj: JSONObject) {
    trie.put(superKey)
    jsonObj.keySet().forEach {
        val currentKey = "$superKey-$it"
        trie.put(currentKey)
        when (jsonObj.get(it)) {
            is JSONObject -> calculateKeyValues(trie, currentKey, jsonObj.getJSONObject(it))
            is String -> trie.put("$currentKey=${jsonObj.getString(it)}")
            is Number -> trie.put("$currentKey=${jsonObj.getNumber(it)}")
        }
    }
}