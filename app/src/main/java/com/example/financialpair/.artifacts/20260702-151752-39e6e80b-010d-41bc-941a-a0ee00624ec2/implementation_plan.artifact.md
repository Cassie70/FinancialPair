# Display Category Names in Topics List

The goal is to replace category IDs with their corresponding names in the `TopicsScreen` list. The most efficient way to achieve this in Room is by using a SQL `JOIN` and a custom POJO to receive the combined data.

## Proposed Changes

### Data Layer
- **[NEW] [TopicWithCategory.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/example/financialpair/data/entity/TopicWithCategory.kt)**: Create a data class to hold `Topic` data along with the `categoryName`.
- **[TopicDao.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/example/financialpair/data/dao/TopicDao.kt)**: Add a new `@Query` that performs a `LEFT JOIN` between `Topic` and `Category` and returns a `Flow<List<TopicWithCategory>>`.
- **[TopicRepository.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/example/financialpair/data/repository/TopicRepository.kt)**: Update the repository to expose the new joined data.

### ViewModel
- **[TopicsScreenState.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/example/financialpair/ui/screens/topics/TopicsScreenState.kt)**: Update the `topics` list type from `List<Topic>` to `List<TopicWithCategory>`.
- **[TopicsScreenViewModel.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/example/financialpair/ui/screens/topics/TopicsScreenViewModel.kt)**: Update the collection of topics to use the new repository method.

### UI Layer
- **[TopicsScreen.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/example/financialpair/ui/screens/topics/TopicsScreen.kt)**:
    - Update the `LazyColumn` to display `topic.categoryName` in the headers instead of `topic.categoryId`.
    - Update `TopicsScreenPreview` to use `TopicWithCategory` objects.

## Verification Plan

### Manual Verification
- Render `TopicsScreenPreview` in Android Studio.
- Verify that the headers in the list show "Transporte" and "Comida" instead of "1" and "2".
- Verify that the individual topic names still appear correctly under their respective headers.
