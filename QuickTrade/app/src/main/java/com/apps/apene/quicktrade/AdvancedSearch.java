package com.apps.apene.quicktrade;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Clae que gestiona la búsqueda avanzada para buscar por varios parámetros a la vez
 * Solo están implementadas la búsqueda por categoría y por país. Falta desarrollar la búsqueda
 * por email y las búsquedas generales (all).
 * */
public class AdvancedSearch extends AppCompatActivity {

    // Objetos
    protected Spinner mCategorySpinner = null;
    protected Spinner mCountrySpinner = null;
    protected EditText mEmail = null;
    protected Button mSearchButton = null;
    protected FirebaseAuth mAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        // Mostramos la barra de acción superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Referencias
        mCategorySpinner = findViewById(R.id.spn_advanced_category);
        mCountrySpinner = findViewById(R.id.spn_advanced_country);
        mEmail = findViewById(R.id.et_advanced_email);
        mSearchButton = findViewById(R.id.bt_advanced_search);

        // Instancia de autenticación para recuperar el uid del usuario
        mAuth = FirebaseAuth.getInstance();

        // Adaptador para la lista de categorías
        String[] sProductCategories = getResources().getStringArray(R.array.product_search_categories);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, sProductCategories);
        mCategorySpinner.setAdapter(categoryAdapter);

        // Adaptador para la lista de países
        String[] sCountries = getResources().getStringArray(R.array.product_search_country);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, sCountries);
        mCountrySpinner.setAdapter(countryAdapter);

        // Listener acción del botón SEARCH
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtenemos los valores introducidos por el usuario y los pasamos a los strings
                String category = mCategorySpinner.getSelectedItem().toString();
                String country = mCountrySpinner.getSelectedItem().toString();
                String email = mEmail.getText().toString();

                // Llevamos al usuario a los resultados de la búsqueda pasando los parámetros seleccionados
                Intent search = new Intent (AdvancedSearch.this, ResultsView.class);
                search.putExtra("category", category);
                search.putExtra("country", country);
                search.putExtra("email", email);
                startActivity(search);
            }
        });


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
