package com.example.ui.navigation

object BookstoreRoutes {
    const val ROUTE_STORE = "store"
    const val ROUTE_LIBRARY = "library"
    const val ROUTE_CART = "cart"
    const val ROUTE_ORDERS = "orders"

    const val ROUTE_BOOK_DETAIL = "book_detail/{bookId}"
    const val ROUTE_READER = "reader/{bookId}/{chapter}"
    const val ROUTE_CHECKOUT = "checkout"
    const val ROUTE_ORDER_SUCCESS = "order_success/{orderId}"

    fun bookDetail(bookId: String): String = "book_detail/$bookId"
    fun reader(bookId: String, chapter: Int = 0): String = "reader/$bookId/$chapter"
    fun orderSuccess(orderId: Long): String = "order_success/$orderId"
}
