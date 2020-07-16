package com.kerjen.vuzixdemo.view

import android.os.Bundle
import android.view.View
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

    private lateinit var adapter: ThingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getThingChangedLiveData().observe(this) {
            val index = adapter.things.indexOfFirst { thing -> it.id == thing.id }
            adapter.things[index].state = it.state
            adapter.notifyItemChanged(index)
        }

        viewModel.getThingAddedLiveData().observe(this) {
            adapter.things.add(it)
            adapter.notifyItemInserted(adapter.itemCount - 1)
        }

        viewModel.getThingsLiveData().observe(this) {
            setUpThingsList(it)
            viewModel.listenThingStateChanged()
            viewModel.listenThingAdded()
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

        webSocketService.connect()
    }

    private fun setUpThingsList(things: MutableList<Thing>) {
        if (things.isEmpty()) {
            thingsListVisible(false)
        } else {
            thingsListVisible(true)
            thingsList.layoutManager = LinearLayoutManager(this)
            adapter = ThingsAdapter(things)
            adapter.thingCheckedChanged = {
                viewModel.changeThingState(it)
            }
            thingsList.adapter = adapter
        }
    }

    private fun thingsListVisible(visible: Boolean) {
        if (visible) {
            thingsList.visibility = View.VISIBLE
            thingsNoText.visibility = View.GONE

        } else {
            thingsList.visibility = View.GONE
            thingsNoText.visibility = View.VISIBLE
        }
    }

    private fun getFakeThings() =
        mutableListOf(
            Thing("", true, "Открыть дверь"),
            Thing("", false, "Достать коробку №1")
        )
}