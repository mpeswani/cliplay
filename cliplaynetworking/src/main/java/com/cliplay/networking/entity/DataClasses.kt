package com.cliplay.networking.entity

import java.util.*


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

data class User(val _id: String, val name: String, val about: String,
                val phoneNumber: String, val token: String, val roles: List<Role>,
                val lastLogin: Date, val avatarUrl: String, val location: Location,
                val emailId: String, val gender: Boolean)


data class Location(val userId: String, val lat: Double, val lng: Double, val city: String,
                    val country: String, val state: String)


data class Role(val roleName: String, val roleType: Int)


data class Post(val _id: String, val userId: String, val media: Media,
                val description: String, val likeCount: Int, val shareCount: Int, val commentCount: Int)


data class Media(val mediaType: Int, val mediaUrl: String, val mediaThumbnail: String, val sizeInKB: Int)


data class UserProfile(val userId: String, val posts: Int, val followers: Int,
                       val following: Int)

enum class MediaType(val value: Int) {
    IMAGE(2), VIDEO(3), TEXT(1);
}