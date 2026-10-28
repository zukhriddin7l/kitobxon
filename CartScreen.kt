package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.BookFormats
import com.example.data.local.CartItemWithBook
import com.example.data.repository.BookstoreRepository
import com.example.ui.CartSummaryState
import com.example.ui.components.BookCoverArtwork
import com.example.ui.components.formatSom

@Composable
fun CartScreen(
    cartState: CartSummaryState,
    onUpdateQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onApplyPromo: (String) -> Unit,
    onClearPromo: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onBrowseStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("cart_screen")
    ) {
        Text(
            text = "${stringResource(R.string.cart_title)} (${cartState.totalItemCount})",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (cartState.items.isEmpty()) {
            EmptyCartContent(
                onBrowseStore = onBrowseStore,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )
            return
        }

        var promoInput by remember(cartState.promoCode) { mutableStateOf(cartState.promoCode) }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Free Delivery Progress Banner (if physical items exist)
            if (cartState.hasPhysicalItems) {
                item(key = "free_shipping_banner") {
                    FreeDeliveryBanner(remainingSom = cartState.remainingForFreeDeliverySom)
                }
            }

            items(cartState.items, key = { it.cartItem.id }) { entry ->
                CartItemRowCard(
                    entry = entry,
                    onUpdateQuantity = { newQty -> onUpdateQuantity(entry.cartItem.id, newQty) },
                    onRemove = { onRemoveItem(entry.cartItem.id) }
                )
            }

            // Promo Code Card
            item(key = "promo_section") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Chegirma va aksiyalar",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = promoInput,
                                onValueChange = { promoInput = it },
                                label = { Text(stringResource(R.string.cart_promo_label)) },
                                singleLine = true,
                                trailingIcon = {
                                    if (cartState.promoCode.isNotEmpty()) {
                                        IconButton(onClick = {
                                            promoInput = ""
                                            onClearPromo()
                                        }) {
                                            Icon(Icons.Default.Close, contentDescription = "Tozalash")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("cart_promo_input")
                            )
                            OutlinedButton(
                                onClick = { onApplyPromo(promoInput) },
                                modifier = Modifier.testTag("cart_apply_promo_button")
                            ) {
                                Text(stringResource(R.string.cart_promo_apply))
                            }
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val presets = listOf(
                                "KITOBXON10" to "-10% chegirma",
                                "MUTOLAA15" to "-15% chegirma",
                                "BEPULYETKAZISH" to "Bepul kuryer"
                            )
                            items(presets) { (code, desc) ->
                                AssistChip(
                                    onClick = {
                                        promoInput = code
                                        onApplyPromo(code)
                                    },
                                    label = { Text("$code ($desc)") },
                                    modifier = Modifier.testTag("promo_chip_$code")
                                )
                            }
                        }
                    }
                }
            }

            // Summary Breakdown
            item(key = "summary_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryLine(stringResource(R.string.cart_subtotal), formatSom(cartState.subtotalSom))
                        if (cartState.discountSom > 0L) {
                            SummaryLine(
                                label = "${stringResource(R.string.cart_discount)} (${cartState.promoCode})",
                                value = "-${formatSom(cartState.discountSom)}",
                                highlight = true
                            )
                        }
                        SummaryLine(
                            label = stringResource(R.string.cart_delivery_est),
                            value = if (!cartState.hasPhysicalItems) {
                                "Raqamli (Bepul)"
                            } else if (cartState.remainingForFreeDeliverySom == 0L || cartState.promoCode == "BEPULYETKAZISH") {
                                "Bepul!"
                            } else {
                                "Keyingi qadamda tanlanadi"
                            }
                        )
                        HorizontalDivider()
                        val netBeforeShipping = (cartState.subtotalSom - cartState.discountSom).coerceAtLeast(0L)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.cart_total),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatSom(netBeforeShipping),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Bottom Checkout Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onProceedToCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("proceed_to_checkout_button")
                ) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.cart_checkout_btn),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun FreeDeliveryBanner(remainingSom: Long) {
    val threshold = BookstoreRepository.FREE_DELIVERY_THRESHOLD_SOM
    val progress = if (remainingSom <= 0L) 1f else ((threshold - remainingSom).toFloat() / threshold.toFloat()).coerceIn(0.08f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (remainingSom <= 0L) {
                        "Tabriklaymiz! Kuryer orqali yetkazib berish BEPUL!"
                    } else {
                        "Bepul kuryer yetkazib berishgacha yana ${formatSom(remainingSom)} qoldi"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
private fun CartItemRowCard(
    entry: CartItemWithBook,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val isPrint = entry.cartItem.format == BookFormats.PRINT
    val unitPrice = if (isPrint) entry.book.printPriceSom else entry.book.ebookPriceSom

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BookCoverArtwork(
                title = entry.book.title,
                author = entry.book.author,
                coverArtKey = entry.book.coverArtKey,
                coverPaletteIndex = entry.book.coverPaletteIndex,
                compactText = true,
                modifier = Modifier
                    .width(64.dp)
                    .height(92.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    color = if (isPrint) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.tertiaryContainer
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isPrint) "Bosma kitob + Dostavka" else "Elektron kitob",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = entry.book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatSom(unitPrice * entry.cartItem.quantity),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isPrint) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = { onUpdateQuantity(entry.cartItem.quantity - 1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Kamaytirish",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${entry.cartItem.quantity}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    FilledTonalIconButton(
                        onClick = { onUpdateQuantity(entry.cartItem.quantity + 1) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ko'paytirish",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "O'chirish",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (highlight) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EmptyCartContent(
    onBrowseStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ShoppingCart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = stringResource(R.string.cart_empty_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.cart_empty_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = onBrowseStore,
            modifier = Modifier.testTag("empty_cart_browse_button")
        ) {
            Text(stringResource(R.string.cart_browse_store))
        }
    }
}
