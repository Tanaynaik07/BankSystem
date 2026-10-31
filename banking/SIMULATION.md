# Banking System — Operation Simulation

This document is a repeatable manual test walkthrough for the console application. It uses fictional accounts and amounts.

The goal is to exercise the main repository workflows and verify that account balances and transaction records remain consistent.

> Run these steps through the application menu. Do not edit the SQLite database directly for this walkthrough; the purpose is to test the Java application and repository layer.

## Test data

Create these two accounts (if they do not already exist):

| Account holder | Opening balance |
|---|---:|
| Asha | ₹10,000 |
| Ravi | ₹5,000 |

Record the account IDs printed by the application:

- Asha's account ID: `________`
- Ravi's account ID: `________`

Use the IDs returned by your own database. Do not assume IDs will always start at 1 because the database may already contain records.

## Operation sequence

### 1. Check initial balances

| Account | Expected balance |
|---|---:|
| Asha | ₹10,000 |
| Ravi | ₹5,000 |

### 2. Deposit money

Deposit ₹2,000 into Asha's account.

Expected result:

- Deposit is accepted.
- Asha's balance becomes **₹12,000**.
- A deposit transaction is recorded.

### 3. Withdraw money

Withdraw ₹1,500 from Asha's account.

Expected result:

- Withdrawal is accepted.
- Asha's balance becomes **₹10,500**.
- A withdrawal transaction is recorded.

### 4. Attempt an invalid withdrawal

Attempt to withdraw ₹20,000 from Asha's account.

Expected result:

- The withdrawal is rejected because the available balance is insufficient.
- Asha's balance remains **₹10,500**.
- No successful withdrawal should be recorded.

### 5. Transfer money

Transfer ₹3,000 from Asha's account to Ravi's account.

Expected result:

- The transfer succeeds.
- Asha's balance becomes **₹7,500**.
- Ravi's balance becomes **₹8,000**.
- One transfer transaction is recorded.

### 6. Attempt an invalid transfer

Attempt to transfer ₹9,000 from Asha to Ravi.

Expected result:

- The transfer is rejected because Asha has only ₹7,500.
- Neither account balance changes.
- No successful transfer should be recorded.

### 7. Attempt a transfer to the same account

Attempt to transfer ₹500 from Asha's account to Asha's own account.

Expected result:

- The transfer is rejected.
- The balance remains unchanged.

### 8. Review transaction history

Open the recent transaction history.

For the successful operations above, expect these records:

| Type | Account involved | Amount |
|---|---|---:|
| Deposit | Asha | ₹2,000 |
| Withdrawal | Asha | ₹1,500 |
| Transfer | Asha → Ravi | ₹3,000 |

The exact order shown may depend on the transaction ID ordering and any other test records already in the database.

## Final balance check

After all successful operations:

| Account | Calculation | Expected balance |
|---|---|---:|
| Asha | ₹10,000 + ₹2,000 − ₹1,500 − ₹3,000 | **₹7,500** |
| Ravi | ₹5,000 + ₹3,000 | **₹8,000** |
| **Combined** | ₹7,500 + ₹8,000 | **₹15,500** |

The combined balance should remain ₹15,500 because deposits and withdrawals are the only operations in this sequence that change the total money held in the system. A transfer moves money between accounts; it does not create or destroy it.

## Additional edge cases

Try these after the main walkthrough:

- Create an account with a blank name.
- Create an account with a negative opening balance.
- Deposit zero or a negative amount.
- Withdraw zero or a negative amount.
- Transfer zero or a negative amount.
- Use an account ID that does not exist.
- Transfer to a non-existent receiver account.
- Enter letters where the menu expects a number.

For each case, check that the application responds cleanly and that no unintended balance or transaction changes occur.

## Concurrency test — planned

This is a separate test to add once the concurrent test harness is implemented.

1. Create an account with a balance of ₹1,000.
2. Trigger two withdrawals of ₹700 at nearly the same time.
3. Verify that at most one withdrawal succeeds.
4. Verify that the final balance is never negative.
5. Verify that the transaction history contains only successful operations.

Expected invariant:

```text
Starting balance:       ₹1,000
Two withdrawal requests:  ₹700 each
Maximum total withdrawn: ₹700
Minimum final balance:     ₹300
```

Do not count this concurrency test as passed until it has been executed repeatedly against the actual application.
