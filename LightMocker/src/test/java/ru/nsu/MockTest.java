package ru.nsu;

import org.junit.jupiter.api.Test;

import java.util.List;

import static ru.nsu.CustomUltimateMocker.create;
import static ru.nsu.CustomUltimateMocker.setup;
import static ru.nsu.CustomUltimateMocker.verify;

public class MockTest {

    @Test
    void test() {
        List<Integer> mockList = create(List.class);

        setup(mockList, "size")
                .returns(100);

        setup(mockList, "get", int.class)
                .withArgs(0)
                .returns(777);

        setup(mockList, "get", int.class)
                .withArgs(1)
                .returns(999);

        setup(mockList, "get", int.class)
                .withArgs(-1)
                .thenThrow(new IndexOutOfBoundsException("absolute cinema"));

        System.out.println("size " + mockList.size());
        System.out.println("elem 0 " + mockList.get(0));
        System.out.println("elem 1 " + mockList.get(1));

        System.out.println("elem 5 " + mockList.get(5));

        try {
            mockList.get(-1);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }

        verify(mockList, "size").once();
        verify(mockList, "get", int.class).withArgs(0).once();
        verify(mockList, "get", int.class).withArgs(1).times(1);
        verify(mockList, "get", int.class).withArgs(5).atLeast(1);
        verify(mockList, "get", int.class).withArgs(-1).atMost(1);
        verify(mockList, "clear").never();
    }
}
