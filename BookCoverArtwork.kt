package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BookCoverSwatches
import com.example.ui.theme.PlayfairDisplayFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatSom(amount: Long): String {
    if (amount <= 0L) return "Bepul"
    val raw = amount.toString()
    val grouped = buildString {
        raw.forEachIndexed { index, ch ->
            if (index > 0 && (raw.length - index) % 3 == 0) {
                append(' ')
            }
            append(ch)
        }
    }
    return "$grouped so'm"
}

fun formatUzbekDateTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.US)
    return formatter.format(Date(timestamp))
}

@Composable
fun BookCoverArtwork(
    title: String,
    author: String,
    coverArtKey: String,
    coverPaletteIndex: Int,
    modifier: Modifier = Modifier,
    compactText: Boolean = false
) {
    val shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 12.dp, bottomEnd = 12.dp)
    val imageRes: Int? = when (coverArtKey) {
        "otkan_kunlar" -> R.drawable.img_cover_otkan_kunlar
        "alximik" -> R.drawable.img_cover_alximik
        "atom_odatlar" -> R.drawable.img_cover_atom_odatlar
        else -> null
    }

    val baseSwatch = when (coverPaletteIndex % 6) {
        0 -> BookCoverSwatches.DeepRuby
        1 -> BookCoverSwatches.RoyalIndigo
        2 -> BookCoverSwatches.ForestMoss
        3 -> BookCoverSwatches.TerracottaClay
        4 -> BookCoverSwatches.PlumVelvet
        else -> BookCoverSwatches.MidnightSlate
    }

    Box(
        modifier = modifier
            .shadow(elevation = 6.dp, shape = shape)
            .clip(shape)
            .background(baseSwatch)
            .border(
                width = 0.75.dp,
                color = BookCoverSwatches.GoldFoil.copy(alpha = 0.38f),
                shape = shape
            )
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "$title muqovasi",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Subtle editorial gradient for crisp title legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.78f)
                            )
                        )
                    )
            )
        } else {
            // Clothbound collector's edition geometric Samarkand ornament
            Canvas(modifier = Modifier.fillMaxSize()) {
                val pad = size.minDimension * 0.08f
                drawRect(
                    color = BookCoverSwatches.GoldFoil.copy(alpha = 0.35f),
                    topLeft = Offset(pad, pad),
                    size = androidx.compose.ui.geometry.Size(
                        width = (size.width - pad * 2).coerceAtLeast(1f),
                        height = (size.height - pad * 2).coerceAtLeast(1f)
                    ),
                    style = Stroke(width = 1.2.dp.toPx())
                )
                val center = Offset(size.width * 0.52f, size.height * 0.36f)
                val radius = size.minDimension * 0.22f
                drawCircle(
                    color = BookCoverSwatches.GoldFoil.copy(alpha = 0.22f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.4.dp.toPx())
                )
                drawCircle(
                    color = BookCoverSwatches.GoldFoil.copy(alpha = 0.14f),
                    radius = radius * 0.65f,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }

        // Tactile book spine highlight on left edge
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(8.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.38f),
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Cover typography overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = if (compactText) 10.dp else 14.dp,
                    end = if (compactText) 8.dp else 12.dp,
                    top = if (compactText) 8.dp else 12.dp,
                    bottom = if (compactText) 8.dp else 12.dp
                ),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontFamily = PlayfairDisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = if (compactText) 12.sp else 15.sp,
                lineHeight = if (compactText) 15.sp else 19.sp,
                color = BookCoverSwatches.CreamFoil,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = author,
                style = MaterialTheme.typography.labelSmall,
                fontSize = if (compactText) 9.sp else 10.sp,
                color = BookCoverSwatches.GoldFoil,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
