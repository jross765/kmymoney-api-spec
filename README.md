# Notes on the Module "API Specialized Entities"

## What Does It Do?

This module provides specialized entities (classes) that are based on more generic entities in the module "API".

## What is This Repo's Relationship with the Other Repos?

* This is a module-level repository which is part of a multi-module project, i.e. it has a parent and several siblings. 

  [Parent](https://github.com/jross765/JKMyMoneyLibNTools.git)

* Under normal circumstances, you cannot compile it on its own (at least not without further preparation), but instead, you should clone it together with the other repos and use the parent repo's build-script.

## Major Changes
### V. 0.1
New.

Introduced:

* `KMyMoneySimpleTransaction` (includes code that used to be in module "API" and which does not belong there)

* `KMyMoneyStockBuyTransaction`

* `KMyMoneyStockSplitTransaction`

## Planned

* Other kinds of special transactions, e.g.:

  * Single transactions:
    * Stock/security sell transaction
    * Dividend transaction
    * Foreign currency transaction

  * Multi-transactions:
    * Move-stocks/securities transaction (from one securities account to another)
    * Loan-related transactions
    * Crypto-currency transaction (buy s.t. w/ crypto = spec. security)

## Known Issues
(None)

## Notes on Scope
* This module only contains data object classes. The classes that *generate*/*handle* them are located in the module "API Extensions".

