package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FifaCardDao {
    @Query("SELECT * FROM fifa_cards ORDER BY id DESC")
    fun getAllCards(): Flow<List<FifaCard>>

    @Query("SELECT * FROM fifa_cards WHERE id = :id LIMIT 1")
    suspend fun getCardById(id: Int): FifaCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: FifaCard): Long

    @Delete
    suspend fun deleteCard(card: FifaCard)

    @Query("DELETE FROM fifa_cards WHERE id = :id")
    suspend fun deleteCardById(id: Int)
}
