package com.svs.hztb.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.hztb.Activities.HomeScreenActivity;
import com.svs.hztb.Bean.UserProfileResponse;
import com.svs.hztb.CustomViews.WalkWayButton;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Permissions.MarshMallowPermission;
import com.svs.hztb.R;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.RegisterService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.LoadingBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileFragment extends Fragment {

    private String mobileNumber;
    private EditText emailEditText;
    private EditText nameEditText;
    private ImageView profilePic;
    private final int RESULT_CAMERA = 0;
    private final int RESULT_GALLERY = 1;
    private final int CROP_PIC = 2;
    protected LoadingBar _loader;
    private File mediaFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mobileNumber = new AppSharedPreference().getMobileNumber(getActivity());
        TextView mobileNum = (TextView)view.findViewById(R.id.editText_mobileNumber);
        profilePic = (ImageView)view.findViewById(R.id.profile_thumb);
        mobileNum.setText(getResources().getString(R.string.string_plus) + mobileNumber);
        emailEditText = (EditText)view.findViewById (R.id.editText_email);
        nameEditText =  (EditText)view.findViewById (R.id.editText_name);
        nameEditText.setText(new AppSharedPreference().getUserName(getActivity().getApplicationContext()));
        Button profileEditButton = (Button)view.findViewById(R.id.edit_profilePic);
        Bitmap picBitmap = new AppSharedPreference().getUserBitmap(getActivity().getApplicationContext());
        profilePic.setImageBitmap(picBitmap);
        profileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePicSelectionDialog();
            }
        });

//        emailEditText.setText(new AppSharedPreference().get);
        _loader=new LoadingBar(getActivity());
        Button doneButton = (Button)view.findViewById(R.id.button_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDataForUpdateUserProfile();
            }
        });
    }


    private void showProfilePicSelectionDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

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

                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Toast toast = Toast.makeText(getActivity(), "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                    toast.show();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri fileUri = Uri.fromFile(photoFile);
                    ((HomeScreenActivity) getActivity()).setCapturedImageURI(fileUri);
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            ((HomeScreenActivity) getActivity()).getCapturedImageURI());
                    startActivityForResult(takePictureIntent, RESULT_CAMERA);
                }
            }
        });

        galleryPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , RESULT_GALLERY);//one can be replaced with any action code
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


    public void showLoader(){
        if(_loader!=null && !_loader.isShowing()){
            _loader.show();
        }
    }

    public void cancelLoader(){
        if(_loader!=null && _loader.isShowing()){
            _loader.cancel();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {

            case RESULT_CAMERA:
                if(resultCode == Activity.RESULT_OK){
                    if (imageReturnedIntent != null) {
                        if (imageReturnedIntent.getData() != null) {
                            Uri selectedImage = imageReturnedIntent.getData();
                            performCrop(selectedImage);
                        } else {
                            Uri selectedImage = ((HomeScreenActivity) getActivity()).getCapturedImageURI();
                            performCrop(selectedImage);
                        }
                    }else {
                        Uri selectedImage = ((HomeScreenActivity) getActivity()).getCapturedImageURI();
                        performCrop(selectedImage);
                    }
                }

                break;
            case RESULT_GALLERY:
                if(resultCode ==Activity.RESULT_OK){
                    if (imageReturnedIntent.getData() != null) {
                        Uri selectedImage = imageReturnedIntent.getData();
                        performCrop(selectedImage);
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
            Toast.makeText(getActivity().getApplicationContext(),"Device Crop Not Supported",Toast.LENGTH_LONG).show();
        }
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
            emailParam = "";
        }else {
            emailParam = emailEditText.getText().toString().trim();
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
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getString(R.string.toast_userprofile_update_success),Toast.LENGTH_LONG).show();
                    new AppSharedPreference().storeSuccessLoginInSharedPreferences(getActivity().getApplicationContext(),nameParam,emailParam,picArray);
                    ((HomeScreenActivity)getActivity()).slideMenuAdapter.notifyDataSetChanged();
                } else {
                    List<ErrorStatus> listErrorStatus = ServiceGenerator.parseErrorBody(userProfileResponseObservable);
                    for (ErrorStatus listErrorState : listErrorStatus) {
                        Log.d("Error Message : ", listErrorState.getMessage() + " " + listErrorState.getStatus());
                    }
                }
            }
        });
    }


}
