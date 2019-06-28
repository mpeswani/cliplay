package com.cliplay.networking.repository.dbrepository

import com.cliplay.networking.database.PostDao
import com.cliplay.networking.entity.Post
import com.cliplay.networking.entity.Response
import com.cliplay.networking.repository.PostRepository
import com.cliplay.networking.response
import io.reactivex.Maybe
import io.reactivex.Observable

class DBPostRepository(private val postDao: PostDao = com.cliplay.networking.postDao()) : PostRepository {

    override fun getPostById(id: String): Maybe<Post> {
        return postDao.getPostByID(id)
    }

    override fun getPosts(currentUserId: String): Observable<List<Post>> {
        return postDao.posts
    }

    override fun createPost(post: Post): Response {
       return response(postDao.insertAll(post))
    }

    override fun editPost(post: Post): Response {
        return response(postDao.updatePost(post).toLong())
    }

}