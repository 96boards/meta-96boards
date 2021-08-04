# Enable machine specific hooks for the generic Linux kernel
# e.g. generating boot images

def get_include_handler(d):
    machine = d.getVar('MACHINE', True)
    if (machine == "dragonboard-410c"):
        include = "recipes-kernel/linux/linux-qcom-bootimg.inc"
    else:
        include = "recipes-kernel/linux/file-cannot-be-found.inc"
    return include

# Use a weak include to avoid to produce an error when the file cannot be found.
# It is the case when we don't have any machine specific hooks.
include ${@get_include_handler(d)}

KERNEL_EXTRA_ARGS:am57xx-evm += "LOADADDR=${UBOOT_ENTRYPOINT}"
# Define the kernel device tree for am57xx-evm machine
# meta-ti is setting more device trees than the ones available in mainline kernel
KERNEL_DEVICETREE:am57xx-evm = "am57xx-beagle-x15.dtb"
SRC_URI:append:am57xx-evm = " file://multi_v7_hugepage.config;subdir=git/kernel/configs"
KERNEL_CONFIG_FRAGMENTS:append:am57xx-evm = " ${S}/kernel/configs/multi_v7_hugepage.config"

KERNEL_DEVICETREE:beaglebone = "am335x-bone.dtb am335x-boneblack.dtb am335x-bonegreen.dtb"
SRC_URI:append:beaglebone = " file://multi_v7_hugepage.config;subdir=git/kernel/configs"
KERNEL_CONFIG_FRAGMENTS:append:beaglebone = " ${S}/kernel/configs/multi_v7_hugepage.config"

KERNEL_EXTRA_ARGS:stih410-b2260 += "LOADADDR=${ST_KERNEL_LOADADDR}"

SRC_URI:append = " file://virtio.config;subdir=git/kernel/configs"
KERNEL_CONFIG_FRAGMENTS:append = " ${S}/kernel/configs/virtio.config"
