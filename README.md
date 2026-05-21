# Notes on the Module "API Specialized Entities"

## What Does It Do?

This module provides specialized entities (classes) that are based on more generic entities in the module "API (Core)".

## What is This Repo's Relationship with the Other Repos?

* This is a module-level repository which is part of a multi-module project, i.e. it has a parent and several siblings. 

  [Parent](https://github.com/jross765/JKMyMoneyLibNTools.git)

* Under normal circumstances, you cannot compile it on its own (at least not without further preparation), but instead, you should clone it together with the other repos and use the parent repo's build-script.

## Major Changes

Cf. document "[Major Changes](https://github.com/jross765/JKMyMoneyLibNTools/kmymoney-api-spec/major_changes.md)".

## Planned

* Other kinds of special transactions, e.g.:

  * Single transactions:
    * Stock/security sell transaction
    * Foreign currency transaction
    * Possibly: Book-closing transaction (EOY)
      (cf. notes for module "API Extensions" on why the word "possibly" has been added).

  * Multi-transactions:
    * Move-stocks/securities transaction (from one securities account to another)
    * Loan-related transactions
    * Crypto-currency transaction (buy s.t. w/ crypto = spec. security)

* Support building up the specialized entities
(at least the more complex ones, such as stock-buy/dividend transaction).

  Currently, only existing ones are supported that you then can amend.

## Known Issues
(None)

## Notes on Scope
* This module only contains data object classes. The classes that *generate*/*handle* them are located in the module "API Extensions".

