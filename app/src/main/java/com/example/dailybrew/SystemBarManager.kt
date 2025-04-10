import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object SystemBarManager {
    /**
     * Configure status bar with dark background and white icons
     */
    fun setDarkStatusBar(activity: Activity) {
        activity.window?.apply {
            // Set status bar color
            statusBarColor = Color.parseColor("#5D3A25") // Dark brown

            // For API 30+ (Android 11+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowCompat.setDecorFitsSystemWindows(this, false)
                WindowCompat.getInsetsController(this, decorView).apply {
                    isAppearanceLightStatusBars = false // FALSE = WHITE icons
                    isAppearanceLightNavigationBars = false
                }
            }
            // For API 23-29
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = decorView.systemUiVisibility and
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
}