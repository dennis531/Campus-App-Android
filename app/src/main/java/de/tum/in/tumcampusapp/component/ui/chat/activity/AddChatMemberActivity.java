package de.tum.in.tumcampusapp.component.ui.chat.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.general.ApiHelper;
import de.tum.in.tumcampusapp.component.other.generic.activity.BaseActivity;
import de.tum.in.tumcampusapp.component.ui.chat.adapter.MemberSuggestionsListAdapter;
import de.tum.in.tumcampusapp.component.ui.chat.api.ChatAPI;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatMember;
import de.tum.in.tumcampusapp.component.ui.chat.model.ChatRoom;
import de.tum.in.tumcampusapp.database.TcaDb;
import de.tum.in.tumcampusapp.utils.Component;
import de.tum.in.tumcampusapp.utils.ConfigUtils;
import de.tum.in.tumcampusapp.utils.Const;
import de.tum.in.tumcampusapp.utils.Utils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Allows user to search for other users which he or she can then add to the ChatRoom
 */
public class AddChatMemberActivity extends BaseActivity {
    private static final int THRESHOLD = 3; // min number of characters before getting suggestions
    private static final int DELAY = 1000; // millis after user stopped typing before getting suggestions
    private ChatRoom room;
    private ChatAPI apiClient;
    private CompositeDisposable compositeDisposable;
    private AutoCompleteTextView searchView;

    // for delayed suggestions
    private Handler delayHandler;
    private Runnable suggestionRunnable = this::getSuggestions;

    private List<ChatMember> suggestions;

    public AddChatMemberActivity() {
        super(R.layout.activity_add_chat_member, Component.CHAT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suggestions = new ArrayList<>();
        delayHandler = new Handler();

        String encodedRoom = getIntent().getStringExtra(Const.CURRENT_CHAT_ROOM);
        room = new Gson().fromJson(encodedRoom, ChatRoom.class);
        Utils.log("ChatRoom: " + room.getTitle() + " (roomId: " + room.getId() + ")");

        apiClient = (ChatAPI) ConfigUtils.getApiClient(this, Component.CHAT);

        compositeDisposable = new CompositeDisposable();

        searchView = findViewById(R.id.chat_user_search);
        searchView.setThreshold(THRESHOLD);
        searchView.setAdapter(new MemberSuggestionsListAdapter(this, suggestions));

        searchView.setOnItemClickListener((adapterView, view, pos, l) -> {
            ChatMember member = (ChatMember) adapterView.getItemAtPosition(pos);
            showConfirmDialog(member);
        });

        searchView.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Utils.log("Search");
                delayHandler.removeCallbacks(suggestionRunnable);
                getSuggestions();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // do nothing, we want to know the new input -> onTextChanged
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                delayHandler.removeCallbacks(suggestionRunnable);

                if (charSequence.length() < THRESHOLD) {
                    return;
                }

                boolean containsDigit = false;
                for (int i = 0; i < charSequence.length(); i++) {
                    if (Character.isDigit(charSequence.charAt(i))) {
                        containsDigit = true;
                        break;
                    }
                }
                if (containsDigit) {
                    // don't try to get new suggestions (we don't autocomplete IDs)
                    searchView.setError(null);
                    return;
                }

                delayHandler.postDelayed(suggestionRunnable, DELAY);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing, we do everything in onTextChanged
            }
        });

        ImageView qrCode = findViewById(R.id.join_chat_qr_code);
        qrCode.setImageBitmap(ApiHelper.createQRCode(new Gson().toJson(room)));
    }

    private void getSuggestions() {
        String input = searchView.getText()
                                 .toString();

        Utils.log("Get suggestions for " + input);
        Disposable disposable = Single.fromCallable(() -> apiClient.searchChatMember(input))
                                      .subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(response -> {
                                          searchView.setError(null);
                                          suggestions = response;
                                          ((MemberSuggestionsListAdapter) searchView.getAdapter()).updateSuggestions(suggestions);
                                      }, t -> onError());

        compositeDisposable.add(disposable);
    }

    private void onError() {
        searchView.setError(getString(R.string.error_user_not_found));
    }

    private void showConfirmDialog(ChatMember member) {
        String message = getString(R.string.add_user_to_chat_message,
                                   member.getDisplayName(), room.getTitle());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.add, (dialogInterface, i) -> {
                    joinRoom(member);
                    reset();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow()
                  .setBackgroundDrawableResource(R.drawable.rounded_corners_background);
        }

        dialog.show();
    }

    /**
     * Clears everything from the last search.
     */
    private void reset() {
        suggestions = new ArrayList<>();
        ((MemberSuggestionsListAdapter) searchView.getAdapter()).updateSuggestions(suggestions);
        searchView.setText("");
    }

    private void joinRoom(ChatMember member) {
        Disposable disposable = Single.fromCallable(() -> apiClient.addMemberToChatRoom(room, member))
                                      .subscribeOn(Schedulers.io())
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(response -> {
                                          if (response != null) {
                                              if (response.getMembers() != null) {
                                                  TcaDb.Companion.getInstance(getBaseContext())
                                                                 .chatRoomDao()
                                                                 .updateMemberCount(response.getMembers(), response.getId());
                                              }
                                              Utils.showToast(getBaseContext(), R.string.chat_member_added);
                                          } else {
                                              Utils.showToast(getBaseContext(), R.string.error_something_wrong);
                                          }
                                      }, t -> Utils.showToast(getBaseContext(), R.string.error));

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
