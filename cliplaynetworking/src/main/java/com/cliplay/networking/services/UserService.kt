package com.cliplay.networking.services

import com.cliplay.networking.entity.User
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {

    @GET("/mutualusers/{userId}")
    fun usersToFollow(@Path("userId") userId: String): Flowable<List<User>>

    @GET("users/{userId}")
    fun user(@Path("userId") userId: String): Maybe<User>

    @POST
    fun createUser(@Body user: User): User

    @POST
    fun editUser(@Body user: User): User

}
