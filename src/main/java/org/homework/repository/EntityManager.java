package org.homework.repository;

import org.homework.annotation.*;
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


    private ResultSet execQuery(String stat) {
        try {
            Statement Stat = connection.createStatement();
            return Stat.executeQuery(stat);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void execUpdate(String stat) {
        try {
            Statement Stat = connection.createStatement();
            Stat.executeUpdate(stat);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public <TYPE> TYPE get(long id, Class<TYPE> clazz) {
        TYPE returnObjekt = null;
        String anno;
        try {
            returnObjekt = (TYPE) clazz.getConstructors()[0].newInstance();
            String tableName = clazz.getAnnotation(Table.class).name();
            Field[] fieldz = clazz.getDeclaredFields();
            String statSQL = "SELECT * FROM " + tableName + " WHERE id = " + Long.toString(id);
            ResultSet rs = execQuery(statSQL);
            rs.next();
            for (Field tempField : fieldz) {
                tempField.setAccessible(true);
                anno = tempField.getAnnotation(Column.class).name();
                Object param = rs.getObject(anno);
                if (tempField.isAnnotationPresent(LinkedEntity.class)) {
                    String statSQL2 = "SELECT " + tempField.getAnnotation(LinkedEntity.class).name() + " FROM "
                            + tempField.getAnnotation(LinkedTable.class).name() + " WHERE " +
                            tempField.getAnnotation(LinkedEntity.class).key() + " = " + param;
                    ResultSet rs2 = execQuery(statSQL2);
                    rs2.next();
                    tempField.set(returnObjekt, rs2.getObject(tempField.getAnnotation(LinkedEntity.class).name()));
                } else {
                    tempField.set(returnObjekt, param);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return returnObjekt;
    }

    public <TYPE> void updateNumericValue(TYPE objekt) {
        String tableName = objekt.getClass().getAnnotation(Table.class).name();
        Field[] fieldz = objekt.getClass().getDeclaredFields();
        String anno = "", value = "", id = "", toSet = "";
        try {
            for (Field tempField : fieldz) {
                anno = tempField.getAnnotation(Column.class).name();
                if (tempField.isAnnotationPresent(ID.class)) {
                    id = anno;
                    value = "'" + tempField.get(objekt) + "'";
                }
                if (tempField.getType() == double.class) {
                    String vvalue = tempField.get(objekt).toString();
                    toSet = anno + " = " + vvalue;
                }
            }
            String statSQL = "UPDATE " + tableName + " SET " + toSet + " WHERE " + id + " = " + value;
            execUpdate(statSQL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <TYPE> void delete(TYPE objekt) {
        String tableName = objekt.getClass().getAnnotation(Table.class).name();
        Field[] fieldz = objekt.getClass().getDeclaredFields();
        String anno = "", value = "", id = "";
        try {
            for (Field tempField : fieldz) {
                anno = tempField.getAnnotation(Column.class).name();
                if (tempField.isAnnotationPresent(ID.class)) {
                    id = anno;
                    value = "'" + tempField.get(objekt) + "'";
                    if (tempField.isAnnotationPresent(LinkedEntity.class)) {
                        tableName = tableName + ", " + tempField.getAnnotation(LinkedTable.class).name();
                        value = value + " AND " + tempField.getAnnotation(LinkedEntity.class).name()
                                + " = " + tempField.getAnnotation(LinkedEntity.class).key();
                    }
                }
            }
            String statSQL = "DELETE FROM " + tableName + " WHERE " + id + " = " + value;
            execUpdate(statSQL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <TYPE> void insert(TYPE objekt) {
        String tableName = objekt.getClass().getAnnotation(Table.class).name();
        Field[] fieldz = objekt.getClass().getDeclaredFields();
        String statSQL = "SELECT MAX(ID) FROM " + tableName;
        int id;
        String columns, values, temp;
        try {
            ResultSet rs = execQuery(statSQL);
            rs.next();
            id = (int) rs.getObject("max");
            id++;
            columns = "id,";
            values = id + ",";
            for (Field tempField : fieldz) {
                temp = tempField.getAnnotation(Column.class).name();
                if (tempField.isAnnotationPresent(LinkedEntity.class)) {
                    columns = columns + temp + ",";
                    String statSQL2 = "SELECT " + tempField.getAnnotation(LinkedEntity.class).key() + " FROM "
                            + tempField.getAnnotation(LinkedTable.class).name() + " WHERE " +
                            tempField.getAnnotation(LinkedEntity.class).name() + " = " + "'" + tempField.get(objekt) + "'";
                    ResultSet rs2 = execQuery(statSQL2);
                    rs2.next();
                    values = values + rs2.getObject("id") + ",";
                } else if (tempField.isAnnotationPresent(ID.class)) {
                    columns = columns + temp + ",";
                    values = values + "'" + tempField.get(objekt) + "',";
                } else {
                    columns = columns + temp + ",";
                    values = values + tempField.get(objekt) + ",";
                }
            }
            String statSQL3 = "INSERT INTO " + tableName + "(" + columns.substring(0, columns.length() - 1) + ") VALUES("
                    + values.substring(0, values.length() - 1) + ")";
            execUpdate(statSQL3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}




