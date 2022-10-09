package de.uos.campusapp.component.ui.chat.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.WorkManager;
import de.uos.campusapp.R;
import de.uos.campusapp.component.other.generic.activity.ActivityForDownloadingExternal;
import de.uos.campusapp.component.ui.chat.ChatMessageViewModel;
import de.uos.campusapp.component.ui.chat.ChatRoomController;
import de.uos.campusapp.component.ui.chat.api.ChatAPI;
import de.uos.campusapp.component.ui.chat.adapter.ChatHistoryAdapter;
import de.uos.campusapp.component.ui.chat.model.ChatMember;
import de.uos.campusapp.component.ui.chat.model.ChatMessageItem;
import de.uos.campusapp.component.ui.chat.model.AbstractChatRoom;
import de.uos.campusapp.component.ui.chat.model.ChatRoom;
import de.uos.campusapp.component.ui.chat.repository.ChatMessageLocalRepository;
import de.uos.campusapp.component.ui.chat.repository.ChatMessageRemoteRepository;
import de.uos.campusapp.component.ui.overview.CardManager;
import de.uos.campusapp.database.CaDb;
import de.uos.campusapp.service.DownloadWorker;
import de.uos.campusapp.service.SendChatMessageWorker;
import de.uos.campusapp.utils.Component;
import de.uos.campusapp.utils.ConfigConst;
import de.uos.campusapp.utils.ConfigUtils;
import de.uos.campusapp.utils.Const;
import de.uos.campusapp.utils.Utils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Shows an ongoing chat conversation.
 * <p/>
 * NEEDS: Const.CURRENT_CHAT_ROOM set in incoming bundle (json serialised object of class ChatRoom)
 * Const.CURRENT_CHAT_MEMBER set in incoming bundle (json serialised object of class ChatMember)
 */
public class ChatActivity extends ActivityForDownloadingExternal
        implements AbsListView.OnScrollListener, ChatHistoryAdapter.OnRetrySendListener {

    public static AbstractChatRoom mCurrentOpenChatRoom; // determines whether there will be a notification or not

    private ChatMessageViewModel chatMessageViewModel;
    private CompositeDisposable disposables = new CompositeDisposable();

    private ListView messagesListView;
    private ChatHistoryAdapter chatHistoryAdapter;
    private EditText messageEditText;
    private ProgressBar progressbar;

    private AbstractChatRoom currentChatRoom;
    private ChatMember currentChatMember;
    private boolean isLoadingMore;

    private Handler pollingHandler;

    private final int POLLING_INTERVAL = 30000;  // Polls every 30 seconds

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleBroadcastReceive(intent);
        }
    };

    public ChatActivity() {
        super(R.layout.activity_chat, Component.CHAT);
        // TODO(pfent): Const.CURRENT_CHAT_ROOM was previously non-existent
    }

    @Nullable
    @Override
    public DownloadWorker.Action getMethod() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupToolbarTitle();
        initChatMessageViewModel();
        bindUIElements();
    }

    private void setupToolbarTitle() {
        String encodedChatRoom = getIntent().getStringExtra(Const.CURRENT_CHAT_ROOM);
        currentChatRoom = new Gson().fromJson(encodedChatRoom, ChatRoom.class);
        currentChatMember = Utils.getSetting(this, Const.CHAT_MEMBER, ChatMember.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(currentChatRoom.getTitle());
        }
    }

    private void initChatMessageViewModel() {
        CaDb caDb = CaDb.Companion.getInstance(this);

        ChatMessageRemoteRepository remoteRepository = ChatMessageRemoteRepository.INSTANCE;
        remoteRepository.setApiClient((ChatAPI) ConfigUtils.getApiClient(this, Component.CHAT));

        ChatMessageLocalRepository localRepository = ChatMessageLocalRepository.INSTANCE;
        localRepository.setDb(caDb);

        chatMessageViewModel = new ChatMessageViewModel(localRepository, remoteRepository);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNextHistoryFromServer(true);
        mCurrentOpenChatRoom = currentChatRoom;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel((currentChatRoom.getId().hashCode() << 4) + CardManager.CARD_CHAT);
        }

        // Start message polling
        HandlerThread handlerThread = new HandlerThread("PollingMessagesThread");
        handlerThread.start();

        pollingHandler = new Handler(handlerThread.getLooper());
        pollingHandler.postDelayed(this::pollNewMessages, POLLING_INTERVAL);

        IntentFilter filter = new IntentFilter(Const.CHAT_BROADCAST_NAME);
        LocalBroadcastManager.getInstance(this)
                             .registerReceiver(receiver, filter);
    }

    private void pollNewMessages() {
        Utils.log("Poll new messages");

        Observable<List<ChatMessageItem>> observable = chatMessageViewModel.getNewMessages(currentChatRoom);
        disposables.add(observable.subscribe(this::processNewMessages, Utils::log));
    }

    private void processNewMessages(List<ChatMessageItem> newMessages) {
        // poll messages every 30 seconds
        pollingHandler.postDelayed(this::pollNewMessages, POLLING_INTERVAL);

        // No new messages available
        if (chatMessageViewModel.getNumberUnread(currentChatRoom.getId()) == 0 || chatHistoryAdapter == null) {
            return;
        }

        isLoadingMore = true;
        startAudio();
        showMessages(newMessages, true);
    }

    private void handleBroadcastReceive(Intent intent) {
        Utils.logVerbose("Message sent. Trying to parse...");

        ChatMessageItem chat = intent.getParcelableExtra(Const.CHAT_MESSAGE);
        if (chat != null) {
            handleSuccessBroadcast(chat);
        } else {
            handleFailureBroadcast();
        }
    }

    private void handleFailureBroadcast() {
        Utils.showToast(this, R.string.message_send_error);
        getNextHistoryFromServer(true);
    }

    private void handleSuccessBroadcast(ChatMessageItem chat) {
        if (!chat.getRoomId().equals(currentChatRoom.getId()) || chatHistoryAdapter == null) {
            return;
        }

        if (!chat.getMember().getId().equals(currentChatMember.getId())) {
            // This is a new message from a different user
            startAudio();
        }

        getNextHistoryFromServer(true);
    }

    private void startAudio() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am != null && am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            // Play a notification sound
            MediaPlayer mediaPlayer = MediaPlayer.create(ChatActivity.this, R.raw.message);
            mediaPlayer.start();
        } else if (am != null && am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            // Possibly only vibration is enabled
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrate(vibrator);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void vibrate(@NonNull Vibrator vibrator) {
        if (SDK_INT >= VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, 128));
        } else {
            vibrator.vibrate(500);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatMessageViewModel.markAsRead(currentChatRoom.getId());
        mCurrentOpenChatRoom = null;
        pollingHandler.removeCallbacksAndMessages(null);
        LocalBroadcastManager.getInstance(this)
                             .unregisterReceiver(receiver);
    }

    /**
     * User pressed on the notification and wants to view the room with the new messages
     *
     * @param intent Intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Try to get the room from the extras
        AbstractChatRoom room = null;
        if (intent.getExtras() != null) {
            String value = intent.getExtras()
                                 .getString(Const.CURRENT_CHAT_ROOM);
            room = new Gson().fromJson(value, AbstractChatRoom.class);
        }

        // Check, maybe it wasn't there
        if (room != null && !room.getId().equals(currentChatRoom.getId())) {
            // If currently in a room which does not match the one from the notification --> Switch
            currentChatRoom = room;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(currentChatRoom.getTitle());
            }
            chatHistoryAdapter = null;
            getNextHistoryFromServer(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_activity_chat, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItemAddMember = menu.findItem(R.id.action_add_chat_member);
        MenuItem menuItemLeaveRoom = menu.findItem(R.id.action_leave_chat_room);

        menuItemAddMember.setVisible(ConfigUtils.getConfig(ConfigConst.CHAT_ROOM_MEMBER_ADDABLE, true));
        menuItemLeaveRoom.setVisible(ConfigUtils.getConfig(ConfigConst.CHAT_ROOM_LEAVEABLE, true));

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_chat_member:
                openAddChatMemberActivity();
                return true;
            case R.id.action_leave_chat_room:
                showLeaveChatRoomDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openAddChatMemberActivity() {
        Intent intent = new Intent(this, AddChatMemberActivity.class);
        intent.putExtra(Const.CURRENT_CHAT_ROOM, new Gson().toJson(currentChatRoom));
        startActivity(intent);
    }

    private void showLeaveChatRoomDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.leave_chat_room)
                .setMessage(getResources().getString(R.string.leave_chat_room_body))
                .setPositiveButton(R.string.leave, (dialogInterface, i) -> leaveChatRoom())
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow()
                  .setBackgroundDrawableResource(R.drawable.rounded_corners_background);
        }

        dialog.show();
    }

    private void leaveChatRoom() {
        AbstractChatRoom leaveRoom = currentChatRoom;

        Disposable leaveRoomDisposable =
                Completable.fromAction(() -> ((ChatAPI) ConfigUtils.getApiClient(this, Component.CHAT)).leaveChatRoom(leaveRoom))
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(() -> {
                          new ChatRoomController(ChatActivity.this).leave(leaveRoom);

                          Intent intent = new Intent(ChatActivity.this, ChatRoomsActivity.class);
                          startActivity(intent);
                          finish();
                      }, t -> {
                         Utils.log(t, "Failure leaving chat room");
                         Utils.showToast(ChatActivity.this, R.string.error_something_wrong);
                      });

        disposables.add(leaveRoomDisposable);
    }

    private void sendMessage(String text) {
        if (currentChatMember == null) {
            Utils.showToast(this, R.string.message_send_error);
            return;
        }

        ChatMessageItem message = new ChatMessageItem(text, currentChatMember);
        message.setRoomId(currentChatRoom.getId());
        message.setSendingStatus(ChatMessageItem.STATUS_SENDING);
        chatHistoryAdapter.add(message);
        chatMessageViewModel.addToUnsent(message);

        WorkManager.getInstance()
                   .enqueue(SendChatMessageWorker.getWorkRequest());
    }

    @Override
    public void onRetrySending(ChatMessageItem message) {
        //chatMessageViewModel.removeUnsent(message);
        message.setSendingStatus(ChatMessageItem.STATUS_SENDING);
        sendMessage(message.getText());

        List<ChatMessageItem> messages = chatMessageViewModel.getAll(currentChatRoom.getId());
        chatHistoryAdapter.updateHistory(messages);
    }

    /**
     * Sets UI elements listeners
     */
    private void bindUIElements() {
        messagesListView = findViewById(R.id.lvMessageHistory);
        messagesListView.setOnScrollListener(this);

        chatHistoryAdapter = new ChatHistoryAdapter(this, currentChatMember);
        messagesListView.setAdapter(chatHistoryAdapter);

        // Add the button for loading more messages to list header
        progressbar = new ProgressBar(this);
        messagesListView.addHeaderView(progressbar);
        messagesListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        messageEditText = findViewById(R.id.etMessage);

        ImageButton sendButton = findViewById(R.id.btnSend);
        sendButton.setOnClickListener(view -> {
            if (messageEditText.getText().toString().isEmpty()) {
                return;
            }

            sendMessage(messageEditText.getText().toString());
            messageEditText.getText().clear();
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        Utils.log("Scroll State changed:" + scrollState);
        // Noop
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // If the top item is visible and not loading more already, then load more
        if (firstVisibleItem == 0 && !isLoadingMore && chatHistoryAdapter != null) {
            getNextHistoryFromServer(false);
        }
    }

    /**
     * Loads older chat messages from the server and sets the adapter accordingly
     */
    private void getNextHistoryFromServer(boolean hasNewMessage) {
        isLoadingMore = true;

        Observable<List<ChatMessageItem>> observable;

        if (hasNewMessage || chatHistoryAdapter.isEmpty()) {
            observable = chatMessageViewModel.getNewMessages(currentChatRoom);
        } else {
            ChatMessageItem latestMessage = chatHistoryAdapter.getItem(0);
            observable = chatMessageViewModel.getOlderMessages(currentChatRoom, latestMessage);
        }

        disposables.add(observable.subscribe(messages -> showMessages(messages, hasNewMessage), Utils::log));
    }

    private void showMessages(List<ChatMessageItem> messages, boolean hasNewMessages) {
        if (hasNewMessages) {
            List<ChatMessageItem> unsent = chatMessageViewModel.getUnsentInChatRoom(currentChatRoom);
            messages.addAll(unsent);
        }

        if (messages.isEmpty()) {
            messagesListView.removeHeaderView(progressbar);
            return;
        }

        if (hasNewMessages) {
            chatHistoryAdapter.updateHistory(messages);
        } else {
            chatHistoryAdapter.addHistory(messages);
            // Scroll to last visible message
            messagesListView.setSelection(messages.size());
        }

        // We remove the progress indicator in the header view if all messages are loaded
        if (chatHistoryAdapter.isEmpty()) {
            messagesListView.removeHeaderView(progressbar);
        } else {
            isLoadingMore = false;
        }

        chatMessageViewModel.markAsRead(currentChatRoom.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }
}
