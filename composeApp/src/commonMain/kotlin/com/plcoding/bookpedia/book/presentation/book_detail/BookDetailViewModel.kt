package com.plcoding.bookpedia.book.presentation.book_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.book.domain.BookRepository
import com.plcoding.bookpedia.book.domain.BookTutorRepository
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.getOrNull
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookRepository: BookRepository,
    private val bookTutorRepository: BookTutorRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId = try {
        savedStateHandle.toRoute<Route.BookDetail>().id
    } catch (e: Exception) {
        savedStateHandle.get<String>("id") ?: ""
    }

    private val _state = MutableStateFlow(BookDetailState())
    val state = _state
        .onStart {
            fetchBookDescription()
            observeFavoriteStatus()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    fun onAction(action: BookDetailAction) {
        when(action) {
            is BookDetailAction.OnSelectedBookChange -> {
                _state.update { 
                    it.copy(book = action.book)
                }
            }
            is BookDetailAction.OnFavoriteClick -> {
                viewModelScope.launch {
                    if(state.value.isFavorite) {
                        bookRepository.deleteFromFavorites(bookId)
                    } else {
                        state.value.book?.let { book ->
                            bookRepository.markAsFavorite(book)
                        }
                    }
                }
            }
            else -> Unit
        }
    }

    private fun fetchBookDescription() {
        viewModelScope.launch {
            bookRepository
                .getBookDescription(bookId)
                .onSuccess { summary ->
                    _state.update { it.copy(
                        isLoading = false,
                        book = it.book?.copy(description = summary)
                    ) }
                    _state.value.book?.let { book ->
                        fetchAiTutorInfo(book)
                    }
                }
                .onError {
                    _state.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun fetchAiTutorInfo(book: Book) {
        viewModelScope.launch {
            _state.update { it.copy(isAiLoading = true) }
            
            val summaryResult = bookTutorRepository.getBookSummary(book)
            val recommendationsResult = bookTutorRepository.getRecommendations(book)
            
            _state.update { state ->
                state.copy(
                    aiTutorSummary = summaryResult.onSuccess { it }.getOrNull(),
                    aiRecommendations = recommendationsResult.onSuccess { it }.getOrNull() ?: emptyList(),
                    isAiLoading = false
                )
            }
        }
    }

    private fun observeFavoriteStatus() {
        bookRepository
            .isBookFavorite(bookId)
            .onEach { isFavorite ->
                _state.update {
                    it.copy(isFavorite = isFavorite)
                }
            }
            .launchIn(viewModelScope)
    }
}
