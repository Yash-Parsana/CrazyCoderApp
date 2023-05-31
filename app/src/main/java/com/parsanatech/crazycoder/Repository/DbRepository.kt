package com.parsanatech.crazycoder.Repository

import androidx.lifecycle.LiveData
import com.parsanatech.crazycoder.*

class DbRepository(private val myDao:Dao) {

    val FriendCodefoecesHandles:LiveData<List<FriendCodeforces>> = myDao.getFriendsCodeforcesHandles()

    val FriendLeetcodeHandles:LiveData<List<FriendLeetcode>> = myDao.getFriendsLeetcodeHandles()

    val FriendSpojHandles:LiveData<List<FriendAtcoder>> = myDao.getFriendAtcoderHandle()

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
    fun FriendInsertSpoj(obj:FriendAtcoder)
    {
        return myDao.FriendInsertAtcoder(obj)
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
    fun FriendDeleteSpoj(obj:FriendAtcoder)
    {
        return myDao.FriendDeleteAtcoder(obj)
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