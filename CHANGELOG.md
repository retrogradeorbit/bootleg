# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
- Support markdown-clj options (footnotes and reference links) - PR #76 thankyou Dominic Freeston
- Static musl libc binary on linux

## [0.1.9] - 2020-05-27
### Added
- Support for deferred pod namespace loading in babashka (requires bb >= 0.0.99) - #65

## [0.1.8] - 2020-05-24
### Fixed
- YAML upgrade to SnakeYAML 2.0 #46
- Build failed when trying build with graalvm-ce-java11-19.3.0 #47
- Can we have the ns/require code from spire in bootleg? #58
- Running from file in same directory requires leading `./`#61
- Generating XML preserving the tag name case #62
- Fix reflection error preventing XML roundtrip #63

### Added
- add babashka pod support #60

## [0.1.7] - 2020-01-31
### Fixed
- fixed broken selmer page inheritance tags - #49
- cull empty {} from hiccup conversions - #50
- missing warning on lost form when html is converted to hiccup - #53

### Added
- added environment variables and command line argument support - #42
- added support for reading from standard in - #44
- added missing :data flag to yaml, json and edn functions - #45
- introducing xml support - #50
- added java.time classes to those available - #51

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

[Unreleased]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.9...HEAD
[0.1.9]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.8...v0.1.9
[0.1.8]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.7...v0.1.8
[0.1.7]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.6-1...v0.1.7
[0.1.6-1]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.6...v0.1.6-1
[0.1.6]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.5...v0.1.6
[0.1.5]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.4...v0.1.5
[0.1.4]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.3...v0.1.4
[0.1.3]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.2...v0.1.3
[0.1.2]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/retrogradeorbit/bootleg/compare/v0.1.0...v0.1.1
