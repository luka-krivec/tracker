package si.krivec.tracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView txtEmail;
    private TextView txtPassword;
    private Button btnRegister;

    private FirebaseAuth mAuth;

    private static final String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPassword = (TextView) findViewById(R.id.txtPassword);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnRegister) {
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();

            if(validateForm()) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegistrationActivity.this, R.string.password6,
                                            Toast.LENGTH_SHORT).show();
                                    Toast.makeText(RegistrationActivity.this, task.getResult().toString(),
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseCrash.log("Password less than 6 chars try");
                                } else {
                                    Toast.makeText(RegistrationActivity.this, R.string.registration_success,
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.getMessage());
                                Toast.makeText(RegistrationActivity.this, R.string.authentication_failed,
                                        Toast.LENGTH_SHORT).show();
                                FirebaseCrash.log("Authentication failed.");
                            }
                });
            } else {
                Toast.makeText(RegistrationActivity.this, R.string.data_required,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            valid = false;
        }

        return valid;
    }
}
