package com.accintern.ricardarmankuodis.messenger;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Button.OnClickListener, TextWatcher {

    public static final int MAX_CHARACTERS=100;
    private static final String KEY_PHONE = "KEY_PHONE";
    private static final String KEY_MESSAGE = "KEY_MESSAGE";

    private EditText mPhoneEditText;
    private EditText mMessageEditText;
    private TextView mCharsLeftTextView;
    private Button mSendMessageButton;


    // TODO add ability to choose number from contacts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // wire up views
        mPhoneEditText = (EditText) findViewById(R.id.editTextPhone);
        mMessageEditText = (EditText) findViewById(R.id.editTextMessage);
        mCharsLeftTextView = (TextView) findViewById(R.id.textViewCharsLeft);
        mSendMessageButton = (Button) findViewById(R.id.buttonSendMessage);

        // add listeners
        mSendMessageButton.setOnClickListener(this);
        mMessageEditText.addTextChangedListener(this);
    }

    // we define this activity as our button listener
    // since we know that this activity will have only one button to send messages
    @Override
    public void onClick(View view) {

        String phoneNumber = mPhoneEditText.getText().toString();
        String messageContent = mMessageEditText.getText().toString();

        // do not proceed if input data is invalid
        if(validateMessage(phoneNumber,messageContent)) return;

        Intent intent = new Intent();
        // we could put 2 charseqeuences to the intent, but lets do it bundle way
//        Bundle messageInfoBundle = new Bundle();
//        messageInfoBundle.putString("PHONE",phoneNumber);
//        messageInfoBundle.putString("MESSAGE",messageContent);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_TEXT,messageInfoBundle);
        intent.putExtra(Intent.EXTRA_TEXT,messageContent);

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phoneNumber);
        smsIntent.putExtra("sms_body", messageContent);
        startActivity(smsIntent);

        // Force app chooser
        Intent chooser = Intent.createChooser(smsIntent, "Choose an app to handle your intent");

        // Verify the intent will resolve to at least one activity
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
//        startActivity(intent);

    }

    /** Perform input data check.
     * phone number must be 8 digits long, body must have text (even 1 space counts)
     * @param phone Phone number
     * @param body Message text
     * @return validity*/
    private boolean validateMessage(String phone, String body){
        boolean hasErrors=false;

        if(phone.length()!=8){
            // TODO sdelat bolee podrobnuju proverku
            mPhoneEditText.setError("Invalid phone number!\nMust contain 8 digits");
            mPhoneEditText.requestFocus();
            hasErrors=true;
        }
        // more checks...
        if(body.length()==0){
            mMessageEditText.setError("Message should have a body!");
            mMessageEditText.requestFocus();
            hasErrors=true;
        }else if(body.trim().length()==0 ){
            mMessageEditText.setError("Message should contain at least 1 non-space character!");
            mMessageEditText.requestFocus();
            hasErrors=true;
        }

        return hasErrors;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPhoneEditText.setText(savedInstanceState.getString(KEY_PHONE));
        mMessageEditText.setText(savedInstanceState.getString(KEY_MESSAGE));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_PHONE,mPhoneEditText.getText().toString());
        outState.putString(KEY_MESSAGE,mMessageEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Do not show characts count textview if no input present
        mCharsLeftTextView.setVisibility(View.VISIBLE);
        mCharsLeftTextView.setText(String.format(getResources().getString(R.string.charsLeftMsg), charSequence.length()));
        if(i2==0) mCharsLeftTextView.setVisibility(View.GONE);

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    // TODO create phone regex validator

    /* Lifecycle Loggin */
}
