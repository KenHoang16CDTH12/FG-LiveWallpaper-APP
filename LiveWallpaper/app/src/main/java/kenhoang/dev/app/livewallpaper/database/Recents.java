package kenhoang.dev.app.livewallpaper.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "recents", primaryKeys = {"imageUrl",  "categoryId"})
public class Recents {
    @ColumnInfo(name = "imageUrl")
    @NonNull
    private String imageUrl;

    @ColumnInfo(name = "categoryId")
    @NonNull
    private String categoryId;

    @ColumnInfo(name = "saveTime")
    private String saveTime;

    @ColumnInfo(name = "key")
    private String key;

    public Recents(@NonNull String imageUrl, @NonNull String categoryId, String saveTime, String key) {
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.saveTime = saveTime;
        this.key = key;
    }

    @NonNull
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@NonNull String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NonNull String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
