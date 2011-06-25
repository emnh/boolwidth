#include <jni.h>
#include <stdio.h>
#include "FastCutBool.h"


JNIEXPORT jint JNICALL Java_FastCutBool_test
        (JNIEnv *env, jint n, jintArray arr) {
//    jint buf[10];
//    jint i, sum = 0;
//    env->GetIntArrayRegion(arr, 0, 10, buf);
//    for (i = 0; i < 10; i++) {
//        sum += buf[i];
//    }
    return n;
}
