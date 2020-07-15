package com.kerjen.vuzixdemo.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.kerjen.vuzixdemo.R
import com.kerjen.vuzixdemo.model.Thing
import com.kerjen.vuzixdemo.network.WebSocketService
import com.kerjen.vuzixdemo.network.WebSocketState
import com.kerjen.vuzixdemo.view.adapters.ThingsAdapter
import com.kerjen.vuzixdemo.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var webSocketService: WebSocketService

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getThingsLiveData().observe(this) {
            setUpThingsList(it)
        }

        webSocketService.listener.webSocketStateCallback.add {
            when (it) {
                WebSocketState.CONNECTED -> {
                    viewModel.getThings()
                }
                else -> {
                    //Toast.makeText(this, getString(R.string.lost_internet_connection), Toast.LENGTH_LONG).show()
                }
            }
        }

        //webSocketService.connect()
    }

    private fun setUpThingsList(things: MutableList<Thing>) {
        thingsList.layoutManager = LinearLayoutManager(this)
        thingsList.adapter = ThingsAdapter(things)
    }

    private fun getFakeThings() =
        mutableListOf(
            Thing("", true, "Открыть дверь"),
            Thing("", false, "Достать коробку №1")
        )
}