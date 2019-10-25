package edu.cnm.deepdive.blackjack;

import android.app.Application;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;
import edu.cnm.deepdive.blackjack.service.BlackjackDatabase;
import edu.cnm.deepdive.blackjack.service.GoogleSignInService;

public class BlackjackApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    GoogleSignInService.setApplicationContext(this);
    Stetho.initializeWithDefaults(this);
    Picasso.setSingletonInstance(
        new Picasso.Builder(this)
            .loggingEnabled(true) // Disable for production
            .build()
    );
    BlackjackDatabase.setApplicationContext(this);
    BlackjackDatabase database = BlackjackDatabase.getInstance();
    new Thread(new Runnable() {
      @Override
      public void run() {
        database.getShoeDao().delete();
      }
    }).start();
  }

}
