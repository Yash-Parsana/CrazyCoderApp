package com.parsanatech.crazycoder.Repository

import androidx.lifecycle.LiveData
import com.parsanatech.crazycoder.*

class DbRepository(private val myDao:Dao) {

    val FriendCodefoecesHandles:LiveData<List<FriendCodeforces>> = myDao.getFriendsCodeforcesHandles()

    val FriendLeetcodeHandles:LiveData<List<FriendLeetcode>> = myDao.getFriendsLeetcodeHandles()

    val FriendSpojHandles:LiveData<List<FriendSpoj>> = myDao.getFriendSpojHandle()

    val FriendCodechefHandles:LiveData<List<FriendCodechef>> = myDao.getFriendCodechefHandle()

    val myplatforms:LiveData<List<MyPlatforms>> = myDao.getmyPlatForms()

    fun FriendInsertCodeforces(obj:FriendCodeforces)
    {
        return myDao.FriendInsertCodeforces(obj)
    }
    fun FriendInsertLeetcode(obj:FriendLeetcode)
    {
        return myDao.FriendInsertLeetcode(obj)
    }
    fun FriendInsertSpoj(obj:FriendSpoj)
    {
        return myDao.FriendInsertSpoj(obj)
    }

    fun FriendInsertCodechef(obj:FriendCodechef)
    {
        return myDao.FriendInsertCodechef(obj)
    }

    fun FriendDeleteCodeforces(obj:FriendCodeforces)
    {
        return myDao.FriendDeleteCodeforces(obj)
    }
    fun FriendDeleteLeetcode(obj:FriendLeetcode)
    {
        return myDao.FriendDeleteLeetcode(obj)
    }
    fun FriendDeleteSpoj(obj:FriendSpoj)
    {
        return myDao.FriendDeleteSpoj(obj)
    }

    fun FriendDeleteCodechef(obj:FriendCodechef)
    {
        return myDao.FriendDeleteCodechef(obj)
    }

    fun InsertMyPlatform(obj:MyPlatforms)
    {
        return myDao.insertMyPlatform(obj)
    }


}