package syao6_mychen5.ece420.uiuc.kapow;

import android.os.Bundle;
import android.preference.PreferenceFragment;


/**
 * A placeholder fragment containing a simple view.
 */
public class KapowPreferencesFragment extends PreferenceFragment
{

    public KapowPreferencesFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
