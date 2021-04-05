package com.arist

import kotlinx.coroutines.*
import okhttp3.WebSocket
import org.json.JSONObject
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.*
import java.io.File
import java.lang.Runnable
import kotlin.system.exitProcess

/**
 * @author arist
 */
fun main(args: Array<String>) {
    CommandLine(Runner()).execute(*args)
}

@Command(
    name = "kvBroker",
    version = ["kvBroker v1"],
    mixinStandardHelpOptions = true,
    showDefaultValues = true
)
class Runner : Runnable {

    private val log = LoggerFactory.getLogger("Broker")

    @Option(
        names = ["-i"], required = true,
        description = ["File containing available server IPs"]
    )
    private var dataToIndex: String = String()

    @Option(
        names = ["-k"], required = true,
        description = ["Replication factor. The # of server having identical data"]
    )
    private var replicationFactor: Int = 0

    @Option(
        names = ["-s"], required = true,
        description = ["File containing generated data"]
    )
    private var serverFile: String = String()

    override fun run() {
        log.info("Replication factor: $replicationFactor")
        log.info("Data to index file: $dataToIndex")
        log.info("Available server IPs file: $serverFile")

        val ws = WSListener()
        ws.instantiateServers(serverFile);

        if (ws.getAvailableServers().size < replicationFactor) {
            log.error("Not enough available servers for replication...")
            exitProcess(1)
        }

        val waitingForServersJob = GlobalScope.launch {
            log.info("Waiting for servers...")
            while (true) {
                delay(2000L)
                if (ws.getConnectedServers().size >= replicationFactor) break
            }
        }

        runBlocking {
            waitingForServersJob.join()
        }

        transmit(ws)

        while (true) {
            val input = readLine().toString()
            if (input.toLowerCase().contains("delete") && ws.hasUnavailableServers()) {
                log.error("Cannot perform delete, there are some disconnected servers")
            } else {
                if (input.isNotEmpty()) {
                    ws.sendToAll(JSONObject(Message(Type.COMMAND, input)).toString())
                    ws.responses = mutableSetOf()
                }
            }
        }
    }

    private fun transmit(ws: WSListener) {
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
}