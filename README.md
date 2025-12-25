A simple Account Creation and Balance Checker using JDBC and Sql.

!!! CLICK bankaccountbalance --> CLICK src -->CLICK bankaccountbalance --> CLICK BankBalance.java


**Create → Deposit → Check**  
A minimalist Java banking system using JDBC.

## 🚀 Quick Start

```sql
-- 1. Setup database
CREATE DATABASE simple_bank;
USE simple_bank;
CREATE TABLE accounts (
    acc_no INT AUTO_INCREMENT PRIMARY KEY,
    holder_name VARCHAR(50),
    balance DOUBLE DEFAULT 0
);
```

## ✨ Features
- 🆕 **Create Account** - Open new account with initial deposit
- 💰 **Deposit Money** - Add funds to existing account
- 📊 **Check Balance** - View current balance instantly

## 🛠️ Tech Stack
- Java + JDBC
- MySQL Database
- PreparedStatement (SQL Injection Safe)

## 📈 Sample Flow
```
Create Account → Get Account Number → 
Deposit Money → Check Balance → 💸 Happy Banking!
```
