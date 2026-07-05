package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.FifaCard
import com.example.ui.FifaCardViewModel
import com.example.ui.components.FifaCardType
import com.example.ui.components.FifaCardView
import com.example.ui.components.getCountryEmoji

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFifaScreen(
    viewModel: FifaCardViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Squad, 1 = Card Creator
    val cards by viewModel.allCards.collectAsStateWithLifecycle()
    var selectedCardForDetail by remember { mutableStateOf<FifaCard?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = "Soccer Ball",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FUT CARD CREATOR",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        selectedCardForDetail = null
                    },
                    icon = { Icon(Icons.Default.People, contentDescription = "My Squad") },
                    label = { Text("My Squad") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        selectedCardForDetail = null
                    },
                    icon = { Icon(Icons.Default.AddBox, contentDescription = "Creator") },
                    label = { Text("Card Creator") }
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> {
                    SquadCollectionScreen(
                        cards = cards,
                        onCardClick = { selectedCardForDetail = it },
                        onEditClick = { card ->
                            viewModel.setEditingCard(card)
                            selectedTab = 1
                        },
                        onDeleteClick = { viewModel.deleteCard(it) },
                        onLoadSamples = { viewModel.addSampleLegends() }
                    )
                }
                1 -> {
                    CardCreatorScreen(
                        viewModel = viewModel,
                        onSaved = {
                            selectedTab = 0 // Go back to squad to view
                        }
                    )
                }
            }

            // High Fidelity Overlay Modal for Card Details
            selectedCardForDetail?.let { card ->
                CardDetailModal(
                    card = card,
                    onDismiss = { selectedCardForDetail = null },
                    onEdit = {
                        viewModel.setEditingCard(card)
                        selectedCardForDetail = null
                        selectedTab = 1
                    },
                    onDelete = {
                        viewModel.deleteCard(card)
                        selectedCardForDetail = null
                    }
                )
            }
        }
    }
}

@Composable
fun SquadCollectionScreen(
    cards: List<FifaCard>,
    onCardClick: (FifaCard) -> Unit,
    onEditClick: (FifaCard) -> Unit,
    onDeleteClick: (FifaCard) -> Unit,
    onLoadSamples: () -> Unit
) {
    if (cards.isEmpty()) {
        // High fidelity empty state with a helpful tip and dynamic presets
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = "Empty Badge",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Cards Created Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start designing your dream FIFA card! Adjust ratings, customize specialized stats, upload personal photos, or load preset football legends to explore.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoadSamples,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = "Legends")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Load Football Legends")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Ultimate Squad (${cards.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                
                TextButton(
                    onClick = onLoadSamples,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, modifier = Modifier.size(16.dp), contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Legends", fontSize = 13.sp)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 145.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(cards, key = { it.id }) { card ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .clickable { onCardClick(card) }
                            .padding(8.dp)
                    ) {
                        // FIFA Card Composable scaled down for Grid listing
                        FifaCardView(
                            card = card,
                            scaleFactor = 0.55f,
                            elevation = 3.dp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Quick action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onEditClick(card) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Card",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { onDeleteClick(card) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Card",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardCreatorScreen(
    viewModel: FifaCardViewModel,
    onSaved: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Collect states
    val playerName by viewModel.playerName.collectAsStateWithLifecycle()
    val rating by viewModel.rating.collectAsStateWithLifecycle()
    val position by viewModel.position.collectAsStateWithLifecycle()
    val nationality by viewModel.nationality.collectAsStateWithLifecycle()
    val club by viewModel.club.collectAsStateWithLifecycle()
    val pace by viewModel.pace.collectAsStateWithLifecycle()
    val shooting by viewModel.shooting.collectAsStateWithLifecycle()
    val passing by viewModel.passing.collectAsStateWithLifecycle()
    val dribbling by viewModel.dribbling.collectAsStateWithLifecycle()
    val defending by viewModel.defending.collectAsStateWithLifecycle()
    val physicality by viewModel.physicality.collectAsStateWithLifecycle()
    val cardType by viewModel.cardType.collectAsStateWithLifecycle()
    val imageUri by viewModel.imageUri.collectAsStateWithLifecycle()
    val editingId by viewModel.editingCardId.collectAsStateWithLifecycle()

    val isGK = position.uppercase() == "GK"

    // Photo picker setup
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Take persistable permissions if possible, or save Uri string
            try {
                val flag = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
            } catch (e: Exception) {
                // Ignore if not direct content provider Uri
            }
            viewModel.imageUri.value = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Live Preview Header Banner ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            Color.Transparent
                        )
                    )
                )
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LIVE CARD PREVIEW",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Instantiate live rendering Card
                val liveCard = FifaCard(
                    playerName = playerName.ifBlank { "PLAYER NAME" },
                    rating = rating.toIntOrNull() ?: 85,
                    position = position.ifBlank { "ST" },
                    nationality = nationality,
                    club = club,
                    pace = pace.toIntOrNull() ?: 80,
                    shooting = shooting.toIntOrNull() ?: 80,
                    passing = passing.toIntOrNull() ?: 80,
                    dribbling = dribbling.toIntOrNull() ?: 80,
                    defending = defending.toIntOrNull() ?: 50,
                    physicality = physicality.toIntOrNull() ?: 70,
                    cardType = cardType,
                    imageUri = imageUri
                )

                FifaCardView(card = liveCard, scaleFactor = 0.85f)
            }
        }

        // --- Customization Form Fields ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Player Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Name
            OutlinedTextField(
                value = playerName,
                onValueChange = { viewModel.playerName.value = it },
                label = { Text("Player Name") },
                placeholder = { Text("e.g. Ronaldinho") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating & Position in Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Rating
                OutlinedTextField(
                    value = rating,
                    onValueChange = { input ->
                        if (input.isEmpty() || (input.toIntOrNull() != null && input.toInt() <= 99)) {
                            viewModel.rating.value = input
                        }
                    },
                    label = { Text("Rating (1-99)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp)
                )

                // Position Dropdown / Quick choice
                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = position,
                        onValueChange = { viewModel.position.value = it },
                        label = { Text("Position") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        val positions = listOf("ST", "CF", "LW", "RW", "CAM", "CM", "CDM", "CB", "LB", "RB", "GK")
                        positions.forEach { pos ->
                            DropdownMenuItem(
                                text = { Text(pos) },
                                onClick = {
                                    viewModel.position.value = pos
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Upload Photo Row
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Upload",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Player Portrait",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (imageUri != null) "Custom Photo Linked" else "Default Outline Silhouette Active",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row {
                        if (imageUri != null) {
                            IconButton(
                                onClick = { viewModel.imageUri.value = null },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Clear Photo",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (imageUri != null) "Change" else "Upload")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nation & Club Selection
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Nation
                OutlinedTextField(
                    value = nationality,
                    onValueChange = { viewModel.nationality.value = it },
                    label = { Text("Nationality (or flag)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Text(getCountryEmoji(nationality), fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                    }
                )

                // Club
                OutlinedTextField(
                    value = club,
                    onValueChange = { viewModel.club.value = it },
                    label = { Text("Club Crest") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Theme Selection ---
            Text(
                text = "Card Theme Style",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(FifaCardType.values()) { type ->
                    val isSelected = cardType == type.idName
                    Card(
                        modifier = Modifier
                            .width(130.dp)
                            .clickable { viewModel.cardType.value = type.idName }
                            .border(
                                width = 2.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = type.backgroundColors.first()
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type.displayName,
                                color = type.textColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Tactical Stats Customization (1 to 99 Sliders) ---
            Text(
                text = "Player Attributes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AttributeSlider(
                label = if (isGK) "Diving (DIV)" else "Pace (PAC)",
                value = pace.toIntOrNull() ?: 80,
                onValueChange = { viewModel.pace.value = it.toString() }
            )

            AttributeSlider(
                label = if (isGK) "Handling (HAN)" else "Shooting (SHO)",
                value = shooting.toIntOrNull() ?: 80,
                onValueChange = { viewModel.shooting.value = it.toString() }
            )

            AttributeSlider(
                label = if (isGK) "Kicking (KIC)" else "Passing (PAS)",
                value = passing.toIntOrNull() ?: 80,
                onValueChange = { viewModel.passing.value = it.toString() }
            )

            AttributeSlider(
                label = if (isGK) "Reflexes (REF)" else "Dribbling (DRI)",
                value = dribbling.toIntOrNull() ?: 80,
                onValueChange = { viewModel.dribbling.value = it.toString() }
            )

            AttributeSlider(
                label = if (isGK) "Speed (SPD)" else "Defending (DEF)",
                value = defending.toIntOrNull() ?: 50,
                onValueChange = { viewModel.defending.value = it.toString() }
            )

            AttributeSlider(
                label = if (isGK) "Positioning (POS)" else "Physicality (PHY)",
                value = physicality.toIntOrNull() ?: 75,
                onValueChange = { viewModel.physicality.value = it.toString() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Actions: Save / Reset ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.clearForm() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Form")
                }

                Button(
                    onClick = {
                        viewModel.saveCard {
                            onSaved()
                        }
                    },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (editingId != null) Icons.Default.Save else Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (editingId != null) "Update Card" else "Save Card")
                }
            }
        }
    }
}

@Composable
fun AttributeSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = value.toString(),
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..99f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Fullscreen-style high fidelity details modal for reviewing FUT cards
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CardDetailModal(
    card: FifaCard,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(enabled = false) { } // prevent click-through dismissal
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Card Spotlight",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display fully styled card at 1.0x scale
            FifaCardView(card = card, scaleFactor = 1.0f)

            Spacer(modifier = Modifier.height(24.dp))

            // Action row inside detail view
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDelete()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        onEdit()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Card", fontSize = 13.sp)
                }
            }
        }
    }
}
