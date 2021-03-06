package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

  public static final String TAG = "TimeLineActivity";
  private final int REQUEST_CODE = 20;

  TwitterClient client;
  RecyclerView rv_tweets;
  List<Tweet> tweets;
  TweetsAdapter adapter;
  SwipeRefreshLayout swipeContainer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_timeline);

    client = TwitterApp.getRestClient(this);

    swipeContainer = findViewById(R.id.swiperContainer);
    swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
    swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        Log.i("TAG", "Pull to refresh");
        populateHomeTimeline();
      }
    });

    // Find the recycler view
    rv_tweets = findViewById(R.id.rv_tweets);

    // Init the list of tweets
    tweets = new ArrayList<>();
    adapter = new TweetsAdapter(this, tweets);

    // Configure the recyclerview: layoutmanager and adapter
    rv_tweets.setLayoutManager(new LinearLayoutManager(this));
    rv_tweets.setAdapter(adapter);

    populateHomeTimeline();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.compose) {

      // Compose icon is tapped
      Intent intent = new Intent(this, ComposeActivity.class);
      startActivityForResult(intent, REQUEST_CODE);

      // Navigate to the compose activity

    }
    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
      // Get data from intent (tweet object)
      Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));

      // Update recycler view with new tweet


      // Modify data source of tweets
      tweets.add(0, tweet);

      // Update the adapter
      adapter.notifyItemInserted(0);
      rv_tweets.smoothScrollToPosition(0)  ;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void populateHomeTimeline() {
    client.getHomeTimeline(new JsonHttpResponseHandler() {


      @Override
      public void onSuccess(int statusCode, Headers headers, JSON json) {
        Log.i(TAG,"onSuccess " + json.toString());
        JSONArray jsonArray = json.jsonArray;
        try {
          adapter.clear();
          adapter.addAll(Tweet.fromJsonArray(jsonArray));
          swipeContainer.setRefreshing(false);

        } catch (JSONException e) {
          Log.e(TAG, "Json exception", e);
        }
      }

      @Override
      public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
        Log.i(TAG,"onFailure " + response, throwable);
      }
    });
  }
}