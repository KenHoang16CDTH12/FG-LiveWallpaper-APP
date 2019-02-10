package kenhoang.dev.app.livewallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kenhoang.dev.app.livewallpaper.adapter.viewholder.WallpaperViewHolder;
import kenhoang.dev.app.livewallpaper.config.Common;
import kenhoang.dev.app.livewallpaper.helpers.ItemClickListeners;
import kenhoang.dev.app.livewallpaper.model.Wallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ListWallpaperActivity extends AppCompatActivity {

    private static final String TAG = ListWallpaperActivity.class.getName();

    Query query;
    FirebaseRecyclerOptions<Wallpaper> options;
    FirebaseRecyclerAdapter<Wallpaper, WallpaperViewHolder> adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wallpaper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Common.CATEGORY_SELECTED);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recycler_list_wallpaper);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        fetchWallpapers();
    }

    private void fetchWallpapers() {
        query = FirebaseDatabase.getInstance().getReference(Common.STR_WALLPAPER)
                    .orderByChild("categoryId").equalTo(Common.CATEGORY_ID_SELECTED);
        options = new FirebaseRecyclerOptions.Builder<Wallpaper>()
                    .setQuery(query, Wallpaper.class)
                    .build();

        adapter = new FirebaseRecyclerAdapter<Wallpaper, WallpaperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final WallpaperViewHolder wallpaperViewHolder, int i, @NonNull final Wallpaper wallpaper) {
                Picasso.get()
                        .load(wallpaper.getImageUrl())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(wallpaperViewHolder.image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                // Try again online if cache failed
                                Picasso.get()
                                        .load(wallpaper.getImageUrl())
                                        .error(R.drawable.ic_terrain_black_24dp)
                                        .into(wallpaperViewHolder.image, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Log.e(TAG, "onError: ", e);
                                            }
                                        });
                            }
                        });

                wallpaperViewHolder.setItemClickListeners(new ItemClickListeners() {
                    @Override
                    public void onClick(View view, int position) {
                        Common.selecte_background = wallpaper;
                        Common.select_background_key = adapter.getRef(position).getKey();
                        Intent intent = new Intent(ListWallpaperActivity.this, WallpaperActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false);
                int height = parent.getMeasuredHeight() / 2;
                itemView.setMinimumHeight(height);
                return new WallpaperViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish(); // Close activity when click back button
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
