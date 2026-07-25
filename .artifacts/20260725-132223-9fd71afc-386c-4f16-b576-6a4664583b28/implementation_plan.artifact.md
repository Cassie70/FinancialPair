# Image Caching and Offline Logo Persistence

Currently, the application fetches logo URLs from Firebase Storage at runtime but does not persist these URLs or the images themselves across app restarts without an internet connection. This plan outlines how to enable Coil's disk caching and persist the logo URLs in the Room database.

## Proposed Changes

### Coil Caching

#### [FinancialPairApplication.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/FinancialPairApplication.kt)

- Configure `ImageLoader` to use a disk cache.
- Set `diskCache` to a `DiskCache.Builder` instance, specifying the cache directory and maximum size.
- Enable `crossfade` for smoother image loading transitions.

```kotlin
override fun newImageLoader(): ImageLoader {
    return ImageLoader.Builder(this)
        .components {
            add(SvgDecoder.Factory())
        }
        .diskCache {
            DiskCache.Builder()
                .directory(this.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .crossfade(true)
        .build()
}
```

---

### Data Layer Persistence

#### [Topic.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/data/entity/Topic.kt)

- Add a `logoUrl: String? = null` field to the `Topic` entity.

#### [TopicDao.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/data/dao/TopicDao.kt)

- Add a method to update the `logoUrl` of a `Topic`.

```kotlin
@Query("UPDATE Topic SET logoUrl = :logoUrl WHERE id = :topicId")
suspend fun updateLogoUrl(topicId: Int, logoUrl: String)
```

#### [TopicRepository.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/data/repository/TopicRepository.kt)

- Add a `updateLogoUrl` method that calls the DAO.

```kotlin
suspend fun updateLogoUrl(topicId: Int, logoUrl: String): Result<Unit> =
    runCatching {
        dao.updateLogoUrl(topicId, logoUrl)
    }
```

---

### ViewModel Logic

#### [MovementsScreenViewModel.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/ui/screens/movements/MovementsScreenViewModel.kt)

- Update `fetchMissingLogos` to use the cached `logoUrl` from the `Topic` entity if available.
- When a new URL is successfully fetched from Firebase, call `topicRepository.updateLogoUrl` to persist it.
- Update `uiState` to prioritize the URL fetched from Firebase (or already in the state) but fall back to the one in the `Topic` entity.

---

## Verification Plan

### Automated Tests
- I will check if existing unit tests for `MovementsScreenViewModel` or `TopicRepository` exist and add new ones if appropriate to verify the caching logic.

### Manual Verification
1. **Initial Load**: Run the app with internet connection. Observe logos being fetched and displayed.
2. **Database Check**: (Optional) Use App Inspection to verify `logoUrl` is populated in the `Topic` table.
3. **Offline Mode**: Close the app. Turn off internet connection (Airplane mode).
4. **Offline Relaunch**: Open the app. Verify that logos are still displayed for previously seen movements.
5. **New Movement Offline**: (If applicable) Verify that new movements matching existing topics also show the logo.
