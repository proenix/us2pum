package pl.proenix.android.us2pum.lab5contactbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.StringCharacterIterator;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    WebView webView;
    String searchString = "";
    Boolean sortOrder = true;
    Boolean backButtonActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check permission to read contact and request if needed.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "Please grant Read Contact permission manually.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                // PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Already granted.
            loadWebView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                loadWebView();
            } else {
                Toast.makeText(this, "Please grant Read Contact permission manually.", Toast.LENGTH_LONG).show();
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Load WebView action.
     * Use after check if contact read permissions are granted.
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void loadWebView() {
        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());
        WebView.setWebContentsDebuggingEnabled(true);
        webView.addJavascriptInterface(new JavaScriptInterface(this), "AndroidContactHandler");

        webView.loadUrl("file:///android_asset/contact-book/dist/index.html");
    }

    /**
     * Get contacts list as JSON.
     * @param searchName Name to search for.
     * @param sort On True sort ASC, on false DESC
     * @return JSON String.
     */
    public String getContacts(String searchName, Boolean sort) {
        String[] PROJECTION = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        };

        // Sorting order
        String sortOrder = ContactsContract.Contacts.Entity.DISPLAY_NAME_PRIMARY + ((sort)?" ASC":" DESC");

        // Prepare JSON
        JSONArray ja = new JSONArray();

        // Initialize Content resolver
        ContentResolver cr = getContentResolver();

        // Generate lookupUri depending if something is typed in search.
        Uri lookupUri;
        if (searchName.isEmpty()) {
            lookupUri = ContactsContract.Contacts.CONTENT_URI;
        } else {
            lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(searchName));
        }

        Cursor mainCursor = cr.query(lookupUri, PROJECTION, null, null, sortOrder);
        if (mainCursor != null) {
            while (mainCursor.moveToNext()) {
                JSONObject person = new JSONObject();

                String id = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts._ID));
                String lookup_key = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                String name = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                try {
                    String photoUri = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    if (photoUri != null) {
                        Uri photo = Uri.parse(photoUri);
                        person.put("photo", forJSON(getPhotoAsBase64(photo)));
                    } else {
                        person.put("photo", "");
                    }
                } catch (NullPointerException | JSONException e) {
                    e.printStackTrace();
                }

                JSONObject numbers = new JSONObject();
                // TODO: 01/05/2020 Cache data or get numbers in a different form for displaying on list. Current method is not optimal for performance.
//                if (Integer.parseInt(mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                    //ADD PHONE DATA...
//                    Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);
//                    if (phoneCursor != null) {
//                        while (phoneCursor.moveToNext()) {
//                            String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                            try {
//                                numbers.put("number", phone);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    if (phoneCursor != null) {
//                        phoneCursor.close();
//                    }
//                }
                try {
                    person.put("id", id);
                    person.put("name", forJSON(name));
                    person.put("lookup_key", lookup_key);
                    person.put("numbers", numbers);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ja.put(person);
            }
            mainCursor.close();
        }

        return ja.toString();
    }

    /**
     * Get Contact Details from provider.
     * @param id Integer - ID of Contact as in ContactsContract.Contacts._ID
     * @param lookup_key String - Lookup key as in ContactsContract.Contacts.LOOKUP_KEY
     * @return String JSON formatted data.
     */
    private String getContactDetails(int id, String lookup_key) {
        // Prepare JSON
        JSONObject ja = new JSONObject();

        // Initialize Content resolver
        ContentResolver cr = getContentResolver();

        // Cursor get Phone number by ID
        Uri lookupUri = ContactsContract.Contacts.getLookupUri(id, lookup_key);

        Cursor mainCursor = cr.query(
                lookupUri,
                new String[] {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                },
                null,
                null,
                null);
        if (mainCursor != null) {
            while (mainCursor.moveToNext()) {
                String _id = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                try {
                    String photoUri = mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    if (photoUri != null) {
                        Uri photo = Uri.parse(photoUri);
                        ja.put("photo", forJSON(getPhotoAsBase64(photo)));
                    } else {
                        ja.put("photo", "");
                    }
                } catch (NullPointerException | JSONException e) {
                    e.printStackTrace();
                }

                // Get Phone Numbers
                JSONArray numbers = new JSONArray();
                if (Integer.parseInt(mainCursor.getString(mainCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phoneCursor = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE,
                            },
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{_id},
                            null);
                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            JSONObject number = new JSONObject();
                            String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String phone_normalized = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
                            int type = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            try {
                                number.put("number", phone);
                                number.put("number_normalized", phone_normalized);
                                number.put("type", getPhoneType(type));
                                numbers.put(number);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        phoneCursor.close();
                    }
                }

                // Get Emails
                JSONArray emails = new JSONArray();
                Cursor emailCursor = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[] { _id },
                        null);
                if (emailCursor != null) {
                    while (emailCursor.moveToNext()) {
                        JSONObject email = new JSONObject();
                        String address = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        try {
                            email.put("email", forJSON(address));
                            emails.put(email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    emailCursor.close();
                }

                // Get Address
                JSONArray addresses = new JSONArray();
                Cursor addrCursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?",
                        new String[] { _id },
                        null);
                if (addrCursor != null) {
                    while (addrCursor.moveToNext()) {
                        JSONObject address = new JSONObject();
                        String formatted_address = addrCursor.getString(addrCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                        String city = addrCursor.getString(addrCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                        String state = addrCursor.getString(addrCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                        String country = addrCursor.getString(addrCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                        try {
                            address.put("formatted_address", forJSON(formatted_address));
                            address.put("city", forJSON(city));
                            address.put("state", forJSON(state));
                            address.put("country", forJSON(country));
                            addresses.put(address);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    addrCursor.close();
                }
                try {
                    ja.put("id", _id);
                    ja.put("name", forJSON(name));
                    ja.put("numbers", numbers);
                    ja.put("emails", emails);
                    ja.put("addresses", addresses);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mainCursor.close();
        }
        Log.w("AndroidContactDetails", ja.toString());


        return ja.toString();
    }

    /**
     * Get Name of Phone Number Type from integer.
     * TODO: 02/05/2020  Implement all type of phone numbers.
     * @param type Integer as in ContactsContract.CommonDataKinds.Phone.TYPE
     * @return String Readable name of phone number type.
     */
    private String getPhoneType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return "Home";
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return "Mobile";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return "Work";
            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                return "Custom";
            default:
                return "Other";
        }
    }


    /**
     Escapes characters for text appearing as data in the
     <a href='http://www.json.org/'>Javascript Object Notation</a>
     (JSON) data interchange format.

     <P>The following commonly used control characters are escaped :
     <table border='1' cellpadding='3' cellspacing='0'>
     <tr><th> Character </th><th> Escaped As </th></tr>
     <tr><td> " </td><td> \" </td></tr>
     <tr><td> \ </td><td> \\ </td></tr>
     <tr><td> / </td><td> \/ </td></tr>
     <tr><td> back space </td><td> \b </td></tr>
     <tr><td> form feed </td><td> \f </td></tr>
     <tr><td> line feed </td><td> \n </td></tr>
     <tr><td> carriage return </td><td> \r </td></tr>
     <tr><td> tab </td><td> \t </td></tr>
     </table>

     <P>See <a href='http://www.ietf.org/rfc/rfc4627.txt'>RFC 4627</a> for more information.
     @author (c) 2002-2018 Hirondelle Systems. BSD license Copyright
     @link http://www.javapractices.com/topic/TopicAction.do?Id=96
     */
    private static String forJSON(String aText){
        if (aText == null) {
            return "";
        }
        final StringBuilder result = new StringBuilder();
        StringCharacterIterator iterator = new StringCharacterIterator(aText);
        char character = iterator.current();
        while (character != StringCharacterIterator.DONE){
            if( character == '\"' ){
                result.append("\\\"");
            }
            else if(character == '\\'){
                result.append("\\\\");
            }
            else if(character == '/'){
                result.append("\\/");
            }
            else if(character == '\b'){
                result.append("\\b");
            }
            else if(character == '\f'){
                result.append("\\f");
            }
            else if(character == '\n'){
                result.append("\\n");
            }
            else if(character == '\r'){
                result.append("\\r");
            }
            else if(character == '\t'){
                result.append("\\t");
            }
            else {
                //the char is not a special one
                //add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    /**
     * Prevent default back button behaviour when displaying contact details.
     * Instead run action of going back in WebView app and reset behaviour.
     */
    @Override
    public void onBackPressed() {
        if (backButtonActive) {
            super.onBackPressed();
        } else {
            // App history get up to contact list
            webView.loadUrl("javascript:app.__vue__.$router.back()");
            backButtonActive = true;
        }
    }

    /**
     * Get Base64 encoded string containing contact thumbnail photo.
     * Usage on web <img src="data:image/jpg;base64,BASE64_ENCODED_STRING" />
     * @param photoUri URI to contact photo.
     * @return String Base64 encoded photo or empty string if not extracted correctly.
     */
    private String getPhotoAsBase64(Uri photoUri) {
        if (photoUri == null) {
            return "";
        }
        Cursor cursor = getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    Bitmap bp=BitmapFactory.decodeStream(new ByteArrayInputStream(data)); //decode stream to a bitmap image
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();

                    return Base64.encodeToString(b, Base64.DEFAULT);
                }
            }
        } finally {
            cursor.close();
        }
        return "";
    }

    /**
     * Opens Dial Intent with number already inputted.
     * Does not require CALL_PHONE permissions in this way.
     * @param number Phone number to dial.
     */
    public void requestContactCall(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+number));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);
    }

    /**
     * Opens Sendto Intent with email already inputted to TO field.
     * @param email Email address.
     */
    public void requestContactEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"+email));
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(emailIntent);
    }

    /**
     * Injects contact details data to WebView.
     * @param id Integer - ID of Contact as in ContactsContract.Contacts._ID
     * @param lookup_key String - Lookup key as in ContactsContract.Contacts.LOOKUP_KEY
     */
    public void requestContactDetails(final int id, final String lookup_key) {
        runOnUiThread(new Runnable() {
            public void run() {
                webView.loadUrl("javascript:app.__vue__.loadContactDetails('" + getContactDetails(id, lookup_key) + "')");
            }
        });
    }


    /**
     * Injects contact list data to WebView.
     * @param sString String Text used for narrowing contact list by filtering.
     */
    public void requestLoadContacts(String sString) {
        searchString = sString;
        runOnUiThread(new Runnable() {
            public void run() {
                webView.loadUrl("javascript:app.__vue__.loadContacts('" + getContacts(searchString, sortOrder) + "')");
            }
        });
    }

    /**
     * Injects contact list data to WebView.
     * @param sOrder Boolean For true sort data ASC.
     */
    public void requestChangeSort(boolean sOrder) {
        sortOrder = sOrder;
        runOnUiThread(new Runnable() {
            public void run() {
                webView.loadUrl("javascript:app.__vue__.loadContacts('" + getContacts(searchString, sortOrder) + "')");
            }
        });
    }

    /**
     * Set variable for custom back button behaviour.
     */
    public void preventDefaultBackButtonBehaviour() {
        backButtonActive = false;
    }
}


