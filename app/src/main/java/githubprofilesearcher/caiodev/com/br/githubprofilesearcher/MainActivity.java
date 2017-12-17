package githubprofilesearcher.caiodev.com.br.githubprofilesearcher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //Attributes to save and restore the app's instance and its content
    private static final String USER_PROFILE_IMAGE = "UserProfileImage";
    private static final String USER_NAME = "UserName";
    private static final String USER_BIO = "UserBio";
    private static final String USER_FOLLOWERS = "UserFollowers";
    private static final String USER_REPOS = "UserRepos";
    //Attributes
    private final String BASE_URL_ADDRESS = "https://api.github.com/users/";
    private final String GITHUB_USER_PAGE = "https://github.com/";
    //Views
    @BindView(R.id.search_profile)
    protected EditText searchProfile;
    //UserRepoViews
    @BindView(R.id.card_user_info)
    protected CardView userCard;
    @BindView(R.id.user_avatar)
    protected ImageView userAvatar;
    @BindView(R.id.user_name)
    protected TextView userName;
    @BindView(R.id.user_bio)
    protected TextView userBio;
    @BindView(R.id.user_folowers)
    protected TextView userFollowers;
    @BindView(R.id.user_repos)
    protected TextView userRepos;
    private boolean isUserInfoLoaded = false;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        searchProfile.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    run();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserInfoLoaded) {
                    openRepoPage(GITHUB_USER_PAGE.concat(searchProfile.getText().toString()));

                } else {
                    toastMaker(getString(R.string.empty_card_view_error));
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        BitmapDrawable drawable = (BitmapDrawable) userAvatar.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        // Save the state of item position
        outState.putParcelable(USER_PROFILE_IMAGE, bitmap);
        outState.putString(USER_NAME, userName.getText().toString());
        outState.putString(USER_BIO, userBio.getText().toString());
        outState.putString(USER_FOLLOWERS, userFollowers.getText().toString());
        outState.putString(USER_REPOS, userRepos.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Read the state of item position
        userAvatar.setImageBitmap((Bitmap) savedInstanceState.getParcelable(USER_PROFILE_IMAGE));
        userName.setText(savedInstanceState.getString(USER_NAME));
        userBio.setText(savedInstanceState.getString(USER_BIO));
        userFollowers.setText(savedInstanceState.getString(USER_FOLLOWERS));
        userRepos.setText(savedInstanceState.getString(USER_REPOS));
    }

    //This method gets the text inserted into the mSearchProfile EditText, besides,
    //it checks first, if there is internet connection and second, if the EditText is empty, if these two
    //conditions are satisfied, the EditText's content will be obtained
    void run() {
        if (isNetworkAvailable()) {
            if (!isFieldEmpty(searchProfile)) {
                fillProfileInfo(searchProfile.getText().toString());
            }
        } else {
            toastMaker(getString(R.string.no_connection_error));
        }
    }

    //This method fills the requested github profile info
    void fillProfileInfo(String profile) {
        String requestUrl = BASE_URL_ADDRESS.concat(profile);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(requestUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("getProfileInfo", "FAIL");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String jsonData = response.body().string();
                Log.i("getProfileInfo", jsonData);

                if (response.isSuccessful()) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                JSONObject rootObj = new JSONObject(jsonData);

                                userName.setText(rootObj.getString(getString(R.string.login_alias)));

                                if (rootObj.getString(getString(R.string.bio_alias)).equals(getString(R.string.null_string))) {
                                    userBio.setText(getString(R.string.null_bio_message));

                                } else {
                                    userBio.setText(rootObj.getString(getString(R.string.bio_alias)));
                                }

                                if (rootObj.getInt(getString(R.string.followers_alias)) <= 1) {
                                    userFollowers.setText(getString(R.string.number_of_followers_passing_attributes,
                                            rootObj.getInt(getString(R.string.followers_alias)),
                                            getString(R.string.number_of_followers_singular)));

                                } else {
                                    userFollowers.setText(getString(R.string.number_of_followers_passing_attributes,
                                            rootObj.getInt(getString(R.string.followers_alias)),
                                            getString(R.string.number_of_followers_plural)));
                                }

                                if (rootObj.getInt(getString(R.string.public_repos_alias)) <= 1) {
                                    userRepos.setText(getString(R.string.number_of_repos_passing_attributes,
                                            rootObj.getInt(getString(R.string.public_repos_alias)),
                                            getString(R.string.number_of_repos_singular)));

                                } else {
                                    userRepos.setText(getString(R.string.number_of_repos_passing_attributes,
                                            rootObj.getInt(getString(R.string.public_repos_alias)),
                                            getString(R.string.number_of_repos_plural)));
                                }

                                try {
                                    Glide.with(MainActivity.this)
                                            .load(rootObj.getString(getString(R.string.avatar_image_url)))
                                            .asBitmap()
                                            .fitCenter()
                                            .into(userAvatar);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                userCard.setVisibility(View.VISIBLE);
                                fillRepoInfo(rootObj.getString(getString(R.string.login_alias)));

                                isUserInfoLoaded = true;

                            } catch (JSONException e) {
                                userCard.setVisibility(View.GONE);
                            }
                        }

                    });
                }
            }
        });
    }

    //This method fills the requested github Repo information as Repo name, repo URL and so on
    void fillRepoInfo(String userName) {

        String requestUrl = BASE_URL_ADDRESS.concat(userName).concat("/repos");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(requestUrl)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("getProfileInfo", "FAIL");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    private void openRepoPage(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }

    //This method checks if there is an empty field based on the EditText object received
    boolean isFieldEmpty(EditText textField) {

        if (textField.getText().toString().equals("")) {
            textField.setError(getString(R.string.empty_field_error));
            return true;
        } else {
            return false;
        }
    }

    //It checks whether internet connection is available or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchProfile.getApplicationWindowToken(), 0);
    }

    //It pops up a message received as parameter
    void toastMaker(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }
}