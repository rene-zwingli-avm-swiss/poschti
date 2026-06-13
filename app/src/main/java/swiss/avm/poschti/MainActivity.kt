package swiss.avm.poschti

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import swiss.avm.poschti.ui.PoschtiApp
import swiss.avm.poschti.ui.theme.PoschtiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PoschtiTheme {
                PoschtiApp()
            }
        }
    }
}
