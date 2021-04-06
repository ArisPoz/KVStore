package com.arist

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineExceptionHandler
import org.json.JSONObject
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

/**
 * @author arist
 */
private val trie = Trie()
private val log = LoggerFactory.getLogger("Server")

suspend fun DefaultWebSocketServerSession.outputMessages(ip: String, port: Int) {

    send(
        Frame.Text(
            JSONObject(
                Message(
                    from = "$ip:$port",
                    type = Type.MSG,
                    message = "Connection established!"
                )
            ).toString()
        )
    )

    while (true) {
        val message = incoming.receive()
        if (message is Frame.Text) {
            val jsonObj = JSONObject(message.readText())
            val incomingType = jsonObj.getString("type")
            val incomingMessage = jsonObj.getString("message")

            if (incomingType == "MESSAGE") {
                log.info("MESSAGE: $incomingMessage")
            } else if (incomingType == "COMMAND") {
                log.info("COMMAND: $incomingMessage")
                val pair = executeCommand(trie, incomingMessage)

                send(
                    Frame.Text(
                        JSONObject(
                            Message(
                                from = "$ip:$port",
                                type = pair.first,
                                message = pair.second
                            )
                        ).toString()
                    )
                )
            }
        }
    }
}

fun exceptionHandler(): CoroutineContext {
    return CoroutineExceptionHandler { _, e ->
        log.error("${e.message}")
    }
}