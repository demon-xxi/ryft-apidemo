
--------------------------------------------------------------------------
Instructions for using a cloned VM disk image in a new VirtualBox instance
Ryft Systems Inc., P. McGarry, 20150114
--------------------------------------------------------------------------

1. Download VirtualBox from https://www.virtualbox.org/wiki/Downloads .  We use it on both 64-bit Linux (Ubuntu 14.04 LTS) and on 64-bit windows.  

***
*** YOU MUST USE A 64-BIT HOST IMPLEMENTATION OF VIRTUALBOX, since the disk image you'll be running is itself a 64-bit environment!
***

2. Install it.  If on windows, accept all installation options, and accept any security windows that come up.

3. Run VirtualBox.

4. Select Machine > New

  Fill in the dialog fields

  Name: ryftone-vm-pm    <-- you could use something different, but to avoid confusion we recommend "ryftone-vm-pm" which is the same name that the clone's OS is setup for.
  Type: Linux
  Version: Ubuntu (64-bit)

  Click Next.

5. Dialog: Memory Size.  

We recommend using at least 2GB (2048 MB) for the VM RAM space at present.  

Once you set the RAM size, click "Next".

(Note that our physical box implementation uses 16GB, but that is overkill for use with the scaled-back VM implementation.)

6. Dialog: Hard drive.  

Choose the radio button for "Use an existing virtual hard drive file".  

After selecting the radio button, use the file browse icon to point to the "clone-rytonevmpm-20150113.vdi" file from the provided USB stick

***
*** IMPORTANT NOTE: THE USB STICK IS PROVIDED AS AN NTFS FILE SYSTEM!  SO YOUR OPERATING SYSTEM MUST BE ABLE TO READ AN NTFS FILE SYSTEM.
*** This was done since the disk image is too large for a FAT32 file system.
*** Windows 64-bit will not have an issue, but some Linux and/or Mac OS distros may have problems without installing specialty NTFS drivers.
***

Once selected, click the Open button.  

Then click the Create button.

7. The VM now appears in the lefthand pane of your VirtualBox manager window.  Select it if it is not selected yet, but don't start it yet.

8. On the right-hand pane you'll see various VM properties.  Hover over the word "System" and it turns blue - click on it.  

A Settings window comes up.  

Move to the Processor tab and check the "Enable PAE/NX" box.  

Move to the Acceleration tab and make sure that both the "Enable VT-x/AMD-V" and "Enable Nested Paging" are both checked - if they aren't, click on the checkbox to enable them.

9. Now in that same Settings window, click the Network icon/word in the left hand pane.  Click on it.  

For Adapter 1, make sure the "Enable Network Adapter" checkbox is checked, and if it is not, click it so the checkmark appears.  

From the "Attached to:" dropdown, select "Bridged Adapter".

In the "Name:" field, make sure it maps to your active network connection on your host machine.  This is usually a wired or wireless network adapter, depending on your host machine's network configuration.

10. Once those System & Network settings are finished, click OK, and all of them will now be saved.

11. Start the VM with the Start button in the VirtualBox Manager window.  The VM will show up locally, and you can interact with it directly from the graphical console that comes up.  

The login and password are: ryftuser / ryftuserpm

12. The network configuration of the VM by default is as follows:

static IP address: 10.17.40.2
Network mask: 255.255.0.0
Gateway: 10.17.0.1

This is the internal network that we use here at Ryft in our R&D group, but that is probably not what you want for your network environment.  

There are several ways you can modify this to suit your environment, but the easiest way and the way I recommend is using the graphical user interface that was started when the VM was started to open a terminal (an icon exists for this in the lefthand Unity pane in the GUI), stop the VM's eth0 network interface, and then update /etc/network/interfaces for the static IP addressing, mask, gateway and DNS server(s) for your network configuration.  Here are the commands to accomplish that:

Click the terminal icon in the Unity pane... then:
$ sudo ifdown eth0
(if it asks, the sudo password is the same as the login password: ryftuserpm)
$ sudo vi /etc/network/interfaces

... and then change the values as appropriate.  If you don't know how to use the vi editor, use another editor of your choice (but note that you must use sudo access to edit the file given its system permissions).
After you exit the editor, restart the network interface on eth0 using:

$ sudo ifup eth0

Then issue the ifconfig command to verify your new network settings:

$ ifconfig

If they are correct, great, and if not, repeat this step until you have it right.  It is a good idea to attempt to ping the VM from a terminal on your host machine to make sure the settings are accurate and the VM is reachable.

13. Once the networking is correct, you should be able to open up a terminal from any machine on your network (including the host machine's OS) and ssh into the box using the static IP address that you configured.

Note that for a Linux host, you can use ssh directly; on Windows, use something like putty or cygwin with the openssh package installed.  I personally use cygwin with openssh.

The same login credentials will apply (ryftuser / ryftuserpm).  You can have as many ssh sessions as you like, and they can of course operate simultaneously in time with the graphical session.

