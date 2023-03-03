package com.tec.personclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPerson extends AppCompatActivity {

    Intent intent;
    Spinner spnHairColor;
    Person newPerson;
    RadioButton rbtKat;
    RadioButton rbtHund;
    RadioButton rbtFugl;
    TextView txtSaved;
    ToggleButton tbtnIsFavorit;

    String[] hairColor = {"Blond", "Sort", "Brun", "Gråt"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_person);

        EditText edtName = findViewById(R.id.edtName);
        EditText edtTlf = findViewById(R.id.edtTlf);
        EditText edtJob = findViewById(R.id.edtJob);
        spnHairColor = findViewById(R.id.spnHairColor);
        tbtnIsFavorit = findViewById(R.id.tbtnIsFavorit);
        txtSaved = findViewById(R.id.txtSaved);

        rbtKat = findViewById(R.id.rbtKat);
        rbtHund = findViewById(R.id.rbtHund);
        rbtFugl = findViewById(R.id.rbtFugl);

        RadioGroup rbGroup = findViewById(R.id.rbGroup);

        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Opretter en adapter til spinneren og sætter den til spinneren
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, hairColor);
        spnHairColor.setAdapter(adapter);

        // Når "Gem" knappen trykkes
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Henter data fra de forskellige view-elementer
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

                // Opretter et nyt Person-objekt med data fra view-elementerne
                newPerson = new Person();

                newPerson.setName(name);
                newPerson.setTlf(tlf);
                newPerson.setJob(job);
                newPerson.setHairColor(hairColorIndex);
                newPerson.setPet(String.valueOf(pet));
                newPerson.setFavorit(isFavorit);

                // Sender den nye person til serveren via Retrofit
                PersonService personService = ServiceBuilder.buildService(PersonService.class);
                Call<Person> request = personService.addPerson(newPerson);
                request.enqueue(new Callback<Person>() {
                    @Override
                    public void onResponse(Call<Person> call, Response<Person> response) {
                        if (response.isSuccessful()) {
                            Person savedPerson = response.body();
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("newPerson", savedPerson);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Person> call, Throwable t) {
                        Log.d("Person Error: ", t.getMessage());
                        txtSaved.setText("Personen blev ikke oprettet");
                    }
                });

                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}