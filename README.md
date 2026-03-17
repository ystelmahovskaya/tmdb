# TMDB

A movie browsing app built with Jetpack Compose. Search movies, save them to your watchlist, watch trailers — works offline too.

## Setup

You'll need a free TMDB API key:

1. Sign up at [themoviedb.org](https://www.themoviedb.org/)
2. Go to Settings > API > Request an API Key
3. Add it to `local.properties`:

```properties
TMDB_API_KEY=your_api_key_here
```

Build and run.

## What it does

- **Trending** infinite scroll of this week's trending movies, cached locally
- **Search** find movies by title, debounced so it doesn't spam the API
- **Details** ratings, overview, release date, and a "Watch Trailer" button that opens YouTube
- **Watchlist** save movies locally, swipe to remove. Syncs across screens in real time via Room
- **Offline** trending movies and details load from cache when there's no network
- **Tablet** adaptive layout with side-by-side poster + info on wide screens

## Tech

- **UI:** Jetpack Compose, Material 3, Coil for images
- **Architecture:** Clean Architecture / MVVM with use cases
- **Network:** Retrofit + Kotlinx Serialization, retry with exponential backoff
- **Persistence:** Room with migrations, RemoteMediator for trending pagination
- **Pagination:** Paging 3  RemoteMediator for trending (offline-capable), PagingSource for search
- **DI:** Hilt
- **Navigation:** Compose Navigation with bottom bar (Trending / Watchlist)
- **Testing:** JUnit + MockK for unit tests, Compose UI tests for screens and navigation
