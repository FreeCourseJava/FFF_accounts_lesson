package org.homework.repository;

import org.homework.annotation.Service;
import org.homework.entity.Currency;

@Service
public class CurrencyFileRepo extends FileRepoAbstract<Currency> implements RepositoryCurrency{

    public CurrencyFileRepo(){
        super(Currency[].class, "currencies.json");
        System.out.println("Выполнен конструктор CurrFileRepo");
    }

}
