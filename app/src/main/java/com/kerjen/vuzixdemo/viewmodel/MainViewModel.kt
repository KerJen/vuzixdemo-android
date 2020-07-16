package com.kerjen.vuzixdemo.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kerjen.vuzixdemo.model.Thing
import com.kerjen.vuzixdemo.model.repository.MainRepository

class MainViewModel @ViewModelInject constructor(private val repo: MainRepository) : ViewModel() {

    private val thingsLiveData = MutableLiveData<MutableList<Thing>>()
    fun getThingsLiveData(): LiveData<MutableList<Thing>> = thingsLiveData

    private val thingChangedLiveData = MutableLiveData<Thing>()
    fun getThingChangedLiveData(): LiveData<Thing> = thingChangedLiveData

    private val thingAddedLiveData = MutableLiveData<Thing>()
    fun getThingAddedLiveData(): LiveData<Thing> = thingAddedLiveData

    private val thingRemovedLiveData = MutableLiveData<String>()
    fun getThingRemovedLiveData(): LiveData<String> = thingRemovedLiveData

    fun getThings() {
        repo.getThings {
            thingsLiveData.postValue(it.things)
        }
    }

    fun changeThingState(thing: Thing) {
        repo.changeThingState(thing) {
            //TODO: confirm changing
        }
    }

    fun listenThingStateChanged() {
        repo.listenThingStateChanges {
            thingChangedLiveData.postValue(it.thing)
        }
    }

    fun listenThingAdded() {
        repo.listenThingAdded {
            thingAddedLiveData.postValue(it.thing)
        }
    }

    fun listenThingRemoved() {
        repo.listenThingRemoved {
            thingRemovedLiveData.postValue(it.id)
        }
    }
}