package com.arist

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * @author arist
 */
fun Application.module(ip: String, port: Int) {
    install(WebSockets)

    routing {
        webSocket("/socket") {
            val job = GlobalScope.launch(exceptionHandler()) {
                val outMessages = launch { outputMessages(ip, port) }
                outMessages.join()
            }
            job.join()
        }
    }
}