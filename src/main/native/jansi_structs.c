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

#if defined(HAVE_IOCTL)
typedef struct Termios_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID c_iflag, c_oflag, c_cflag, c_lflag, c_cc, c_ispeed, c_ospeed;
} Termios_FID_CACHE;

Termios_FID_CACHE TermiosFc;

void cacheTermiosFields(JNIEnv *env, jobject lpObject)
{
	if (TermiosFc.cached) return;
	TermiosFc.clazz = (*env)->GetObjectClass(env, lpObject);
	TermiosFc.c_iflag = (*env)->GetFieldID(env, TermiosFc.clazz, "c_iflag", "J");
	TermiosFc.c_oflag = (*env)->GetFieldID(env, TermiosFc.clazz, "c_oflag", "J");
	TermiosFc.c_cflag = (*env)->GetFieldID(env, TermiosFc.clazz, "c_cflag", "J");
	TermiosFc.c_lflag = (*env)->GetFieldID(env, TermiosFc.clazz, "c_lflag", "J");
	TermiosFc.c_cc = (*env)->GetFieldID(env, TermiosFc.clazz, "c_cc", "[B");
	TermiosFc.c_ispeed = (*env)->GetFieldID(env, TermiosFc.clazz, "c_ispeed", "J");
	TermiosFc.c_ospeed = (*env)->GetFieldID(env, TermiosFc.clazz, "c_ospeed", "J");
	hawtjni_w_barrier();
	TermiosFc.cached = 1;
}

struct termios *getTermiosFields(JNIEnv *env, jobject lpObject, struct termios *lpStruct)
{
	if (!TermiosFc.cached) cacheTermiosFields(env, lpObject);
#if defined(HAVE_IOCTL)
	lpStruct->c_iflag = (*env)->GetLongField(env, lpObject, TermiosFc.c_iflag);
#endif
#if defined(HAVE_IOCTL)
	lpStruct->c_oflag = (*env)->GetLongField(env, lpObject, TermiosFc.c_oflag);
#endif
#if defined(HAVE_IOCTL)
	lpStruct->c_cflag = (*env)->GetLongField(env, lpObject, TermiosFc.c_cflag);
#endif
#if defined(HAVE_IOCTL)
	lpStruct->c_lflag = (*env)->GetLongField(env, lpObject, TermiosFc.c_lflag);
#endif
#if defined(HAVE_IOCTL)
	{
	jbyteArray lpObject1 = (jbyteArray)(*env)->GetObjectField(env, lpObject, TermiosFc.c_cc);
	(*env)->GetByteArrayRegion(env, lpObject1, 0, sizeof(lpStruct->c_cc), (jbyte *)lpStruct->c_cc);
	}
#endif
#if defined(HAVE_IOCTL)
	lpStruct->c_ispeed = (*env)->GetLongField(env, lpObject, TermiosFc.c_ispeed);
#endif
#if defined(HAVE_IOCTL)
	lpStruct->c_ospeed = (*env)->GetLongField(env, lpObject, TermiosFc.c_ospeed);
#endif
	return lpStruct;
}

void setTermiosFields(JNIEnv *env, jobject lpObject, struct termios *lpStruct)
{
	if (!TermiosFc.cached) cacheTermiosFields(env, lpObject);
#if defined(HAVE_IOCTL)
	(*env)->SetLongField(env, lpObject, TermiosFc.c_iflag, (jlong)lpStruct->c_iflag);
#endif
#if defined(HAVE_IOCTL)
	(*env)->SetLongField(env, lpObject, TermiosFc.c_oflag, (jlong)lpStruct->c_oflag);
#endif
#if defined(HAVE_IOCTL)
	(*env)->SetLongField(env, lpObject, TermiosFc.c_cflag, (jlong)lpStruct->c_cflag);
#endif
#if defined(HAVE_IOCTL)
	(*env)->SetLongField(env, lpObject, TermiosFc.c_lflag, (jlong)lpStruct->c_lflag);
#endif
#if defined(HAVE_IOCTL)
	{
	jbyteArray lpObject1 = (jbyteArray)(*env)->GetObjectField(env, lpObject, TermiosFc.c_cc);
	(*env)->SetByteArrayRegion(env, lpObject1, 0, sizeof(lpStruct->c_cc), (jbyte *)lpStruct->c_cc);
	}
#endif
#if defined(HAVE_IOCTL)
	(*env)->SetLongField(env, lpObject, TermiosFc.c_ispeed, (jlong)lpStruct->c_ispeed);
#endif
#if defined(HAVE_IOCTL)
	(*env)->SetLongField(env, lpObject, TermiosFc.c_ospeed, (jlong)lpStruct->c_ospeed);
#endif
}
#endif

#if defined(HAVE_IOCTL)
typedef struct WinSize_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID ws_row, ws_col, ws_xpixel, ws_ypixel;
} WinSize_FID_CACHE;

WinSize_FID_CACHE WinSizeFc;

void cacheWinSizeFields(JNIEnv *env, jobject lpObject)
{
	if (WinSizeFc.cached) return;
	WinSizeFc.clazz = (*env)->GetObjectClass(env, lpObject);
	WinSizeFc.ws_row = (*env)->GetFieldID(env, WinSizeFc.clazz, "ws_row", "S");
	WinSizeFc.ws_col = (*env)->GetFieldID(env, WinSizeFc.clazz, "ws_col", "S");
	WinSizeFc.ws_xpixel = (*env)->GetFieldID(env, WinSizeFc.clazz, "ws_xpixel", "S");
	WinSizeFc.ws_ypixel = (*env)->GetFieldID(env, WinSizeFc.clazz, "ws_ypixel", "S");
	hawtjni_w_barrier();
	WinSizeFc.cached = 1;
}

struct winsize *getWinSizeFields(JNIEnv *env, jobject lpObject, struct winsize *lpStruct)
{
	if (!WinSizeFc.cached) cacheWinSizeFields(env, lpObject);
#if defined(HAVE_IOCTL)
	lpStruct->ws_row = (*env)->GetShortField(env, lpObject, WinSizeFc.ws_row);
#endif
#if defined(HAVE_IOCTL)
	lpStruct->ws_col = (*env)->GetShortField(env, lpObject, WinSizeFc.ws_col);
#endif
#if defined(HAVE_IOCTL)
	lpStruct->ws_xpixel = (*env)->GetShortField(env, lpObject, WinSizeFc.ws_xpixel);
#endif
#if defined(HAVE_IOCTL)
	lpStruct->ws_ypixel = (*env)->GetShortField(env, lpObject, WinSizeFc.ws_ypixel);
#endif
	return lpStruct;
}

void setWinSizeFields(JNIEnv *env, jobject lpObject, struct winsize *lpStruct)
{
	if (!WinSizeFc.cached) cacheWinSizeFields(env, lpObject);
#if defined(HAVE_IOCTL)
	(*env)->SetShortField(env, lpObject, WinSizeFc.ws_row, (jshort)lpStruct->ws_row);
#endif
#if defined(HAVE_IOCTL)
	(*env)->SetShortField(env, lpObject, WinSizeFc.ws_col, (jshort)lpStruct->ws_col);
#endif
#if defined(HAVE_IOCTL)
	(*env)->SetShortField(env, lpObject, WinSizeFc.ws_xpixel, (jshort)lpStruct->ws_xpixel);
#endif
#if defined(HAVE_IOCTL)
	(*env)->SetShortField(env, lpObject, WinSizeFc.ws_ypixel, (jshort)lpStruct->ws_ypixel);
#endif
}
#endif

#if defined(_WIN32) || defined(_WIN64)
typedef struct CHAR_INFO_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID attributes, unicodeChar;
} CHAR_INFO_FID_CACHE;

CHAR_INFO_FID_CACHE CHAR_INFOFc;

void cacheCHAR_INFOFields(JNIEnv *env, jobject lpObject)
{
	if (CHAR_INFOFc.cached) return;
	CHAR_INFOFc.clazz = (*env)->GetObjectClass(env, lpObject);
	CHAR_INFOFc.attributes = (*env)->GetFieldID(env, CHAR_INFOFc.clazz, "attributes", "S");
	CHAR_INFOFc.unicodeChar = (*env)->GetFieldID(env, CHAR_INFOFc.clazz, "unicodeChar", "C");
	hawtjni_w_barrier();
	CHAR_INFOFc.cached = 1;
}

CHAR_INFO *getCHAR_INFOFields(JNIEnv *env, jobject lpObject, CHAR_INFO *lpStruct)
{
	if (!CHAR_INFOFc.cached) cacheCHAR_INFOFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	lpStruct->Attributes = (*env)->GetShortField(env, lpObject, CHAR_INFOFc.attributes);
#endif
#if defined(_WIN32) || defined(_WIN64)
	lpStruct->Char.UnicodeChar = (*env)->GetCharField(env, lpObject, CHAR_INFOFc.unicodeChar);
#endif
	return lpStruct;
}

void setCHAR_INFOFields(JNIEnv *env, jobject lpObject, CHAR_INFO *lpStruct)
{
	if (!CHAR_INFOFc.cached) cacheCHAR_INFOFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetShortField(env, lpObject, CHAR_INFOFc.attributes, (jshort)lpStruct->Attributes);
#endif
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetCharField(env, lpObject, CHAR_INFOFc.unicodeChar, (jchar)lpStruct->Char.UnicodeChar);
#endif
}
#endif

#if defined(_WIN32) || defined(_WIN64)
typedef struct CONSOLE_SCREEN_BUFFER_INFO_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID size, cursorPosition, attributes, window, maximumWindowSize;
} CONSOLE_SCREEN_BUFFER_INFO_FID_CACHE;

CONSOLE_SCREEN_BUFFER_INFO_FID_CACHE CONSOLE_SCREEN_BUFFER_INFOFc;

void cacheCONSOLE_SCREEN_BUFFER_INFOFields(JNIEnv *env, jobject lpObject)
{
	if (CONSOLE_SCREEN_BUFFER_INFOFc.cached) return;
	CONSOLE_SCREEN_BUFFER_INFOFc.clazz = (*env)->GetObjectClass(env, lpObject);
	CONSOLE_SCREEN_BUFFER_INFOFc.size = (*env)->GetFieldID(env, CONSOLE_SCREEN_BUFFER_INFOFc.clazz, "size", "Lorg/fusesource/jansi/internal/Kernel32$COORD;");
	CONSOLE_SCREEN_BUFFER_INFOFc.cursorPosition = (*env)->GetFieldID(env, CONSOLE_SCREEN_BUFFER_INFOFc.clazz, "cursorPosition", "Lorg/fusesource/jansi/internal/Kernel32$COORD;");
	CONSOLE_SCREEN_BUFFER_INFOFc.attributes = (*env)->GetFieldID(env, CONSOLE_SCREEN_BUFFER_INFOFc.clazz, "attributes", "S");
	CONSOLE_SCREEN_BUFFER_INFOFc.window = (*env)->GetFieldID(env, CONSOLE_SCREEN_BUFFER_INFOFc.clazz, "window", "Lorg/fusesource/jansi/internal/Kernel32$SMALL_RECT;");
	CONSOLE_SCREEN_BUFFER_INFOFc.maximumWindowSize = (*env)->GetFieldID(env, CONSOLE_SCREEN_BUFFER_INFOFc.clazz, "maximumWindowSize", "Lorg/fusesource/jansi/internal/Kernel32$COORD;");
	hawtjni_w_barrier();
	CONSOLE_SCREEN_BUFFER_INFOFc.cached = 1;
}

CONSOLE_SCREEN_BUFFER_INFO *getCONSOLE_SCREEN_BUFFER_INFOFields(JNIEnv *env, jobject lpObject, CONSOLE_SCREEN_BUFFER_INFO *lpStruct)
{
	if (!CONSOLE_SCREEN_BUFFER_INFOFc.cached) cacheCONSOLE_SCREEN_BUFFER_INFOFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
		{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, CONSOLE_SCREEN_BUFFER_INFOFc.size);
	if (lpObject1 != NULL) getCOORDFields(env, lpObject1, &lpStruct->dwSize);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
		{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, CONSOLE_SCREEN_BUFFER_INFOFc.cursorPosition);
	if (lpObject1 != NULL) getCOORDFields(env, lpObject1, &lpStruct->dwCursorPosition);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
	lpStruct->wAttributes = (*env)->GetShortField(env, lpObject, CONSOLE_SCREEN_BUFFER_INFOFc.attributes);
#endif
#if defined(_WIN32) || defined(_WIN64)
		{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, CONSOLE_SCREEN_BUFFER_INFOFc.window);
	if (lpObject1 != NULL) getSMALL_RECTFields(env, lpObject1, &lpStruct->srWindow);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
		{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, CONSOLE_SCREEN_BUFFER_INFOFc.maximumWindowSize);
	if (lpObject1 != NULL) getCOORDFields(env, lpObject1, &lpStruct->dwMaximumWindowSize);
	}
#endif
	return lpStruct;
}

void setCONSOLE_SCREEN_BUFFER_INFOFields(JNIEnv *env, jobject lpObject, CONSOLE_SCREEN_BUFFER_INFO *lpStruct)
{
	if (!CONSOLE_SCREEN_BUFFER_INFOFc.cached) cacheCONSOLE_SCREEN_BUFFER_INFOFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, CONSOLE_SCREEN_BUFFER_INFOFc.size);
	if (lpObject1 != NULL) setCOORDFields(env, lpObject1, &lpStruct->dwSize);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
	{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, CONSOLE_SCREEN_BUFFER_INFOFc.cursorPosition);
	if (lpObject1 != NULL) setCOORDFields(env, lpObject1, &lpStruct->dwCursorPosition);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetShortField(env, lpObject, CONSOLE_SCREEN_BUFFER_INFOFc.attributes, (jshort)lpStruct->wAttributes);
#endif
#if defined(_WIN32) || defined(_WIN64)
	{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, CONSOLE_SCREEN_BUFFER_INFOFc.window);
	if (lpObject1 != NULL) setSMALL_RECTFields(env, lpObject1, &lpStruct->srWindow);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
	{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, CONSOLE_SCREEN_BUFFER_INFOFc.maximumWindowSize);
	if (lpObject1 != NULL) setCOORDFields(env, lpObject1, &lpStruct->dwMaximumWindowSize);
	}
#endif
}
#endif

#if defined(_WIN32) || defined(_WIN64)
typedef struct COORD_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID x, y;
} COORD_FID_CACHE;

COORD_FID_CACHE COORDFc;

void cacheCOORDFields(JNIEnv *env, jobject lpObject)
{
	if (COORDFc.cached) return;
	COORDFc.clazz = (*env)->GetObjectClass(env, lpObject);
	COORDFc.x = (*env)->GetFieldID(env, COORDFc.clazz, "x", "S");
	COORDFc.y = (*env)->GetFieldID(env, COORDFc.clazz, "y", "S");
	hawtjni_w_barrier();
	COORDFc.cached = 1;
}

COORD *getCOORDFields(JNIEnv *env, jobject lpObject, COORD *lpStruct)
{
	if (!COORDFc.cached) cacheCOORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	lpStruct->X = (*env)->GetShortField(env, lpObject, COORDFc.x);
#endif
#if defined(_WIN32) || defined(_WIN64)
	lpStruct->Y = (*env)->GetShortField(env, lpObject, COORDFc.y);
#endif
	return lpStruct;
}

void setCOORDFields(JNIEnv *env, jobject lpObject, COORD *lpStruct)
{
	if (!COORDFc.cached) cacheCOORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetShortField(env, lpObject, COORDFc.x, (jshort)lpStruct->X);
#endif
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetShortField(env, lpObject, COORDFc.y, (jshort)lpStruct->Y);
#endif
}
#endif

#if defined(_WIN32) || defined(_WIN64)
typedef struct FOCUS_EVENT_RECORD_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID setFocus;
} FOCUS_EVENT_RECORD_FID_CACHE;

FOCUS_EVENT_RECORD_FID_CACHE FOCUS_EVENT_RECORDFc;

void cacheFOCUS_EVENT_RECORDFields(JNIEnv *env, jobject lpObject)
{
	if (FOCUS_EVENT_RECORDFc.cached) return;
	FOCUS_EVENT_RECORDFc.clazz = (*env)->GetObjectClass(env, lpObject);
	FOCUS_EVENT_RECORDFc.setFocus = (*env)->GetFieldID(env, FOCUS_EVENT_RECORDFc.clazz, "setFocus", "Z");
	hawtjni_w_barrier();
	FOCUS_EVENT_RECORDFc.cached = 1;
}

FOCUS_EVENT_RECORD *getFOCUS_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, FOCUS_EVENT_RECORD *lpStruct)
{
	if (!FOCUS_EVENT_RECORDFc.cached) cacheFOCUS_EVENT_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	lpStruct->bSetFocus = (*env)->GetBooleanField(env, lpObject, FOCUS_EVENT_RECORDFc.setFocus);
#endif
	return lpStruct;
}

void setFOCUS_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, FOCUS_EVENT_RECORD *lpStruct)
{
	if (!FOCUS_EVENT_RECORDFc.cached) cacheFOCUS_EVENT_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetBooleanField(env, lpObject, FOCUS_EVENT_RECORDFc.setFocus, (jboolean)lpStruct->bSetFocus);
#endif
}
#endif

#if defined(_WIN32) || defined(_WIN64)
typedef struct INPUT_RECORD_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID eventType, keyEvent, mouseEvent, windowBufferSizeEvent, menuEvent, focusEvent;
} INPUT_RECORD_FID_CACHE;

INPUT_RECORD_FID_CACHE INPUT_RECORDFc;

void cacheINPUT_RECORDFields(JNIEnv *env, jobject lpObject)
{
	if (INPUT_RECORDFc.cached) return;
	INPUT_RECORDFc.clazz = (*env)->GetObjectClass(env, lpObject);
	INPUT_RECORDFc.eventType = (*env)->GetFieldID(env, INPUT_RECORDFc.clazz, "eventType", "S");
	INPUT_RECORDFc.keyEvent = (*env)->GetFieldID(env, INPUT_RECORDFc.clazz, "keyEvent", "Lorg/fusesource/jansi/internal/Kernel32$KEY_EVENT_RECORD;");
	INPUT_RECORDFc.mouseEvent = (*env)->GetFieldID(env, INPUT_RECORDFc.clazz, "mouseEvent", "Lorg/fusesource/jansi/internal/Kernel32$MOUSE_EVENT_RECORD;");
	INPUT_RECORDFc.windowBufferSizeEvent = (*env)->GetFieldID(env, INPUT_RECORDFc.clazz, "windowBufferSizeEvent", "Lorg/fusesource/jansi/internal/Kernel32$WINDOW_BUFFER_SIZE_RECORD;");
	INPUT_RECORDFc.menuEvent = (*env)->GetFieldID(env, INPUT_RECORDFc.clazz, "menuEvent", "Lorg/fusesource/jansi/internal/Kernel32$MENU_EVENT_RECORD;");
	INPUT_RECORDFc.focusEvent = (*env)->GetFieldID(env, INPUT_RECORDFc.clazz, "focusEvent", "Lorg/fusesource/jansi/internal/Kernel32$FOCUS_EVENT_RECORD;");
	hawtjni_w_barrier();
	INPUT_RECORDFc.cached = 1;
}

INPUT_RECORD *getINPUT_RECORDFields(JNIEnv *env, jobject lpObject, INPUT_RECORD *lpStruct)
{
	if (!INPUT_RECORDFc.cached) cacheINPUT_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	lpStruct->EventType = (*env)->GetShortField(env, lpObject, INPUT_RECORDFc.eventType);
#endif
#if defined(_WIN32) || defined(_WIN64)
		{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, INPUT_RECORDFc.keyEvent);
	if (lpObject1 != NULL) getKEY_EVENT_RECORDFields(env, lpObject1, &lpStruct->Event.KeyEvent);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
		{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, INPUT_RECORDFc.mouseEvent);
	if (lpObject1 != NULL) getMOUSE_EVENT_RECORDFields(env, lpObject1, &lpStruct->Event.MouseEvent);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
		{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, INPUT_RECORDFc.windowBufferSizeEvent);
	if (lpObject1 != NULL) getWINDOW_BUFFER_SIZE_RECORDFields(env, lpObject1, &lpStruct->Event.WindowBufferSizeEvent);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
		{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, INPUT_RECORDFc.menuEvent);
	if (lpObject1 != NULL) getMENU_EVENT_RECORDFields(env, lpObject1, &lpStruct->Event.MenuEvent);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
		{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, INPUT_RECORDFc.focusEvent);
	if (lpObject1 != NULL) getFOCUS_EVENT_RECORDFields(env, lpObject1, &lpStruct->Event.FocusEvent);
	}
#endif
	return lpStruct;
}

void setINPUT_RECORDFields(JNIEnv *env, jobject lpObject, INPUT_RECORD *lpStruct)
{
	if (!INPUT_RECORDFc.cached) cacheINPUT_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetShortField(env, lpObject, INPUT_RECORDFc.eventType, (jshort)lpStruct->EventType);
#endif
#if defined(_WIN32) || defined(_WIN64)
	{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, INPUT_RECORDFc.keyEvent);
	if (lpObject1 != NULL) setKEY_EVENT_RECORDFields(env, lpObject1, &lpStruct->Event.KeyEvent);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
	{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, INPUT_RECORDFc.mouseEvent);
	if (lpObject1 != NULL) setMOUSE_EVENT_RECORDFields(env, lpObject1, &lpStruct->Event.MouseEvent);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
	{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, INPUT_RECORDFc.windowBufferSizeEvent);
	if (lpObject1 != NULL) setWINDOW_BUFFER_SIZE_RECORDFields(env, lpObject1, &lpStruct->Event.WindowBufferSizeEvent);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
	{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, INPUT_RECORDFc.menuEvent);
	if (lpObject1 != NULL) setMENU_EVENT_RECORDFields(env, lpObject1, &lpStruct->Event.MenuEvent);
	}
#endif
#if defined(_WIN32) || defined(_WIN64)
	{
	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, INPUT_RECORDFc.focusEvent);
	if (lpObject1 != NULL) setFOCUS_EVENT_RECORDFields(env, lpObject1, &lpStruct->Event.FocusEvent);
	}
#endif
}
#endif

#if defined(_WIN32) || defined(_WIN64)
typedef struct KEY_EVENT_RECORD_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID keyDown, repeatCount, keyCode, scanCode, uchar, controlKeyState;
} KEY_EVENT_RECORD_FID_CACHE;

KEY_EVENT_RECORD_FID_CACHE KEY_EVENT_RECORDFc;

void cacheKEY_EVENT_RECORDFields(JNIEnv *env, jobject lpObject)
{
	if (KEY_EVENT_RECORDFc.cached) return;
	KEY_EVENT_RECORDFc.clazz = (*env)->GetObjectClass(env, lpObject);
	KEY_EVENT_RECORDFc.keyDown = (*env)->GetFieldID(env, KEY_EVENT_RECORDFc.clazz, "keyDown", "Z");
	KEY_EVENT_RECORDFc.repeatCount = (*env)->GetFieldID(env, KEY_EVENT_RECORDFc.clazz, "repeatCount", "S");
	KEY_EVENT_RECORDFc.keyCode = (*env)->GetFieldID(env, KEY_EVENT_RECORDFc.clazz, "keyCode", "S");
	KEY_EVENT_RECORDFc.scanCode = (*env)->GetFieldID(env, KEY_EVENT_RECORDFc.clazz, "scanCode", "S");
	KEY_EVENT_RECORDFc.uchar = (*env)->GetFieldID(env, KEY_EVENT_RECORDFc.clazz, "uchar", "C");
	KEY_EVENT_RECORDFc.controlKeyState = (*env)->GetFieldID(env, KEY_EVENT_RECORDFc.clazz, "controlKeyState", "I");
	hawtjni_w_barrier();
	KEY_EVENT_RECORDFc.cached = 1;
}

KEY_EVENT_RECORD *getKEY_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, KEY_EVENT_RECORD *lpStruct)
{
	if (!KEY_EVENT_RECORDFc.cached) cacheKEY_EVENT_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	lpStruct->bKeyDown = (*env)->GetBooleanField(env, lpObject, KEY_EVENT_RECORDFc.keyDown);
	lpStruct->wRepeatCount = (*env)->GetShortField(env, lpObject, KEY_EVENT_RECORDFc.repeatCount);
	lpStruct->wVirtualKeyCode = (*env)->GetShortField(env, lpObject, KEY_EVENT_RECORDFc.keyCode);
	lpStruct->wVirtualScanCode = (*env)->GetShortField(env, lpObject, KEY_EVENT_RECORDFc.scanCode);
	lpStruct->uChar.UnicodeChar = (*env)->GetCharField(env, lpObject, KEY_EVENT_RECORDFc.uchar);
	lpStruct->dwControlKeyState = (*env)->GetIntField(env, lpObject, KEY_EVENT_RECORDFc.controlKeyState);
#endif
	return lpStruct;
}

void setKEY_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, KEY_EVENT_RECORD *lpStruct)
{
	if (!KEY_EVENT_RECORDFc.cached) cacheKEY_EVENT_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetBooleanField(env, lpObject, KEY_EVENT_RECORDFc.keyDown, (jboolean)lpStruct->bKeyDown);
	(*env)->SetShortField(env, lpObject, KEY_EVENT_RECORDFc.repeatCount, (jshort)lpStruct->wRepeatCount);
	(*env)->SetShortField(env, lpObject, KEY_EVENT_RECORDFc.keyCode, (jshort)lpStruct->wVirtualKeyCode);
	(*env)->SetShortField(env, lpObject, KEY_EVENT_RECORDFc.scanCode, (jshort)lpStruct->wVirtualScanCode);
	(*env)->SetCharField(env, lpObject, KEY_EVENT_RECORDFc.uchar, (jchar)lpStruct->uChar.UnicodeChar);
	(*env)->SetIntField(env, lpObject, KEY_EVENT_RECORDFc.controlKeyState, (jint)lpStruct->dwControlKeyState);
#endif
}
#endif

#if defined(_WIN32) || defined(_WIN64)
typedef struct MENU_EVENT_RECORD_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID commandId;
} MENU_EVENT_RECORD_FID_CACHE;

MENU_EVENT_RECORD_FID_CACHE MENU_EVENT_RECORDFc;

void cacheMENU_EVENT_RECORDFields(JNIEnv *env, jobject lpObject)
{
	if (MENU_EVENT_RECORDFc.cached) return;
	MENU_EVENT_RECORDFc.clazz = (*env)->GetObjectClass(env, lpObject);
	MENU_EVENT_RECORDFc.commandId = (*env)->GetFieldID(env, MENU_EVENT_RECORDFc.clazz, "commandId", "I");
	hawtjni_w_barrier();
	MENU_EVENT_RECORDFc.cached = 1;
}

MENU_EVENT_RECORD *getMENU_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, MENU_EVENT_RECORD *lpStruct)
{
	if (!MENU_EVENT_RECORDFc.cached) cacheMENU_EVENT_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	lpStruct->dwCommandId = (*env)->GetIntField(env, lpObject, MENU_EVENT_RECORDFc.commandId);
#endif
	return lpStruct;
}

void setMENU_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, MENU_EVENT_RECORD *lpStruct)
{
	if (!MENU_EVENT_RECORDFc.cached) cacheMENU_EVENT_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetIntField(env, lpObject, MENU_EVENT_RECORDFc.commandId, (jint)lpStruct->dwCommandId);
#endif
}
#endif

#if defined(_WIN32) || defined(_WIN64)
typedef struct MOUSE_EVENT_RECORD_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID mousePosition, buttonState, controlKeyState, eventFlags;
} MOUSE_EVENT_RECORD_FID_CACHE;

MOUSE_EVENT_RECORD_FID_CACHE MOUSE_EVENT_RECORDFc;

void cacheMOUSE_EVENT_RECORDFields(JNIEnv *env, jobject lpObject)
{
	if (MOUSE_EVENT_RECORDFc.cached) return;
	MOUSE_EVENT_RECORDFc.clazz = (*env)->GetObjectClass(env, lpObject);
	MOUSE_EVENT_RECORDFc.mousePosition = (*env)->GetFieldID(env, MOUSE_EVENT_RECORDFc.clazz, "mousePosition", "Lorg/fusesource/jansi/internal/Kernel32$COORD;");
	MOUSE_EVENT_RECORDFc.buttonState = (*env)->GetFieldID(env, MOUSE_EVENT_RECORDFc.clazz, "buttonState", "I");
	MOUSE_EVENT_RECORDFc.controlKeyState = (*env)->GetFieldID(env, MOUSE_EVENT_RECORDFc.clazz, "controlKeyState", "I");
	MOUSE_EVENT_RECORDFc.eventFlags = (*env)->GetFieldID(env, MOUSE_EVENT_RECORDFc.clazz, "eventFlags", "I");
	hawtjni_w_barrier();
	MOUSE_EVENT_RECORDFc.cached = 1;
}

MOUSE_EVENT_RECORD *getMOUSE_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, MOUSE_EVENT_RECORD *lpStruct)
{
	if (!MOUSE_EVENT_RECORDFc.cached) cacheMOUSE_EVENT_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
    {
	    jobject lpObject1 = (*env)->GetObjectField(env, lpObject, MOUSE_EVENT_RECORDFc.mousePosition);
	    if (lpObject1 != NULL) getCOORDFields(env, lpObject1, &lpStruct->dwMousePosition);
	}
	lpStruct->dwButtonState = (*env)->GetIntField(env, lpObject, MOUSE_EVENT_RECORDFc.buttonState);
	lpStruct->dwControlKeyState = (*env)->GetIntField(env, lpObject, MOUSE_EVENT_RECORDFc.controlKeyState);
	lpStruct->dwEventFlags = (*env)->GetIntField(env, lpObject, MOUSE_EVENT_RECORDFc.eventFlags);
#endif
	return lpStruct;
}

void setMOUSE_EVENT_RECORDFields(JNIEnv *env, jobject lpObject, MOUSE_EVENT_RECORD *lpStruct)
{
	if (!MOUSE_EVENT_RECORDFc.cached) cacheMOUSE_EVENT_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	{
	    jobject lpObject1 = (*env)->GetObjectField(env, lpObject, MOUSE_EVENT_RECORDFc.mousePosition);
	    if (lpObject1 != NULL) setCOORDFields(env, lpObject1, &lpStruct->dwMousePosition);
	}
	(*env)->SetIntField(env, lpObject, MOUSE_EVENT_RECORDFc.buttonState, (jint)lpStruct->dwButtonState);
	(*env)->SetIntField(env, lpObject, MOUSE_EVENT_RECORDFc.controlKeyState, (jint)lpStruct->dwControlKeyState);
	(*env)->SetIntField(env, lpObject, MOUSE_EVENT_RECORDFc.eventFlags, (jint)lpStruct->dwEventFlags);
#endif
}
#endif

#if defined(_WIN32) || defined(_WIN64)
typedef struct SMALL_RECT_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID left, top, right, bottom;
} SMALL_RECT_FID_CACHE;

SMALL_RECT_FID_CACHE SMALL_RECTFc;

void cacheSMALL_RECTFields(JNIEnv *env, jobject lpObject)
{
	if (SMALL_RECTFc.cached) return;
	SMALL_RECTFc.clazz = (*env)->GetObjectClass(env, lpObject);
	SMALL_RECTFc.left = (*env)->GetFieldID(env, SMALL_RECTFc.clazz, "left", "S");
	SMALL_RECTFc.top = (*env)->GetFieldID(env, SMALL_RECTFc.clazz, "top", "S");
	SMALL_RECTFc.right = (*env)->GetFieldID(env, SMALL_RECTFc.clazz, "right", "S");
	SMALL_RECTFc.bottom = (*env)->GetFieldID(env, SMALL_RECTFc.clazz, "bottom", "S");
	hawtjni_w_barrier();
	SMALL_RECTFc.cached = 1;
}

SMALL_RECT *getSMALL_RECTFields(JNIEnv *env, jobject lpObject, SMALL_RECT *lpStruct)
{
	if (!SMALL_RECTFc.cached) cacheSMALL_RECTFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	lpStruct->Left = (*env)->GetShortField(env, lpObject, SMALL_RECTFc.left);
	lpStruct->Top = (*env)->GetShortField(env, lpObject, SMALL_RECTFc.top);
	lpStruct->Right = (*env)->GetShortField(env, lpObject, SMALL_RECTFc.right);
	lpStruct->Bottom = (*env)->GetShortField(env, lpObject, SMALL_RECTFc.bottom);
#endif
	return lpStruct;
}

void setSMALL_RECTFields(JNIEnv *env, jobject lpObject, SMALL_RECT *lpStruct)
{
	if (!SMALL_RECTFc.cached) cacheSMALL_RECTFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	(*env)->SetShortField(env, lpObject, SMALL_RECTFc.left, (jshort)lpStruct->Left);
	(*env)->SetShortField(env, lpObject, SMALL_RECTFc.top, (jshort)lpStruct->Top);
	(*env)->SetShortField(env, lpObject, SMALL_RECTFc.right, (jshort)lpStruct->Right);
	(*env)->SetShortField(env, lpObject, SMALL_RECTFc.bottom, (jshort)lpStruct->Bottom);
#endif
}
#endif

#if defined(_WIN32) || defined(_WIN64)
typedef struct WINDOW_BUFFER_SIZE_RECORD_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID size;
} WINDOW_BUFFER_SIZE_RECORD_FID_CACHE;

WINDOW_BUFFER_SIZE_RECORD_FID_CACHE WINDOW_BUFFER_SIZE_RECORDFc;

void cacheWINDOW_BUFFER_SIZE_RECORDFields(JNIEnv *env, jobject lpObject)
{
	if (WINDOW_BUFFER_SIZE_RECORDFc.cached) return;
	WINDOW_BUFFER_SIZE_RECORDFc.clazz = (*env)->GetObjectClass(env, lpObject);
	WINDOW_BUFFER_SIZE_RECORDFc.size = (*env)->GetFieldID(env, WINDOW_BUFFER_SIZE_RECORDFc.clazz, "size", "Lorg/fusesource/jansi/internal/Kernel32$COORD;");
	hawtjni_w_barrier();
	WINDOW_BUFFER_SIZE_RECORDFc.cached = 1;
}

WINDOW_BUFFER_SIZE_RECORD *getWINDOW_BUFFER_SIZE_RECORDFields(JNIEnv *env, jobject lpObject, WINDOW_BUFFER_SIZE_RECORD *lpStruct)
{
	if (!WINDOW_BUFFER_SIZE_RECORDFc.cached) cacheWINDOW_BUFFER_SIZE_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	{
	    jobject lpObject1 = (*env)->GetObjectField(env, lpObject, WINDOW_BUFFER_SIZE_RECORDFc.size);
	    if (lpObject1 != NULL) getCOORDFields(env, lpObject1, &lpStruct->dwSize);
	}
#endif
	return lpStruct;
}

void setWINDOW_BUFFER_SIZE_RECORDFields(JNIEnv *env, jobject lpObject, WINDOW_BUFFER_SIZE_RECORD *lpStruct)
{
	if (!WINDOW_BUFFER_SIZE_RECORDFc.cached) cacheWINDOW_BUFFER_SIZE_RECORDFields(env, lpObject);
#if defined(_WIN32) || defined(_WIN64)
	{
    	jobject lpObject1 = (*env)->GetObjectField(env, lpObject, WINDOW_BUFFER_SIZE_RECORDFc.size);
	    if (lpObject1 != NULL) setCOORDFields(env, lpObject1, &lpStruct->dwSize);
	}
#endif
}
#endif

