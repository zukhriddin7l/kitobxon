package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.BookFormats
import com.example.data.local.DeliveryMethods
import com.example.data.local.OrderEntity
import com.example.data.local.OrderStatuses
import com.example.data.local.OrderWithItems
import com.example.data.local.SavedAddressEntity
import com.example.ui.BookstoreViewModel
import com.example.ui.components.formatSom
import com.example.ui.components.formatUzbekDateTime

@Composable
fun OrderSuccessScreen(
    orderId: Long,
    viewModel: BookstoreViewModel,
    onGoToOrders: () -> Unit,
    onGoToLibrary: () -> Unit,
    onBackToStore: () -> Unit
) {
    BackHandler(onBack = onBackToStore)

    val orderWithItems by remember(orderId) {
        viewModel.observeOrderById(orderId)
    }.collectAsStateWithLifecycle(initialValue = null)

    val data = orderWithItems
    if (data == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val order = data.order

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("order_success_screen"),
        contentPadding = PaddingValues(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.order_success_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Buyurtma raqami: #${order.orderCode}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            OrderReceiptCard(data = data)
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onGoToLibrary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("success_open_library_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoStories, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${stringResource(R.string.order_read_ebook_btn)} (Elektron nashr ochildi!)")
                }
                FilledTonalButton(
                    onClick = onGoToOrders,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("success_track_orders_button")
                ) {
                    Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.order_track_btn))
                }
                OutlinedButton(
                    onClick = onBackToStore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Do'konga qaytish")
                }
            }
        }
    }
}

@Composable
fun OrdersHistoryScreen(
    orders: List<OrderWithItems>,
    savedAddresses: List<SavedAddressEntity>,
    onAdvanceOrderStatus: (OrderEntity) -> Unit,
    onDeleteAddress: (Long) -> Unit,
    onBrowseStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("orders_history_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "orders_title") {
            Text(
                text = stringResource(R.string.orders_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (orders.isEmpty()) {
            item(key = "empty_orders") {
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = stringResource(R.string.orders_empty_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.orders_empty_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = onBrowseStore) {
                            Text(stringResource(R.string.cart_browse_store))
                        }
                    }
                }
            }
        } else {
            items(orders, key = { it.order.id }) { orderWithItems ->
                OrderTrackingCard(
                    data = orderWithItems,
                    onAdvanceStatus = { onAdvanceOrderStatus(orderWithItems.order) }
                )
            }
        }

        if (savedAddresses.isNotEmpty()) {
            item(key = "addresses_header") {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Saqlangan yetkazib berish manzillari",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            items(savedAddresses, key = { "addr_${it.id}" }) { addr ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${addr.recipientName} • ${addr.phone}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "${addr.region}, ${addr.streetAddress}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDeleteAddress(addr.id) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "O'chirish",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderTrackingCard(
    data: OrderWithItems,
    onAdvanceStatus: () -> Unit
) {
    val order = data.order
    var showFullReceipt by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.orderCode}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Buyurtma #${order.orderCode}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatUzbekDateTime(order.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(status = order.status)
            }

            // 4-Step Delivery Timeline for physical shipments
            if (order.deliveryMethod != DeliveryMethods.DIGITAL_INSTANT) {
                DeliveryTimelineStepper(currentStatus = order.status)
            }

            Text(
                text = "Yetkazish: ${order.estimatedDeliveryText}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Manzil: ${order.region}, ${order.addressLine}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${data.items.sumOf { it.quantity }} ta kitob • ${order.maskedPaymentAccount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatSom(order.totalSom),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showFullReceipt = !showFullReceipt },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.orders_show_receipt))
                }

                if (order.status != OrderStatuses.DELIVERED && order.status != OrderStatuses.CANCELLED) {
                    FilledTonalButton(
                        onClick = onAdvanceStatus,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("advance_status_${order.id}")
                    ) {
                        Text("Keyingi bosqich")
                    }
                }
            }

            if (showFullReceipt) {
                OrderReceiptCard(data = data)
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val label = when (status) {
        OrderStatuses.CONFIRMED -> stringResource(R.string.status_confirmed)
        OrderStatuses.PACKING -> stringResource(R.string.status_packing)
        OrderStatuses.ON_THE_WAY -> stringResource(R.string.status_on_the_way)
        OrderStatuses.DELIVERED -> stringResource(R.string.status_delivered)
        else -> stringResource(R.string.status_cancelled)
    }
    val containerColor = if (status == OrderStatuses.DELIVERED) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = if (status == OrderStatuses.DELIVERED) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(color = containerColor, shape = RoundedCornerShape(10.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DeliveryTimelineStepper(currentStatus: String) {
    val steps = listOf(
        OrderStatuses.CONFIRMED to "Tasdiq",
        OrderStatuses.PACKING to "Qadoqlash",
        OrderStatuses.ON_THE_WAY to "Yo'lda",
        OrderStatuses.DELIVERED to "Yetkazildi"
    )
    val activeIdx = steps.indexOfFirst { it.first == currentStatus }.coerceAtLeast(0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, (_, title) ->
            val reached = index <= activeIdx
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            if (reached) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (reached) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (reached) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            }
        }
    }
}

@Composable
private fun OrderReceiptCard(data: OrderWithItems) {
    val order = data.order
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "KITOBXON • ELEKTRON TO'LOV CHEKI",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider()
            data.items.forEach { item ->
                val fmt = if (item.format == BookFormats.PRINT) "Bosma" else "Elektron"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.bookTitle} ($fmt x${item.quantity})",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatSom(item.unitPriceSom * item.quantity),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("To'lov tizimi:", style = MaterialTheme.typography.bodySmall)
                Text(order.maskedPaymentAccount, style = MaterialTheme.typography.bodySmall)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dostavka xizmati:", style = MaterialTheme.typography.bodySmall)
                Text(
                    if (order.deliveryFeeSom == 0L) "Bepul" else formatSom(order.deliveryFeeSom),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (order.discountSom > 0L) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Chegirma (${order.promoCode}):", style = MaterialTheme.typography.bodySmall)
                    Text("-${formatSom(order.discountSom)}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("JAMI TO'LANGAN:", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = formatSom(order.totalSom),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
