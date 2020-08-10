import java.util.*;

class AddressTranslator {

	//Class variables
	private int pageSize;
	private int numTLBRows;
	private List<Entry> pageTable;
    private TLB tlb;
    private int tlbHits;

	// Constructor   
	// pageSize: size of a page in bytes. Can be either 4096 or 8192.   
	// numTLBRows: number of rows in the TLB (translation lookaside buffer). Can be either 4 or 8 or 16.
	public AddressTranslator(int pageSize, int numTLBRows) {
	    // Page size initialization
        if(pageSize == 4096 || pageSize == 8192){
            this.pageSize = pageSize;

            // Page table creation
            pageTable = Arrays.asList(pageTableArray(this.pageSize));
        }else{
            throw new IllegalArgumentException("Wrong page size ! The page size should be 4096 or 8192.");
        }

        // numTLBRows initialization
        if(numTLBRows == 4 || numTLBRows == 8 || numTLBRows == 16){
            this.numTLBRows = numTLBRows;

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

            int offset;
            if(pageSize == 4096){
                // offset de 12 (array of size 31)
                offset = 12;
            }else{
                // offset de 13 (array of size 31)
                offset = 13;
            }

	        // First we try to find the physical address in the TLB
            int tlbRowIndexSize = 6;
            double tlbTag = 0;
            for(int i = binaryVpn.length - (tlbRowIndexSize + offset + 1), j=0; i >= 0 ; i--, j++){
                tlbTag += binaryVpn[i] * Math.pow (2, j);
            }
            int start = binaryVpn.length - (tlbRowIndexSize + offset + 1);
            double tlbRowIndex = 0;
            for(int i = start + tlbRowIndexSize, j=0; i >= start; i--, j++){
                tlbRowIndex += binaryVpn[i] * Math.pow (2, j);
            }
            try {
                // TLB hits !
                // TODO TLB
                int ppn = tlb.rows[(int) tlbRowIndex].findTag((int) tlbTag).ppn;
                tlbHits++;
                return ppn;
            }catch (NullPointerException e){
                // TLB miss !
                // TODO TLB
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

    private Entry[] pageTableArray(int pageSize){
	    Entry []array = new Entry[pageTableSize(pageSize)];
        for(int i = 0; i < array.length; i++) {
            array[i] = new Entry(i);
            // array[i] = null;
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

        // Constructor
        // tag: the tag used to find the slot in the TLB "table"
        // ppn: the associate ppn
        public TLBSlot(int tag, int ppn) {
            this.tag = tag;
            this.ppn = ppn;
        }

        public TLBSlot() {

        }
    }

    private class TLBRow{

	    // Class variables
        TLBSlot[] slots;

        // Constructor
        public TLBRow() {
            // Fixed size because it is a 4-way associative TLB
            this.slots = new TLBSlot[4];
            for(int i = 0; i < slots.length; i++) {
                slots[i] = new TLBSlot();
            }
        }

        // tag: tag to find in the given row
        // return the TLBSlot with the given tag or null
        public TLBSlot findTag(int tag){
            for(int i = 0; i < slots.length; i++){
                if(slots[i].tag == tag){
                    return slots[i];
                }
            }
            return null;
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
            // TODO Replacement policy
            for(int i = 0; i < 4; i++){
                TLBSlot current = tlb.rows[(int) tlbRowIndex].slots[i];
                if(current.tag == null){
                    tlb.rows[(int) tlbRowIndex].slots[i] = new TLBSlot((int) tlbTag, ppn);
                }
            }
        }
    }
} 
 