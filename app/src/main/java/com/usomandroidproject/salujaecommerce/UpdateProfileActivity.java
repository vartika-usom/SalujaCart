package com.usomandroidproject.salujaecommerce;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {
    TextView nameEditText, emailEditText, phoneEdittext, passwordEditText;
    User user;
    CircleImageView profileClick;
    SharedPreferences sharedPrefrences;
    Uri filePath;
    File destinationFile;
    Bitmap bitmap;
    int loggedInUserId;
    String filename, imageFilePath, encodedString, imgurl,imgLocalPath;
    ImageView Cancelbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        nameEditText = (TextView) findViewById(R.id.nameEditText);
        emailEditText = (TextView) findViewById(R.id.emailEditText);
        phoneEdittext = (TextView) findViewById(R.id.phoneEditText);
        passwordEditText = (TextView) findViewById(R.id.passwordEditText);
        profileClick = (CircleImageView) findViewById(R.id.profile);
        RelativeLayout editButton = (RelativeLayout) findViewById(R.id.editButton);
        Cancelbutton = (ImageView)findViewById(R.id.Cancelbutton);

        sharedPrefrences = UpdateProfileActivity.this
                .getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userInfo = BaseClass.getStringFromPreferences(UpdateProfileActivity.this,
                null, Config.USERINFO);

        imgurl = sharedPrefrences.getString(Config.LOGGEDINIMGURL, "");
        imgLocalPath = sharedPrefrences.getString(Config.LOGGEDINLOCALIMAGE, "");

        if (userInfo != null) {
            user = BaseClass.convertStringToUser(userInfo);
            nameEditText.setText(user.getName());
            emailEditText.setText(user.getEmail());
            phoneEdittext.setText(user.getPhone());
            passwordEditText.setText(user.getPassword());
            loggedInUserId = user.getId();
        }

        Cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(imgLocalPath != "") {
            File file = new File(imgLocalPath);

            if (file.exists()) {
                Bitmap bit = BaseClass.getCompressedBitmap(file);
                profileClick.setImageBitmap(bit);
            } else {
                Picasso.with(getApplicationContext()).load(imgurl)
                        .placeholder(R.drawable.ic_profile_set).into(profileClick);
            }
        }

        profileClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPermissionGranted = RequestStoragePermission();
                if (isPermissionGranted) {
                    selectImage();
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean RequestStoragePermission() {
        int readStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded
                    .toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("TAG", "Permission callback called-------");
        switch (requestCode) {
            case 100: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                //perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                //perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("TAG", "sms & location services permission granted");
                        selectImage();
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("TAG", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                            showDialogOK("Read Phone State and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    RequestStoragePermission();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
//                                                    android.os.Process.killProcess(android.os.Process.myPid());
//                                                    System.exit(1);
                                                    dialog.dismiss();
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(
                UpdateProfileActivity.this);
        builder.setTitle("Set Profile Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (items[i].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    Uri photoUri = getPhotoUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, 0);

                } else if (items[i].equals("Gallery")) {
                    showFileChooser();
                }
            }
        }).show();
    }

    private Uri getPhotoUri() {
        File photoFile = null;
        try {
            photoFile = createImageFile(UpdateProfileActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri photoUri = FileProvider.getUriForFile(UpdateProfileActivity.this,
                getPackageName() + ".provider", photoFile);

        return photoUri;

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    private File createImageFile(Context context) throws IOException {

        String  uniqueID = UUID.randomUUID().toString();
        filename =  loggedInUserId + ".png";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, filename);
        imageFilePath = image.getAbsolutePath();

        SharedPreferences.Editor editor = sharedPrefrences.edit();
        Toast.makeText(context, imageFilePath+"", Toast.LENGTH_SHORT).show();
        editor.putString(Config.LOGGEDINLOCALIMAGE, imageFilePath);
        editor.commit();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    bitmap = BaseClass.getResizedBitmap(bitmap, 150);
                    profileClick.setImageBitmap(bitmap);
                    File galleryfile = getPath(filePath);
                    copyFile(galleryfile, destinationFile);

                } catch (IOException e) {

                }
            }
        } else if (requestCode == 0 && resultCode == RESULT_OK) {
            bitmap = BitmapFactory.decodeFile(imageFilePath);
            bitmap = BaseClass.getResizedBitmap(bitmap, 200);
            profileClick.setImageBitmap(bitmap);
        }
        if (bitmap != null) {
            uploadImage();
        }
    }

    //copy file from source to destination
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }


    //get Actual path of picked image from gallery
    public File getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return new File(cursor.getString(column_index));
    }

    private void uploadImage() {
        try {
            encodedString = getStringImage(bitmap);
            Log.i("Tag",encodedString);
            Intent i = new Intent(UpdateProfileActivity.this, UploadImageService.class);
            i.putExtra("image_string", encodedString);
            i.putExtra("id", String.valueOf(loggedInUserId));
            bitmap = null;
            startService(i);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getStringImage(Bitmap bmp) {
        String encodedImage = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() + "", Toast.LENGTH_SHORT).show();
        }
        return encodedImage;
    }


    private void showFileChooser() {
        try {
            destinationFile = createImageFile(UpdateProfileActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

}