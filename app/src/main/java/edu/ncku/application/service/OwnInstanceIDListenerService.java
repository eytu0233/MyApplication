package edu.ncku.application.service;

/**
 * 此類別是官網範例中的一個Service，當DeviceID(token)
 * 發生改變時，在背景向GCM Server重新註冊
 */
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
        RegistrationIntentService.startActionRegisterGCM(this);
    }
    // [END refresh_token]
}
