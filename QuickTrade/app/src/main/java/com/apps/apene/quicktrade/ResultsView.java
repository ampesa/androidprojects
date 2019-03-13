package com.apps.apene.quicktrade;

import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.apps.apene.quicktrade.model.Product;
import com.apps.apene.quicktrade.model.ProductSearch;
import com.apps.apene.quicktrade.model.QuickTradeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ResultsView extends AppCompatActivity {

    protected FirebaseAuth mAuth = null;

    // Lista para recoger las entradas de los personajes parseadas de la base de datos
    protected List<Product> mListProducts = null;

    // Objeto para referenciar el RecyclerView
    protected RecyclerView mRecyclerView = null;

    // Objeto para referenciar el Adapter que rellenará el RecyclerView
    protected QuickTradeAdapter mAdapter = null;

    // Objeto para referenciar el LayoutManager que dipondrá las vistas del RecyclerView
    protected GridLayoutManager mManager = null;

    // Estrings que recogeran los extras con los valores de la búsqueda
    protected String attribute = null;
    protected String value = null;
    protected String category = null;
    protected String country = null;
    protected String email = null;

    // int para definir el número de columnas del GridLayoutManager
    protected static final int NUMBER_OF_COLUMNS = 2;

    protected DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_view);

        // Mostramos la barra de acción superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        // Referencia del ArrayList que contendrá los objetos Product
        mListProducts = new ArrayList<>();

        // Recogemos los valores recibidos en los extras con el método getData
        getData();

        if (category != null){
            attribute = "category";
            value = category;
            advancedSearch(attribute,category);

        } else {
            // Ejecutamos método getProducts y pasamos y rellenamos el ArrayList mListProducts con los resultados
            //mListProducts.addAll(search.getProducts(attribute,value));
            getProducts(attribute, value);
        }

    }

    protected void getData(){
        Bundle extras = getIntent().getExtras();
        attribute = extras.getString("attribute");
        value = extras.getString("value");
        category = extras.getString("category");
        country = extras.getString("country");
        email = extras.getString("email");
    }

    // Método para obtener los productos buscados. Recibe dos strings y devuelve un ArrrayList de productos
    public void getProducts (String attribute, String value){
        mDataBase = FirebaseDatabase.getInstance().getReference("products");
        Query query = mDataBase.orderByChild(attribute).equalTo(value);
        Toast.makeText(getApplicationContext(), "iniciamos búsqueda", Toast.LENGTH_LONG).show();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            Product product = new Product();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorremos el DataSnapshot
                Toast.makeText(getApplicationContext(), "continuamos búsqueda", Toast.LENGTH_LONG).show();
                int cont = 0;
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    // Como solo debe haber un resultado, asignamos los valores a product
                    product = datasnapshot.getValue(Product.class);
                    cont++;
                    mListProducts.add(product);
                    Toast.makeText(getApplicationContext(), "He encontrado " + cont, Toast.LENGTH_LONG).show();

                }
                // Creamos la referencia del Recycler pasándole el elemento gráfico con el método findVeiwById
                mRecyclerView = findViewById(R.id.rv_results_viewer);

                // Creamos la referncia del adapter pasándole como parámetro a su constructor el List en el que hemos cargado los resultados
                mAdapter = new QuickTradeAdapter(mListProducts);

                // Creamos la referencia del LayoutManager pasándole el contexto (this)
                mManager = new GridLayoutManager(getApplicationContext(), NUMBER_OF_COLUMNS);

                // Cambiamos la orientación del LayoutManager
                mManager.setOrientation(LinearLayoutManager.VERTICAL);

                // Creadas las referencias le asignamos al RecyclerView su Adapter u su LayoutManager
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mManager);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Método para obtener los productos buscados en la búsqueda avanzada
    public void advancedSearch (String attribute, String value){
        mDataBase = FirebaseDatabase.getInstance().getReference("products");
        Query query = mDataBase.orderByChild(attribute).equalTo(value);
        Toast.makeText(getApplicationContext(), "iniciamos búsqueda", Toast.LENGTH_LONG).show();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            Product product = new Product();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorremos el DataSnapshot
                Toast.makeText(getApplicationContext(), "continuamos búsqueda", Toast.LENGTH_LONG).show();
                int cont = 0;
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    // Para cada datasnapshot recogemos el objto producto y, si cumple el resto de condiciones lo añadimos al array
                    product = datasnapshot.getValue(Product.class);
                    if (product.getCountry().equals(country)){
                        mListProducts.add(product);

                        //if (product.getSellerUID().equals(email)){
                        //}
                    }
                    cont++;
                    Toast.makeText(getApplicationContext(), "He encontrado " + cont, Toast.LENGTH_LONG).show();

                }
                // Creamos la referencia del Recycler pasándole el elemento gráfico con el método findVeiwById
                mRecyclerView = findViewById(R.id.rv_results_viewer);

                // Creamos la referncia del adapter pasándole como parámetro a su constructor el List en el que hemos cargado los resultados
                mAdapter = new QuickTradeAdapter(mListProducts);

                // Creamos la referencia del LayoutManager pasándole el contexto (this)
                mManager = new GridLayoutManager(getApplicationContext(), NUMBER_OF_COLUMNS);

                // Cambiamos la orientación del LayoutManager
                mManager.setOrientation(LinearLayoutManager.VERTICAL);

                // Creadas las referencias le asignamos al RecyclerView su Adapter u su LayoutManager
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(mManager);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                return true;
            case R.id.menu_settings :
                Intent gotToSettings = new Intent (this, UserSettings.class);
                startActivity(gotToSettings);
                return true;
            case R.id.menu_search :
                Intent goToAdvancedSearch = new Intent (this, AdvancedSearch.class);
                startActivity(goToAdvancedSearch);
                return true;
            case R.id.menu_my_products :
                String value = mAuth.getCurrentUser().getUid();
                String attribute = "sellerUID";
                Intent goToMyProducts = new Intent (this, ResultsView.class);
                goToMyProducts.putExtra("attribute", attribute);
                goToMyProducts.putExtra("value", value);
                startActivity(goToMyProducts);
                return true;
            case R.id.menu_logout :
                mAuth.signOut();
                Intent goToLogin = new Intent (this, LoginActivity.class);
                startActivity(goToLogin);
                return true;
            default:
                return true;
        }
    }
}
