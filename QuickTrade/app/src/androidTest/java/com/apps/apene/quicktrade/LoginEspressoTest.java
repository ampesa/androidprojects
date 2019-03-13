package com.apps.apene.quicktrade;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.util.regex.Matcher;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.PositionAssertions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.intent.Intents.intended;
import static org.hamcrest.Matchers.anything;

public class LoginEspressoTest {

    // Referenciamos la actividad LoginActivity
    @Rule
    public ActivityTestRule<LoginActivity> loginActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

    // Para el test de crear un nuevo producto utilizaremos IntentsTestRule
    //@Rule
    //public IntentsTestRule<AddProduct> intentsTestRule = new IntentsTestRule<>(AddProduct.class);

    /**
     * Creamos un test endToEnd con el que evaluamos el fincionamiento de los EditText (email y pass) y el funcionamiento
     * del botón Login. Comprobamos que al loguearnos nos lleva a la actividad principal.
     */

    @Test
    public void endToEndLogin() throws Exception {

        // Escribimos el correo de test para realizar el test
        Espresso.onView(ViewMatchers.withHint("Email")).perform(ViewActions.typeText("test@test.com"));
        // Escribimos la contraseña de test
        Espresso.onView(ViewMatchers.withHint("Password")).perform(ViewActions.typeText("testing"));
        // Escondemos el teclado
        Espresso.closeSoftKeyboard();
        // Iniciamos los intents para poder seguir su actividad
        Intents.init();
        // Hacemos click en el botón Login
        Espresso.onView(ViewMatchers.withId(R.id.bt_login_sign_in)).perform(ViewActions.click());
        // Dejamos un tiempo para que se conecte a firebase, autentique al usuario e inicie la actividad principal
        Thread.sleep(5000);
        // Comprobamos que nos lleva a la actividad principal, de no ser así el test fallaría, podemo probarlo cambiando MainActivito por otra actividad
        intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
        Intents.release();
        tryToAddProduct();

    }

    public void tryToAddProduct() throws Exception {
        // Iniciamos los intents para poder seguir su actividad
        Intents.init();
        // Hacemos click en el botón Upload
        Espresso.onView(ViewMatchers.withId(R.id.bt_main_upload)).perform(ViewActions.click());
        // Comprobamos que se inicia la nueva actividad
        intended(IntentMatchers.hasComponent(AddProduct.class.getName()));
        Intents.release();

        // Completamos un nuevo producto
        // Añadimos el título
        Espresso.onView(ViewMatchers.withId(R.id.et_addproduct_title)).perform(ViewActions.typeText("test product"));
        // Escondemos el teclado
        Espresso.closeSoftKeyboard();
        // Añadimos la descripción
        Espresso.onView(ViewMatchers.withId(R.id.et_addproduct_description)).
                perform(ViewActions.typeText("We are testing AddProduct activity"));
        // Escondemos el teclado
        Espresso.closeSoftKeyboard();
        // Seleccionamos una categoría del spinner
        Espresso.onView(ViewMatchers.withId(R.id.spn_addproduct_category)).perform(ViewActions.click());
        Espresso.onData(anything()).atPosition(1).perform(ViewActions.click());
        // Seleccionamos una país del spinner
        Espresso.onView(ViewMatchers.withId(R.id.spn_addproduct_country)).perform(ViewActions.click());
        Espresso.onData(anything()).atPosition(3).perform(ViewActions.click());
        // Nos aseguramos que el símbolo de la moneda a cambiado a dolares porque el país es USA
        Espresso.onView(ViewMatchers.withId(R.id.et_addproduct_price_currency)).
                check(ViewAssertions.matches(ViewMatchers.withText("$")));
        // Completamos el zip code
        Espresso.onView(ViewMatchers.withId(R.id.et_addproduct_zip)).perform(ViewActions.typeText("00000"));
        // Escondemos el teclado
        Espresso.closeSoftKeyboard();
        // Añadimos el precio
        Espresso.onView(ViewMatchers.withId(R.id.et_addproduct_price)).perform(ViewActions.typeText("100"));
        // Escondemos el teclado
        Espresso.closeSoftKeyboard();
        // Probamos a añadir el producto y lo testeamos
        testAddedProduct();

    }

    public void testAddedProduct() throws Exception {
        // Iniciamos los intents para poder seguir su actividad
        Intents.init();
        // Hacemos click en el botón add
        Espresso.onView(ViewMatchers.withId(R.id.bt_addproduct_add)).perform(ViewActions.click());
        Thread.sleep(5000);
        // Comprobamos que se inicia la nueva actividad
        intended(IntentMatchers.hasComponent(ProductView.class.getName()));
        Intents.release();
        // Comprobamos el título
        Espresso.onView(ViewMatchers.withId(R.id.tv_product_view_title)).
                check(ViewAssertions.matches(ViewMatchers.withText("test product")));
        // Comprobamos la descripción
        Espresso.onView(ViewMatchers.withId(R.id.tv_product_view_description)).
                check(ViewAssertions.matches(ViewMatchers.withText("We are testing AddProduct activity")));
        // Comprobamos la categoría
        Espresso.onView(ViewMatchers.withId(R.id.tv_product_view_category)).
                check(ViewAssertions.matches(ViewMatchers.withText("Home")));
        // Comprobamos el país
        Espresso.onView(ViewMatchers.withId(R.id.tv_product_view_country)).
                check(ViewAssertions.matches(ViewMatchers.withText("USA")));
        // Comprobamos el zip code
        Espresso.onView(ViewMatchers.withId(R.id.tv_product_view_zip)).
                check(ViewAssertions.matches(ViewMatchers.withText("00000")));
        // Comprobamos el precio
        Espresso.onView(ViewMatchers.withId(R.id.tv_product_view_price)).
                check(ViewAssertions.matches(ViewMatchers.withText("100")));
        // Comprobado el producto procedemos a intentar eliminarlo
        tryToEditAndDelete();
    }

    public void tryToEditAndDelete() throws Exception {
        // Iniciamos los intents para poder seguir su actividad
        Intents.init();
        // Hacemos click en el botón add
        Espresso.onView(ViewMatchers.withId(R.id.bt_product_view_buy)).perform(ViewActions.scrollTo()).perform(ViewActions.click());
        Thread.sleep(5000);
        // Comprobamos que se inicia la nueva actividad
        intended(IntentMatchers.hasComponent(AddProduct.class.getName()));
        // Comprobamos que el intent tiene datos con la clave "key"
        intended(IntentMatchers.hasExtraWithKey("key"));
        Intents.release();
        // Escondemos el teclado
        Espresso.closeSoftKeyboard();
        // Eliminamos el producto creado
        Espresso.onView(ViewMatchers.withId(R.id.bt_addproduct_delete)).perform(ViewActions.scrollTo()).perform(ViewActions.click());
        Espresso.onData(anything()).atPosition(0).perform(ViewActions.click());
    }
}
