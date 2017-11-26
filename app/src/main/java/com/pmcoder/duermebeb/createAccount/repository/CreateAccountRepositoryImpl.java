package com.pmcoder.duermebeb.createAccount.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pmcoder.duermebeb.createAccount.model.CreateAccountActivity;
import com.pmcoder.duermebeb.createAccount.presenter.CreateAccountPresenter;
import com.pmcoder.duermebeb.createAccount.presenter.CreateAccountPresenterImpl;

import java.util.HashMap;

/**
 * Created by pmcoder on 12/11/17.
 */

public class CreateAccountRepositoryImpl implements CreateAccountRepository {

    private CreateAccountPresenter presenter;
    private CreateAccountActivity createAccountActivity;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    private SharedPreferences preferences;

    public CreateAccountRepositoryImpl (CreateAccountPresenterImpl presenter,
                                        CreateAccountActivity createAccountActivity){

        this.presenter = presenter;
        this.createAccountActivity = createAccountActivity;
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.
                getInstance()
                .getReference()
                .child("users");

        preferences = createAccountActivity.getSharedPreferences("User-Data", Context.MODE_PRIVATE);

    }

    @Override
    public void newUSer(final String email, String password, final String name){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(createAccountActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            try {
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                if (user != null){

                                    user.sendEmailVerification();
                                    updateProfile(name, email, user);
                                    presenter.goToLoginActivity();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            presenter.finishActivity();
                        }
                    }
                }).addOnFailureListener(createAccountActivity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                presenter.createAccountError(e);

            }
        });
    }

    private void updateProfile(String name, String email, FirebaseUser user) {

        UserProfileChangeRequest profileUpdates =
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
        user.updateProfile(profileUpdates).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (!task.isSuccessful()){
                            Log.
                                    i("Error", "No se pudo actualizar el perfil");
                        }
                    }
                });

        setUserDatabase(user.getUid(), name, email);
    }

    private void setUserDatabase(String uid, String name, String email){

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("UID", uid);
        editor.putString("NAME", name);
        editor.putString("EMAIL", email);
        editor.apply();

        HashMap<String, String> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);

        database.child(uid).child("userdata").setValue(userData);
    }

}
