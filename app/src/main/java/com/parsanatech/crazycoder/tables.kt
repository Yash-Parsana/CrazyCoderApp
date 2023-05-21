package com.parsanatech.crazycoder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "FriendCodeforces",indices = [Index(value = ["handle"], unique = true)])
class FriendCodeforces(@PrimaryKey var handle:String, @ColumnInfo var itsMe:Boolean) {

}
@Entity(tableName = "FriendCodechef",indices = [Index(value = ["handle"], unique = true)])
    class FriendCodechef(@PrimaryKey var handle:String, @ColumnInfo var itsMe:Boolean) {

}
@Entity(tableName = "FriendLeetcode", indices = [Index(value = ["handle"], unique = true)])
class FriendLeetcode(@PrimaryKey var handle: String, @ColumnInfo var itsMe:Boolean){

}

@Entity(tableName = "FriendSpoj", indices = [Index(value = ["handle"], unique = true)])
class FriendAtcoder(@PrimaryKey var handle: String, @ColumnInfo var itsMe:Boolean)
{

}

@Entity(tableName = "MyPlatforms")
class MyPlatforms(@PrimaryKey var platform:String,@ColumnInfo var handle:String)
{

}