package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookEntity
import com.example.data.local.BookFormats
import com.example.data.local.BookWithChapters
import com.example.data.local.BookmarkEntity
import com.example.data.local.CartItemWithBook
import com.example.data.local.DeliveryMethods
import com.example.data.local.OrderEntity
import com.example.data.local.OrderWithItems
import com.example.data.local.ReaderSettingsEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.SavedAddressEntity
import com.example.data.repository.BookstoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class BookSortOption {
    POPULAR,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    HIGHEST_RATED
}

sealed interface StoreUiState {
    data object Loading : StoreUiState
    data class Ready(
        val allBooks: List<BookEntity>,
        val filteredBooks: List<BookEntity>,
        val bestsellers: List<BookEntity>,
        val continueReadingBook: BookEntity?,
        val searchQuery: String,
        val selectedCategory: String,
        val sortOption: BookSortOption
    ) : StoreUiState
}

data class LibraryUiState(
    val currentlyReading: List<BookEntity> = emptyList(),
    val unlockedBooks: List<BookEntity> = emptyList(),
    val favoriteBooks: List<BookEntity> = emptyList(),
    val bookmarks: List<BookmarkEntity> = emptyList()
)

data class CartSummaryState(
    val items: List<CartItemWithBook> = emptyList(),
    val totalItemCount: Int = 0,
    val hasPhysicalItems: Boolean = false,
    val subtotalSom: Long = 0L,
    val promoCode: String = "",
    val discountSom: Long = 0L,
    val remainingForFreeDeliverySom: Long = BookstoreRepository.FREE_DELIVERY_THRESHOLD_SOM
)

class BookstoreViewModel(
    private val repository: BookstoreRepository
) : ViewModel() {

    companion object {
        const val CATEGORY_ALL = "ALL"
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(CATEGORY_ALL)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _sortOption = MutableStateFlow(BookSortOption.POPULAR)
    val sortOption: StateFlow<BookSortOption> = _sortOption.asStateFlow()

    private val _activePromoCode = MutableStateFlow("")
    val activePromoCode: StateFlow<String> = _activePromoCode.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.ensureCatalogSeeded()
        }
    }

    val storeUiState: StateFlow<StoreUiState> = combine(
        repository.allBooks,
        _searchQuery,
        _selectedCategory,
        _sortOption
    ) { books, query, category, sort ->
        if (books.isEmpty()) {
            StoreUiState.Loading
        } else {
            val trimmed = query.trim().lowercase()
            val filtered = books
                .filter { book ->
                    val matchesCategory = category == CATEGORY_ALL || book.category == category
                    val matchesQuery = trimmed.isEmpty() ||
                        book.title.lowercase().contains(trimmed) ||
                        book.author.lowercase().contains(trimmed) ||
                        book.category.lowercase().contains(trimmed)
                    matchesCategory && matchesQuery
                }
                .let { list ->
                    when (sort) {
                        BookSortOption.POPULAR -> list.sortedWith(
                            compareByDescending<BookEntity> { it.isBestseller }
                                .thenByDescending { it.reviewCount }
                        )
                        BookSortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.printPriceSom }
                        BookSortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.printPriceSom }
                        BookSortOption.HIGHEST_RATED -> list.sortedByDescending { it.rating }
                    }
                }

            val continueBook = books
                .filter { it.lastReadTimestamp > 0L }
                .maxByOrNull { it.lastReadTimestamp }

            StoreUiState.Ready(
                allBooks = books,
                filteredBooks = filtered,
                bestsellers = books.filter { it.isBestseller },
                continueReadingBook = continueBook,
                searchQuery = query,
                selectedCategory = category,
                sortOption = sort
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StoreUiState.Loading
    )

    val libraryUiState: StateFlow<LibraryUiState> = combine(
        repository.allBooks,
        repository.allBookmarks
    ) { books, bookmarks ->
        LibraryUiState(
            currentlyReading = books
                .filter { it.readingProgressPercent > 0 || it.lastReadTimestamp > 0L }
                .sortedByDescending { it.lastReadTimestamp },
            unlockedBooks = books.filter { it.isEbookUnlocked || it.isFreeEbook },
            favoriteBooks = books.filter { it.isFavorite },
            bookmarks = bookmarks
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState()
    )

    val cartSummaryState: StateFlow<CartSummaryState> = combine(
        repository.cartItems,
        _activePromoCode
    ) { items, promo ->
        val subtotal = items.sumOf { entry ->
            val unit = if (entry.cartItem.format == BookFormats.PRINT) {
                entry.book.printPriceSom
            } else {
                entry.book.ebookPriceSom
            }
            unit * entry.cartItem.quantity
        }
        val hasPhysical = items.any { it.cartItem.format == BookFormats.PRINT }
        val discount = repository.calculateDiscountSom(subtotal, promo)
        val remaining = (BookstoreRepository.FREE_DELIVERY_THRESHOLD_SOM - subtotal).coerceAtLeast(0L)
        CartSummaryState(
            items = items,
            totalItemCount = items.sumOf { it.cartItem.quantity },
            hasPhysicalItems = hasPhysical,
            subtotalSom = subtotal,
            promoCode = promo,
            discountSom = discount,
            remainingForFreeDeliverySom = remaining
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartSummaryState()
    )

    val allOrders: StateFlow<List<OrderWithItems>> = repository.allOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val savedAddresses: StateFlow<List<SavedAddressEntity>> = repository.savedAddresses.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val readerSettings: StateFlow<ReaderSettingsEntity> = repository.readerSettings
        .map { it ?: ReaderSettingsEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReaderSettingsEntity()
        )

    fun observeBookWithChapters(bookId: String): Flow<BookWithChapters?> =
        repository.observeBookWithChapters(bookId)

    fun observeReviews(bookId: String): Flow<List<ReviewEntity>> =
        repository.observeReviews(bookId)

    fun observeOrderById(orderId: Long): Flow<OrderWithItems?> =
        repository.observeOrderById(orderId)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun onSortChange(sortOption: BookSortOption) {
        _sortOption.value = sortOption
    }

    fun resetStoreFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = CATEGORY_ALL
        _sortOption.value = BookSortOption.POPULAR
    }

    fun applyPromoCode(code: String) {
        val normalized = code.trim().uppercase()
        if (normalized.isEmpty()) {
            _activePromoCode.value = ""
            return
        }
        if (normalized in setOf("KITOBXON10", "MUTOLAA15", "BEPULYETKAZISH")) {
            _activePromoCode.value = normalized
            _snackbarEvents.tryEmit("Promokod muvaffaqiyatli qo'llandi: $normalized")
        } else {
            _snackbarEvents.tryEmit("Bunday promokod topilmadi. KITOBXON10 yoki BEPULYETKAZISH kodini sinab ko'ring.")
        }
    }

    fun clearPromoCode() {
        _activePromoCode.value = ""
    }

    fun calculateDeliveryFee(
        hasPhysicalItems: Boolean,
        subtotalSom: Long,
        deliveryMethod: String,
        promoCode: String
    ): Long = repository.calculateDeliveryFeeSom(hasPhysicalItems, subtotalSom, deliveryMethod, promoCode)

    fun toggleFavorite(book: BookEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(book)
            val msg = if (!book.isFavorite) {
                "\"${book.title}\" sevimlilarga qo'shildi"
            } else {
                "\"${book.title}\" sevimlilardan olindi"
            }
            _snackbarEvents.tryEmit(msg)
        }
    }

    fun addToCart(book: BookEntity, format: String) {
        viewModelScope.launch {
            repository.addToCart(book.id, format)
            val formatName = if (format == BookFormats.PRINT) "Bosma kitob" else "Elektron kitob"
            _snackbarEvents.tryEmit("${book.title} ($formatName) savatchaga qo'shildi")
        }
    }

    fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, newQuantity)
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun recordReadingProgress(bookId: String, chapterNumber: Int, totalChapters: Int) {
        viewModelScope.launch {
            repository.updateReadingProgress(bookId, chapterNumber, totalChapters)
        }
    }

    fun saveBookmark(
        bookId: String,
        bookTitle: String,
        chapterNumber: Int,
        chapterTitle: String,
        quoteSnippet: String,
        note: String
    ) {
        viewModelScope.launch {
            repository.addBookmark(bookId, bookTitle, chapterNumber, chapterTitle, quoteSnippet, note)
            _snackbarEvents.tryEmit("Xatcho'p saqlandi ($chapterTitle)")
        }
    }

    fun deleteBookmark(bookmarkId: Long) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmarkId)
        }
    }

    fun submitReview(book: BookEntity, reviewerName: String, rating: Int, comment: String) {
        viewModelScope.launch {
            repository.addReview(book, reviewerName, rating, comment)
            _snackbarEvents.tryEmit("Fikringiz uchun rahmat!")
        }
    }

    fun updateReaderSettings(settings: ReaderSettingsEntity) {
        viewModelScope.launch {
            repository.saveReaderSettings(settings)
        }
    }

    fun deleteSavedAddress(addressId: Long) {
        viewModelScope.launch {
            repository.deleteSavedAddress(addressId)
        }
    }

    fun submitCheckoutOrder(
        recipientName: String,
        phone: String,
        region: String,
        addressLine: String,
        courierNote: String,
        deliveryMethod: String,
        pickupPointName: String,
        paymentMethod: String,
        maskedPaymentAccount: String,
        saveAddressForFuture: Boolean,
        onOrderCreated: (Long) -> Unit
    ) {
        val currentCart = cartSummaryState.value
        if (currentCart.items.isEmpty()) return
        viewModelScope.launch {
            val orderId = repository.placeOrder(
                cartItems = currentCart.items,
                recipientName = recipientName,
                phone = phone,
                region = region,
                addressLine = addressLine,
                courierNote = courierNote,
                deliveryMethod = if (!currentCart.hasPhysicalItems) DeliveryMethods.DIGITAL_INSTANT else deliveryMethod,
                pickupPointName = pickupPointName,
                paymentMethod = paymentMethod,
                maskedPaymentAccount = maskedPaymentAccount,
                promoCode = currentCart.promoCode,
                saveAddressForFuture = saveAddressForFuture
            )
            _activePromoCode.value = ""
            onOrderCreated(orderId)
        }
    }

    fun advanceOrderDeliveryStatus(order: OrderEntity) {
        viewModelScope.launch {
            repository.advanceOrderStatus(order)
            _snackbarEvents.tryEmit("Buyurtma #${order.orderCode} kuzatuv holati yangilandi")
        }
    }
}

class BookstoreViewModelFactory(
    private val repository: BookstoreRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookstoreViewModel::class.java)) {
            return BookstoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
