package org.homework.repository;

import org.homework.annotation.Service;
import org.homework.entity.Account;

import java.sql.*;


@Service
public class AccountDataBaseRepo implements RepositoryAccount {

    private Connection connection;

    private PreparedStatement prepStat;

    public AccountDataBaseRepo() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/AccountsDB",
                    "postgres", "666");
        } catch (Exception e) {
            System.out.println("Не удается подключится к базе данных");
            throw new RuntimeException(e);
        }
        System.out.println("Выполнен конструктор AccDB");
    }

    @Override
    public Account getEntity(String name) {
        String statSQL = "SELECT * FROM accounts, currencies WHERE acc_name = ? AND accounts.curr_id=currencies.id";
        Account returnAcc = new Account();

        try {
            prepStat = connection.prepareStatement(statSQL);
            prepStat.setString(1, name);
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                returnAcc.accountName = rs.getString("acc_name");
                returnAcc.currencyAbbrev = rs.getString("abbrev");
                returnAcc.balance = rs.getDouble("balance");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (returnAcc.accountName == null) {
            return null;
        }
        return returnAcc;
    }

    @Override
    public void updateNumericValue(Account objekt) {
        String statSQL = "UPDATE accounts SET balance = ? WHERE acc_name = ?";
        try {
            prepStat = connection.prepareStatement(statSQL);
            prepStat.setString(2, objekt.accountName);
            prepStat.setDouble(1, objekt.balance);
            prepStat.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
