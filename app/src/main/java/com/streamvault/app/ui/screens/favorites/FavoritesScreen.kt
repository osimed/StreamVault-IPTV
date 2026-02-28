package com.streamvault.app.ui.screens.favorites

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.*
import com.streamvault.app.ui.components.TopNavBar
import com.streamvault.app.ui.theme.*
import com.streamvault.domain.model.ContentType
import androidx.compose.ui.res.stringResource
import com.streamvault.app.R
@Composable
fun FavoritesScreen(
    onItemClick: (FavoriteUiModel) -> Unit,
    onNavigate: (String) -> Unit,
    currentRoute: String,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopNavBar(currentRoute = currentRoute, onNavigate = onNavigate)

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.favorites_loading), style = MaterialTheme.typography.bodyLarge, color = OnSurface)
            }
        } else if (uiState.favorites.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⭐", style = MaterialTheme.typography.displayLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.favorites_no_favorites), style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                    Text(
                        stringResource(R.string.favorites_empty_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceDim
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main favorites list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Virtual groups
                    if (uiState.groups.isNotEmpty()) {
                        items(uiState.groups, key = { it.id }) { group ->
                            Text(
                                text = "${group.iconEmoji ?: "📁"} ${group.name}",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnBackground,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    // All favorites
                    items(uiState.favorites, key = { it.favorite.id }) { item ->
                        val isReorderingThis = uiState.isReorderMode && uiState.reorderItem?.id == item.favorite.id
                        val scale by animateFloatAsState(if (isReorderingThis) 1.05f else 1f)
                        
                        Surface(
                            onClick = {
                                if (uiState.isReorderMode) {
                                    if (isReorderingThis) viewModel.saveReorder()
                                } else {
                                    if (item.favorite.contentType == ContentType.SERIES) {
                                        onNavigate("series_detail/${item.favorite.contentId}")
                                    } else {
                                        onItemClick(item)
                                    }
                                }
                            },
                            onLongClick = {
                                viewModel.enterReorderMode(item)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .scale(scale)
                                .then(
                                    if (isReorderingThis) {
                                        Modifier.onKeyEvent { event ->
                                            if (event.type == KeyEventType.KeyDown) {
                                                when (event.nativeKeyEvent.keyCode) {
                                                    android.view.KeyEvent.KEYCODE_DPAD_UP -> {
                                                        viewModel.moveItem(-1)
                                                        true
                                                    }
                                                    android.view.KeyEvent.KEYCODE_DPAD_DOWN -> {
                                                        viewModel.moveItem(1)
                                                        true
                                                    }
                                                    android.view.KeyEvent.KEYCODE_DPAD_CENTER, android.view.KeyEvent.KEYCODE_ENTER -> {
                                                        viewModel.saveReorder()
                                                        true
                                                    }
                                                    android.view.KeyEvent.KEYCODE_BACK -> {
                                                        viewModel.exitReorderMode()
                                                        true
                                                    }
                                                    else -> false
                                                }
                                            } else false
                                        }
                                    } else Modifier
                                ),
                            shape = ClickableSurfaceDefaults.shape(
                                RoundedCornerShape(8.dp)
                            ),
                            colors = ClickableSurfaceDefaults.colors(
                                containerColor = if (isReorderingThis) Primary.copy(alpha = 0.2f) else SurfaceElevated,
                                focusedContainerColor = if (isReorderingThis) Primary else SurfaceHighlight,
                                contentColor = if (isReorderingThis) Primary else OnSurface,
                                focusedContentColor = if (isReorderingThis) OnPrimary else OnSurface
                            ),
                            border = ClickableSurfaceDefaults.border(
                                border = Border(
                                    border = BorderStroke(
                                        width = if (isReorderingThis) 2.dp else 0.dp,
                                        color = if (isReorderingThis) Primary else Color.Transparent
                                    )
                                )
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isReorderingThis) Primary else OnSurface
                                )
                                Spacer(Modifier.weight(1f))
                                if (isReorderingThis) {
                                    Text("↕ " + stringResource(R.string.favorites_reordering), style = MaterialTheme.typography.bodySmall, color = Primary)
                                } else {
                                    Text(
                                        text = item.subtitle ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceDim
                                    )
                                }
                            }
                        }
                    }
                }

                // Reorder mode hint — compact top-right corner overlay
                androidx.compose.animation.AnimatedVisibility(
                    visible = uiState.isReorderMode,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .width(220.dp)
                            .heightIn(max = 280.dp)
                            .background(CardBackground.copy(alpha = 0.97f), RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header
                        Text(
                            text = "↕  Reorder Mode",
                            style = MaterialTheme.typography.titleSmall,
                            color = Primary
                        )

                        // Button hints
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ReorderHintRow(icon = "▲", label = "Move Up")
                            ReorderHintRow(icon = "▼", label = "Move Down")
                            ReorderHintRow(icon = "●", label = "Confirm")
                            ReorderHintRow(icon = "← Back", label = "Cancel")
                        }

                        // Mini-map — only show up to 8 items to keep panel compact
                        val movingId = uiState.reorderItem?.id
                        Column(
                            modifier = Modifier
                                .background(SurfaceElevated.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            uiState.favorites.take(10).forEach { item ->
                                val isMoving = movingId == item.favorite.id
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isMoving) "▶ " else "   ",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Primary
                                    )
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isMoving) Primary else OnSurfaceDim,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (uiState.isReorderMode) {
        BackHandler {
            viewModel.exitReorderMode()
        }
    }
}

@Composable
private fun ReorderHintRow(icon: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(SurfaceHighlight, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(icon, style = MaterialTheme.typography.labelSmall, color = Primary)
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceDim)
    }
}


