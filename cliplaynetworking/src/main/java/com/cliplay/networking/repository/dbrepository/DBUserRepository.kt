package com.cliplay.networking.repository.dbrepository

import com.cliplay.networking.database.UserDao
import com.cliplay.networking.entity.Response
import com.cliplay.networking.entity.User
import com.cliplay.networking.repository.UserRepository
import com.cliplay.networking.response
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

class DBUserRepository(private val userDao: UserDao = com.cliplay.networking.userDao()) : UserRepository {

    override fun getUserById(id: String): Maybe<User> {
        return userDao.getUserByID(id)
    }

    override fun getUserToFollow(currentUserId: String): Observable<List<User>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createUser(user: User): Response {
        val value = userDao.insertAll(user)
        return response(value)
    }

    override fun editUser(user: User): Response {
        val value = userDao.updateUser(user)
        return response(value.toLong())
    }

}
