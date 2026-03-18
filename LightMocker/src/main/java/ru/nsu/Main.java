package ru.nsu;

import java.util.ArrayList;

import ru.nsu.dsl.ref.MethodRefInt;

public class Main {
    public static void main(String[] args) {
        ArrayList<Integer> list = CustomUltimateMocker.create(ArrayList.class);
        System.out.println("mock class: " + list.getClass());
        System.out.println("mock is null: " + (list == null));

        CustomUltimateMocker.setup(list, (MethodRefInt<ArrayList<Integer>, Object>) ArrayList::get)
                .withArgs(0)
                .returns(42);

        Object result = list.get(0);
        System.out.println("result: " + result);
        System.out.println("result is null: " + (result == null));
    }
}


