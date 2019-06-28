package com.cliplay.networking

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.cliplay.networking.database.AppDatabase
import com.cliplay.networking.database.PostDao
import com.cliplay.networking.database.UserDao
import com.cliplay.networking.entity.Response
import com.cliplay.networking.services.RetrofitService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.rx2.asCoroutineDispatcher


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

class CliplayNetworking {

    companion object {
        fun init(context: Application) {
            RetrofitService.getInstance()
            getDatabase(context)
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

fun sc(): AppCoroutineDispatchers {
    return AppCoroutineDispatchers.getInstance(AppRxSchedulers.getInstance())
}

fun provideDispatchers(schedulers: AppRxSchedulers) =
        AppCoroutineDispatchers(
                database = schedulers.background().asCoroutineDispatcher(),
                disk = schedulers.io().asCoroutineDispatcher(),
                network = schedulers.network().asCoroutineDispatcher(),
                main = UI
        )

private var db: AppDatabase? = null


fun response(value: Long): Response {
    return if (value != 0L) {
        Response(1, "success")
    } else {
        Response(0, "success")
    }
}

fun getDatabase(context: Context): AppDatabase {
    if (db == null) {
        db = Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()
    }
    return db as AppDatabase
}

fun userDao(): UserDao {
    return db!!.userDao
}

fun postDao(): PostDao {
    return db!!.postDao
}