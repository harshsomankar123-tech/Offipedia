package com.plcoding.bookpedia.book.presentation.book_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.presentation.book_list.components.BookList
import com.plcoding.bookpedia.book.presentation.book_list.components.BookSearchBar
import offipedia.composeapp.generated.resources.Res
import offipedia.composeapp.generated.resources.favorites
import offipedia.composeapp.generated.resources.logo_offipedia
import offipedia.composeapp.generated.resources.search_results
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BookListScreenRoot(
    viewModel: BookListViewModel = koinViewModel(),
    onBookClick: (Book) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BookListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is BookListAction.OnBookClick -> onBookClick(action.book)
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    state: BookListState,
    onAction: (BookListAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val pagerState = rememberPagerState { 2 }
    val searchResultsListState = androidx.compose.foundation.lazy.rememberLazyListState()
    val favoriteBooksListState = androidx.compose.foundation.lazy.rememberLazyListState()

    val density = androidx.compose.ui.platform.LocalDensity.current
    val maxHeaderHeight = 180.dp
    val maxHeaderHeightPx = with(density) { maxHeaderHeight.toPx() }
    var headerOffsetHeightPx by remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                val newOffset = headerOffsetHeightPx + delta
                val oldOffset = headerOffsetHeightPx
                headerOffsetHeightPx = newOffset.coerceIn(-maxHeaderHeightPx, 0f)
                val consumed = headerOffsetHeightPx - oldOffset
                return Offset(0f, consumed)
            }
        }
    }

    LaunchedEffect(state.selectedTabIndex) {
        if (state.selectedTabIndex in 0..1) {
            pagerState.animateScrollToPage(state.selectedTabIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(BookListAction.OnTabSelected(pagerState.currentPage))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .nestedScroll(nestedScrollConnection)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = headerOffsetHeightPx
                }
                .zIndex(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxHeaderHeight),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo_offipedia),
                    contentDescription = "Offipedia Logo",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
            }

            BookSearchBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = {
                    onAction(BookListAction.OnSearchQueryChange(it))
                },
                onImeSearch = {
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Start below the search bar. 
                    // Search bar total height is roughly 56 (bar) + 32 (vertical padding) = 88.dp
                    translationY = headerOffsetHeightPx + maxHeaderHeightPx + with(density) { 88.dp.toPx() }
                },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TabRow(
                    selectedTabIndex = state.selectedTabIndex,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(),
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTabIndex])
                        )
                    }
                ) {
                    Tab(
                        selected = state.selectedTabIndex == 0,
                        onClick = {
                            onAction(BookListAction.OnTabSelected(0))
                        },
                        modifier = Modifier.weight(1f),
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = Color.Gray
                    ) {
                        Text(
                            text = stringResource(Res.string.search_results),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                    Tab(
                        selected = state.selectedTabIndex == 1,
                        onClick = {
                            onAction(BookListAction.OnTabSelected(1))
                        },
                        modifier = Modifier.weight(1f),
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = Color.Gray
                    ) {
                        Text(
                            text = stringResource(Res.string.favorites),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.Top
                ) { pageIndex ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (pageIndex == 0) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                BookList(
                                    books = state.searchResults,
                                    onBookClick = {
                                        onAction(BookListAction.OnBookClick(it))
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                    emptyMessage = if (state.errorMessage != null) {
                                        state.errorMessage.asString()
                                    } else {
                                        "No search results found"
                                    }
                                )
                            }
                        } else {
                            BookList(
                                books = state.favoriteBooks,
                                onBookClick = {
                                    onAction(BookListAction.OnBookClick(it))
                                },
                                modifier = Modifier.fillMaxSize(),
                                emptyMessage = "You haven't added any favorites yet."
                            )
                        }
                    }
                }
            }
        }
    }
}
