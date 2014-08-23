package org.kopitiam.learning.contactmanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Main extends Activity {

    private EditText txtName, txtPhone, txtEmail, txtAddress;
    private final List<Contacts> _myContacts = new ArrayList<Contacts>();
    private final int CONTEXT_CONTACT_EDIT = 0, CONTEXT_CONTACT_DELETE = 1;
    private final int INTENT_GET_IMAGE = 5001;
    ListView lvContact;
    ImageView imgContact;
    Uri imgUri = Uri.parse("android.resource://org.kopitiam.learning.contactmanager/drawable/user.png");
    DatabaseHandler dbHandler;

    ArrayAdapter<Contacts> adapter;

    int longClickItemIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //region Init database
        dbHandler = new DatabaseHandler(getApplicationContext());
        //endregion

        // Assign adapter to list view
        adapter = new ContactListAdapter();
        lvContact.setAdapter(adapter);


        //region Event Listener
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //note: use String.valueOf(something) instead of .toString()
                btnAdd.setEnabled(!String.valueOf(txtName.getText()).trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contacts newItem = new Contacts(dbHandler.GetLastContactId() + 1, String.valueOf(txtName.getText()), String.valueOf(txtPhone.getText()),
                        String.valueOf(txtEmail.getText()), String.valueOf(txtAddress.getText()),
                        imgUri);
                if (_myContacts.add(newItem)) {
                    dbHandler.CreateContact(newItem);

                    adapter.notifyDataSetChanged();

                }

                //populateList(); -->this is not needed when we assign adapter to the list view. everytime the list/array content changes, the adapter will change notify view
                //Toast.makeText(getApplicationContext(),"New Contact is saved.", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), String.valueOf(txtName.getText()) + " has been added to your contact. Total Contact :" + String.valueOf(dbHandler.GetContactsCount()), Toast.LENGTH_SHORT).show();
            }
        });

        imgContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent();
                myIntent.setType("image/*");
                myIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(myIntent, "Set Image for selected Contact"), INTENT_GET_IMAGE);
            }
        });

        registerForContextMenu(lvContact);
        lvContact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                longClickItemIndex = i;
                return false;
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
        _myContacts.addAll(dbHandler.GetAllContacts());
        //endregion
    }

    //when the intent event/activity is finished and give result
    //the request code is to match the intent starter and the result, since this eventhandler will be called by more than one intent
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == INTENT_GET_IMAGE) {
                imgUri = data.getData();
                imgContact.setImageURI(imgUri);
            }
        }
    }

//    private void populateList(){
//        ArrayAdapter<Contacts> adapter = new ContactListAdapter();
//        lvContact.setAdapter(adapter);
//    }

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
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super. onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.edit);
        menu.setHeaderTitle("Options");
        menu.add(Menu.NONE, CONTEXT_CONTACT_EDIT, Menu.NONE, "Edit Contact");
        menu.add(Menu.NONE, CONTEXT_CONTACT_DELETE, Menu.NONE, "Delete Contact");
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONTEXT_CONTACT_EDIT:
                // TODO: Implement editing contact
                break;
            case CONTEXT_CONTACT_DELETE:
                dbHandler.DeleteContact(_myContacts.get(longClickItemIndex).getId());
                _myContacts.remove(longClickItemIndex);
                adapter.notifyDataSetChanged();
                break;
        }

        return super.onContextItemSelected(item);
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
