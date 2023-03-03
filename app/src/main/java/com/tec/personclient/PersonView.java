package com.tec.personclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PersonView extends AppCompatActivity {

    Intent intent;
    Spinner spnHairColor;
    Person person;
    RadioButton rbtKat;
    RadioButton rbtHund;
    RadioButton rbtFugl;
    TextView txtUpdated;
    ToggleButton tbtnIsFavorit;

    String[] hairColor = {"Blond", "Sort", "Brun", "Gråt"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_view);

        EditText edtName = findViewById(R.id.edtName);
        EditText edtTlf = findViewById(R.id.edtTlf);
        EditText edtJob = findViewById(R.id.edtJob);
        spnHairColor = findViewById(R.id.spnHairColor);
        tbtnIsFavorit = findViewById(R.id.tbtnIsFavorit);
        txtUpdated = findViewById(R.id.txtUpdated);

        rbtKat = findViewById(R.id.rbtKat);
        rbtHund = findViewById(R.id.rbtHund);
        rbtFugl = findViewById(R.id.rbtFugl);

        RadioGroup rbGroup = findViewById(R.id.rbGroup);

        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);
        Button btnDelete = findViewById(R.id.btnDelete);

        // Slet knap
        // Slet knappen sender en slet-anmodning til databasen og afslutter aktiviteten
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonService personService = ServiceBuilder.buildService(PersonService.class);
                Call<Void> deleteRequest = personService.deletePersonById(person.getId());
                deleteRequest.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Intent intentDelete = new Intent();
                            intentDelete.putExtra("person", person);
                            setResult(Activity.RESULT_OK, intentDelete);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
            }
        });

        // Knapper til at gemme, slette eller annullere
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Gem Knap
        // Gem knappen opdaterer personobjektet og sender en opdateringsanmodning til databasen
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                int tlf = Integer.parseInt(edtTlf.getText().toString());
                String job = edtJob.getText().toString();
                int hairColorIndex = spnHairColor.getSelectedItemPosition();
                int pet = 0;
                if (rbtKat.isChecked()) {
                    pet = 1;
                } else if (rbtHund.isChecked()) {
                    pet = 2;
                } else if (rbtFugl.isChecked()) {
                    pet = 3;
                }

                boolean isFavorit = tbtnIsFavorit.isChecked();

                // Opdatere person objektet med nye værdier
                person.setName(name);
                person.setTlf(tlf);
                person.setJob(job);
                person.setHairColor(hairColorIndex);
                person.setPet(String.valueOf(pet));
                person.setFavorit(isFavorit);

//                tbtnIsFavorit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                        PersonService personService = ServiceBuilder.buildService(PersonService.class);
//                        Call<Void> updateRequest = personService.updatePerson(person.getId(), person);
//                        updateRequest.enqueue(new Callback<Void>() {
//                            @Override
//                            public void onResponse(Call<Void> call, Response<Void> response) {
//                                if (response.isSuccessful()) {
//                                    Log.d("Favorit opdateret", "Done");
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Void> call, Throwable t) {
//
//                            }
//                        });
//                    }
//                });



                PersonService personService = ServiceBuilder.buildService(PersonService.class);
                Call<Void> updateRequest = personService.updatePerson(person.getId(), person);
                updateRequest.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            txtUpdated.setText("Person opdateret");
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("person", person);
                            setResult(Activity.RESULT_OK, resultIntent);
                            Log.d("Opdateret navn", person.getName());
                            txtUpdated.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    txtUpdated.setVisibility(View.GONE);
                                }
                            }, 5000);
                            //finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        txtUpdated.setText("Ikke opdateret");
                    }
                });
            }
        });

        // Sætter en adapter på spinneren til hårfarve
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, hairColor);
        spnHairColor.setAdapter(adapter);

        intent = getIntent();
        person = (Person) intent.getSerializableExtra("person");

        if (person != null) {
            edtName.setText(person.getName());
            // Hvis det er en int skal den konverteres til en string, det kan gøres ved at sætte "" foran
            edtTlf.setText("" + person.getTlf());
            edtJob.setText(person.getJob());

            int hairColorIndex = person.getHairColor();
            String hcStr = hairColor[hairColorIndex];
            int spnPos = adapter.getPosition(hcStr);
            spnHairColor.setSelection(spnPos);

            tbtnIsFavorit.setChecked(person.getFavorit());
        }

        int pet = Integer.parseInt(person.getPet());
        switch (pet) {
            case 1:
                rbtKat.setChecked(true);
                break;
            case 2:
                rbtHund.setChecked(true);
                break;
            case 3:
                rbtFugl.setChecked(true);
                break;
            default:
                break;
        }

    }

}