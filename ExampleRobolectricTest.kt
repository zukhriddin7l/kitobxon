package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.BookFormats
import com.example.data.local.BookstoreDatabase
import com.example.data.local.DeliveryMethods
import com.example.data.local.OrderStatuses
import com.example.data.local.PaymentMethods
import com.example.data.repository.BookstoreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: BookstoreDatabase
    private lateinit var repository: BookstoreRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BookstoreDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = BookstoreRepository(db.bookstoreDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `app name matches Kitobxon`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Kitobxon", appName)
    }

    @Test
    fun `catalog seeds 10 books with chapters and supports reading and delivery checkout`() = runTest {
        repository.ensureCatalogSeeded()

        val books = repository.allBooks.first()
        assertEquals(10, books.size)

        val otkanKunlar = repository.observeBookWithChapters("otkan_kunlar").first()
        assertNotNull(otkanKunlar)
        assertEquals(4, otkanKunlar!!.chapters.size)
        assertTrue(otkanKunlar.book.isFreeEbook)

        // Paid book starts locked beyond chapter 1, then unlocks after Print/Ebook order
        val atomBefore = repository.observeBookById("atom_odatlar").first()!!
        assertEquals(false, atomBefore.isEbookUnlocked)

        repository.addToCart("atom_odatlar", BookFormats.PRINT)
        val cart = repository.cartItems.first()
        assertEquals(1, cart.size)

        val orderId = repository.placeOrder(
            cartItems = cart,
            recipientName = "Zuxriddin",
            phone = "+998 90 123 45 67",
            region = "Toshkent shahri",
            addressLine = "Amir Temur ko'chasi, 15-uy",
            courierNote = "Qo'ng'iroq qiling",
            deliveryMethod = DeliveryMethods.EXPRESS_COURIER,
            pickupPointName = "",
            paymentMethod = PaymentMethods.CLICK,
            maskedPaymentAccount = "Click (+998 90 123 45 67)",
            promoCode = "KITOBXON10",
            saveAddressForFuture = true
        )

        val createdOrder = repository.observeOrderById(orderId).first()
        assertNotNull(createdOrder)
        assertEquals(OrderStatuses.CONFIRMED, createdOrder!!.order.status)
        // 85,000 - 8,500 (10%) + 20,000 express courier = 96,500
        assertEquals(96_500L, createdOrder.order.totalSom)

        // Cart is cleared and E-book is unlocked automatically
        assertTrue(repository.cartItems.first().isEmpty())
        val atomAfter = repository.observeBookById("atom_odatlar").first()!!
        assertTrue(atomAfter.isEbookUnlocked)
    }
}
