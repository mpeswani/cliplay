package com.cliplay.networking.repository.networkrepository

import com.cliplay.networking.entity.Response
import com.cliplay.networking.entity.User
import com.cliplay.networking.repository.UserRepository
import com.cliplay.networking.services.UserService
import com.cliplay.networking.services.RetrofitService
import io.reactivex.Maybe
import io.reactivex.Observable


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

class NtUserRepository : UserRepository {
    private var networkService: UserService = RetrofitService.getInstance().create(UserService::class.java)

    override fun getUserById(id: String): Maybe<User> {
        return networkService.user(id)
    }

    override fun getUserToFollow(currentUserId: String): Observable<List<User>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createUser(user: User): Response {
        val createUser = networkService.createUser(user)
        return if (user.name == createUser.name) {
            Response(1, "success")
        } else {
            Response(0, "fail")
        }
    }

    override fun editUser(user: User): Response {
        val createUser = networkService.editUser(user)
        return if (user.name == createUser.name) {
            Response(1, "success")
        } else {
            Response(0, "fail")
        }
    }

}