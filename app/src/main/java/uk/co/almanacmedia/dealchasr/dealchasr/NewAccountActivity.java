package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import uk.co.almanacmedia.dealchasr.dealspotr.R;

public class NewAccountActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
    }

    public void BackToLogin(View v){
        Intent intent = new Intent(NewAccountActivity.this, LoginActivity.class);
        NewAccountActivity.this.startActivity(intent);
        ((Activity)NewAccountActivity.this).finish();
    }

    public void createAccount(View v){
        EditText nameInput = findViewById(R.id.nameText);
        EditText emailInput = findViewById(R.id.emailTextC);
        EditText passInput = findViewById(R.id.passwordTextC);
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passInput.getText().toString();
        TextView error = findViewById(R.id.errorText);
        if(password != ""){
            new DoCreateAccount(NewAccountActivity.this, error, name, email, password).execute();
        } else {
            error.append("A PASSWORD MUST BE ENTERED");
        }
    }
}
