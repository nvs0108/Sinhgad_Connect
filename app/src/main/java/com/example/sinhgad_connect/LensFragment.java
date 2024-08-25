package com.example.sinhgad_connect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.Arrays;

public class LensFragment extends Fragment {
    private ImageView imageView;
    private TextView resultTextView;
    private Button captureButton, uploadButton, confirmButton;
    private ProgressBar progressBar;
    private Bitmap capturedImageBitmap;

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_GALLERY_IMAGE = 102;

    public LensFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lens, container, false);

        imageView = view.findViewById(R.id.imageView);
        resultTextView = view.findViewById(R.id.resultTextView);
        captureButton = view.findViewById(R.id.captureButton);
        uploadButton = view.findViewById(R.id.uploadButton);
        confirmButton = view.findViewById(R.id.confirmButton);
        progressBar = view.findViewById(R.id.progressBar);


        return view;
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(getActivity(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchSelectImageIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
        } else {
            Toast.makeText(getActivity(), "No gallery app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == requireActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            capturedImageBitmap = imageBitmap;
        } else if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == requireActivity().RESULT_OK) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(imageBitmap);
                capturedImageBitmap = imageBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
