package com.mienpv.contactsapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mienpv.contactsapp.contacts.ContactRVAdapter;
import com.mienpv.contactsapp.contacts.ContactsModal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ContactsModal> contactsModalArrayList;
    private RecyclerView contactRV;
    private ContactRVAdapter contactRVAdapter;
    private ProgressBar loadingPB;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactsModalArrayList = new ArrayList<>();
        contactRV = findViewById(R.id.idRVContacts);
        //FloatingActionButton addNewContactFAB = findViewById(R.id.idFABadd);
        loadingPB = findViewById(R.id.idPBLoading);
        //calling method to prepare our recycler view.
        prepareContactRV();
        //calling a method to request permissions.
        requestPermissions();
    }
    private void prepareContactRV() {
        //in thi method we are preparing our recycler view with adapter.
        contactRVAdapter = new ContactRVAdapter(this, contactsModalArrayList);
        //on below line we are setting layout mnager.
        contactRV.setLayoutManager(new LinearLayoutManager(this));
        //on below line we are setting adapter to our recycler view.
        contactRV.setAdapter(contactRVAdapter);
    }
    private void filter(String text) {
        //in this method we are filtering our array list.
        //on below line we are creating a new filtered array list.
        ArrayList<ContactsModal> filteredlist = new ArrayList<>();
        //on below line we are running a loop for checking if the item is present in array list.
        for (ContactsModal item : contactsModalArrayList) {
            if (item.getUserName().toLowerCase().contains(text.toLowerCase())) {
                //on below line we are adding item to our filtered array list.
                filteredlist.add(item);
            }
        }
        //on below line we are checking if the filtered list is empty or not.
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Contact Found", Toast.LENGTH_SHORT).show();
        } else {
            //passing this filtered list to our adapter with filter list method.
            contactRVAdapter.filterList(filteredlist);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //in this on create options menu we are calling a menu inflater and inflating our menu file.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        //on below line we are getting our menu item as search view item
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        //on below line we are creating a variable for our search view.
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        //on below line we are setting on query text listner for our search view.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //on query submit we are clearing the focus forour search view.
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //on changing the text in our search view we are calling a filter method to filter our array list.
                filter(newText.toLowerCase());
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called on click on positive
                // button and on clicking shit button we
                // are redirecting our user from our app to the
                // settings page of our app.
                dialog.cancel();
                // below is the intent from which we
                // are redirecting our user.
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called when
                // user click on negative button.
                dialog.cancel();
            }
        });
        // below line is used
        // to display our dialog
        builder.show();
    }

    private void getContacts() {
        //this method is use to read contact from users device.
        //on below line we are creating a string variables for our contact id and display name.
        String contactId = "";
        String displayName = "";
        String emailName="";
        //on below line we are calling our content resolver for getting contacts
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        //on blow line we are checking the count for our cursor.
        if (cursor.getCount() > 0) {
            //if the count is greater thatn 0 then we are running a loop to move our cursor to next.
            while (cursor.moveToNext()) {
                //on below line we are getting the phone number.
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    //we are checking if the has phine number is >0
                    //on below line we are getting our contact id and user name for that contact
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //on below line we are calling a content resolver and making a query
                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);
                    //on below line we are moving our cursor to next position.
                    if (phoneCursor.moveToNext()) {
                        //on below line we are getting the phone number for our users and then adding the name along with phone number in array list.
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactsModalArrayList.add(new ContactsModal(displayName, phoneNumber, emailName));
                    }
                    //on below line we are closing our phone cursor.
                    phoneCursor.close();
                }
            }
        }
        //on below line we are closing our cursor.
        cursor.close();
        //on below line we are hiding our progress bar and notifying our adapter class.
        loadingPB.setVisibility(View.GONE);
        contactRVAdapter.notifyDataSetChanged();
    }
    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContacts();
            }
        } else {
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}