# LINGI2241-Exam
The text file named “readme.txt” where you describe your cache management strategy (especially the replacement). Keep it short but precise. 

Some remarks :

* This project will use Java 14.
* We will assume that all addresses are positive int values, i.e. numbers between 0 and 2^31 - 1.
* We will also assume that the physical memory has a maximum size, i.e. all physical addresses between 0 and 2^31 - 1 are valid.

Cache management strategy :
* The TLB (translation lookaside buffer) is updated (slots can be added or even overwritten) everytime that a translation is done 
(i.e. everytime the functions "translate" or "prefetch" are called).
* I am using a Least Recently Used (over each TLB's row) as replacement policy since we had to implement it in software 
and that is the most usual way to do it in software.
(Simpler policies are more commonly used in hardware implementation).
