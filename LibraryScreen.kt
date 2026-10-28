package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.BookEntity
import com.example.data.local.BookmarkEntity
import com.example.ui.LibraryUiState
import com.example.ui.components.BookCoverArtwork
import com.example.ui.components.formatUzbekDateTime

@Composable
fun LibraryScreen(
    state: LibraryUiState,
    onOpenReader: (String, Int) -> Unit,
    onOpenBookDetail: (String) -> Unit,
    onDeleteBookmark: (Long) -> Unit,
    onBrowseStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabLabels = listOf(
        "${stringResource(R.string.library_tab_reading)} (${state.currentlyReading.size})",
        "${stringResource(R.string.library_tab_unlocked)} (${state.unlockedBooks.size})",
        "${stringResource(R.string.library_tab_favorites)} (${state.favoriteBooks.size})",
        "${stringResource(R.string.library_tab_bookmarks)} (${state.bookmarks.size})"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("library_screen")
    ) {
        Text(
            text = stringResource(R.string.library_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            tabLabels.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    modifier = Modifier.testTag("library_tab_$index")
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    if (state.currentlyReading.isEmpty()) {
                        item {
                            EmptyLibraryCard(
                                message = stringResource(R.string.library_empty_reading),
                                onActionClick = onBrowseStore
                            )
                        }
                    } else {
                        items(state.currentlyReading, key = { "read_${it.id}" }) { book ->
                            LibraryBookRow(
                                book = book,
                                showProgress = true,
                                onCardClick = { onOpenBookDetail(book.id) },
                                onReadClick = { onOpenReader(book.id, book.currentChapterNumber) }
                            )
                        }
                    }
                }
                1 -> {
                    items(state.unlockedBooks, key = { "unl_${it.id}" }) { book ->
                        LibraryBookRow(
                            book = book,
                            showProgress = book.readingProgressPercent > 0,
                            onCardClick = { onOpenBookDetail(book.id) },
                            onReadClick = { onOpenReader(book.id, book.currentChapterNumber) }
                        )
                    }
                }
                2 -> {
                    if (state.favoriteBooks.isEmpty()) {
                        item {
                            EmptyLibraryCard(
                                message = stringResource(R.string.library_empty_favorites),
                                onActionClick = onBrowseStore
                            )
                        }
                    } else {
                        items(state.favoriteBooks, key = { "fav_${it.id}" }) { book ->
                            LibraryBookRow(
                                book = book,
                                showProgress = false,
                                onCardClick = { onOpenBookDetail(book.id) },
                                onReadClick = { onOpenReader(book.id, book.currentChapterNumber) }
                            )
                        }
                    }
                }
                3 -> {
                    if (state.bookmarks.isEmpty()) {
                        item {
                            EmptyLibraryCard(
                                message = stringResource(R.string.library_empty_bookmarks),
                                onActionClick = onBrowseStore
                            )
                        }
                    } else {
                        items(state.bookmarks, key = { "bm_${it.id}" }) { bm ->
                            BookmarkCard(
                                bookmark = bm,
                                onOpenChapter = { onOpenReader(bm.bookId, bm.chapterNumber) },
                                onDelete = { onDeleteBookmark(bm.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryBookRow(
    book: BookEntity,
    showProgress: Boolean,
    onCardClick: () -> Unit,
    onReadClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    .width(68.dp)
                    .height(96.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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
                if (showProgress) {
                    LinearProgressIndicator(
                        progress = { (book.readingProgressPercent / 100f).coerceIn(0.05f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = "${book.currentChapterNumber}-bob • ${book.readingProgressPercent}% o'qildi",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            FilledTonalButton(
                onClick = onReadClick,
                modifier = Modifier.testTag("library_read_${book.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("O'qish")
            }
        }
    }
}

@Composable
private fun BookmarkCard(
    bookmark: BookmarkEntity,
    onOpenChapter: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenChapter),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${bookmark.bookTitle} • ${bookmark.chapterTitle}",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "O'chirish",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Text(
                text = "\"${bookmark.quoteSnippet}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (bookmark.note.isNotBlank()) {
                Text(
                    text = "Eslatma: ${bookmark.note}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = formatUzbekDateTime(bookmark.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun EmptyLibraryCard(
    message: String,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onActionClick) {
                Text(stringResource(R.string.cart_browse_store))
            }
        }
    }
}
