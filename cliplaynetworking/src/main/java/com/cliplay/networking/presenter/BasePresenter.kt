package com.cliplay.networking.presenter

import com.cliplay.networking.views.UserView
import io.reactivex.disposables.CompositeDisposable


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

open class BasePresenter {
    protected val disposable: CompositeDisposable = CompositeDisposable()

    fun showProgress(userView: UserView?) {
        userView?.loading(true)
    }

    fun hideProgress(userView: UserView?) {
        userView?.loading(false)
    }
}
