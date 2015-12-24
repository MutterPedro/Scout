package com.br.scout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.br.scout.backend.DatabaseOperations;
import com.br.scout.beans.User;
import com.br.scout.widget.Utility;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_confirm) {
            User user = new User();
            user.setName(String.valueOf(((EditText) findViewById(R.id.name_text)).getText()));
            user.setEmail(String.valueOf(((EditText) findViewById(R.id.email_text)).getText()));
            user.setPassword(String.valueOf(((EditText) findViewById(R.id.password_text)).getText()));
            user.setSpecial(((Switch) findViewById(R.id.special_switch)).isChecked());
            try {
                DatabaseOperations db = new DatabaseOperations(this);
                db.addUser(user);
                Utility.USER = user;
                //Snackbar.make((View) this.getCurrentFocus().getParent(), "Cadastro realizado com sucesso, bem-vindo "+user.getName(), Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
                Toast.makeText(this,"Cadastro realizado com sucesso, bem-vindo "+user.getName(),Toast.LENGTH_LONG).show();
                finish();
                startActivity(new Intent(this,MapsActivity.class));
            } catch (Exception e){
                new AlertDialog.Builder(this).setTitle("Erro").setMessage("Não foi possível relaizar o cadastro").setPositiveButton("Ok",null).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
