package com.bcttgd.kidapp.Watchers;

import android.text.Editable;
import android.text.TextWatcher;

import com.bcttgd.kidapp.App;
import com.bcttgd.kidapp.R;
import com.google.android.material.textfield.TextInputLayout;

public class NameWatcher implements TextWatcher {
    private final TextInputLayout nameLayout;

    public NameWatcher(TextInputLayout nameLayout) {
        this.nameLayout = nameLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            nameLayout.setError(null);
        } else {
            nameLayout.setError(App.getContext().getResources().getString(R.string.error_name));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
