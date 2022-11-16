package org.homework.entity;

import java.util.Objects;

public class Account {
    
    public String accountName;
    
    public String currencyAbbrev;
    
    public double balance;

    @Override
    public boolean equals(Object o) {
      return accountName.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountName, currencyAbbrev, balance);
    }
}
