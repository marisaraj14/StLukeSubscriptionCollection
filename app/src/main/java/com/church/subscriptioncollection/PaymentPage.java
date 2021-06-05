package com.church.subscriptioncollection;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.MutableLiveData;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.church.subscriptioncollection.data.Repository;
import com.church.subscriptioncollection.model.Volunteer;
import com.church.subscriptioncollection.result.CreateUserResult;
import com.church.subscriptioncollection.result.VolunteerResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class PaymentPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    String name="Cash";
    private Repository repository;
    EditText memberID;

    // For Asynchronous retrieval of data we need to watch the result from the db.
    private final MutableLiveData<CreateUserResult> createUserResultMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<VolunteerResult> volunteerResultMutableLiveData = new MutableLiveData <VolunteerResult>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);
        repository = Repository.getInstance();

        //Hide Default Action Bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Initializing
        final ImageView scan = (ImageView) findViewById(R.id.scanFamilyCard);
        final Spinner spinner=findViewById(R.id.nameOfCollector);
        final RadioGroup radioGroup = findViewById(R.id.mode);
        final EditText amountPaid=findViewById(R.id.amountPaid);
        final AppCompatButton submit=findViewById(R.id.submit);
        final EditText dateOfSub = (EditText) findViewById(R.id.paymentDate);
        memberID = (EditText) findViewById(R.id.memberID);

        //Call to Scan QR/Barcode
        scan.setOnClickListener(this);

        //Initializing values in Spinner
        repository.getVolunteers(volunteerResultMutableLiveData);

        //Setting Today's Date
        Calendar c1 = Calendar.getInstance();
        String todayDate = DateFormat.getDateInstance().format(c1.getTime());
        c1.add(Calendar.DATE, 7);
        c1.add(Calendar.MONTH, 0);
        c1.add(Calendar.YEAR, 0);
        dateOfSub.setText(todayDate);

        //RadioGroup: Getting the Payment Mode
        radioGroup.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override

                    // Check which radio button has been clicked
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {
                        RadioButton rb=(RadioButton)findViewById(checkedId);
                        name= (String) rb.getText();
                    }
                });


        createUserResultMutableLiveData.observe(this, createUserResult -> {
            if (createUserResult == null) {
                return;
            }
            if (createUserResult.getError() != null) {
                showLoginFailed(createUserResult.getError());
            }
            if (createUserResult.getSuccess() != null) {
                updateUiWithUser(createUserResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);
            //Complete and destroy login activity once successful
            finish();
        });


        volunteerResultMutableLiveData.observe(this, volunteerResult -> {
            if (volunteerResult == null) {
                return;
            }
            if (volunteerResult.getError() != null) {
                showLoginFailed(volunteerResult.getError());
            }
            if (volunteerResult.getVolunteerList() != null) {
                spinner.setPrompt("Select Name");
                List<Volunteer> volunteerList=volunteerResult.getVolunteerList();
                ArrayAdapter adapter = new ArrayAdapter(this, R.layout.styling,volunteerList);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
            }

        });

        submit.setOnClickListener(v -> {
            if(memberID.getText().toString().trim().isEmpty()||amountPaid.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Kindly Fill in all the fields", Toast.LENGTH_LONG).show();


            }
            else{
                repository.createUser(spinner.getSelectedItem().toString(),
                        memberID.getText().toString(),
                        todayDate,
                        amountPaid.getText().toString(),
                        name,
                        createUserResultMutableLiveData);
            }

        });


    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void onClick(View view) {
        scanCode();
    }

    private void updateUiWithUser(String displayName) {
        Toast.makeText(getApplicationContext(), "Record Inserted Successfully.", Toast.LENGTH_LONG).show();
    }


    private void scanCode() {
        IntentIntegrator i = new IntentIntegrator(this);
        i.setCaptureActivity(CaptureAct.class);
        i.setOrientationLocked(false);
        i.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        i.setPrompt("Scanning Code");
        i.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult ir = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (ir != null) {
            if (ir.getContents() != null) {
                memberID.setText(ir.getContents());
            } else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage("Exit App?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }).create().show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}