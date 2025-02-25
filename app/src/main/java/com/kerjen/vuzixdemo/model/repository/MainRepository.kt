package com.kerjen.vuzixdemo.model.repository

import com.kerjen.vuzixdemo.model.Thing
import com.kerjen.vuzixdemo.model.dto.*
import com.kerjen.vuzixdemo.network.WebSocketService
import javax.inject.Inject

class MainRepository @Inject constructor(val service: WebSocketService) {

    fun getThings(callback: (ThingsGetResponseDTO) -> (Unit)) {
        service.once("things.get", ThingsGetResponseDTO::class.java, callback)
        service.request("things.get")
    }

    fun changeThingState(thing: Thing, callback: (ThingChangeResponseDTO) -> (Unit)) {
        service.request("things.change", thing)
        service.once("things.change", ThingChangeResponseDTO::class.java, callback)
    }

    fun listenThingStateChanges(callback: (ThingChangedUpdateDTO) -> (Unit)) {
        service.on("updates.thing.changed", ThingChangedUpdateDTO::class.java, callback)
    }

    fun listenThingAdded(callback: (ThingAddedUpdateDTO) -> (Unit)) {
        service.on("updates.thing.added", ThingAddedUpdateDTO::class.java, callback)
    }

    fun listenThingRemoved(callback: (ThingRemovedUpdateDTO) -> (Unit)) {
        service.on("updates.thing.removed", ThingRemovedUpdateDTO::class.java, callback)
    }
}