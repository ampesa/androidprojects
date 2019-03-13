package com.apps.apene.quicktrade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.apene.quicktrade.model.Product;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity con la vista del producto. Muestra los detalles del producto y un botón para editar si
 * se es el propietario del producto o comprar si no se es el propietario
 * */
public class ProductView extends AppCompatActivity {

    // Elementos gráficos
    protected TextView mProductTitle = null;
    protected TextView mUrl = null;
    protected ImageView mProductImage = null;
    protected TextView mProductDescription = null;
    protected TextView mProductCategory = null;
    protected TextView mProductCountry = null;
    protected TextView mProductZip = null;
    protected TextView mProductPrice = null;
    protected TextView mProductCurrency = null;

    // Botón COMPRAR/EDITAR
    protected Button mButtonBuy = null;

    // String para recoger el sellerUID que compararemos con el currentUser
    protected String sellerUID = null;
    // Objeto FirebaseAuth para comprobar si el usuario actual es el propietario del producto
    protected FirebaseAuth mAuth = null;
    // String para recoger el UID del usuario y compararlo con el del vendedor del producto
    protected String sUserUID = null;
    // String para recibir la clave del producto a través del Bundle
    protected String key = null;
    // Objeto Product, recibirá los valores desde Firebase
    protected Product product = null;
    // Objeto BBDD
    protected DatabaseReference mDataBase;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Mostramos la barra de acción superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // referencias
        mUrl = findViewById(R.id.tv_product_view_url);
        mProductTitle = findViewById(R.id.tv_product_view_title);
        mProductImage = findViewById(R.id.iv_product_view_image);
        mProductDescription = findViewById(R.id.tv_product_view_description);
        mProductCategory = findViewById(R.id.tv_product_view_category);
        mProductCountry = findViewById(R.id.tv_product_view_country);
        mProductZip = findViewById(R.id.tv_product_view_zip);
        mProductPrice = findViewById(R.id.tv_product_view_price);
        mProductCurrency = findViewById(R.id.tv_product_view_currency);
        mButtonBuy = findViewById(R.id.bt_product_view_buy);
        mAuth = FirebaseAuth.getInstance();
        sUserUID = mAuth.getUid();

        // Obtenemos la clave del producto a mostrar con el método getData()
        getData();

        // Referencia de la bbdd sobre el nodo "products"
        mDataBase = FirebaseDatabase.getInstance().getReference(getString(R.string.db_node_products));

        // Query para buscar el producto que coincide con la clave
        final Query selectedProductData = mDataBase.orderByChild("key").equalTo(key);

        getProductValues(selectedProductData);


    }
    /**
     * Método para obtener los datos del producto seleccionado recibe un objeto Query,
     * le añade un listener y recoge los datos de la base de datos en un Datasnapshot.
     * La consulta es sobre la clave del producto, por lo que solo puede haber un resultado y no
     * es necesario un Array, simplemente pasamos el resultado a "product" y con los datos de este
     * rellenamos las vistas.
     * */
    private void getProductValues (Query selectedProductData){
        selectedProductData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorremos el DataSnapshot
                for (DataSnapshot datasnapshot: dataSnapshot.getChildren()) {
                    // Como solo debe haber un resultado, asignamos los valores a product
                    product = datasnapshot.getValue(Product.class);
                    // Rellenamos los EditText con los valores que acabamos de recoger en product
                    mProductTitle.setText(product.getTitle());
                    mProductDescription.setText(product.getDescription());
                    mProductCategory.setText(product.getCategory());
                    mProductCountry.setText(product.getCountry());
                    mProductZip.setText(product.getZip());
                    mProductPrice.setText(product.getPrice());
                    sellerUID = product.getSellerUID();
                }
                Toast.makeText(getApplicationContext(), key, Toast.LENGTH_LONG).show();

                // Si el usuario actual es el propietario del producto cambiamos el texto del botón a Edit
                // En caso contrario se muestra Buy
                if (sUserUID.equals(sellerUID)){
                    mButtonBuy.setText(R.string.bt_product_edit);
                }

                // Descargamos la foto del producto desde el Storage y la asignamos al ImageView
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://quicktrade-9d786.appspot.com/images/"+key);
                try {
                    // Creamos un archivo temporal en el que recibimos la imagen (bitmap) y la pasamos al ImageView
                    final File localFile = File.createTempFile("images", "jpeg");
                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            mProductImage.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
                } catch (IOException e ) {}

                // Acciones del botón BUY / EDIT
                mButtonBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Si el botón es comprar mostramos un toast confirmando la compra
                        if (mButtonBuy.getText().equals(getString(R.string.bt_product_buy))){
                            Toast.makeText(getApplicationContext(), getString(R.string.toast_buy_confirmation), Toast.LENGTH_LONG).show();
                        } else { // Si no, llevamos al usuario a la actividad de addproduct para editar el producto
                            Intent goToAddProduct = new Intent(ProductView.this, AddProduct.class);
                            goToAddProduct.putExtra("key", key);
                            startActivity(goToAddProduct);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Método que recoge los extras de la actividad padre
    protected void getData(){
        Bundle extras = getIntent().getExtras();
        key = extras.getString("key");
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
                Intent goToMain = new Intent (this, MainActivity.class);
                startActivity(goToMain);
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
