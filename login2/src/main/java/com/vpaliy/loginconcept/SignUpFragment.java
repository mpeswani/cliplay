package com.vpaliy.loginconcept;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class SignUpFragment extends AuthFragment {
    protected List<TextInputEditText> views;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_sign_up));
        views = new ArrayList<>();
        views.add(view.findViewById(R.id.email_input_edit));
        views.add(view.findViewById(R.id.password_input_edit));
        views.add(view.findViewById(R.id.confirm_password_edit));
        caption.setText(getString(R.string.sign_up_label));

        final TextInputLayout emailLayout = view.findViewById(R.id.email_input);
        final TextInputLayout inputLayout = view.findViewById(R.id.password_input);
        final TextInputLayout confirmLayout = view.findViewById(R.id.confirm_password);
        for (TextInputEditText editText : views) {
            if (editText.getId() == R.id.password_input_edit) {
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                emailLayout.setTypeface(boldTypeface);
                inputLayout.setTypeface(boldTypeface);
                confirmLayout.setTypeface(boldTypeface);
                editText.addTextChangedListener(new TextWatcherAdapter() {
                    @Override
                    public void afterTextChanged(Editable editable) {
                        inputLayout.setPasswordVisibilityToggleEnabled(editable.length() > 0);
                    }
                });
            }
            editText.setOnFocusChangeListener((temp, hasFocus) -> {
                if (!hasFocus) {
                    boolean isEnabled = editText.getText().length() > 0;
                    editText.setSelected(isEnabled);
                }
            });
        }
        caption.setVerticalText(true);
        foldStuff();
        caption.setTranslationX(getTextPadding());

        EditText editText = confirmLayout.getEditText();
        EditText pass = inputLayout.getEditText();
        if (editText != null && pass != null) {
            editText.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
                if (!editText.getText().toString().equals(pass.getText().toString())) {
                    Toast.makeText(getActivity(), "Confirm password does not match", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (actionId == EditorInfo.IME_ACTION_DONE && validateInput(emailLayout, confirmLayout)) {
                    login(emailLayout.getEditText().getText().toString(),
                            confirmLayout.getEditText().getText().toString());
                }
                return false;
            });
        }
    }

    @Override
    public int authLayout() {
        return R.layout.sign_up_fragment;
    }

    @Override
    public void clearFocus() {
        for (View view : views) view.clearFocus();
    }

    @Override
    public void fold() {
        lock = false;
        Rotate transition = new Rotate();
        transition.setEndAngle(-90f);
        transition.addTarget(caption);
        TransitionSet set = new TransitionSet();
        set.setDuration(getResources().getInteger(R.integer.duration));
        ChangeBounds changeBounds = new ChangeBounds();
        set.addTransition(changeBounds);
        set.addTransition(transition);
        TextSizeTransition sizeTransition = new TextSizeTransition();
        sizeTransition.addTarget(caption);
        set.addTransition(sizeTransition);
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        set.addListener(new Transition.TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                caption.setTranslationX(getTextPadding());
                caption.setRotation(0);
                caption.setVerticalText(true);
                caption.requestLayout();
            }
        });
        TransitionManager.beginDelayedTransition(parent, set);
        foldStuff();
        caption.setTranslationX(-caption.getWidth() / 8 + getTextPadding());
    }

    private void foldStuff() {
        caption.setTextSize(TypedValue.COMPLEX_UNIT_PX, caption.getTextSize() / 2f);
        caption.setTextColor(Color.WHITE);
        ConstraintLayout.LayoutParams params = getParams();
        params.rightToRight = ConstraintLayout.LayoutParams.UNSET;
        params.verticalBias = 0.5f;
        caption.setLayoutParams(params);
    }

    private float getTextPadding() {
        return getResources().getDimension(R.dimen.folded_label_padding) / 2.1f;
    }
}
