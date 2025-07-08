package net.broachcutter.vendorapp.migrationtest

/*
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import net.broachcutter.vendorapp.db.DatabaseMigration.MIGRATION_4_5
import net.broachcutter.vendorapp.models.Product
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class BroachCutterDatabaseMigrationTest {
    private val TEST_DB = "broach_cutter_migration-test"


    @Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        Product::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate4To5() {
        var db = helper.createDatabase(TEST_DB, 4).apply {
            execSQL("""INSERT INTO Product VALUES ('123456','Test123','Description',2,'testimage',2,2,18.0,)""".trimIndent())
            close()
        }
        db = helper.runMigrationsAndValidate(TEST_DB, 5, true, MIGRATION_4_5)
    }
}*/
