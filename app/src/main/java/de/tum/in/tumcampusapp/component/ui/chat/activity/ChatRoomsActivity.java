package de.tum.in.tumcampusapp.component.ui.chat.activity;

import android.os.Bundle;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.component.other.generic.activity.BaseActivity;
import de.tum.in.tumcampusapp.component.ui.chat.ChatRoomsFragment;
import de.tum.in.tumcampusapp.utils.Component;
import de.tum.in.tumcampusapp.utils.ConfigConst;

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
