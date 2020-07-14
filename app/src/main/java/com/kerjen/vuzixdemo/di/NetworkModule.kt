package com.kerjen.vuzixdemo.di

import com.kerjen.vuzixdemo.network.WebSocketService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideWebSocketService(): WebSocketService = WebSocketService()
}