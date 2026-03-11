package ru.nsu;

import org.junit.jupiter.api.Test;

import java.util.List;


public class MockTest {

    CustomUltimateMocker mock = new CustomUltimateMocker();

    @Test
    void test() {

        List<Integer> mockList = mock.create(List.class);
        mock.setup(mockList, List::size)
                .returns(100);

        mock.setup(mockList, List::get)
                .withArgs(0)
                .returns(777);

        mock.setup(mockList, List::get)
                .withArgs(1)
                .returns(999);

        mock.setup(mockList, List::get)
                .withArgs(-1)
                .thenThrow(new IndexOutOfBoundsException("absolute cinema"));


        System.out.println("size " + mockList.size()); // 100
        System.out.println("elem 0 " + mockList.get(0));   // 777
        System.out.println("elem 1 " + mockList.get(1));   // 999

        System.out.println("elem 5 " + mockList.get(5));   // null

        try {
            mockList.get(-1);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
    }
}
