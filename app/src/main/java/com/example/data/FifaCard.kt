package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fifa_cards")
data class FifaCard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val rating: Int,
    val position: String,
    val nationality: String,
    val club: String,
    val pace: Int,
    val shooting: Int,
    val passing: Int,
    val dribbling: Int,
    val defending: Int,
    val physicality: Int,
    val cardType: String, // "Gold Rare", "TOTY", "Icon", "FutBirthday", "FutCenturions"
    val imageUri: String? = null // Local URI of uploaded player portrait
)
