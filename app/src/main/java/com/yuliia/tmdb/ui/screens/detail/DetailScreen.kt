package com.yuliia.tmdb.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.yuliia.tmdb.R
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.ui.components.ErrorState
import com.yuliia.tmdb.ui.components.userFriendlyMessage
import com.yuliia.tmdb.ui.theme.RatingYellow
import com.yuliia.tmdb.util.TMDBResult

@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val movieState by viewModel.movie.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val isWatchlisted by viewModel.isWatchlisted.collectAsStateWithLifecycle()

    DetailContent(
        movieState = movieState,
        isRefreshing = isRefreshing,
        isWatchlisted = isWatchlisted,
        onBackClick = onBackClick,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::retry,
        onTrailerClick = { url ->
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        },
        onToggleWatchlist = viewModel::toggleWatchlist
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    movieState: TMDBResult<Movie>,
    isRefreshing: Boolean = false,
    isWatchlisted: Boolean = false,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit = {},
    onRetry: (() -> Unit)? = null,
    onTrailerClick: (String) -> Unit = {},
    onToggleWatchlist: () -> Unit = {}
) {
    BoxWithConstraints {
        val isTablet = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onToggleWatchlist) {
                            Icon(
                                imageVector = if (isWatchlisted) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = if (isWatchlisted) {
                                    stringResource(R.string.remove_from_watchlist)
                                } else {
                                    stringResource(R.string.add_to_watchlist)
                                }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isTablet) MaterialTheme.colorScheme.background else Color.Transparent
                    )
                )
            }
        ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            when (movieState) {
                is TMDBResult.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        val loadingDesc = stringResource(R.string.loading)
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.semantics { contentDescription = loadingDesc }
                        )
                    }
                }

                is TMDBResult.Error -> {
                    ErrorState(
                        message = userFriendlyMessage(movieState.exception),
                        onRetry = onRetry,
                        modifier = Modifier.padding(padding)
                    )
                }

                is TMDBResult.Success -> {
                    if (isTablet) {
                        TabletMovieDetail(
                            movie = movieState.data,
                            onTrailerClick = onTrailerClick,
                            modifier = Modifier.padding(padding)
                        )
                    } else {
                        PhoneMovieDetail(movie = movieState.data, onTrailerClick = onTrailerClick)
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun PhoneMovieDetail(movie: Movie, onTrailerClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        BackdropImage(movie = movie)
        MovieInfo(movie = movie, bottomPadding = 16.dp, onTrailerClick = onTrailerClick)
    }
}

@Composable
private fun TabletMovieDetail(
    movie: Movie,
    onTrailerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        SubcomposeAsyncImage(
            model = movie.posterPath,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            loading = { ImagePlaceholder() },
            error = { ImagePlaceholder() },
            modifier = Modifier
                .weight(1f)
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.width(24.dp))

        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            MovieInfo(movie = movie, bottomPadding = 16.dp, onTrailerClick = onTrailerClick)
        }
    }
}

@Composable
private fun BackdropImage(movie: Movie) {
    Box {
        SubcomposeAsyncImage(
            model = movie.backdropPath ?: movie.posterPath, // some movies only have a poster
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            loading = { ImagePlaceholder() },
            error = { ImagePlaceholder() },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                        startY = 200f
                    )
                )
        )
    }
}

@Composable
private fun MovieInfo(
    movie: Movie,
    bottomPadding: Dp,
    onTrailerClick: (String) -> Unit = {}
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Star,
                contentDescription = stringResource(R.string.rating),
                tint = RatingYellow,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.rating_format).format(movie.voteAverage),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.votes_count, movie.voteCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            if (movie.releaseDate.isNotBlank()) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = movie.releaseDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        if (movie.trailerUrl != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onTrailerClick(movie.trailerUrl) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.watch_trailer))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.overview),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = movie.overview,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
        )

        Spacer(modifier = Modifier.height(bottomPadding))
    }
}

@Composable
private fun ImagePlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}
