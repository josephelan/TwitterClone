package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

  public String name;
  public String screen_name;
  public String public_image_url;

  public User(){
  }

  public static User fromJson(JSONObject jsonObject) throws JSONException {
    User user = new User();
    user.name = jsonObject.getString("name");
    user.screen_name = jsonObject.getString("screen_name");
    user.public_image_url = jsonObject.getString("profile_image_url_https");
    return user;
  }
}
