# CHANGELOG

## [1.0.9] - 2024-06-24
### Added
- Added the ability to return the specific `View` that was clicked in the item layout for `onItemClickListener` and `onItemLongClickListener`.
- `CHANGELOG.md` file added.

### Changed
- Updated the `setOnItemClickListener` and `setOnItemLongClickListener` methods to accept a lambda that takes both the clicked `View` and the item of type `T`.
- Changed to use the stable version of OkHttp.