package org.homework.repository;

import com.google.gson.Gson;

import java.io.*;
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


    public void write(Type[] objekt) {
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


    public Type[] read() {
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

    @Override
    public Type getEntity(String name) {
        for (Type entity : records) {
            if (entity.equals(name)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public void putEntity(Type objekt) {
        for (Type entity : records) {
            if (entity.equals(objekt)) {
                entity = objekt;
            }
        }
        write(records);
    }

}
