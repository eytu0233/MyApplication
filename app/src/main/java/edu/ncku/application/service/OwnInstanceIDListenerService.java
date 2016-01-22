package edu.ncku.application.service;

/**
 * Created by NCKU on 2016/1/5.
 */
import android.content.Intent;

public class OwnInstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService {

    private static final String DEBUG_FLAG = OwnInstanceIDListenerService.class.getName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
    // [END refresh_token]
}
