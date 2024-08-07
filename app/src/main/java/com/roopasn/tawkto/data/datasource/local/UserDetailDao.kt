package com.roopasn.tawkto.data.datasource.local

import androidx.room.*
import com.roopasn.tawkto.data.model.local.TABLE_NAME_USER_DETAILS
import com.roopasn.tawkto.data.model.local.UserDetailEntity

/**
 * ROOM database dao class interface for Git user detail
 */
@Dao
interface UserDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gitUserDetail: UserDetailEntity)

//    @Query("SELECT * FROM $TABLE_NAME_USER_DETAILS")
//    suspend fun getAllUserDetails(): List<GitUserDetailEntity>

    @Query("SELECT * FROM $TABLE_NAME_USER_DETAILS where login = :login")
    suspend fun getUserDetail(login: String): UserDetailEntity?

    @Delete
    suspend fun deleteUserDetail(gitUserList: UserDetailEntity)

//    @Query("DELETE FROM $TABLE_NAME_USER_DETAILS")
//    suspend fun deleteAllUserDetails()

    @Update
    suspend fun updateUserDetail(gitUserList: UserDetailEntity)
}