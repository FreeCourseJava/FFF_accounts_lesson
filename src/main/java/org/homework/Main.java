package org.homework;

import org.homework.dependency.injection.DependencyInjection;

public class Main {

    public static void main(String[] args) {

        System.out.println("Инициализация программы");
        DependencyInjection.start();
    }
    
}
