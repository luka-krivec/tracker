package si.krivec.tracker;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import authentication.AccountGeneral;

import static authentication.AccountGeneral.sServerAuthenticate;
import static si.krivec.tracker.AuthenticatorActivity.ARG_ACCOUNT_TYPE;
import static si.krivec.tracker.AuthenticatorActivity.KEY_ERROR_MESSAGE;
import static si.krivec.tracker.AuthenticatorActivity.PARAM_USER_PASS;



public class SignUpActivity extends ActionBarActivity implements View.OnClickListener {

    private String TAG = getClass().getSimpleName();
    private String mAccountType;

    private TextView txtAlreadyMember;
    private Button btnSignUpSubmit;
    private EditText editName;
    private EditText editAccountName;
    private EditText editAccountPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtAlreadyMember = (TextView) findViewById(R.id.txtAlreadyMember);
        btnSignUpSubmit = (Button) findViewById(R.id.btnSignUpSubmit);
        editName = (EditText) findViewById(R.id.editSignUpName);
        editAccountName = (EditText) findViewById(R.id.editSignUpAccountName);
        editAccountPassword = (EditText) findViewById(R.id.editSignUpAccountPassword);

        txtAlreadyMember.setOnClickListener(this);
        btnSignUpSubmit.setOnClickListener(this);

        mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAlreadyMember:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.btnSignUpSubmit:
                createAccount();
                break;
        }
    }

    private void createAccount() {

        // Validation!

        new AsyncTask<String, Void, Intent>() {

            String name = editName.getText().toString().trim();
            String accountName = editAccountName.getText().toString().trim();
            String accountPassword = editAccountPassword.getText().toString().trim();

            @Override
            protected Intent doInBackground(String... params) {

                Log.d("CyclingMaster", TAG + "> Started authenticating");

                String authtoken;
                Bundle data = new Bundle();
                try {
                    authtoken = sServerAuthenticate.userSignUp(name, accountName, accountPassword, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(PARAM_USER_PASS, accountPassword);
                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
