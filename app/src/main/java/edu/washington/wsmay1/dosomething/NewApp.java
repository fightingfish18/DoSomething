package edu.washington.wsmay1.dosomething;

import android.app.Application;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

/**
 * Created by Smyth on 5/26/2015.
 */
public class NewApp extends Application {
    private MobileServiceClient client;

    public void setClient(MobileServiceClient client) {
        this.client = client;
    }

    public MobileServiceClient getClient() {
        return this.client;
    }
}
