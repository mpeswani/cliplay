package com.cliplay.networking

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.cliplay.networking.database.AppDatabase
import com.cliplay.networking.database.UserDao
import com.cliplay.networking.presenter.UserPresenter
import com.cliplay.networking.repository.UserRepository
import com.cliplay.networking.repository.dbrepository.DBUserRepository
import com.cliplay.networking.views.UserView
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit






/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleUnitTest {
    private var mUserDao: UserDao? = null
    private var mDb: AppDatabase? = null
    @get:Rule
    var rule = MockitoJUnit.rule()!!

    @Mock
    lateinit var userView: UserView

    @Mock
    lateinit var networkRepo: UserRepository

    lateinit var dbRepo: UserRepository

    @InjectMocks
    lateinit var userService: UserPresenter


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val context = InstrumentationRegistry.getTargetContext()
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        mUserDao = mDb!!.userDao
        dbRepo = DBUserRepository(mUserDao!!)
    }

    @Test
    fun emptyTest() {

//        Mockito.`when`(userService.getUser("5b374c12a35c3a1a0e7d5dcc")).thenReturn(us)

    }


    @Test
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun testUser() {
        userService.getUser("5b374c12a35c3a1a0e7d5dcc")
    }

}