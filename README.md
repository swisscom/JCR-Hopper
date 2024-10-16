# JCR Hopper

<img src="./docs/logo.svg" width=200 height=200 alt="">

_Migrate AEM with Grace_

JCR Hopper is a migration and reporting tool for AEM’s content repository.
It has its own script format that is valid JSON and consists of a series of actions,
each of which “hops” over nodes in the repository.

JCR Hopper also comes with a visual tool to create, preview and run the scripts.

## Installation

## Usage

### Java API

### HTTP API

#### Use the Script Builder

#### Run the Script Manually

## Expression Syntax

All configurable fields are either JEXL expressions (when the result type is arbitrary) or JEXL string templates (when the result is `String`).

Consult the [JEXL syntax reference](https://commons.apache.org/proper/commons-jexl/reference/syntax.html) for further details.

## Action Types

### <img src="./docs/icons/arrow_curve_down.svg" width=20 height=20 alt=""> Get Child Nodes

### <img src="./docs/icons/pirate_flag.svg" width=20 height=20 alt=""> Copy Node

### <img src="./docs/icons/sparkler.svg" width=20 height=20 alt=""> Create Child Node

### <img src="./docs/icons/control_knobs.svg" width=20 height=20 alt=""> Declare Variables

### <img src="./docs/icons/loop.svg" width=20 height=20 alt=""> Iterate

### <img src="./docs/icons/hole.svg" width=20 height=20 alt=""> Filter

### <img src="./docs/icons/desert_island.svg" width=20 height=20 alt=""> Move Node

### <img src="./docs/icons/magnifying_glass_right.svg" width=20 height=20 alt=""> Query JCR

### <img src="./docs/icons/speech_bubble.svg" width=20 height=20 alt=""> Rename Property

### <img src="./docs/icons/arrow_up_down.svg" width=20 height=20 alt=""> Reorder Node

### <img src="./docs/icons/heart_exclamation.svg" width=20 height=20 alt=""> Resolve Specific Node

### <img src="./docs/icons/pink_potion.svg" width=20 height=20 alt=""> Run a Script

### <img src="./docs/icons/pencil.svg" width=20 height=20 alt=""> Set a Property

### <img src="./docs/icons/bang.svg" width=20 height=20 alt=""> Catch Thrown Errors

## Acknowledgements

[Mutant Standard emoji](https://mutant.tech/) are licensed CC BY-NC-SA 4.0 International.
