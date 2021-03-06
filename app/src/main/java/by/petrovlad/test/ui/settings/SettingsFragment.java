package by.petrovlad.test.ui.settings;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Locale;

import by.petrovlad.test.Constants;
import by.petrovlad.test.FontSettings;
import by.petrovlad.test.ui.activity.ChangeFontActivity;
import by.petrovlad.test.ui.activity.ChangeLanguageActivity;
import by.petrovlad.test.ui.activity.EditAccountActivity;
import by.petrovlad.test.R;
import by.petrovlad.test.StringGenerator;
import by.petrovlad.test.Upload;
import by.petrovlad.test.ui.activity.SetLocationActivity;
import by.petrovlad.test.ui.activity.SignUpActivity;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private Button btnUploadImage;
    private Button btnUploadVideo;
    private Button btnLogOut;
    private Button btnEditAccount;
    private Button btnEditLocation;
    private Button btnChangeLanguage;
    private Button btnFontSettings;

    private ProgressBar progressBar;

    private SettingsViewModel settingsViewModel;
    private Uri fileUri;


    // or create 4 variables e.g. 'storageImagesReference' 'dbImagesReference' 'storageVideosReference' 'dbVideosReference'?
    private FirebaseStorage storageInstance;
    private FirebaseDatabase databaseInstance;
    private FirebaseAuth firebaseAuth;

    private Typeface typeface;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        init(root);
        return root;
    }

    private void init(View view) {
        typeface = ResourcesCompat.getFont(getContext(), FontSettings.fontId);

        btnFontSettings = view.findViewById(R.id.btnFontSettings);
        btnFontSettings.setTextSize(FontSettings.fontSize);
        btnFontSettings.setTypeface(typeface);
        btnFontSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangeFontActivity.class));
            }
        });

        btnChangeLanguage = view.findViewById(R.id.btnChangeLanguage);
        btnChangeLanguage.setTextSize(FontSettings.fontSize);
        btnChangeLanguage.setTypeface(typeface);
        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangeLanguageActivity.class);
                startActivity(intent);
            }
        });

        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        btnUploadImage.setTextSize(FontSettings.fontSize);
        btnUploadImage.setTypeface(typeface);
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser("image/*", Constants.PICK_IMAGE_REQUEST);
            }
        });

        btnUploadVideo = view.findViewById(R.id.btnUploadVideo);
        btnUploadVideo.setTextSize(FontSettings.fontSize);
        btnUploadVideo.setTypeface(typeface);
        btnUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser("video/*", Constants.PICK_VIDEO_REQUEST);
            }
        });

        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnLogOut.setTextSize(FontSettings.fontSize);
        btnLogOut.setTypeface(typeface);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SignUpActivity.class));
            }
        });

        btnEditAccount = view.findViewById(R.id.btnEditAccount);
        btnEditAccount.setTextSize(FontSettings.fontSize);
        btnEditAccount.setTypeface(typeface);
        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditAccountActivity.class);
                startActivity(intent);
            }
        });

        progressBar = view.findViewById(R.id.pgLoading);
        progressBar.setVisibility(View.INVISIBLE);

        storageInstance = FirebaseStorage.getInstance();
        databaseInstance = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void openFileChooser(String type, int requestCode) {
        Intent intent = new Intent();
        intent.setType(type);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            fileUri = data.getData();
            uploadFile(requestCode);

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(int requestCode) {
        if (fileUri != null) {
            String referencePath;
            switch (requestCode) {
                case Constants.PICK_IMAGE_REQUEST : {
                    referencePath = Constants.FIREBASE_IMAGES_REFERENCE;
                    break;
                }
                case Constants.PICK_VIDEO_REQUEST: {
                    referencePath = Constants.FIREBASE_VIDEOS_REFERENCE;
                    break;
                }
                default:
                    return;
            }

            progressBar.setVisibility(View.VISIBLE);
            btnUploadVideo.setEnabled(false);
            btnUploadImage.setEnabled(false);

            long currTime = System.currentTimeMillis();
            StorageReference file = storageInstance.getReference(referencePath)
                    .child(firebaseAuth.getCurrentUser().getUid() + "/" + currTime + "." + getFileExtension(fileUri));

            UploadTask uploadTask = file.putFile(fileUri);
            Task<Uri> continueTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    return file.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    progressBar.setVisibility(View.INVISIBLE);
                    btnUploadVideo.setEnabled(true);
                    btnUploadImage.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsFragment.this.getActivity(), R.string.toast_file_uploaded, Toast.LENGTH_SHORT).show();

                        Upload upload = new Upload(StringGenerator.generateString(10), task.getResult().toString());
                        databaseInstance.getReference(referencePath).child(firebaseAuth.getCurrentUser().getUid()).child(Long.toString(currTime)).setValue(upload);

                    } else {
                        Log.w("SettFrag.uploadFile:", task.getException().getMessage());
                        Toast.makeText(SettingsFragment.this.getActivity(), R.string.toast_file_not_uploaded, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(SettingsFragment.this.getActivity(), R.string.toast_no_photo_selected, Toast.LENGTH_SHORT).show();
        }
    }


    public void showToast(String text) {
        Toast.makeText(SettingsFragment.this.getActivity(), text, Toast.LENGTH_SHORT).show();
    }

}