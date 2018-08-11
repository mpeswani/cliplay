package com.cliplay.networking.views

import com.cliplay.networking.entity.Response
import com.cliplay.networking.entity.User


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

interface UserView : BaseView {
    fun gotUser(user: User)
    fun gotUserToFollow(users: List<User>)
    fun gotUserCreated(users: Response)
    fun gotUserEdited(users: Response)
}

interface BaseView {
    fun loading(loading: Boolean)

    fun onError(view: BaseView, response: Response)
}