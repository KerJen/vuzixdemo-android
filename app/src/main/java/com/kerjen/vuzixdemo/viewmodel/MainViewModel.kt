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

    fun getThings() {
        repo.getThings {
            //TODO: get
        }
    }

    fun listenThingStateChanged() {
        repo.listenThingStateChanges {
            //TODO: listen
        }
    }
}