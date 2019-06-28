package com.cliplay.networking.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Manohar Peswani on 11/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */

@Entity
data class User(@PrimaryKey(autoGenerate = true) val id: Long, val _id: String, val name: String, val about: String,
                val phoneNumber: String, val token: String,
                val lastLogin: String, val avatarUrl: String, @Embedded val location: Location,
                val emailId: String, val gender: Boolean, val timestamp: Long = System.currentTimeMillis())

@Entity
data class Location(val userId: String, val lat: Double, val lng: Double, val city: String,
                    val country: String, val state: String)

@Entity
data class Role(@PrimaryKey(autoGenerate = true) val id: Long, val roleName: String, val roleType: Int)

@Entity
data class Post(@PrimaryKey val _id: String, val userId: String, @Embedded val media: Media,
                val description: String, val likeCount: Int, val shareCount: Int, val commentCount: Int)

@Entity
data class Media(val mediaType: Int, val mediaUrl: String, val mediaThumbnail: String, val sizeInKB: Int)

@Entity
data class UserProfile(@PrimaryKey val userId: String, val posts: Int, val followers: Int,
                       val following: Int)

@Entity
enum class MediaType(val value: Int) {
    IMAGE(2), VIDEO(3), TEXT(1);
}