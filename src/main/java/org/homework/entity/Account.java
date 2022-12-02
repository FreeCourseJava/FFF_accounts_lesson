package org.homework.entity;

import org.homework.annotation.*;

import java.util.Objects;

@Table(name="accounts")
public class Account {

    @ID
    @Column(name="acc_name")
    public String accountName;

    @Column(name="curr_id")
    @LinkedTable(name="currencies")
    @LinkedEntity(name="abbrev", key = "id")
    public String currencyAbbrev;

    @Column(name="balance")
    public double balance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Double.compare(account.balance, balance) == 0 && accountName.equals(account.accountName) && currencyAbbrev.equals(account.currencyAbbrev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountName, currencyAbbrev, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountName='" + accountName + '\'' +
                ", currencyAbbrev='" + currencyAbbrev + '\'' +
                ", balance=" + balance +
                '}';
    }
}