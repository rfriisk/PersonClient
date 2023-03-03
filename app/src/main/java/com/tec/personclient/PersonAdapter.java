package com.tec.personclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import retrofit2.Callback;

public class PersonAdapter extends BaseAdapter {

    private final List<Person> personList;
    private final MainActivity mainActivity;

    public PersonAdapter(List<Person> personList, MainActivity mainActivity) {
        this.personList = personList;
        this.mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return personList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        Person personAll = personList.get(position);
        LayoutInflater inflater = LayoutInflater.from(mainActivity);
        View v = inflater.inflate(R.layout.custom_item, null);

        TextView txtName = v.findViewById(R.id.txtName);
        txtName.setText("Navn: " + personAll.getName());

        TextView txtTlf = v.findViewById(R.id.txtTlf);
        txtTlf.setText(("Tlf: " + personAll.getTlf()));

        TextView txtJob = v.findViewById(R.id.txtJob);
        txtJob.setText("Job: " + personAll.getJob());

        ImageView imvStar = v.findViewById(R.id.imvStar);
        if (personAll.getFavorit() == true) {
            imvStar.setVisibility(View.VISIBLE);
        } else {
            imvStar.setVisibility(View.GONE);
        }

        return v;
    }
}
