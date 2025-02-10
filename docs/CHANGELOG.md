## [0.7.0](https://github.com/swisscom/JCR-Hopper/compare/v0.6.1...v0.7.0) (2025-02-10)


### Features

* **editor:** added sample for change property value ([29c6f16](https://github.com/swisscom/JCR-Hopper/commit/29c6f161f1702e715d3614c79489ec0d4d7d16ad))
* **editor:** added script for value finder ([4cbab25](https://github.com/swisscom/JCR-Hopper/commit/4cbab258632bd0c8cb66801d2df9217892022367))
* **editor:** finished csv output ([783e462](https://github.com/swisscom/JCR-Hopper/commit/783e462eb7a8a42b16e5848789eaa374e7bf57b7))


### Bug Fixes

* **editor:** fix review comments ([14377df](https://github.com/swisscom/JCR-Hopper/commit/14377df0ffc0e838ad529665ff584bc83ddf307e))
* **editor:** formatting issues ([41261e2](https://github.com/swisscom/JCR-Hopper/commit/41261e2d2f22f4260012233c108b28837afa64e9))
* **editor:** prettier format changes ([fcde4bc](https://github.com/swisscom/JCR-Hopper/commit/fcde4bc3ca85f05d56222d1198eefd4d66a6543d))

## [0.6.1](https://github.com/swisscom/JCR-Hopper/compare/v0.6.0...v0.6.1) (2025-02-04)


### Bug Fixes

* **editor:** ensure empty args don’t get submitted ([4be1ed2](https://github.com/swisscom/JCR-Hopper/commit/4be1ed2204a08fbea8322c283db29ec364247050)), closes [#31](https://github.com/swisscom/JCR-Hopper/issues/31)
* make sure .parcel-cache also gets cleaned ([ebb2eea](https://github.com/swisscom/JCR-Hopper/commit/ebb2eea28ffa5c98974ddab0b4d5aed8182a018c))

## [0.6.0](https://github.com/swisscom/JCR-Hopper/compare/v0.5.1...v0.6.0) (2025-01-11)


### Features

* **editor:** add additional sample scripts ([5516e12](https://github.com/swisscom/JCR-Hopper/commit/5516e12ea61052cf72e695f5866467070186bc44))
* **editor:** apply suggestion change type ([3700132](https://github.com/swisscom/JCR-Hopper/commit/3700132324da341cced4ef9b74075b780e8fc494))
* **editor:** file rename ([5be490e](https://github.com/swisscom/JCR-Hopper/commit/5be490ef2caba81bf1abaeb7181b9e5a868ac209))
* **editor:** move declaration to parameters ([2f8c411](https://github.com/swisscom/JCR-Hopper/commit/2f8c4117464c5115d8de18ab4c85f0fcbe0a9ee3))
* **editor:** remove iteration, move declarations to parameters ([36f8219](https://github.com/swisscom/JCR-Hopper/commit/36f8219502bc86e71c8d8cef4096107bb5cda028))
* **editor:** remove iteration, move declarations to parameters, change content path ([bb19828](https://github.com/swisscom/JCR-Hopper/commit/bb1982853f975221b2e88e88e5a7f7577a3a878b))
* **editor:** rename variable ([9b78e6b](https://github.com/swisscom/JCR-Hopper/commit/9b78e6b81dd12db8be0e39a7e474dea0306e5099))
* **editor:** replace declarations with parameters, changed query path ([bf8cb13](https://github.com/swisscom/JCR-Hopper/commit/bf8cb13049b827e8d906ac142211368296e26c68))
* **editor:** replace logging with file output ([41fccfb](https://github.com/swisscom/JCR-Hopper/commit/41fccfb3506e8eddb797255bb737b3f87d8bcd93))
* **editor:** replace variables with parameters ([a2e36c6](https://github.com/swisscom/JCR-Hopper/commit/a2e36c68b3a27bcccd9874481ff218bd493f1ffc))
* **editor:** simplified script by merging single/multiple value property type ([105e90d](https://github.com/swisscom/JCR-Hopper/commit/105e90d6ac0f582cbc7a7a459e8c756f03711bb2))
* **editor:** split script, move declaration to parameters ([09f14ac](https://github.com/swisscom/JCR-Hopper/commit/09f14acf8ff43475365a8fa7be4ada472ed32a2d))

## [0.5.1](https://github.com/swisscom/JCR-Hopper/compare/v0.5.0...v0.5.1) (2024-11-19)


### Bug Fixes

* **meta:** clean up config files in root ([74fe1b2](https://github.com/swisscom/JCR-Hopper/commit/74fe1b2a37a36e59d6eb368d5d23a594fe643c9e))

## [0.5.0](https://github.com/swisscom/JCR-Hopper/compare/v0.4.0...v0.5.0) (2024-11-03)


### Features

* **editor:** disable the existing node switch if there aren’t any hops to run ([c5728c6](https://github.com/swisscom/JCR-Hopper/commit/c5728c618b2ae8b14933fc4def5db179c9bba02e))
* **runner:** add option to control running descendant steps on existing nodes ([8bb8aa8](https://github.com/swisscom/JCR-Hopper/commit/8bb8aa8240ee169e7f84f0233ae3131b95f7a9f5))


### Bug Fixes

* **runner:** allow each to loop over primitive and nested arrays ([214f936](https://github.com/swisscom/JCR-Hopper/commit/214f936676915937872ac3eb7756f75cb60c3f8e))
* **runner:** allow moving to the root node ([6f64314](https://github.com/swisscom/JCR-Hopper/commit/6f64314e2a9bfc0d7c5ba5e226336b20ac431547))
* **runner:** ensure `each` skips iterations on non-nodes when assumeNodes is true ([a47fd3a](https://github.com/swisscom/JCR-Hopper/commit/a47fd3a83a6103878bd0628313befc490856c574))
* **runner:** refactor copyNode hop ([c08663c](https://github.com/swisscom/JCR-Hopper/commit/c08663c8e96805a5095fac8971f4350dd53a72e9))
* **runner:** renamed nodes now keep their position ([a944fb7](https://github.com/swisscom/JCR-Hopper/commit/a944fb75ccf978c6abf6277b42c9e79eaf3fbcf1)), closes [#16](https://github.com/swisscom/JCR-Hopper/issues/16)

