package org.homework.entity;

import org.homework.annotation.*;

import java.util.Objects;

@Table(name="currencies")
public class Currency {

    @Column(name="rate")
    public double rateToUsd;

    @ID
    @Column(name="abbrev")
    @LinkedTable(name="accounts")
    @LinkedEntity(name="curr_id", key = "id")
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

    @Override
    public String toString() {
        return "Currency{" +
                "rateToUsd=" + rateToUsd +
                ", abbrev='" + abbrev + '\'' +
                '}';
    }
}
