package com.tec.personclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView lstPerson;
    List<Person> personAll;

    FloatingActionButton fbtnNewPerson;
    
    private static final int DELETE_PERSON_REQUEST_CODE = 2;

    ActivityResultLauncher<Intent> personViewActivityLauncher;
    ActivityResultLauncher<Intent> newPersonViewActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstPerson = findViewById(R.id.lstPerson);
        lstPerson.setOnItemClickListener(this);
        fbtnNewPerson = findViewById(R.id.fbtnNewPerson);
        fbtnNewPerson.setOnClickListener(this);

        // Opretter et PersonService objekt
        PersonService personService = ServiceBuilder.buildService(PersonService.class);

        // Opretter et Call objekt til at hente alle personer fra serveren
        Call<List<Person>> requestAll = personService.getAllPerson();

        // Registrerer ActivityResultLauncherne
        personViewActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Opdaterer en eksisterende person i listen
                        Person updatedPerson = (Person) result.getData().getSerializableExtra("person");
                        int requestCode = result.getData().getIntExtra("requestCode", 0);
                        if (requestCode == DELETE_PERSON_REQUEST_CODE) {
                            personAll.remove(updatedPerson);
                        } else {
                            for (int i = 0; i < personAll.size(); i++) {
                                if (personAll.get(i).getId() == updatedPerson.getId()) {
                                    personAll.set(i, updatedPerson);
                                    break;
                                }
                            }
                        }
                    }
                });

        newPersonViewActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Opdatere listen med ny person
                        Person newPerson = (Person) result.getData().
                                getSerializableExtra("newPerson");
                        personAll.add(newPerson);
                        PersonAdapter adapter = new PersonAdapter(personAll, MainActivity.this);
                        lstPerson.setAdapter(adapter);
                    }
                }
        );


        requestAll.enqueue(new Callback<List<Person>>() {

            @Override
            public void onResponse(Call<List<Person>> call, Response<List<Person>> response) {
                personAll = response.body();

                PersonAdapter adapter = new PersonAdapter(personAll, MainActivity.this);

                lstPerson.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {
                Log.d("Person Error: ", t.getMessage());
            }

        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Person person = personAll.get(position);
        Intent intent = new Intent(MainActivity.this, PersonView.class);
        intent.putExtra("person", person);
        intent.putExtra("requestCode", 1);
        personViewActivityLauncher.launch(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        PersonService personService = ServiceBuilder.buildService(PersonService.class);
        Call<List<Person>> requestAll = personService.getAllPerson();
        // Udfører asynkron kald for at hente alle personer fra serveren
        requestAll.enqueue(new Callback<List<Person>>() {
            // onResponse metoden, der udføres, hvis kaldet til serveren var succesfuldt
            @Override
            public void onResponse(Call<List<Person>> call, Response<List<Person>> response) {
                personAll = response.body();
                // Opretter en PersonAdapter og sætter den på ListView'et
                PersonAdapter adapter = new PersonAdapter(personAll, MainActivity.this);
                lstPerson.setAdapter(adapter);
            }

            // onFailure metoden, der udføres, hvis kaldet til serveren fejlede
            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {
                Log.d("Person Error: ", t.getMessage());
            }
        });
    }

    // FAB
    // Metoden, der kaldes, når der klikkes på et element i ListView'et
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, NewPerson.class);
        newPersonViewActivityLauncher.launch(intent);

    }
}