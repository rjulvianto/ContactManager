package org.kopitiam.learning.contactmanager;

import android.net.Uri; //notes: use android.net.uri, not java.net.uri

/**
 * Created by rjulvianto on 8/19/2014.
 */
public class Contacts {

    private final String _name, _phone, _email, _address;
    private final Uri _imgUri;
    private final int _id;

    public Contacts(int id, String name, String phone, String email, String address, Uri imgURI) {
        _id = id;
        _name = name;
        _phone = phone;
        _email = email;
        _address = address;
        _imgUri = imgURI;

    }

    public int getId(){ return _id; }
    public String getName(){
        return _name;
    }
    public String getPhone(){
        return _phone;
    }
    public String getEmail(){
        return _email;
    }
    public String getAddress(){
        return _address;
    }
    public Uri getImage() { return _imgUri; }
}
