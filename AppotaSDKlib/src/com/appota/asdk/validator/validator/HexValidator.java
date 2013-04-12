package com.appota.asdk.validator.validator;

import java.util.regex.Pattern;

import android.content.Context;

import com.appota.asdk.R;
import com.appota.asdk.validator.AbstractValidator;


public class HexValidator extends AbstractValidator {

    /**
     * This is Hex Pattern to verify value.
     */
    private static final Pattern mPattern = Pattern.compile("^(#|)[0-9A-Fa-f]+$");

    private int mErrorMessage = R.string.validator_alnum;

    public HexValidator(Context c) {
        super(c);
    }

    @Override
    public boolean isValid(String value) {
        return mPattern.matcher(value).matches();
    }

    @Override
    public String getMessage() {
        return mContext.getString(mErrorMessage);
    }
}
