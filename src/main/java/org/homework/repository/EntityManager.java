package org.homework.repository;

import org.homework.annotation.Column;
import org.homework.annotation.Table;
import org.homework.entity.Account;

import java.lang.reflect.Field;
import java.sql.*;


public class EntityManager {

    public static void main(String[] args) {
        EntityManager demo = new EntityManager();
        System.out.println(demo.get(1, Account.class));

        Account tempAcc = new Account();
        tempAcc.accountName = "Petja";
        tempAcc.currencyAbbrev = "RUB";
        tempAcc.balance = 70000;
        demo.updateNumericValue(tempAcc);

        Account tempAcc2 = new Account();
        tempAcc2.accountName = "Elon";
        tempAcc2.currencyAbbrev = "USD";
        tempAcc2.balance = 10000000;
        demo.insert(tempAcc2);
        demo.delete(tempAcc2);

    }

    private Connection connection;

    public EntityManager() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/AccountsDB",
                    "postgres", "666");
        } catch (Exception e) {
            System.out.println("Не удается подключится к базе данных");
            throw new RuntimeException(e);
        }
    }

    public <Type> Type get(long id, Class<Type> clazz) {
        Type returnObjekt = null;
        String anno;
        try {
            returnObjekt = (Type) clazz.getConstructors()[0].newInstance();
            String tableName = clazz.getAnnotation(Table.class).name();
            Field[] fieldz = clazz.getDeclaredFields();
            String statSQL = "SELECT * FROM " + tableName + " WHERE id = " + Long.toString(id);
            PreparedStatement prepStat = connection.prepareStatement(statSQL);
            ResultSet rs = prepStat.executeQuery();
            rs.next();
            for (Field tempField : fieldz) {
                tempField.setAccessible(true);
                anno = tempField.getAnnotation(Column.class).name();
                Object param = rs.getObject(anno);
                if (anno.equals("curr_id")) {
                    String statSQL2 = "SELECT * FROM currencies WHERE id = ?";
                    PreparedStatement prepStat2 = connection.prepareStatement(statSQL2);
                    prepStat2.setObject(1, param);
                    ResultSet rs2 = prepStat2.executeQuery();
                    rs2.next();
                    tempField.set(returnObjekt, rs2.getObject("abbrev"));
                } else {
                    tempField.set(returnObjekt, param);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return returnObjekt;
    }

    public <Type> void updateNumericValue(Type objekt) {
        String tableName = objekt.getClass().getAnnotation(Table.class).name();
        Field[] fieldz = objekt.getClass().getDeclaredFields();
        String anno = "", value = "", id = "", toSet = "";
        try {
            for (Field tempField : fieldz) {
                anno = tempField.getAnnotation(Column.class).name();
                if (anno.equals("acc_name") || anno.equals("abbrev")) {
                    id = anno;
                    value = "'" + tempField.get(objekt) + "'";
                }
                if (anno.equals("balance") || anno.equals("rate")) {
                    String vvalue = tempField.get(objekt).toString();
                    toSet = anno + " = " + vvalue;
                }
            }
            String statSQL = "UPDATE " + tableName + " SET " + toSet + " WHERE " + id + " = " + value;
            PreparedStatement prepStat = connection.prepareStatement(statSQL);
            prepStat.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <Type> void delete(Type objekt) {
        String tableName = objekt.getClass().getAnnotation(Table.class).name();
        Field[] fieldz = objekt.getClass().getDeclaredFields();
        String anno = "", value = "", id = "";
        try {
            for (Field tempField : fieldz) {
                anno = tempField.getAnnotation(Column.class).name();
                if (anno.equals("acc_name")) {
                    id = anno;
                    value = "'" + (String) tempField.get(objekt) + "'";
                }
                if (anno.equals("abbrev")) {
                    id = anno;
                    value = (String) tempField.get(objekt);
                    tableName = tableName + ", accounts ";
                    value = value + " AND curr_id = currencies.id";
                }
            }
            String statSQL = "DELETE FROM " + tableName + " WHERE " + id + " = " + value;
            PreparedStatement prepStat = connection.prepareStatement(statSQL);
            prepStat.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <Type> void insert(Type objekt) {
        String tableName = objekt.getClass().getAnnotation(Table.class).name();
        Field[] fieldz = objekt.getClass().getDeclaredFields();
        String statSQL = "SELECT MAX(ID) FROM " + tableName;
        int id;
        String columns, values, temp;
        try {
            PreparedStatement prepStat = connection.prepareStatement(statSQL);
            ResultSet rs = prepStat.executeQuery();
            rs.next();
            id = (int) rs.getObject("max");
            id++;
            columns = "id,";
            values = id + ",";
            for (Field tempField : fieldz) {
                temp = tempField.getAnnotation(Column.class).name();
                if (temp.equals("curr_id")) {
                    columns = columns + temp + ",";
                    String statSQL2 = "SELECT id FROM currencies WHERE abbrev =" + "'" + tempField.get(objekt) + "'";
                    PreparedStatement prepStat2 = connection.prepareStatement(statSQL2);
                    ResultSet rs2 = prepStat2.executeQuery();
                    rs2.next();
                    values = values + rs2.getObject("id") + ",";
                } else if (temp.equals("acc_name") || temp.equals("abbrev")) {
                    columns = columns + temp + ",";
                    values = values + "'" + tempField.get(objekt) + "',";
                } else {
                    columns = columns + temp + ",";
                    values = values + tempField.get(objekt) + ",";
                }
            }
            String statSQL3 = "INSERT INTO " + tableName + "(" + columns.substring(0, columns.length() - 1) + ") VALUES("
                    + values.substring(0, values.length() - 1) + ")";
            PreparedStatement prepStat3 = connection.prepareStatement(statSQL3);
            prepStat3.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}




