package com.apps.apene.quicktrade;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.apene.quicktrade.model.ExchangeRate;
import com.apps.apene.quicktrade.model.Product;
import com.apps.apene.quicktrade.model.ProductSearch;
import com.apps.apene.quicktrade.model.QuickTradeAdapter;
import com.apps.apene.quicktrade.utils.NetworkUtils;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ExchangeRate> {

    // Elementos gráficos
    protected ImageView mCategoryMotor = null;
    protected ImageView mCategoryHome = null;
    protected ImageView mCategoryTech = null;
    protected Button mButtonUpload = null;
    protected TextView mSearchBy = null;

    // Objetos
    protected FirebaseAuth mAuth = null;
    protected String sCurrentUID = null;
    protected ProductSearch mSearch = null;

    // Objetos para el recycler:
    // Lista para recoger las entradas de los personajes parseadas de la base de datos
    protected List<Product> mListProducts = null;
    // Objeto para referenciar el RecyclerView
    protected RecyclerView mRecyclerView = null;
    // Objeto para referenciar el Adapter que rellenará el RecyclerView
    protected QuickTradeAdapter mAdapter = null;
    // Objeto para referenciar el LayoutManager que dipondrá las vistas del RecyclerView
    protected GridLayoutManager mManager = null;
    // int para definir el número de columnas del GridLayoutManager
    protected static final int NUMBER_OF_COLUMNS = 2;
    // Objeto BBDD para las query
    protected DatabaseReference mDataBase;

    // Variables para ajustar los parámetros de conexión con la API de tipos de cambio del euro
    protected static String SERVER_HTTP_REQUEST_ADDRESS = "https://api.exchangeratesapi.io/latest?symbols=USD";

    // Constantes para el Loader
    protected static final int API_LOADER_ID = 125;
    protected static final String SERVER_ADDRESS_KEY = "SERVER_ADDRESS";

    // String para recoger el tipo de cambio
    protected String mExchangeRate = "";

    // Recuerda conectar con la API de la moneda para actualizar el tipo de cambio al iniciar
    // esta actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Mostramos la barra de acción superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Referencias
        mCategoryMotor = findViewById(R.id.iv_category_motor);
        mCategoryHome = findViewById(R.id.iv_category_home);
        mCategoryTech = findViewById(R.id.iv_category_tech);
        mButtonUpload = findViewById(R.id.bt_main_upload);
        mSearchBy = findViewById(R.id.tv_main_search_in);
        mAuth = FirebaseAuth.getInstance();
        sCurrentUID = mAuth.getCurrentUser().getUid();
        mSearch = new ProductSearch();

        // Referencia del ArrayList que contendrá los objetos Product
        mListProducts = new ArrayList<>();

        mCategoryMotor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = "Vehicles";
                String attribute = "category";
                Intent result = new Intent(MainActivity.this, ResultsView.class);
                result.putExtra("attribute", attribute);
                result.putExtra("value", value);
                startActivity(result);
            }
        });

        mCategoryHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = "Home";
                String attribute = "category";
                Intent result = new Intent(MainActivity.this, ResultsView.class);
                result.putExtra("attribute", attribute);
                result.putExtra("value", value);
                startActivity(result);
            }
        });

        mCategoryTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = "Technology";
                String attribute = "category";
                Intent result = new Intent(MainActivity.this, ResultsView.class);
                result.putExtra("attribute", attribute);
                result.putExtra("value", value);
                startActivity(result);
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToAddProduct = new Intent(getApplicationContext(), AddProduct.class);
                startActivity(goToAddProduct);
            }
        });

        // Ejecutamos el método getAllProducts que rellenará los resultados de últimos productos
        getAllProducts();
    }


    // Método para obtener los productos y mostrarlos en la actividad principal
    public void getAllProducts() {
        mDataBase = FirebaseDatabase.getInstance().getReference();
        Query query = mDataBase.child(getString(R.string.db_node_products));
        Toast.makeText(getApplicationContext(), "iniciamos búsqueda", Toast.LENGTH_LONG).show();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            Product product = new Product();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorremos el DataSnapshot
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    // Pasamos al objeto product cada producto encontrado y lo añadimos al arraylist
                    product = datasnapshot.getValue(Product.class);
                    mListProducts.add(product);
                }
                // Creamos la referencia del Recycler pasándole el elemento gráfico con el método findVeiwById
                mRecyclerView = findViewById(R.id.rv_main_results);

                // Creamos la referncia del adapter pasándole como parámetro a su constructor el List en el que hemos cargado los resultados
                mAdapter = new QuickTradeAdapter(mListProducts);

                // Creamos la referencia del LayoutManager pasándole el contexto (this)
                mManager = new GridLayoutManager(getApplicationContext(), NUMBER_OF_COLUMNS);

                // Cambiamos la orientación del LayoutManager
                mManager.setOrientation(RecyclerView.VERTICAL);

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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(this.getClass().getName(), "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(this.getClass().getName(), "onResume");
        // Referencia del LoaderManager para obtener el tipo de cambio Dollar/Euro
        LoaderManager loaderManager = getSupportLoaderManager();
        // Objeto Bundle, lo usamos para pasar la url al Loader
        Bundle bundle = new Bundle();
        // Metemos en el bundle la dirección del servidor identificada con la clave (SERVER_ADDRESS_KEY)
        bundle.putString(SERVER_ADDRESS_KEY, SERVER_HTTP_REQUEST_ADDRESS);
        // Inicamos el Loader pasándole el ID con el que queremos identificarlo, el bundle y qué callbacks debe implentar
        // para este caso los propios definidos en esta actividad (this).
        loaderManager.initLoader(API_LOADER_ID, bundle, this).forceLoad();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(this.getClass().getName(), "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(this.getClass().getName(), "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(this.getClass().getName(), "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(this.getClass().getName(), "onRestart");
    }

    // Métodos e inner class del Loader. Trabajará en segundo plano para obtener el valor del dollar
    @NonNull
    @Override
    public Loader<ExchangeRate> onCreateLoader(int id, @Nullable Bundle bundle) {
        // Si el id del Loader coincide y el bundel tiene datos (no es nulo)
        if (id == API_LOADER_ID && bundle != null){
            // Obtenemos el String del bundle que coincide con la clave de dirección del servidor
            String urlString = bundle.getString(SERVER_ADDRESS_KEY);
            // Devolvemos un LoadInfoTask al que pasamos por parámetro el contexto y la URL
            return new LoadInfoTask(this, urlString);
        }

        return null;
    }

    // Al finalizar la lectura de los datos del servidor haremos lo siguiente
    @Override
    public void onLoadFinished(@NonNull Loader<ExchangeRate> loader, ExchangeRate exchangeRate) {
        int id = loader.getId();
        if (id == API_LOADER_ID) {
            // pasamos el resultado al objeto ExchangeRate y obtenemos el valor USD para el String
            mExchangeRate = exchangeRate.getUsd();
            // mostramos el tipo de cambio en el TextView dejamos esta parte comentada en espera de nuevos desarrollos
            // mSearchBy.setText(mSearchBy.getText() + "  USD exchange = " + mExchangeRate);
        }
    }
    // resetea los datos de la interfaz gráfica borrando los datos que pudiera tener el String
    @Override
    public void onLoaderReset(@NonNull Loader<ExchangeRate> loader) {
        // Al reinicar el loader vaciamos el string mExchangeRate.
        int id = loader.getId();
        if (id == API_LOADER_ID) {
            mExchangeRate = "";
            mSearchBy.setText(R.string.tv_main_search_category);

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // Definimos el objeto Loader como una inner class que hereda de AsyncTaskLoader y que
    // devuelve un objeto ExchangeRate a través de su método loadInBackground()

    public static class LoadInfoTask extends AsyncTaskLoader<ExchangeRate> {

        // String para la URL
        protected String urlString;

        // Constructor
        public LoadInfoTask (Context ctx, String urlString){
            super(ctx);
            this.urlString = urlString;
        }

        @Nullable
        @Override
        public ExchangeRate loadInBackground() {
            // Devolverá el ExchangeRate que genera el método loadDataFromServer(String) de la clase NetworkUtils
            return NetworkUtils.loadDataFromServer(urlString);
        }
    }
}
