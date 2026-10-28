package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.BookFormats
import com.example.data.local.BookstoreDatabase
import com.example.data.repository.BookstoreRepository
import com.example.ui.BookstoreViewModel
import com.example.ui.BookstoreViewModelFactory
import com.example.ui.navigation.BookstoreRoutes
import com.example.ui.screens.BookDetailScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.OrdersHistoryScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.theme.KitobxonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KitobxonTheme {
                KitobxonApp()
            }
        }
    }
}

private data class TopLevelDestination(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun KitobxonApp() {
    val context = LocalContext.current
    val repository = remember(context) {
        BookstoreRepository(BookstoreDatabase.getInstance(context).bookstoreDao())
    }
    val viewModel: BookstoreViewModel = viewModel(
        factory = BookstoreViewModelFactory(repository)
    )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val storeState by viewModel.storeUiState.collectAsStateWithLifecycle()
    val libraryState by viewModel.libraryUiState.collectAsStateWithLifecycle()
    val cartState by viewModel.cartSummaryState.collectAsStateWithLifecycle()
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()
    val savedAddresses by viewModel.savedAddresses.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val topDestinations = remember {
        listOf(
            TopLevelDestination(
                route = BookstoreRoutes.ROUTE_STORE,
                labelRes = R.string.nav_store,
                selectedIcon = Icons.Filled.Storefront,
                unselectedIcon = Icons.Outlined.Storefront,
                testTag = "nav_store_tab"
            ),
            TopLevelDestination(
                route = BookstoreRoutes.ROUTE_LIBRARY,
                labelRes = R.string.nav_library,
                selectedIcon = Icons.Filled.AutoStories,
                unselectedIcon = Icons.Outlined.AutoStories,
                testTag = "nav_library_tab"
            ),
            TopLevelDestination(
                route = BookstoreRoutes.ROUTE_CART,
                labelRes = R.string.nav_cart,
                selectedIcon = Icons.Filled.ShoppingCart,
                unselectedIcon = Icons.Outlined.ShoppingCart,
                testTag = "nav_cart_tab"
            ),
            TopLevelDestination(
                route = BookstoreRoutes.ROUTE_ORDERS,
                labelRes = R.string.nav_orders,
                selectedIcon = Icons.Filled.LocalShipping,
                unselectedIcon = Icons.Outlined.LocalShipping,
                testTag = "nav_orders_tab"
            )
        )
    }

    val isFullScreenRoute = currentRoute == BookstoreRoutes.ROUTE_READER ||
        currentRoute == BookstoreRoutes.ROUTE_CHECKOUT

    fun navigateTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (isExpandedScreen && !isFullScreenRoute) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    topDestinations.forEach { dest ->
                        val selected = currentRoute == dest.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = { navigateTopLevel(dest.route) },
                            icon = {
                                NavIconWithBadge(
                                    dest = dest,
                                    selected = selected,
                                    cartCount = cartState.totalItemCount
                                )
                            },
                            label = { Text(stringResource(dest.labelRes)) },
                            modifier = Modifier.testTag(dest.testTag)
                        )
                    }
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                contentWindowInsets = if (currentRoute == BookstoreRoutes.ROUTE_READER) {
                    WindowInsets(0, 0, 0, 0)
                } else {
                    WindowInsets.safeDrawing
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    if (!isExpandedScreen && !isFullScreenRoute) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            topDestinations.forEach { dest ->
                                val selected = currentRoute == dest.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateTopLevel(dest.route) },
                                    icon = {
                                        NavIconWithBadge(
                                            dest = dest,
                                            selected = selected,
                                            cartCount = cartState.totalItemCount
                                        )
                                    },
                                    label = { Text(stringResource(dest.labelRes)) },
                                    modifier = Modifier.testTag(dest.testTag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = BookstoreRoutes.ROUTE_STORE,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(BookstoreRoutes.ROUTE_STORE) {
                        StoreScreen(
                            uiState = storeState,
                            onSearchQueryChange = viewModel::onSearchQueryChange,
                            onCategorySelect = viewModel::onCategorySelect,
                            onSortChange = viewModel::onSortChange,
                            onResetFilters = viewModel::resetStoreFilters,
                            onBookClick = { id ->
                                navController.navigate(BookstoreRoutes.bookDetail(id))
                            },
                            onReadBookClick = { id, chapter ->
                                navController.navigate(BookstoreRoutes.reader(id, chapter))
                            },
                            onQuickAddPrintToCart = { book ->
                                viewModel.addToCart(book, BookFormats.PRINT)
                            }
                        )
                    }

                    composable(BookstoreRoutes.ROUTE_LIBRARY) {
                        LibraryScreen(
                            state = libraryState,
                            onOpenReader = { id, ch ->
                                navController.navigate(BookstoreRoutes.reader(id, ch))
                            },
                            onOpenBookDetail = { id ->
                                navController.navigate(BookstoreRoutes.bookDetail(id))
                            },
                            onDeleteBookmark = viewModel::deleteBookmark,
                            onBrowseStore = { navigateTopLevel(BookstoreRoutes.ROUTE_STORE) }
                        )
                    }

                    composable(BookstoreRoutes.ROUTE_CART) {
                        CartScreen(
                            cartState = cartState,
                            onUpdateQuantity = viewModel::updateCartQuantity,
                            onRemoveItem = viewModel::removeCartItem,
                            onApplyPromo = viewModel::applyPromoCode,
                            onClearPromo = viewModel::clearPromoCode,
                            onProceedToCheckout = {
                                navController.navigate(BookstoreRoutes.ROUTE_CHECKOUT)
                            },
                            onBrowseStore = { navigateTopLevel(BookstoreRoutes.ROUTE_STORE) }
                        )
                    }

                    composable(BookstoreRoutes.ROUTE_ORDERS) {
                        OrdersHistoryScreen(
                            orders = orders,
                            savedAddresses = savedAddresses,
                            onAdvanceOrderStatus = viewModel::advanceOrderDeliveryStatus,
                            onDeleteAddress = viewModel::deleteSavedAddress,
                            onBrowseStore = { navigateTopLevel(BookstoreRoutes.ROUTE_STORE) }
                        )
                    }

                    composable(
                        route = BookstoreRoutes.ROUTE_BOOK_DETAIL,
                        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                    ) { entry ->
                        val bookId = entry.arguments?.getString("bookId") ?: return@composable
                        BookDetailScreen(
                            bookId = bookId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onOpenReader = { id, ch ->
                                navController.navigate(BookstoreRoutes.reader(id, ch))
                            },
                            onGoToCart = { navigateTopLevel(BookstoreRoutes.ROUTE_CART) }
                        )
                    }

                    composable(
                        route = BookstoreRoutes.ROUTE_READER,
                        arguments = listOf(
                            navArgument("bookId") { type = NavType.StringType },
                            navArgument("chapter") {
                                type = NavType.IntType
                                defaultValue = 1
                            }
                        )
                    ) { entry ->
                        val bookId = entry.arguments?.getString("bookId") ?: return@composable
                        val chapter = entry.arguments?.getInt("chapter") ?: 1
                        ReaderScreen(
                            bookId = bookId,
                            initialChapter = chapter,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onGoToCart = { navigateTopLevel(BookstoreRoutes.ROUTE_CART) }
                        )
                    }

                    composable(BookstoreRoutes.ROUTE_CHECKOUT) {
                        CheckoutScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onOrderCompleted = { createdOrderId ->
                                navController.navigate(BookstoreRoutes.orderSuccess(createdOrderId)) {
                                    popUpTo(BookstoreRoutes.ROUTE_STORE)
                                }
                            }
                        )
                    }

                    composable(
                        route = BookstoreRoutes.ROUTE_ORDER_SUCCESS,
                        arguments = listOf(navArgument("orderId") { type = NavType.LongType })
                    ) { entry ->
                        val orderId = entry.arguments?.getLong("orderId") ?: return@composable
                        OrderSuccessScreen(
                            orderId = orderId,
                            viewModel = viewModel,
                            onGoToOrders = {
                                navController.navigate(BookstoreRoutes.ROUTE_ORDERS) {
                                    popUpTo(BookstoreRoutes.ROUTE_STORE)
                                }
                            },
                            onGoToLibrary = {
                                navController.navigate(BookstoreRoutes.ROUTE_LIBRARY) {
                                    popUpTo(BookstoreRoutes.ROUTE_STORE)
                                }
                            },
                            onBackToStore = {
                                navController.popBackStack(BookstoreRoutes.ROUTE_STORE, inclusive = false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavIconWithBadge(
    dest: TopLevelDestination,
    selected: Boolean,
    cartCount: Int
) {
    val iconVec = if (selected) dest.selectedIcon else dest.unselectedIcon
    if (dest.route == BookstoreRoutes.ROUTE_CART && cartCount > 0) {
        BadgedBox(
            badge = {
                Badge {
                    Text("$cartCount")
                }
            }
        ) {
            Icon(
                imageVector = iconVec,
                contentDescription = stringResource(dest.labelRes)
            )
        }
    } else {
        Icon(
            imageVector = iconVec,
            contentDescription = stringResource(dest.labelRes)
        )
    }
}
