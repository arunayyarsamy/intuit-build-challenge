package producerconsumer.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DataItemTest {

    @Test
    void testNormalItemCreation() {
        DataItem<String> item = DataItem.normal("test");
        assertNotNull(item);
        assertEquals("test", item.getValue());
        assertFalse(item.isPoisonPill());
        assertEquals(0, item.getVerificationCount());
    }

    @Test
    void testPoisonPillCreation() {
        DataItem<String> pill = DataItem.poison(5);
        assertNotNull(pill);
        assertNull(pill.getValue());
        assertTrue(pill.isPoisonPill());
        assertEquals(5, pill.getVerificationCount());
    }

    @Test
    void testToStringNormal() {
        DataItem<Integer> item = DataItem.normal(123);
        assertEquals("DataItem{value=123}", item.toString());
    }

    @Test
    void testToStringPoison() {
        DataItem<Object> pill = DataItem.poison(10);
        assertEquals("DataItem{POISON_PILL}", pill.toString());
    }
}
