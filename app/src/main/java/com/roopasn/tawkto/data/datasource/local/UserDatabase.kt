package com.roopasn.tawkto.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.roopasn.tawkto.data.model.local.UserDetailEntity
import com.roopasn.tawkto.data.model.local.UserEntity

/**
 * ROOM database abstract class definition
 */
@Database(entities = [UserEntity::class, UserDetailEntity::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun getUsersDao(): UsersDao
    abstract fun getUserDetailDao(): UserDetailDao
}