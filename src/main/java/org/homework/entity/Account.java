package org.homework.entity;

import org.homework.annotation.ID;

import java.util.Objects;

public class Account {

    @ID
    public String accountName;

    public String currencyAbbrev;

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
}