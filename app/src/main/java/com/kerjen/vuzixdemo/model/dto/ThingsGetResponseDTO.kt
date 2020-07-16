package com.kerjen.vuzixdemo.model.dto

import com.kerjen.vuzixdemo.model.Thing

data class ThingsGetResponseDTO(val result: Int, val things: MutableList<Thing>)