package com.example.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

object BookFormats {
    const val PRINT = "PRINT"
    const val EBOOK = "EBOOK"
}

object DeliveryMethods {
    const val EXPRESS_COURIER = "EXPRESS_COURIER"
    const val STANDARD_REGIONAL = "STANDARD_REGIONAL"
    const val PICKUP_POINT = "PICKUP_POINT"
    const val DIGITAL_INSTANT = "DIGITAL_INSTANT"
}

object PaymentMethods {
    const val CLICK = "CLICK"
    const val PAYME = "PAYME"
    const val UZCARD_HUMO = "UZCARD_HUMO"
    const val UZUM = "UZUM"
    const val CASH_ON_DELIVERY = "CASH_ON_DELIVERY"
}

object OrderStatuses {
    const val CONFIRMED = "CONFIRMED"
    const val PACKING = "PACKING"
    const val ON_THE_WAY = "ON_THE_WAY"
    const val DELIVERED = "DELIVERED"
    const val CANCELLED = "CANCELLED"
}

object ReaderThemes {
    const val PARCHMENT = "PARCHMENT"
    const val DAY = "DAY"
    const val SEPIA = "SEPIA"
    const val NIGHT = "NIGHT"
}

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val category: String,
    val synopsis: String,
    val printPriceSom: Long,
    val ebookPriceSom: Long,
    val isFreeEbook: Boolean,
    val rating: Float,
    val reviewCount: Int,
    val pageCount: Int,
    val publishYear: Int,
    val language: String,
    val publisher: String,
    val coverArtKey: String, // "otkan_kunlar", "alximik", "atom_odatlar", or clothbound key
    val coverPaletteIndex: Int, // 0..5 swatch preset for clothbound editions
    val isBestseller: Boolean,
    val isFavorite: Boolean = false,
    val isEbookUnlocked: Boolean = isFreeEbook,
    val currentChapterNumber: Int = 1,
    val readingProgressPercent: Int = 0,
    val lastReadTimestamp: Long = 0L
)

@Entity(
    tableName = "chapters",
    indices = [Index(value = ["bookId", "chapterNumber"], unique = true)]
)
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val bookId: String,
    val chapterNumber: Int,
    val title: String,
    val content: String,
    val estimatedMinutes: Int,
    val isFreePreview: Boolean
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val bookId: String,
    val bookTitle: String,
    val chapterNumber: Int,
    val chapterTitle: String,
    val quoteSnippet: String,
    val note: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "cart_items",
    indices = [Index(value = ["bookId", "format"], unique = true)]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val bookId: String,
    val format: String, // BookFormats.PRINT or BookFormats.EBOOK
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_addresses")
data class SavedAddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val recipientName: String,
    val phone: String,
    val region: String,
    val streetAddress: String,
    val courierNote: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val orderCode: String,
    val createdAt: Long,
    val recipientName: String,
    val phone: String,
    val region: String,
    val addressLine: String,
    val courierNote: String,
    val deliveryMethod: String,
    val pickupPointName: String,
    val paymentMethod: String,
    val maskedPaymentAccount: String,
    val promoCode: String,
    val subtotalSom: Long,
    val discountSom: Long,
    val deliveryFeeSom: Long,
    val totalSom: Long,
    val status: String,
    val estimatedDeliveryText: String
)

@Entity(
    tableName = "order_items",
    indices = [Index(value = ["orderId"])]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val orderId: Long,
    val bookId: String,
    val bookTitle: String,
    val bookAuthor: String,
    val format: String,
    val quantity: Int,
    val unitPriceSom: Long,
    val coverArtKey: String,
    val coverPaletteIndex: Int
)

@Entity(
    tableName = "reviews",
    indices = [Index(value = ["bookId"])]
)
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val bookId: String,
    val reviewerName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reader_settings")
data class ReaderSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val fontSizeSp: Int = 18,
    val themeMode: String = ReaderThemes.PARCHMENT,
    val useSerifFont: Boolean = true,
    val wideLineSpacing: Boolean = true
)

data class BookWithChapters(
    @Embedded val book: BookEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "bookId"
    )
    val chapters: List<ChapterEntity>
)

data class CartItemWithBook(
    @Embedded val cartItem: CartItemEntity,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "id"
    )
    val book: BookEntity
)

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)
