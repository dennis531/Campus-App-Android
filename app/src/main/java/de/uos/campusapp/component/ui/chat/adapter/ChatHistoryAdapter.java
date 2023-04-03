package de.uos.campusapp.component.ui.chat.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import de.uos.campusapp.R;
import de.uos.campusapp.component.ui.chat.model.ChatMember;
import de.uos.campusapp.component.ui.chat.model.ChatMessageItem;

public class ChatHistoryAdapter extends BaseAdapter {

    private static final int OUTGOING_MESSAGE = 0;
    private static final int INCOMING_MESSAGE = 1;

    private List<ChatMessageItem> chatHistoryList = new ArrayList<>();

    private Context mContext;
    private OnRetrySendListener mRetryListener;

    private ChatMember currentChatMember;

    public ChatHistoryAdapter(Context context, ChatMember member) {
        mContext = context;
        mRetryListener = (OnRetrySendListener) mContext;
        currentChatMember = member;
    }

    public void updateHistory(List<ChatMessageItem> newHistory) {
        chatHistoryList = newHistory;
        sortHistory();
        notifyDataSetChanged();
    }

    public void addHistory(List<ChatMessageItem> newHistory) {
        chatHistoryList.addAll(newHistory);
        sortHistory();
        notifyDataSetChanged();
    }

    private void sortHistory() {
        Collections.sort(chatHistoryList, (lhs, rhs) -> lhs.getTimestamp().compareTo(rhs.getTimestamp()));
    }

    @Override
    public int getCount() {
        return chatHistoryList.size();
    }

    @Override
    public ChatMessageItem getItem(int position) {
        return chatHistoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public boolean isEmpty() {
        return getCount() == 0;
    }

    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMember member = getItem(position).getMember();
        return currentChatMember.getId().equals(member.getId()) ? OUTGOING_MESSAGE : INCOMING_MESSAGE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean isOutgoing = getItemViewType(position) == OUTGOING_MESSAGE;
        int layout = isOutgoing ? R.layout.activity_chat_history_row_outgoing
                                : R.layout.activity_chat_history_row_incoming;

        ChatMessageItem message = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layout, parent, false);
            holder = new ViewHolder(convertView, isOutgoing);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bind(mContext, message, isOutgoing, mRetryListener);
        return convertView;
    }

    public void add(ChatMessageItem unsentMessage) {
        chatHistoryList.add(unsentMessage);
        notifyDataSetChanged();
    }

    public interface OnRetrySendListener {
        void onRetrySending(ChatMessageItem message);
    }

    // Layout of the list row
    private static class ViewHolder {

        LinearLayout containerLayout;
        TextView userTextView;
        TextView messageTextView;
        TextView timestampTextView;
        ProgressBar sendingProgressBar;
        ImageView statusImageView;

        public ViewHolder(View itemView, boolean isOutgoingMessage) {
            containerLayout = itemView.findViewById(R.id.chatMessageLayout);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timeTextView);

            if (isOutgoingMessage) {
                sendingProgressBar = itemView.findViewById(R.id.progressBar);
                statusImageView = itemView.findViewById(R.id.statusImageView);
            } else {
                userTextView = itemView.findViewById(R.id.userTextView);
            }
        }

        public void bind(Context context, ChatMessageItem message,
                         boolean isOutgoingMessage, OnRetrySendListener retryListener) {
            messageTextView.setText(message.getText());

            if (isOutgoingMessage) {
                boolean isSending = message.getSendingStatus() == ChatMessageItem.STATUS_SENDING;
                statusImageView.setVisibility(isSending ? View.GONE : View.VISIBLE);
                sendingProgressBar.setVisibility(isSending ? View.VISIBLE : View.GONE);
                updateSendingStatus(context, message);
            } else {
                userTextView.setText(message.getMember().getDisplayName());
                timestampTextView.setText(message.getFormattedTimestamp(context));
            }

            containerLayout.setOnClickListener(view -> resendIfError(context, message, retryListener));
        }

        private void resendIfError(Context context,
                                   ChatMessageItem message, OnRetrySendListener retryListener) {
            if (message.getSendingStatus() == ChatMessageItem.STATUS_ERROR) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setMessage(R.string.chat_message_try_again)
                        .setPositiveButton(R.string.retry, (dialogInterface, i) -> {
                            retryListener.onRetrySending(message);
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();

                if (dialog.getWindow() != null) {
                    dialog.getWindow()
                            .setBackgroundDrawableResource(R.drawable.rounded_corners_background);
                }

                dialog.show();
            }
        }

        private void updateSendingStatus(Context context, ChatMessageItem message) {
            boolean inProgress = message.getSendingStatus() == ChatMessageItem.STATUS_SENDING;

            statusImageView.setVisibility(inProgress ? View.GONE : View.VISIBLE);
            sendingProgressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);

            int darkTextColor = ContextCompat.getColor(context, R.color.text_secondary);

            if (inProgress) {
                timestampTextView.setTextColor(darkTextColor);
                timestampTextView.setText(message.getFormattedTimestamp(context));
                return;
            }

            boolean isError = message.getSendingStatus() == ChatMessageItem.STATUS_ERROR;

            Drawable statusIcon;
            int iconTint;

            if (isError) {
                statusIcon = ContextCompat.getDrawable(context, R.drawable.ic_error_outline);
                iconTint = ContextCompat.getColor(context, R.color.error);
            } else {
                statusIcon = ContextCompat.getDrawable(context, R.drawable.ic_check);
                iconTint = ContextCompat.getColor(context, R.color.campus_blue);
            }

            if (isError) {
                timestampTextView.setText(R.string.message_send_error);
                timestampTextView.setTextColor(iconTint);
            } else {
                int textColor = ContextCompat.getColor(context, R.color.text_secondary);
                timestampTextView.setTextColor(textColor);
                timestampTextView.setText(message.getFormattedTimestamp(context));
            }

            if (statusIcon != null) {
                statusIcon.setTint(iconTint);
                statusImageView.setImageDrawable(statusIcon);
            }
        }

    }

}
