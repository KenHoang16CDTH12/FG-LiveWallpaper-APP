package kenhoang.dev.app.livewallpaper.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import kenhoang.dev.app.livewallpaper.R;
import kenhoang.dev.app.livewallpaper.helpers.ItemClickListeners;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView category_name;
    public ImageView category_image;

    private ItemClickListeners itemClickListeners;

    public void setItemClickListeners(ItemClickListeners itemClickListeners) {
        this.itemClickListeners = itemClickListeners;
    }

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        category_name = itemView.findViewById(R.id.name);
        category_image = itemView.findViewById(R.id.image);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListeners.onClick(view, getAdapterPosition());
    }
}
