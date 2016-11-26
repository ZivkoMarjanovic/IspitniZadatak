package com.example.zivko.glumacactivity;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.List;

import db.DataBaseHelper;
import objects.Glumac;

public class ListActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    ListView listaGlumaca;
    List<Glumac> glumci;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_glumaca);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        toolbar.setTitle("Actors");
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);

        listaGlumaca = (ListView) findViewById(R.id.listaGlumaca);

        try {
            glumci = getDatabaseHelper().getGlumacDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (glumci != null) {

            ListAdapter adapter = new ArrayAdapter<Glumac>(this, R.layout.list_item, glumci);
            listaGlumaca.setAdapter(adapter);
            listaGlumaca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Glumac glumac = (Glumac) listaGlumaca.getItemAtPosition(i);
                    Intent intent = new Intent(ListActivity.this, DeatailActivity.class);
                    intent.putExtra("POSITION", glumac.getId());
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_item_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(ListActivity.this, Settings.class);
                startActivity(intent);
                break;

            case R.id.About:
                About about = new About();
                about.show(getSupportFragmentManager(), "ABOUT");
                break;


            case R.id.action_add:

                final Dialog dialog = new Dialog(this);
                dialog.setTitle("Enter new Glumac");
                dialog.setContentView(R.layout.dialog_add_glumac);

                final EditText imePrezime = (EditText) dialog.findViewById(R.id.imePrezime);
                final EditText rodjen = (EditText) dialog.findViewById(R.id.rodjen);
                final EditText ocena = (EditText) dialog.findViewById(R.id.ocena);
                final EditText biografija = (EditText) dialog.findViewById(R.id.biografija);


                Button ok = (Button) dialog.findViewById(R.id.save);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Glumac newGlumac = new Glumac();

                        newGlumac.setImePrezime(imePrezime.getText().toString());
                        newGlumac.setRodjen(rodjen.getText().toString());
                        newGlumac.setOcena(Integer.parseInt(ocena.getText().toString()));
                        newGlumac.setBigrafija(biografija.getText().toString());

                        try {
                            getDatabaseHelper().getGlumacDao().create(newGlumac);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        refreshGlumac();

                        showMessage("Napravljen je novi glumac", newGlumac.getImePrezime());

                        dialog.dismiss();

                    }
                });

                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }

                });
                dialog.show();
                if( dialog.isShowing()) {dialog.getWindow().setBackgroundDrawableResource(R.color.filmDialog);}
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshGlumac();
    }

    private void refreshGlumac() {

        ListView listview = (ListView) findViewById(R.id.listaGlumaca);

        if (listview != null) {
            ArrayAdapter<Glumac> adapter = (ArrayAdapter<Glumac>) listview.getAdapter();

            if (adapter != null) {
                try {
                    adapter.clear();
                    List<Glumac> list = getDatabaseHelper().getGlumacDao().queryForAll();

                    adapter.addAll(list);

                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void showMessage(String text, String newGlumac) {
        SharedPreferences st = PreferenceManager.getDefaultSharedPreferences(ListActivity.this);
        String name = st.getString("message", "Toast");
        if (name.equals("Toast")) {
            Toast.makeText(ListActivity.this, text + "\n"+ newGlumac, Toast.LENGTH_LONG).show();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ListActivity.this);
            builder.setSmallIcon(R.drawable.ic_action_name);
            builder.setContentTitle(text);
            builder.setContentText(newGlumac);


            // Shows notification with the notification manager (notification ID is used to update the notification later on)
            NotificationManager manager = (NotificationManager) ListActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, builder.build());
        }
    }

    public DataBaseHelper getDatabaseHelper() {
        if (dataBaseHelper == null) {
            dataBaseHelper = OpenHelperManager.getHelper(this, DataBaseHelper.class);
        }
        return dataBaseHelper;
    }

    @Override
    public void onDestroy() {
        Log.d("ZIL", "Main onDESTROY");
        super.onDestroy();

        if (dataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            dataBaseHelper = null;
        }
    }
}
