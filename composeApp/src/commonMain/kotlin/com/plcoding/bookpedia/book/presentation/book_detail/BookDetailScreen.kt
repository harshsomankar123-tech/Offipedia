package com.plcoding.bookpedia.book.presentation.book_detail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.plcoding.bookpedia.book.presentation.book_detail.components.BookChip
import com.plcoding.bookpedia.book.presentation.book_detail.components.TitledDetail
import offipedia.composeapp.generated.resources.Res
import offipedia.composeapp.generated.resources.languages
import offipedia.composeapp.generated.resources.pages
import offipedia.composeapp.generated.resources.rating
import offipedia.composeapp.generated.resources.synopsis
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.round

@Composable
fun BookDetailScreenRoot(
    viewModel: BookDetailViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    BookDetailScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is BookDetailAction.OnBackClick -> onBackClick()
                is BookDetailAction.OnReadClick -> {
                    state.book?.let { book ->
                        val url = if (book.coverEditionKey != null) {
                            "https://openlibrary.org/books/${book.coverEditionKey}/read"
                        } else {
                            "https://openlibrary.org/works/${book.id}"
                        }
                        uriHandler.openUri(url)
                    }
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookDetailScreen(
    state: BookDetailState,
    onAction: (BookDetailAction) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { onAction(BookDetailAction.OnBackClick) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = { onAction(BookDetailAction.OnFavoriteClick) }) {
                    Icon(
                        imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (state.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        state.book?.let { book ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = book.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(20.dp),
                        contentScale = ContentScale.Crop,
                        onError = { /* Log */ }
                    )
                    // Fallback for blurred background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                                )
                            )
                    )
                    
                    val imageAlpha = remember { Animatable(0f) }
                    val imageScale = remember { Animatable(0.8f) }
                    val imageRotation = remember { Animatable(-10f) }

                    LaunchedEffect(Unit) {
                        imageAlpha.animateTo(1f, animationSpec = tween(600))
                        imageScale.animateTo(1f, animationSpec = tween(600))
                        imageRotation.animateTo(0f, animationSpec = tween(600))
                    }

                    AsyncImage(
                        model = book.imageUrl,
                        contentDescription = book.title,
                        modifier = Modifier
                            .height(250.dp)
                            .aspectRatio(0.65f)
                            .clip(RoundedCornerShape(8.dp))
                            .graphicsLayer {
                                alpha = imageAlpha.value
                                scaleX = imageScale.value
                                scaleY = imageScale.value
                                rotationZ = imageRotation.value
                            },
                        contentScale = ContentScale.Crop,
                        onError = { /* Log */ }
                    )
                    // Placeholder for main image
                    Box(
                        modifier = Modifier
                            .height(250.dp)
                            .aspectRatio(0.65f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray.copy(alpha = 0.2f))
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.authors.joinToString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        book.averageRating?.let { rating ->
                            BookChip {
                                Text(text = "${round(rating * 10) / 10.0}")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        book.numPages?.let { pages ->
                            BookChip {
                                Text(text = "$pages ${stringResource(Res.string.pages)}")
                            }
                        }
                    }
                    
                    if(book.languages.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TitledDetail(title = stringResource(Res.string.languages)) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                            ) {
                                book.languages.forEach { language ->
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(text = language) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onAction(BookDetailAction.OnReadClick) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = "Read Book")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TitledDetail(
                        title = stringResource(Res.string.synopsis),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if(state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        } else {
                            Text(
                                text = book.description ?: "No description available.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
