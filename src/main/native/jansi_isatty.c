/*******************************************************************************
 * Copyright (C) 2017, the original author(s).
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

#define CLibrary_NATIVE(func) Java_org_fusesource_jansi_internal_CLibrary_##func

#if defined(_WIN32) || defined(_WIN64)

typedef struct _UNICODE_STRING {
	USHORT Length;
	USHORT MaximumLength;
	PWSTR  Buffer;
} UNICODE_STRING, *PUNICODE_STRING;

typedef struct _OBJECT_NAME_INFORMATION {
	UNICODE_STRING          Name;
	WCHAR                   NameBuffer[0];
} OBJECT_NAME_INFORMATION, *POBJECT_NAME_INFORMATION;

typedef enum {
	ObjectBasicInformation,
	ObjectNameInformation,
	ObjectTypeInformation,
	ObjectAllInformation,
	ObjectDataInformation
} OBJECT_INFORMATION_CLASS;

typedef NTSTATUS (NTAPI *TFNNtQueryObject)(HANDLE, OBJECT_INFORMATION_CLASS, PVOID, ULONG, PULONG);
TFNNtQueryObject NtQueryObject = 0;

HANDLE hModuleNtDll = 0;

JNIEXPORT jint JNICALL CLibrary_NATIVE(isatty)
	(JNIEnv *env, jclass that, jint arg0)
{
	jint rc;

	ULONG result;
	BYTE buffer[1024];
	POBJECT_NAME_INFORMATION nameinfo = (POBJECT_NAME_INFORMATION) buffer;
	PWSTR name;
	DWORD mode;

	/* check if fd is a pipe */
	HANDLE h = (HANDLE) _get_osfhandle(arg0);
	DWORD t = h != NULL ? GetFileType(h) : 0;
	if (h != NULL && t == FILE_TYPE_CHAR) {
	    // check that this is a real tty because the /dev/null
	    // and /dev/zero streams are also of type FILE_TYPE_CHAR
		rc = GetConsoleMode(h, &mode) != 0;
	}
	else {
		if (hModuleNtDll == 0) {
			hModuleNtDll = LoadLibraryW(L"ntdll.dll");
		}
		if (hModuleNtDll == 0) {
			rc = 0;
		}
		else {
			if (NtQueryObject == 0) {
				NtQueryObject = (TFNNtQueryObject) GetProcAddress(hModuleNtDll, "NtQueryObject");
			}
			if (NtQueryObject == 0) {
				rc = 0;
			}
			/* get pipe name */
			else if (NtQueryObject(h, ObjectNameInformation, buffer, sizeof(buffer) - 2, &result) != 0) {
				rc = 0;
			}
			else {

				name = nameinfo->Name.Buffer;
				if (name == NULL) {
				    rc = 0;
				}
				else {
                    name[nameinfo->Name.Length / 2] = 0;

                    //fprintf( stderr, "Standard stream %d: pipe name: %S\n", arg0, name);

                    /*
                     * Check if this could be a MSYS2 pty pipe ('msys-XXXX-ptyN-XX')
                     * or a cygwin pty pipe ('cygwin-XXXX-ptyN-XX')
                     */
                    if ((wcsstr(name, L"msys-") || wcsstr(name, L"cygwin-")) && wcsstr(name, L"-pty")) {
                        rc = 1;
                    } else {
                        // This is definitely not a tty
                        rc = 0;
                    }
                }
			}
		}
	}

	return rc;
}

#else
#if defined(HAVE_ISATTY)

JNIEXPORT jint JNICALL CLibrary_NATIVE(isatty)
	(JNIEnv *env, jclass that, jint arg0)
{
	jint rc = 0;

	rc = (jint)isatty(arg0);

	return rc;
}

#endif
#endif
