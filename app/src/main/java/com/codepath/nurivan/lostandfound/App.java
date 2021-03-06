package com.codepath.nurivan.lostandfound;

import android.app.Application;

import com.codepath.nurivan.lostandfound.models.FoundItem;
import com.codepath.nurivan.lostandfound.models.LostItem;
import com.codepath.nurivan.lostandfound.models.Match;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(LostItem.class);
        ParseObject.registerSubclass(FoundItem.class);
        ParseObject.registerSubclass(Match.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "174768931081");
        installation.saveInBackground();
    }
}
