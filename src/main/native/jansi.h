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
#ifndef JANSI_H
#define JANSI_H

#ifdef HAVE_CONFIG_H
  /* configure based build.. we will use what it discovered about the platform */
  #include "config.h"
#else
  #if defined(_WIN32) || defined(_WIN64)
    /* Windows based build */
    #define HAVE_STDLIB_H 1
    #define HAVE_STRINGS_H 1

    #define STDIN_FILENO 0
    #define STDOUT_FILENO 1
    #define STDERR_FILENO 2
    #define HAVE_ISATTY

    #include <windows.h>
    #include <conio.h>
    #include <io.h>

    #define isatty _isatty
    #define getch _getch

    #ifndef MOUSE_HWHEELED
      #define MOUSE_HWHEELED 0x0008
    #endif

  #endif
#endif

#ifdef HAVE_UNISTD_H
  #include <unistd.h>
#endif

#ifdef HAVE_STDLIB_H
  #include <stdlib.h>
#endif

#ifdef HAVE_STRINGS_H
  #include <string.h>
#endif

#ifdef HAVE_JANSI_TERM_H
  #include <term.h>
#endif

#ifdef HAVE_JANSI_LIBUTIL_H
  #include <libutil.h>
#endif

#ifdef HAVE_JANSI_UTIL_H
  #include <util.h>
#endif

#ifdef HAVE_TERMIOS_H
  #include <termios.h>
#endif

#ifdef HAVE_IOCTL_H
  #include <sys/ioctl.h>
#endif

#ifdef HAVE_PTY_H
  #include <pty.h>
#endif


#endif /* JANSI_H */
