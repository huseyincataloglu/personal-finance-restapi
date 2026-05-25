

### AssetAccount
Extends base Account abstract class. 

Relationships:
This class contains List of **AssetAccountHolding** objects || One-to-Many  
1 asset account can have many holdings
one holding can only be pointed by one asset account

### AssetAccountHolding 
This class serves as associative entity. It decreases the many-to-many relationship between AssetAccount and Asset entities 
by adding some new features. Actually, this class represents metadata,which forms and changes at the end of each
asset operations on the asset account,  about the assets. To sum up, one holding traces one asset and changes its states 
at the end of the asset operations in the system for an account
Unique constraints = [account_id,asset_id]

Object Relationships:
#### 1.AssetAccount
Many-To-One
This relationship managed in this table with the column name of account_id
- many holdings can be pointed by one asset account but one holding can be included just one asset account.

#### 2.Asset
Many-To-One
This relationship managed in this table with the column name of asset_id
- many assets can be pointed by multiple holdings
**Note** Assets are unique records that are shared by all the users. What we need to build is to have only one asset for a holding in
a asset account but mo other holding can refer to that asset. This must be achieved via **composite unique constraints**.Account_id and
asset_id columns in the holding tables will be unique together and wont be duplicated accross table.
So for an account, single asset will be only included one holding. This is our purpose because holding will always trace single asset.


  

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