package com.tec.personclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PersonService {

    @GET("Person/")
    Call<List<Person>> getAllPerson();

    @GET("Person/{id}/")
    Call<Person> getPersonById(@Path("id") int id);

    @POST("Person/")
    Call<Person> addPerson(@Body Person p);

    @DELETE("Person/{id}")
    Call<Void> deletePersonById(@Path("id") int id);

    @PUT("Person/{id}")
    Call<Void> updatePerson(@Path("id") int id, @Body Person p);
}
