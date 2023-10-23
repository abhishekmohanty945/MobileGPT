package com.example.assignment3_277.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_prompt")
data class Prompt(
    @PrimaryKey(autoGenerate = true)
    val sequenceNumber: Int? = null,
    val datetime: String,
    val prompt: String
)