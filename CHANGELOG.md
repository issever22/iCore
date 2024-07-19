# CHANGELOG

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
