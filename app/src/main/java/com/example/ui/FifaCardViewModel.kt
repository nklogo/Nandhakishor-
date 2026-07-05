package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FifaCard
import com.example.data.FifaCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FifaCardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FifaCardRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = FifaCardRepository(database.fifaCardDao())
    }

    val allCards: StateFlow<List<FifaCard>> = repository.allCards
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Form inputs state
    val playerName = MutableStateFlow("")
    val rating = MutableStateFlow("90")
    val position = MutableStateFlow("ST")
    val nationality = MutableStateFlow("Brazil")
    val club = MutableStateFlow("Madrid")
    val pace = MutableStateFlow("85")
    val shooting = MutableStateFlow("85")
    val passing = MutableStateFlow("85")
    val dribbling = MutableStateFlow("85")
    val defending = MutableStateFlow("50")
    val physicality = MutableStateFlow("75")
    val cardType = MutableStateFlow("Gold Rare")
    val imageUri = MutableStateFlow<String?>(null)

    // State of the currently editing card ID (null if creating new)
    private val _editingCardId = MutableStateFlow<Int?>(null)
    val editingCardId = _editingCardId.asStateFlow()

    fun setEditingCard(card: FifaCard) {
        _editingCardId.value = card.id
        playerName.value = card.playerName
        rating.value = card.rating.toString()
        position.value = card.position
        nationality.value = card.nationality
        club.value = card.club
        pace.value = card.pace.toString()
        shooting.value = card.shooting.toString()
        passing.value = card.passing.toString()
        dribbling.value = card.dribbling.toString()
        defending.value = card.defending.toString()
        physicality.value = card.physicality.toString()
        cardType.value = card.cardType
        imageUri.value = card.imageUri
    }

    fun clearForm() {
        _editingCardId.value = null
        playerName.value = ""
        rating.value = "88"
        position.value = "ST"
        nationality.value = "Brazil"
        club.value = "Madrid"
        pace.value = "85"
        shooting.value = "85"
        passing.value = "80"
        dribbling.value = "88"
        defending.value = "50"
        physicality.value = "75"
        cardType.value = "Gold Rare"
        imageUri.value = null
    }

    fun saveCard(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val rVal = rating.value.toIntOrNull() ?: 85
            val pVal = pace.value.toIntOrNull() ?: 80
            val sVal = shooting.value.toIntOrNull() ?: 80
            val paVal = passing.value.toIntOrNull() ?: 80
            val dVal = dribbling.value.toIntOrNull() ?: 80
            val dfVal = defending.value.toIntOrNull() ?: 50
            val phVal = physicality.value.toIntOrNull() ?: 70

            val card = FifaCard(
                id = _editingCardId.value ?: 0,
                playerName = playerName.value.ifBlank { "Custom Player" },
                rating = rVal.coerceIn(1, 99),
                position = position.value.ifBlank { "ST" },
                nationality = nationality.value.ifBlank { "Brazil" },
                club = club.value.ifBlank { "Madrid" },
                pace = pVal.coerceIn(1, 99),
                shooting = sVal.coerceIn(1, 99),
                passing = paVal.coerceIn(1, 99),
                dribbling = dVal.coerceIn(1, 99),
                defending = dfVal.coerceIn(1, 99),
                physicality = phVal.coerceIn(1, 99),
                cardType = cardType.value,
                imageUri = imageUri.value
            )

            withContext(Dispatchers.IO) {
                repository.insertCard(card)
            }
            clearForm()
            onSuccess()
        }
    }

    fun deleteCard(card: FifaCard) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteCard(card)
            }
        }
    }

    // Add sample cards to dynamic collection on demand or if database is empty
    fun addSampleLegends() {
        viewModelScope.launch {
            val samples = listOf(
                FifaCard(
                    playerName = "Pelé",
                    rating = 99,
                    position = "ST",
                    nationality = "Brazil",
                    club = "🛡️",
                    pace = 96,
                    shooting = 96,
                    passing = 93,
                    dribbling = 97,
                    defending = 60,
                    physicality = 87,
                    cardType = "Icon"
                ),
                FifaCard(
                    playerName = "L. Messi",
                    rating = 97,
                    position = "CF",
                    nationality = "Argentina",
                    club = "inter miami",
                    pace = 91,
                    shooting = 96,
                    passing = 98,
                    dribbling = 99,
                    defending = 45,
                    physicality = 72,
                    cardType = "Team of the Year"
                ),
                FifaCard(
                    playerName = "C. Ronaldo",
                    rating = 95,
                    position = "ST",
                    nationality = "Portugal",
                    club = "al nassr",
                    pace = 94,
                    shooting = 97,
                    passing = 85,
                    dribbling = 90,
                    defending = 40,
                    physicality = 92,
                    cardType = "Centurions"
                )
            )

            withContext(Dispatchers.IO) {
                samples.forEach { card ->
                    repository.insertCard(card)
                }
            }
        }
    }
}
