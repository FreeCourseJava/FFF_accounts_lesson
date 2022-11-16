package org.homework.repository;

public interface Repository<Type> {

   void write(Type[] objekt);

    Type[] read();

    Type getEntity(String name);

    void putEntities(Type objekt, Type objekt2);

}
