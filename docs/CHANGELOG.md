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

