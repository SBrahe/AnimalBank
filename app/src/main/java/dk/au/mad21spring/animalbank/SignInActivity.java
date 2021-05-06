package dk.au.mad21spring.animalbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

//code inspired by lectures
public class SignInActivity extends AppCompatActivity {

    public static final int REQUEST_LOGIN = 1010;

    FirebaseAuth auth;
    Repository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repo = Repository.getAnimalRepository(getApplicationContext());

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
            repo.setUser(auth.getCurrentUser());
            repo.setUid(auth.getCurrentUser().getUid());
            goToMainApp();
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

        if (requestCode == REQUEST_LOGIN) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();
                goToMainApp();
            }
        }
    }

    public void goToMainApp() {
        Intent intent = new Intent(this, NavBarActivity.class);
        startActivity(intent);
        finish();
    }
}