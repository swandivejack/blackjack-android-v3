package edu.cnm.deepdive.blackjack.service;

import android.content.Context;
import edu.cnm.deepdive.blackjack.BuildConfig;
import edu.cnm.deepdive.blackjack.R;
import edu.cnm.deepdive.blackjack.model.entity.Card;
import edu.cnm.deepdive.blackjack.model.entity.Shoe;
import edu.cnm.deepdive.blackjack.model.pojo.Draw;
import io.reactivex.Single;
import java.net.MalformedURLException;
import java.net.URL;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeckOfCardsService {

  @GET("api/deck/new/shuffle/")
  Single<Shoe> newShoe(@Query("deck_count") int count);

  @GET("api/deck/{shoeKey}/draw/")
  Single<Draw> draw(@Path("shoeKey") String shoeKey, @Query("count") int count); // Specify type parameter

  @GET("api/deck/{shoeKey}/shuffle/")
  Single<Shoe> shuffle(@Path("shoeKey") String shoeKey);

  default URL getImageUrl(Card card) {
    try {
      String baseUrl = BuildConfig.BASE_URL;
      String imagePattern = BuildConfig.STATIC_IMAGE_PATTERN;
      String abbreviation = card.getAbbreviation();
      return new URL(String.format(imagePattern, baseUrl, abbreviation));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  static DeckOfCardsService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  class InstanceHolder {

    private static final DeckOfCardsService INSTANCE;

    static {
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(Level.BODY);
      OkHttpClient client = new OkHttpClient.Builder()
          .addInterceptor(interceptor)
          .build();
      Retrofit retrofit = new Retrofit.Builder()
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .baseUrl(BuildConfig.BASE_URL)
          .client(client)
          .build();
      INSTANCE = retrofit.create(DeckOfCardsService.class);
    }

  }

}

