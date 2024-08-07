package com.roopasn.tawkto.data.datasource.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.roopasn.tawkto.data.model.local.TABLE_NAME_USERS
import com.roopasn.tawkto.data.model.local.UserEntity

/**
 * Git User ROM DB Dao interface class
 */
@Dao
interface UsersDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insert(gitUserEntity: GitUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(gitUserEntity: List<UserEntity>)

//    @Query("SELECT * FROM $TABLE_NAME_USERS ORDER BY since ASC")
//    fun getAllUsers(): List<GitUserEntity>

    @Query("SELECT * FROM $TABLE_NAME_USERS WHERE since = :since ORDER BY since ASC")
    fun getAllUsers(since: Int): List<UserEntity>

    /**
     * API to support compose paging mechanism
     */
    @Query("SELECT * FROM $TABLE_NAME_USERS  WHERE login LIKE :query OR nodeId LIKE :query ORDER BY since ASC")
    fun getAllUsersPagingSource(query: String): PagingSource<Int, UserEntity>

    @Query("SELECT * FROM $TABLE_NAME_USERS where login = :login")
    fun getUser(login: String): UserEntity?

//    @Delete
//    fun deleteUser(gitUserEntity: GitUserEntity)

    @Query("DELETE FROM $TABLE_NAME_USERS WHERE since = :since")
    fun deleteUsersPage(since: Int)

    @Query("DELETE FROM $TABLE_NAME_USERS")
    fun deleteAllUser()

//    @Update
//    fun updateUser(gitUserEntity: GitUserEntity)
}

