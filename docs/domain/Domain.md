
# Domain Terms
**Transaction:** Transaction represents what the system will perform for a particular request.
**Entry:** Entry define what will happen to an account whom the transactions are applied.
**Category:** Categories will answer the question why is this 
**Account:** Account represents the place of the flowing money. Transactions might effect one or more accounts based on its entries


## Domain-Driven Uniqueness Constraints

### Asset Holding Uniqueness
Constraint:
(account_id, asset_symbol, asset_unit)

This constraint ensures that the same asset cannot exist multiple times within the same account using the same unit. 
An asset holding represents the current aggregated position of an asset, meaning purchases update the existing holding’s quantity 
and average unit price instead of creating duplicate rows. 
This prevents inconsistent portfolio states and guarantees a single source of truth for each asset position.

### Account Uniqueness
Constraint:
(user_id, name, type)

This constraint prevents users from creating multiple accounts with the same financial meaning and behavior. 
In this system, an account’s identity is defined by its name (financial purpose) and type (capabilities/behavior). 
Currency is intentionally excluded because it is treated as an immutable financial property enforced by domain rules, 
not as part of account identity. If a user accidentally selects the wrong currency, it can only be corrected before any transactions exist; 
otherwise, the account must be closed and recreated.

## What is Transaction ?
Tranasction represents financial operation that a user of the system can perform on a single account or multiple accounts.
A transaction act as a container that includes metadata about the transaction such as userId, date, category, isActive, reversed. In addition,
it includes list of entries that must be applied to related accounts. A single transaction can impact one or multiple accounts.
In summary, the transactions are historical events in the system and they build the **audit trail** mechanism.


## Financial System Design & Transaction Integrity Summary
### 1. Immutable Transaction Principle (Reverse Logic)
Instead of a hard delete, the system employs a ***"Reverse Entry"*** mechanism for erroneous records. 
When a user try to "delete" a transaction, the system generates a counter-transaction with identical attributes but opposite financial entries. 
These records are flagged as "reversed" to prevent clutter, while maintaining a perfect audit trail in the database.

### Refund vs. Reverse
While a Reverse corrects a human error, a Refund represents a real-world financial event. 
Refunds are treated as "Negative Expenses" linked to the original transaction's category. 
This ensures that monthly spending reports reflect "Net Expenses" (Gross Expense - Refunds) accurately, without artificially inflating "Income" figures.

#### Reverse Transaction Type
Represents correction action.
Scenario: User performed incorrect transaction by mistake and wanted to delete that transaction. The system does not allow deleting any transaction due to audit trail
but instead of deleting system performs the same transaction but in opposite way.
- -500 expense on Market for Wallet -> needs to be deleted
- +500 reverse on Market for Wallet -> this is a copy of original transaction but its amount's signature is opposite and the type is reverse

Reversed and referenceTransactionId fields manage this transaction
Reversed transaction's reversed fields becomes  ***true** respectively, referenceTransactionId points **null**
But system creates a reverse transaction which is type of Reverse and referenceTransactionId points the original reversed transaction id

Note: Reverse and reversed transaction make impact on the static balance fields of the accounts but. The system wont expose them to the outside when
the user tries to get their transaction history. Moreover, they are accesiable by specifying query parameters such as reversed = true or type = REVERSAL.


#### Reverse Rules ? 
- Reverse and Refund transaction cannot be reversed due to some reasons.
- Reversed transaction cannot be reversed again.
- Reverse transaction's itself cannot be reversed.
- Reverse transaction creates a mirror image of the original transaction especially **entries**

#### Refund Transaction Type
This a real financial action that can happen in real life.
For Income and Expense transactions, refund is likely to happen. Our system will handle refunding as a different transaction type due to maintaining data inconsistency
Scenario: User bought shoes in exchange of 1000 cash but then gave it back to the store because of some reasons refunded by 1000 cash
In this moment, 1000 cash wont represent an income but it will be acted as refund transaction

#### Refund Rules
- Only Income and Expense transactions can be refunded
- refunded transactions cannot be refunded again
- refund and refunded transactions wont be reversed 


### Field Update Constraints & Data Integrity
To preserve the "Historical Truth" of the ledger, core financial attributes—Amount, Account, Transaction Type, and Date—are locked after creation.
**Reason:** Changing these fields retroactively would alter the financial history.
**Workflow:** If a user needs to update a locked field, the system forces a "Reverse & Re-entry" workflow
(automatically reversing the old entry and creating a new one with corrected data).
**Exceptions:** Non-financial metadata like Descriptions or Category (classification) may be updated as they do not change the cash flow or balance integrity.

### Hesap Yönetimi ve Veri Bütünlüğü

#### Hesap Kapatma (Account Closure) Kuralları:
- **Kalıcılık:** Bir hesap kapatıldığında, o hesaba bağlı geçmiş işlemler kesinlikle silinmez veya değiştirilmez.
- **İşlem Engeli:** `Kapalı` statüsündeki bir hesap, hiçbir `İşlem Tipi` (Giriş, Çıkış, Reversal) ile etkileşime giremez.
- **Sistem Etkisi:** Hesap kapatma yerel bir işlemdir; sistem genelindeki diğer hesapların bakiyelerini veya işlem geçmişlerini etkilemez.
