package com.arist

import okhttp3.*
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author arist
 */
class WSListener : WebSocketListener() {

    private val log = LoggerFactory.getLogger("Websocket")
    private val servers = mutableSetOf<WebSocket?>()
    private val serverMap = mutableMapOf<WebSocket?, String>()
    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build()
    var responses = mutableSetOf<String>()

    fun getAvailableServers(): MutableMap<WebSocket?, String> =
        serverMap

    fun getConnectedServers(): MutableSet<WebSocket?> =
        servers;

    fun hasUnavailableServers(): Boolean =
        serverMap.size != servers.size

    fun getRandomServer(): WebSocket? =
        servers.random()

    fun sendToAll(message: String) =
        servers.forEach { it?.send(message) }

    fun instantiateServers(connectionFile: String) {
        val lines: List<String> = File(connectionFile).readLines()

        lines.forEach {
            val ipAndPort = it.split(" ")
            val ip = ipAndPort[0]
            val port = ipAndPort[1]
            val url = "http://$ip:$port/socket"
            val request = Request.Builder()
                .url(url)
                .build()

            serverMap[client.newWebSocket(request, this)] = url
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocket.send(JSONObject(Message(Type.MESSAGE, "Broker connected...")).toString())
        servers.add(webSocket)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        servers.remove(webSocket)
        webSocket.close(1000, "terminated")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val jsonObj = JSONObject(text)
        val from = jsonObj.getString("from")
        val msg = jsonObj.getString("message")

        when (jsonObj.getString("type")) {
            ReceivedMessageType.MSG.msg -> {
                log.info("$from $msg")
            }
            ReceivedMessageType.ERROR.msg -> {
                log.error("$from $msg")
            }
            ReceivedMessageType.OK.msg -> {
                if (responses.add(from))
                    log.info("$from $msg")
            }
            else -> {
                log.info("$from $msg")
            }
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        log.error("${t.message}. Reconnection attempt in 30s")
        servers.remove(webSocket)
        Thread.sleep(30_000)
        val request = Request.Builder().url(serverMap[webSocket]!!).build()
        val newWebSocket = client.newWebSocket(request, this)
        serverMap[newWebSocket] = serverMap.remove(webSocket)!!
    }
}