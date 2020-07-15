package com.kerjen.vuzixdemo.model.repository

import com.kerjen.vuzixdemo.network.WebSocketService
import javax.inject.Inject

class MainRepository @Inject constructor(val service: WebSocketService) {

    fun getThings(callback: () -> (Unit)) {
        //TODO: get things from the server
    }

    fun listenThingStateChanges(callback: () -> (Unit)) {
        //TODO: listen things changed
    }
}