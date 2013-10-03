
all: libFastCutBool.so FastCutBool.class

%.class: %.java
	javac $<

FastCutBool.h: FastCutBool.class
	javah -classpath . -jni FastCutBool

libFastCutBool.so: FastCutBool.cc
	gcc  -o libFastCutBool.so -shared -Wl,-soname,libFastCutBool.so \
		 -I /usr/lib/jvm/java-6-sun/include \
		 -I /usr/lib/jvm/java-6-sun/include/linux \
		 FastCutBool.cc \
		 -static -lc
