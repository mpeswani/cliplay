package com.cliplay.networking.services

import com.cliplay.networking.entity.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface NetworkService {

    @GET("/users/{userId}")
    fun usersToFollow(@Path("userId") userId: String): Single<List<User>>

    @GET("users/{userId}")
    fun user(@Path("userId") userId: String): Single<User>

}
