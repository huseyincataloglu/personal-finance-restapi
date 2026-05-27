

## Initial Balance Rules
1. **Account Type Constraint:**
    - Only applicable to `BalanceAccount` types (e.g., Cash, Bank, Digital Wallets). It cannot be applied to `AssetAccount` types.
2. **Timeline Priority:**
    - Historically, it must always be the first transaction of the account.
    - The transaction date and time cannot be set externally by the user. The system implicitly enforces it to match the belonging account's `createdAt` timestamp.
3. **Uniqueness Constraint:**
    - An account can have only one active (non-reversed) initial balance transaction at a time. If an active initial balance already exists, no further initial balance can be applied.

4. **Immutability & Adjustment Scope:**
    - Initial balance transactions can be adjusted, but modifications are strictly restricted to the `amount` field.
    - The associated account, asset/currency type, and timestamp are completely immutable.

5. **Deletability & Re-application:**
    - If a user deletes an initial balance (due to erroneous entry), the system marks the transaction as reversed (storno).
    - Once the active initial balance is removed, the user is allowed to apply a new initial balance later.
    - Any newly applied initial balance will automatically inherit the account's original `createdAt` timestamp again, maintaining ledger consistency.