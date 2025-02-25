package com.kerjen.vuzixdemo.network

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kerjen.vuzixdemo.model.dto.EmptyDTO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okio.ByteString.Companion.toByteString
import org.msgpack.jackson.dataformat.MessagePackFactory

class WebSocketService {
    val listener = WebSocketListener()
    private val okHttpClient = OkHttpClient()
    private lateinit var request: Request
    private var webSocketClient: WebSocket? = null
    private val listenersPool = HashMap<String, ApiCallback<*>>()
    private val objectMapper = ObjectMapper(MessagePackFactory())
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
        .registerKotlinModule()

    //TODO: добавить удаление предыдущих connectCallbacks
    fun connect(ip: String, port: String) {
        val connectCallbackIndex = listener.webSocketStateCallback.size
        listener.webSocketStateCallback.add {
            if (it == WebSocketState.CONNECTED) {
                listenResponses()
                listenForDisconnect()
            }
            listener.webSocketStateCallback.removeAt(connectCallbackIndex)
        }

        request = Request.Builder().url("ws://$ip:$port").build()
        webSocketClient = okHttpClient.newWebSocket(request, listener)
    }

    private fun listenResponses() {
        listener.webSocketMessageCallback = {
            try {
                val methodName = objectMapper.readValue<String>(it)
                val methodBytesLength = methodName.toByteArray().size

                @Suppress("UNCHECKED_CAST")
                val emitter = listenersPool[methodName]?.observableEmitter as (Any) -> (Unit)
                val dataClass = listenersPool[methodName]?.dataClass

                val tree = objectMapper.readTree(it.copyOfRange(methodBytesLength + 1, it.size))
                val response = objectMapper.treeToValue(tree, dataClass)

                emitter.invoke(response!!)
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun listenForDisconnect() {
        listener.webSocketStateCallback.add {
            if (it == WebSocketState.FAILURE) reconnect()
        }
    }

    private fun reconnect() {
        listenerPoolOnceClean()
        GlobalScope.launch {
            delay(5000)
            webSocketClient = okHttpClient.newWebSocket(request, listener)
        }
    }

    private fun listenerPoolOnceClean() {
        val iterator = listenersPool.iterator()
        while (iterator.hasNext()) {
            val current = iterator.next()
            if (current.value.once) {
                iterator.remove()
            }
        }
    }

    fun <T> request(method: String, data: T) {
        val serializedMethodName = objectMapper.writeValueAsBytes(method)
        val serializedData = objectMapper.writeValueAsBytes(data)
        val serialized = serializedMethodName + serializedData

        webSocketClient?.send(serialized.toByteString())
    }

    fun request(method: String) {
        val serializedMethodName = objectMapper.writeValueAsBytes(method)
        val serializedData = objectMapper.writeValueAsBytes(EmptyDTO())
        val serialized = serializedMethodName + serializedData

        webSocketClient?.send(serialized.toByteString())
    }

    fun <T> on(method: String, dataClass: Class<T>, callback: (T) -> Unit) {
        listenersPool[method] = ApiCallback(false, callback, dataClass)
    }

    fun <T> once(method: String, dataClass: Class<T>, callback: (T) -> Unit) {
        listenersPool[method] = ApiCallback(true, {
            listenersPool.remove(method)
            callback(it)
        }, dataClass)
    }
}