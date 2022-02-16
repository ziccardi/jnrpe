## [3.2.1](https://github.com/ziccardi/jnrpe/compare/3.2.0...3.2.1) (2022-02-16)


### Bug Fixes

* fixed build script ([4e48310](https://github.com/ziccardi/jnrpe/commit/4e483107b6ea9466071924dfb2fdd93622836631))



# [3.2.0](https://github.com/ziccardi/jnrpe/compare/3.1.1...3.2.0) (2020-12-22)


### Bug Fixes

* 🐛 added description for the start command ([7d70356](https://github.com/ziccardi/jnrpe/commit/7d703562da6d52fd19a77bea5370ec3c236859a9))


### Features

* **cli:** added commands to list plugins and get plugin help ([6e2097c](https://github.com/ziccardi/jnrpe/commit/6e2097ce56abcbb1fb0fbed0668462089fe5b015)), closes [#56](https://github.com/ziccardi/jnrpe/issues/56)
* **plugins:** implemented plugin execution ([a067981](https://github.com/ziccardi/jnrpe/commit/a067981a50f014c6141b58705336bd19be561414))
* **threshold:** implemented threshold syntax parsing and validation ([2932f80](https://github.com/ziccardi/jnrpe/commit/2932f808c00132e48ccf31fe7edfa5924fca94ce)), closes [#87](https://github.com/ziccardi/jnrpe/issues/87)



## [3.1.1](https://github.com/ziccardi/jnrpe/compare/3.1.0...3.1.1) (2020-09-15)


### Bug Fixes

* 🐛 made the conf parameter global to all commands ([#75](https://github.com/ziccardi/jnrpe/issues/75)) ([dfef38f](https://github.com/ziccardi/jnrpe/commit/dfef38fb94d95d67b0399da2673dbe18b6629e5a))



# [3.1.0](https://github.com/ziccardi/jnrpe/compare/3.0.0...3.1.0) (2020-09-15)


### Features

* 🎸 added command `start` to the commandline ([12cd24f](https://github.com/ziccardi/jnrpe/commit/12cd24fafd3c3302a68c6ca0175195f79af30b8e))



# [3.0.0](https://github.com/ziccardi/jnrpe/compare/2.1.0-SNAPSHOT...3.0.0) (2020-09-15)


### Bug Fixes

* fixed protocol V3 and V4 impl ([e173cdf](https://github.com/ziccardi/jnrpe/commit/e173cdfbdef35c28bb2708b8a7fe15a8d5f1576f))


### Features

* 🎸 added a new event manager module ([#65](https://github.com/ziccardi/jnrpe/issues/65)) ([c75c8df](https://github.com/ziccardi/jnrpe/commit/c75c8dfa6764e8015722d608565754900ddfb6b2)), closes [#62](https://github.com/ziccardi/jnrpe/issues/62)
* 🎸 added command executor ([16565e7](https://github.com/ziccardi/jnrpe/commit/16565e7ce35e7fbc86f81a2dfa6108dc4a7e0e0b))
* 🎸 added config and config source service ([871ecfe](https://github.com/ziccardi/jnrpe/commit/871ecfedf73eac8b5599f9d81c1115eac06793c9))
* 🎸 added config parameter to the main class ([0641ac0](https://github.com/ziccardi/jnrpe/commit/0641ac0738ef274ad6726555c9b5438495968708))
* 🎸 added EventManager ([ac60f28](https://github.com/ziccardi/jnrpe/commit/ac60f285079a45c3c4940784de151a5cf810c1be)), closes [#61](https://github.com/ziccardi/jnrpe/issues/61)
* 🎸 added first draft of the real command execution flow ([d303b40](https://github.com/ziccardi/jnrpe/commit/d303b403284f2b2de830692cd86124f5a30c4e77))
* 🎸 starting development of the new JNRPE version ([b0b4088](https://github.com/ziccardi/jnrpe/commit/b0b4088326c29fd13e1726782ef8acceecad9681))
* **executor:** added command executor service ([d4a6d6c](https://github.com/ziccardi/jnrpe/commit/d4a6d6c164bafd68c5095124cec0b5b3344bad41))
* **network:** added network module ([ab92ab3](https://github.com/ziccardi/jnrpe/commit/ab92ab35bb51015ab97435075cdd47a659b6c618))


### BREAKING CHANGES

* 🧨 Totally new development



