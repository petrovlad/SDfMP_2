package by.petrovlad.test.ui.settings;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.nio.charset.Charset;
import java.util.Random;

import by.petrovlad.test.Constants;
import by.petrovlad.test.MainActivity;
import by.petrovlad.test.R;
import by.petrovlad.test.StringGenerator;
import by.petrovlad.test.Upload;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private Button btnUploadImage;
    private SettingsViewModel settingsViewModel;
    private Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        init(root);
        return root;
    }

    private void init(View view) {
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference(Constants.FIREBASE_IMAGE_REFERENCE);
        databaseReference = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_IMAGE_REFERENCE);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constants.PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            imageUri = data.getData();

            uploadFile();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            long currTime = System.currentTimeMillis();
            StorageReference file = storageReference.child(firebaseAuth.getCurrentUser().getUid() + "/" + currTime + "." + getFileExtension(imageUri));
            //StorageReference file = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            UploadTask uploadTask = file.putFile(imageUri);
            Task<Uri> continueTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    return file.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsFragment.this.getActivity(), R.string.toast_photo_uploaded, Toast.LENGTH_SHORT).show();

                        Upload upload = new Upload(StringGenerator.generateString(10), task.getResult().toString());
                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child(Long.toString(currTime)).setValue(upload);
                    } else {
                        Log.w("SettFrag.uploadFile:", task.getException().getMessage());
                        Toast.makeText(SettingsFragment.this.getActivity(), R.string.toast_photo_not_uploaded, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(SettingsFragment.this.getActivity(), R.string.toast_no_photo_selected, Toast.LENGTH_SHORT).show();
        }
    }

}