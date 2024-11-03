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

## [0.4.0](https://github.com/swisscom/JCR-Hopper/compare/v0.3.0...v0.4.0) (2024-10-31)


### Features

* **editor:** pass all possible scripting languages to the editor ([7c42719](https://github.com/swisscom/JCR-Hopper/commit/7c427196c8c1c9985a0e14e3e9355eedab07b32c))
* **runner:** make ScriptEngineManager instance configurable on runner ([7de3009](https://github.com/swisscom/JCR-Hopper/commit/7de3009ecf09d5a3467ac341300056065e1aa85e))
* **runner:** use script engine from OSGi if available ([38c42dc](https://github.com/swisscom/JCR-Hopper/commit/38c42dcde6752a1b914a6b7888b44e97370dc2c0))

## [0.3.0](https://github.com/swisscom/JCR-Hopper/compare/v0.2.0...v0.3.0) (2024-10-29)


### Features

* **editor:** display size in file summary ([57162df](https://github.com/swisscom/JCR-Hopper/commit/57162df1228e72c1f252fb1bc960ab0ba60a1967))
* **runner:** add index var to `each` hop ([95ce249](https://github.com/swisscom/JCR-Hopper/commit/95ce249a088c29208f32cadab6609bf5cd4a0a51))


### Bug Fixes

* ensure frontend doesn’t crash when file is empty ([8e6a8f2](https://github.com/swisscom/JCR-Hopper/commit/8e6a8f209c369335accad8560cfcab1338a36490))

## [0.2.0](https://github.com/swisscom/JCR-Hopper/compare/v0.1.4...v0.2.0) (2024-10-28)


### Features

* **meta:** add to aem tools panel pr fixes ([e3ff73b](https://github.com/swisscom/JCR-Hopper/commit/e3ff73be83ee2d07817186293d6818dafa983efd))
* **meta:** adds configuration to include jcr-hopper in aem tools ([ab683ad](https://github.com/swisscom/JCR-Hopper/commit/ab683ad1c3d1a1ac8d1a106c13908dcf10b36804))
* **meta:** swap icon for color logo ([fb68109](https://github.com/swisscom/JCR-Hopper/commit/fb6810976063f9a1c6e8d38ac75a0d6526c2add3))

## [0.1.4](https://github.com/swisscom/JCR-Hopper/compare/v0.1.3...v0.1.4) (2024-10-17)

