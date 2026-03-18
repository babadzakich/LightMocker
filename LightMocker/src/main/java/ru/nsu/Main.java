package ru.nsu;

import java.util.ArrayList;

import ru.nsu.CustomUltimateMocker.*;

import static ru.nsu.CustomUltimateMocker.create;
import static ru.nsu.CustomUltimateMocker.setup;

public class Main {
    public static void main(String[] args) {
        ArrayList list = create(ArrayList.class);
        System.out.println("mock class: " + list.getClass());
        System.out.println("mock is null: " + (list == null));

        setup(list, "get", int.class)
                .withArgs(0)
                .returns(42);

        Object result = list.get(0);
        System.out.println("result: " + result);
        System.out.println("result is null: " + (result == null));
    }
}


