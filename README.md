Meta-96boards
================================

Introduction
-------------------------

The official OpenEmbedded/Yocto Project BSP layer for the 96boards.org machines

The meta-96boards layer depends on:

	URI: git://git.openembedded.org/openembedded-core
	layers: meta
	branch: master

	URI: git://git.openembedded.org/meta-openembedded
	layers: meta-oe
	branch: master

Please follow the recommended setup procedures of your OE distribution. For Angstrom that is http://www.angstrom-distribution.org/building-angstrom, other distros should have similar online resources.

Notes
-------------------------

Please note that the support for Qualcomm based 96boards.org, such as the
DragonBoard 410c, is done through the meta-qualcomm BSP layer:
https://github.com/ndechesne/meta-qualcomm not meta-96boards.

Hikey kernel enables CONFIG_MODULE_SIG options which enforces that openssl dev
package is installed on build host.

for debian-like systems

sudo apt-get install libssl-dev

fedora-like systems

sudo yum install openssl-devel

Contributing
-------------------------

Please use github for pull requests: https://github.com/96boards/meta-96boards/pulls

Reporting bugs
-------------------------

The github issue tracker (https://github.com/96boards/meta-96boards/issues) is being used to keep track of bugs.

Maintainers
-------------------------

* Koen Kooi <koen.kooi@linaro.org>
* Fathi Boudra <mailto:fathi.boudra@linaro.org>
