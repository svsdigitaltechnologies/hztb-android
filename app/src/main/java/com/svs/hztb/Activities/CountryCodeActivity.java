package com.svs.hztb.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.svs.hztb.Adapters.CountryCodeAdapter;
import com.svs.hztb.Bean.CountryItem;
import com.svs.hztb.R;

import java.util.ArrayList;

public class CountryCodeActivity extends AbstractActivity {

    private ListView countriesList;
    private CountryCodeAdapter adapter;
    private ArrayList<CountryItem> countryItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_code);
        actionBarSettings(R.string.country_choose);

        initializeCountryItem();
        initView();

    }

    private void initializeCountryItem() {
        int[] countrycodeArray =  getResources().getIntArray(R.array.countryCodeArray);
       String[] countryNameArray =getResources().getStringArray(R.array.countries);
        countryItemList = new ArrayList<>();
        for (int i = 0 ; i < countrycodeArray.length ; i ++){
            CountryItem countryItem = new CountryItem();
            countryItem.setCountryName(countryNameArray[i]);
            countryItem.setCountryCode(countrycodeArray[i]);
            countryItemList.add(countryItem);

        }
    }

    private void initView() {

        countriesList   = getView(R.id.listview_countrycode);

        adapter = new CountryCodeAdapter(getApplicationContext(),countryItemList);
        countriesList.setAdapter(adapter);

        EditText searchText = getView(R.id.edittext_search_countrycode);
        /**
         * Enabling Search Filter
         * */
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                CountryCodeActivity.this.adapter.filter(""+cs);
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        countriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.putExtra("COUNTRYNAME",getResources().getStringArray(R.array.countries)[position]);
                intent.putExtra("CODE",getResources().getIntArray(R.array.countryCodeArray)[position]);
                setResult(100,intent);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent();
        intent.putExtra("COUNTRYNAME",countryItemList.get(7).getCountryName());
        intent.putExtra("CODE",countryItemList.get(7).getCountryCode());
        setResult(100,intent);
        finish();
        super.onBackPressed();
    }
}
