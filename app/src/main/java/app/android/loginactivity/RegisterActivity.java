package app.android.loginactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterActivity extends AppCompatActivity {

    EditText eTxtEmailRegister , eTxtPasswordRegister;
    Button btnRegister;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        eTxtEmailRegister = (EditText) findViewById(R.id.txtEmailRegister);
        eTxtPasswordRegister = (EditText) findViewById(R.id.txtPasswordRegister);
        btnRegister = (Button) findViewById(R.id.btnRegisterAkun);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAccount();
            }
        });
        
    }

    private void registerAccount() {
        String email = eTxtEmailRegister.getText().toString().trim();
        String password = eTxtPasswordRegister.getText().toString().trim();

        if (email.isEmpty()){
            eTxtEmailRegister.setError("Email Tidak Boleh Kosong");
            eTxtEmailRegister.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            eTxtEmailRegister.setError("Masukan Email Dengan Benar");
            eTxtEmailRegister.requestFocus();
            return;
        }

        if (password.isEmpty()){
            eTxtPasswordRegister.setError("Password Tidak Boleh Kosong");
            eTxtPasswordRegister.requestFocus();
            return;
        }

        if (password.length() < 6){
            eTxtEmailRegister.setError("Masukan Minimal 6 karakter");
            eTxtEmailRegister.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //kalo register berhasil langsung punya akun
                if (task.isSuccessful()){
                    finish();
                    Toast.makeText(RegisterActivity.this, "Behasil Register Akun", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {

                    //cek kalo dia udah register atau belum
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        finish();
                        Toast.makeText(RegisterActivity.this, "Anda Sudah Register", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else {
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    
                }
            }
        });

    }

}
