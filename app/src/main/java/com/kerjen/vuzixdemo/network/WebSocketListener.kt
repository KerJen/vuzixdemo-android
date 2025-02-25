package com.kerjen.vuzixdemo.network

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketListener : WebSocketListener() {

    var webSocketStateCallback = ArrayList<((WebSocketState) -> (Unit))?>()
    var webSocketMessageCallback: ((ByteArray) -> (Unit))? = null

    override fun onOpen(webSocket: WebSocket, response: Response) {
        webSocketStateCallback.forEach {
            it?.invoke(WebSocketState.CONNECTED)
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        webSocketStateCallback.forEach {
            it?.invoke(WebSocketState.CLOSED)
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        webSocketStateCallback.forEach {
            it?.invoke(WebSocketState.FAILURE)
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        webSocketMessageCallback?.invoke(bytes.toByteArray())
    }
}