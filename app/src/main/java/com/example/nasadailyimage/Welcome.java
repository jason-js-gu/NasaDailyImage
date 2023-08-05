package com.example.nasadailyimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

/**
 * @author Jinsheng Gu
 */
public class Welcome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //toolbar, navigation
        handleCommonComponents();
        // set checkbox listner to show the snackbar
        CheckBox chxRemember = findViewById(R.id.chxRemeber);
        chxRemember.setOnCheckedChangeListener((cpt, isChecked)->{
                String msg = isChecked ? this.getString(R.string.snackbar_message_on)
                        : this.getString(R.string.snackbar_message_off);
                Snackbar.make(chxRemember, msg, Snackbar.LENGTH_LONG)
                        .setAction(this.getString(R.string.undo),
                                click->cpt.setChecked(!isChecked)).show();
        });

        EditText etName = findViewById(R.id.etName);
        //sharedPreferences
        SharedPreferences sps = getPreferences(MODE_PRIVATE);
        String name = sps.getString("name", null);
        if(name != null && !"".equals(name)){
            TextView tvGreeting = findViewById(R.id.tvGreeting);
            tvGreeting.setText(getString(R.string.welcome));
            chxRemember.setActivated(true);
            etName.setText(name);
            //hide checkbox
            chxRemember.setVisibility(View.INVISIBLE);
        }else {
            chxRemember.setVisibility(View.VISIBLE);
        }

        // set button listner to save information to sharedpreferences or show toast
        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener((click)->{
            String inputName = etName.getText().toString();

            if(!"".equals(inputName) && chxRemember.isChecked()){
                //save name into shared preferences
                SharedPreferences.Editor editor = sps.edit();
                editor.putString("name", inputName);
                editor.commit();
                //jump to skywalker page
                Intent intent = new Intent(Welcome.this, Skywalker.class);
                startActivity(intent);
            }else if(!"".equals(inputName)){
                Intent intent = new Intent(Welcome.this, Skywalker.class);
                intent.putExtra("name", inputName);
                startActivity(intent);
            } else{
                Toast.makeText(this, "You need to input a valid name",Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * method to handle common components which appear on all pages
     */
    public void handleCommonComponents(){
        Toolbar tBar = findViewById(R.id.toolbar);
        tBar.setTitle(this.getClass().getSimpleName());
        setSupportActionBar(tBar);
        //toggle button
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, tBar,
                R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //listen navigationview
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    /**
     * define menu icon action
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                int id = item.getItemId();
        int id1 = R.id.help;

        if(id == id1){
            AlertDialog.Builder builder = new AlertDialog.Builder(Welcome.this);
            builder.setTitle(R.string.alert_title);
            String activity_name = this.getClass().getSimpleName();
            String help_msg = "";
            if(activity_name.equals(this.getString(R.string.welcome_activity_name))){
                help_msg = this.getString(R.string.welcome_activity_help);
            }else if(activity_name.equals(this.getString(R.string.skywalker_activity_name))){
                help_msg = this.getString(R.string.skywalker_activity_help);
            }else if(activity_name.equals(this.getString(R.string.archive_activity_name))){
                help_msg = this.getString(R.string.archive_activity_help);
            }else if(activity_name.equals(this.getString(R.string.about_activity_name))){
                help_msg = this.getString(R.string.about_activity_help);
            }
            builder.setMessage(help_msg);
            builder.setPositiveButton("OK", (DialogInterface.OnClickListener)(dialog, which)->{
                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return true;
    }

    /**
     * define menu item action
     * @param item The selected item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        int id1 = R.id.home;
        int id2 = R.id.magic;
        int id3 = R.id.archive;
        int id4 = R.id.about;

        if(id == id1){
            Intent intent = new Intent(this, Welcome.class);
            this.startActivity(intent);
        }else if(id == id2){
            Intent intent = new Intent(this, Skywalker.class);
            this.startActivity(intent);
        }else if(id == id3){
            Intent intent = new Intent(this, MyArchive.class);
            this.startActivity(intent);
        }else if(id == id4){
            Intent intent = new Intent(this, About.class);
            this.startActivity(intent);
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}