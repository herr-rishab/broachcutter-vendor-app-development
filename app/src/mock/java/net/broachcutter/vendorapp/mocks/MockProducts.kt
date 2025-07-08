package net.broachcutter.vendorapp.mocks

import net.broachcutter.vendorapp.models.Machine.*
import net.broachcutter.vendorapp.models.Product
import net.broachcutter.vendorapp.models.ProductType
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterMaterial
import net.broachcutter.vendorapp.screens.cutters.specifications.CutterType

val mockDrillingMachines = listOf(
    Product(
        partNumber = "900001",
        name = "CUB",
        productType = ProductType.MACHINE
    ),
    Product(
        partNumber = "900002",
        name = "CUB AUTO",
        productType = ProductType.MACHINE
    ),
    Product(
        partNumber = "900003",
        name = "CUB SLEEK",
        productType = ProductType.MACHINE,
        taxRate = 18f
    ),
    Product(
        partNumber = "900004",
        name = "SUPER",
        productType = ProductType.MACHINE,
        taxRate = 18f
    ),
    Product(
        partNumber = "900005",
        name = "TRIDENT",
        productType = ProductType.MACHINE,
        taxRate = 18f
    ),
    Product(
        partNumber = "900006",
        name = "TITAN",
        productType = ProductType.MACHINE,
        taxRate = 18f
    ),
    Product(
        partNumber = "900007",
        name = "MAXIMUS",
        productType = ProductType.MACHINE,
        taxRate = 18f
    )
)

val mockHssAnnularCutters = listOf(
    Product(
        partNumber = "100114",
        name = "HSS cutter ⌀14mm x 25mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.HSS,
        cutterType = CutterType.ANNULAR,
        taxRate = 18f
    ),
    Product(
        partNumber = "100018",
        name = "HSS cutter ⌀18mm x 50mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.HSS,
        cutterType = CutterType.ANNULAR,
        taxRate = 18f
    ),
    Product(
        partNumber = "100322",
        name = "HSS cutter ⌀22mm x 75mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.HSS,
        cutterType = CutterType.ANNULAR,
        taxRate = 18f
    )
)

val mockArborsByItemNumber = listOf(
    Product(
        partNumber = "100114",
        name = "Sample Arbor ⌀14mm x 25mm",
        productType = ProductType.ARBOR,
        taxRate = 18f
    ),
    Product(
        partNumber = "100018",
        name = "Sample Arbor ⌀18mm x 50mm",
        productType = ProductType.ARBOR,
        taxRate = 18f
    ),
    Product(
        partNumber = "100322",
        name = "Sample Arbor ⌀22mm x 75mm",
        productType = ProductType.ARBOR,
        taxRate = 18f
    )
)

val mockTctAnnularCutters = listOf(
    Product(
        partNumber = "200114",
        name = "TCT cutter ⌀14mm x 35mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.TCT,
        cutterType = CutterType.ANNULAR,
        taxRate = 18f
    ),
    Product(
        partNumber = "200018",
        name = "TCT cutter ⌀18mm x 50mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.TCT,
        cutterType = CutterType.ANNULAR,
        taxRate = 18f
    ),
    Product(
        partNumber = "200122.5",
        name = "TCT cutter ⌀22.5mm x 35mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.TCT,
        cutterType = CutterType.ANNULAR,
        taxRate = 18f
    ),
    Product(
        partNumber = "200022",
        name = "TCT cutter ⌀22mm x 50mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.TCT,
        cutterType = CutterType.ANNULAR,
        taxRate = 18f
    ),
    Product(
        partNumber = "200026",
        name = "TCT cutter ⌀26mm x 50mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.TCT,
        cutterType = CutterType.ANNULAR,
        taxRate = 18f
    )
)

// todo - Uncomment when mocking SolidDrill
// val mockRailAnnularCutters = listOf(
//    Product(
//        partNumber = "111018",
//        name = "Rail annular cutter ⌀18mm x 25mm",
//        productType = ProductType.CUTTER,
//        cutterMaterial = CutterMaterial.RAIL,
//        cutterType = CutterType.RAIL,
//        taxRate = 18f
//    ),
//    Product(
//        partNumber = "111027.5",
//        name = "Rail annular cutter ⌀27.5mm x 25mm",
//        productType = ProductType.CUTTER,
//        cutterMaterial = CutterMaterial.RAIL,
//        cutterType = CutterType.RAIL,
//        taxRate = 18f
//    )
// )
//
// val mockSolidDrills = listOf(
//    Product(
//        partNumber = "180006",
//        name = "Solid drill ⌀6mm x 35mm",
//        productType = ProductType.SOLID_DRILL,
//        cutterMaterial = CutterMaterial.SOLID_DRILL,
//        cutterType = CutterType.SOLID_DRILL,
//        taxRate = 18f
//    ),
//    Product(
//        partNumber = "180009",
//        name = "Solid drill ⌀9mm x 35mm",
//        productType = ProductType.SOLID_DRILL,
//        cutterMaterial = CutterMaterial.SOLID_DRILL,
//        cutterType = CutterType.SOLID_DRILL,
//        taxRate = 18f
//    ),
//    Product(
//        partNumber = "180011",
//        name = "Solid drill 11mm x 35mm",
//        productType = ProductType.SOLID_DRILL,
//        cutterMaterial = CutterMaterial.SOLID_DRILL,
//        cutterType = CutterType.SOLID_DRILL,
//        taxRate = 18f
//    )
// )
//
//
// val mockDrillBits = listOf(
//    Product(
//        partNumber = "190005",
//        name = "Solid drill ⌀5mm x 35mm",
//        productType = ProductType.DRILL_BITS,
//        cutterMaterial = CutterMaterial.SOLID_DRILL,
//        cutterType = CutterType.SOLID_DRILL,
//        taxRate = 18f
//    ),
//    Product(
//        partNumber = "190006",
//        name = "Solid drill ⌀6mm x 35mm",
//        productType = ProductType.DRILL_BITS,
//        cutterMaterial = CutterMaterial.SOLID_DRILL,
//        cutterType = CutterType.SOLID_DRILL,
//        taxRate = 18f
//    ),
//    Product(
//        partNumber = "190007",
//        name = "Solid drill ⌀7mm x 35mm",
//        productType = ProductType.DRILL_BITS,
//        cutterMaterial = CutterMaterial.SOLID_DRILL,
//        cutterType = CutterType.SOLID_DRILL,
//        taxRate = 18f
//    )
// )
// val mockSolidDrillAndDrillBits = mockSolidDrills + mockDrillBits

// todo - Uncomment when mocking SolidDrill
val allMockCutters =
    mockHssAnnularCutters + mockTctAnnularCutters // + mockSolidDrills + mockRailAnnularCutters

val mockPilotPins = listOf(
    Product(
        partNumber = "170009",
        name = "TCT Pilot Pin ⌀6.35mm x 106mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.TCT,
        taxRate = 18f
    ),
    Product(
        partNumber = "170010",
        name = "TCT Pilot Pin ⌀7.98mm x 108mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.TCT,
        taxRate = 18f
    ),
    Product(
        partNumber = "170002",
        name = "HSS Pilot Pin ⌀6.35mm x 102mm",
        productType = ProductType.CUTTER,
        cutterMaterial = CutterMaterial.HSS,
        taxRate = 18f
    )
)

val mockArbors = listOf(
    Product(
        partNumber = "141300",
        name = "Arbor taper 3, ⌀19.05mm x 50mm",
        productType = ProductType.ACCESSORY,
        taxRate = 18f
    ),
    Product(
        partNumber = "141310",
        name = "Arbor taper 3, ⌀31.75mm x 50mm",
        productType = ProductType.ACCESSORY,
        taxRate = 18f
    ),
    Product(
        partNumber = "151410",
        name = "Arbor taper 4, ⌀31.75mm x 75mm",
        productType = ProductType.ACCESSORY,
        taxRate = 18f
    )
)

val mockAdaptors = listOf(
    Product(
        partNumber = "160001",
        name = "UNIVERSAL ADAPTER - Designed to allow use of BroachCutter® Annular Cutters of 19.05mm weldon",
        productType = ProductType.ADAPTOR,
        taxRate = 18f
    ),
    Product(
        partNumber = "160004",
        name = "QUICK-IN SHANK ADAPTER - Designed to allow use of  BroachCutter® Annular Cutters of 19.05mm weldon",
        productType = ProductType.ADAPTOR,
        taxRate = 18f
    ),
    Product(
        partNumber = "160008",
        name = "Arbor extension, 75mm, 19.05mm Weldon Shank",
        productType = ProductType.ARBOR_EXTENSIONS,
        taxRate = 18f
    ),
    Product(
        partNumber = "160009",
        name = "Arbor extension, 100mm, 19.05mm Weldon Shank",
        productType = ProductType.ARBOR_EXTENSIONS,
        taxRate = 18f
    )
)

val mockAccessories = listOf(
    Product(
        partNumber = "920003",
        name = "Pipe drilling attachment for SUPER",
        productType = ProductType.ACCESSORY,
        associatedMachines = listOf(SUPER)
    ),
    Product(
        partNumber = "930002",
        name = "Pipe attachment for drilling holes",
        productType = ProductType.ACCESSORY,
        associatedMachines = listOf(TRIDENT, TITAN)
    ),
    Product(
        partNumber = "950003",
        name = "Non-reversible tapping attachment",
        productType = ProductType.ACCESSORY,
        associatedMachines = listOf(TRIDENT)
    ),
    Product(
        partNumber = "920002",
        name = "Pressurized Coolant System",
        productType = ProductType.ACCESSORY,
        associatedMachines = values().toList()
    )
)

val mockSpares = listOf(
    Product(
        partNumber = "610004",
        name = "Switch start/stop-230V - CUB, SUPER",
        productType = ProductType.SPARE,
        associatedMachines = listOf(CUB, SUPER)
    ),
    Product(
        partNumber = "990005",
        name = "Carbon brush - CUB, CUB Auto and CUB Sleek",
        productType = ProductType.SPARE,
        associatedMachines = listOf(CUB, CUB_AUTO, CUB_SLEEK)
    ),
    Product(
        partNumber = "610003",
        name = "Magnet switch - CUB, CUB Auto, CUB Sleek, TRIDENT, TITAN",
        productType = ProductType.SPARE,
        associatedMachines = listOf(CUB, CUB_AUTO, CUB_SLEEK, TRIDENT, TITAN)
    ),
    Product(
        partNumber = "WRN-0399-02-01-10-0",
        name = "Armature Assy -230V - SUPER",
        productType = ProductType.SPARE,
        associatedMachines = listOf(SUPER)
    ),
    Product(
        partNumber = "WRN-0473-02-01-20-1",
        name = "Armature Assy -230V - TRIDENT",
        productType = ProductType.SPARE,
        associatedMachines = listOf(TRIDENT)
    )
)

val allMockAccessories = mockAccessories + mockArbors + mockAdaptors

val allMockProducts =
    mockDrillingMachines + allMockCutters + mockPilotPins + allMockAccessories + mockSpares
