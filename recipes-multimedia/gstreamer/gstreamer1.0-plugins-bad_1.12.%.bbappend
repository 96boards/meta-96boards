PACKAGECONFIG[egl]             = "--enable-egl,--disable-egl,virtual/egl,libegl"
PACKAGECONFIG[gles2]           = "--enable-gles2,--disable-gles2,virtual/libgles2,libgles2"
PACKAGECONFIG[opengl]          = "--enable-opengl,--disable-opengl,virtual/libgl libglu,libgl"

RDEPENDS_libgstgl-1.0 += "libgles2 libegl libgl"
RDEPENDS_gstreamer1.0-plugins-bad-opengl += "libgles2 libegl libgl"
