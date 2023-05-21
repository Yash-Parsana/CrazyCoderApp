package com.parsanatech.crazycoder.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.parsanatech.crazycoder.database.MyDatabase
import com.parsanatech.crazycoder.entity.FriendCodechef
import com.parsanatech.crazycoder.entity.FriendCodeforces
import com.parsanatech.crazycoder.entity.FriendLeetcode
import com.parsanatech.crazycoder.entity.FriendSpoj
import com.parsanatech.crazycoder.entity.MyPlatforms
import com.parsanatech.crazycoder.repository.DbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DbViewModel(application:Application):AndroidViewModel(application) {

    val FriendCodeforcesHandles:LiveData<List<FriendCodeforces>>
    val FriendLeetcodeHandles:LiveData<List<FriendLeetcode>>
    val FriendSpojHandles:LiveData<List<FriendSpoj>>
    val FriendCodechefHandles:LiveData<List<FriendCodechef>>
    val MyPlatforms:LiveData<List<MyPlatforms>>

    val repository:DbRepository

    init {

        val dao= MyDatabase.getDatabase(application).getDao()

        repository= DbRepository(dao)

        FriendCodeforcesHandles=repository.FriendCodefoecesHandles
        FriendLeetcodeHandles=repository.FriendLeetcodeHandles
        FriendSpojHandles=repository.FriendSpojHandles
        FriendCodechefHandles=repository.FriendCodechefHandles
        MyPlatforms=repository.myplatforms

    }

    fun FriendInsertCodeforces(obj: FriendCodeforces)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendInsertCodeforces(obj)
    }
    fun FriendInsertLeetcode(obj: FriendLeetcode)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendInsertLeetcode(obj)
    }
    fun FriendInsertSpoj(obj: FriendSpoj)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendInsertSpoj(obj)
    }
    fun FriendInsertCodechef(obj: FriendCodechef)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendInsertCodechef(obj)
    }


    fun FriendDeleteCodeforces(obj: FriendCodeforces)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendDeleteCodeforces(obj)
    }

    fun FriendDeleteLeetcode(obj: FriendLeetcode)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendDeleteLeetcode(obj)
    }

    fun FriendDeleteSpoj(obj: FriendSpoj)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendDeleteSpoj(obj)
    }
    fun FriendDeleteCodechef(obj: FriendCodechef)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.FriendDeleteCodechef(obj)
    }



    fun insertMyPlatform(obj: MyPlatforms)=viewModelScope.launch(Dispatchers.IO)
    {
        repository.InsertMyPlatform(obj)
    }

}