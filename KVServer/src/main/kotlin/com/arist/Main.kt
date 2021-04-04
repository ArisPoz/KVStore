package com.arist

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.*

/**
 * @author arist
 */
fun main(args: Array<String>) {
    CommandLine(ArgRunner()).execute(*args)
}

@Command(
    name = "kvServer",
    version = ["kvServer v1"],
    mixinStandardHelpOptions = true,
    showDefaultValues = true
)
class ArgRunner : Runnable {

    @Option(
        names = ["-a"],
        required = true,
        description = ["The specified IP address of the server"]
    )
    private var serverIp: String? = null

    @Option(
        names = ["-p"],
        required = true,
        description = ["The specified Port of the server"]
    )
    private var serverPort: Int? = null

    private val log = LoggerFactory.getLogger("Main")

    override fun run() {
        log.info("IP Address: $serverIp")
        log.info("Server Port: $serverPort")

        val env = applicationEngineEnvironment {
            module { module(serverIp!!, serverPort!!) }
            connector {
                host = serverIp!!
                port = serverPort!!
            }
        }

        embeddedServer(Netty, env).start(wait = true)
    }
}