package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.DeliveryMethods
import com.example.data.local.PaymentMethods
import com.example.data.repository.BookstoreRepository
import com.example.data.seed.UzbekistanDeliveryData
import com.example.ui.BookstoreViewModel
import com.example.ui.components.formatSom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: BookstoreViewModel,
    onBack: () -> Unit,
    onOrderCompleted: (Long) -> Unit
) {
    BackHandler(onBack = onBack)

    val cartState by viewModel.cartSummaryState.collectAsStateWithLifecycle()
    val savedAddresses by viewModel.savedAddresses.collectAsStateWithLifecycle()

    val hasPhysical = cartState.hasPhysicalItems

    var deliveryMethod by remember(hasPhysical) {
        mutableStateOf(
            if (hasPhysical) DeliveryMethods.EXPRESS_COURIER else DeliveryMethods.DIGITAL_INSTANT
        )
    }
    var selectedPickupId by remember {
        mutableStateOf(UzbekistanDeliveryData.pickupPoints.first().id)
    }

    var recipientName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+998 ") }
    var selectedRegion by remember { mutableStateOf(UzbekistanDeliveryData.regions.first()) }
    var streetAddress by remember { mutableStateOf("") }
    var courierNote by remember { mutableStateOf("") }
    var saveAddress by remember { mutableStateOf(true) }
    var regionDropdownExpanded by remember { mutableStateOf(false) }

    var paymentMethod by remember { mutableStateOf(PaymentMethods.CLICK) }
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var walletPhone by remember { mutableStateOf("+998 ") }

    var validationError by remember { mutableStateOf<String?>(null) }
    var showOtpGatewayDialog by remember { mutableStateOf(false) }

    val deliveryFee = viewModel.calculateDeliveryFee(
        hasPhysicalItems = hasPhysical,
        subtotalSom = cartState.subtotalSom,
        deliveryMethod = deliveryMethod,
        promoCode = cartState.promoCode
    )
    val finalTotal = (cartState.subtotalSom - cartState.discountSom + deliveryFee).coerceAtLeast(0L)

    val selectedPickup = UzbekistanDeliveryData.pickupPoints.find { it.id == selectedPickupId }
        ?: UzbekistanDeliveryData.pickupPoints.first()

    fun buildMaskedPaymentLabel(): String {
        val digits = cardNumber.filter { it.isDigit() }
        return when (paymentMethod) {
            PaymentMethods.UZCARD_HUMO -> {
                val net = when {
                    digits.startsWith("8600") -> "Uzcard"
                    digits.startsWith("9860") -> "Humo"
                    else -> "Karta"
                }
                val tail = digits.takeLast(4).ifEmpty { "0000" }
                "$net •••• $tail"
            }
            PaymentMethods.CLICK -> "Click (${walletPhone.trim()})"
            PaymentMethods.PAYME -> "Payme (${walletPhone.trim()})"
            PaymentMethods.UZUM -> "Uzum Bank (${walletPhone.trim()})"
            else -> "Kuryerga naqd / terminal"
        }
    }

    fun validateInputs(): Boolean {
        if (recipientName.trim().length < 2) {
            validationError = "Iltimos, qabul qiluvchi ismini kiriting."
            return false
        }
        val phoneDigits = phone.filter { it.isDigit() }
        if (phoneDigits.length < 9) {
            validationError = "Iltimos, to'g'ri telefon raqamini kiriting."
            return false
        }
        if (hasPhysical && deliveryMethod != DeliveryMethods.PICKUP_POINT && streetAddress.trim().length < 4) {
            validationError = "Yetkazib berish manzilini (ko'cha va uy) kiriting."
            return false
        }
        if (paymentMethod == PaymentMethods.UZCARD_HUMO) {
            val cDigits = cardNumber.filter { it.isDigit() }
            if (cDigits.length < 16) {
                validationError = "16 xonali Uzcard yoki Humo karta raqamini kiriting."
                return false
            }
            if (cardExpiry.filter { it.isDigit() }.length < 4) {
                validationError = "Karta amal qilish muddatini kiriting (masalan: 08/28)."
                return false
            }
        }
        validationError = null
        return true
    }

    fun finalizeOrder() {
        val addressText = when {
            !hasPhysical -> "Raqamli kutubxona (onlayn)"
            deliveryMethod == DeliveryMethods.PICKUP_POINT -> "${selectedPickup.title} — ${selectedPickup.address}"
            else -> streetAddress.trim()
        }
        viewModel.submitCheckoutOrder(
            recipientName = recipientName,
            phone = phone,
            region = if (deliveryMethod == DeliveryMethods.PICKUP_POINT) selectedPickup.city else selectedRegion,
            addressLine = addressText,
            courierNote = courierNote,
            deliveryMethod = deliveryMethod,
            pickupPointName = if (deliveryMethod == DeliveryMethods.PICKUP_POINT) selectedPickup.title else "",
            paymentMethod = paymentMethod,
            maskedPaymentAccount = buildMaskedPaymentLabel(),
            saveAddressForFuture = saveAddress,
            onOrderCreated = onOrderCompleted
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("checkout_screen")
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.checkout_title)) },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("checkout_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.detail_back)
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // STEP 1: Delivery Method (Dostavka)
            item(key = "step_delivery") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.checkout_step_delivery),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    if (!hasPhysical) {
                        DeliveryChoiceCard(
                            selected = true,
                            title = stringResource(R.string.delivery_digital),
                            subtitle = stringResource(R.string.delivery_digital_desc),
                            feeLabel = "Bepul",
                            icon = Icons.Default.ElectricBolt,
                            onClick = {},
                            testTag = "delivery_digital_card"
                        )
                    } else {
                        val expressFee = viewModel.calculateDeliveryFee(
                            true, cartState.subtotalSom, DeliveryMethods.EXPRESS_COURIER, cartState.promoCode
                        )
                        val standardFee = viewModel.calculateDeliveryFee(
                            true, cartState.subtotalSom, DeliveryMethods.STANDARD_REGIONAL, cartState.promoCode
                        )

                        DeliveryChoiceCard(
                            selected = deliveryMethod == DeliveryMethods.EXPRESS_COURIER,
                            title = stringResource(R.string.delivery_express),
                            subtitle = stringResource(R.string.delivery_express_desc),
                            feeLabel = if (expressFee == 0L) "Bepul" else formatSom(expressFee),
                            icon = Icons.Default.ElectricBolt,
                            onClick = { deliveryMethod = DeliveryMethods.EXPRESS_COURIER },
                            testTag = "delivery_express_card"
                        )
                        DeliveryChoiceCard(
                            selected = deliveryMethod == DeliveryMethods.STANDARD_REGIONAL,
                            title = stringResource(R.string.delivery_standard),
                            subtitle = stringResource(R.string.delivery_standard_desc),
                            feeLabel = if (standardFee == 0L) "Bepul" else formatSom(standardFee),
                            icon = Icons.Default.LocalShipping,
                            onClick = { deliveryMethod = DeliveryMethods.STANDARD_REGIONAL },
                            testTag = "delivery_standard_card"
                        )
                        DeliveryChoiceCard(
                            selected = deliveryMethod == DeliveryMethods.PICKUP_POINT,
                            title = stringResource(R.string.delivery_pickup),
                            subtitle = stringResource(R.string.delivery_pickup_desc),
                            feeLabel = "Bepul",
                            icon = Icons.Default.Storefront,
                            onClick = { deliveryMethod = DeliveryMethods.PICKUP_POINT },
                            testTag = "delivery_pickup_card"
                        )
                    }
                }
            }

            // STEP 2: Recipient & Address / Pickup Point
            item(key = "step_address") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.checkout_step_address),
                            style = MaterialTheme.typography.headlineSmall
                        )

                        if (savedAddresses.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(savedAddresses, key = { it.id }) { addr ->
                                    AssistChip(
                                        onClick = {
                                            recipientName = addr.recipientName
                                            phone = addr.phone
                                            selectedRegion = addr.region
                                            streetAddress = addr.streetAddress
                                            courierNote = addr.courierNote
                                        },
                                        label = { Text("${addr.region}: ${addr.streetAddress.take(18)}…") }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = recipientName,
                            onValueChange = {
                                recipientName = it
                                validationError = null
                            },
                            label = { Text(stringResource(R.string.field_recipient_name)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_name_input")
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                                walletPhone = it
                                validationError = null
                            },
                            label = { Text(stringResource(R.string.field_phone)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_phone_input")
                        )

                        if (hasPhysical && deliveryMethod == DeliveryMethods.PICKUP_POINT) {
                            Text(
                                text = "Topshirish punktini tanlang:",
                                style = MaterialTheme.typography.titleSmall
                            )
                            UzbekistanDeliveryData.pickupPoints.forEach { point ->
                                val chosen = point.id == selectedPickupId
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPickupId = point.id },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (chosen) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = chosen,
                                            onClick = { selectedPickupId = point.id }
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(point.title, style = MaterialTheme.typography.titleSmall)
                                            Text(
                                                "${point.city} • ${point.address} (${point.hours})",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        } else if (hasPhysical) {
                            ExposedDropdownMenuBox(
                                expanded = regionDropdownExpanded,
                                onExpandedChange = { regionDropdownExpanded = !regionDropdownExpanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedRegion,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.field_region)) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = regionDropdownExpanded)
                                    },
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = regionDropdownExpanded,
                                    onDismissRequest = { regionDropdownExpanded = false }
                                ) {
                                    UzbekistanDeliveryData.regions.forEach { reg ->
                                        DropdownMenuItem(
                                            text = { Text(reg) },
                                            onClick = {
                                                selectedRegion = reg
                                                regionDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = streetAddress,
                                onValueChange = {
                                    streetAddress = it
                                    validationError = null
                                },
                                label = { Text(stringResource(R.string.field_address)) },
                                placeholder = { Text("Masalan: Amir Temur ko'chasi, 15-uy, 24-xonadon") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("checkout_address_input")
                            )

                            OutlinedTextField(
                                value = courierNote,
                                onValueChange = { courierNote = it },
                                label = { Text(stringResource(R.string.field_courier_note)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { saveAddress = !saveAddress }
                            ) {
                                Checkbox(
                                    checked = saveAddress,
                                    onCheckedChange = { saveAddress = it }
                                )
                                Text(
                                    text = stringResource(R.string.save_address_checkbox),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // STEP 3: Payment Systems (Click, Payme, Uzcard/Humo, Uzum, Cash)
            item(key = "step_payment") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.checkout_step_payment),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    PaymentChoiceCard(
                        selected = paymentMethod == PaymentMethods.CLICK,
                        title = stringResource(R.string.pay_click),
                        subtitle = stringResource(R.string.pay_click_desc),
                        badgeText = "CLICK",
                        icon = Icons.Default.AccountBalanceWallet,
                        onClick = { paymentMethod = PaymentMethods.CLICK },
                        testTag = "payment_click_card"
                    )
                    PaymentChoiceCard(
                        selected = paymentMethod == PaymentMethods.PAYME,
                        title = stringResource(R.string.pay_payme),
                        subtitle = stringResource(R.string.pay_payme_desc),
                        badgeText = "PAYME",
                        icon = Icons.Default.AccountBalanceWallet,
                        onClick = { paymentMethod = PaymentMethods.PAYME },
                        testTag = "payment_payme_card"
                    )
                    PaymentChoiceCard(
                        selected = paymentMethod == PaymentMethods.UZCARD_HUMO,
                        title = stringResource(R.string.pay_card),
                        subtitle = stringResource(R.string.pay_card_desc),
                        badgeText = "8600 / 9860",
                        icon = Icons.Default.CreditCard,
                        onClick = { paymentMethod = PaymentMethods.UZCARD_HUMO },
                        testTag = "payment_uzcard_card"
                    )
                    PaymentChoiceCard(
                        selected = paymentMethod == PaymentMethods.UZUM,
                        title = stringResource(R.string.pay_uzum),
                        subtitle = stringResource(R.string.pay_uzum_desc),
                        badgeText = "UZUM",
                        icon = Icons.Default.QrCode2,
                        onClick = { paymentMethod = PaymentMethods.UZUM },
                        testTag = "payment_uzum_card"
                    )
                    if (hasPhysical) {
                        PaymentChoiceCard(
                            selected = paymentMethod == PaymentMethods.CASH_ON_DELIVERY,
                            title = stringResource(R.string.pay_cash),
                            subtitle = stringResource(R.string.pay_cash_desc),
                            badgeText = "NAQD",
                            icon = Icons.Default.Payments,
                            onClick = { paymentMethod = PaymentMethods.CASH_ON_DELIVERY },
                            testTag = "payment_cash_card"
                        )
                    }

                    // Dynamic Payment Details Sub-card
                    if (paymentMethod == PaymentMethods.UZCARD_HUMO) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val rawDigits = cardNumber.filter { it.isDigit() }
                                val detectedNetwork = when {
                                    rawDigits.startsWith("8600") -> "UZCARD aniqlandi"
                                    rawDigits.startsWith("9860") -> "HUMO aniqlandi"
                                    rawDigits.startsWith("4") -> "VISA aniqlandi"
                                    rawDigits.startsWith("5") -> "MASTERCARD aniqlandi"
                                    else -> "8600 (Uzcard) yoki 9860 (Humo)"
                                }
                                Text(
                                    text = detectedNetwork,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                OutlinedTextField(
                                    value = cardNumber,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }.take(16)
                                        cardNumber = digits.chunked(4).joinToString(" ")
                                        validationError = null
                                    },
                                    label = { Text(stringResource(R.string.field_card_number)) },
                                    placeholder = { Text("8600 1234 5678 9012") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("checkout_card_number_input")
                                )
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }.take(4)
                                        cardExpiry = if (digits.length > 2) {
                                            "${digits.take(2)}/${digits.drop(2)}"
                                        } else {
                                            digits
                                        }
                                        validationError = null
                                    },
                                    label = { Text(stringResource(R.string.field_card_expiry)) },
                                    placeholder = { Text("09/28") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("checkout_card_expiry_input")
                                )
                            }
                        }
                    } else if (paymentMethod != PaymentMethods.CASH_ON_DELIVERY) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                OutlinedTextField(
                                    value = walletPhone,
                                    onValueChange = { walletPhone = it },
                                    label = { Text(stringResource(R.string.field_payment_phone)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // Final Order Cost Summary
            item(key = "final_totals") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kitoblar (${cartState.totalItemCount} ta)")
                            Text(formatSom(cartState.subtotalSom))
                        }
                        if (cartState.discountSom > 0L) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Promokod chegirmasi")
                                Text("-${formatSom(cartState.discountSom)}", color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Yetkazib berish (Dostavka)")
                            Text(if (deliveryFee == 0L) "Bepul" else formatSom(deliveryFee))
                        }
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Jami to'lov:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatSom(finalTotal),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            validationError?.let { err ->
                item(key = "validation_error") {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = err,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Sticky Pay & Order CTA respecting navigationBars insets
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = {
                        if (validateInputs()) {
                            if (paymentMethod == PaymentMethods.CASH_ON_DELIVERY || finalTotal == 0L) {
                                finalizeOrder()
                            } else {
                                showOtpGatewayDialog = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_payment_order_button")
                ) {
                    Icon(
                        imageVector = if (paymentMethod == PaymentMethods.CASH_ON_DELIVERY) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.Lock
                        },
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (paymentMethod == PaymentMethods.CASH_ON_DELIVERY) {
                            "${stringResource(R.string.checkout_confirm_cash_btn)} • ${formatSom(finalTotal)}"
                        } else {
                            "${stringResource(R.string.checkout_pay_now_btn)} • ${formatSom(finalTotal)}"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    if (showOtpGatewayDialog) {
        PaymentGatewayOtpDialog(
            paymentMethodName = buildMaskedPaymentLabel(),
            amountSom = finalTotal,
            onDismiss = { showOtpGatewayDialog = false },
            onConfirmed = {
                showOtpGatewayDialog = false
                finalizeOrder()
            }
        )
    }
}

@Composable
private fun DeliveryChoiceCard(
    selected: Boolean,
    title: String,
    subtitle: String,
    feeLabel: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
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
                imageVector = icon,
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
                text = feeLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PaymentChoiceCard(
    selected: Boolean,
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
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
                imageVector = icon,
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
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PaymentGatewayOtpDialog(
    paymentMethodName: String,
    amountSom: Long,
    onDismiss: () -> Unit,
    onConfirmed: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.otp_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Qabul qiluvchi: KITOBXON DO'KONI", style = MaterialTheme.typography.labelMedium)
                        Text("To'lov usuli: $paymentMethodName", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = "Summa: ${formatSom(amountSom)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                AssistChip(
                    onClick = { otpCode = "1234" },
                    label = { Text("SMS kodni avtomatik kiritish: 1234") },
                    modifier = Modifier.testTag("otp_autofill_chip")
                )

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { otpCode = it.filter { ch -> ch.isDigit() }.take(4) },
                    label = { Text(stringResource(R.string.otp_code_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_code_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmed,
                enabled = otpCode.length == 4,
                modifier = Modifier.testTag("otp_confirm_button")
            ) {
                Text(stringResource(R.string.otp_confirm_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.review_cancel))
            }
        }
    )
}
