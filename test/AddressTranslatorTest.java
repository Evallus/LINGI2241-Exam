import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class AddressTranslatorTest {

    @Test
    void AddressTranslatorOk(){
        AddressTranslator translator = new AddressTranslator(4096, 16);
    }

    @Test
    void AddressTranslatorWrongPageSize(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AddressTranslator(2048, 16));
        assertEquals("Wrong page size ! The page size should be 4096 or 8192.", exception.getMessage());
    }

    @Test
    void AddressTranslatorWrongTlb(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AddressTranslator(4096, 32));
        assertEquals("Wrong number of rows in the TLB (translation lookaside buffer) ! The number of rows in the TLB should be 4, 8 or 16.", exception.getMessage());
    }

    @Test
    void setPageTableEntryOkTLBMiss() {
        AddressTranslator translator = new AddressTranslator(4096, 16);
        translator.setPageTableEntry(1, 5, true);
        assertEquals(20484, translator.translate(4100));
        assertEquals(0, translator.getNumberOfHits());
    }

    @Test
    void setPageTableEntryOkTLBHit() {
        AddressTranslator translator = new AddressTranslator(4096, 16);
        translator.setPageTableEntry(1, 5, true);
        translator.translate(4100);
        assertEquals(20484, translator.translate(4100));
        assertEquals(1, translator.getNumberOfHits());
    }

    @Test
    void setPageTableEntryExpectError() {
        AddressTranslator translator = new AddressTranslator(4096, 16);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> translator.setPageTableEntry(1048575+1,2,true));
        assertEquals("Page number not in valid range.", exception.getMessage());
    }

    @Test
    void translateOk() {
        AddressTranslator translator = new AddressTranslator(4096, 16);
        translator.setPageTableEntry(1, 5, true);
        assertEquals(20484, translator.translate(4100));
    }

    @Test
    void translateExpectNotPresent() {
        AddressTranslator translator = new AddressTranslator(4096, 16);
        translator.setPageTableEntry(1, 5, false);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> translator.translate(1));
        assertEquals("There is no page present at that address.", exception.getMessage());
    }

    @Test
    void translateExpectError() {
        AddressTranslator translator = new AddressTranslator(4096, 16);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> translator.translate(1));
        translator.setPageTableEntry(1, 5, true);
        assertEquals("There is no page present at that address.", exception.getMessage());
    }

    @Test
    void translateExpectNotInRange() {
        AddressTranslator translator = new AddressTranslator(4096, 16);
        translator.setPageTableEntry(1, 5, true);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> translator.translate(1048575+1));
        assertEquals("There is no page present at that address.", exception.getMessage());
    }

    @Test
    void getNumberOfHits() {
        AddressTranslator translator = new AddressTranslator(4096, 16);
    }

    @Test
    void testTLBLRU() {
        AddressTranslator translator = new AddressTranslator(4096, 16);
        translator.setPageTableEntry(1, 5, true);
        translator.translate(4100);
        translator.translate(4100);
        assertEquals(1, translator.getNumberOfHits());

        translator.setPageTableEntry(17, 9, true);
        translator.translate(69636);
        translator.translate(69636);
        assertEquals(2, translator.getNumberOfHits());

        translator.setPageTableEntry(33, 2, true);
        translator.translate(135172);
        translator.translate(135172);
        assertEquals(3, translator.getNumberOfHits());

        translator.setPageTableEntry(49, 15, true);
        translator.translate(200708);
        translator.translate(200708);
        assertEquals(4, translator.getNumberOfHits());

        translator.setPageTableEntry(65, 6, true);
        translator.translate(266244);
        translator.translate(266244);
        assertEquals(5, translator.getNumberOfHits());


        translator.translate(4100);
        assertEquals(5, translator.getNumberOfHits());
    }

    @Test
    void testTLBLRUBiS() {
        AddressTranslator translator = new AddressTranslator(4096, 16);
        translator.setPageTableEntry(1, 5, true);
        translator.translate(4100);
        translator.translate(4100);
        assertEquals(1, translator.getNumberOfHits());

        translator.setPageTableEntry(17, 9, true);
        translator.translate(69636);
        translator.translate(69636);
        assertEquals(2, translator.getNumberOfHits());

        translator.setPageTableEntry(33, 2, true);
        translator.translate(135172);
        translator.translate(135172);
        assertEquals(3, translator.getNumberOfHits());

        translator.setPageTableEntry(49, 15, true);
        translator.translate(200708);
        translator.translate(200708);
        assertEquals(4, translator.getNumberOfHits());

        translator.translate(4100);
        assertEquals(5, translator.getNumberOfHits());

        translator.setPageTableEntry(65, 6, true);
        translator.translate(266244);
        translator.translate(266244);
        assertEquals(6, translator.getNumberOfHits());


        translator.translate(4100);
        assertEquals(7, translator.getNumberOfHits());

        translator.translate(69636);
        assertEquals(7, translator.getNumberOfHits());
    }
}