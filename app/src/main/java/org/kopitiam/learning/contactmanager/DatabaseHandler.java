package org.kopitiam.learning.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rjulvianto on 8/21/2014.
 */
class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "ContactManager",
                                TABLE_CONTACTS = "contact",
                                KEY_ID = "id",
                                KEY_NAME = "name",
                                KEY_PHONE = "phone",
                                KEY_EMAIL = "email",
                                KEY_ADDRESS = "address",
                                KEY_IMAGE = "imageUri";

    public DatabaseHandler(Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                                                           + KEY_NAME + " TEXT, "
                                                            + KEY_PHONE + " TEXT, "
                                                            + KEY_EMAIL + " TEXT, "
                                                            + KEY_ADDRESS + " TEXT, "
                                                            + KEY_IMAGE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void CreateContact(Contacts contact)
    {
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            ContentValues val = new ContentValues();

            val.put(KEY_NAME, contact.getName());
            val.put(KEY_PHONE, contact.getPhone());
            val.put(KEY_EMAIL, contact.getEmail());
            val.put(KEY_ADDRESS, contact.getAddress());
            val.put(KEY_IMAGE, String.valueOf(contact.getImage()));

            db.insert(TABLE_CONTACTS, null, val);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public Contacts GetContact(int id){
        Contacts result = null;

        SQLiteDatabase db = null;
        Cursor csr = null;

        try {
            db = getReadableDatabase();
            csr = db.query(TABLE_CONTACTS, new String[]{ KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS, KEY_IMAGE}, KEY_ID + "=?",
                                                    new String[]{ String.valueOf(id)}, null, null, null, null);

            if (csr.moveToFirst()) {
                result = new Contacts(csr.getInt(0), csr.getString(1), csr.getString(2), csr.getString(3), csr.getString(4), Uri.parse(csr.getString(5)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csr.close();
            db.close();
        }

        return  result;
    }

    public int UpdateContact(int id, Contacts contact){
        SQLiteDatabase db = null;
        int result = 0; //total rows affected in this update action

        try {
            db = getWritableDatabase();
            ContentValues val = new ContentValues();

            val.put(KEY_NAME, contact.getName());
            val.put(KEY_PHONE, contact.getPhone());
            val.put(KEY_EMAIL, contact.getEmail());
            val.put(KEY_ADDRESS, contact.getAddress());
            val.put(KEY_IMAGE, String.valueOf(contact.getImage()));

            result = db.update(TABLE_CONTACTS, val, KEY_ID + "=?", new String[] { String.valueOf(id) });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return  result;
    }

    public void DeleteContact(int id){
        SQLiteDatabase db = null;

        try {
             db = getWritableDatabase();
             db.delete(TABLE_CONTACTS, KEY_ID + "=?", new String[] { String.valueOf(id) });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public int GetContactsCount(){
        int result = -1;

        SQLiteDatabase db = null;
        Cursor csr = null;

        try {
            db = getReadableDatabase();
            csr = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
            if (csr.moveToFirst()) {
                result = csr.getCount();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csr.close();
            db.close();
        }

        return result;
    }

    public List<Contacts> GetAllContacts(){
        List<Contacts> result = null;
        SQLiteDatabase db = null;
        Cursor csr = null;

        try {
            result = null;

            db = getReadableDatabase();
            csr = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null );

            if (csr != null)
            {
                result = new ArrayList<Contacts>();
//                csr.moveToFirst();
//
//                while(!csr.isLast()){
//                    Contacts item = new Contacts(csr.getInt(0), csr.getString(1), csr.getString(2), csr.getString(3), csr.getString(4), Uri.parse(csr.getString(5)));
//                    result.add(item);
//
//                    csr.moveToNext();
//                }
                if (csr.moveToFirst()){
                    do {
                        result.add(new Contacts(csr.getInt(0), csr.getString(1), csr.getString(2), csr.getString(3), csr.getString(4), Uri.parse(csr.getString(5))))
                        ;
                    }
                    while (csr.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csr.close();
            db.close();
        }

        return  result;
    }

    public int GetLastContactId(){
        int result = -1;

        SQLiteDatabase db = null;
        Cursor csr = null;

        try {
            db = getReadableDatabase();
            csr = db.rawQuery("SELECT MAX(ID) FROM " + TABLE_CONTACTS, null);

            if(csr != null){
                if(csr.getCount() > 0){
                    result = csr.getInt(0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            csr.close();
        }

        return  result;
    }
}
