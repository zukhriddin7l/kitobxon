package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.BookFormats
import com.example.data.local.ReaderSettingsEntity
import com.example.data.local.ReaderThemes
import com.example.ui.BookstoreViewModel
import com.example.ui.components.formatSom
import com.example.ui.theme.LoraReaderFontFamily
import com.example.ui.theme.PlayfairDisplayFontFamily
import com.example.ui.theme.PlusJakartaSansFontFamily
import com.example.ui.theme.ReaderPalettes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    bookId: String,
    initialChapter: Int,
    viewModel: BookstoreViewModel,
    onBack: () -> Unit,
    onGoToCart: () -> Unit
) {
    BackHandler(onBack = onBack)

    val bookWithChapters by remember(bookId) {
        viewModel.observeBookWithChapters(bookId)
    }.collectAsStateWithLifecycle(initialValue = null)

    val settings by viewModel.readerSettings.collectAsStateWithLifecycle()

    val detail = bookWithChapters
    if (detail == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val book = detail.book
    val chapters = remember(detail.chapters) {
        detail.chapters.sortedBy { it.chapterNumber }
    }
    if (chapters.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Boblar topilmadi")
        }
        return
    }

    var activeChapterNum by remember(book.id) {
        val start = if (initialChapter > 0) initialChapter else book.currentChapterNumber
        mutableIntStateOf(start.coerceIn(1, chapters.size))
    }

    val currentChapter = chapters.find { it.chapterNumber == activeChapterNum } ?: chapters.first()
    val isBookUnlocked = book.isFreeEbook || book.isEbookUnlocked
    val isChapterReadable = isBookUnlocked || currentChapter.isFreePreview

    val scrollState = rememberScrollState()
    var showTocSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showBookmarkDialog by remember { mutableStateOf(false) }

    LaunchedEffect(activeChapterNum) {
        scrollState.scrollTo(0)
        if (isChapterReadable) {
            viewModel.recordReadingProgress(book.id, activeChapterNum, chapters.size)
        }
    }

    val palette = when (settings.themeMode) {
        ReaderThemes.DAY -> ReaderPalettes.CrispDay
        ReaderThemes.SEPIA -> ReaderPalettes.Sepia
        ReaderThemes.NIGHT -> ReaderPalettes.Night
        else -> ReaderPalettes.Parchment
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .testTag("reader_screen_root")
    ) {
        // Reader Top Bar respecting statusBar insets
        Surface(
            color = palette.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("reader_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.detail_back),
                        tint = palette.onBackground
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = palette.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${activeChapterNum}/${chapters.size}-bob • ${book.author}",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.secondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = { showBookmarkDialog = true },
                    enabled = isChapterReadable,
                    modifier = Modifier.testTag("reader_bookmark_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = stringResource(R.string.reader_bookmark),
                        tint = palette.accent
                    )
                }
                IconButton(
                    onClick = { showTocSheet = true },
                    modifier = Modifier.testTag("reader_toc_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                        contentDescription = stringResource(R.string.reader_toc),
                        tint = palette.onBackground
                    )
                }
                IconButton(
                    onClick = { showSettingsSheet = true },
                    modifier = Modifier.testTag("reader_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.TextFields,
                        contentDescription = stringResource(R.string.reader_settings),
                        tint = palette.onBackground
                    )
                }
            }
        }

        LinearProgressIndicator(
            progress = { activeChapterNum.toFloat() / chapters.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = palette.accent,
            trackColor = palette.surface
        )

        // Reader Body Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = currentChapter.title,
                    fontFamily = PlayfairDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = (settings.fontSizeSp + 6).sp,
                    lineHeight = (settings.fontSizeSp + 12).sp,
                    color = palette.onBackground
                )

                HorizontalDivider(color = palette.secondaryText.copy(alpha = 0.25f))

                if (isChapterReadable) {
                    val bodyFont = if (settings.useSerifFont) {
                        LoraReaderFontFamily
                    } else {
                        PlusJakartaSansFontFamily
                    }
                    val lineMultiplier = if (settings.wideLineSpacing) 1.72f else 1.45f
                    Text(
                        text = currentChapter.content,
                        fontFamily = bodyFont,
                        fontSize = settings.fontSizeSp.sp,
                        lineHeight = (settings.fontSizeSp * lineMultiplier).sp,
                        color = palette.onBackground,
                        modifier = Modifier.testTag("reader_chapter_text")
                    )
                } else {
                    LockedChapterCard(
                        ebookPriceSom = book.ebookPriceSom,
                        printPriceSom = book.printPriceSom,
                        onBuyEbook = {
                            viewModel.addToCart(book, BookFormats.EBOOK)
                            onGoToCart()
                        },
                        onOrderPrint = {
                            viewModel.addToCart(book, BookFormats.PRINT)
                            onGoToCart()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Bottom Chapter Switcher respecting navigationBars insets
        Surface(
            color = palette.surface,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { if (activeChapterNum > 1) activeChapterNum-- },
                    enabled = activeChapterNum > 1,
                    modifier = Modifier.testTag("reader_prev_chapter_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.reader_prev_chapter))
                }

                Text(
                    text = "$activeChapterNum / ${chapters.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = palette.secondaryText
                )

                Button(
                    onClick = { if (activeChapterNum < chapters.size) activeChapterNum++ },
                    enabled = activeChapterNum < chapters.size,
                    modifier = Modifier.testTag("reader_next_chapter_button")
                ) {
                    Text(stringResource(R.string.reader_next_chapter))
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    if (showTocSheet) {
        ModalBottomSheet(onDismissRequest = { showTocSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.reader_toc),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                chapters.forEach { ch ->
                    val unlocked = isBookUnlocked || ch.isFreePreview
                    val isCurrent = ch.chapterNumber == activeChapterNum
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                activeChapterNum = ch.chapterNumber
                                showTocSheet = false
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCurrent) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ch.title,
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f)
                            )
                            if (!unlocked) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showSettingsSheet) {
        ModalBottomSheet(onDismissRequest = { showSettingsSheet = false }) {
            ReaderTypographySheet(
                settings = settings,
                onUpdate = { viewModel.updateReaderSettings(it) }
            )
        }
    }

    if (showBookmarkDialog) {
        var note by remember { mutableStateOf("") }
        val snippet = remember(currentChapter.content) {
            currentChapter.content.take(120).replace("\n", " ") + "…"
        }
        AlertDialog(
            onDismissRequest = { showBookmarkDialog = false },
            title = { Text(stringResource(R.string.bookmark_dialog_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "\"$snippet\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text(stringResource(R.string.bookmark_note_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveBookmark(
                            bookId = book.id,
                            bookTitle = book.title,
                            chapterNumber = currentChapter.chapterNumber,
                            chapterTitle = currentChapter.title,
                            quoteSnippet = snippet,
                            note = note
                        )
                        showBookmarkDialog = false
                    }
                ) {
                    Text(stringResource(R.string.review_submit))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookmarkDialog = false }) {
                    Text(stringResource(R.string.review_cancel))
                }
            }
        )
    }
}

@Composable
private fun LockedChapterCard(
    ebookPriceSom: Long,
    printPriceSom: Long,
    onBuyEbook: () -> Unit,
    onOrderPrint: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = stringResource(R.string.reader_locked_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = stringResource(R.string.reader_locked_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onBuyEbook,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reader_unlock_ebook_button")
            ) {
                Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("${stringResource(R.string.reader_unlock_ebook_btn)} (${formatSom(ebookPriceSom)})")
            }
            FilledTonalButton(
                onClick = onOrderPrint,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reader_order_print_button")
            ) {
                Text("${stringResource(R.string.reader_order_print_btn)} (${formatSom(printPriceSom)})")
            }
        }
    }
}

@Composable
private fun ReaderTypographySheet(
    settings: ReaderSettingsEntity,
    onUpdate: (ReaderSettingsEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.reader_settings),
            style = MaterialTheme.typography.headlineSmall
        )

        // Font Size
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.reader_font_size), style = MaterialTheme.typography.titleSmall)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onUpdate(settings.copy(fontSizeSp = (settings.fontSizeSp - 2).coerceAtLeast(14)))
                    },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("A-")
                }
                Text(
                    text = "${settings.fontSizeSp} sp",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = {
                        onUpdate(settings.copy(fontSizeSp = (settings.fontSizeSp + 2).coerceAtMost(28)))
                    },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("A+")
                }
            }
        }

        // Paper Theme Swatches
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.reader_theme_label), style = MaterialTheme.typography.titleSmall)
            val themes = listOf(
                Triple(ReaderThemes.PARCHMENT, "Qog'oz", ReaderPalettes.Parchment),
                Triple(ReaderThemes.DAY, "Kunduz", ReaderPalettes.CrispDay),
                Triple(ReaderThemes.SEPIA, "Sepiya", ReaderPalettes.Sepia),
                Triple(ReaderThemes.NIGHT, "Tungi", ReaderPalettes.Night)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                themes.forEach { (mode, label, swatch) ->
                    val selected = settings.themeMode == mode
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(CircleShape)
                            .clickable { onUpdate(settings.copy(themeMode = mode)) },
                        shape = CircleShape,
                        color = swatch.background,
                        border = BorderStroke(
                            width = if (selected) 2.5.dp else 1.dp,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                color = swatch.onBackground
                            )
                        }
                    }
                }
            }
        }

        // Font Style & Line Spacing
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = settings.useSerifFont,
                onClick = { onUpdate(settings.copy(useSerifFont = true)) },
                label = { Text(stringResource(R.string.reader_font_serif)) }
            )
            FilterChip(
                selected = !settings.useSerifFont,
                onClick = { onUpdate(settings.copy(useSerifFont = false)) },
                label = { Text(stringResource(R.string.reader_font_sans)) }
            )
            Spacer(modifier = Modifier.weight(1f))
            FilterChip(
                selected = settings.wideLineSpacing,
                onClick = { onUpdate(settings.copy(wideLineSpacing = !settings.wideLineSpacing)) },
                label = {
                    Text(
                        stringResource(
                            if (settings.wideLineSpacing) R.string.reader_spacing_wide else R.string.reader_spacing_normal
                        )
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
