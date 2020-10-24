/*******************************************************************************
 * Copyright (C) 2009-2017 the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
/**
 * hawtjni.h
 *
 * This file contains the global macro declarations for a hawtjni based 
 * library.
 *
 */
#ifndef INC_HAWTJNI_H
#define INC_HAWTJNI_H
#define _MULTI_THREADED

#ifdef HAVE_CONFIG_H
  #include "config.h"
#endif

#include "jni.h"
#include <stdint.h>


#ifdef __cplusplus
extern "C" {
#endif

extern int IS_JNI_1_2;

#ifndef JNI64
#if defined(_LP64)
#define JNI64
#endif
#endif

/* 64 bit support */
#ifndef JNI64

/* int/long defines */
#define GetIntLongField GetIntField
#define SetIntLongField SetIntField
#define GetIntLongArrayElements GetIntArrayElements
#define ReleaseIntLongArrayElements ReleaseIntArrayElements
#define GetIntLongArrayRegion GetIntArrayRegion
#define SetIntLongArrayRegion SetIntArrayRegion
#define NewIntLongArray NewIntArray
#define CallStaticIntLongMethod CallStaticIntMethod
#define CallIntLongMethod CallIntMethod
#define CallStaticIntLongMethodV CallStaticIntMethodV
#define CallIntLongMethodV CallIntMethodV
#define jintLongArray jintArray
#define jintLong jint
#define I_J "I"
#define I_JArray "[I"

/* float/double defines */
#define GetFloatDoubleField GetFloatField
#define SetFloatDoubleField SetFloatField
#define GetFloatDoubleArrayElements GetFloatArrayElements
#define ReleaseFloatDoubleArrayElements ReleaseFloatArrayElements
#define GetFloatDoubleArrayRegion GetFloatArrayRegion
#define jfloatDoubleArray jfloatArray
#define jfloatDouble jfloat
#define F_D "F"
#define F_DArray "[F"

#else
	
/* int/long defines */
#define GetIntLongField GetLongField
#define SetIntLongField SetLongField
#define GetIntLongArrayElements GetLongArrayElements
#define ReleaseIntLongArrayElements ReleaseLongArrayElements
#define GetIntLongArrayRegion GetLongArrayRegion
#define SetIntLongArrayRegion SetLongArrayRegion
#define NewIntLongArray NewLongArray
#define CallStaticIntLongMethod CallStaticLongMethod
#define CallIntLongMethod CallLongMethod
#define CallStaticIntLongMethodV CallStaticLongMethodV
#define CallIntLongMethodV CallLongMethodV
#define jintLongArray jlongArray
#define jintLong jlong
#define I_J "J"
#define I_JArray "[J"

/* float/double defines */
#define GetFloatDoubleField GetDoubleField
#define SetFloatDoubleField SetDoubleField
#define GetFloatDoubleArrayElements GetDoubleArrayElements
#define ReleaseFloatDoubleArrayElements ReleaseDoubleArrayElements
#define GetFloatDoubleArrayRegion GetDoubleArrayRegion
#define jfloatDoubleArray jdoubleArray
#define jfloatDouble jdouble
#define F_D "D"
#define F_DArray "[D"

#endif


#ifdef JNI_VERSION_1_2
extern JavaVM *JVM;
#endif

#ifdef __GNUC__
  #define hawtjni_w_barrier() __sync_synchronize()
#elif defined(SOLARIS2) && SOLARIS2 >= 10
  #include <mbarrier.h>
  #define hawtjni_w_barrier() __machine_w_barrier()
#elif defined(__APPLE__)
  #include <libkern/OSAtomic.h>
  #define hawtjni_w_barrier() OSMemoryBarrier()
#elif defined(_WIN32) || defined(_WIN64)
  #include <intrin.h>
  #define hawtjni_w_barrier() _mm_sfence(); _WriteBarrier()
#else
  #pragma message ( "Don't know how to do a memory barrier on this platform" )
  #define hawtjni_w_barrier()
#endif


#ifdef __cplusplus
}
#endif 

#endif /* ifndef INC_HAWTJNI_H */
