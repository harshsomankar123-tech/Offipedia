package com.plcoding.bookpedia.book.presentation.book_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.book.domain.BookTutorRepository
import com.plcoding.bookpedia.book.presentation.toUiText
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.UiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookListViewModel(
    private val bookRepository: BookRepository,
    private val bookTutorRepository: BookTutorRepository
) : ViewModel() {

    private var searchJob: Job? = null
    private var observeFavoritesJob: Job? = null

    private val _state = MutableStateFlow(BookListState())
    val state = _state
        .onStart {
            if(_state.value.searchResults.isEmpty()) {
                observeSearchQuery()
            }
            observeFavoriteBooks()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    fun onAction(action: BookListAction) {
        when(action) {
            is BookListAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
            }
            is BookListAction.OnBookClick -> {
                // To be handled by navigation
            }
            is BookListAction.OnTabSelected -> {
                _state.update { it.copy(selectedTabIndex = action.index) }
            }
            is BookListAction.OnRefresh -> {
                val query = _state.value.searchQuery
                searchJob?.cancel()
                searchJob = if (query.isBlank()) {
                    searchBooks("trending")
                } else {
                    searchBooks(query)
                }
            }
            is BookListAction.OnAskAiSummary -> {
                generateBookAiSummary(action.book)
            }
            is BookListAction.OnDismissSummary -> {
                _state.update { it.copy(selectedBookSummary = null, isMentorDialogVisible = false) }
            }
            is BookListAction.OnToggleMentorMode -> {
                if (_state.value.aiSummary != null) {
                    _state.update { it.copy(aiSummary = null) }
                } else {
                    onAction(BookListAction.OnRefresh)
                }
            }
            is BookListAction.OnToggleMentorDialog -> {
                _state.update { 
                    val newVisible = !it.isMentorDialogVisible
                    if (newVisible && it.mentorChatHistory.isEmpty()) {
                        it.copy(
                            isMentorDialogVisible = true,
                            mentorChatHistory = listOf(ChatMessage("Hi! I'm your AI Reading Mentor. How can I help you with your books today?", isAi = true)),
                            suggestedQuestions = listOf("What can you do?", "How do I use this?", "Tell me a fun fact")
                        )
                    } else {
                        it.copy(isMentorDialogVisible = newVisible)
                    }
                }
            }
            is BookListAction.OnSendChatMessage -> {
                if (!isProcessing) {
                    sendMentorMessage(action.message)
                    _state.update { it.copy(mentorMessage = "") }
                }
            }
            is BookListAction.OnSuggestedQuestionClick -> {
                if (!isProcessing) {
                    sendMentorMessage(action.question)
                }
            }
            is BookListAction.OnMentorMessageChange -> {
                _state.update { it.copy(mentorMessage = action.message) }
            }
            is BookListAction.OnClearMentorHistory -> {
                _state.update { it.copy(mentorChatHistory = emptyList(), detailedErrorMessage = null) }
            }
        }
    }

    private var isProcessing = false
    private fun sendMentorMessage(message: String) {
        if (isProcessing) return 
        isProcessing = true
        
        viewModelScope.launch {
            _state.update { 
                it.copy(
                    mentorChatHistory = it.mentorChatHistory + ChatMessage(message, isAi = false),
                    isBookSummaryLoading = true,
                    errorMessage = null,
                    detailedErrorMessage = null
                ) 
            }
            
            val historyContext = _state.value.mentorChatHistory.takeLast(5).joinToString("\n") { 
                if (it.isAi) "AI: ${it.content}" else "User: ${it.content}"
            }
            
            val prompt = "You are an AI Reading Mentor. Conversation history (last 5 turns):\n" +
                    "$historyContext\n\n" +
                    "Context: This conversation identifies key takeaways and explains concepts from books. " +
                    "Please provide a helpful, educational response to the last user message."
            
            bookTutorRepository.getCustomResponse(prompt)
                .onSuccess { response ->
                    _state.update { 
                        it.copy(
                            mentorChatHistory = it.mentorChatHistory + ChatMessage(response, isAi = true),
                            isBookSummaryLoading = false
                        )
                    }
                    isProcessing = false
                }
                .onError {
                    val detail = bookTutorRepository.lastError
                    val mainError = "AI Mentor is currently unavailable. " + (detail ?: "Please try again.")
                    _state.update { 
                        it.copy(
                            isBookSummaryLoading = false,
                            errorMessage = UiText.DynamicString(mainError),
                            detailedErrorMessage = detail
                        ) 
                    }
                    isProcessing = false
                }
        }
    }

    private fun generateBookAiSummary(book: Book) {
        viewModelScope.launch {
            _state.update { 
                it.copy(
                    isBookSummaryLoading = true,
                    selectedBookSummary = null,
                    isMentorDialogVisible = true, // Show dialog immediately
                    mentorChatHistory = emptyList(),
                    suggestedQuestions = listOf(
                        "Explain key concepts",
                        "Compare with other theories",
                        "Give practical examples"
                    )
                ) 
            }
            bookTutorRepository.getBookSummary(book)
                .onSuccess { summary ->
                    val greeting = "Hi! Let's explore '${book.title}'. How can I assist with your summary and key takeaways?"
                    _state.update { 
                        it.copy(
                            selectedBookSummary = summary,
                            isBookSummaryLoading = false,
                            mentorChatHistory = listOf(
                                ChatMessage(greeting, isAi = true),
                                ChatMessage(summary, isAi = true)
                            )
                        )
                    }
                }
                .onError {
                    _state.update { 
                        it.copy(
                            isBookSummaryLoading = false,
                            isMentorDialogVisible = false,
                            errorMessage = UiText.DynamicString("AI Tutor is currently busy. Please try again later.")
                        ) 
                    }
                }
        }
    }

    private fun observeSearchQuery() {
        state
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L)
            .onEach { query ->
                when {
                    query.isBlank() -> {
                        _state.update { 
                            it.copy(errorMessage = null)
                        }
                        searchJob?.cancel()
                        searchJob = searchBooks("trending")
                    }
                    query.length >= 2 -> {
                        searchJob?.cancel()
                        searchJob = searchBooks(query)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun searchBooks(query: String) = viewModelScope.launch {
        _state.update { 
            it.copy(
                isLoading = true,
                aiSummary = null
            ) 
        }
        bookRepository
            .searchBooks(query)
            .onSuccess { results ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                        searchResults = results
                    )
                }
                if (results.isNotEmpty()) {
                    generateAiSummary(query, results)
                }
            }
            .onError { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.toUiText(),
                        searchResults = emptyList()
                    )
                }
            }
    }

    private fun generateAiSummary(query: String, books: List<Book>) {
        viewModelScope.launch {
            _state.update { it.copy(isAiLoading = true) }
            bookTutorRepository.getSearchSummary(query, books)
                .onSuccess { summary ->
                    _state.update { it.copy(
                        isAiLoading = false,
                        aiSummary = summary
                    ) }
                }
                .onError {
                    val detail = bookTutorRepository.lastError
                    val mainError = "AI Summary failed. " + (detail ?: "Please try again.")
                    _state.update { it.copy(
                        isAiLoading = false,
                        errorMessage = UiText.DynamicString(mainError),
                        detailedErrorMessage = detail
                    ) }
                }
        }
    }

    private fun observeFavoriteBooks() {
        observeFavoritesJob?.cancel()
        observeFavoritesJob = bookRepository
            .getFavoriteBooks()
            .onEach { favoriteBooks ->
                _state.update {
                    it.copy(favoriteBooks = favoriteBooks)
                }
            }
            .launchIn(viewModelScope)
    }
}
