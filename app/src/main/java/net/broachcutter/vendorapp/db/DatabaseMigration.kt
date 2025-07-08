package net.broachcutter.vendorapp.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Note after adding Database Migration here,
 * Please don't forget to increase the version of database in AppDatabase class
 */
@SuppressWarnings("MagicNumber", "MaxLineLength")
object DatabaseMigration {

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE UpdatedOrder ADD COLUMN trackingDetails TEXT DEFAULT NULL")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE UpdatedOrder ADD COLUMN docDueDate INTEGER DEFAULT NULL")
        }
    }

    @Suppress("MaxLineLength")
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `CartItemTemporary` (`part_number_primary` TEXT NOT NULL, `quantity` INTEGER NOT NULL, `unitPrice` TEXT, `selectedPaymentTerm` TEXT, `paymentTerms` TEXT, `partNumber` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `productType` INTEGER NOT NULL DEFAULT -1, `imageUrl` TEXT, `taxRate` REAL NOT NULL, `cutterType` INTEGER, `depthOfCut` INTEGER, `diameter` REAL, `cutterMaterial` INTEGER, `pilotPinType` TEXT, `associatedMachines` TEXT, PRIMARY KEY(`part_number_primary`))")

            database.execSQL("INSERT INTO CartItemTemporary(part_number_primary,quantity,unitPrice,selectedPaymentTerm,paymentTerms,partNumber,name,description,productType,imageUrl,taxRate,cutterType,depthOfCut,diameter,cutterMaterial,pilotPinType,associatedMachines) SELECT part_number_primary,quantity,unitPrice,selectedPaymentTerm,paymentTerms,partNumber,name,description,productType,imageUrl,taxRate,cutterType,depthOfCut,diameter,cutterMaterial,pilotPinType,associatedMachines FROM CartItem")

            database.execSQL("DROP TABLE CartItem")

            database.execSQL("ALTER TABLE CartItemTemporary RENAME TO CartItem")

            database.execSQL("CREATE TABLE IF NOT EXISTS `ProductTemporary` (`partNumber` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `productType` INTEGER NOT NULL DEFAULT -1, `imageUrl` TEXT, `taxRate` REAL NOT NULL, `cutterType` INTEGER, `depthOfCut` INTEGER, `diameter` REAL, `cutterMaterial` INTEGER, `pilotPinType` TEXT, `associatedMachines` TEXT, PRIMARY KEY(`partNumber`))")

            database.execSQL("INSERT INTO ProductTemporary(partNumber,name,description,productType,imageUrl,taxRate,cutterType,depthOfCut,diameter,cutterMaterial,pilotPinType,associatedMachines) SELECT partNumber,name,description,productType,imageUrl,taxRate,cutterType,depthOfCut,diameter,cutterMaterial,pilotPinType,associatedMachines FROM Product")

            database.execSQL("DROP TABLE Product")

            database.execSQL("ALTER TABLE ProductTemporary RENAME TO Product")
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE UpdatedOrder ADD COLUMN couponDiscount DOUBLE DEFAULT NULL")
        }
    }

    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DELETE FROM Product")
        }
    }
}
