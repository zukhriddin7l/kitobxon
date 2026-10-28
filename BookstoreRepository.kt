package com.example.data.repository

import com.example.data.local.BookEntity
import com.example.data.local.BookFormats
import com.example.data.local.BookWithChapters
import com.example.data.local.BookmarkEntity
import com.example.data.local.BookstoreDao
import com.example.data.local.CartItemEntity
import com.example.data.local.CartItemWithBook
import com.example.data.local.ChapterEntity
import com.example.data.local.DeliveryMethods
import com.example.data.local.OrderEntity
import com.example.data.local.OrderItemEntity
import com.example.data.local.OrderStatuses
import com.example.data.local.OrderWithItems
import com.example.data.local.ReaderSettingsEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.SavedAddressEntity
import com.example.data.seed.CatalogSeedBooks
import com.example.data.seed.CatalogSeedChaptersPart2
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class BookstoreRepository(private val dao: BookstoreDao) {

    companion object {
        const val FREE_DELIVERY_THRESHOLD_SOM = 150_000L
        const val EXPRESS_COURIER_FEE_SOM = 20_000L
        const val STANDARD_REGIONAL_FEE_SOM = 15_000L
    }

    val allBooks: Flow<List<BookEntity>> = dao.observeAllBooks()
    val allBookmarks: Flow<List<BookmarkEntity>> = dao.observeAllBookmarks()
    val cartItems: Flow<List<CartItemWithBook>> = dao.observeCartItemsWithBooks()
    val savedAddresses: Flow<List<SavedAddressEntity>> = dao.observeSavedAddresses()
    val allOrders: Flow<List<OrderWithItems>> = dao.observeOrdersWithItems()
    val readerSettings: Flow<ReaderSettingsEntity?> = dao.observeReaderSettings()

    suspend fun ensureCatalogSeeded() {
        if (dao.getBookCount() == 0) {
            dao.insertBooks(CatalogSeedBooks.initialBooks())
            dao.insertChapters(CatalogSeedBooks.chaptersPart1() + CatalogSeedChaptersPart2.chaptersPart2())
            dao.insertReviews(CatalogSeedBooks.initialReviews())
            dao.saveReaderSettings(ReaderSettingsEntity())
        }
    }

    fun observeBookWithChapters(bookId: String): Flow<BookWithChapters?> =
        dao.observeBookWithChapters(bookId)

    fun observeBookById(bookId: String): Flow<BookEntity?> =
        dao.observeBookById(bookId)

    fun observeChapters(bookId: String): Flow<List<ChapterEntity>> =
        dao.observeChaptersForBook(bookId)

    fun observeReviews(bookId: String): Flow<List<ReviewEntity>> =
        dao.observeReviewsForBook(bookId)

    fun observeOrderById(orderId: Long): Flow<OrderWithItems?> =
        dao.observeOrderById(orderId)

    suspend fun toggleFavorite(book: BookEntity) {
        dao.updateFavorite(book.id, !book.isFavorite)
    }

    suspend fun updateReadingProgress(bookId: String, chapterNumber: Int, totalChapters: Int) {
        val safeTotal = totalChapters.coerceAtLeast(1)
        val percent = ((chapterNumber.toFloat() / safeTotal.toFloat()) * 100f)
            .toInt()
            .coerceIn(1, 100)
        dao.updateReadingProgress(
            bookId = bookId,
            chapterNumber = chapterNumber,
            progressPercent = percent
        )
    }

    suspend fun addBookmark(
        bookId: String,
        bookTitle: String,
        chapterNumber: Int,
        chapterTitle: String,
        quoteSnippet: String,
        note: String
    ) {
        dao.insertBookmark(
            BookmarkEntity(
                bookId = bookId,
                bookTitle = bookTitle,
                chapterNumber = chapterNumber,
                chapterTitle = chapterTitle,
                quoteSnippet = quoteSnippet,
                note = note.trim()
            )
        )
    }

    suspend fun deleteBookmark(bookmarkId: Long) {
        dao.deleteBookmark(bookmarkId)
    }

    suspend fun addReview(book: BookEntity, reviewerName: String, rating: Int, comment: String) {
        val cleanName = reviewerName.trim().ifBlank { "Kitobxon" }
        val cleanRating = rating.coerceIn(1, 5)
        dao.insertReview(
            ReviewEntity(
                bookId = book.id,
                reviewerName = cleanName,
                rating = cleanRating,
                comment = comment.trim()
            )
        )
        val totalScore = (book.rating * book.reviewCount) + cleanRating
        val newCount = book.reviewCount + 1
        val newAvg = ((totalScore / newCount) * 10f).toInt() / 10f
        dao.updateBookRatingSummary(book.id, newAvg.coerceIn(1f, 5f), newCount)
    }

    suspend fun addToCart(bookId: String, format: String) {
        val existing = dao.findCartItem(bookId, format)
        if (existing != null) {
            // Digital e-book only needs 1 copy; physical books increment quantity
            if (format == BookFormats.PRINT) {
                dao.updateCartItemQuantity(existing.id, (existing.quantity + 1).coerceAtMost(20))
            }
        } else {
            dao.insertCartItem(
                CartItemEntity(
                    bookId = bookId,
                    format = format,
                    quantity = 1
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            dao.deleteCartItem(cartItemId)
        } else {
            dao.updateCartItemQuantity(cartItemId, newQuantity.coerceAtMost(20))
        }
    }

    suspend fun removeCartItem(cartItemId: Long) {
        dao.deleteCartItem(cartItemId)
    }

    suspend fun saveReaderSettings(settings: ReaderSettingsEntity) {
        dao.saveReaderSettings(settings)
    }

    suspend fun deleteSavedAddress(addressId: Long) {
        dao.deleteSavedAddress(addressId)
    }

    fun calculateDiscountSom(subtotalSom: Long, promoCode: String): Long {
        return when (promoCode.trim().uppercase()) {
            "KITOBXON10" -> (subtotalSom * 10L) / 100L
            "MUTOLAA15" -> (subtotalSom * 15L) / 100L
            else -> 0L
        }
    }

    fun calculateDeliveryFeeSom(
        hasPhysicalItems: Boolean,
        subtotalSom: Long,
        deliveryMethod: String,
        promoCode: String
    ): Long {
        if (!hasPhysicalItems) return 0L
        if (deliveryMethod == DeliveryMethods.PICKUP_POINT || deliveryMethod == DeliveryMethods.DIGITAL_INSTANT) {
            return 0L
        }
        if (subtotalSom >= FREE_DELIVERY_THRESHOLD_SOM) return 0L
        if (promoCode.trim().uppercase() == "BEPULYETKAZISH") return 0L

        return when (deliveryMethod) {
            DeliveryMethods.EXPRESS_COURIER -> EXPRESS_COURIER_FEE_SOM
            DeliveryMethods.STANDARD_REGIONAL -> STANDARD_REGIONAL_FEE_SOM
            else -> 0L
        }
    }

    suspend fun placeOrder(
        cartItems: List<CartItemWithBook>,
        recipientName: String,
        phone: String,
        region: String,
        addressLine: String,
        courierNote: String,
        deliveryMethod: String,
        pickupPointName: String,
        paymentMethod: String,
        maskedPaymentAccount: String,
        promoCode: String,
        saveAddressForFuture: Boolean
    ): Long {
        val hasPhysical = cartItems.any { it.cartItem.format == BookFormats.PRINT }
        val effectiveDeliveryMethod = if (!hasPhysical) DeliveryMethods.DIGITAL_INSTANT else deliveryMethod

        val subtotal = cartItems.sumOf { entry ->
            val unitPrice = if (entry.cartItem.format == BookFormats.PRINT) {
                entry.book.printPriceSom
            } else {
                entry.book.ebookPriceSom
            }
            unitPrice * entry.cartItem.quantity
        }
        val discount = calculateDiscountSom(subtotal, promoCode)
        val deliveryFee = calculateDeliveryFeeSom(hasPhysical, subtotal, effectiveDeliveryMethod, promoCode)
        val total = (subtotal - discount + deliveryFee).coerceAtLeast(0L)

        val estimatedDelivery = when (effectiveDeliveryMethod) {
            DeliveryMethods.DIGITAL_INSTANT -> "Darhol — Kutubxonam bo'limida ochildi"
            DeliveryMethods.EXPRESS_COURIER -> "Bugun, 2–4 soat ichida kuryer yetkazadi"
            DeliveryMethods.PICKUP_POINT -> "Bugun 1 soatdan so'ng olib ketishga tayyor"
            else -> "1–3 ish kuni ichida yetkazib beriladi"
        }

        val initialStatus = if (!hasPhysical) OrderStatuses.DELIVERED else OrderStatuses.CONFIRMED
        val orderCode = "KTB-${Random.nextInt(10000, 99999)}"

        val orderEntity = OrderEntity(
            orderCode = orderCode,
            createdAt = System.currentTimeMillis(),
            recipientName = recipientName.trim().ifBlank { "Kitobxon Xaridor" },
            phone = phone.trim(),
            region = region,
            addressLine = addressLine.trim(),
            courierNote = courierNote.trim(),
            deliveryMethod = effectiveDeliveryMethod,
            pickupPointName = pickupPointName,
            paymentMethod = paymentMethod,
            maskedPaymentAccount = maskedPaymentAccount,
            promoCode = promoCode.trim().uppercase(),
            subtotalSom = subtotal,
            discountSom = discount,
            deliveryFeeSom = deliveryFee,
            totalSom = total,
            status = initialStatus,
            estimatedDeliveryText = estimatedDelivery
        )

        val orderItems = cartItems.map { entry ->
            val unitPrice = if (entry.cartItem.format == BookFormats.PRINT) {
                entry.book.printPriceSom
            } else {
                entry.book.ebookPriceSom
            }
            OrderItemEntity(
                orderId = 0L,
                bookId = entry.book.id,
                bookTitle = entry.book.title,
                bookAuthor = entry.book.author,
                format = entry.cartItem.format,
                quantity = entry.cartItem.quantity,
                unitPriceSom = unitPrice,
                coverArtKey = entry.book.coverArtKey,
                coverPaletteIndex = entry.book.coverPaletteIndex
            )
        }

        // Buying either EBOOK or PRINT unlocks the digital E-book in Kutubxonam!
        val unlockedIds = cartItems.map { it.book.id }.distinct()

        val addressEntity = if (saveAddressForFuture && hasPhysical && effectiveDeliveryMethod != DeliveryMethods.PICKUP_POINT && addressLine.isNotBlank()) {
            SavedAddressEntity(
                recipientName = recipientName.trim(),
                phone = phone.trim(),
                region = region,
                streetAddress = addressLine.trim(),
                courierNote = courierNote.trim()
            )
        } else {
            null
        }

        return dao.placeOrderTransaction(
            order = orderEntity,
            items = orderItems,
            unlockedBookIds = unlockedIds,
            addressToSave = addressEntity
        )
    }

    suspend fun advanceOrderStatus(order: OrderEntity) {
        val next = when (order.status) {
            OrderStatuses.CONFIRMED -> OrderStatuses.PACKING
            OrderStatuses.PACKING -> OrderStatuses.ON_THE_WAY
            OrderStatuses.ON_THE_WAY -> OrderStatuses.DELIVERED
            else -> order.status
        }
        if (next != order.status) {
            dao.updateOrderStatus(order.id, next)
        }
    }
}
