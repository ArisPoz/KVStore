package com.arist

/**
 * @author arist
 */
data class Message(val type: Type, val message: String)

enum class Type {
    COMMAND,
    MESSAGE
}

enum class ReceivedMessageType(var msg: String) {
    MSG("MSG"),
    OK("OK"),
    ERROR("ERROR"),
    NOT_FOUND("NOT FOUND")
}