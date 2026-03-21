package com.plcoding.bookpedia.book.presentation.book_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
private fun BookListScreen(
    state: BookListState,
    onAction: (BookListAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val pagerState = rememberPagerState { 2 }
    
    LaunchedEffect(state.selectedTabIndex) {
        if (state.selectedTabIndex in 0..1) {
            pagerState.animateScrollToPage(state.selectedTabIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(BookListAction.OnTabSelected(pagerState.currentPage))
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage = state.errorMessage?.asString()
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            // Note: We don't clear the error action here to avoid recomposition loops if not needed, 
            // but we could send a ClearError action if the state needs it.
        }
    }

    if (state.isMentorDialogVisible) {
        AiMentorDialog(
            state = state,
            onAction = onAction
        )
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.logo_offipedia),
                            contentDescription = "Offipedia Logo",
                            modifier = Modifier.fillMaxWidth(),
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
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    TabRow(
                        selectedTabIndex = state.selectedTabIndex,
                        modifier = Modifier.fillMaxWidth(),
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
                            onClick = { onAction(BookListAction.OnTabSelected(0)) },
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
                            onClick = { onAction(BookListAction.OnTabSelected(1)) },
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
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Powered by Catroid AI",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(BookListAction.OnToggleMentorDialog) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(100.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI Tutor",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                if (pageIndex == 0) {
                    PullToRefreshBox(
                        isRefreshing = state.isLoading,
                        onRefresh = {
                            onAction(BookListAction.OnRefresh)
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        BookList(
                            books = state.searchResults,
                            onBookClick = {
                                onAction(BookListAction.OnBookClick(it))
                            },
                            onAiSummaryClick = { book ->
                                onAction(BookListAction.OnAskAiSummary(book))
                            },
                            modifier = Modifier.fillMaxSize(),
                            emptyMessage = if (state.errorMessage != null) {
                                state.errorMessage.asString()
                            } else {
                                "No search results found"
                            },
                            header = {
                                AiTutorHeaderCard(
                                    summary = state.aiSummary,
                                    isLoading = state.isAiLoading,
                                    onClick = { onAction(BookListAction.OnToggleMentorMode) }
                                )
                            }
                        )
                    }
                } else {
                    BookList(
                        books = state.favoriteBooks,
                        onBookClick = {
                            onAction(BookListAction.OnBookClick(it))
                        },
                        onAiSummaryClick = { book ->
                            onAction(BookListAction.OnAskAiSummary(book))
                        },
                        modifier = Modifier.fillMaxSize(),
                        emptyMessage = "You haven't added any favorites yet."
                    )
                }
            }
        }
    }
}

@Composable
fun AiMentorDialog(
    state: BookListState,
    onAction: (BookListAction) -> Unit
) {
    
    Dialog(
        onDismissRequest = { onAction(BookListAction.OnToggleMentorDialog) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .imePadding(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "AI Mentor",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Row {
                        IconButton(onClick = { onAction(BookListAction.OnClearMentorHistory) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Chat",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        IconButton(onClick = { onAction(BookListAction.OnToggleMentorDialog) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }
                
                // Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (state.mentorChatHistory.isEmpty()) {
                        // Empty state welcome robot
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Hi! I'm your AI Reader Mentor. Ready to discuss any book?")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(state.mentorChatHistory) { chatMessage ->
                                ChatBubble(chatMessage)
                            }
                            
                            if (state.isBookSummaryLoading) {
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(8.dp).fillMaxWidth()
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Mentor is thinking...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }
                            }

                            state.errorMessage?.let { errorText ->
                                item {
                                    Surface(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = errorText.asString(),
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            state.detailedErrorMessage?.let { detail ->
                                                Text(
                                                    text = detail,
                                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                            TextButton(
                                                onClick = { 
                                                    val lastUserMsg = state.mentorChatHistory.lastOrNull { !it.isAi }?.content
                                                    if (lastUserMsg != null) {
                                                        onAction(BookListAction.OnSendChatMessage(lastUserMsg))
                                                    }
                                                },
                                                modifier = Modifier.align(Alignment.End)
                                            ) {
                                                Text("Try Again", color = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Footer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    // Suggested Questions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.suggestedQuestions.forEach { question ->
                            SuggestionChip(
                                text = question,
                                onClick = { 
                                    if (!state.isBookSummaryLoading) {
                                        onAction(BookListAction.OnSuggestedQuestionClick(question)) 
                                    }
                                }
                            )
                        }
                    }
                    
                    // Input Area
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = state.mentorMessage,
                            onValueChange = { onAction(BookListAction.OnMentorMessageChange(it)) },
                            placeholder = { Text("Ask the Mentor...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (state.mentorMessage.isNotBlank()) {
                                    onAction(BookListAction.OnSendChatMessage(state.mentorMessage))
                                }
                            },
                            enabled = state.mentorMessage.isNotBlank() && !state.isBookSummaryLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (state.mentorMessage.isNotBlank()) Color(0xFF1976D2) else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isAi) Alignment.Start else Alignment.End
    ) {
        Surface(
            color = if (message.isAi) Color(0xFFE3F2FD) else Color(0xFF1976D2),
            contentColor = if (message.isAi) Color.Black else Color.White,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isAi) 4.dp else 16.dp,
                bottomEnd = if (message.isAi) 16.dp else 4.dp
            )
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SuggestionChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFF1976D2)),
        color = Color.White
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF1976D2)
        )
    }
}

@Composable
fun AiTutorHeaderCard(
    summary: String?,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD) // Light blue theme
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFBBDEFB))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "AI Reading Mentor & Tutor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    if (summary == null && !isLoading) {
                        Text(
                            text = "Hi there! I can create instant summaries, identify key takeaways, and suggest reading plans. Tap \"Ask for Summary\" on any book or tap here to refresh!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1976D2)
                        )
                    }
                }
            }
            
            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1976D2)
                )
            } else if (summary != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1565C0)
                )
            }
        }
    }
}
