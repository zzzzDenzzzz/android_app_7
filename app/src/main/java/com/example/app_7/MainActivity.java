package com.example.app_7;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_7.databinding.ActivityMainBinding;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String URL_POSTS = "https://jsonplaceholder.typicode.com/posts";

    private ActivityMainBinding binding;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchPosts();
    }

    private void fetchPosts() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(MainActivity.URL_POSTS)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() == null) throw new AssertionError();
                    String responseData = response.body().string();

                    Gson gson = new Gson();
                    Type postListType = new TypeToken<List<Post>>() {}.getType();
                    postList = gson.fromJson(responseData, postListType);

                    runOnUiThread(() -> {
                        postAdapter = new PostAdapter(postList.subList(0, 10));
                        binding.recyclerView.setAdapter(postAdapter);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(String.valueOf(MainActivity.class), "Error: " + e.getMessage());
            }
        });
    }
}