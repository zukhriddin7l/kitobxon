package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.BookEntity
import com.example.data.local.BookFormats
import com.example.data.seed.CatalogSeedBooks
import com.example.ui.BookSortOption
import com.example.ui.BookstoreViewModel
import com.example.ui.StoreUiState
import com.example.ui.components.BookCoverArtwork
import com.example.ui.components.formatSom

@Composable
fun StoreScreen(
    uiState: StoreUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSortChange: (BookSortOption) -> Unit,
    onResetFilters: () -> Unit,
    onBookClick: (String) -> Unit,
    onReadBookClick: (String, Int) -> Unit,
    onQuickAddPrintToCart: (BookEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        StoreUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.testTag("store_loading"))
            }
        }
        is StoreUiState.Ready -> {
            StoreReadyContent(
                state = uiState,
                onSearchQueryChange = onSearchQueryChange,
                onCategorySelect = onCategorySelect,
                onSortChange = onSortChange,
                onResetFilters = onResetFilters,
                onBookClick = onBookClick,
                onReadBookClick = onReadBookClick,
                onQuickAddPrintToCart = onQuickAddPrintToCart,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun StoreReadyContent(
    state: StoreUiState.Ready,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSortChange: (BookSortOption) -> Unit,
    onResetFilters: () -> Unit,
    onBookClick: (String) -> Unit,
    onReadBookClick: (String, Int) -> Unit,
    onQuickAddPrintToCart: (BookEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = remember {
        listOf(
            BookstoreViewModel.CATEGORY_ALL to R.string.cat_all,
            CatalogSeedBooks.CAT_UZBEK to R.string.cat_uzbek,
            CatalogSeedBooks.CAT_WORLD to R.string.cat_world,
            CatalogSeedBooks.CAT_SELF_DEV to R.string.cat_self_dev,
            CatalogSeedBooks.CAT_SCIENCE_KIDS to R.string.cat_science_kids
        )
    }

    var sortMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("store_book_list"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header + Search
        item(key = "header") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.store_greeting_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Tezkor dostavka",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("store_search_input"),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.store_search_placeholder),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier.testTag("store_clear_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.store_clear_search)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        // Editorial Hero Banner (only when not actively filtering by text)
        if (state.searchQuery.isBlank() && state.selectedCategory == BookstoreViewModel.CATEGORY_ALL) {
            item(key = "hero_banner") {
                StoreHeroBanner(
                    onExploreFeatured = { onBookClick("otkan_kunlar") },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Continue Reading Card if user started reading
        state.continueReadingBook?.let { readingBook ->
            if (state.searchQuery.isBlank()) {
                item(key = "continue_reading") {
                    ContinueReadingCard(
                        book = readingBook,
                        onResume = {
                            onReadBookClick(readingBook.id, readingBook.currentChapterNumber)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        // Category Filter Chips
        item(key = "categories") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { (catKey, labelRes) ->
                    val selected = state.selectedCategory == catKey
                    FilterChip(
                        selected = selected,
                        onClick = { onCategorySelect(catKey) },
                        label = {
                            Text(
                                text = stringResource(labelRes),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("category_chip_${catKey.lowercase().replace(" ", "_")}")
                    )
                }
            }
        }

        // Horizontal Bestsellers Shelf (when on All category and empty search)
        if (state.searchQuery.isBlank() && state.selectedCategory == BookstoreViewModel.CATEGORY_ALL) {
            item(key = "bestsellers_shelf") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.store_bestsellers),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(state.bestsellers, key = { "best_${it.id}" }) { book ->
                            BestsellerShelfCard(
                                book = book,
                                onClick = { onBookClick(book.id) }
                            )
                        }
                    }
                }
            }
        }

        // Section Header + Sort Dropdown
        item(key = "catalog_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${stringResource(R.string.store_all_books)} (${state.filteredBooks.size})",
                    style = MaterialTheme.typography.headlineSmall
                )
                Box {
                    AssistChip(
                        onClick = { sortMenuExpanded = true },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.SwapVert,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = {
                            val sortLabel = when (state.sortOption) {
                                BookSortOption.POPULAR -> stringResource(R.string.sort_popular)
                                BookSortOption.PRICE_LOW_TO_HIGH -> stringResource(R.string.sort_price_low)
                                BookSortOption.PRICE_HIGH_TO_LOW -> stringResource(R.string.sort_price_high)
                                BookSortOption.HIGHEST_RATED -> stringResource(R.string.sort_rating)
                            }
                            Text(text = sortLabel)
                        },
                        modifier = Modifier.testTag("store_sort_chip")
                    )
                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.sort_popular)) },
                            onClick = {
                                onSortChange(BookSortOption.POPULAR)
                                sortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.sort_price_low)) },
                            onClick = {
                                onSortChange(BookSortOption.PRICE_LOW_TO_HIGH)
                                sortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.sort_price_high)) },
                            onClick = {
                                onSortChange(BookSortOption.PRICE_HIGH_TO_LOW)
                                sortMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.sort_rating)) },
                            onClick = {
                                onSortChange(BookSortOption.HIGHEST_RATED)
                                sortMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Filtered Catalog List or Empty Search State
        if (state.filteredBooks.isEmpty()) {
            item(key = "empty_search") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = stringResource(R.string.store_empty_search_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.store_empty_search_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onResetFilters,
                            modifier = Modifier.testTag("store_reset_filters_button")
                        ) {
                            Text(stringResource(R.string.store_reset_filters))
                        }
                    }
                }
            }
        } else {
            items(state.filteredBooks, key = { it.id }) { book ->
                CatalogBookCard(
                    book = book,
                    onCardClick = { onBookClick(book.id) },
                    onReadClick = { onReadBookClick(book.id, book.currentChapterNumber) },
                    onAddPrintToCart = { onQuickAddPrintToCart(book) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun StoreHeroBanner(
    onExploreFeatured: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(196.dp)
            .clickable(onClick = onExploreFeatured)
            .testTag("store_hero_banner"),
        shape = MaterialTheme.shapes.large
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.img_store_hero),
                contentDescription = stringResource(R.string.store_hero_title),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.82f),
                                Color.Black.copy(alpha = 0.56f),
                                Color.Black.copy(alpha = 0.22f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                ) {
                    Text(
                        text = stringResource(R.string.store_hero_badge),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.store_hero_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        maxLines = 2
                    )
                    Text(
                        text = stringResource(R.string.store_hero_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun ContinueReadingCard(
    book: BookEntity,
    onResume: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onResume)
            .testTag("continue_reading_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BookCoverArtwork(
                title = book.title,
                author = book.author,
                coverArtKey = book.coverArtKey,
                coverPaletteIndex = book.coverPaletteIndex,
                compactText = true,
                modifier = Modifier
                    .width(56.dp)
                    .height(78.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = stringResource(R.string.store_continue_reading),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                LinearProgressIndicator(
                    progress = { (book.readingProgressPercent / 100f).coerceIn(0.05f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface
                )
                Text(
                    text = "${book.currentChapterNumber}-bob • ${book.readingProgressPercent}% o'qildi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            FilledTonalButton(
                onClick = onResume,
                modifier = Modifier.testTag("resume_reading_button")
            ) {
                Text(stringResource(R.string.store_resume_btn))
            }
        }
    }
}

@Composable
private fun BestsellerShelfCard(
    book: BookEntity,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(142.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(4.dp)
            .testTag("bestseller_card_${book.id}"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box {
            BookCoverArtwork(
                title = book.title,
                author = book.author,
                coverArtKey = book.coverArtKey,
                coverPaletteIndex = book.coverPaletteIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
            )
            if (book.isFreeEbook || book.isEbookUnlocked) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomEnd = 10.dp, topStart = 4.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = if (book.isFreeEbook) "Bepul e-kitob" else "Ochiq",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
        Text(
            text = book.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = formatSom(book.printPriceSom),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun CatalogBookCard(
    book: BookEntity,
    onCardClick: () -> Unit,
    onReadClick: () -> Unit,
    onAddPrintToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("book_card_${book.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BookCoverArtwork(
                title = book.title,
                author = book.author,
                coverArtKey = book.coverArtKey,
                coverPaletteIndex = book.coverPaletteIndex,
                compactText = true,
                modifier = Modifier
                    .width(92.dp)
                    .height(132.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = book.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "${book.rating}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatSom(book.printPriceSom),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        color = if (book.isFreeEbook || book.isEbookUnlocked) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.tertiaryContainer
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (book.isFreeEbook) {
                                "E-kitob: Bepul"
                            } else if (book.isEbookUnlocked) {
                                "E-kitob: Ochiq"
                            } else {
                                "E-kitob: ${formatSom(book.ebookPriceSom)}"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (book.isFreeEbook || book.isEbookUnlocked) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onReadClick,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_read_${book.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "O'qish",
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1
                        )
                    }
                    Button(
                        onClick = onAddPrintToCart,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_cart_${book.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Savatga",
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
