package com.example.prm392_project_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    DBHelper DB;

    EditText username, emailAddress, password, confirmPassword, phone, address;

    Button registerBtn;

    String userNameFailureMsg = "";
    String emailAddressFailureMsg = "";
    String passwordFailureMsg = "";
    String confirmPasswordFailureMsg = "";
    String phoneFailureMsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        DB = new DBHelper(this);

        username = findViewById(R.id.usernameInputForRegister);
        emailAddress = findViewById(R.id.emailAddressInputForRegister);
        password = findViewById(R.id.passwordInputForRegister);
        confirmPassword = findViewById(R.id.confirmPasswordInputForRegister);
        phone = findViewById(R.id.phoneInputForRegister);
        address = findViewById(R.id.addressInputForRegister);

        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputtedUsername = username.getText().toString();
                String inputtedEmailAddress = emailAddress.getText().toString();
                String inputtedPassword = password.getText().toString();
                String inputtedConfirmPassword = confirmPassword.getText().toString();
                String inputtedPhone = phone.getText().toString();
                String inputtedAddress = address.getText().toString();

                boolean checkUsernameValidation = validateInputtedUsername(inputtedUsername);
                boolean checkEmailAddressValidation = validateInputtedEmailAddress(inputtedEmailAddress);
                boolean checkPasswordValidation = validateInputtedPassword(inputtedPassword);
                boolean checkConfirmPasswordValidation = validateInputtedConfirmPassword(inputtedConfirmPassword, inputtedPassword);
                boolean checkPhoneValidation = validateInputtedPhone(inputtedPhone);

                if (checkUsernameValidation && checkEmailAddressValidation && checkPasswordValidation
                        && checkConfirmPasswordValidation && checkPhoneValidation) {
                    // Toast.makeText(getApplicationContext(), "All correct", Toast.LENGTH_LONG).show();

                    Boolean checkinsertdata = DB.insertuserdata(inputtedUsername, inputtedEmailAddress,
                            inputtedPhone, inputtedPassword, inputtedAddress);
                    if (checkinsertdata == true) {
                        Toast.makeText(getApplicationContext(), "Register Successfully", Toast.LENGTH_SHORT).show();

                        // Navigate to home screen
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Register Failed", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    StringBuilder failureMessages = new StringBuilder();
                    if (!userNameFailureMsg.equals("")) {
                        failureMessages.append(userNameFailureMsg).append("\n");
                    }
                    if (!emailAddressFailureMsg.equals("")) {
                        failureMessages.append(emailAddressFailureMsg).append("\n");
                    }
                    if (!passwordFailureMsg.equals("")) {
                        failureMessages.append(passwordFailureMsg).append("\n");
                    }
                    if (!confirmPasswordFailureMsg.equals("")) {
                        failureMessages.append(confirmPasswordFailureMsg).append("\n");
                    }
                    if (!phoneFailureMsg.equals("")) {
                        failureMessages.append(phoneFailureMsg).append("\n");
                    }

                    // Remove the last newline character
                    if (failureMessages.length() > 0) {
                        failureMessages.setLength(failureMessages.length() - 1);
                    }

                    // Show the failure messages in an AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Validation Failed");
                    builder.setMessage(failureMessages.toString());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    public boolean validateInputtedUsername(String inputtedUsername) {
        Cursor cursor = DB.getUserByUsername(inputtedUsername);

        if (cursor.moveToFirst()) {
            userNameFailureMsg = "The inputted username already exists.";
            return false;
        }

        if (inputtedUsername.length() < 8) {
            userNameFailureMsg = "The inputted username length must be greater than or equal to 8.";
            return false;
        }

        userNameFailureMsg = "";
        return true;
    }

    public boolean validateInputtedEmailAddress(String inputtedEmailAddress) {
        Cursor cursor = DB.getUserByEmailAddress(inputtedEmailAddress);
        if (cursor.moveToFirst()) {
            emailAddressFailureMsg = "The inputted email address already exists.";
            return false;
        }

        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!inputtedEmailAddress.matches(emailPattern)) {
            emailAddressFailureMsg = "The inputted email address is not in the correct form.";
            return false;
        }

        emailAddressFailureMsg = "";
        return true;
    }

    private boolean validateInputtedPassword(String inputtedPassword) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";

        if (!inputtedPassword.matches(passwordPattern)) {
            passwordFailureMsg = "A password must contain lowercase, uppercase, numeric and special values and has at least 8 characters.";
            return false;
        }

        passwordFailureMsg = "";
        return true;
    }

    private boolean validateInputtedConfirmPassword(String inputtedConfirmPassword, String inputtedPassword) {
        if (!inputtedConfirmPassword.equals(inputtedPassword)) {
            confirmPasswordFailureMsg = "Confirm password does not match inputted password.";
            return false;
        }

        confirmPasswordFailureMsg = "";
        return true;
    }

    private boolean validateInputtedPhone(String inputtedPhone) {
        Cursor cursor = DB.getUserByPhoneNumber(inputtedPhone);
        if (cursor.moveToFirst()) {
            phoneFailureMsg = "The inputted phone number already exists.";
            return false;
        }

        String phonePattern = "\\d{8,}";

        if (!inputtedPhone.matches(phonePattern)) {
            phoneFailureMsg = "A phone number must have at least 8 numeric values.";
            return false;
        }

        phoneFailureMsg = "";
        return true;
    }
}