package com.svs.hztb.Activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.hztb.Bean.UserProfileResponse;
import com.svs.hztb.CustomViews.WalkWayButton;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.R;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.RegisterService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.ConnectionDetector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileActivity extends AbstractActivity {

    private String mobileNumber;
    private EditText emailEditText;
    private EditText nameEditText;
    private ImageView profilePic;
    private final int RESULT_CAMERA = 0;
    private final int RESULT_GALLERY = 1;
    private final int CROP_PIC = 2;

    public Uri getCapturedImageURI() {
        return capturedImageURI;
    }

    public void setCapturedImageURI(Uri capturedImageURI) {
        this.capturedImageURI = capturedImageURI;
    }

    private Uri capturedImageURI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        actionBarSettings(R.string.profile_title);
        initView();

    }

    private void initView() {
        mobileNumber = getIntent().getStringExtra("NUMBER");
        TextView mobileNum = getView(R.id.editText_mobileNumber);
        profilePic = getView(R.id.profile_thumb);
        mobileNum.setText(getResources().getString(R.string.string_plus) + mobileNumber);
        emailEditText = getView(R.id.editText_email);
        nameEditText = getView(R.id.editText_name);

    }

    public void onProfileThumbClicked(View view){
        showProfilePicSelectionDialog();

    }

    private void showProfilePicSelectionDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View dialog = inflater.inflate(R.layout.custom_profile_pic_selection, null);
        dialogBuilder.setView(dialog);

        final AlertDialog alertDialog = dialogBuilder.create();


        WalkWayButton cameraPic = (WalkWayButton)dialog.findViewById(R.id.button_camera_pic);
        WalkWayButton galleryPic = (WalkWayButton)dialog.findViewById(R.id.button_gallery_pic);
        WalkWayButton cancel = (WalkWayButton)dialog.findViewById(R.id.button_cancel);
        alertDialog.setTitle(getResources().getString(R.string.profile_choose_from));
        cameraPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, RESULT_CAMERA);//zero can be replaced with any action code
            }
        });

        galleryPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){


                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Toast toast = Toast.makeText(ProfileActivity.this, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri fileUri = Uri.fromFile(photoFile);
                        setCapturedImageURI(fileUri);
                        Intent takePictureIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                         getCapturedImageURI());
                        startActivityForResult(takePictureIntent, RESULT_GALLERY);
                    }

                }else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"), RESULT_GALLERY);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "1mind_" + timeStamp + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(),  imageFileName);
        return photo;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {

            case RESULT_CAMERA:
                if(resultCode == RESULT_OK){
                        performCrop(capturedImageURI);
                }

                break;
            case RESULT_GALLERY:
                if(resultCode == RESULT_OK){
                    if (imageReturnedIntent.getData() != null) {
                        performCrop(capturedImageURI);
                    }
                }
                break;
            case CROP_PIC:{
                // get the returned data
                Bundle extras = imageReturnedIntent.getExtras();
                if (extras != null){
                    // get the cropped bitmap
                    Bitmap bitmapPic = extras.getParcelable("data");
                    profilePic.setImageBitmap(bitmapPic);
                }
            }
        }
    }


    /**
     * this function does the crop operation.
     */
    private void performCrop(Uri picUri) {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            displayMessage( "This device doesn't support the crop action!");
        }
    }

    /**
     * OnClick event for Sign Up
     *
     * @param view
     */
    public void onSignUpDoneButtonClicked(View view) {
        ConnectionDetector c = new ConnectionDetector(getApplicationContext());
        if(c.isConnectingToInternet()) {
            postDataForUpdateUserProfile();
        }else displayMessage("No Network");
    }

    private void postDataForUpdateUserProfile() {
        showLoader();
        final String nameParam,emailParam;
        if (nameEditText.getText().toString() == null || nameEditText.getText().toString().equals("")){
            nameParam = "My Info";
        }else {
            nameParam = nameEditText.getText().toString().trim();
        }
        if (emailEditText.getText().toString() == null || emailEditText.getText().toString().equals("")){
            emailParam = null;
        }else {
            emailParam = nameEditText.getText().toString().trim();
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ((BitmapDrawable)profilePic.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 50, stream);
        final byte[] picArray = stream.toByteArray();

        RegisterService registerService = new RegisterService();
        Observable<Response<UserProfileResponse>> userProfileResponseObservable = registerService.updateUserProfile(mobileNumber, nameParam,emailParam,picArray);

        userProfileResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<UserProfileResponse>>() {
            @Override
            public void onCompleted() {
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                cancelLoader();
            }

            @Override
            public void onNext(Response<UserProfileResponse> userProfileResponseObservable) {

                cancelLoader();
                if (userProfileResponseObservable.isSuccessful()) {
                    displayMessage(getResources().getString(R.string.toast_userprofile_update_success));
                    pushActivity(HomeScreenActivity.class);
                    new AppSharedPreference().storeSuccessLoginInSharedPreferences(getApplicationContext(),nameParam,emailParam,picArray);

                    finish();
                } else {
                    List<ErrorStatus> listErrorStatus = ServiceGenerator.parseErrorBody(userProfileResponseObservable);
                    for (ErrorStatus listErrorState : listErrorStatus) {
                        Log.d("Error Message : ", listErrorState.getMessage() + " " + listErrorState.getStatus());
                        displayMessage(listErrorState.getMessage() + "Status :" + listErrorState.getStatus());
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        android.app.AlertDialog alertDialog = alertDialog();
        alertDialog.show();
    }

}
