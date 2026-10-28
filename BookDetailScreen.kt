package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.BookEntity
import com.example.data.local.BookFormats
import com.example.data.local.BookWithChapters
import com.example.data.local.ChapterEntity
import com.example.data.local.ReviewEntity
import com.example.ui.BookstoreViewModel
import com.example.ui.components.BookCoverArtwork
import com.example.ui.components.formatSom
import com.example.ui.components.formatUzbekDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    viewModel: BookstoreViewModel,
    onBack: () -> Unit,
    onOpenReader: (String, Int) -> Unit,
    onGoToCart: () -> Unit
) {
    BackHandler(onBack = onBack)

    val bookWithChapters by remember(bookId) {
        viewModel.observeBookWithChapters(bookId)
    }.collectAsStateWithLifecycle(initialValue = null)

    val reviews by remember(bookId) {
        viewModel.observeReviews(bookId)
    }.collectAsStateWithLifecycle(initialValue = emptyList())

    val cartSummary by viewModel.cartSummaryState.collectAsStateWithLifecycle()

    var selectedFormat by remember { mutableStateOf(BookFormats.PRINT) }
    var showReviewDialog by remember { mutableStateOf(false) }

    val detail = bookWithChapters
    if (detail == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val book = detail.book
    val sortedChapters = remember(detail.chapters) {
        detail.chapters.sortedBy { it.chapterNumber }
    }
    val isInCart = cartSummary.items.any {
        it.cartItem.bookId == book.id && it.cartItem.format == selectedFormat
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = book.category,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.detail_back)
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { viewModel.toggleFavorite(book) },
                    modifier = Modifier.testTag("detail_favorite_button")
                ) {
                    Icon(
                        imageVector = if (book.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(
                            if (book.isFavorite) R.string.detail_favorite_remove else R.string.detail_favorite_add
                        ),
                        tint = if (book.isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Cover + Primary Info
            item(key = "hero_info") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BookCoverArtwork(
                        title = book.title,
                        author = book.author,
                        coverArtKey = book.coverArtKey,
                        coverPaletteIndex = book.coverPaletteIndex,
                        modifier = Modifier
                            .width(126.dp)
                            .height(178.dp)
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = book.author,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = book.publisher,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${book.rating} (${book.reviewCount} ta fikr)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }

            // Metadata Stat Strip
            item(key = "stats") {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BookStatColumn(stringResource(R.string.detail_pages_label), "${book.pageCount} bet")
                        BookStatColumn(stringResource(R.string.detail_year_label), "${book.publishYear}")
                        BookStatColumn(stringResource(R.string.detail_lang_label), book.language)
                    }
                }
            }

            // Format Selector (Print + Delivery vs E-book)
            item(key = "format_selector") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FormatOptionCard(
                        selected = selectedFormat == BookFormats.PRINT,
                        title = stringResource(R.string.format_print_title),
                        subtitle = stringResource(R.string.format_print_subtitle),
                        priceText = formatSom(book.printPriceSom),
                        leadingIcon = Icons.Default.LocalShipping,
                        onClick = { selectedFormat = BookFormats.PRINT },
                        testTag = "format_option_print"
                    )
                    FormatOptionCard(
                        selected = selectedFormat == BookFormats.EBOOK,
                        title = stringResource(R.string.format_ebook_title),
                        subtitle = if (book.isFreeEbook || book.isEbookUnlocked) {
                            "To'liq barcha boblar o'qish uchun ochiq"
                        } else {
                            stringResource(R.string.format_ebook_subtitle)
                        },
                        priceText = if (book.isFreeEbook || book.isEbookUnlocked) {
                            stringResource(R.string.price_free)
                        } else {
                            formatSom(book.ebookPriceSom)
                        },
                        leadingIcon = Icons.Default.AutoStories,
                        onClick = { selectedFormat = BookFormats.EBOOK },
                        testTag = "format_option_ebook"
                    )
                }
            }

            // Action Buttons (Read Now + Add to Cart)
            item(key = "primary_actions") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onOpenReader(book.id, book.currentChapterNumber) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("detail_read_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (book.isFreeEbook || book.isEbookUnlocked) {
                                stringResource(R.string.btn_read_now)
                            } else {
                                stringResource(R.string.btn_read_sample)
                            }
                        )
                    }

                    val canBuySelected = !(selectedFormat == BookFormats.EBOOK && (book.isFreeEbook || book.isEbookUnlocked))
                    if (canBuySelected) {
                        Button(
                            onClick = {
                                if (isInCart) {
                                    onGoToCart()
                                } else {
                                    viewModel.addToCart(book, selectedFormat)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("detail_cart_button")
                        ) {
                            Icon(
                                imageVector = if (isInCart) Icons.Default.CheckCircle else Icons.Default.AddShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(
                                    if (isInCart) R.string.btn_in_cart else R.string.btn_add_to_cart
                                )
                            )
                        }
                    }
                }
            }

            // Synopsis
            item(key = "synopsis") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.detail_synopsis_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = book.synopsis,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Table of Contents (Mundarija)
            item(key = "chapters_header") {
                Text(
                    text = "${stringResource(R.string.detail_chapters_title)} (${sortedChapters.size} bob)",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            items(sortedChapters, key = { it.id }) { chapter ->
                ChapterPreviewRow(
                    chapter = chapter,
                    isBookUnlocked = book.isFreeEbook || book.isEbookUnlocked,
                    onClick = { onOpenReader(book.id, chapter.chapterNumber) }
                )
            }

            // Reviews Section
            item(key = "reviews_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.detail_reviews_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    OutlinedButton(
                        onClick = { showReviewDialog = true },
                        modifier = Modifier.testTag("write_review_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.detail_write_review))
                    }
                }
            }
            items(reviews, key = { "rev_${it.id}" }) { review ->
                ReviewCard(review = review)
            }
        }
    }

    if (showReviewDialog) {
        WriteReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { name, rating, comment ->
                viewModel.submitReview(book, name, rating, comment)
                showReviewDialog = false
            }
        )
    }
}

@Composable
private fun BookStatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FormatOptionCard(
    selected: Boolean,
    title: String,
    subtitle: String,
    priceText: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = priceText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ChapterPreviewRow(
    chapter: ChapterEntity,
    isBookUnlocked: Boolean,
    onClick: () -> Unit
) {
    val canRead = isBookUnlocked || chapter.isFreePreview
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("chapter_row_${chapter.chapterNumber}"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "~${chapter.estimatedMinutes} daqiqa mutolaa",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (canRead) "O'qish" else "Yopiq",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (canRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
                Icon(
                    imageVector = if (canRead) Icons.Default.LockOpen else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (canRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ReviewCard(review: ReviewEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.reviewerName,
                    style = MaterialTheme.typography.titleSmall
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Text(
                text = formatUzbekDateTime(review.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun WriteReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.review_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.review_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star yulduz",
                                tint = if (star <= rating) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(stringResource(R.string.review_comment_label)) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(name, rating, comment) },
                enabled = comment.isNotBlank()
            ) {
                Text(stringResource(R.string.review_submit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.review_cancel))
            }
        }
    )
}
