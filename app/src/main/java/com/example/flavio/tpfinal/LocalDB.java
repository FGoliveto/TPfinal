package com.example.flavio.tpfinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class LocalDB extends SQLiteOpenHelper {
    private static final String FILENAME ="Local";
    private static final int DB_VERSION = 2;

    LocalDB(Context context) {
        super(context, FILENAME, null, DB_VERSION);
    }
    private static final String createTableLocal = "CREATE TABLE local ("
            + "  id integer primary key autoincrement,"
            + "  nombre text NOT NULL,"
            + "  descripcion longtext NOT NULL,"
            + "  rating float NOT NULL,"
            + "	 telefono text,"
            + "	 foto longtext,"
            + "  direccion text NOT NULL,"
            + "  latitud double NOT NULL,"
            + "  longitud double NOT NULL,"
            + "  rubro text)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableLocal);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE local ADD COLUMN rubro text");
    }
}
