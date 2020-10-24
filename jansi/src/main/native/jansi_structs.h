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
#include "jansi.h"

#if defined(HAVE_IOCTL)
void cacheTermiosFields(JNIEnv *env, jobject lpObject);
struct termios *getTermiosFields(JNIEnv *env, jobject lpObject, struct termios *lpStruct);
void setTermiosFields(JNIEnv *env, jobject lpObject, struct termios *lpStruct);
#else
#define cacheTermiosFields(a,b)
#define getTermiosFields(a,b,c) NULL
#define setTermiosFields(a,b,c)
#endif

#if defined(HAVE_IOCTL)
void cacheWinSizeFields(JNIEnv *env, jobject lpObject);
struct winsize *getWinSizeFields(JNIEnv *env, jobject lpObject, struct winsize *lpStruct);
void setWinSizeFields(JNIEnv *env, jobject lpObject, struct winsize *lpStruct);
#else
#define cacheWinSizeFields(a,b)
#define getWinSizeFields(a,b,c) NULL
#define setWinSizeFields(a,b,c)
#endif

#if defined(_WIN32) || defined(_WIN64)
void cacheCHAR_INFOFields(JNIEnv *env, jobject lpObject);
CHAR_INFO *getCHAR_INFOFields(JNIEnv *env, jobject lpObject, CHAR_INFO *lpStruct);
void setCHAR_INFOFields(JNIEnv *env, jobject lpObject, CHAR_INFO *lpStruct);
#else
#define cacheCHAR_INFOFields(a,b)
#define getCHAR_INFOFields(a,b,c) NULL
#define setCHAR_INFOFields(a,b,c)
#endif

#if defined(_WIN32) || defined(_WIN64)
void cacheCONSOLE_SCREEN_BUFFER_INFOFields(JNIEnv *env, jobject lpObject);
CONSOLE_SCREEN_BUFFER_INFO *getCONSOLE_SCREEN_BUFFER_INFOFields(JNIEnv *env, jobject lpObject, CONSOLE_SCREEN_BUFFER_INFO *lpStruct);
void setCONSOLE_SCREEN_BUFFER_INFOFields(JNIEnv *env, jobject lpObject, CONSOLE_SCREEN_BUFFER_INFO *lpStruct);
#else
#define cacheCONSOLE_SCREEN_BUFFER_INFOFields(a,b)
#define getCONSOLE_SCREEN_BUFFER_INFOFields(a,b,c) NULL
#define setCONSOLE_SCREEN_BUFFER_INFOFields(a,b,c)
#endif

#if defined(_WIN32) || defined(_WIN64)
void cacheCOORDFields(JNIEnv *env, jobject lpObject);
COORD *getCOORDFields(JNIEnv *env, jobject lpObject, COORD *lpStruct);
void setCOORDFields(JNIEnv *env, jobject lpObject, COORD *lpStruct);
#else
#define cacheCOORDFields(a,b)
#define getCOORDFields(a,b,c) NULL
#define setCOORDFields(a,b,c)
#endif

#if defined(_WIN32) || defined(_WIN64)
void cacheFOCUS_EVENT_RECORDFields(JNIEnv *env, jobject lpObject);
FOCUS_EVENT_RECORD *getFOCUS_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, FOCUS_EVENT_RECORD *lpStruct);
void setFOCUS_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, FOCUS_EVENT_RECORD *lpStruct);
#else
#define cacheFOCUS_EVENT_RECORDFields(a,b)
#define getFOCUS_EVENT_RECORDFields(a,b,c) NULL
#define setFOCUS_EVENT_RECORDFields(a,b,c)
#endif

#if defined(_WIN32) || defined(_WIN64)
void cacheINPUT_RECORDFields(JNIEnv *env, jobject lpObject);
INPUT_RECORD *getINPUT_RECORDFields(JNIEnv *env, jobject lpObject, INPUT_RECORD *lpStruct);
void setINPUT_RECORDFields(JNIEnv *env, jobject lpObject, INPUT_RECORD *lpStruct);
#else
#define cacheINPUT_RECORDFields(a,b)
#define getINPUT_RECORDFields(a,b,c) NULL
#define setINPUT_RECORDFields(a,b,c)
#endif

#if defined(_WIN32) || defined(_WIN64)
void cacheKEY_EVENT_RECORDFields(JNIEnv *env, jobject lpObject);
KEY_EVENT_RECORD *getKEY_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, KEY_EVENT_RECORD *lpStruct);
void setKEY_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, KEY_EVENT_RECORD *lpStruct);
#else
#define cacheKEY_EVENT_RECORDFields(a,b)
#define getKEY_EVENT_RECORDFields(a,b,c) NULL
#define setKEY_EVENT_RECORDFields(a,b,c)
#endif

#if defined(_WIN32) || defined(_WIN64)
void cacheMENU_EVENT_RECORDFields(JNIEnv *env, jobject lpObject);
MENU_EVENT_RECORD *getMENU_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, MENU_EVENT_RECORD *lpStruct);
void setMENU_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, MENU_EVENT_RECORD *lpStruct);
#else
#define cacheMENU_EVENT_RECORDFields(a,b)
#define getMENU_EVENT_RECORDFields(a,b,c) NULL
#define setMENU_EVENT_RECORDFields(a,b,c)
#endif

#if defined(_WIN32) || defined(_WIN64)
void cacheMOUSE_EVENT_RECORDFields(JNIEnv *env, jobject lpObject);
MOUSE_EVENT_RECORD *getMOUSE_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, MOUSE_EVENT_RECORD *lpStruct);
void setMOUSE_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, MOUSE_EVENT_RECORD *lpStruct);
#else
#define cacheMOUSE_EVENT_RECORDFields(a,b)
#define getMOUSE_EVENT_RECORDFields(a,b,c) NULL
#define setMOUSE_EVENT_RECORDFields(a,b,c)
#endif

#if defined(_WIN32) || defined(_WIN64)
void cacheSMALL_RECTFields(JNIEnv *env, jobject lpObject);
SMALL_RECT *getSMALL_RECTFields(JNIEnv *env, jobject lpObject, SMALL_RECT *lpStruct);
void setSMALL_RECTFields(JNIEnv *env, jobject lpObject, SMALL_RECT *lpStruct);
#else
#define cacheSMALL_RECTFields(a,b)
#define getSMALL_RECTFields(a,b,c) NULL
#define setSMALL_RECTFields(a,b,c)
#endif

#if defined(_WIN32) || defined(_WIN64)
void cacheWINDOW_BUFFER_SIZE_RECORDFields(JNIEnv *env, jobject lpObject);
WINDOW_BUFFER_SIZE_RECORD *getWINDOW_BUFFER_SIZE_RECORDFields(JNIEnv *env, jobject lpObject, WINDOW_BUFFER_SIZE_RECORD *lpStruct);
void setWINDOW_BUFFER_SIZE_RECORDFields(JNIEnv *env, jobject lpObject, WINDOW_BUFFER_SIZE_RECORD *lpStruct);
#else
#define cacheWINDOW_BUFFER_SIZE_RECORDFields(a,b)
#define getWINDOW_BUFFER_SIZE_RECORDFields(a,b,c) NULL
#define setWINDOW_BUFFER_SIZE_RECORDFields(a,b,c)
#endif

