package com.svs.hztb.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svs.hztb.Adapters.RetriveGroupsAdapter;
import com.svs.hztb.Bean.GroupDetail;
import com.svs.hztb.Bean.Product;
import com.svs.hztb.Bean.RequestOpinionInput;
import com.svs.hztb.Bean.RequestOpinionOutput;
import com.svs.hztb.Bean.Status;
import com.svs.hztb.Bean.UserData;
import com.svs.hztb.Bean.UserID;
import com.svs.hztb.Database.AppSharedPreference;
import com.svs.hztb.Interfaces.IRealmDataStoredCallBack;
import com.svs.hztb.R;
import com.svs.hztb.RealmDatabase.GroupDetailRealm;
import com.svs.hztb.RealmDatabase.RealmDatabase;
import com.svs.hztb.RealmDatabase.RealmInt;
import com.svs.hztb.RealmDatabase.RealmUserData;
import com.svs.hztb.RestService.ErrorStatus;
import com.svs.hztb.RestService.OpinionService;
import com.svs.hztb.RestService.ServiceGenerator;
import com.svs.hztb.Utils.LoadingBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.realm.RealmList;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewRequestFragment extends Fragment implements IRealmDataStoredCallBack{

    private final int RESULT_CAMERA = 0;
    private final int CROP_PIC = 2;
    private ListView groupsList;
    protected LoadingBar _loader;
    private ArrayList<GroupDetail> groupList;
    private Button reguestOpinionButton;
    private RetriveGroupsAdapter adapter;
    private ImageView productImage;
    private String imageData;
    private RealmDatabase database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_request, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null){
            imageData = savedInstanceState.getString("ImageData");
        }
        database = new RealmDatabase(this);
        groupsList = (ListView) view.findViewById(R.id.listview_groups);
        reguestOpinionButton =(Button)view.findViewById(R.id.button_request_opinion);
        reguestOpinionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Integer> selectedGroup = new ArrayList<Integer>();
                Iterator<GroupDetail> itertor = groupList.iterator();
                while (itertor.hasNext()){
                    GroupDetail group = itertor.next();
                    if (group.isSelect() == true){
                        selectedGroup.add(group.getGroupId());
                    }
                }
                if (selectedGroup.size() > 0){
                    postDataForNewRequest(selectedGroup);
                }
            }
        });


        _loader=new LoadingBar(getActivity());
        productImage = (ImageView)view.findViewById(R.id.product_thumb);
        Button captureSelf = (Button)view.findViewById(R.id.button_capture_selfie);
        captureSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, RESULT_CAMERA);//zero can be replaced with any action code
            }
        });
        if (imageData != null){
            String encodedString = imageData;
            byte[] picArray = Base64.decode(encodedString, Base64.DEFAULT);
            productImage.setImageBitmap( BitmapFactory.decodeByteArray(picArray , 0, picArray .length));
        }


        groupList = database.getAllGroupList();
        if (groupList.size()>0){
            updateListviewWithGroups();
            postDataToGetGroups(true);
        }else {
            showLoader();
            postDataToGetGroups(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray("ImageData",getSelfieByteArray());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {

            case RESULT_CAMERA:
                if(resultCode == Activity.RESULT_OK){
                    if (imageReturnedIntent.getData() != null) {
                        Uri selectedImage = imageReturnedIntent.getData();
                        performCrop(selectedImage);
                    }else {
                        Bitmap bitmap= (Bitmap)imageReturnedIntent.getExtras().get("data");
                        productImage.setImageBitmap(bitmap);
                    }
                }

                break;
            case CROP_PIC:{
                // get the returned data
                Bundle extras = imageReturnedIntent.getExtras();
                if (extras != null){
                    // get the cropped bitmap
                    Bitmap bitmapPic = extras.getParcelable("data");
                    productImage.setImageBitmap(bitmapPic);
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

    private byte[] getSelfieByteArray(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ((BitmapDrawable)productImage.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }


    private void postDataForNewRequest(ArrayList<Integer> selectedGroup) {
        showLoader();


        OpinionService opinionService = new OpinionService();
        RequestOpinionInput requestOpinionInput = new RequestOpinionInput();
        requestOpinionInput.setRequesterUserId(Integer.valueOf(new AppSharedPreference().getUserID(getActivity().getApplicationContext())));
        Product product = new Product();
        product.setName("Test606163");
        product.setShortDesc("Awesome");
        product.setLongDesc("brilliant");
        product.setPrice(22.0);
        requestOpinionInput.setSelfiePic(getSelfieByteArray());
        requestOpinionInput.setProduct(product);
        requestOpinionInput.setRequestedGroupIds(selectedGroup);
        Observable<Response<RequestOpinionOutput>> registerResponseObservable = opinionService.requestOpinionForNewProduct(requestOpinionInput);
        registerResponseObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<RequestOpinionOutput>>() {
            @Override
            public void onCompleted() {
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                cancelLoader();
            }

            @Override
            public void onNext(Response<RequestOpinionOutput> requestResponse) {

                if (requestResponse.isSuccessful()) {

                    if (requestResponse.body().getStatus() == Status.SUCCESS){
                        Toast.makeText(getActivity().getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
                        clearSelectionList();
                    }else {
                        Toast.makeText(getActivity().getApplicationContext(),"UNSUCCESSFUL",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    List<ErrorStatus> listErrorStatus = ServiceGenerator.parseErrorBody(requestResponse);
                    for (ErrorStatus listErrorState : listErrorStatus) {
                        Log.d("Error Message : ", listErrorState.getMessage() + " " + listErrorState.getStatus());
                    }

                }
            }
        });
    }

    private void clearSelectionList() {
        Iterator<GroupDetail> itertor = groupList.iterator();
        while (itertor.hasNext()){
            GroupDetail group = itertor.next();
            if (group.isSelect() == true){
                group.setSelect(false);
            }
        }
        adapter.notifyDataSetChanged();
    }


    private void postDataToGetGroups(final boolean isBackgroudTask) {
        if (!isBackgroudTask)
        showLoader();


        OpinionService opinionService = new OpinionService();
        UserID userId = new UserID();
        AppSharedPreference sherdPref = new AppSharedPreference();
        int id = Integer.valueOf(sherdPref.getUserID(getActivity().getApplicationContext()));
        userId.setUserID(id);
        Observable<Response<List<GroupDetail>>> getGroupObservable = opinionService.getGroups(userId);

        getGroupObservable.observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).subscribe(new Subscriber<Response<List<GroupDetail>>>() {
            @Override
            public void onCompleted() {
                if (!isBackgroudTask)
                cancelLoader();
            }

            @Override
            public void onError(Throwable e) {
                if (!isBackgroudTask)
                    cancelLoader();
                Log.d("Error ",e.toString());
            }

            @Override
            public void onNext(Response<List<GroupDetail>> groupResponse) {

                if (groupResponse.isSuccessful()) {
                    if (((ArrayList<GroupDetail>) groupResponse.body()).size() > 0) {
                        addAllTheListToDatabase((ArrayList<GroupDetail>) groupResponse.body());
                    }else {
                        updateListviewWithGroups();
                    }
                }
                else Toast.makeText(getActivity().getApplicationContext(),"No Options Available",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void updateListviewWithGroups() {

        GroupDetail group = new GroupDetail();
        group.setGroupName("Select From Contacts");
        groupList.add(group);
        adapter = new RetriveGroupsAdapter(getActivity().getApplicationContext(), groupList,false);
        groupsList.setAdapter(adapter);
        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (groupList.size() - 1 == i) {
                    ContactsFragment fragment = new ContactsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("imageData",getSelfieByteArray());
                    fragment.setArguments(bundle);
                    String backStateName = fragment.getClass().getName();
                    FragmentManager fragmentManager = getFragmentManager();
                    boolean fragmentPopped = fragmentManager
                            .popBackStackImmediate(backStateName, 0);
                    if (!fragmentPopped) {
                        FragmentTransaction ftx = fragmentManager.beginTransaction();
                        ftx.replace(R.id.fragment, fragment);
                        ftx.addToBackStack(backStateName);
                        ftx.commit();
                    }
                }
                else {
                    if (!groupList.get(i).isSelect()) {
                        groupList.get(i).setSelect(true);
                    }else groupList.get(i).setSelect(false);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void addAllTheListToDatabase(ArrayList<GroupDetail> groupDetailList){
        Iterator<GroupDetail> iterator = groupDetailList.iterator();
        RealmList<GroupDetailRealm> groupDetailRealmList = new RealmList<>();
        while (iterator.hasNext()){
            GroupDetail groupDetail = iterator.next();
            GroupDetailRealm groupDetailRealm = new GroupDetailRealm();
            groupDetailRealm.setGroupName(groupDetail.getGroupName());
            groupDetailRealm.setGroupId(groupDetail.getGroupId());
            RealmList<RealmUserData> realmUserList = new RealmList<>();
            Iterator<UserData> integerIterator = groupDetail.getGroupMembers().iterator();
            while (integerIterator.hasNext()){
                UserData userData = integerIterator.next();

                RealmUserData realmUserData = new RealmUserData();
                realmUserData.setUserId(userData.getUserId());
                realmUserData.setFirstName(userData.getFirstName());
                realmUserData.setLastname(userData.getLastname());
                realmUserList.add(realmUserData);
            }
            groupDetailRealm.setUserDataList(realmUserList);
            groupDetailRealmList.add(groupDetailRealm);
        }
        database.addGroupsListToDatabase(groupDetailRealmList);
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

    public static String toJson(Object object) {
        String jsonString = "";
        ObjectMapper mapper = new ObjectMapper();
        Writer strWriter = new StringWriter();
        try {
            mapper.writeValue(strWriter, object);
            jsonString = strWriter.toString();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonString;
    }

    @Override
    public void dataSuccessfullyStore(Boolean successful) {
        groupList = database.getAllGroupList();
        updateListviewWithGroups();
    }
}
