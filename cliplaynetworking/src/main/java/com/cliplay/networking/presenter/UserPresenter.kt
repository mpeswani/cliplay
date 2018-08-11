package com.cliplay.networking.presenter

import com.cliplay.networking.entity.Response
import com.cliplay.networking.entity.User
import com.cliplay.networking.services.NetworkService
import com.cliplay.networking.services.RetrofitService
import com.cliplay.networking.views.UserView
import io.reactivex.observers.DisposableSingleObserver


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

class UserPresenter(val userView: UserView) : BasePresenter() {

    private var networkService: NetworkService = RetrofitService.getInstance().create(NetworkService::class.java)

    fun getUser(userId: String) {
        val observer = object : DisposableSingleObserver<User>() {
            override fun onSuccess(o: User) {
                userView.gotUser(o)
            }

            override fun onError(e: Throwable) {
                userView.onError(userView, Response(0, "", e.localizedMessage))
            }
        }
        disposable.add(networkService.user(userId).subscribeWith(observer))
    }
}