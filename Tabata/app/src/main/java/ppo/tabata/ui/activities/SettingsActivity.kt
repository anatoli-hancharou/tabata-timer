package ppo.tabata.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import com.zeugmasolutions.localehelper.Locales
import ppo.tabata.R
import ppo.tabata.viewModels.TabataViewModel
import ppo.tabata.viewModels.TabataViewModelFactory
import ppo.tabata.utility.TabataApp

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
class SettingsActivity : LocaleAwareCompatActivity() {
    private lateinit var settingsFragment: SettingsFragment
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        sharedPreferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        settingsFragment = SettingsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.prefs_content, settingsFragment)
            .commit()
    }

    override fun onResume() {

        val themePreference = settingsFragment.findPreference<Preference>("dark_theme")!!
        themePreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->

            sharedPreferences.edit().putBoolean("dark_theme", newValue as Boolean).apply()
            TabataApp.updateTheme(newValue)
            true
        }

        val fontSizePreference = settingsFragment.findPreference<ListPreference>("text_size")!!
        fontSizePreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->

                var sizeCoefficient = 0f
                when (newValue as String) {
                    "small" -> sizeCoefficient = 0.85f
                    "medium" -> sizeCoefficient = 1.0f
                    "large" -> sizeCoefficient = 1.15f
                }
                resources.configuration.fontScale = sizeCoefficient
                resources.displayMetrics.scaledDensity = resources.configuration.fontScale * resources.displayMetrics.density
                baseContext.resources.updateConfiguration(resources.configuration, DisplayMetrics())
                finish()
                startActivity(Intent(this, this::class.java))
                true
            }

        val localePreference = settingsFragment.findPreference<Preference>("lang")!!
        localePreference.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->

            if(newValue as String == "ru")
                updateLocale(Locales.Russian)
            else
                updateLocale(Locales.English)
            true
        }

        val deleteAllPreference = settingsFragment.findPreference<Preference>("delete_all")!!
        deleteAllPreference.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {

            val alertDialog = AlertDialog.Builder(this)
            val r = resources

            alertDialog.setMessage(r.getString(R.string.dialog_message))

            alertDialog.setPositiveButton(R.string.ok) { _, _ ->
                val tabataViewModel: TabataViewModel by viewModels {
                    TabataViewModelFactory((this.application as TabataApp).repository)
                }
                tabataViewModel.clear()
            }
            alertDialog.setNegativeButton(R.string.no) { _, _ -> }

            alertDialog.show()
            true
        }
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

}