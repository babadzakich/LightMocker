package ru.nsu;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static ru.nsu.CustomUltimateMocker.*;

public class SpyTest {
    @Test
    void testSpyArrayList() {
        List<String> list = new ArrayList<>();
        list.add("first");
        
        List<String> spyList = spy(list);
        
        // 1. Реальное поведение
        assertEquals(1, spyList.size());
        assertEquals("first", spyList.get(0));
        
        // 2. Стаббирование
        setup(spyList, "size").returns(100);
        assertEquals(100, spyList.size());
        
        // 3. Другие методы все еще работают реально
        spyList.add("second");
        // Проверяем оригинал (так как спай делегирует ему)
        assertEquals(2, list.size());
        assertEquals(100, spyList.size());
        
        // 4. Верификация
        verify(spyList, "add").withArgs("second").once();
        verify(spyList, "size").atLeast(1);
    }

    @Test
    void testSpyCustomClass() {
        MathUtils realMath = new MathUtils();
        MathUtils spyMath = spy(realMath);

        // Real call
        assertEquals(5, spyMath.addInstance(2, 3));

        // Stub
        setup(spyMath, "addInstance").withArgs(2, 3).returns(10);
        assertEquals(10, spyMath.addInstance(2, 3));

        // Another call — real
        assertEquals(7, spyMath.addInstance(3, 4));

        verify(spyMath, "addInstance").withArgs(2, 3).times(2);
        verify(spyMath, "addInstance").withArgs(3, 4).once();
    }
}
