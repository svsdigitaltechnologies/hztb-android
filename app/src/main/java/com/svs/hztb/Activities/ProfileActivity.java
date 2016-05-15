package com.svs.hztb.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.svs.hztb.Bean.UserProfileResponse;
import com.svs.hztb.CustomViews.WalkWayButton;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.R;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.RegisterService;
import com.svs.hztb.RestService.ServiceGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    private static final int RESULT_CAMERA = 0;
    private static final int RESULT_GALLERY = 1;

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
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProfilePicSelectionDialog();

            }
        });
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
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , RESULT_GALLERY);//one can be replaced with any action code
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

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    if (imageReturnedIntent.getData() != null) {
                        Uri selectedImage = imageReturnedIntent.getData();
                        Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                        profilePic.setImageBitmap(getRotatedBitmap(selectedImage, bitmap));
                    }else displayMessage("Error");
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    profilePic.setImageURI(selectedImage);
                }
                break;
        }
    }


    private Bitmap getRotatedBitmap(Uri selectedImage,Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.postRotate(getImageOrientation(selectedImage.toString()));
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }

    public static int getImageOrientation(String imagePath){
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }



    /**
     * OnClick event for Sign Up
     *
     * @param view
     */
    public void onSignUpDoneButtonClicked(View view) {
                postDataForUpdateUserProfile();
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
        ((BitmapDrawable)profilePic.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
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



    /**
     * Check if name field is empty or not
     *
     * @param name
     * @return
     */
    private boolean isNotEmpty(EditText name) {

        if (name.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog alertDialog = alertDialog();
        alertDialog.show();
    }

}
