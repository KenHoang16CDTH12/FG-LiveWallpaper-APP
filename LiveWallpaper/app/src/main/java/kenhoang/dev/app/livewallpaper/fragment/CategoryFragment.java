package kenhoang.dev.app.livewallpaper.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kenhoang.dev.app.livewallpaper.ListWallpaperActivity;
import kenhoang.dev.app.livewallpaper.R;
import kenhoang.dev.app.livewallpaper.adapter.viewholder.CategoryViewHolder;
import kenhoang.dev.app.livewallpaper.config.Common;
import kenhoang.dev.app.livewallpaper.helpers.ItemClickListeners;
import kenhoang.dev.app.livewallpaper.model.Category;

public class CategoryFragment extends Fragment {

    private static final String TAG = CategoryFragment.class.getName();
    // FireBase
    FirebaseDatabase database;
    DatabaseReference categoryRef;
    // FireBase UI adapter
    FirebaseRecyclerOptions<Category> options;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;
    // View
    RecyclerView recyclerView;

    private static CategoryFragment INSTANCE = null;

    public static CategoryFragment getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CategoryFragment();
        return INSTANCE;
    }


    public CategoryFragment() {
        // Requires empty public constructor
        database = FirebaseDatabase.getInstance();
        categoryRef = database.getReference(Common.STR_CATEGORY_REF);

        options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(categoryRef, Category.class) // Select all
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CategoryViewHolder categoryViewHolder, int i, @NonNull final Category category) {
                categoryViewHolder.category_name.setText(category.getName());
                Picasso.get()
                        .load(category.getImageLink())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(categoryViewHolder.category_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                // Try again online if cache failed
                                Picasso.get()
                                        .load(category.getImageLink())
                                        .error(R.drawable.ic_terrain_black_24dp)
                                        .into(categoryViewHolder.category_image, new Callback() {
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
                categoryViewHolder.setItemClickListeners(new ItemClickListeners() {
                    @Override
                    public void onClick(View view, int position) {
                        Common.CATEGORY_ID_SELECTED = adapter.getRef(position).getKey(); // Get Key of item
                        Common.CATEGORY_SELECTED = category.getName();
                        Intent intent = new Intent(getActivity(), ListWallpaperActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_category, parent, false);
                return new CategoryViewHolder(itemView);
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recycler_category);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        fetchCategory();

        return view;
    }

    private void fetchCategory() {
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
