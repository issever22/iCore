# CHANGELOG

# CHANGELOG

## [1.1.5] - 2024-09-27
### Added
- **ActivityExtensions**:
  - Added `fun AppCompatActivity.hideSystemUI()` method to hide system bars on devices running Android R and above.
  - Added `fun AppCompatActivity.showSystemUI()` method to show system bars on devices running Android R and above.

- **EndlessRecyclerViewScrollListener**:
  - Added abstract class `EndlessRecyclerViewScrollListener` for handling endless scrolling, with optional support for a return-to-top `FloatingActionButton` and swipe-to-refresh.

- **Event**:
  - Added `Event` class to handle content that should only be consumed once. `getContentIfNotHandled()` returns the content only if it hasn’t been consumed, while `peekContent()` returns the content regardless.

- **ViewExtensions**:
  - Added `RecyclerView.addOnEndlessScrollListener()` function to add an endless scrolling listener with optional `SwipeRefreshLayout` and return-to-top `FloatingActionButton`.

### Changed
- **ActivityExtensions**:
  - Updated `fun AppCompatActivity.showCustomDialog` function to include `stateType` and `hideIcon` parameters. Now supports showing different icons for success, error, warning, or info states, with an option to hide the icon.

- **BaseViewModel**:
  - Updated the `collectData` function. The `snackbarType` parameter was renamed to `stateType`, and a new `shouldShowError` parameter was added to control whether an error message should be shown.

- **StateType**:
  - Renamed `SnackbarType` enum class to `StateType`, which now includes the following states: `SUCCESS`, `ERROR`, `INFO`, `WARNING`, `DEFAULT`.

- **Theme, Colors, and Styles**:
  - Updated theme and style files. The color palette was refreshed, new style definitions were added, and improvements were made to existing themes.

- **Drawable Files**:
  - Renamed several drawable files for better readability and naming conventions.


## [1.1.2] - 2024-07-26
### Changed
- **BaseAdapter**:
  - Refactored click listener handling to retain original click and long click listeners for views.
  - Added protected getter functions for `onItemViewClickListener`, `onItemClickListener`, `onItemViewLongClickListener`, `onItemLongClickListener`, `onItemViewDoubleClickListener`, and `onItemDoubleClickListener`.
  - Improved `setClickListenerForView` method to correctly manage existing click listeners on views.

### Fixed
- **BaseAdapter**:
  - Fixed an issue where original click and long click listeners were overridden, leading to unexpected behavior.

### Added
- **BaseBottomSheetDialogFragment**:
  - Added `setExpanded()` to expand the bottom sheet.
  - Added `setCollapsed()` to collapse the bottom sheet.
  - Added `setBottomSheetHeight(height: Int)` to set the height of the bottom sheet.
  - Added `dismissBottomSheet()` to hide and dismiss the bottom sheet.
  - Added `setHalfExpanded()` to half-expand the bottom sheet.
  - Added `setInteractionEnabled(enabled: Boolean)` to enable or disable user interaction with the bottom sheet.
  - Added `observeBottomSheetState(onStateChanged: (newState: Int) -> Unit)` to observe state changes of the bottom sheet.

## [1.1.0] - 2024-07-19
### Added
- **BaseAdapter**
  - Added double click listener.
  - Added `setDoubleClickTimeout`.

### Changed
- **BaseAdapter**
  - Split click listener into `ItemClickListener` and `ItemViewClickListener`.
  - Split click listener into `ItemLongClickListener` and `ItemViewLongClickListener`.

### Fixed
- **CoreNetwork**
  - Fixed `LoggingInterceptor` issue.
- **BaseAdapter**
  - Fixed issues related to click listeners.
- **ViewExtensions**
  - Fixed issue in `ImageView.likeAnimation` function and added parameters: `isLiked: Boolean`, `@DrawableRes likeDrawable: Int? = null`, `@DrawableRes dislikeDrawable: Int? = null`.

## [1.0.9] - 2024-06-24
### Added
- Added the ability to return the specific `View` that was clicked in the item layout for `onItemClickListener` and `onItemLongClickListener`.
- Added `CHANGELOG.md` file.

### Changed
- Updated the `setOnItemClickListener` and `setOnItemLongClickListener` methods to accept a lambda that takes both the clicked `View` and the item of type `T`.
- Changed to use the stable version of OkHttp.
