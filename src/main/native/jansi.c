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
#include "jansi_structs.h"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
  return JNI_VERSION_1_2;
}

#define CLibrary_NATIVE(func) Java_org_fusesource_jansi_internal_CLibrary_##func

JNIEXPORT void JNICALL CLibrary_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(HAVE_ISATTY)
	(*env)->SetStaticBooleanField(env, that, (*env)->GetStaticFieldID(env, that, "HAVE_ISATTY", "Z"), (jboolean)1);
#endif
#if defined(HAVE_TTYNAME)
	(*env)->SetStaticBooleanField(env, that, (*env)->GetStaticFieldID(env, that, "HAVE_TTYNAME", "Z"), (jboolean)1);
#endif
#if defined(TCSANOW)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "TCSANOW", "I"), (jint)TCSANOW);
#endif
#if defined(TCSADRAIN)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "TCSADRAIN", "I"), (jint)TCSADRAIN);
#endif
#if defined(TCSAFLUSH)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "TCSAFLUSH", "I"), (jint)TCSAFLUSH);
#endif
#if defined(TIOCGETA)
	(*env)->SetStaticLongField(env, that, (*env)->GetStaticFieldID(env, that, "TIOCGETA", "J"), (jlong)TIOCGETA);
#endif
#if defined(TIOCSETA)
	(*env)->SetStaticLongField(env, that, (*env)->GetStaticFieldID(env, that, "TIOCSETA", "J"), (jlong)TIOCSETA);
#endif
#if defined(TIOCGETD)
	(*env)->SetStaticLongField(env, that, (*env)->GetStaticFieldID(env, that, "TIOCGETD", "J"), (jlong)TIOCGETD);
#endif
#if defined(TIOCSETD)
	(*env)->SetStaticLongField(env, that, (*env)->GetStaticFieldID(env, that, "TIOCSETD", "J"), (jlong)TIOCSETD);
#endif
#if defined(TIOCGWINSZ)
	(*env)->SetStaticLongField(env, that, (*env)->GetStaticFieldID(env, that, "TIOCGWINSZ", "J"), (jlong)TIOCGWINSZ);
#endif
#if defined(TIOCSWINSZ)
	(*env)->SetStaticLongField(env, that, (*env)->GetStaticFieldID(env, that, "TIOCSWINSZ", "J"), (jlong)TIOCSWINSZ);
#endif
   return;
}

#if defined(HAVE_IOCTL)
JNIEXPORT jint JNICALL CLibrary_NATIVE(ioctl__IJLorg_fusesource_jansi_internal_CLibrary_00024WinSize_2)
	(JNIEnv *env, jclass that, jint arg0, jlong arg1, jobject arg2)
{
	struct winsize _arg2, *lparg2=NULL;
	jint rc = 0;

	if (arg2) if ((lparg2 = getWinSizeFields(env, arg2, &_arg2)) == NULL) goto fail;
	rc = (jint)ioctl(arg0, arg1, (intptr_t)lparg2);
fail:
	if (arg2 && lparg2) setWinSizeFields(env, arg2, lparg2);

	return rc;
}

JNIEXPORT jint JNICALL CLibrary_NATIVE(ioctl__IJ_3I)
	(JNIEnv *env, jclass that, jint arg0, jlong arg1, jintArray arg2)
{
	jint *lparg2=NULL;
	jint rc = 0;

	if (arg2) if ((lparg2 = (*env)->GetIntArrayElements(env, arg2, NULL)) == NULL) goto fail;
	rc = (jint)ioctl(arg0, arg1, lparg2);
fail:
	if (arg2 && lparg2) (*env)->ReleaseIntArrayElements(env, arg2, lparg2, 0);

	return rc;
}
#endif

#if defined(HAVE_OPENPTY)
JNIEXPORT jint JNICALL CLibrary_NATIVE(openpty)
	(JNIEnv *env, jclass that, jintArray arg0, jintArray arg1, jbyteArray arg2, jobject arg3, jobject arg4)
{
	jint *lparg0=NULL;
	jint *lparg1=NULL;
	jbyte *lparg2=NULL;
	struct termios _arg3, *lparg3=NULL;
	struct winsize _arg4, *lparg4=NULL;
	jint rc = 0;

	if (arg0) if ((lparg0 = (*env)->GetIntArrayElements(env, arg0, NULL)) == NULL) goto fail;
	if (arg1) if ((lparg1 = (*env)->GetIntArrayElements(env, arg1, NULL)) == NULL) goto fail;
	if (arg2) if ((lparg2 = (*env)->GetByteArrayElements(env, arg2, NULL)) == NULL) goto fail;
	if (arg3) if ((lparg3 = getTermiosFields(env, arg3, &_arg3)) == NULL) goto fail;
	if (arg4) if ((lparg4 = getWinSizeFields(env, arg4, &_arg4)) == NULL) goto fail;
	rc = (jint)openpty((int *)lparg0, (int *)lparg1, (char *)lparg2, (struct termios *)lparg3, (struct winsize *)lparg4);
fail:
	if (arg2 && lparg2) (*env)->ReleaseByteArrayElements(env, arg2, lparg2, 0);
	if (arg1 && lparg1) (*env)->ReleaseIntArrayElements(env, arg1, lparg1, 0);
	if (arg0 && lparg0) (*env)->ReleaseIntArrayElements(env, arg0, lparg0, 0);

	return rc;
}
#endif

#if defined(HAVE_TCGETATTR)
JNIEXPORT jint JNICALL CLibrary_NATIVE(tcgetattr)
	(JNIEnv *env, jclass that, jint arg0, jobject arg1)
{
	struct termios _arg1, *lparg1=NULL;
	jint rc = 0;

	if (arg1) if ((lparg1 = &_arg1) == NULL) goto fail;
	rc = (jint)tcgetattr(arg0, (struct termios *)lparg1);
fail:
	if (arg1 && lparg1) setTermiosFields(env, arg1, lparg1);

	return rc;
}
#endif

#if defined(HAVE_TCSETATTR)
JNIEXPORT jint JNICALL CLibrary_NATIVE(tcsetattr)
	(JNIEnv *env, jclass that, jint arg0, jint arg1, jobject arg2)
{
	struct termios _arg2, *lparg2=NULL;
	jint rc = 0;

	if (arg2) if ((lparg2 = getTermiosFields(env, arg2, &_arg2)) == NULL) goto fail;
	rc = (jint)tcsetattr(arg0, arg1, (struct termios *)lparg2);
fail:

	return rc;
}
#endif

#define Termios_NATIVE(func) Java_org_fusesource_jansi_internal_CLibrary_00024Termios_##func

JNIEXPORT void JNICALL Termios_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(HAVE_IOCTL)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(struct termios));
#endif
   return;
}

#define WinSize_NATIVE(func) Java_org_fusesource_jansi_internal_CLibrary_00024WinSize_##func

JNIEXPORT void JNICALL WinSize_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(HAVE_IOCTL)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(struct winsize));
#endif
   return;
}
#define Kernel32_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_##func

#if defined(_WIN32) || defined(_WIN64)
JNIEXPORT jint JNICALL Kernel32_NATIVE(CloseHandle)
	(JNIEnv *env, jclass that, jlong arg0)
{
	jint rc = 0;
	rc = (jint)CloseHandle((HANDLE)arg0);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(FillConsoleOutputAttribute)
	(JNIEnv *env, jclass that, jlong arg0, jshort arg1, jint arg2, jobject arg3, jintArray arg4)
{
	COORD _arg3, *lparg3=NULL;
	jint *lparg4=NULL;
	jint rc = 0;
	if (arg3) if ((lparg3 = getCOORDFields(env, arg3, &_arg3)) == NULL) goto fail;
	if (arg4) if ((lparg4 = (*env)->GetIntArrayElements(env, arg4, NULL)) == NULL) goto fail;
	rc = (jint)FillConsoleOutputAttribute((HANDLE)(intptr_t)arg0, arg1, arg2, *lparg3, lparg4);
fail:
	if (arg4 && lparg4) (*env)->ReleaseIntArrayElements(env, arg4, lparg4, 0);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(FillConsoleOutputCharacterW)
	(JNIEnv *env, jclass that, jlong arg0, jchar arg1, jint arg2, jobject arg3, jintArray arg4)
{
	COORD _arg3, *lparg3=NULL;
	jint *lparg4=NULL;
	jint rc = 0;
	if (arg3) if ((lparg3 = getCOORDFields(env, arg3, &_arg3)) == NULL) goto fail;
	if (arg4) if ((lparg4 = (*env)->GetIntArrayElements(env, arg4, NULL)) == NULL) goto fail;
	rc = (jint)FillConsoleOutputCharacterW((HANDLE)(intptr_t)arg0, arg1, arg2, *lparg3, lparg4);
fail:
	if (arg4 && lparg4) (*env)->ReleaseIntArrayElements(env, arg4, lparg4, 0);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(FlushConsoleInputBuffer)
	(JNIEnv *env, jclass that, jlong arg0)
{
	jint rc = 0;
	rc = (jint)FlushConsoleInputBuffer((HANDLE)(intptr_t)arg0);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(FormatMessageW)
	(JNIEnv *env, jclass that, jint arg0, jlong arg1, jint arg2, jint arg3, jbyteArray arg4, jint arg5, jlongArray arg6)
{
	jbyte *lparg4=NULL;
	jlong *lparg6=NULL;
	jint rc = 0;
    if (arg4) if ((lparg4 = (*env)->GetPrimitiveArrayCritical(env, arg4, NULL)) == NULL) goto fail;
    if (arg6) if ((lparg6 = (*env)->GetPrimitiveArrayCritical(env, arg6, NULL)) == NULL) goto fail;
	rc = (jint)FormatMessageW(arg0, (void *)(intptr_t)arg1, arg2, arg3, (void *)lparg4, arg5, (void *)NULL);
fail:
    if (arg6 && lparg6) (*env)->ReleasePrimitiveArrayCritical(env, arg6, lparg6, 0);
    if (arg4 && lparg4) (*env)->ReleasePrimitiveArrayCritical(env, arg4, lparg4, 0);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(GetConsoleMode)
	(JNIEnv *env, jclass that, jlong arg0, jintArray arg1)
{
	jint *lparg1=NULL;
	jint rc = 0;
	if (arg1) if ((lparg1 = (*env)->GetIntArrayElements(env, arg1, NULL)) == NULL) goto fail;
	rc = (jint)GetConsoleMode((HANDLE)(intptr_t)arg0, lparg1);
fail:
	if (arg1 && lparg1) (*env)->ReleaseIntArrayElements(env, arg1, lparg1, 0);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(GetConsoleOutputCP)
	(JNIEnv *env, jclass that)
{
	jint rc = 0;
	rc = (jint)GetConsoleOutputCP();
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(GetConsoleScreenBufferInfo)
	(JNIEnv *env, jclass that, jlong arg0, jobject arg1)
{
	CONSOLE_SCREEN_BUFFER_INFO _arg1, *lparg1=NULL;
	jint rc = 0;
	if (arg1) if ((lparg1 = &_arg1) == NULL) goto fail;
	rc = (jint)GetConsoleScreenBufferInfo((HANDLE)(intptr_t)arg0, lparg1);
fail:
	if (arg1 && lparg1) setCONSOLE_SCREEN_BUFFER_INFOFields(env, arg1, lparg1);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(GetLastError)
	(JNIEnv *env, jclass that)
{
	jint rc = 0;
	rc = (jint)GetLastError();
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(GetNumberOfConsoleInputEvents)
	(JNIEnv *env, jclass that, jlong arg0, jintArray arg1)
{
	jint *lparg1=NULL;
	jint rc = 0;
	if (arg1) if ((lparg1 = (*env)->GetIntArrayElements(env, arg1, NULL)) == NULL) goto fail;
	rc = (jint)GetNumberOfConsoleInputEvents((HANDLE)(intptr_t)arg0, lparg1);
fail:
	if (arg1 && lparg1) (*env)->ReleaseIntArrayElements(env, arg1, lparg1, 0);
	return rc;
}

JNIEXPORT jlong JNICALL Kernel32_NATIVE(GetStdHandle)
	(JNIEnv *env, jclass that, jint arg0)
{
	jlong rc = 0;
	rc = (intptr_t)(HANDLE)GetStdHandle(arg0);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(PeekConsoleInputW)
	(JNIEnv *env, jclass that, jlong arg0, jlong arg1, jint arg2, jintArray arg3)
{
	jint *lparg3=NULL;
	jint rc = 0;
	if (arg3) if ((lparg3 = (*env)->GetIntArrayElements(env, arg3, NULL)) == NULL) goto fail;
	rc = (jint)PeekConsoleInputW((HANDLE)(intptr_t)arg0, (PINPUT_RECORD)(intptr_t)arg1, arg2, lparg3);
fail:
	if (arg3 && lparg3) (*env)->ReleaseIntArrayElements(env, arg3, lparg3, 0);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(ReadConsoleInputW)
	(JNIEnv *env, jclass that, jlong arg0, jlong arg1, jint arg2, jintArray arg3)
{
	jint *lparg3=NULL;
	jint rc = 0;
	if (arg3) if ((lparg3 = (*env)->GetIntArrayElements(env, arg3, NULL)) == NULL) goto fail;
	rc = (jint)ReadConsoleInputW((HANDLE)(intptr_t)arg0, (PINPUT_RECORD)(intptr_t)arg1, arg2, lparg3);
fail:
	if (arg3 && lparg3) (*env)->ReleaseIntArrayElements(env, arg3, lparg3, 0);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(ScrollConsoleScreenBuffer)
	(JNIEnv *env, jclass that, jlong arg0, jobject arg1, jobject arg2, jobject arg3, jobject arg4)
{
	SMALL_RECT _arg1, *lparg1=NULL;
	SMALL_RECT _arg2, *lparg2=NULL;
	COORD _arg3, *lparg3=NULL;
	CHAR_INFO _arg4, *lparg4=NULL;
	jint rc = 0;
	if (arg1) if ((lparg1 = getSMALL_RECTFields(env, arg1, &_arg1)) == NULL) goto fail;
	if (arg2) if ((lparg2 = getSMALL_RECTFields(env, arg2, &_arg2)) == NULL) goto fail;
	if (arg3) if ((lparg3 = getCOORDFields(env, arg3, &_arg3)) == NULL) goto fail;
	if (arg4) if ((lparg4 = getCHAR_INFOFields(env, arg4, &_arg4)) == NULL) goto fail;
	rc = (jint)ScrollConsoleScreenBuffer((HANDLE)(intptr_t)arg0, lparg1, lparg2, *lparg3, lparg4);
fail:
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(SetConsoleCursorPosition)
	(JNIEnv *env, jclass that, jlong arg0, jobject arg1)
{
	COORD _arg1, *lparg1=NULL;
	jint rc = 0;
	if (arg1) if ((lparg1 = getCOORDFields(env, arg1, &_arg1)) == NULL) goto fail;
	rc = (jint)SetConsoleCursorPosition((HANDLE)(intptr_t)arg0, *lparg1);
fail:
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(SetConsoleMode)
	(JNIEnv *env, jclass that, jlong arg0, jint arg1)
{
	return (jint)SetConsoleMode((HANDLE)(intptr_t)arg0, arg1);
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(SetConsoleOutputCP)
	(JNIEnv *env, jclass that, jint arg0)
{
	return (jint)SetConsoleOutputCP(arg0);
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(SetConsoleTextAttribute)
	(JNIEnv *env, jclass that, jlong arg0, jshort arg1)
{
	return (jint)SetConsoleTextAttribute((HANDLE)arg0, arg1);
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(SetConsoleTitle)
	(JNIEnv *env, jclass that, jstring arg0)
{
	const jchar *lparg0= NULL;
	jint rc = 0;
	if (arg0) if ((lparg0 = (*env)->GetStringChars(env, arg0, NULL)) == NULL) goto fail;
	rc = (jint)SetConsoleTitle(lparg0);
fail:
	if (arg0 && lparg0) (*env)->ReleaseStringChars(env, arg0, lparg0);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(WaitForSingleObject)
	(JNIEnv *env, jclass that, jlong arg0, jint arg1)
{
	return (jint)WaitForSingleObject((HANDLE)arg0, arg1);
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(WriteConsoleW)
	(JNIEnv *env, jclass that, jlong arg0, jcharArray arg1, jint arg2, jintArray arg3, jlong arg4)
{
	jchar *lparg1=NULL;
	jint *lparg3=NULL;
	jint rc = 0;
	if (arg1) if ((lparg1 = (*env)->GetCharArrayElements(env, arg1, NULL)) == NULL) goto fail;
	if (arg3) if ((lparg3 = (*env)->GetIntArrayElements(env, arg3, NULL)) == NULL) goto fail;
	rc = (jint)WriteConsoleW((HANDLE)(intptr_t)arg0, lparg1, arg2, lparg3, (LPVOID)(intptr_t)arg4);
fail:
	if (arg3 && lparg3) (*env)->ReleaseIntArrayElements(env, arg3, lparg3, 0);
	if (arg1 && lparg1) (*env)->ReleaseCharArrayElements(env, arg1, lparg1, JNI_ABORT);
	return rc;
}

JNIEXPORT jint JNICALL Kernel32_NATIVE(_1getch)
	(JNIEnv *env, jclass that)
{
	jint rc = 0;
	rc = (jint)_getch();
	return rc;
}

JNIEXPORT void JNICALL Kernel32_NATIVE(free)
	(JNIEnv *env, jclass that, jlong arg0)
{
	free((void *)(intptr_t)arg0);
}
#endif

JNIEXPORT void JNICALL Kernel32_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "FOREGROUND_BLUE", "S"), (jshort)FOREGROUND_BLUE);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "FOREGROUND_GREEN", "S"), (jshort)FOREGROUND_GREEN);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "FOREGROUND_RED", "S"), (jshort)FOREGROUND_RED);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "FOREGROUND_INTENSITY", "S"), (jshort)FOREGROUND_INTENSITY);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "BACKGROUND_BLUE", "S"), (jshort)BACKGROUND_BLUE);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "BACKGROUND_GREEN", "S"), (jshort)BACKGROUND_GREEN);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "BACKGROUND_RED", "S"), (jshort)BACKGROUND_RED);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "BACKGROUND_INTENSITY", "S"), (jshort)BACKGROUND_INTENSITY);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "COMMON_LVB_LEADING_BYTE", "S"), (jshort)COMMON_LVB_LEADING_BYTE);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "COMMON_LVB_TRAILING_BYTE", "S"), (jshort)COMMON_LVB_TRAILING_BYTE);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "COMMON_LVB_GRID_HORIZONTAL", "S"), (jshort)COMMON_LVB_GRID_HORIZONTAL);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "COMMON_LVB_GRID_LVERTICAL", "S"), (jshort)COMMON_LVB_GRID_LVERTICAL);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "COMMON_LVB_GRID_RVERTICAL", "S"), (jshort)COMMON_LVB_GRID_RVERTICAL);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "COMMON_LVB_REVERSE_VIDEO", "S"), (jshort)COMMON_LVB_REVERSE_VIDEO);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "COMMON_LVB_UNDERSCORE", "S"), (jshort)COMMON_LVB_UNDERSCORE);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "FORMAT_MESSAGE_FROM_SYSTEM", "I"), (jint)FORMAT_MESSAGE_FROM_SYSTEM);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "STD_INPUT_HANDLE", "I"), (jint)STD_INPUT_HANDLE);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "STD_OUTPUT_HANDLE", "I"), (jint)STD_OUTPUT_HANDLE);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "STD_ERROR_HANDLE", "I"), (jint)STD_ERROR_HANDLE);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "INVALID_HANDLE_VALUE", "I"), (jint)INVALID_HANDLE_VALUE);
#endif
   return;
}

#if defined(_WIN32) || defined(_WIN64)
JNIEXPORT jlong JNICALL Kernel32_NATIVE(malloc)
	(JNIEnv *env, jclass that, jlong arg0)
{
	jlong rc = 0;
	rc = (intptr_t)(void *)malloc((size_t)arg0);
	return rc;
}
#endif

#define CHAR_INFO_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_00024CHAR_1INFO_##func

JNIEXPORT void JNICALL CHAR_INFO_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(CHAR_INFO));
#endif
   return;
}
#define CONSOLE_SCREEN_BUFFER_INFO_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_00024CONSOLE_1SCREEN_1BUFFER_1INFO_##func

JNIEXPORT void JNICALL CONSOLE_SCREEN_BUFFER_INFO_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(CONSOLE_SCREEN_BUFFER_INFO));
#endif
   return;
}
#define COORD_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_00024COORD_##func

JNIEXPORT void JNICALL COORD_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(COORD));
#endif
   return;
}
#define FOCUS_EVENT_RECORD_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_00024FOCUS_1EVENT_1RECORD_##func

JNIEXPORT void JNICALL FOCUS_EVENT_RECORD_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(WINDOW_BUFFER_SIZE_RECORD));
#endif
   return;
}
#define INPUT_RECORD_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_00024INPUT_1RECORD_##func

JNIEXPORT void JNICALL INPUT_RECORD_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(INPUT_RECORD));
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "KEY_EVENT", "S"), (jshort)KEY_EVENT);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "MOUSE_EVENT", "S"), (jshort)MOUSE_EVENT);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "WINDOW_BUFFER_SIZE_EVENT", "S"), (jshort)WINDOW_BUFFER_SIZE_EVENT);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "FOCUS_EVENT", "S"), (jshort)FOCUS_EVENT);
	(*env)->SetStaticShortField(env, that, (*env)->GetStaticFieldID(env, that, "MENU_EVENT", "S"), (jshort)MENU_EVENT);
#endif
   return;
}
#if defined(_WIN32) || defined(_WIN64)
JNIEXPORT void JNICALL INPUT_RECORD_NATIVE(memmove)
	(JNIEnv *env, jclass that, jobject arg0, jlong arg1, jlong arg2)
{
	INPUT_RECORD _arg0, *lparg0=NULL;
	if (arg0) if ((lparg0 = &_arg0) == NULL) goto fail;
	memmove((void *)lparg0, (const void *)(intptr_t)arg1, (size_t)arg2);
fail:
	if (arg0 && lparg0) setINPUT_RECORDFields(env, arg0, lparg0);
}
#endif

#define KEY_EVENT_RECORD_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_00024KEY_1EVENT_1RECORD_##func

JNIEXPORT void JNICALL KEY_EVENT_RECORD_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(KEY_EVENT_RECORD));
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "CAPSLOCK_ON", "I"), (jint)CAPSLOCK_ON);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "NUMLOCK_ON", "I"), (jint)NUMLOCK_ON);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SCROLLLOCK_ON", "I"), (jint)SCROLLLOCK_ON);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "ENHANCED_KEY", "I"), (jint)ENHANCED_KEY);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "LEFT_ALT_PRESSED", "I"), (jint)LEFT_ALT_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "LEFT_CTRL_PRESSED", "I"), (jint)LEFT_CTRL_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "RIGHT_ALT_PRESSED", "I"), (jint)RIGHT_ALT_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "RIGHT_CTRL_PRESSED", "I"), (jint)RIGHT_CTRL_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SHIFT_PRESSED", "I"), (jint)SHIFT_PRESSED);
#endif
   return;
}
#define MENU_EVENT_RECORD_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_00024MENU_1EVENT_1RECORD_##func

JNIEXPORT void JNICALL MENU_EVENT_RECORD_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(MENU_EVENT_RECORD));
#endif
   return;
}
#define MOUSE_EVENT_RECORD_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_00024MOUSE_1EVENT_1RECORD_##func

JNIEXPORT void JNICALL MOUSE_EVENT_RECORD_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(MOUSE_EVENT_RECORD));
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "FROM_LEFT_1ST_BUTTON_PRESSED", "I"), (jint)FROM_LEFT_1ST_BUTTON_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "FROM_LEFT_2ND_BUTTON_PRESSED", "I"), (jint)FROM_LEFT_2ND_BUTTON_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "FROM_LEFT_3RD_BUTTON_PRESSED", "I"), (jint)FROM_LEFT_3RD_BUTTON_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "FROM_LEFT_4TH_BUTTON_PRESSED", "I"), (jint)FROM_LEFT_4TH_BUTTON_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "RIGHTMOST_BUTTON_PRESSED", "I"), (jint)RIGHTMOST_BUTTON_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "CAPSLOCK_ON", "I"), (jint)CAPSLOCK_ON);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "NUMLOCK_ON", "I"), (jint)NUMLOCK_ON);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SCROLLLOCK_ON", "I"), (jint)SCROLLLOCK_ON);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "ENHANCED_KEY", "I"), (jint)ENHANCED_KEY);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "LEFT_ALT_PRESSED", "I"), (jint)LEFT_ALT_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "LEFT_CTRL_PRESSED", "I"), (jint)LEFT_CTRL_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "RIGHT_ALT_PRESSED", "I"), (jint)RIGHT_ALT_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "RIGHT_CTRL_PRESSED", "I"), (jint)RIGHT_CTRL_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SHIFT_PRESSED", "I"), (jint)SHIFT_PRESSED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "DOUBLE_CLICK", "I"), (jint)DOUBLE_CLICK);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "MOUSE_HWHEELED", "I"), (jint)MOUSE_HWHEELED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "MOUSE_MOVED", "I"), (jint)MOUSE_MOVED);
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "MOUSE_WHEELED", "I"), (jint)MOUSE_WHEELED);
#endif
   return;
}
#define SMALL_RECT_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_00024SMALL_1RECT_##func

JNIEXPORT void JNICALL SMALL_RECT_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(SMALL_RECT));
#endif
   return;
}
#define WINDOW_BUFFER_SIZE_RECORD_NATIVE(func) Java_org_fusesource_jansi_internal_Kernel32_00024WINDOW_1BUFFER_1SIZE_1RECORD_##func

JNIEXPORT void JNICALL WINDOW_BUFFER_SIZE_RECORD_NATIVE(init)(JNIEnv *env, jclass that)
{
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetStaticIntField(env, that, (*env)->GetStaticFieldID(env, that, "SIZEOF", "I"), (jint)sizeof(WINDOW_BUFFER_SIZE_RECORD));
#endif
   return;
}
