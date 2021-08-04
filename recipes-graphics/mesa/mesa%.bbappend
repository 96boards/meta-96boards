# Define the osmesa block in PACKAGECONFIG for target, this block is
# not defined in the master recipe, effectively causing the osmesa
# feature to be disabled and -Dosmesa=none set.
PACKAGECONFIG:append:hikey = " osmesa"

# Solve 'Problem encountered: OSMesa classic requires dri (classic) swrast.'
# by defining the dri swrast for hikey machine
DRIDRIVERS:append:hikey = "swrast"

# Solve 'ERROR: Problem encountered: Only one swrast provider can be built'
# by excluding gallium support, dri is used together with 'classic' mesa backend.
PACKAGECONFIG:remove:hikey = "gallium"

PROVIDES:remove:hikey = "virtual/libgles1 virtual/libgles2 virtual/egl"

# As mali450-userland is providing the shared objects, we only want
# the headers and pkgconfig from mesa.

do_install:append:hikey() {
  rm -rf ${D}${libdir}/dri
  rm -f ${D}${libdir}/*.so*
}
