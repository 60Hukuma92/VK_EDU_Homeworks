package com.vkedu.rickandmortyapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterScreen(viewModel: CharacterViewModel = viewModel()) {
    val characters = viewModel.characters.collectAsLazyPagingItems()
    var selectedCharacter by remember { mutableStateOf<Character?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(minSize = 128.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = characters.itemCount,
                key = characters.itemKey { it.id }
            ) { index ->
                val character = characters[index]
                if (character != null) {
                    CharacterImage(character = character, onClick = { selectedCharacter = character })
                }
            }

            when (val state = characters.loadState.append) {
                is LoadState.Loading -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is LoadState.Error -> {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        ErrorState(message = state.error.localizedMessage ?: stringResource(id = R.string.error)) {
                            characters.retry()
                        }
                    }
                }
                else -> {}
            }
        }

        if (characters.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (characters.loadState.refresh is LoadState.Error) {
            val e = characters.loadState.refresh as LoadState.Error
            ErrorState(modifier = Modifier.align(Alignment.Center), message = e.error.localizedMessage ?: stringResource(id = R.string.error)) {
                characters.retry()
            }
        }

        selectedCharacter?.let {
            CharacterDetailsDialog(character = it, onDismiss = { selectedCharacter = null })
        }
    }
}

@Composable
fun CharacterImage(character: Character, onClick: () -> Unit) {
    val possibleHeights = remember { listOf(180.dp, 220.dp, 260.dp) }
    val height = possibleHeights[character.id % possibleHeights.size]

    AsyncImage(
        model = character.image,
        contentDescription = stringResource(R.string.character_image_content_description, character.name),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    )
}

@Composable
fun CharacterDetailsDialog(character: Character, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = character.name, style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HorizontalDivider()
                DetailRow(label = stringResource(R.string.details_id), value = character.id.toString())
                DetailRow(label = stringResource(R.string.details_status), value = character.status)
                DetailRow(label = stringResource(R.string.details_species), value = character.species)
                if (character.type.isNotEmpty()) {
                    DetailRow(label = stringResource(R.string.details_type), value = character.type)
                } else {
                    DetailRow(label = stringResource(R.string.details_type), value = stringResource(R.string.details_type_unknown))
                }
                DetailRow(label = stringResource(R.string.details_location), value = character.location.name)
                val firstEpisodeNumber = character.episode.firstOrNull()?.split("/")?.lastOrNull()
                DetailRow(
                    label = stringResource(R.string.details_first_seen),
                    value = if (firstEpisodeNumber != null) stringResource(R.string.details_episode, firstEpisodeNumber) else stringResource(R.string.episode_data_not_available)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.close))
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$label: ", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(text = value, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Composable
fun ErrorState(modifier: Modifier = Modifier, message: String, onRetry: () -> Unit) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.something_went_wrong),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}
