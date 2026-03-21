package com.plcoding.bookpedia.book.presentation.book_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.plcoding.bookpedia.book.domain.Book
import kotlin.math.round

@Composable
fun BookListItem(
    book: Book,
    onAiSummaryClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .clickable(onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxHeight()
                        .aspectRatio(0.65f),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray.copy(alpha = 0.2f))
                    )
                    AsyncImage(
                        model = book.imageUrl,
                        contentDescription = book.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    book.authors.firstOrNull()?.let { author ->
                        Text(
                            text = author,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    book.averageRating?.let { rating ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${round(rating * 10) / 10.0}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedButton(
                onClick = onAiSummaryClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ask for Summary & Key Takeaways",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
