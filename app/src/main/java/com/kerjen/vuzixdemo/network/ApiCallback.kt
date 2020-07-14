package com.kerjen.vuzixdemo.network

data class ApiCallback<T>(
    val observableEmitter: (T)->(Unit),
    val dataClass: Class<T>
)