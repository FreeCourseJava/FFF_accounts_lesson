package org.homework.repository;

import com.google.gson.Gson;
import org.homework.annotation.ID;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Scanner;

public abstract class FileRepoAbstract<Type> implements Repository<Type> {

    private Class<Type[]> readType;

    private Type[] records;

    private String fileName;

    private Gson gson;

    public FileRepoAbstract(Class<Type[]> readType, String fileName) {
        this.readType = readType;
        this.gson = new Gson();
        this.fileName = fileName;
        this.records = read();
    }


    private void write(Type[] objekt) {
        String toWrite = gson.toJson(objekt);
        try (OutputStream outputStream = new FileOutputStream(fileName);
             PrintStream writer = new PrintStream(outputStream)) {
            writer.println(toWrite);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка записи");
        }
    }


    private Type[] read() {
        String temp = "";
        try (InputStream inputStream = new FileInputStream(fileName);
             Scanner reader = new Scanner(inputStream)) {
            temp = reader.nextLine();
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Ошибка чтения");
        }
        return gson.fromJson(temp, readType);
    }

    private String getTypeID(Type entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        String nameID = "";
        for (Field tempField : fields) {
            if (tempField.isAnnotationPresent(ID.class)) {
                try {
                    nameID = (String) tempField.get(entity);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return nameID;
    }

    @Override
    public Type getEntity(String name) {
        for (Type entity : records) {
            if (getTypeID(entity).equals(name)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public void updateNumericValue(Type objekt) {
        for (Type entity : records) {
            if (getTypeID(entity).equals(getTypeID(objekt))) {
                entity = objekt;
            }
        }
        write(records);
    }

}
