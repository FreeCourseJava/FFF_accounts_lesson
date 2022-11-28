package org.homework.entity;

import org.homework.annotation.ID;

import java.util.Objects;

public class Currency {
    
    public double rateToUsd;

    @ID
    public String abbrev;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Double.compare(currency.rateToUsd, rateToUsd) == 0 && Objects.equals(abbrev, currency.abbrev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rateToUsd, abbrev);
    }
}
