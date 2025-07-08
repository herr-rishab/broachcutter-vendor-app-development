package net.broachcutter.vendorapp.models

enum class Machine(val partNumber: String) {
    CUB("900001"),
    CUB_AUTO("900006"),
    CUB_SLEEK("900009"),
    SUPER("900002"),
    TRIDENT("900005"),
    TITAN("900003"),
    CUB_XL("900015"),
    SUPER_XL("900016"),
    TITAN_XL("900017");

    companion object {
        fun getMachine(partNumber: String): Machine {
            for (machine in values()) {
                if (machine.partNumber == partNumber) {
                    return machine
                }
            }
            return CUB_XL
        }
    }
}
