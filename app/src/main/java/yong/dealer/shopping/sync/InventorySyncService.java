package yong.dealer.shopping.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class InventorySyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static InventorySyncAdapter sInventorySyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SyncService", "onCreate SyncService");
        synchronized (sSyncAdapterLock) {
            if (sInventorySyncAdapter == null) {
                sInventorySyncAdapter = new InventorySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sInventorySyncAdapter.getSyncAdapterBinder();
    }
}
