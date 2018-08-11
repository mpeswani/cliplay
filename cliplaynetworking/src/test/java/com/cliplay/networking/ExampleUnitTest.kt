package com.cliplay.networking

import com.cliplay.networking.entity.Response
import com.cliplay.networking.entity.User
import com.cliplay.networking.presenter.UserPresenter
import com.cliplay.networking.services.NetworkService
import com.cliplay.networking.views.BaseView
import com.cliplay.networking.views.UserView
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    @get:Rule
    var rule = MockitoJUnit.rule()
    @Mock
    lateinit var service: NetworkService

    @Mock
    lateinit var userView: UserView

    @InjectMocks
    lateinit var userService: UserPresenter


    @Before
    fun before() {

    }

    @Test
    fun emptyTest() {
        userService.getUser("5b374c12a35c3a1a0e7d5dcc")
    }


    @Test
    fun addition_isCorrect() {
        assertEquals(4, (2 + 2).toLong())
    }

    @Test
    fun testUser() {
        UserPresenter(object : UserView {
            override fun gotUser(user: User) {
                assertEquals("Mr. Ana McKenzie", user.name)
            }

            override fun gotUserToFollow(users: List<User>) {
            }

            override fun gotUserCreated(users: Response) {
            }

            override fun gotUserEdited(users: Response) {
            }

            override fun loading(loading: Boolean) {
                assertEquals(true, loading)
            }

            override fun onError(view: BaseView, response: Response) {
            }
        }).getUser("5b374c12a35c3a1a0e7d5dcc")
    }

}