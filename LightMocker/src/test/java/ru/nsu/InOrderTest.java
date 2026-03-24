package ru.nsu;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static ru.nsu.CustomUltimateMocker.*;
import ru.nsu.dsl.verify.InOrder;
import ru.nsu.exception.MockerException;

public class InOrderTest {
    
    @Test
    void testSingleMockInOrder() {
        List<String> mock = create(List.class);
        
        mock.add("first");
        mock.add("second");
        
        InOrder inOrder = inOrder(mock);
        
        inOrder.verify(mock, "add").withArgs("first").once();
        inOrder.verify(mock, "add").withArgs("second").once();
    }
    
    @Test
    void testMultipleMocksInOrder() {
        List<String> firstMock = create(List.class);
        List<String> secondMock = create(List.class);
        
        firstMock.add("A");
        secondMock.add("B");
        firstMock.add("C");
        
        InOrder inOrder = inOrder(firstMock, secondMock);
        
        inOrder.verify(firstMock, "add").withArgs("A").once();
        inOrder.verify(secondMock, "add").withArgs("B").once();
        inOrder.verify(firstMock, "add").withArgs("C").once();
    }
    
    @Test
    void testInOrderFailure() {
        List<String> mock = create(List.class);
        
        mock.add("first");
        mock.add("second");
        
        InOrder inOrder = inOrder(mock);
        
        // Перепутанный порядок
        assertThrows(MockerException.class, () -> {
            inOrder.verify(mock, "add").withArgs("second").once();
            inOrder.verify(mock, "add").withArgs("first").once();
        });
    }

    @Test
    void testInOrderWithSpy() {
        MathUtils spy = spy(new MathUtils());
        
        spy.addInstance(1, 1);
        spy.greetInstance("World");
        
        InOrder inOrder = inOrder(spy);
        
        inOrder.verify(spy, "addInstance").withArgs(1, 1).once();
        inOrder.verify(spy, "greetInstance").withArgs("World").once();
    }
}
