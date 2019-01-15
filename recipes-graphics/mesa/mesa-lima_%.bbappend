# Enable lima for hisilicon
PACKAGECONFIG_append_hikey = " gallium"
PACKAGECONFIG_remove_hikey = " vulkan"
GALLIUMDRIVERS_hikey = "hisilicon,meson,lima,freedreno,etnaviv,swrast,imx,rockchip,sun4i"
DRIDRIVERS_hikey = ""
