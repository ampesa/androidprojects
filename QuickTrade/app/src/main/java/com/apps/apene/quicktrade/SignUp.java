package com.apps.apene.quicktrade;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.apps.apene.quicktrade.model.FirebaseAdapter;
import com.apps.apene.quicktrade.model.User;

import androidx.appcompat.app.AppCompatActivity;

// Actividad de registro del usuario
public class SignUp extends AppCompatActivity {

    // Elementos gráficos
    protected EditText mSignUpEmail = null;
    protected EditText mSignUpPass = null;
    protected EditText mSignUpName = null;
    protected EditText mSignUpFamily = null;
    protected EditText mSignUpCountry = null;
    protected EditText mSignUpZip = null;
    protected Button mButtonSignUp = null;

    // Objeto FirbaseAdapter, llamará a los métodos de registro del usuario en Firebase
    FirebaseAdapter fbAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Mostramos la barra de acción superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Referencias
        mSignUpEmail = findViewById(R.id.et_signup_email);
        mSignUpPass = findViewById(R.id.et_signup_pass);
        mSignUpName = findViewById(R.id.et_signup_name);
        mSignUpFamily = findViewById(R.id.et_signup_family);
        mSignUpCountry = findViewById(R.id.et_signup_country);
        mSignUpZip = findViewById(R.id.et_signup_zip);
        mButtonSignUp = findViewById(R.id.bt_sign_up);

        // Adaptador Firebase
        fbAdapter = new FirebaseAdapter(this);

        // Acción del botón SIGN UP
        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(mSignUpEmail.getText().toString())) {
                    if (mSignUpPass.getText().toString().length()>= 6) {
                        if (!TextUtils.isEmpty(mSignUpName.getText().toString())) {
                            if (!TextUtils.isEmpty(mSignUpFamily.getText().toString())) {
                                if (!TextUtils.isEmpty(mSignUpCountry.getText().toString())) {
                                    if (!TextUtils.isEmpty(mSignUpZip.getText().toString())) {
                                        // Recogemos los datos introducidos por el usuario los usaremos para crear un User
                                        String email = mSignUpEmail.getText().toString();
                                        String pass = mSignUpPass.getText().toString();
                                        String name = mSignUpName.getText().toString();
                                        String family_name = mSignUpFamily.getText().toString();
                                        String country = mSignUpCountry.getText().toString();
                                        String zip = mSignUpZip.getText().toString();

                                        // Creamos un User con los datos recogidos
                                        User u = new User(email, pass, name, family_name, country, zip);
                                        // Llamaos al método signUp con el User "u" como parámetro para registrar al usuario
                                        fbAdapter.signUp(u);

                                    } else { // Si faltan datos se lo indicamos al usuario con un Toast
                                        Toast.makeText(SignUp.this, getString(R.string.signup_no_zip),
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(SignUp.this, getString(R.string.signup_no_country),
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(SignUp.this, getString(R.string.signup_no_family),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(SignUp.this, getString(R.string.signup_no_name),
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SignUp.this, getString(R.string.signup_no_password),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SignUp.this, getString(R.string.signup_no_email),
                            Toast.LENGTH_LONG).show();
                }
            } // fin onClick
        }); // fin listener

    } // fin onCreate

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        // Al pulsar atrás, finalizamos la actividad y volvemos a main
        switch (item.getItemId()){
            case android.R.id.home :
                Intent goToLogin = new Intent (this, LoginActivity.class);
                startActivity(goToLogin);
                this.finish();
                return true;
            default:
                return true;
        }
    }
}
