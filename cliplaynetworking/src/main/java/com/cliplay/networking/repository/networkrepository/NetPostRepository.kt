package com.cliplay.networking.repository.networkrepository

import com.cliplay.networking.entity.Post
import com.cliplay.networking.entity.Response
import com.cliplay.networking.repository.PostRepository
import com.cliplay.networking.services.PostService
import com.cliplay.networking.services.RetrofitService
import io.reactivex.Maybe
import io.reactivex.Observable

class NetPostRepository : PostRepository {

    private var networkService: PostService = RetrofitService.getInstance().create(PostService::class.java)

    override fun getPostById(id: String): Maybe<Post> {
        return networkService.postById(id)
    }

    override fun getPosts(currentUserId: String): Observable<List<Post>> {
        return networkService.postsList(currentUserId)
    }

    override fun createPost(post: Post): Response {
        return networkService.createPost(post)
    }

    override fun editPost(post: Post): Response {
        return networkService.editPost(post)
    }

}