package ru.nsu;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static ru.nsu.CustomUltimateMocker.*;
import static ru.nsu.CustomUltimateMocker.verify;

public class ProxyTest {
    @Test
    void test() {
        List<Integer> mockList = create(List.class);

        when(mockList.size())
                .returns(100);

        when(mockList.get(0))
                .returns(777);

        when(mockList.get(1))
                .returns(999);

        when(mockList.get(-1))
                .thenThrow(new IndexOutOfBoundsException("absolute cinema"));

        assertEquals(mockList.size(), 100);
        assertEquals(mockList.get(0), 777);
        assertEquals(mockList.get(1), 999);
        assertNull(mockList.get(5));

        try {
            mockList.get(-1);
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }

        verify(mockList, "size").times(2);
        verify(mockList, "get", int.class).withArgs(0).times(2);
        verify(mockList, "get", int.class).withArgs(1).times(2);
        verify(mockList, "get", int.class).withArgs(5).atLeast(1);
        verify(mockList, "get", int.class).withArgs(-1).atMost(2);
        verify(mockList, "clear").never();
    }
}
