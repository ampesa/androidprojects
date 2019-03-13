package com.apps.apene.quicktrade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class UserSettings extends AppCompatActivity {

    protected FirebaseAuth mAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        // Mostramos la barra de acción en la actividad
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new UserPreferences())
                .commit();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

    }

    // Inflamos el menú con el xml correspondiente
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Definimos las acciones de los items del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Intents para cada selección de items del menú
        switch (item.getItemId()){
            // Al pulsar atrás, finalizamos la actividad y volvemos a main
            case android.R.id.home :
                this.finish();
                return true;
            case R.id.menu_profile :
                Intent goToProfile = new Intent (this, Profile.class);
                startActivity(goToProfile);
                this.finish();
                return true;
            case R.id.menu_settings :
                Intent gotToSettings = new Intent (this, UserSettings.class);
                startActivity(gotToSettings);
                this.finish();
                return true;
            case R.id.menu_search :
                Intent goToAdvancedSearch = new Intent (this, AdvancedSearch.class);
                startActivity(goToAdvancedSearch);
                this.finish();
                return true;
            case R.id.menu_my_products :
                String value = mAuth.getCurrentUser().getUid();
                String attribute = "sellerUID";
                Intent goToMyProducts = new Intent (this, ResultsView.class);
                goToMyProducts.putExtra("attribute", attribute);
                goToMyProducts.putExtra("value", value);
                startActivity(goToMyProducts);
                this.finish();
                return true;
            case R.id.menu_logout :
                mAuth.signOut();
                Intent goToLogin = new Intent (this, LoginActivity.class);
                startActivity(goToLogin);
                this.finish();
                return true;
            default:
                return true;
        }
    }
}
