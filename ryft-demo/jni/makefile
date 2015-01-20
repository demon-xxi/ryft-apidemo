# Define a variable for classpath
CLASS_PATH = ../target/classes

# Define a virtual path for .class in the bin directory
vpath %.class $(CLASS_PATH)

all : wrapper.dll

# $@ matches the target, $< matches the first dependancy
wrapper.dll : Wrapper.o
	gcc -Wl,--add-stdcall-alias -shared -o $@ $< -LD:\work\projects\gitlab\elance\ryft\ryft-mock\Debug -llibryft-mock

# $@ matches the target, $< matches the first dependancy
Wrapper.o : Wrapper.c Wrapper.h
	gcc -I"D:\work\tools\java\jdk7\include" -I"D:\work\tools\java\jdk7\include\win32" -c $< -o $@

# $* matches the target filename without the extension
Wrapper.h : com/metasys/ryft/Wrapper.class
	javah -classpath $(CLASS_PATH) com.metasys.ryft.$*

clean :
	rm com_metasys_ryft_Wrapper.h Wrapper.o wrapper.dll
	