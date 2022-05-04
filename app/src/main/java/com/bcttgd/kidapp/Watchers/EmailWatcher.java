package com.bcttgd.kidapp.Watchers;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;

import com.bcttgd.kidapp.App;
import com.bcttgd.kidapp.R;
import com.google.android.material.textfield.TextInputLayout;

public class EmailWatcher implements TextWatcher {
    private final TextInputLayout emailLayout;

    public EmailWatcher(TextInputLayout emailLayout) {
        this.emailLayout = emailLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!TextUtils.isEmpty(charSequence) && Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()){
            emailLayout.setError(null);
        } else {
            emailLayout.setError(App.getContext().getResources().getString(R.string.error_email));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
