# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## [0.1.6-1] - 2019-12-04
### Fixed
- fix broken -o command line option - #40
- fix spit function error

## [0.1.6] - 2019-12-04
### Fixed
- nice print edamame exceptions - #30
- <header> tags in loaded markup munged - #31
- fix slurp to return string not input stream - #34
- fix hiccup-seq containing nil causes NPE on conversion - #37

### Changed
- clojure.core namespace integration for borkdude/sci#130 - #23
- path capture to use thread local - #24 #28
- improve documentation around hiccup and hiccup-seq - #35 #36
- upgraded to latest sci and edamame to fix some reader issues

### Added
- more enlive documentation in readme - #25
- support for reagent :style hashmaps - #32
- docs on building binary - #33

## [0.1.5] - 2019-11-8
### Fixed
- File Not Found error when yaml file missing - #17
- Github actions - #22
- Friendly error reporting - #8

### Added
- MacOS build - #14
- Windows build - #15
- Selmer template support - #16
- Enlive macros - #13
- Autocoerce Enlive transforms - #9
- File globbing - #18
- Print warning on data coercion loss - #11
- Coloured output
- string parsing with edamame
- symlink and directory making
- many unit tests

## [0.1.4] - 2019-10-24
### Fixed
- Fixed more data conversion errors - #2

## [0.1.3] - 2019-10-24
### Changed
- Add type conversion test suite

### Fixed
- Fixed hickory-seq data conversion problems - #7

## [0.1.2] - 2019-10-23
### Changed
- Add clojure.walk namespace to sci environment

### Fixed
- Fixed enlive/hickory :type encoding incompatibility - #2 #4

## [0.1.1] - 2019-10-23
Initial release

[Unreleased]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.6-1...HEAD
[0.1.6-1]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.6...v0.1.6-1
[0.1.6]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.5...v0.1.6
[0.1.5]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.4...v0.1.5
[0.1.4]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.3...v0.1.4
[0.1.3]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.2...v0.1.3
[0.1.2]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.0...v0.1.1
