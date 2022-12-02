package org.homework.repository;

import org.homework.annotation.Service;
import org.homework.entity.Currency;

import java.sql.*;

@Service
public class CurrencyDataBaseRepo implements RepositoryCurrency {

    private Connection connection;

    private PreparedStatement prepStat;

    public CurrencyDataBaseRepo() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/AccountsDB",
                    "postgres", "666");
        } catch (Exception e) {
            System.out.println("Не удается подключится к базе данных");
            throw new RuntimeException(e);
        }
        System.out.println("Выполнен конструктор CurrDB");
    }

    @Override
    public Currency getEntity(String name) {
        String statSQL = "SELECT * FROM currencies WHERE abbrev = ?";
        Currency returnCurr = new Currency();

        try {
            prepStat = connection.prepareStatement(statSQL);
            prepStat.setString(1, name);
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                returnCurr.abbrev = rs.getString("abbrev");
                returnCurr.rateToUsd = rs.getDouble("rate");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (returnCurr.abbrev == null) {
            return null;
        }
        return returnCurr;
    }

    @Override
    public void updateNumericValue(Currency objekt) {
        String statSQL = "UPDATE currencies SET rate = ? WHERE abbrev = ?";
        try {
            prepStat = connection.prepareStatement(statSQL);
            prepStat.setString(2, objekt.abbrev);
            prepStat.setDouble(1, objekt.rateToUsd);
            prepStat.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
