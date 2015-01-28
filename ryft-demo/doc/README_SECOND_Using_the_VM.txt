
--------------------------------------------------------------------------
Using the VM
Ryft Systems Inc., P. McGarry, 20150114
--------------------------------------------------------------------------

---
General Usage
---


To use the VM from a separate host operating system:

1.	If you are using Linux as your host, then you can ssh directly.  If you are using windows, install 64-bit Cygwin on your 64-bit host windows operating system (you can use the URL http://cygwin.com/setup-x86_64.exe, or you can go to cygwin.com and navigate to that link directly on your own), and make sure you select the openssh and openssl packages as part of the install.


2.	Open first session
	a.	ssh ryftuser@ryftone-vm-pm
		i.	password: ryftuserpm
	b.	cd
	c.	ccc_top     <-- starts up the ccc_top program so that you can see example program progress as they run
	d. 	leave this session open ...


3.	Open a second session
	a.	ssh ryftuser@ryftone-vm-pm
		i.	password: ryftuserpm
	b.	cd
	c.	tail -f /var/log/ryft/rfytone.log     <-- so you can watch the log files as you run example programs
	d. 	leave this session open ...


4.	Open a third session
	a.	ssh ryftuser@ryftone-vm-pm
		i.	password: ryftuserpm
	b.	cd ryft_example
	c.	./ryft_example 4   <-- runs an example program using all 4 system resources (which can be seen in the ccc_top output window while this runs)
	c.	./ryft_example 2   <-- runs an example program using 2 of the 4 system resources  (which can be seen in the ccc_top output window while this runs)
	e.	If you try and do these things in parallel in multiple windows, they will just queue up, and you can see that in the ccc_top output as well


---
Processes of interest:
---

ryftuser@ryftone-vm-pm:~$ ps -AF | grep ccc
root       883   882  0 144156 5580   0 15:01 ?        00:00:00 /home/ryftuser/main/software/CCC/src/ccc_mgr

ryftuser@ryftone-vm-pm:~$ ps -AF | grep tlog
root       958     1  0  5406  1200   0 15:01 ?        00:00:00 /usr/sbin/tlogd


---
Important notes:
---

- The directory /ryftone (from the root directory, not the user's home directory), is currently configured to store input and output files for the VM.  
	- This is the case only for the VM.  For real target hardware, the 48 drive SSD RAID storage subystem will hold the filesets.  
	- There's a sample input file "passengers.txt" in that directory, which is what the example program uses as input (as can be seen in its source code).
	- Similarly, after program execution, results will be stored in that directory as "results.txt".
	- But, since there is no backend Ryft hardware attached to the VM, the files created at present by the VM in isolation are meaningless.

- The full source code for the ryft_example program is in ~/ryft_example.  
	- This can be used as a template for creating your own sample programs (or by auto-creating source code to be compiled by some web front-end after updating the various API parameters in the source code itself).
	- You could also modify the source code in-place with an editor of your choice (such as vi), and then recompile the program using:
		- cd ~/ryft_example
		- vi main.c
			<make your changes and save the file>
		- make clean
		- make
		.. the output executable will be "ryft_example" in that directory.  Note that this is what was run in step 4 in the "General Usage" section above.

