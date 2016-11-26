package objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Živko on 2016-10-25.
 */
@DatabaseTable (tableName = Beleska.TABLE_NAME)
public class Beleska {

    public static final String TABLE_NAME = "beleska";
    public static final String ID_GLUMAC = "id";
    public static final String NASLOV = "naslov";
    public static final String OPIS = "OPIS";

    public static final String DATUM = "datum";


    @DatabaseField(generatedId = true, columnName = ID_GLUMAC)
    int id;

    @DatabaseField(columnName = NASLOV)
    String naslov;

    @DatabaseField(columnName = OPIS)
    String opis;


    @DatabaseField(columnName = DATUM)
    String datum;




    public Beleska() {

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }



    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    @Override
    public String toString() {
        return naslov;
    }
}
