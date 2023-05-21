package com.parsanatech.crazycoder.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.parsanatech.crazycoder.entity.FriendCodechef
import com.parsanatech.crazycoder.entity.FriendCodeforces
import com.parsanatech.crazycoder.entity.FriendLeetcode
import com.parsanatech.crazycoder.entity.FriendSpoj
import com.parsanatech.crazycoder.entity.MyPlatforms

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun FriendInsertCodeforces(obj: FriendCodeforces)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun FriendInsertLeetcode(obj: FriendLeetcode)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun FriendInsertSpoj(obj: FriendSpoj)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun FriendInsertCodechef(obj: FriendCodechef)

    @Delete
    fun FriendDeleteCodeforces(obj: FriendCodeforces)

    @Delete
    fun FriendDeleteLeetcode(obj: FriendLeetcode)

    @Delete
    fun FriendDeleteSpoj(obj: FriendSpoj)

    @Delete
    fun FriendDeleteCodechef(obj: FriendCodechef)

    @Query("select*from FriendCodeforces")
    fun getFriendsCodeforcesHandles():LiveData<List<FriendCodeforces>>

    @Query("select*from FriendLeetcode")
    fun getFriendsLeetcodeHandles():LiveData<List<FriendLeetcode>>

    @Query("select*from FriendSpoj")
    fun getFriendSpojHandle():LiveData<List<FriendSpoj>>

    @Query("select*from FriendCodechef")
    fun getFriendCodechefHandle():LiveData<List<FriendCodechef>>


    @Query("select*from MyPlatforms")
    fun getmyPlatForms():LiveData<List<MyPlatforms>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMyPlatform(obj: MyPlatforms)

}