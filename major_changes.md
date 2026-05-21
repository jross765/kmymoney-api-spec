# Major Changes

## V. 0.3 &rarr; 0.4
Followed the deprecation of `FixedPointNumber` in the module
"Core API", V. 0.10: 

* Deprecated everything that is `FixedPointNumber`-related (cf. previous release).

* Partially changed implementations so that `BigFraction` is used internally instead of `FixedPointNumber`.

* Usual maintenance: Fixed small bugs, small improvements, low-level code-cleaning.

## V. 0.2 &rarr; 0.3
**Introduced:**

(Nothing)

**Improvements:**

* `KMyMoney(Writable)StockBuyTransaction(Impl)`: Improvements (changed interface):

    Added various methods to get and set data; now even better aligned to business perspective.

* `KMyMoney(Writable)StockDividendTransaction(Impl)`: dto.

* Fixed bugs

* Better test coverage

## V. 0.1 &rarr; 0.2
**Introduced:**

* `KMyMoney(Writable)StockDividendTransaction(Impl)`

**Improvements:**

* Added to the real added value of this module's classes: Added non-trivial special methods
  and removed other ones (changed interface), now better aligned to business perspective.

* Analogously, for the writable variants of theses classes, introduced various methods
  to set data and removed other ones.

* For all the above-mentioned new methods: The `BigFraction` variant, as well.

* For all `KMyMoneyWritableXYZTransaction` classes: Proper test cases / data.
  (Now they are all covered)

* Overall: 
    * Fixed bugs
    * A few minor improvements here and there.

## V. 0.1
New.

**Introduced:**

* `KMyMoneySimpleTransaction` (includes code that used to be in module "API" and which does not belong there)

* `KMyMoneyStockBuyTransaction`

* `KMyMoneyStockSplitTransaction`
