# TMDB

An Android app for browsing trending movies, searching by title, and viewing details — built with Jetpack Compose and clean architecture.

## Setup

You'll need a TMDB API key to run the app. It's free:

1. Create an account at [themoviedb.org](https://www.themoviedb.org/)
2. Go to Settings > API > Request an API Key
3. Add your key to `local.properties` in the project root:

```properties
TMDB_API_KEY=your_api_key_here
```

That's it — build and run.

## Tech stack

- **UI:** Jetpack Compose, Material 3, Coil
- **Architecture:** Clean Architecture, MVVM
- **Networking:** Retrofit, Kotlinx Serialization, OkHttp
- **Local storage:** Room (offline cache for trending + movie details)
- **Pagination:** Paging 3 with RemoteMediator (trending) and PagingSource (search)
- **DI:** Hilt
- **Navigation:** Jetpack Navigation Compose
- **Testing:** JUnit, MockK, Compose UI tests

## Features

- Browse trending movies with infinite scroll
- Search movies by title with debounced input
- Movie detail screen with rating, overview, and YouTube trailer link
- Offline support — cached trending movies and details load without network
- Pull-to-refresh on detail screen
- Adaptive layout for phones and tablets
- Error states with retry
