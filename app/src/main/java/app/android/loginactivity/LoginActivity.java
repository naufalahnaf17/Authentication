package app.android.loginactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText eTxtEmail , eTxtPassword;
    Button btnLogin;
    TextView btnRegister;
    ProgressBar loading;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eTxtEmail = (EditText) findViewById(R.id.txtEmail);
        eTxtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (TextView) findViewById(R.id.btnRegister);
        loading = (ProgressBar) findViewById(R.id.loading);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToRegisterActivity();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

    }

    private void userLogin() {
        String email = eTxtEmail.getText().toString().trim();
        String password = eTxtPassword.getText().toString().trim();

        if (email.isEmpty()){
            eTxtEmail.setError("Email Tidak Boleh Kosong");
            eTxtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            eTxtPassword.setError("Password Tidak Boleh Kosong");
            eTxtPassword.requestFocus();
            return;
        }

        loading.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loading.setVisibility(View.GONE);
                //cek kalo login berhasil
                if (task.isSuccessful()){
                    finish();
                    Intent intent = new Intent(LoginActivity.this , SetProfile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    Toast.makeText(LoginActivity.this, "Gagal Login", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(LoginActivity.this , SetProfile.class));
        }

    }

    private void moveToRegisterActivity() {
        Intent intent = new Intent(this , RegisterActivity.class);
        startActivity(intent);
    }
}
