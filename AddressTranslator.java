class AddressTranslator {   
	// Constructor   
	// pageSize: size of a page in bytes. Can be either 4096 or 8192.   
	// numTLBRows: number of rows in the TLB. Can be either 4 or 8 or 16.   
	public AddressTranslator(int pageSize, int numTLBRows) { … } 
 
	// Sets an entry in the page table VPN->PPN. There is only one flag.   
	// Should throw an IllegalArgumentException exception if the page numbers 
	// are not in a valid range.   
	public void setPageTableEntry(int VPN, int PPN, boolean isPresent) { … } 
 
	// Translates the virtual address and returns the corresponding physical address.   
	// Must throw an IllegalArgumentException exception if there is no page 
	// present at that address.   
	public int translate(int virtualAddress) { … } 
 
	// Returns the number of TLB hits   
	public int getNumberOfHits() { … }  
} 
 