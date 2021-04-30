package dk.au.mad21spring.animalbank;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

//code inspired by lectures
public class SignInActivity extends AppCompatActivity {

    public static final int REQUEST_LOGIN = 1010;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button btnSignIn = findViewById(R.id.signInBtn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        if (auth.getCurrentUser() != null) {
            goToCameraActivity();
        } else {

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build()
            );

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    REQUEST_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== REQUEST_LOGIN) {

            if (resultCode == RESULT_OK) {
                String userId = auth.getCurrentUser().getUid();
                Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();

                goToCameraActivity();
            }
        }
    }

    public void goToCameraActivity(){
        Intent intent = new Intent(this,CameraActivity.class);
        startActivity(intent);
        finish();
    }
}