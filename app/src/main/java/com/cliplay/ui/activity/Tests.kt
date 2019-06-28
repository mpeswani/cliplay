package com.cliplay.ui.activity

import android.content.Context
import android.widget.Toast
import com.cliplay.networking.entity.Response
import com.cliplay.networking.entity.User
import com.cliplay.networking.presenter.UserPresenter
import com.cliplay.networking.views.BaseView
import com.cliplay.networking.views.UserView


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

fun x(context: Context) {
//    Log.v("SINGLE", "====>" + AppCoroutineDispatchers.getInstance(AppRxSchedulers.getInstance()))
//    Log.v("SINGLE", "====>" + AppCoroutineDispatchers.getInstance(AppRxSchedulers.getInstance()))
//    Log.e("SINGLE", "====>" + AppCoroutineDispatchers.getInstance(AppRxSchedulers.getInstance()))
//    Log.e("SINGLE", "====>" + AppCoroutineDispatchers.getInstance(AppRxSchedulers.getInstance()))

    val p = UserPresenter(object : UserView {
        override fun gotUser(user: User) {
            Toast.makeText(context, user.toString(), Toast.LENGTH_LONG).show()
        }

        override fun gotUserToFollow(users: List<User>) {
        }

        override fun gotUserCreated(users: Response) {
        }

        override fun gotUserEdited(users: Response) {
        }

        override fun loading(loading: Boolean) {
            Toast.makeText(context, ":$loading", Toast.LENGTH_LONG).show()
        }

        override fun onError(view: BaseView, response: Response) {
            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
        }

    })

    p.getUser("5b3f4342311d5b031839a953")
}