package kenhoang.dev.app.livewallpaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kenhoang.dev.app.livewallpaper.ListWallpaperActivity;
import kenhoang.dev.app.livewallpaper.R;
import kenhoang.dev.app.livewallpaper.WallpaperActivity;
import kenhoang.dev.app.livewallpaper.adapter.viewholder.WallpaperViewHolder;
import kenhoang.dev.app.livewallpaper.config.Common;
import kenhoang.dev.app.livewallpaper.database.Recents;
import kenhoang.dev.app.livewallpaper.helpers.ItemClickListeners;
import kenhoang.dev.app.livewallpaper.model.Wallpaper;

public class RecentAdapter extends RecyclerView.Adapter<WallpaperViewHolder> {

    private Context context;
    private List<Recents> recents;

    public RecentAdapter(Context context, List<Recents> recents) {
        this.context = context;
        this.recents = recents;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false);
        int height = parent.getMeasuredHeight() / 2;
        itemView.setMinimumHeight(height);
        return new WallpaperViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final WallpaperViewHolder holder, final int position) {
        Picasso.get()
                .load(recents.get(position).getImageUrl())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        // Try again online if cache failed
                        Picasso.get()
                                .load(recents.get(position).getImageUrl())
                                .error(R.drawable.ic_terrain_black_24dp)
                                .into(holder.image, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e("ERROR", "onError: ", e);
                                    }
                                });
                    }
                });

        holder.setItemClickListeners(new ItemClickListeners() {
            @Override
            public void onClick(View view, int position) {
                Common.selecte_background = new Wallpaper(recents.get(position).getImageUrl(), recents.get(position).getCategoryId());
                Common.select_background_key = recents.get(position).getKey();
                Intent intent = new Intent(context, WallpaperActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recents.size();
    }
}
