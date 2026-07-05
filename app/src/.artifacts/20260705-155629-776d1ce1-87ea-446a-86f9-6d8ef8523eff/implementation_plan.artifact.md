# Integrate Search into Topics TextField

This plan outlines how to make the name `TextField` act as a search filter for the topics list, while maintaining its function for creating and editing topics.

## Proposed Changes

### UI Layer

#### [TopicsScreenViewModel.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/example/financialpair/ui/screens/topics/TopicsScreenViewModel.kt)

- **Filtered List**: Update the UI state to hold both the full list of topics and a filtered version.
- **Filtering Logic**: In `onNameChange`, if the user is **not** editing a topic, filter the `topics` list based on the input string.
- **Edit vs Search**: When `editingTopic` is null, the text in the `TextField` acts as a search query. When `editingTopic` is NOT null, it acts as the name of the topic being edited (with auto-save).
- **Update Init**: Ensure that whenever the source topics change, the filtered list is updated according to the current text if not editing.

#### [TopicsScreen.kt](file:///C:/Users/Cass/Desktop/FinancialPair/app/src/main/java/com/example/financialpair/ui/screens/topics/TopicsScreen.kt)

- **Display Filtered List**: Ensure the `LazyColumn` displays the filtered topics from the state.
- **Visual Hint**: Optionally change the label of the `TextField` to "Nombre o buscar..." when not editing.

---

## Verification Plan

### Automated Tests
- Build project: `./gradlew app:assembleDebug`

### Manual Verification
1. **Search**: With no topic selected, type in the `TextField`. The list below should filter to show only topics containing that text.
2. **Edit and Auto-save**: Select a topic. Change its name. The list should update (auto-save), and since it's in "edit mode", the filtering should probably pause or be less aggressive to avoid the item disappearing if it no longer matches the search (or we just allow it to match).
3. **Add with Filter**: Type something that doesn't exist, select a category, and click "Añadir". It should create the topic.
4. **Clear Search**: Clear the text to see the full list again.
