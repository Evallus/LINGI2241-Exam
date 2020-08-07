import java.util.*;

class AddressTranslator {

	//Class variables
	private int pageSize;
	private int numTLBRows;
	private List<Entry> pageTable;
    private LinkedHashMap<Integer,Integer> tlb;

	// Constructor   
	// pageSize: size of a page in bytes. Can be either 4096 or 8192.   
	// numTLBRows: number of rows in the TLB (translation lookaside buffer). Can be either 4 or 8 or 16.
	public AddressTranslator(int pageSize, int numTLBRows) {
	    // Page size intialisation
        if(pageSize == 4096 || pageSize == 8192){
            this.pageSize = pageSize;

            // Page table creation
            pageTable = new LinkedList<Entry>(pageTableSize(this.pageSize));
        }else{
            System.out.println("Wrong page size ! The page size should be 4096 or 8192.");
        }

        // numTLBRows initialisation
        if(numTLBRows == 4 || numTLBRows == 8 || numTLBRows == 16){
            this.numTLBRows = numTLBRows;

            // TLB creation
            tlb = new LinkedHashMap<Integer,Integer>(this.numTLBRows);
        }else{
            System.out.println("Wrong number of rows in the TLB (translation lookaside buffer) ! The number of rows in the TLB should be 4, 8 or 16.");
        }
	}
 
	// Sets an entry in the page table VPN->PPN. There is only one flag.   
	// Should throw an IllegalArgumentException exception if the page numbers 
	// are not in a valid range.   
	public void setPageTableEntry(int VPN, int PPN, boolean isPresent) {

    }
 
	// Translates the virtual address and returns the corresponding physical address.   
	// Must throw an IllegalArgumentException exception if there is no page 
	// present at that address.   
	public int translate(int virtualAddress) {

    }
 
	// Returns the number of TLB hits   
	public int getNumberOfHits() {

    }

    private int pageTableSize(int pageSize){
	    if(pageSize == 4096){
	        return 1048575;
        }
	    return 524287;
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

        // Getters
        public int getVpn() {
            return vpn;
        }

        public int getPpn() {
            return ppn;
        }

        public boolean isPresent() {
            return present;
        }

        // Setters
        public void setVpn(int vpn) {
            this.vpn = vpn;
        }

        public void setPpn(int ppn) {
            this.ppn = ppn;
        }

        public void setPresent(boolean present) {
            this.present = present;
        }
    }
} 
 