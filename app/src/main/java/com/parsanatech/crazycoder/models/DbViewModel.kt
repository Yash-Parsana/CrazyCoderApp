package com.parsanatech.crazycoder.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.parsanatech.crazycoder.*
import com.parsanatech.crazycoder.Repository.DbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DbViewModel(application:Application):AndroidViewModel(application) {

    val FriendCodeforcesHandles:LiveData<List<FriendCodeforces>>
    val FriendLeetcodeHandles:LiveData<List<FriendLeetcode>>
    val FriendAtcoderHandles:LiveData<List<FriendAtcoder>>
    val FriendCodechefHandles:LiveData<List<FriendCodechef>>
    val MyPlatforms:LiveData<List<MyPlatforms>>

    val repository:DbRepository

    init {

        val dao=MyDatabase.getDatabase(application).getDao()

        repository= DbRepository(dao)

        FriendCodeforcesHandles=repository.FriendCodefoecesHandles
        FriendLeetcodeHandles=repository.FriendLeetcodeHandles
        FriendAtcoderHandles=repository.FriendSpojHandles
        FriendCodechefHandles=repository.FriendCodechefHandles
        MyPlatforms=repository.myplatforms

    }

    fun FriendInsertCodeforces(obj:FriendCodeforces)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendInsertCodeforces(obj)
    }
    fun FriendInsertLeetcode(obj:FriendLeetcode)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendInsertLeetcode(obj)
    }
    fun FriendInsertAtcoder(obj:FriendAtcoder)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendInsertSpoj(obj)
    }
    fun FriendInsertCodechef(obj:FriendCodechef)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendInsertCodechef(obj)
    }


    fun FriendDeleteCodeforces(obj:FriendCodeforces)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendDeleteCodeforces(obj)
    }

    fun FriendDeleteLeetcode(obj:FriendLeetcode)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendDeleteLeetcode(obj)
    }

    fun FriendDeleteAtcoder(obj:FriendAtcoder)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendDeleteSpoj(obj)
    }
    fun FriendDeleteCodechef(obj:FriendCodechef)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendDeleteCodechef(obj)
    }



    fun insertMyPlatform(obj:MyPlatforms)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.InsertMyPlatform(obj)
    }

}