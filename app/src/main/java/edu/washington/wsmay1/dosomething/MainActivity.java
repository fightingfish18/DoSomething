package edu.washington.wsmay1.dosomething;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import android.view.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.Exception.*;
import java.util.concurrent.Callable;

import com.microsoft.windowsazure.mobileservices.*;
import android.content.*;




public class MainActivity extends ActionBarActivity {
    private MobileServiceClient client;
    private MobileServiceUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login = (Button) findViewById(R.id.LoginButton);

        try {
            client = new MobileServiceClient("https://dosomething.azure-mobile.net/", "SIZclmUxUGubaEXCuEXKkKjDlPxBfK77", this);
            Toast.makeText(this, "Client Created", Toast.LENGTH_LONG).show();
        } catch (MalformedURLException ex) {
            System.out.println(ex.toString());
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate();
            }
        });

        //TODO : move to somewhere else
        alertFormElements();
    }

    private void authenticate() {
        client.login(MobileServiceAuthenticationProvider.Facebook, new UserAuthenticationCallback() {
            @Override
            public void onCompleted(MobileServiceUser mobileServiceUser, Exception e, ServiceFilterResponse serviceFilterResponse) {
                user = mobileServiceUser;
                setContentView(R.layout.activity_map);
                Toast.makeText(MainActivity.this, "Successfully Signed in", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
     * Show AlertDialog with some form elements.
     * TODO : move somewhere else
     */
    public void alertFormElements() {

        event e = new event{ id="1", name="sample"  };
        client.getPush();
    /*
     * Inflate the XML view. activity_main is in
     * res/layout/form_elements.xml
     */
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.form_elements,
                null, false);

        // You have to list down your form elements
        final Spinner categorySpinner = (Spinner) formElementsView
                .findViewById(R.id.category_spinner);
        final EditText nameEditText = (EditText) formElementsView
                .findViewById(R.id.nameEditText);



        // the alert dialog
        AlertDialog ok = new AlertDialog.Builder(MainActivity.this).setView(formElementsView)
                .setTitle("Form Elements")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    String toastString = "";
                    // Getting the value of an EditText
                    toastString += "Name is: " + nameEditText.getText() + "!\n";
                    // showToast(toastString);


                    dialog.cancel();
                }
        }).show();
    }

    //private void loggedIn() {

    //}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
