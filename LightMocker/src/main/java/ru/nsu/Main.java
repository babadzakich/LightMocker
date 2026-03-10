package ru.nsu;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Integer> list = CustomUltimateMocker.create(ArrayList.class);
        System.out.println("mock class: " + list.getClass());
        System.out.println("mock is null: " + (list == null));

        // метод с аргументами — через имя + типы
        CustomUltimateMocker.setup(list, "get", int.class)
                .withArgs(0)
                .returns(42);

        Object result = list.get(0);
        System.out.println("result: " + result);
        System.out.println("result is null: " + (result == null));
    }
}


