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

        //TODO: replace the notify logic to the adapter

        viewModel.getThingChangedLiveData().observe(this) {
            val position = adapter.things.indexOfFirst { thing -> it.id == thing.id }
            adapter.things[position].apply {
                state = it.state
                text = it.text
            }
            adapter.notifyItemChanged(position)
        }

        viewModel.getThingRemovedLiveData().observe(this) { id ->
            val position = adapter.things.indexOfFirst { thing -> id == thing.id }
            adapter.things.removeAt(position)
            adapter.notifyDataSetChanged()
            if (adapter.things.isEmpty()) {
                thingsListVisible(false)
            }
        }

        viewModel.getThingAddedLiveData().observe(this) {
            if (adapter.things.isEmpty()) {
                thingsListVisible(true)
            }
            adapter.things.add(it)
            adapter.notifyDataSetChanged()
            thingsList.scrollToPosition(adapter.itemCount - 1)
        }

        viewModel.getThingsLiveData().observe(this) {
            setUpThingsList(it)
            with(viewModel) {
                listenThingStateChanged()
                listenThingAdded()
                listenThingRemoved()
            }
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
        thingsList.layoutManager = LinearLayoutManager(this)
        adapter = ThingsAdapter(things)
        thingsList.adapter = adapter
        if (things.isEmpty()) {
            thingsListVisible(false)
        } else {
            thingsListVisible(true)
            adapter.thingCheckedChanged = {
                viewModel.changeThingState(it)
            }
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