package com.cliplay.networking.presenter

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.cliplay.networking.database.AppDatabase
import com.cliplay.networking.database.UserDao
import com.cliplay.networking.entity.User
import com.cliplay.networking.repository.UserRepository
import com.cliplay.networking.repository.dbrepository.DBUserRepository
import com.cliplay.networking.repository.networkrepository.NtUserRepository
import com.cliplay.networking.views.UserView
import com.google.gson.Gson
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit

/**
 * Created by Manohar Peswani on 12/08/18.
 * copyright (c) crafty endeavours
 * manoharpeswani@outlook.com
 */
@RunWith(AndroidJUnit4::class)
class UserPresenterTest {
    private var mUserDao: UserDao? = null
    private var mDb: AppDatabase? = null
    @get:Rule
    var rule = MockitoJUnit.rule()!!

    @Mock
    lateinit var userView: UserView

    lateinit var networkRepo: UserRepository

    lateinit var dbRepo: UserRepository

    @Mock
    lateinit var userService: UserPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val context = InstrumentationRegistry.getTargetContext()
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        mUserDao = mDb!!.userDao
        dbRepo = DBUserRepository(mUserDao!!)
        networkRepo = NtUserRepository()
        userService = UserPresenter(userView, networkRepo, dbRepo)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getUser() {
        val user = "{\n" +
                "    \"_id\": \"5b374c12a35c3a1a0e7d5dcc\",\n" +
                "    \"name\": \"Mr. Ana McKenzie\",\n" +
                "    \"about\": \"Can one desire too much of a good thing?.\",\n" +
                "    \"phoneNumber\": \"835.129.7304\",\n" +
                "    \"token\": \"dZUYCeXSHyURoVGdPEpsMHvmNkNXFgEXlSuYaruUnXiLDvBlpUTMExhXHdhRdHJv\",\n" +
                "    \"roles\": [\n" +
                "        {\n" +
                "            \"roleName\": \"user\",\n" +
                "            \"roleType\": 1\n" +
                "        }\n" +
                "    ],\n" +
                "    \"lastLogin\": \"2018-07-14T16:33:18.837+0000\",\n" +
                "    \"avatarUrl\": \"https://s3.amazonaws.com/uifaces/faces/twitter/nicoleglynn/128.jpg\",\n" +
                "    \"location\": {\n" +
                "        \"userId\": \"5b374c12a35c3a1a0e7d5dcc\",\n" +
                "        \"lat\": 63.091689,\n" +
                "        \"lng\": 39.938532,\n" +
                "        \"city\": \"Shieldsshire\",\n" +
                "        \"country\": \"Falkland Islands (Malvinas)\",\n" +
                "        \"state\": \"Washington\"\n" +
                "    },\n" +
                "    \"emailId\": \"savion.muller@hotmail.com\",\n" +
                "    \"gender\": false\n" +
                "}"

        val us = Gson().fromJson(user, User::class.java)
        Mockito.`when`(userService.getUser("5b374c12a35c3a1a0e7d5dcc")).thenReturn(Single.just(us) as Unit)
        Mockito.verify(userView).gotUser(any())

    }

    @Test
    fun getUserView() {
    }
}