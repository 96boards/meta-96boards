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
