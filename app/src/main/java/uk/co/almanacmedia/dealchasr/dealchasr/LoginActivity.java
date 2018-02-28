package uk.co.almanacmedia.dealchasr.dealchasr;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.String;

import uk.co.almanacmedia.dealchasr.dealchasr.R;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = findViewById(R.id.loginButton);
        login.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            EditText emailInput = findViewById(R.id.nameText);
            EditText passInput = findViewById(R.id.passwordTextC);
            String email = emailInput.getText().toString();
            String password = passInput.getText().toString();
            TextView error = findViewById(R.id.errorText);
            if(password != ""){
                new DoLogin(LoginActivity.this, error, email, password).execute();
            } else {
                error.append("A PASSWORD MUST BE ENTERED");
            }
        }
    };

    public void newAccount(View v){
        Intent intent = new Intent(LoginActivity.this, NewAccountActivity.class);
        LoginActivity.this.startActivity(intent);
        ((Activity)LoginActivity.this).finish();
    }

    public void forgottenPassword(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://password.dealchasr.co.uk"));
        LoginActivity.this.startActivity(browserIntent);
    }
}
