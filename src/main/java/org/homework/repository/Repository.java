package org.homework.repository;

public interface Repository<Type> {


    Type getEntity(String name);

    void updateNumericValue(Type objekt);

}
