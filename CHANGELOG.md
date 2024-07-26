# CHANGELOG

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
