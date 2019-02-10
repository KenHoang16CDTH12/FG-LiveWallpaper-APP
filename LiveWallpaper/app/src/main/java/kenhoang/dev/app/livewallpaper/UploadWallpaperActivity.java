package kenhoang.dev.app.livewallpaper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import kenhoang.dev.app.livewallpaper.config.Common;
import kenhoang.dev.app.livewallpaper.model.Category;
import kenhoang.dev.app.livewallpaper.model.ComputerVision;
import kenhoang.dev.app.livewallpaper.model.URLUpload;
import kenhoang.dev.app.livewallpaper.model.Wallpaper;
import kenhoang.dev.app.livewallpaper.remote.IComputerVision;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UploadWallpaperActivity extends AppCompatActivity {

    ImageView imgPreview;
    Button btnUpload, btnBrowser, btnSubmit;
    MaterialSpinner spinner;
    // Material Spinner data
    Map<String, String> spinnerData = new HashMap<>();
    private Uri filePath;

    String categoryIdSelected = "", directUrl = "", nameOfFile = "";

    // Firebase
    FirebaseStorage storage;
    StorageReference storageRef;

    IComputerVision mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_wallpaper);
        // Init Retrofit
        mService = Common.getComputerVisionAPI();
        // Firebase Storage init
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        // View
        imgPreview = findViewById(R.id.image_preview);
        btnBrowser = findViewById(R.id.btn_browser);
        btnUpload = findViewById(R.id.btn_upload);
        btnSubmit = findViewById(R.id.btn_submit);
        spinner = findViewById(R.id.spinner);

        // Events
        btnBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner.getSelectedIndex() == 0)
                    Toast.makeText(UploadWallpaperActivity.this, "Please choose category", Toast.LENGTH_SHORT).show();
                else
                    upload();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectAdultContent(directUrl);
            }
        });

        fetchSpinnerCategoryData();
    }

    private void detectAdultContent(final String directUrl) {
        if (directUrl.isEmpty())
            Toast.makeText(this, "Picture not uploaded", Toast.LENGTH_SHORT).show();
        else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Analyzing Image ...");
            progressDialog.show();
            mService.analyzeImage(Common.getAPIAdultEndpoint(), new URLUpload(directUrl))
                    .enqueue(new Callback<ComputerVision>() {
                        @Override
                        public void onResponse(Call<ComputerVision> call, Response<ComputerVision> response) {
                            if (response.isSuccessful()) {
                                progressDialog.dismiss();
                                if (!response.body().getAdult().isAdultContent()) {
                                    // If picture is not contain adult content
                                    // We will save it to our background gallery
                                    saveUrlToCategory(categoryIdSelected, directUrl);
                                } else {
                                    // If url is adult content, we will delete it from our Firebase storage
                                    deleteFileFromStorage(nameOfFile);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ComputerVision> call, Throwable t) {
                            Toast.makeText(UploadWallpaperActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteFileFromStorage(String nameOfFile) {
        storageRef.child("images/" + nameOfFile)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UploadWallpaperActivity.this, "Your image is adult content and will be deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void upload() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading ...");
            progressDialog.show();
            nameOfFile = UUID.randomUUID().toString();
            final StorageReference ref = storageRef.child(new StringBuilder("images/").append(nameOfFile).toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    directUrl = uri.toString();
                                    btnSubmit.setEnabled(true);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadWallpaperActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded: " + (int)progress + "%");
                        }
                    });
        }
    }

    private void saveUrlToCategory(String categoryIdSelected, String url) {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .push() // Gen key
                .setValue(new Wallpaper(url, categoryIdSelected))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UploadWallpaperActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture: "), Common.PICK_IMAGE_REQUEST);
    }

    private void fetchSpinnerCategoryData() {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_CATEGORY_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            Category item = data.getValue(Category.class);
                            String key = data.getKey();
                            spinnerData.put(key, item.getName());
                        }

                        // Because Material Spinner will not receive hit so we need custom hint
                        // This is my tip ^^
                        Object[] valueArray = spinnerData.values().toArray();
                        List<Object> valueList = new ArrayList<>();
                        valueList.add(0, "Category"); // We will first item is hint
                        valueList.addAll(Arrays.asList(valueArray)); // And add all remain category name
                        spinner.setItems(valueList); // Set source data for spinner
                        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                                // When user choose category, we will get categoryId (key)
                                Object[] keyArray = spinnerData.keySet().toArray();
                                List<Object> keyList = new ArrayList<>();
                                keyList.add(0, "Category_Key");
                                keyList.addAll(Arrays.asList(keyArray));
                                categoryIdSelected = keyList.get(position).toString(); // Assign key when User choose category
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgPreview.setImageBitmap(bitmap);
                btnUpload.setEnabled(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        detectAdultContent(directUrl);
        super.onBackPressed();
    }
}
