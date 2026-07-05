package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.FifaCard

// Authentic FIFA FUT Card Shield Shape path
val FutCardShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(w * 0.15f, 0f) // Top-left flat start
            lineTo(w * 0.85f, 0f) // Top-right flat end
            lineTo(w, h * 0.12f) // Shoulder right
            lineTo(w, h * 0.73f) // Right vertical edge bottom
            lineTo(w * 0.5f, h)   // Bottom center tip
            lineTo(0f, h * 0.73f) // Left vertical edge bottom
            lineTo(0f, h * 0.12f) // Shoulder left
            close()
        }
        return Outline.Generic(path)
    }
}

enum class FifaCardType(
    val idName: String,
    val displayName: String,
    val backgroundColors: List<Color>,
    val textColor: Color,
    val statLabelColor: Color,
    val borderColor: Color,
    val dividerColor: Color
) {
    GOLD_RARE(
        "Gold Rare",
        "Gold Rare",
        listOf(Color(0xFFFFF1A6), Color(0xFFE5B03E), Color(0xFF926514)),
        Color(0xFF332200),
        Color(0xFF6B4D00),
        Color(0xFFFFD54F),
        Color(0x73926514)
    ),
    TOTY(
        "Team of the Year",
        "Team of the Year (TOTY)",
        listOf(Color(0xFF03102C), Color(0xFF0C2456), Color(0xFF1B4F9B)),
        Color(0xFFF5E3A0),
        Color(0xFF9EBAF2),
        Color(0xFFECC45C),
        Color(0x80ECC45C)
    ),
    ICON(
        "Icon",
        "FUT Icon",
        listOf(Color(0xFFFFFFFF), Color(0xFFF3ECE0), Color(0xFFCFBF96)),
        Color(0xFF282114),
        Color(0xFF706144),
        Color(0xFFC29C51),
        Color(0x66C29C51)
    ),
    FUT_BIRTHDAY(
        "FUT Birthday",
        "FUT Birthday",
        listOf(Color(0xFFFF0C85), Color(0xFF880E4F), Color(0xFF311B92)),
        Color(0xFFEEFF41),
        Color(0xFFF1F8E9),
        Color(0xFFFF4081),
        Color(0x66EEFF41)
    ),
    CENTURIONS(
        "Centurions",
        "Centurions",
        listOf(Color(0xFF3E0101), Color(0xFF6F0000), Color(0xFF140000)),
        Color(0xFFFEECE0),
        Color(0xFFFF9F1C),
        Color(0xFF9B2226),
        Color(0x66FFFF9F)
    )
}

@Composable
fun FifaCardView(
    card: FifaCard,
    modifier: Modifier = Modifier,
    scaleFactor: Float = 1.0f,
    elevation: Dp = 8.dp
) {
    val context = LocalContext.current
    val cardType = FifaCardType.values().firstOrNull { it.idName == card.cardType } ?: FifaCardType.GOLD_RARE
    val isGK = card.position.uppercase() == "GK"

    val baseWidth = 260.dp * scaleFactor
    val baseHeight = 380.dp * scaleFactor

    Surface(
        modifier = modifier
            .width(baseWidth)
            .height(baseHeight),
        shape = FutCardShape,
        color = Color.Transparent,
        shadowElevation = elevation * scaleFactor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(FutCardShape)
                .background(
                    Brush.verticalGradient(
                        colors = cardType.backgroundColors
                    )
                )
                .border(
                    width = (3.dp * scaleFactor),
                    brush = Brush.verticalGradient(
                        colors = listOf(cardType.borderColor, cardType.borderColor.copy(alpha = 0.4f))
                    ),
                    shape = FutCardShape
                )
        ) {
            // Shiny futuristic background pattern overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw decorative abstract lines
                val path1 = Path().apply {
                    moveTo(w * 0.1f, 0f)
                    lineTo(w * 0.3f, h)
                    lineTo(w * 0.5f, h)
                    lineTo(w * 0.2f, 0f)
                    close()
                }
                drawPath(path1, color = Color.White.copy(alpha = 0.05f))

                val path2 = Path().apply {
                    moveTo(w * 0.8f, 0f)
                    lineTo(w * 0.5f, h)
                    lineTo(w * 0.65f, h)
                    lineTo(w * 0.95f, 0f)
                    close()
                }
                drawPath(path2, color = Color.White.copy(alpha = 0.03f))

                // Subtle light highlight top
                drawCircle(
                    color = Color.White.copy(alpha = 0.15f),
                    radius = w * 0.4f,
                    center = Offset(w * 0.5f, 0f)
                )
            }

            // --- Upper Section: Rating, Position, Nation, Club, Portrait ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f)
                    .padding(top = 22.dp * scaleFactor)
            ) {
                // Column on the left: Rating, Position, Flag, Club Crest
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp * scaleFactor),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Rating
                    Text(
                        text = card.rating.toString(),
                        color = cardType.textColor,
                        fontSize = (38.sp * scaleFactor),
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        lineHeight = (38.sp * scaleFactor)
                    )

                    // Position
                    Text(
                        text = card.position.uppercase(),
                        color = cardType.textColor,
                        fontSize = (15.sp * scaleFactor),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        lineHeight = (15.sp * scaleFactor)
                    )

                    Spacer(modifier = Modifier.height(6.dp * scaleFactor))

                    // Nation Flag (Dynamic Emoji style flags or standard text)
                    Text(
                        text = getCountryEmoji(card.nationality),
                        fontSize = (24.sp * scaleFactor),
                        lineHeight = (24.sp * scaleFactor)
                    )

                    Spacer(modifier = Modifier.height(6.dp * scaleFactor))

                    // Club Logo representation
                    Text(
                        text = getClubEmoji(card.club),
                        fontSize = (24.sp * scaleFactor),
                        lineHeight = (24.sp * scaleFactor)
                    )
                }

                // Player Portrait (Fades out at the bottom)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp * scaleFactor, bottom = 2.dp * scaleFactor)
                        .width(160.dp * scaleFactor)
                        .fillMaxHeight()
                ) {
                    if (card.imageUri != null) {
                        AsyncImage(
                            model = Uri.parse(card.imageUri),
                            contentDescription = "Player Portrait",
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithContent {
                                    drawContent()
                                    // Soft bottom fade blend overlay
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.05f)),
                                            startY = this.size.height * 0.6f,
                                            endY = this.size.height
                                        ),
                                        blendMode = BlendMode.DstIn
                                    )
                                },
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_default_player),
                            contentDescription = "Default Player Portrait",
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.05f)),
                                            startY = this.size.height * 0.6f,
                                            endY = this.size.height
                                        ),
                                        blendMode = BlendMode.DstIn
                                    )
                                },
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter
                        )
                    }
                }
            }

            // --- Bottom Section: Name, Stats ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp * scaleFactor),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Name (centered, uppercase, fits nicely)
                Text(
                    text = card.playerName.uppercase(),
                    color = cardType.textColor,
                    fontSize = (20.sp * scaleFactor),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp * scaleFactor)
                )

                Spacer(modifier = Modifier.height(4.dp * scaleFactor))

                // Stats Divider Line
                Box(
                    modifier = Modifier
                        .width(180.dp * scaleFactor)
                        .height(1.5.dp * scaleFactor)
                        .background(cardType.dividerColor)
                )

                Spacer(modifier = Modifier.height(8.dp * scaleFactor))

                // 2 Column Stats Grid
                Row(
                    modifier = Modifier
                        .width(210.dp * scaleFactor)
                        .padding(horizontal = 4.dp * scaleFactor),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column (PAC/DIV, SHO/HAN, PAS/KIC)
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.weight(1f)
                    ) {
                        StatRow(
                            value = card.pace,
                            label = if (isGK) "DIV" else "PAC",
                            textColor = cardType.textColor,
                            labelColor = cardType.statLabelColor,
                            scaleFactor = scaleFactor
                        )
                        Spacer(modifier = Modifier.height(2.dp * scaleFactor))
                        StatRow(
                            value = card.shooting,
                            label = if (isGK) "HAN" else "SHO",
                            textColor = cardType.textColor,
                            labelColor = cardType.statLabelColor,
                            scaleFactor = scaleFactor
                        )
                        Spacer(modifier = Modifier.height(2.dp * scaleFactor))
                        StatRow(
                            value = card.passing,
                            label = if (isGK) "KIC" else "PAS",
                            textColor = cardType.textColor,
                            labelColor = cardType.statLabelColor,
                            scaleFactor = scaleFactor
                        )
                    }

                    // Vertical center divider
                    Box(
                        modifier = Modifier
                            .height(55.dp * scaleFactor)
                            .width(1.5.dp * scaleFactor)
                            .background(cardType.dividerColor)
                    )

                    // Right Column (DRI/REF, DEF/SPD, PHY/POS)
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp * scaleFactor)
                    ) {
                        StatRow(
                            value = card.dribbling,
                            label = if (isGK) "REF" else "DRI",
                            textColor = cardType.textColor,
                            labelColor = cardType.statLabelColor,
                            scaleFactor = scaleFactor
                        )
                        Spacer(modifier = Modifier.height(2.dp * scaleFactor))
                        StatRow(
                            value = card.defending,
                            label = if (isGK) "SPD" else "DEF",
                            textColor = cardType.textColor,
                            labelColor = cardType.statLabelColor,
                            scaleFactor = scaleFactor
                        )
                        Spacer(modifier = Modifier.height(2.dp * scaleFactor))
                        StatRow(
                            value = card.physicality,
                            label = if (isGK) "POS" else "PHY",
                            textColor = cardType.textColor,
                            labelColor = cardType.statLabelColor,
                            scaleFactor = scaleFactor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(
    value: Int,
    label: String,
    textColor: Color,
    labelColor: Color,
    scaleFactor: Float
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = value.toString(),
            color = textColor,
            fontSize = (15.sp * scaleFactor),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.width(22.dp * scaleFactor),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(6.dp * scaleFactor))
        Text(
            text = label,
            color = labelColor,
            fontSize = (13.sp * scaleFactor),
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif
        )
    }
}

// Maps country names to standard flag emojis (supports flexible matching)
fun getCountryEmoji(country: String): String {
    return when (country.trim().lowercase()) {
        "argentina", "arg", "🇦🇷" -> "🇦🇷"
        "brazil", "bra", "🇧🇷" -> "🇧🇷"
        "portugal", "por", "🇵🇹" -> "🇵🇹"
        "germany", "ger", "🇩🇪" -> "🇩🇪"
        "france", "fra", "🇫🇷" -> "🇫🇷"
        "spain", "esp", "🇪🇸" -> "🇪🇸"
        "england", "eng", "🇬🇧" -> "🇬🇧"
        "italy", "ita", "🇮🇹" -> "🇮🇹"
        "netherlands", "ned", "🇳🇱" -> "🇳🇱"
        "belgium", "bel", "🇧🇪" -> "🇧🇪"
        "croatia", "cro", "🇭🇷" -> "🇭🇷"
        "uruguay", "uru", "🇺🇾" -> "🇺🇾"
        "usa", "united states", "🇺🇸" -> "🇺🇸"
        "canada", "can", "🇨🇦" -> "🇨🇦"
        "mexico", "mex", "🇲🇽" -> "🇲🇽"
        "japan", "jpn", "🇯🇵" -> "🇯🇵"
        "korea", "south korea", "🇰🇷" -> "🇰🇷"
        "egypt", "egy", "🇪🇬" -> "🇪🇬"
        "senegal", "sen", "🇸🇳" -> "🇸🇳"
        "morocco", "mar", "🇲🇦" -> "🇲🇦"
        else -> "🌎" // Fallback world emoji
    }
}

// Maps club names or short descriptions to representative emojis
fun getClubEmoji(club: String): String {
    return when (club.trim().lowercase()) {
        "madrid", "real madrid", "white", "🛡️" -> "🛡️"
        "barcelona", "barca", "🔴🔵" -> "🔴🔵"
        "manchester united", "man utd", "red devils", "😈" -> "😈"
        "manchester city", "man city", "citizens", "🩵" -> "🩵"
        "liverpool", "reds", "🦁" -> "🦁"
        "arsenal", "gunners", "🔫" -> "🔫"
        "bayern", "bayern munich", "🔴" -> "🔴"
        "dortmund", "bvb", "🐝" -> "🐝"
        "psg", "paris", "🗼" -> "🗼"
        "juventus", "juve", "🦓" -> "🦓"
        "ac milan", "milan", "😈🔴" -> "🔴🖤"
        "inter", "inter milan", "🔵🖤" -> "🔵🖤"
        "chelsea", "blues", "🦁🔵" -> "🦁🔵"
        "tottenham", "spurs", "🐓" -> "🐓"
        "ajax", "❌❌❌" -> "🛡️"
        "boca", "boca juniors", "🇺🇦" -> "🇺🇦"
        "al nassr", "nassr", "🟡" -> "🟡"
        "inter miami", "miami", "🦩" -> "🦩"
        else -> "⚽" // Fallback soccer ball
    }
}
