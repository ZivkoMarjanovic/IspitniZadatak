package com.example.zivko.glumacactivity;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.misc.TransactionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import db.DataBaseHelper;
import objects.Film;
import objects.Glumac;

public class DeatailActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    Glumac glumac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.podaci);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("Details");
        toolbar.setBackgroundColor(getResources().getColor(R.color.toolbar));
        setSupportActionBar(toolbar);

        int position = getIntent().getIntExtra("POSITION", 0);
        izmena(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.detail_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                try {
                    if (glumac != null) {
                        ForeignCollection<Film> collection = glumac.getFilmovi();
                        final List<Film> list = new ArrayList<Film>();

                        if (!collection.isEmpty()) {
                            CloseableIterator<Film> iterator = collection.closeableIterator();

                            try {

                                while (iterator.hasNext()) {
                                    Film f = iterator.next();
                                    list.add(f);
                                }
                            } finally {
                                try {
                                    iterator.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                            TransactionManager.callInTransaction(getDatabaseHelper().getConnectionSource(),
                                    new Callable<Void>() {
                                        public Void call() throws Exception {

                                            getDatabaseHelper().getFilmDAO().delete(list);

                                            getDatabaseHelper().getGlumacDao().delete(glumac);

                                            showMessage("Izbrisan je glumac", glumac.getImePrezime());

                                            finish();

                                            return null;
                                        }
                                    });

                        } else {getDatabaseHelper().getGlumacDao().delete(glumac);

                            showMessage("Izbrisan je glumac", glumac.getImePrezime());

                            finish();}

                        /*if (!filmoviCollection.isEmpty()) {
                            for (Film f : filmoviCollection) {
                                filmovi.add(f);
                            }
                            for (Film f : filmovi) {
                                getDatabaseHelper().getFilmDAO().delete(f);
                            }
                        }
                        getDatabaseHelper().getGlumacDao().delete(glumac);*/


                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.edit:
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_add_glumac);

                final EditText imePrezime = (EditText) dialog.findViewById(R.id.imePrezime);
                final EditText rodjen = (EditText) dialog.findViewById(R.id.rodjen);
                final EditText ocena = (EditText) dialog.findViewById(R.id.ocena);
                final EditText biografija = (EditText) dialog.findViewById(R.id.biografija);

                imePrezime.setText(glumac.getImePrezime());
                rodjen.setText(glumac.getRodjen());
                ocena.setText(Integer.toString(glumac.getOcena()));
                biografija.setText(glumac.getBigrafija());


                Button ok = (Button) dialog.findViewById(R.id.save);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        glumac.setImePrezime(imePrezime.getText().toString());
                        glumac.setRodjen(rodjen.getText().toString());
                        glumac.setOcena(Integer.parseInt(ocena.getText().toString()));
                        glumac.setBigrafija(biografija.getText().toString());

                        try {
                            getDatabaseHelper().getGlumacDao().update(glumac);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                        showMessage("Izmenjen je glumac", glumac.getImePrezime());
                        refreshGlumac();
                        izmena(glumac.getId());
                        dialog.dismiss();
                        dialog.cancel();

                    }
                });

                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        dialog.cancel();
                    }

                });
                dialog.show();

                break;

            case R.id.add_film:
                final Dialog filmDialog = new Dialog(this);
                filmDialog.setTitle("Add new film:");

                filmDialog.setContentView(R.layout.dialog_add_film);


                final EditText imeFilm = (EditText) filmDialog.findViewById(R.id.ime);
                //final EditText zanr = (EditText) filmDialog.findViewById(R.id.zanr);
                final EditText godina = (EditText) filmDialog.findViewById(R.id.godina);


                Button okFilm = (Button) filmDialog.findViewById(R.id.save);
                okFilm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Film newFilm = new Film();

                        newFilm.setName(imeFilm.getText().toString());
                        newFilm.setGodina(Integer.parseInt(godina.getText().toString()));
                        newFilm.setGlumac(glumac);


                        try {
                            getDatabaseHelper().getFilmDAO().create(newFilm);


                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        showMessage("Dodat je novi film", newFilm.getName());
                        refreshFilm();
                        filmDialog.dismiss();
                    }
                });

                Button cancelFilm = (Button) filmDialog.findViewById(R.id.cancel);
                cancelFilm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        filmDialog.dismiss();
                    }

                });
                filmDialog.show();
                if( filmDialog.isShowing()) {filmDialog.getWindow().setBackgroundDrawableResource(R.color.filmDialog);}
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showMessage(String text, String newGlumac) {
        SharedPreferences st = PreferenceManager.getDefaultSharedPreferences(this);
        String name = st.getString("message", "Toast");
        if (name.equals("Toast")) {
            Toast.makeText(this, text + "\n"+ newGlumac, Toast.LENGTH_LONG).show();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.drawable.ic_action_name);
            builder.setContentTitle(text);
            builder.setContentText(newGlumac);


            // Shows notification with the notification manager (notification ID is used to update the notification later on)
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, builder.build());
        }
    }

    private void refreshFilm() {

        ListView listview = (ListView) findViewById(R.id.filmovi);

        if (listview != null) {
            ArrayAdapter<Film> adapter = (ArrayAdapter<Film>) listview.getAdapter();

            if (adapter != null) {

                adapter.clear();

                List<Film> filmovi = null;
                try {
                    filmovi = getDatabaseHelper().getFilmDAO().queryBuilder().where().eq(Film.GLUMAC_ID, glumac).query();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                adapter.addAll(filmovi);

                adapter.notifyDataSetChanged();


            }
        }
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


    public void izmena(int position) {
        try {
            glumac = getDatabaseHelper().getGlumacDao().queryForId(position);

            Log.d("ZIL", "YYYYYYYYYYYYY");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (glumac != null) {
            TextView imePrezime = (TextView) findViewById(R.id.imePrezime);
            imePrezime.setText(glumac.getImePrezime());

            ImageView fotografija = (ImageView) findViewById(R.id.fotografija);
            fotografija.setImageResource(glumac.getFotografija());

            TextView rodjen = (TextView) findViewById(R.id.rodjen);
            rodjen.setText(glumac.getRodjen());

            TextView umro = (TextView) findViewById(R.id.umro);
            umro.setText(String.valueOf(glumac.getUmro()));

            TextView biografija = (TextView) findViewById(R.id.biografija);
            biografija.setText(glumac.getBigrafija());

            ListView listaFilmovi = (ListView) findViewById(R.id.filmovi);

            ForeignCollection<Film> call = glumac.getFilmovi();
            List<Film> liss = new ArrayList<Film>();
            if (!call.isEmpty()) {
                for (Film item : call) {
                    liss.add(item);
                }
            }
            if (liss != null) {
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, liss);
                listaFilmovi.setAdapter(adapter);
            }


            RatingBar ocena = (RatingBar) findViewById(R.id.ocena);
            ocena.setRating((float) glumac.getOcena());
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
