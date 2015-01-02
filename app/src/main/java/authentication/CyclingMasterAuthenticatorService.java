package authentication;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CyclingMasterAuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {

        CyclingMasterAuthenticator authenticator = new CyclingMasterAuthenticator(this);
        return authenticator.getIBinder();
    }
}
