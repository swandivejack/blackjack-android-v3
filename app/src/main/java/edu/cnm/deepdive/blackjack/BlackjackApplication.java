package edu.cnm.deepdive.blackjack;

import android.app.Application;
import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;
import edu.cnm.deepdive.blackjack.service.BlackjackDatabase;

public class BlackjackApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
    Picasso.setSingletonInstance(
        new Picasso.Builder(this)
            .loggingEnabled(true)
            .build()
    );
    BlackjackDatabase.setApplicationContext(this);
    final BlackjackDatabase database = BlackjackDatabase.getInstance();
    new Thread(new Runnable() {
      @Override
      public void run() {
        database.getShoeDao().delete();
      }
    }).start();
  }

}
