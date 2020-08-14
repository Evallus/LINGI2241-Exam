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
    void test(){
        AddressTranslator addressTranslator = new AddressTranslator(4096, 4);
        int a = 0;
        int b = a + 16384;
        int c = b + 16384;
        int d = c + 16384;
        int e = d + 16384;
        int g = e + 16384;
        int h = g + 16384;
        int i = h + 16384;
        int j = i + 16384;
        try{
            int physicalAddress;
            addressTranslator.setPageTableEntry(0, 5, true);
            addressTranslator.setPageTableEntry(0, 17, true);
            addressTranslator.setPageTableEntry(1, 14, true);
            addressTranslator.setPageTableEntry(2, 15, true);
            addressTranslator.setPageTableEntry(3, 16, true);
            physicalAddress = addressTranslator.translate(a);
            physicalAddress = addressTranslator.translate(4096);
            System.out.println(addressTranslator.getNumberOfHits());
            physicalAddress = addressTranslator.translate(a);
            System.out.println(addressTranslator.getNumberOfHits());

            addressTranslator.setPageTableEntry(4, 6, true);
            physicalAddress = addressTranslator.translate(b);
            System.out.println(addressTranslator.getNumberOfHits());
            physicalAddress = addressTranslator.translate(b);

            addressTranslator.setPageTableEntry(8, 7, true);
            physicalAddress = addressTranslator.translate(c);
            System.out.println(addressTranslator.getNumberOfHits());
            physicalAddress = addressTranslator.translate(c);

            addressTranslator.setPageTableEntry(12, 8, true);
            physicalAddress = addressTranslator.translate(d);
            System.out.println(addressTranslator.getNumberOfHits());
            physicalAddress = addressTranslator.translate(d);

            addressTranslator.setPageTableEntry(16, 9, true);
            physicalAddress = addressTranslator.translate(e);
            System.out.println(addressTranslator.getNumberOfHits());
            System.out.println("---------------------------------------------------------");
            physicalAddress = addressTranslator.translate(e);

            addressTranslator.setPageTableEntry(20, 10, true);
            physicalAddress = addressTranslator.translate(g);
            System.out.println(addressTranslator.getNumberOfHits());
            System.out.println("---------------------------------------------------------");
            physicalAddress = addressTranslator.translate(g);

            addressTranslator.setPageTableEntry(24, 11, true);
            physicalAddress = addressTranslator.translate(h);
            System.out.println(addressTranslator.getNumberOfHits());
            physicalAddress = addressTranslator.translate(h);

            addressTranslator.setPageTableEntry(28, 12, true);
            physicalAddress = addressTranslator.translate(i);
            System.out.println(addressTranslator.getNumberOfHits());
            System.out.println("---------------------------------------------------------");
            physicalAddress = addressTranslator.translate(i);

            addressTranslator.setPageTableEntry(32, 13, true);
            physicalAddress = addressTranslator.translate(j);
            System.out.println(addressTranslator.getNumberOfHits());
            physicalAddress = addressTranslator.translate(j);
        }
        catch (IllegalArgumentException f){
            System.out.println("Exception");
        }
    }
}