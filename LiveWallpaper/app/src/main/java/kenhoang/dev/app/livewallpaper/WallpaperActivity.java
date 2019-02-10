 package kenhoang.dev.app.livewallpaper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kenhoang.dev.app.livewallpaper.config.Common;
import kenhoang.dev.app.livewallpaper.database.Recents;
import kenhoang.dev.app.livewallpaper.database.local.LocalDatabase;
import kenhoang.dev.app.livewallpaper.database.local.RecentsDataSource;
import kenhoang.dev.app.livewallpaper.database.source.RecentsRepository;
import kenhoang.dev.app.livewallpaper.helpers.SaveImageHelper;
import kenhoang.dev.app.livewallpaper.model.Wallpaper;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WallpaperActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fabSetWallpaper;
    FloatingActionButton fabDownloadWallpaper;
    ImageView image;
    CoordinatorLayout rootLayout;
    FloatingActionMenu floatingActionMenu;
    com.github.clans.fab.FloatingActionButton fabShare;

    // RoomDatabase
    CompositeDisposable compositeDisposable;
    RecentsRepository recentsRepository;
    // Facebook
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(bitmap);
                Snackbar.make(rootLayout, "Wallpaper was set success", Snackbar.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private Target facebookConvertBitMap = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Init Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // Init RoomDatabase
        compositeDisposable = new CompositeDisposable();
        LocalDatabase database = LocalDatabase.getInstance(this);
        recentsRepository = RecentsRepository.getInstance(RecentsDataSource.getInstance(database.recentsDAO()));


        // Init
        rootLayout = findViewById(R.id.rootLayout);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        collapsingToolbarLayout.setTitle(Common.CATEGORY_SELECTED);

        image = findViewById(R.id.imgThumb);
        Picasso.get()
                .load(Common.selecte_background.getImageUrl())
                .into(image);

        floatingActionMenu = findViewById(R.id.menu);
        fabShare = findViewById(R.id.fabShare);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create callback
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(WallpaperActivity.this, "Share successful!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(WallpaperActivity.this, "Share canceled!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(WallpaperActivity.this, "Share error " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                // We will fetch photo from link and convert to bitmap
                Picasso.get()
                        .load(Common.selecte_background.getImageUrl())
                        .into(facebookConvertBitMap);
            }
        });

        // add to recents
        addToRecents();

        fabSetWallpaper = findViewById(R.id.fabSetWallpaper);
        fabDownloadWallpaper = findViewById(R.id.fabDownloadWallpaper);

        fabSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.get()
                        .load(Common.selecte_background.getImageUrl())
                        .into(target);

            }
        });

        fabDownloadWallpaper.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // Check permission
                // Request runtime permission
                if (ActivityCompat.checkSelfPermission(WallpaperActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, Common.PERMISSION_REQUEST_CODE);
                } else {
                    AlertDialog dialog = new SpotsDialog.Builder().setContext(WallpaperActivity.this).build();
                    dialog.show();
                    dialog.setMessage("Please waiting ...");
                    String fileName = UUID.randomUUID().toString() + ".png";
                    Picasso.get()
                            .load(Common.selecte_background.getImageUrl())
                            .into(new SaveImageHelper(getBaseContext(), dialog, getApplicationContext().getContentResolver(),fileName, "FGDev LiveWallpaper"));
                }
            }
        });

        // View count
        increaseViewCount();
    }

    private void increaseViewCount() {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .child(Common.select_background_key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("viewCount")) {
                            Wallpaper wallpaper = dataSnapshot.getValue(Wallpaper.class);
                            long count = wallpaper.getViewCount() + 1;
                            // Update
                            Map<String, Object> update_view = new HashMap<>();
                            update_view.put("viewCount", count);

                            FirebaseDatabase.getInstance()
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.select_background_key)
                                    .updateChildren(update_view)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(WallpaperActivity.this, "Cannot update view count", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else { // If view count is not set default
                            // Update
                            Map<String, Object> update_view = new HashMap<>();
                            update_view.put("viewCount", Long.valueOf(1));

                            FirebaseDatabase.getInstance()
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.select_background_key)
                                    .updateChildren(update_view)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(WallpaperActivity.this, "Cannot set default view count", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void addToRecents() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                Recents recents = new Recents(
                        Common.selecte_background.getImageUrl(),
                        Common.selecte_background.getCategoryId(),
                        String.valueOf(System.currentTimeMillis()),
                        Common.select_background_key
                );
                recentsRepository.insertRecents(recents);
                emitter.onComplete();
            }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e("ERROR", "accept: " + throwable.getMessage());
            }
        }, new Action() {
            @Override
            public void run() throws Exception {

            }
        });

        compositeDisposable.add(disposable);
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

    @Override
    protected void onDestroy() {
        Picasso.get().cancelRequest(target);
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Common.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog dialog = new SpotsDialog.Builder().setContext(WallpaperActivity.this).build();
                    dialog.show();
                    dialog.setMessage("Please waiting ...");
                    String fileName = UUID.randomUUID().toString() + ".png";
                    Picasso.get()
                            .load(Common.selecte_background.getImageUrl())
                            .into(new SaveImageHelper(getBaseContext(), dialog, getApplicationContext().getContentResolver(),fileName, "FGDev LiveWallpaper"));
                } else
                    Toast.makeText(this, "You need accept this permission to download image", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
