package pl.proenix.android.us2pum.lab5contactbook;

import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
    private MainActivity parentActivity;

    JavaScriptInterface(MainActivity _activity) {
        parentActivity = _activity;
    }

    /**
     * Interface for getting contact details request.
     * Forwards request to UI, to get requested data and inject to WebView component.
     * @param id Integer - ID of Contact as in ContactsContract.Contacts._ID
     * @param lookup_key String - Lookup key as in ContactsContract.Contacts.LOOKUP_KEY
     */
    @JavascriptInterface
    public void requestContactDetails(int id, String lookup_key) {
        this.parentActivity.requestContactDetails(id, lookup_key);
        this.parentActivity.preventDefaultBackButtonBehaviour();
    }

    /**
     * Interface for getting contact list request.
     * Forwards request to UI, to get requested data and inject to WebView component.
     * @param searchString String - To narrow contact list names.
     */
    @JavascriptInterface
    public void requestLoadContacts(String searchString) {
        this.parentActivity.requestLoadContacts(searchString);
    }

    /**
     * Interface for change sort order of contact list.
     * Forwards request to UI, to get save preferred sorting order for future requests.
     * @param sortOrder Integer - 1 to sort ASC, other value to sort DESC.
     */
    @JavascriptInterface
    public void requestChangeSort(int sortOrder) {
        if (sortOrder == 1) {
            this.parentActivity.requestChangeSort(true);
        } else {
            this.parentActivity.requestChangeSort(false);
        }
    }

    /**
     * Interface for opening dialog to call number.
     * @param number String phone number to input to Dial Intent.
     */
    @JavascriptInterface
    public void requestContactCall(String number) {
        this.parentActivity.requestContactCall(number);
    }

    /**
     * Interface for opening dialog to write email.
     * @param email String email to input in "TO" field.
     */
    @JavascriptInterface
    public void requestContactEmail(String email) {
        this.parentActivity.requestContactEmail(email);
    }
}
