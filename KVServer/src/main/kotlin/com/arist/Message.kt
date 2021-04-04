package com.arist

/**
 * @author arist
 */
data class Message(val from: String, val type: Type, val message: String)

enum class Type(var msg: String) {
    OK("OK"),
    ERROR("ERROR"),
    NOT_FOUND("NOT FOUND"),
    MSG("MESSAGE")
}