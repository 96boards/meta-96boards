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

KERNEL_EXTRA_ARGS_am57xx-evm += "LOADADDR=${UBOOT_ENTRYPOINT}"
# Define the kernel device tree for am57xx-evm machine
# meta-ti is setting more device trees than the ones available in mainline kernel
KERNEL_DEVICETREE_am57xx-evm = "am57xx-beagle-x15.dtb"
SRC_URI_append_am57xx-evm = " file://multi_v7_hugepage.config;subdir=git/kernel/configs"
KERNEL_CONFIG_FRAGMENTS_append_am57xx-evm = " ${S}/kernel/configs/multi_v7_hugepage.config"

KERNEL_EXTRA_ARGS_stih410-b2260 += "LOADADDR=${ST_KERNEL_LOADADDR}"

SRC_URI_append = " file://virtio.config;subdir=git/kernel/configs"
KERNEL_CONFIG_FRAGMENTS_append = " ${S}/kernel/configs/virtio.config"
