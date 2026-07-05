package com.example.data

import kotlinx.coroutines.flow.Flow

class FifaCardRepository(private val fifaCardDao: FifaCardDao) {
    val allCards: Flow<List<FifaCard>> = fifaCardDao.getAllCards()

    suspend fun getCardById(id: Int): FifaCard? = fifaCardDao.getCardById(id)

    suspend fun insertCard(card: FifaCard): Long = fifaCardDao.insertCard(card)

    suspend fun deleteCard(card: FifaCard) = fifaCardDao.deleteCard(card)

    suspend fun deleteCardById(id: Int) = fifaCardDao.deleteCardById(id)
}
