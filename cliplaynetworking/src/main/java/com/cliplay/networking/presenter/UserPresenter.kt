package com.cliplay.networking.presenter

import android.util.Log
import com.cliplay.networking.entity.Response
import com.cliplay.networking.entity.User
import com.cliplay.networking.repository.UserRepository
import com.cliplay.networking.repository.dbrepository.DBUserRepository
import com.cliplay.networking.repository.networkrepository.NtUserRepository
import com.cliplay.networking.views.UserView
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

class UserPresenter(private val userView: UserView?, private val networkRepository: UserRepository = NtUserRepository(), private val databaseRepository: UserRepository = DBUserRepository()) : BasePresenter() {

    fun getUser(userId: String) {

        if (userView == null) {
            throw IllegalArgumentException("Please pass user view")
        }

        showProgress(userView)

        val dbSource = databaseRepository.getUserById(userId).subscribeOn(Schedulers.io())

        val networkSource = networkRepository.getUserById(userId)
                .doOnSuccess { t: User? ->
                    Log.v("rx", "rx saving in db " + System.currentTimeMillis())
                    databaseRepository.editUser(t!!)
                }.subscribeOn(Schedulers.io())

        disposable.add(Maybe.concat(dbSource, networkSource)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ t ->
                    hideProgress(userView)
                    Log.v("rx", "rx got the result " + System.currentTimeMillis())
                    userView.gotUser(t)
                    println(t)
                }, {
                    hideProgress(userView)
                    userView.onError(userView, Response(0, it.localizedMessage))
                    it.printStackTrace()
                    Log.v("rx", "rx got error result " + System.currentTimeMillis())
                }))
    }
}

fun <T> load(loadFunction: () -> T): Deferred<T> {
    return async { loadFunction() }
}

infix fun <T> Deferred<T>.then(uiFunction: (T) -> Unit) {
    launch(UI) { uiFunction(this@then.await()) }
}