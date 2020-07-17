package com.kerjen.vuzixdemo.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.kerjen.vuzixdemo.R
import com.kerjen.vuzixdemo.model.Thing
import com.kerjen.vuzixdemo.network.WebSocketService
import com.kerjen.vuzixdemo.network.WebSocketState
import com.kerjen.vuzixdemo.view.adapters.ThingsAdapter
import com.kerjen.vuzixdemo.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_things.*
import javax.inject.Inject

@AndroidEntryPoint
class ThingsFragment : Fragment(R.layout.fragment_things) {

    @Inject
    lateinit var webSocketService: WebSocketService

    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: ThingsAdapter

    private val callback: (WebSocketState) -> (Unit) = {
        when (it) {
            WebSocketState.CONNECTED -> viewModel.getThings()
            WebSocketState.CLOSED -> {} //TODO:
            WebSocketState.FAILURE -> {} //TODO:
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getThings()
        webSocketService.listener.webSocketStateCallback.add(callback)

        viewModel.getThingChangedLiveData().observe(viewLifecycleOwner) {
            val position = adapter.things.indexOfFirst { thing -> it.id == thing.id }
            adapter.things[position].apply {
                state = it.state
                text = it.text
            }
            adapter.notifyItemChanged(position)
        }

        viewModel.getThingRemovedLiveData().observe(viewLifecycleOwner) { id ->
            val position = adapter.things.indexOfFirst { thing -> id == thing.id }
            adapter.things.removeAt(position)
            adapter.notifyDataSetChanged()
            if (adapter.things.isEmpty()) {
                thingsListVisible(false)
            }
        }

        viewModel.getThingAddedLiveData().observe(viewLifecycleOwner) {
            if (adapter.things.isEmpty()) {
                thingsListVisible(true)
            }
            adapter.things.add(it)
            adapter.notifyDataSetChanged()
            thingsList.scrollToPosition(adapter.itemCount - 1)
        }

        viewModel.getThingsLiveData().observe(viewLifecycleOwner) {
            setUpThingsList(it)
            with(viewModel) {
                listenThingStateChanged()
                listenThingAdded()
                listenThingRemoved()
            }
        }
    }

    private fun setUpThingsList(things: MutableList<Thing>) {
        thingsList.layoutManager = LinearLayoutManager(requireActivity())
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

    override fun onDestroy() {
        webSocketService.listener.webSocketStateCallback.remove(callback)
        super.onDestroy()
    }
}