import java.util.*;

class AddressTranslator {

	//Class variables
	private int pageSize;
	private int numTLBRows;
	private List<Entry> pageTable;
    private TLB tlb;
    private int tlbHits;
    private int offset;
    private int tlbRowIndexSize;
    private int pageTableSize;

	// Constructor   
	// pageSize: size of a page in bytes. Can be either 4096 or 8192.   
	// numTLBRows: number of rows in the TLB (translation lookaside buffer). Can be either 4 or 8 or 16.
	public AddressTranslator(int pageSize, int numTLBRows) {
	    // Page size initialization
        if(pageSize == 4096 || pageSize == 8192){
            this.pageSize = pageSize;

            if(pageSize == 4096){
                // offset de 12 (array of size 31)
                offset = 12;
            }else{
                // offset de 13 (array of size 31)
                offset = 13;
            }

            // Page table creation
            pageTableSize = pageTableSize(this.pageSize);
            pageTable = Arrays.asList(pageTableArray(this.pageTableSize));
        }else{
            throw new IllegalArgumentException("Wrong page size ! The page size should be 4096 or 8192.");
        }

        // numTLBRows initialization
        if(numTLBRows == 4 || numTLBRows == 8 || numTLBRows == 16){
            this.numTLBRows = numTLBRows;

            switch (numTLBRows){
                case 16:
                    tlbRowIndexSize = 4;
                    break;

                case 8:
                    tlbRowIndexSize = 3;
                    break;

                case 4:
                    tlbRowIndexSize = 2;
                    break;
            }

            // TLB creation
            tlb = new TLB(numTLBRows);
        }else{
            throw new IllegalArgumentException("Wrong number of rows in the TLB (translation lookaside buffer) ! The number of rows in the TLB should be 4, 8 or 16.");
        }
	}
 
	// Sets an entry in the page table VPN->PPN. There is only one flag.   
	// Should throw an IllegalArgumentException exception if the page numbers 
	// are not in a valid range.   
	public void setPageTableEntry(int VPN, int PPN, boolean isPresent) {
	    try{
            pageTable.set(VPN, new Entry(VPN, PPN, isPresent));
        }catch (IndexOutOfBoundsException e){
            throw new IllegalArgumentException("Page number not in valid range.");
        }
    }

    // Translates the virtual address and returns the corresponding physical address.
	// Must throw an IllegalArgumentException exception if there is no page 
	// present at that address.   
	public int translate(int virtualAddress) {
	    try{
            int[] binaryVpn = intToLongBinary(virtualAddress);

	        // First we try to find the physical address in the TLB
            double tlbTag = 0;
            for(int i = binaryVpn.length - (tlbRowIndexSize + offset + 1), j=0; i >= 0 ; i--, j++){
                tlbTag += binaryVpn[i] * Math.pow (2, j);
            }
            int start = binaryVpn.length - (tlbRowIndexSize + offset + 1);
            double tlbRowIndex = 0;
            for(int i = start + tlbRowIndexSize, j=0; i > start; i--, j++){
                tlbRowIndex += binaryVpn[i] * Math.pow (2, j);
            }
            try {
                // TLB hits !
                int ppn = tlb.rows[(int) tlbRowIndex].findTag((int) tlbTag).ppn;
                tlbHits++;
                return ppn;
            }catch (NullPointerException e){
                // TLB miss !
                // Prefetch the next entry
                prefetch(virtualAddress+pageSize);
            }

	        // Otherwise we try to translate the virtual address manually
            double vpnEntry = 0;
            for(int i = binaryVpn.length-(offset+1), j=0; i >= 0; i--, j++){
                vpnEntry += binaryVpn[i] * Math.pow (2, j);
            }
            Entry entry = pageTable.get((int) vpnEntry);
            if(entry.present){
                int[] binaryPpn = intToBinary(entry.ppn);
                int[] tmp = new int[binaryPpn.length + offset];
                for(int i = 0; i < binaryPpn.length; i++){
                    tmp[i] = binaryPpn[i];
                }
                for(int i = binaryPpn.length, j = binaryVpn.length-offset; j < binaryVpn.length-1; i++, j++){
                    tmp[i] = binaryVpn[j];
                }
                int res = 0;
                for(int i = 0, j = tmp.length-1; i < tmp.length; i++, j--){
                    res += tmp[i] * Math.pow (2, j);
                }
                tlb.addToTlb(tlbRowIndex, tlbTag, res);
                return res;
            }else{
                throw new IllegalArgumentException("There is no page present at that address.");
            }
        }catch (Exception e){
	        throw new IllegalArgumentException("There is no page present at that address.");
        }
    }

    private void prefetch(int virtualAddress) {
	    try {
            int[] binaryVpn = intToLongBinary(virtualAddress);

            double tlbTag = 0;
            for (int i = binaryVpn.length - (tlbRowIndexSize + offset + 1), j = 0; i >= 0; i--, j++) {
                tlbTag += binaryVpn[i] * Math.pow(2, j);
            }
            int start = binaryVpn.length - (tlbRowIndexSize + offset + 1);
            double tlbRowIndex = 0;
            for (int i = start + tlbRowIndexSize, j = 0; i > start; i--, j++) {
                tlbRowIndex += binaryVpn[i] * Math.pow(2, j);
            }
            double vpnEntry = 0;
            for (int i = binaryVpn.length - (offset + 1), j = 0; i >= 0; i--, j++) {
                vpnEntry += binaryVpn[i] * Math.pow(2, j);
            }
            Entry entry = pageTable.get((int) vpnEntry);
            if (entry.present) {
                int[] binaryPpn = intToBinary(entry.ppn);
                int[] tmp = new int[binaryPpn.length + offset];
                for (int i = 0; i < binaryPpn.length; i++) {
                    tmp[i] = binaryPpn[i];
                }
                for (int i = binaryPpn.length, j = binaryVpn.length - offset; j < binaryVpn.length - 1; i++, j++) {
                    tmp[i] = binaryVpn[j];
                }
                int res = 0;
                for (int i = 0, j = tmp.length - 1; i < tmp.length; i++, j--) {
                    res += tmp[i] * Math.pow(2, j);
                }
                tlb.addToTlb(tlbRowIndex, tlbTag, res);
            }
        }catch (Exception e){
	        //Virtual address too big
        }
    }

    // Returns the number of TLB hits
	public int getNumberOfHits() {
        return tlbHits;
    }

    private int pageTableSize(int pageSize){
	    if(pageSize == 4096){
	        return 1048575;
        }
	    return 524287;
    }

    private Entry[] pageTableArray(int pageTableSize){
	    Entry []array = new Entry[pageTableSize];
        for(int i = 0; i < array.length; i++) {
            array[i] = new Entry(i);
        }
	    return array;
    }

    private int[] intToLongBinary(int value){
        String tmp = Long.toBinaryString( Integer.toUnsignedLong(value) | 0x100000000L ).substring(1);
        String[] tmpArray = tmp.split("");
        int[] res = new int[tmpArray.length];
        for (int i = 0; i < tmpArray.length; i++) {
            res[i] = Integer.parseInt(tmpArray[i]);
        }
        return res;
    }

    private int[] intToBinary(int value){
        String tmp = Integer.toBinaryString(value);
        String[] tmpArray = tmp.split("");
        int[] res = new int[tmpArray.length];
        for (int i = 0; i < tmpArray.length; i++) {
            res[i] = Integer.parseInt(tmpArray[i]);
        }
        return res;
    }

    // Inner class
    private class Entry {

	    // Class variables
	    private int vpn;
	    private int ppn;
	    private boolean present;

        // Constructor
        // vpn: virtual page number used for the translation of this entry.
        // ppn: physical page number used for the translation of this entry.
        // present: tells if the entry is accessible (true) or not (false).
        public Entry(int vpn, int ppn, boolean present) {
            this.vpn = vpn;
            this.ppn = ppn;
            this.present = present;
        }

        public Entry(int vpn) {
            this.vpn = vpn;
        }
    }

    private class TLBSlot{

	    // Class variables
	    Integer tag;
	    Integer ppn;
	    TLBSlot prev;
	    TLBSlot next;

        // Constructor
        // tag: the tag used to find the slot in the TLB "table"
        // ppn: the associate ppn
        public TLBSlot(int tag, int ppn) {
            this.tag = tag;
            this.ppn = ppn;
        }

        public TLBSlot(){}
    }

    private class TLBRow{

	    // Class variables
        TLBSlot head;
        TLBSlot tail;
        // Fixed size since it is a 4-way associative TLB
        int slotNumber = 4;
        int capacity = 0;

        // Constructor
        public TLBRow() {
            head = new TLBSlot();
            tail = new TLBSlot();
            head.next = tail;
            tail.prev = head;
            head.prev = null;
            tail.next = null;
        }

        // tag: tag to find in the given row
        // return the TLBSlot with the given tag or null
        public TLBSlot findTag(int tag){
            TLBSlot current = head;
            for(int i = 0; i < slotNumber; i++){
                if(current.tag != null){
                    if(current.tag == tag){
                        return current;
                    }
                }
                if(current.next.tag != null) {
                    current = current.next;
                }
            }
            return null;
        }

        public void addToRow(int tlbTag, int ppn) {
            TLBSlot slot = findTag(tlbTag);
            if(slot != null){
                // Check if it is not the head
                if(slot.prev != null){
                    slot.prev.next = slot.next;
                    slot.next.prev = slot.prev;
                    head.prev = slot;
                    slot.prev = null;
                    slot.next = head;
                    head = slot;
                }
            }else{
                TLBSlot slotToAdd = new TLBSlot(tlbTag, ppn);
                head.prev = slotToAdd;
                slotToAdd.next = head;
                slotToAdd.prev = null;
                head = slotToAdd;
                capacity++;
                if(capacity == 4) {
                    tail.prev.next = tail.next;
                    tail = tail.prev;
                }
            }
        }
    }

    private class TLB{

	    // Class variables
        TLBRow[] rows;

        // Constructor
        // rowNumber: the number of row present in TLB "table"
        public TLB(int rowNumber) {
            this.rows = new TLBRow[rowNumber];
            for(int i = 0; i < rows.length; i++) {
                rows[i] = new TLBRow();
            }
        }

        public void addToTlb(double tlbRowIndex, double tlbTag, int ppn) {
            tlb.rows[(int) tlbRowIndex].addToRow((int) tlbTag, ppn);
        }
    }

    public static void main( String[] args ) {
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
 