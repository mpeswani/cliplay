package com.cliplay.networking

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

open class AppRxSchedulers : RxSchedulers {


    override fun androidUI(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun network(): Scheduler {
        return NETWORK_SCHEDULER
    }

    override fun background(): Scheduler {
        return BACKGROUND_SCHEDULER
    }

    override fun immediate(): Scheduler {
        return Schedulers.single()

    }


    companion object {
        private val NETWORK_EXECUTOR = Executors.newCachedThreadPool()
        private val NETWORK_SCHEDULER = Schedulers.from(NETWORK_EXECUTOR)
        private val BACKGROUND_EXECUTOR = Executors.newCachedThreadPool()
        private val BACKGROUND_SCHEDULER = Schedulers.from(BACKGROUND_EXECUTOR)

        @Volatile
        private var INSTANCE: AppRxSchedulers? = null

        fun getInstance(): AppRxSchedulers =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: AppRxSchedulers().also { INSTANCE = it }
                }

    }
}

