# Optimal Logo Loading Implementation Plan

The goal is to display logos for each topic in the movements list efficiently. Currently, `FPMovement` makes a Firebase Storage request for every item, which is slow and redundant. We will optimize this by linking movements with topics in the database and caching the download URLs in the ViewModel.

## User Review Required

- **Data Structure Change**: I'm introducing a `MovementWithTopic` class to join `Movement` and `Topic` tables. This is standard practice in Room.
- **URL Caching**: I'll implement a simple URL cache in the ViewModel to avoid re-fetching the same logo URL multiple times for different movements under the same topic.

## Proposed Changes

### Data Layer

#### [NEW] [MovementWithTopic.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/data/entity/MovementWithTopic.kt)

- Define a Room relation to join `Movement` and `Topic`.

```kotlin
package com.juni7.financialpair.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class MovementWithTopic(
    @Embedded val movement: Movement,
    @Relation(
        parentColumn = "topicId",
        entityColumn = "id"
    )
    val topic: Topic
)
```

#### [MovementDao.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/data/dao/MovementDao.kt)

- Update `observeAll` to return `Flow<List<MovementWithTopic>>`.

```diff
-    @Query("SELECT * FROM movement ORDER BY id DESC")
-    fun observeAll(): Flow<List<Movement>>
+    @androidx.room.Transaction
+    @Query("SELECT * FROM movement ORDER BY id DESC")
+    fun observeAll(): Flow<List<MovementWithTopic>>
```

#### [MovementRepository.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/data/repository/MovementRepository.kt)

- Update the `movements` property to reflect the new return type.

---

### UI Layer

#### [MovementsScreenState.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/ui/screens/movements/MovementsScreenState.kt)

- Change `movements` list type to `List<MovementWithTopic>`.
- Add a `logoUrls` map to cache fetched URLs: `Map<String, String>` (Topic Name -> URL).

#### [MovementsScreenViewModel.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/ui/screens/movements/MovementsScreenViewModel.kt)

- Update state collection to handle `MovementWithTopic`.
- Implement a method to fetch and cache logo URLs when new movements appear.

#### [FPMovement.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/ui/components/FPMovement.kt)

- Remove the `await()` call inside the Composable.
- Accept the logo URL as a parameter or fetch it from the state.

#### [MovementsScreen.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/juni7/financialpair/ui/screens/movements/MovementsScreen.kt)

- Pass the cached logo URL to `FPMovement`.

## Verification Plan

### Automated Tests
- I'll check if the Room build still passes after the relation change.
- `gradle_build("app:assembleDebug")`

### Manual Verification
- I will run the app and observe the logo loading behavior.
- Use `take_screenshot` to verify logos are displayed.
- Use `read_logcat` to ensure no excessive network calls or errors are occurring.
