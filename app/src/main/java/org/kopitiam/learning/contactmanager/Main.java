package org.kopitiam.learning.contactmanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class Main extends Activity {

    EditText txtName, txtPhone, txtEmail, txtAddress;
    List<Contacts> _myContacts = new ArrayList<Contacts>();
    ListView lvContact;
    ImageView imgContact;
    Uri imgUri = Uri.parse("android.resource://org.kopitiam.learning.contactmanager/drawable/user.png");
    DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DatabaseHandler(getApplicationContext());

        //region Link layout to Objects

        txtName = (EditText) findViewById(R.id.txtContactName);
        txtPhone = (EditText) findViewById(R.id.txtContactPhone);
        txtEmail = (EditText) findViewById(R.id.txtContactEmail);
        txtAddress = (EditText) findViewById(R.id.txtContactAddress);
        final Button btnAdd = (Button) findViewById(R.id.btnContactSave);

        TabHost tabhost = (TabHost) findViewById(R.id.tabHost);

        lvContact = (ListView) findViewById(R.id.listViewContacts);

        imgContact = (ImageView) findViewById(R.id.imgContact);
        //endregion

        //region Event Listener
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                btnAdd.setEnabled(!txtName.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contacts newItem = new Contacts(dbHandler.GetLastContactId() + 1, txtName.getText().toString(), txtPhone.getText().toString(),
                        txtEmail.getText().toString(), txtAddress.getText().toString(),
                        imgUri);

                dbHandler.CreateContact(newItem);
                _myContacts.add(newItem);
                populateList();
                //Toast.makeText(getApplicationContext(),"New Contact is saved.", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), txtName.getText().toString() + " has been added to your contact.", Toast.LENGTH_SHORT).show();
            }
        });

        imgContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent();
                myIntent.setType("image/*");
                myIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(myIntent, "Set Image for selected Contact"), 123498765);
            }
        });
        //endregion

        //region Init Tab Content
        tabhost.setup();

        TabHost.TabSpec tabCreate = tabhost.newTabSpec("Creator");
        tabCreate.setContent(R.id.tabCreate);
        tabCreate.setIndicator("Create");
        tabhost.addTab(tabCreate);

        TabHost.TabSpec tabView = tabhost.newTabSpec("Viewer");
        tabView.setContent(R.id.tabContacts);
        tabView.setIndicator("View");
        tabhost.addTab(tabView);
        //endregion

        //region Populate Existing Contact from Database
        _myContacts = dbHandler.GetAllContacts();
        if(!_myContacts.isEmpty())
            populateList();
        //endregion
    }

    //when the intent event/activity is finished and give result
    //the request code is to match the intent starter and the result, since this eventhandler will be called by more than one intent
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == 123498765) {
                imgUri = data.getData();
                imgContact.setImageURI(imgUri);
            }
        }
    }

    private void populateList(){
        ArrayAdapter<Contacts> adapter = new ContactListAdapter();
        lvContact.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ContactListAdapter extends ArrayAdapter<Contacts> {
        public ContactListAdapter() {
            super(Main.this, R.layout.contact_item, _myContacts);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) view = getLayoutInflater().inflate(R.layout.contact_item, parent, false);

            Contacts currentContact = _myContacts.get(position);

            TextView name = (TextView) view.findViewById(R.id.lblContactName);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.lblContactPhone);
            phone.setText(currentContact.getPhone());
            TextView email = (TextView) view.findViewById(R.id.lblContactEmail);
            email.setText(currentContact.getEmail());
            TextView address = (TextView) view.findViewById(R.id.lblContactAddress);
            address.setText(currentContact.getAddress());
            ImageView imgContact = (ImageView) view.findViewById(R.id.imgContactItem);
            imgContact.setImageURI(currentContact.getImage());

            return view;
        }
    }
}
