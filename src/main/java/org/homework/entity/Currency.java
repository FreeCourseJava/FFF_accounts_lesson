package org.homework.entity;

import java.util.Objects;

public class Currency {
    
    public double rateToUsd;
    public String abbrev;

    @Override
    public boolean equals(Object o) {
        return abbrev.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(abbrev);
    }
}
