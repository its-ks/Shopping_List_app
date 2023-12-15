package com.example.todo.room.Models.converters

import androidx.room.TypeConverter
import java.util.*

open class DateConverter(){
    @TypeConverter
    fun toDate(date: Long?): Date?{
        return date?.let { Date(it) }
    }

    @TypeConverter
    fun Datefrom(date: Date?): Long?{
        return date?.time
    }
}