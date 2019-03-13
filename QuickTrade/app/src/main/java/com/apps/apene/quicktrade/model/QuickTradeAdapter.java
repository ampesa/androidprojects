package com.apps.apene.quicktrade.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.apene.quicktrade.ProductView;
import com.apps.apene.quicktrade.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Clase adaptador que maneja el RecyclerView que muestra los resultados de búsquedas y de la pantalla
 * principal
 * */
public class QuickTradeAdapter extends RecyclerView.Adapter<QuickTradeAdapter.QuickTradeViewHolder>{

    // Objeto List para recibir el ArrayList de productos a mostrar
    protected List<Product> mProducts;

    // Constructor de la clase, recibe un List y lo asigna a la lista de productos mProducts
    public QuickTradeAdapter(List<Product> productsList){
        // Le asignamos a mProducts el ArrayList que recibe el constructor con la selección de objetos Product
        mProducts = new ArrayList<Product>(productsList);
    }

    /**
     * Método onCreateViewHolder crea una vista (view) y la rellena (inflate) con el layout definicio para el recycler
     * */
    @NonNull
    @Override
    public QuickTradeAdapter.QuickTradeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.quick_trade_view_holder_layout, viewGroup, false);

        // El médoto devuelve el ViewHolder "inflado"
        return new QuickTradeViewHolder(view);
    }

    /**
     * Método onBindViewHolder se asigna los valores del array a las vistas de cada ViewHolder. Utiliza el int position como
     * índice de referencia*/
    @Override
    public void onBindViewHolder(@NonNull final QuickTradeAdapter.QuickTradeViewHolder quickTradeViewHolder, int position) {

        // product recibe el objeto de la posición
        Product product = mProducts.get(position);
        // Asignamos los valores de product a las vistas
        quickTradeViewHolder.mProductTitle.setText(product.getTitle());
        quickTradeViewHolder.mProductPrice.setText(product.getPrice()+"€");
        // Obtenemos la clave del producto para identificar su imagen en el Storage
        quickTradeViewHolder.mKey = product.getKey();


        // Descargamos la foto del producto desde el Storage y la asignamos al ImageView
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://quicktrade-9d786.appspot.com/images/"+
                quickTradeViewHolder.mKey);
        try {
            // Creamos un archivo temporal en el que recibimos la imagen (bitmap) y la pasamos al ImageView
            final File localFile = File.createTempFile("images", "jpeg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    quickTradeViewHolder.mProductImage.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {}

    }

    /**
     * Método que devuelve el total de elementos que podrá mostrar el Recycler
     * */
    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    /**
     * Clase QuickTradeViewHolder extiende ViewHolder e implementa OnClickListener para manejar con más
     * facilidad los listeners de las vistas*/
    public class QuickTradeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Elementos del ViewHolder
        protected ImageView mProductImage = null;
        protected TextView mProductTitle = null;
        protected TextView mProductPrice = null;
        protected String mKey = null;
        protected Context mContext = null;

        // Constructor de la clase
        public QuickTradeViewHolder(@NonNull View itemView) {
            super(itemView);

            // Asignamos a cada View su correspondiente elemento gráfico del xml
            mProductImage = itemView.findViewById(R.id.iv_results_image);
            mProductTitle = itemView.findViewById(R.id.tv_results_title);
            mProductPrice = itemView.findViewById(R.id.tv_results_price);
            // Añadimos listeners a las vistas que tendrán interacción
            mProductImage.setOnClickListener(this);
            mProductTitle.setOnClickListener(this);

            mContext = itemView.getContext();
        }

        // Manejamos los clicks
        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            // Si el usuario pulsa la imagen o el título mostramos un toast con la clave de producto y ejecutamos
            // el método showProductDetails con la clave de producto como parámetro
            if (viewId == mProductImage.getId() || viewId == mProductTitle.getId()){
                // Si pulsa en la imagen o en n los datos del producto, lo llevamos a la vista del producto
                Toast.makeText(mContext, mKey, Toast.LENGTH_LONG).show();
                showProductDetails(mKey);
            }

        }
        /**
         * Método showProductDetails, recibe la clave de producto como parametro y lanza un intent
         * con la clave como extra que lleva al usuario a la vista del producto*/
        public void showProductDetails(String key){

            Intent showProduct = new Intent(mContext, ProductView.class);
            // Pasamos el int al Bundle
            showProduct.putExtra("key", key);
            // Iniciamos la actividad
            mContext.startActivity(showProduct);
        }
    }
}
