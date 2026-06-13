package swiss.avm.poschti

import android.app.Application
import swiss.avm.poschti.data.local.PoschtiDatabase
import swiss.avm.poschti.data.repository.PoschtiRepository

/**
 * Hält die App-weiten Singletons (Datenbank, Repository).
 * Bewusst schlank gehalten – ein DI-Framework (z.B. Hilt) kann später folgen.
 */
class PoschtiApplication : Application() {

    val database: PoschtiDatabase by lazy { PoschtiDatabase.get(this) }
    val repository: PoschtiRepository by lazy { PoschtiRepository(database) }
}
