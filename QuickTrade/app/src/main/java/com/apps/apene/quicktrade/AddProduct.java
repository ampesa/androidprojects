package com.apps.apene.quicktrade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.apene.quicktrade.model.FirebaseAdapter;
import com.apps.apene.quicktrade.model.Product;
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
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Esta clase gestiona la actividad que se crea al pulsar el botón Add desde la actividad principal
 * Solicita al usuario la introducción de datos y guarda un nuevo producto en la BBDD con esos datos
 * Si se inicia desde la actividad de vista del producto permite al usuario actualizar los datos del
 * producto, mostrándole los datos antiguos para que solo tenga que modificar aquellos que considere
 * oportuno
 */
public class AddProduct extends AppCompatActivity implements View.OnClickListener{

    // Elementos gráficos
    protected EditText mProductTitle = null;
    protected ImageView mProductImage = null;
    protected EditText mProductDescription = null;
    protected Spinner mProductSelectCategory = null;
    protected EditText mProductPrice = null;
    protected TextView mProductCurrency = null;
    protected Spinner mProductSelectCountry = null;
    protected EditText mProductZip = null;
    protected Button mButtonAdd = null;
    protected Button mButtonDelete = null;

    // Objetos para las operaciones
    protected FirebaseAuth mAuth = null;
    protected String sUserUID = null;
    protected String sImage = null;
    protected String sParentProductKey = null;
    protected Switch mSold = null;

    // Identificador del Intent para el resultado de abrir la galería
    protected static final int GALLERY_INTENT = 100;

    // Objteo URI para la ruta de la imagen
    protected Uri imageURI = null;

    // Objeto FirebaseAdapter, llamará a los métodos de añadir un nuevo producto
    private FirebaseAdapter fbAdapter = null;

    // Objeto Storage, lo usaremos para guardar la imagen seleccionada en Firebase Storage
    private StorageReference mStorage = null;

    // Objeto BBDD
    protected DatabaseReference mDataBase;

    // String que recoge la clave del producto a editar
    protected String productEditKey = null;

    // Objeto Product de la vista de producto
    protected Product previousProduct = null;
    protected String previousProductKey = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Mostramos la barra de acción superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // referencias
        mProductTitle = findViewById(R.id.et_addproduct_title);
        mProductImage = findViewById(R.id.iv_addproduct_image);
        mProductDescription = findViewById(R.id.et_addproduct_description);
        mProductSelectCategory = findViewById(R.id.spn_addproduct_category);
        mProductPrice = findViewById(R.id.et_addproduct_price);
        mProductCurrency = findViewById(R.id.et_addproduct_price_currency);
        mProductSelectCountry = findViewById(R.id.spn_addproduct_country);
        mProductZip = findViewById(R.id.et_addproduct_zip);
        mButtonAdd = findViewById(R.id.bt_addproduct_add);
        mButtonDelete = findViewById(R.id.bt_addproduct_delete);
        mSold = findViewById(R.id.sv_addproduct_sold);
        mAuth = FirebaseAuth.getInstance();

        // Iniciamos el switch como desactivado
        mSold.setChecked(false);

        // Referencia del Adaptador Firebase
        fbAdapter = new FirebaseAdapter(this);

        // Referencia del Storage
        mStorage = FirebaseStorage.getInstance().getReference();

        // Valor para la imagen por defecto
        sImage = "default_image.png";

        // Adaptador para la lista de categorías
        String[] sProductCategories = getResources().getStringArray(R.array.product_categories);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, sProductCategories);
        mProductSelectCategory.setAdapter(categoryAdapter);

        // Adaptador para la lista de países
        String[] sCountries = getResources().getStringArray(R.array.product_country);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, sCountries);
        mProductSelectCountry.setAdapter(countryAdapter);

        // Si seleccionamos país USA, el símbolo de moneda pasa a dolares
        mProductSelectCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3){
                    mProductCurrency.setText(getString(R.string.tv_add_product_dollar));
                } else {
                    mProductCurrency.setText(getString(R.string.tv_add_product_euro));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Obtenemos el UID del usuario
        mAuth = FirebaseAuth.getInstance();
        sUserUID = mAuth.getUid();

        // Listeners para las acciones de la imagen y el botón
        mProductImage.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);
        mButtonDelete.setOnClickListener(this);

        // Si venimos de la vista de producto para editar el producto hacemos lo siguiente
        productEditKey = getIntent().getStringExtra("key");

        // Si hay clave de producto es que venimos de la vista del producto y no lo estamos añadiendo nuevo
        if (productEditKey != null){
            // Hacemos visible el switch Mark as sold y el botón Delete
            mSold.setVisibility(View.VISIBLE);
            mButtonDelete.setVisibility(View.VISIBLE);
            // Referencia de la bbdd sobre el nodo "products"
            mDataBase = FirebaseDatabase.getInstance().getReference(getString(R.string.db_node_products));

            // Query para buscar el producto que coincide con la clave
            final Query previousProductData = mDataBase.orderByChild("key").equalTo(productEditKey);

            // Obtenemos los datos del producto original para facilitar su edición
            getProductValues(previousProductData);
        }

    }

    // Acciones de la imagen y el botón
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_addproduct_image:
                // Intent para seleccionar la imagen de la galería
                Intent pickPic = new Intent (Intent.ACTION_PICK);
                pickPic.setType("image/*");
                // Si la actividad es correcta se guardará la imagen seleccionada y obtendremos el nombre
                startActivityForResult(pickPic, GALLERY_INTENT);
                break;
            case R.id.bt_addproduct_delete:
                // Al pulsar el botón Delete mostramos un PopupMenu para solicitar confirmación
                PopupMenu confirmDeletePopUp = new PopupMenu(AddProduct.this, mButtonDelete);
                confirmDeletePopUp.getMenuInflater().inflate(R.menu.delete_product, confirmDeletePopUp.getMenu());
                confirmDeletePopUp.show();
                // Listeners de las opciones del PopupMenu
                confirmDeletePopUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete_product_delete){
                            // Si confirma la eliminación, llamamos al método borrar producto
                            fbAdapter.deleteProduct(productEditKey);
                            Toast.makeText(AddProduct.this, getString(R.string.toast_delete_confirmation),
                                    Toast.LENGTH_LONG).show();
                            Intent goToMain = new Intent(AddProduct.this, MainActivity.class);
                            startActivity(goToMain);
                        } else {
                            Toast.makeText(AddProduct.this, getString(R.string.toast_delete_canceled),
                                    Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                });

                break;
            case R.id.bt_addproduct_add:
                // Si todos los campos están rellenados
                if (!TextUtils.isEmpty(mProductTitle.getText().toString())) {
                    if (!TextUtils.isEmpty(mProductDescription.getText().toString())) {
                        if (!TextUtils.isEmpty(mProductPrice.getText().toString())) {
                            if (!TextUtils.isEmpty(mProductZip.getText().toString())) {
                                // Recogemos los datos del producto introducidos por el usuario
                                String title = mProductTitle.getText().toString();
                                String description = mProductDescription.getText().toString();
                                String category = mProductSelectCategory.getSelectedItem().toString();
                                String image;
                                String price = mProductPrice.getText().toString();
                                String country = mProductSelectCountry.getSelectedItem().toString();
                                String zip = mProductZip.getText().toString();
                                String sellerUID = sUserUID;
                                String time = Calendar.getInstance().getTime().toString();
                                String sold = "no";
                                String key = "";

                                // Si venimos de ViewProduct entonces ejecutamos uploadProduct con la clave del producto
                                if (productEditKey != null){
                                    // la clave será la del producto que queremos editar
                                    key = productEditKey;
                                    // Si el switch está marcado el valor de sold pasa a "yes" y añadimos la etiqueta al título
                                    if (mSold.isChecked()){
                                        sold = "yes";
                                        title = title + " **SOLD**";
                                    }
                                    // Si hay imagen la asignamos a image en caso contrario image = default_image
                                    if (imageURI != null){
                                        image = imageURI.toString();
                                    } else {
                                        image = sImage;
                                    }
                                    // Creamos un nuevo Product y le pasamos los datos
                                    Product p = new Product(title, description, category, image, price,
                                            country, zip, sellerUID, time, sold, key);
                                    // Ejecutamos método uploadProduct con los parametros p y clave del producto
                                    fbAdapter.uploadProduct(p);
                                    // Llevamos al usuario de nuevo a ViewProduct
                                    Intent goToProductView = new Intent(getApplicationContext(), ProductView.class);
                                    goToProductView.putExtra("key", key);
                                    startActivity(goToProductView);
                                    this.finish();
                                } else {
                                    // si productEditKey es nulo, es que estamos creando un producto nuevo y la clave estará vacía
                                    // asignamos la imagen
                                    if (imageURI != null){
                                        image = imageURI.toString();
                                    } else {
                                        image = sImage;
                                    }
                                    // Creamos un nuevo Product y le pasamos los datos
                                    Product p = new Product(title, description, category, image, price,
                                            country, zip, sellerUID, time, sold, key);
                                    // Guardamos la imagen el el Strorage llamando al métoddo addProduct con el Product "p" como parámetro
                                    // nos devuelve un string con la clave del producto para usarlo como nombre de la imagen
                                    // de esta manera podremos recuperar la imagen del Storage y asignarla al producto en las búsquedas
                                    String productKey = fbAdapter.addProduct(p);
                                    if (imageURI != null){
                                        StorageReference filePath = mStorage.child(getString(R.string.db_node_images)).child(productKey);
                                        filePath.putFile(imageURI);
                                    }
                                    // Llevamos al usuario a la vista de producto ProductView pasando como extra la clave del producto
                                    Intent goToProductView = new Intent(getApplicationContext(), ProductView.class);
                                    goToProductView.putExtra("key", productKey);
                                    startActivity(goToProductView);
                                    this.finish();
                                }

                            } else {
                                Toast.makeText(AddProduct.this, getString(R.string.signup_no_zip),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddProduct.this, getString(R.string.toast_product_no_price),
                                    Toast.LENGTH_LONG).show();
                        }
                    }  else {
                        Toast.makeText(AddProduct.this, getString(R.string.toast_product_no_description),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddProduct.this, getString(R.string.toast_product_no_title),
                            Toast.LENGTH_LONG).show();
                }
        } // fin del switch
    } // fin de onClick

    // Método que ejecuta el resultado de obtener la imagen de la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Si el requestCode corresponde con el Intent de la Galería y el resultado es OK
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            // Usamos el objeto Uri para recoger la imagen
            imageURI = data.getData();
            // Asignamos la imagen al ImageView
            mProductImage.setImageURI(imageURI);
        } else {
            // Si el intent falla mostramos un toast al usuario y asignamos a sImage el nombre de la imagen por defecto
            Toast.makeText(AddProduct.this, getString(R.string.toast_upload_pic_fail),
                    Toast.LENGTH_LONG).show();
            sImage = "default_image.png";
        }

    }

    // Método para obtener los datos del producto seleccionado recibe un objeto Query
    private void getProductValues (Query selectedProductData){
        selectedProductData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorremos el DataSnapshot
                for (DataSnapshot datasnapshot: dataSnapshot.getChildren()) {
                    // Como solo debe haber un resultado, asignamos los valores a product
                    previousProduct = datasnapshot.getValue(Product.class);
                    // Rellenamos los EditText con los valores que acabamos de recoger en product
                    mProductTitle.setText(previousProduct.getTitle());
                    mProductDescription.setText(previousProduct.getDescription());
                    String category = previousProduct.getCategory();
                    int position = 0;
                    switch (category){
                        case "Vehicles":
                            position = 0;
                            break;
                        case "Home" :
                            position = 1;
                            break;
                        case "Technology" :
                            position = 2;
                    }
                    mProductSelectCategory.setSelection(position);
                    String country = previousProduct.getCountry();
                    int countryPosition = 0;
                    switch (country){
                        case "Spain":
                            countryPosition = 0;
                            break;
                        case "France" :
                            countryPosition = 1;
                            break;
                        case "Germany" :
                            countryPosition = 2;
                            break;
                        case "USA" :
                            countryPosition = 3;
                    }
                    mProductSelectCountry.setSelection(countryPosition);
                    mProductZip.setText(previousProduct.getZip());
                    mProductPrice.setText(previousProduct.getPrice());
                    //sellerUID = previousProduct.getSellerUID();
                }
                Toast.makeText(getApplicationContext(), previousProductKey, Toast.LENGTH_LONG).show();

                // Cambiamos el texto del botón a Save
                mButtonAdd.setText(R.string.bt_profile_save);

                // Recogemos la imagen del storage y la asignamos al imageview
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://quicktrade-9d786.appspot.com/images/"+previousProductKey);
                try {
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
