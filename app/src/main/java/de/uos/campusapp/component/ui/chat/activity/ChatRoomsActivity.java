package de.uos.campusapp.component.ui.chat.activity;

import android.os.Bundle;

import de.uos.campusapp.R;
import de.uos.campusapp.component.other.generic.activity.BaseActivity;
import de.uos.campusapp.component.ui.chat.ChatRoomsFragment;
import de.uos.campusapp.utils.Component;

public class ChatRoomsActivity extends BaseActivity {

    public ChatRoomsActivity() {
        super(R.layout.activity_chat_rooms, Component.CHAT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentFrame, ChatRoomsFragment.newInstance())
                    .commit();
        }
    }

}
