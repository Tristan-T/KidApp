package com.bcttgd.kidapp.Watchers;

import android.text.Editable;
import android.text.TextWatcher;

import com.bcttgd.kidapp.App;
import com.bcttgd.kidapp.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordWatcher implements TextWatcher {
    private final TextInputLayout passwordLayout;

    public PasswordWatcher(TextInputLayout passwordLayout) {
        this.passwordLayout = passwordLayout;
    }

    //CF : https://stackoverflow.com/questions/36574183/how-to-validate-password-field-in-android
    public boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d\\w\\W]{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!isValidPassword(charSequence.toString())){
            passwordLayout.setError(App.getContext().getResources().getString(R.string.error_password));
        } else {
            passwordLayout.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
