rm -rf hello/
unzip $1 -d hello/
rm -rf hello/jni/armeabi-v7a hello/jni/x86/
rm -rf hello.zip
zip -r hello.zip hello/
ls -l hello.zip
