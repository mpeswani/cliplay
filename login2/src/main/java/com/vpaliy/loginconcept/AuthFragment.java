package com.vpaliy.loginconcept;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public abstract class AuthFragment extends Fragment {
    protected Callback callback;
    protected VerticalTextView caption;
    protected ViewGroup parent;
    protected boolean lock;
    private UserLoginWithEmail mUserLoginWithEmail;

    interface UserLoginWithEmail {
        void onLogin(String email, String password, boolean login);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        mUserLoginWithEmail = (UserLoginWithEmail) context;
        super.onAttach(context);
    }

    public void login(String userName, String password) {
        mUserLoginWithEmail.onLogin(userName, password, authLayout() == R.layout.login_fragment);
    }

    protected boolean validateInput(TextInputLayout email, TextInputLayout password) {
        EditText mail = email.getEditText();
        EditText pass = password.getEditText();
        if (mail == null || email.getEditText().length() == 0) {
            Toast.makeText(getActivity(), "Please enter the correct email id", Toast
                    .LENGTH_SHORT).show();
            return false;
        } else if (!EUtil.validate(mail.getText().toString())) {
            Toast.makeText(getActivity(), R.string.enter_valid_email, Toast.LENGTH_SHORT).show();
            return false;
        } else if (pass == null || password.getEditText().length() < 6) {
            Toast.makeText(getActivity(), "Please enter the password minimum 6 characters", Toast
                    .LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable
            Bundle savedInstanceState) {
        View root = inflater.inflate(authLayout(), container, false);
        caption = root.findViewById(R.id.caption);
        parent = root.findViewById(R.id.root);
        KeyboardVisibilityEvent.setEventListener(getActivity(), isOpen -> {
            callback.scale(isOpen);
            if (!isOpen) {
                clearFocus();
            }
        });
        parent.setOnClickListener(view -> unfold());
        return root;
    }

    public void setCallback(@NonNull Callback callback) {
        this.callback = callback;
    }

    @LayoutRes
    public abstract int authLayout();

    public abstract void fold();

    public abstract void clearFocus();

    public void unfold() {
        if (!lock) {
            caption.setVerticalText(false);
            caption.requestLayout();
            Rotate transition = new Rotate();
            transition.setStartAngle(-90f);
            transition.setEndAngle(0f);
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
            caption.post(() -> {
                TransitionManager.beginDelayedTransition(parent, set);
                caption.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R
                        .dimen.unfolded_size));
                caption.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_label));
                caption.setTranslationX(0);
                ConstraintLayout.LayoutParams params = getParams();
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                params.verticalBias = 0.78f;
                caption.setLayoutParams(params);
            });
            callback.show(this);
            lock = true;
        }
    }

    protected ConstraintLayout.LayoutParams getParams() {
        return ConstraintLayout.LayoutParams.class.cast(caption.getLayoutParams());
    }

    interface Callback {
        void show(AuthFragment fragment);

        void scale(boolean hasFocus);
    }
}
