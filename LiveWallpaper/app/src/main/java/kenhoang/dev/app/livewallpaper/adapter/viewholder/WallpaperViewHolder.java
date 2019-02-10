package kenhoang.dev.app.livewallpaper.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kenhoang.dev.app.livewallpaper.R;
import kenhoang.dev.app.livewallpaper.helpers.ItemClickListeners;

public class WallpaperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ItemClickListeners itemClickListeners;

    public ImageView image;

    public void setItemClickListeners(ItemClickListeners itemClickListeners) {
        this.itemClickListeners = itemClickListeners;
    }

    public WallpaperViewHolder(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListeners.onClick(view, getAdapterPosition());
    }
}
