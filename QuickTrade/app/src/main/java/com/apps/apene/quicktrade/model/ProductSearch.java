package com.apps.apene.quicktrade.model;

//import android.support.annotation.NonNull;

import com.apps.apene.quicktrade.R;
import com.apps.apene.quicktrade.ResultsView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import androidx.annotation.NonNull;

/**
 * Clase creada para albergar los métodos de consulta de la base de datos. Finalmente los métodos se han
 * implementado en las propias vistas que necesitan las consultas para evitar problemas de sincronía de datos
 * y evitar que los objetos queden nulos cuando son accedidos para mostrarlos.
 * */
public class ProductSearch {
    // Variables de búsqueda
    protected String attribute = null;
    protected String value = null;
    protected Product product = null;
    protected ArrayList<Product> productsList = null;
    // String para recoger el sellerUID que compararemos con el currentUser
    protected String sellerUID = null;
    // Objeto FirebaseAuth para comprobar si el usuario actual es el propietario del producto
    protected FirebaseAuth mAuth = null;
    // String para recoger el UID del usuario y compararlo con el del vendedor del producto
    protected String sUserUID = null;
    // String para recibir la clave del producto a través del Bundle
    protected String key = null;
    // Objeto BBDD
    protected DatabaseReference mDataBase;

    // Constructores
    public ProductSearch () {
    }

    /**
     * Método para búscar productos por atributo y valor de ese atributo
     * Recibe dos String, uno con el atributo y otro con el valor.
     * Devuelve un ArrayList con los productos coincidentes
     * */
    public ArrayList<Product> getProducts (String attribute, String value){
        mDataBase = FirebaseDatabase.getInstance().getReference("products");
        Query query = mDataBase.orderByChild(attribute).equalTo(value);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorremos el DataSnapshot
                for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
                    // Como solo debe haber un resultado, asignamos los valores a product
                    product = datasnapshot.getValue(Product.class);
                    productsList.add(product);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return productsList;
    }

}
