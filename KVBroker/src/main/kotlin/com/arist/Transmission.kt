package com.arist

import okhttp3.WebSocket
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File

/**
 * @author arist
 */

private val log = LoggerFactory.getLogger("Transmission")
fun transmit(ws: WSListener, dataToIndex: String, replicationFactor: Int) {
    log.info("transmitting...")

    ws.sendToAll(JSONObject(Message(Type.MESSAGE, "Broker transmitting data...")).toString())

    val randomServers = mutableListOf<WebSocket?>()
    File(dataToIndex).readLines().forEach { line ->
        while (true) {
            val randomServer = ws.getRandomServer()
            if (!randomServers.contains(randomServer)) {
                randomServers.add(randomServer)
                if (randomServers.size == replicationFactor) break
            }
        }

        randomServers.forEach { server ->
            server?.send(JSONObject(Message(Type.COMMAND, "${Commands.PUT} $line")).toString())
        }

        randomServers.removeAll(randomServers)
    }

    ws.sendToAll(JSONObject(Message(Type.MESSAGE, "Data transmission completed...")).toString())
    log.info("Servers ready. Commands: [${Commands.values().joinToString { it.name }}]")
}

fun transmitCommand(input: String, ws: WSListener) {
    if (input.toLowerCase().contains("delete") && ws.hasUnavailableServers()) {
        log.error("Cannot perform delete, there are some disconnected servers")
    } else {
        if (input.isNotEmpty()) {
            ws.sendToAll(JSONObject(Message(Type.COMMAND, input)).toString())
            ws.responses = mutableSetOf()
        }
    }
}