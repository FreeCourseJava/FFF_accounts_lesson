package org.homework.service;


import org.homework.annotation.Service;
import org.homework.entity.Currency;
import org.homework.repository.RepositoryCurrency;

@Service
public class CurrencyService {

    private RepositoryCurrency currencyRepo;


    public CurrencyService(RepositoryCurrency currencyRepo) {
        this.currencyRepo = currencyRepo;
        System.out.println("Выполнен конструктор CurrServ");
    }

    private double getRate(String abbrev) {
        Currency tempEntity = currencyRepo.getEntity(abbrev);
        if (tempEntity == null) {
            System.out.println("Неизвестная валюта. Будет переведен 0 рублей");
            return 0;
        }
        return tempEntity.rateToUsd;
    }

    public double convertToUSD(double sum, String abbrev) {
        double tempRate = getRate(abbrev);
        if (tempRate == 0) {
            return 0;
        } else {
            return sum / tempRate;
        }
    }

    public double convertFromUSD(double sum, String abbrev) {
        return sum * getRate(abbrev);
    }

}
