package com.kerjen.vuzixdemo.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.kerjen.vuzixdemo.R
import com.kerjen.vuzixdemo.model.Thing

class ThingsAdapter(val things: MutableList<Thing>) :
    RecyclerView.Adapter<ThingsAdapter.StatesHolder>() {

    var thingCheckedChanged: ((Thing) -> (Unit))? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatesHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_thing, parent, false) as CheckBox
        val holder = StatesHolder(view)
        view.setOnClickListener {
            things[holder.adapterPosition].state = view.isChecked
            thingCheckedChanged?.invoke(things[holder.adapterPosition])
        }
        return holder
    }

    override fun getItemCount() = things.size

    override fun onBindViewHolder(holder: StatesHolder, position: Int) {
        val thing = things[position]
        holder.thing.text = thing.text
        holder.thing.isChecked = thing.state
    }

    class StatesHolder(item: View) : RecyclerView.ViewHolder(item) {
        val thing: CheckBox = item.findViewById(R.id.thing)
    }
}