package kenhoang.dev.app.livewallpaper.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kenhoang.dev.app.livewallpaper.ListWallpaperActivity;
import kenhoang.dev.app.livewallpaper.R;
import kenhoang.dev.app.livewallpaper.WallpaperActivity;
import kenhoang.dev.app.livewallpaper.adapter.viewholder.WallpaperViewHolder;
import kenhoang.dev.app.livewallpaper.config.Common;
import kenhoang.dev.app.livewallpaper.helpers.ItemClickListeners;
import kenhoang.dev.app.livewallpaper.model.Wallpaper;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingFragment extends Fragment {

    private static final String TAG = TrendingFragment.class.getName();
    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference databaseRef;

    FirebaseRecyclerOptions<Wallpaper> options;
    FirebaseRecyclerAdapter<Wallpaper, WallpaperViewHolder> adapter;

    private static TrendingFragment INSTANCE = null;

    public static TrendingFragment getInstance() {
        if (INSTANCE == null)
            INSTANCE = new TrendingFragment();
        return INSTANCE;
    }

    public TrendingFragment() {
        // Init firebase
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference(Common.STR_WALLPAPER);

        Query query = databaseRef.orderByChild("viewCount")
                .limitToLast(10); // get 10 item have biggest viewCount

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
                        Intent intent = new Intent(getActivity(), WallpaperActivity.class);
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_popular, container, false);
        recyclerView = view.findViewById(R.id.recycler_trending);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // Because Firebase return asc sort list so we need reverse RecyclerView to show largest item is first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        fetchTrending();

        return view;
    }

    private void fetchTrending() {
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

}
