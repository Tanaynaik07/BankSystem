<div align="center">

# 🏦 Concurrent Banking System

**A Java console-based banking application backed by SQLite**

[![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/Database-SQLite-003B57?logo=sqlite&logoColor=white)](https://sqlite.org/)
[![Status](https://img.shields.io/badge/Status-In%20Development-yellow)]()

*A hands-on Java project exploring object-oriented programming, JDBC, relational databases, and safe banking operations.*

</div>

---

## 📌 Overview

Concurrent Banking System is a command-line banking application built in Java. It models common banking operations while storing account and transaction data in a local SQLite database.

The project is being developed incrementally, with an emphasis on understanding the engineering behind each feature—not just making the application work.

## ✨ Current functionality

- **Account management** — create and retrieve bank accounts.
- **Persistent storage** — store account and transaction records in SQLite through JDBC.
- **Deposits and withdrawals** — update account balances with validation.
- **Money transfers** — transfer funds between accounts using a database transaction.
- **Transaction records** — retain a record of deposits, withdrawals, and transfers.
- **Transaction history** — retrieve recent transactions with account details.
- **Console interface** — interact with the banking system through the terminal.

> The terminal ATM-style presentation and further concurrency testing are still being developed. Features should be considered complete only when implemented and tested in the repository.

## 🧰 Tech stack

| Technology | Purpose |
|---|---|
| Java 25 | Application logic and object-oriented design |
| JDBC | Java-to-database connectivity |
| SQLite | Local relational data storage |
| Maven | Dependency management and build execution |
| SQLite JDBC driver | SQLite connectivity from Java |

## 🏗️ Project structure

```text
java/
├── pom.xml
├── banking.db                 # Created when the application accesses SQLite
└── banking/
    ├── Main.java              # Application entry point
    ├── ConsoleUi.java         # Terminal interaction
    ├── BankAccount.java       # Account model
    ├── AccountsRepository.java# Account and transaction persistence
    ├── Database.java          # Connection and schema setup
    ├── BankTransaction.java   # Transaction model
    └── Transfer.java          # Transfer task
```

*The structure above reflects the current project layout; update it as classes are added, renamed, or removed.*

## 🚀 Getting started

### Prerequisites

- JDK 25 or a compatible Java version matching the Maven compiler configuration
- Apache Maven
- Windows Terminal, PowerShell, or another terminal

Verify the installations:

```powershell
java -version
mvn -version
```

### Run locally

Clone the repository and move into the project directory:

```powershell
git clone <your-repository-url>
cd <repository-folder>
```

Compile and run the application through Maven:

```powershell
mvn compile exec:java "-Dexec.mainClass=banking.Main"
```

Maven is used here so the SQLite JDBC dependency is available at runtime. Running the class directly with `java Main` will not automatically include Maven-managed dependencies.

### Run the automated demo

Run the automated simulation to see the banking operations execute without manual input:

```powershell
mvn compile exec:java "-Dexec.mainClass=banking.Simulation"
```

The simulation demonstrates:

- Creating two demo accounts
- Depositing money and withdrawing funds
- Transferring money between accounts
- Checking final account balances
- Displaying transaction history

The demo is intended to provide a repeatable walkthrough of the core banking workflows. Its exact operations and validation checks depend on the implementation in `Simulation.java`.

**Database note:** The simulation should use a separate database file, such as `banking-demo.db`, to keep demo data separate from your regular application data in `banking.db`. If your current `Simulation.java` uses `banking.db`, each run may add demo accounts and transactions to your existing data instead. Verify the database configuration before running it.


## 🧪 Trying the operations

See [`SIMULATION.md`](SIMULATION.md) for a repeatable manual walkthrough covering account creation, deposits, withdrawals, transfers, balance checks, and transaction history.

## 🛣️ Development roadmap

- [x] SQLite database connection and schema creation
- [x] Account persistence through a repository layer
- [x] Deposit, withdrawal, and transfer workflows
- [x] Transaction history retrieval
- [ ] Finish the ATM-style terminal experience
- [ ] Add automated unit and integration tests
- [ ] Add repeatable concurrent-operation tests
- [ ] Verify transaction consistency under simultaneous requests
- [ ] Package the application for easier execution

## ⚠️ Scope

This is an educational banking simulation, not a production banking system. It does not implement real-world authentication, authorization, regulatory controls, or production-grade financial security. Use test data only.

## 🤝 Contributing

This is a learning project. Suggestions, bug reports, and discussions about design decisions are welcome.

---

<div align="center">

**Built as a practical Java and database engineering project.**

</div>
