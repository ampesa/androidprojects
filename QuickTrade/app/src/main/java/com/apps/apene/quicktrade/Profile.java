package com.apps.apene.quicktrade;

import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import androidx.core.app.LoaderManager;
//import androidx.core.content.AsyncTaskLoader;
//import androidx.core.content.Loader;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.apene.quicktrade.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    // Elementos gráficos
    protected TextView mProfileEmail = null;
    protected TextView mProfilePass = null;
    protected EditText mProfileName = null;
    protected EditText mProfileFamily = null;
    protected EditText mProfileCountry = null;
    protected EditText mProfileZip = null;
    protected FirebaseAuth mAuth = null;
    protected String sCurrentUID = null;


    // Botón editar/guardar
    protected Button mButtonEdit = null;

    // Objeto User
    protected User user = null;

    // Objeto Base de Datos
    protected DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Mostramos la barra de acción superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Referencias
        mProfileEmail = findViewById(R.id.tv_profile_email);
        mProfilePass = findViewById(R.id.tv_profile_pass);
        mProfileName = findViewById(R.id.tv_profile_name);
        mProfileFamily = findViewById(R.id.tv_profile_family);
        mProfileCountry = findViewById(R.id.tv_profile_country);
        mProfileZip = findViewById(R.id.tv_profile_zip);
        mButtonEdit = findViewById(R.id.bt_profile_edit);
        mAuth = FirebaseAuth.getInstance();
        sCurrentUID = mAuth.getCurrentUser().getUid();

        // Referencia de la bbdd
        mDataBase = FirebaseDatabase.getInstance().getReference(getString(R.string.db_node_users));

        final Query currentUserData = mDataBase.orderByChild("uid").equalTo(sCurrentUID);

        getUserValues(currentUserData);

        // Acciones del botón EDIT
        mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si el el boton EDIT contiene "edit" lo cambiamos a "save" y hacemos editables los EditText
                if (mButtonEdit.getText().equals(getString(R.string.bt_profile_edit))){
                    mButtonEdit.setText(getString(R.string.bt_profile_save));
                    mProfileName.setEnabled(true);
                    mProfileFamily.setEnabled(true);
                    mProfileCountry.setEnabled(true);
                    mProfileZip.setEnabled(true);
                } else { // Si no, hacemos lo contrario
                    mButtonEdit.setText(getString(R.string.bt_profile_edit));
                    mProfileName.setEnabled(false);
                    mProfileFamily.setEnabled(false);
                    mProfileCountry.setEnabled(false);
                    mProfileZip.setEnabled(false);
                    // Y, si todos los campos están completados
                    if (!TextUtils.isEmpty(mProfileName.getText().toString())) {
                        if (!TextUtils.isEmpty(mProfileFamily.getText().toString())) {
                            if (!TextUtils.isEmpty(mProfileCountry.getText().toString())) {
                                if (!TextUtils.isEmpty(mProfileZip.getText().toString())) {

                                    // Actualizamos los datos del usuario en Firebase
                                    setUserValues(currentUserData);

                                } else {
                                    Toast.makeText(Profile.this, getString(R.string.signup_no_zip),
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(Profile.this, getString(R.string.signup_no_country),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Profile.this, getString(R.string.signup_no_family),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Profile.this, getString(R.string.signup_no_name),
                                Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(Profile.this, getString(R.string.profile_edit_ok),
                            Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    // Método para obtener los datos del usuario actual recibe un objeto Query
    private void getUserValues (Query currentUserData){
        currentUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorremos el DataSnapshot
                for (DataSnapshot datasnapshot: dataSnapshot.getChildren()) {
                    // Como solo debe haber un resultado, asignamos los valores a user
                    user = datasnapshot.getValue(User.class);
                    // Rellenamos los EditText con los valores que acabamos de recoger en user
                    mProfileEmail.setText(user.getEmail());
                    mProfilePass.setText(user.getPass());
                    mProfileName.setText(user.getName());
                    mProfileFamily.setText(user.getFamily_name());
                    mProfileCountry.setText(user.getCountry());
                    mProfileZip.setText(user.getZip());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Método para guardar los nuevos datos introducidos por el usuario, recibe un objeto Query
    private void setUserValues (Query currentUserData){
        currentUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorremos el DataSnapshot
                for (DataSnapshot datasnapshot: dataSnapshot.getChildren()) {
                    // Pasamos a user todos los datos introducidos por el usuario
                    user.setName(mProfileName.getText().toString());
                    user.setFamily_name(mProfileFamily.getText().toString());
                    user.setCountry(mProfileCountry.getText().toString());
                    user.setZip(mProfileZip.getText().toString());

                    // Obtenemos la clave de la coincidencia con la Query
                    String key = datasnapshot.getKey();
                    // Actualizamos los valores en Firebase con los nuevos datos de user
                    mDataBase.child(key).setValue(user);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        // Al pulsar atrás, finalizamos la actividad y volvemos a main
        switch (item.getItemId()){
            case android.R.id.home :
                Intent goToMain = new Intent (this, MainActivity.class);
                startActivity(goToMain);
                this.finish();
                return true;
            default:
                return true;
        }
    }

}
