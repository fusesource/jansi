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
#include "hawtjni.h"
#include "jansi_structs.h"

#define CLibrary_NATIVE(func) Java_org_fusesource_jansi_internal_CLibrary_##func

#if defined(HAVE_TTYNAME)
JNIEXPORT jstring JNICALL CLibrary_NATIVE(ttyname)
	(JNIEnv *env, jclass that, jint arg0)
{
	jstring rc = 0;
	char s[256] = { 0 };
	int r = 0;

	r = ttyname_r(arg0, s, 256);
	if (!r) rc = (*env)->NewStringUTF(env,s);

	return rc;
}
#endif
