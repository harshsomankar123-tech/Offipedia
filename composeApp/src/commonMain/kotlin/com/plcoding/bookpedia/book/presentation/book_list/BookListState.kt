package com.plcoding.bookpedia.book.presentation.book_list

import com.plcoding.bookpedia.book.domain.Book
import com.plcoding.bookpedia.core.presentation.UiText

data class BookListState(
    val searchQuery: String = "",
    val searchResults: List<Book> = emptyList(),
    val favoriteBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTabIndex: Int = 0,
    val errorMessage: UiText? = null,
    val aiSummary: String? = null,
    val aiRecommendations: List<String> = emptyList(),
    val isAiLoading: Boolean = false,
    val selectedBookSummary: String? = null,
    val isBookSummaryLoading: Boolean = false,
    val isMentorDialogVisible: Boolean = false,
    val mentorChatHistory: List<ChatMessage> = emptyList(),
    val suggestedQuestions: List<String> = emptyList(),
    val mentorMessage: String = "",
    val detailedErrorMessage: String? = null
)

data class ChatMessage(
    val content: String,
    val isAi: Boolean,
    val detailedErrorMessage: String? = null
)

sealed interface BookListAction {
    data class OnSearchQueryChange(val query: String) : BookListAction
    data class OnBookClick(val book: Book) : BookListAction
    data class OnTabSelected(val index: Int) : BookListAction
    data class OnAskAiSummary(val book: Book) : BookListAction
    data object OnDismissSummary : BookListAction
    data object OnToggleMentorMode : BookListAction
    data object OnToggleMentorDialog : BookListAction
    data class OnSendChatMessage(val message: String) : BookListAction
    data class OnSuggestedQuestionClick(val question: String) : BookListAction
    data class OnMentorMessageChange(val message: String) : BookListAction
    data object OnClearMentorHistory : BookListAction
    data object OnRefresh : BookListAction
}
