package com.example.assignment3_277.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "responses")
data class Response(
    @PrimaryKey(autoGenerate = true)
    val sequenceNumber: Int? = null,
    val datetime: String,
    val response: String
)