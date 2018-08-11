package com.cliplay.networking.repository

import com.cliplay.networking.entity.Response
import com.cliplay.networking.entity.User
import io.reactivex.Observable
import io.reactivex.Single


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

interface UserRepository {

    fun loaduserById(id: String): Single<User>

    fun loadUserToFollow(currentUserId: String): Observable<List<User>>

    fun createUser(user: User): Response

    fun editUser(user: User): Response

}
