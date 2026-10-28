package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BookstoreDao {

    // --- Books & Chapters ---
    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Query("SELECT * FROM books")
    fun observeAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    fun observeBookById(bookId: String): Flow<BookEntity?>

    @Query("SELECT * FROM chapters WHERE bookId = :bookId ORDER BY chapterNumber ASC")
    fun observeChaptersForBook(bookId: String): Flow<List<ChapterEntity>>

    @Transaction
    @Query("SELECT * FROM books WHERE id = :bookId LIMIT 1")
    fun observeBookWithChapters(bookId: String): Flow<BookWithChapters?>

    @Query("UPDATE books SET isFavorite = :isFavorite WHERE id = :bookId")
    suspend fun updateFavorite(bookId: String, isFavorite: Boolean)

    @Query(
        """
        UPDATE books 
        SET currentChapterNumber = :chapterNumber,
            readingProgressPercent = :progressPercent,
            lastReadTimestamp = :timestamp
        WHERE id = :bookId
        """
    )
    suspend fun updateReadingProgress(
        bookId: String,
        chapterNumber: Int,
        progressPercent: Int,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE books SET isEbookUnlocked = 1 WHERE id IN (:bookIds)")
    suspend fun unlockEbooks(bookIds: List<String>)

    // --- Bookmarks ---
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun observeAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun observeBookmarksForBook(bookId: String): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :bookmarkId")
    suspend fun deleteBookmark(bookmarkId: Long)

    // --- Reviews ---
    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY createdAt DESC")
    fun observeReviewsForBook(bookId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("UPDATE books SET rating = :newRating, reviewCount = :newCount WHERE id = :bookId")
    suspend fun updateBookRatingSummary(bookId: String, newRating: Float, newCount: Int)

    // --- Cart ---
    @Transaction
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun observeCartItemsWithBooks(): Flow<List<CartItemWithBook>>

    @Query("SELECT * FROM cart_items WHERE bookId = :bookId AND format = :format LIMIT 1")
    suspend fun findCartItem(bookId: String, format: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :cartItemId")
    suspend fun updateCartItemQuantity(cartItemId: Long, quantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :cartItemId")
    suspend fun deleteCartItem(cartItemId: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // --- Saved Addresses ---
    @Query("SELECT * FROM saved_addresses ORDER BY createdAt DESC")
    fun observeSavedAddresses(): Flow<List<SavedAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedAddress(address: SavedAddressEntity)

    @Query("DELETE FROM saved_addresses WHERE id = :addressId")
    suspend fun deleteSavedAddress(addressId: Long)

    // --- Orders ---
    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun observeOrdersWithItems(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun observeOrderById(orderId: Long): Flow<OrderWithItems?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("UPDATE orders SET status = :newStatus WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, newStatus: String)

    @Transaction
    suspend fun placeOrderTransaction(
        order: OrderEntity,
        items: List<OrderItemEntity>,
        unlockedBookIds: List<String>,
        addressToSave: SavedAddressEntity?
    ): Long {
        val orderId = insertOrder(order)
        val itemsWithOrderId = items.map { it.copy(orderId = orderId) }
        insertOrderItems(itemsWithOrderId)
        if (unlockedBookIds.isNotEmpty()) {
            unlockEbooks(unlockedBookIds)
        }
        if (addressToSave != null) {
            insertSavedAddress(addressToSave)
        }
        clearCart()
        return orderId
    }

    // --- Reader Settings ---
    @Query("SELECT * FROM reader_settings WHERE id = 1 LIMIT 1")
    fun observeReaderSettings(): Flow<ReaderSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReaderSettings(settings: ReaderSettingsEntity)
}
