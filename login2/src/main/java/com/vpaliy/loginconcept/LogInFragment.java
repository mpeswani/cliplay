package com.vpaliy.loginconcept;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

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

public class LogInFragment extends AuthFragment {
    private List<TextInputEditText> views;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lock = true;
        caption.setText(getString(R.string.log_in_label));
        views = new ArrayList<>();
        views.add(view.findViewById(R.id.email_input_edit));
        views.add(view.findViewById(R.id.password_input_edit));
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_log_in));
        final TextInputLayout inputLayout = view.findViewById(R.id.password_input);
        final TextInputLayout emailInput = view.findViewById(R.id.email_input);
        for (TextInputEditText editText : views) {
            if (editText.getId() == R.id.password_input_edit) {
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                inputLayout.setTypeface(boldTypeface);
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
        EditText editText = inputLayout.getEditText();
        if (editText != null) {
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE && validateInput(emailInput,
                        inputLayout)) {
                    login(emailInput.getEditText().getText().toString(),
                            inputLayout.getEditText().getText().toString());
                }
                return false;
            });
        }
        EditText email = emailInput.getEditText();
        if (email != null && !TextUtils.isEmpty(getActivity().getIntent().getStringExtra("email")
        )) {
            email.setText(getActivity().getIntent().getStringExtra("email"));
        }
//        float px = convertDpToPixel(24);
//        Glide.with(getActivity()).asDrawable()
//                .load(R.drawable.flag_in)
//                .apply(new RequestOptions().override((int) px, (int) px))
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable com
//                            .bumptech.glide.request.transition.Transition<? super Drawable>
//                            transition) {
////                        resource.setAlpha(128);
//                        views.get(0).setCompoundDrawablesRelativeWithIntrinsicBounds(resource,
//                                null, null, null);
//                    }
//                });
    }

//    public static float convertDpToPixel(float dp) {
//        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
//        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
//        return px;
//    }

    @Override
    public int authLayout() {
        return R.layout.login_fragment;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
        final float padding = getResources().getDimension(R.dimen.folded_label_padding) / 2;
        set.addListener(new Transition.TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                caption.setTranslationX(-padding);
                caption.setRotation(0);
                caption.setVerticalText(true);
                caption.requestLayout();
            }
        });
        TransitionManager.beginDelayedTransition(parent, set);
        caption.setTextSize(TypedValue.COMPLEX_UNIT_PX, caption.getTextSize() / 2);
        caption.setTextColor(Color.WHITE);
        ConstraintLayout.LayoutParams params = getParams();
        params.leftToLeft = ConstraintLayout.LayoutParams.UNSET;
        params.verticalBias = 0.5f;
        caption.setLayoutParams(params);
        caption.setTranslationX(caption.getWidth() / 8 - padding);
    }

    @Override
    public void clearFocus() {
        for (View view : views) view.clearFocus();
    }
}
