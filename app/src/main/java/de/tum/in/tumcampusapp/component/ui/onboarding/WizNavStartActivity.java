package de.tum.in.tumcampusapp.component.ui.onboarding;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.app.AuthenticationManager;
import de.tum.in.tumcampusapp.api.tumonline.AccessTokenManager;
import de.tum.in.tumcampusapp.component.other.generic.activity.ActivityForLoadingInBackground;
import de.tum.in.tumcampusapp.utils.Const;
import de.tum.in.tumcampusapp.utils.NetUtils;
import de.tum.in.tumcampusapp.utils.Utils;

/**
 * Displays the first page of the startup wizard, where the user can enter his lrz-id.
 */
public class WizNavStartActivity extends ActivityForLoadingInBackground<String, Boolean> implements OnClickListener, TextWatcher {

    private final AccessTokenManager accessTokenManager = new AccessTokenManager(this);
    private String lrzId;

    private EditText lrzIdEditText;
    private AppCompatButton nextButton;

    public WizNavStartActivity() {
        super(R.layout.activity_wiznav_start);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disableRefresh();
        findViewById(R.id.wizard_start_layout).requestFocus();

        nextButton = findViewById(R.id.next_button);

        lrzIdEditText = findViewById(R.id.lrz_id);
        lrzIdEditText.addTextChangedListener(this);
        lrzIdEditText.setText(Utils.getSetting(this, Const.LRZ_ID, ""));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // make sure to delete to delete information that might lead the app to believe the user enabled TUMonline
        Utils.setSetting(this, Const.LRZ_ID, null);
        Utils.setSetting(this, Const.ACCESS_TOKEN, null);
    }

    /**
     * Handle click on next button.
     *
     * @param next Next button handle
     */
    @SuppressWarnings("UnusedParameters")
    public void onClickNext(View next) {
        // Upon clicking on next button and there is no internet connection -> toast to the user.
        if (!NetUtils.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.please_connect_to_internet), Toast.LENGTH_LONG)
                 .show();
            return;
        }

        String enteredId = lrzIdEditText.getText().toString().toLowerCase(Locale.GERMANY);

        // check if lrz could be valid?
        if (!enteredId.matches(Const.TUM_ID_PATTERN)) {
            Utils.showToast(this, R.string.error_lrz_wrong);
            return;
        }

        lrzId = enteredId;
        Utils.setSetting(this, Const.LRZ_ID, lrzId);

        // is access token already set?
        if (accessTokenManager.hasValidAccessToken()) {
            // show Dialog first
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.dialog_new_token))
                    .setPositiveButton(getString(R.string.generate_new_token), this)
                    .setNegativeButton(getString(R.string.cancel), this)
                    .show();
        } else {
            startLoading(lrzId); // create a new token
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean isEmpty = s.toString().trim().isEmpty();
        float alpha = (isEmpty) ? 0.5f : 1.0f;
        nextButton.setClickable(!isEmpty);
        nextButton.setAlpha(alpha);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Pass
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Pass
    }

    /**
     * Handle click in dialog buttons.
     *
     * @param dialog Dialog handle
     * @param which  Button clicked
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            AuthenticationManager am = new AuthenticationManager(this);
            am.clearKeys();
            am.generatePrivateKey(null);
            startLoading(lrzId); // create a new token
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            onLoadFinished(true);
        }
    }

    /**
     * Requests an access-token from the TumOnline server in background.
     *
     * @param arg Unused
     * @return True if the access token was successfully created
     */
    @Override
    protected Boolean onLoadInBackground(String... arg) {
        return accessTokenManager.requestAccessToken(this, arg[0]);
    }

    /**
     * Opens second wizard page if access token available.
     *
     * @param result Was access token successfully created
     */
    @Override
    protected void onLoadFinished(Boolean result) {
        if (result) {
            Intent intent = new Intent(this, WizNavCheckTokenActivity.class);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            startActivity(intent);
        } else {
            showLoadingEnded();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        lrzIdEditText.removeTextChangedListener(this);
    }

}