package com.cliplay.networking

import com.cliplay.networking.services.RetrofitService
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.rx2.asCoroutineDispatcher


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

class CliplayNetworking {

    companion object {
        fun init() {
            RetrofitService.getInstance()
        }

        init {

        }

    }
}

data class AppCoroutineDispatchers(
        val database: CoroutineDispatcher,
        val disk: CoroutineDispatcher,
        val network: CoroutineDispatcher,
        val main: CoroutineDispatcher) {

    companion object {
        @Volatile
        private var INSTANCE: AppCoroutineDispatchers? = null

        fun getInstance(schedulers: AppRxSchedulers): AppCoroutineDispatchers = INSTANCE
                ?: synchronized(this) {
                    INSTANCE ?: provideDispatchers(schedulers).also { INSTANCE = it }
                }
    }
}

fun provideDispatchers(schedulers: AppRxSchedulers) =
        AppCoroutineDispatchers(
                database = schedulers.background().asCoroutineDispatcher(),
                disk = schedulers.io().asCoroutineDispatcher(),
                network = schedulers.network().asCoroutineDispatcher(),
                main = UI
        )


