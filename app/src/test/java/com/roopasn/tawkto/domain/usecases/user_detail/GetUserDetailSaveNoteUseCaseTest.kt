package com.roopasn.tawkto.domain.usecases.user_detail

import com.roopasn.tawkto.data.model.local.UserDetailEntity
import com.roopasn.tawkto.data.repository.UserRepositoryImpl
import com.roopasn.tawkto.domain.model.UserDetail
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class GetUserDetailSaveNoteUseCaseTest {
    companion object {
        fun getGitUserDetailEntity(
            id: Int,
            login: String,
            noteId: String = "noteId"
        ): UserDetailEntity {
            return UserDetailEntity(
                id,
                login,
                noteId,
                "avatarUrl",
                "gravatarId",
                "url",
                "htmlUrl",
                "followersUrl",
                "followingUrl",
                "gistsUrl",
                "starredUrl",
                "subscriptionsUrl",
                "organizationsUrl",
                "reposUrl",
                "eventsUrl",
                "receivedEventsUrl",
                "type",
                false,
                "sname",
                "company",
                "blog",
                "location",
                "email",
                false,
                "bio",
                "twitterUsername",
                1,
                1,
                1,
                1,
                "createdAt",
                "updatedAt"
            )
        }
    }

    @Mock
    lateinit var mUserRepository: UserRepositoryImpl

    private val mUserDetail = UserDetail(
        1, "login", "noteId", "url", "name", "company", "blog", "location",
        "email", "bio", 1, 1, null
    )


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testInvoke() = runBlocking {
        Mockito.doNothing().`when`(mUserRepository).updateUserDetail(any())

        Mockito.`when`(mUserRepository.getUserDetail(any()))
            .thenReturn(getGitUserDetailEntity(1, "login", "updated"))

        val cut = GetUserDetailSaveNoteUseCase(mUserRepository)
        cut.invoke(mUserDetail) {
        }

        cut.saveUserDetailInternal(mUserDetail) {
            Assert.assertTrue(it)
        }
    }
}