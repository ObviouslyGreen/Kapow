package syao6_mychen5.ece420.uiuc.kapow;

import android.app.Activity;
import android.os.Bundle;


public class KapowPreferencesActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new KapowPreferencesFragment()).commit();
    }
}
