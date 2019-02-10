package kenhoang.dev.app.livewallpaper.database.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import kenhoang.dev.app.livewallpaper.database.Recents;

import static kenhoang.dev.app.livewallpaper.database.local.LocalDatabase.DATABASE_VERSION;

@Database(entities = Recents.class, version = DATABASE_VERSION)
public abstract class LocalDatabase extends RoomDatabase {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FGDevLiveWallpaperDB";

    public abstract RecentsDAO recentsDAO();

    private static LocalDatabase instance;

    public static LocalDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, LocalDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
