package de.tum.in.tumcampusapp.component.ui.eduroam;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.other.generic.activity.BaseActivity;
import de.tum.in.tumcampusapp.utils.Component;
import de.tum.in.tumcampusapp.utils.Const;
import de.tum.in.tumcampusapp.utils.Utils;

import static android.provider.Settings.ADD_WIFI_RESULT_SUCCESS;
import static android.provider.Settings.EXTRA_WIFI_NETWORK_RESULT_LIST;

/**
 * Activity that allows the user to easily setup eduroam.
 * Collects all the information needed.
 */
public class SetupEduroamActivity extends BaseActivity {

    private TextInputEditText id;
    private TextInputEditText password;

    public SetupEduroamActivity() {
        super(R.layout.activity_setup_eduroam, Component.EDUROAM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra(Const.EXTRA_FOREIGN_CONFIGURATION_EXISTS, false)) {
            showDeleteProfileDialog();
        }

        // Enable 'More Info' links
        ((TextView) findViewById(R.id.text_with_link_2)).setMovementMethod(LinkMovementMethod.getInstance());

        id = findViewById(R.id.wifi_lrz_id);
        id.setText(Utils.getSetting(this, Const.PROFILE_EMAIL, ""));
        password = findViewById(R.id.wifi_password);

        //Set the focus for improved UX experience
        if (id.getText() != null && id.getText().length() == 0) {
            id.requestFocus();
        } else {
            password.requestFocus();
        }

        findViewById(R.id.eduroam_config_error).setOnClickListener(view -> {
            showDeleteProfileDialog();
        });
    }

    private void showDeleteProfileDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.eduroam_dialog_title)
                .setMessage(R.string.eduroam_dialog_info_text)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.eduroam_dialog_preferences, (dialogInterface, i) -> {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(intent);
                })
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_corners_background);
        }

        dialog.show();
    }

    /**
     * Start setting up the wifi connection
     *
     * @param v Setup button handle
     */
    @SuppressWarnings("UnusedParameters")
    public void onClickSetup(View v) {
        //Identification can not be empty
        if (id.getText().length() == 0) {
            Utils.showToast(this, getString(R.string.eduroam_not_valid_id));
            return;
        }

        //We need some sort of password
        if (password.getText().length() == 0) {
            Utils.showToast(this, getString(R.string.eduroam_please_enter_password));
            return;
        }

        //Do Setup
        EduroamController manager = new EduroamController(getApplicationContext());

        String identityString = id.getText().toString();
        String passwordString = password.getText().toString();

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Invoke add wifi network intent
            startActivityForResult(manager.getEduroamIntent(identityString, passwordString), 0);
        } else {
            // Add wifi network or suggestion explicit
            boolean success = manager.configureEduroam(identityString, passwordString);
            showResultToast(success);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // process add wifi network result
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (resultCode == RESULT_OK) {
                // user agreed to save configurations: still need to check individual results
                if (data != null && data.hasExtra(EXTRA_WIFI_NETWORK_RESULT_LIST)) {
                    ArrayList<Integer> resultList = data.getIntegerArrayListExtra(EXTRA_WIFI_NETWORK_RESULT_LIST);

                    boolean success = resultList.contains(ADD_WIFI_RESULT_SUCCESS);
                    showResultToast(success);
                }
            } else {
                // User refused to save configurations
                Utils.showToast(this, R.string.eduroam_refused);
                finish();
            }
        }
    }

    private void showResultToast(boolean success) {
        if (success) {
            Utils.showToast(this, R.string.eduroam_success);
            Utils.setSetting(this, Const.REFRESH_CARDS, true);
            finish();
        } else {
            findViewById(R.id.eduroam_config_error).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Open android settingsPrefix
     *
     * @param v Button handle
     */
    @SuppressWarnings("UnusedParameters")
    public void showDataBackupSettings(View v) {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    @SuppressWarnings("UnusedParameters")
    public void onClickCancel(View v) {
        Utils.setSetting(this, Const.REFRESH_CARDS, true);
        finish();
    }
}
