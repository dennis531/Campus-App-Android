package de.uos.campusapp.component.ui.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import de.uos.campusapp.component.ui.chat.model.ChatMember;

public class MemberSuggestionsListAdapter extends BaseAdapter implements Filterable {

    private List<ChatMember> members;
    private Context mContext;

    // constructor
    public MemberSuggestionsListAdapter(Context context, List<ChatMember> members) {
        this.members = members;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext)
                                 .inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        ((TextView) view.findViewById(android.R.id.text1)).setText(members.get(position)
                                                                          .getDisplayName());
        ((TextView) view.findViewById(android.R.id.text2)).setText(members.get(position)
                                                                          .getUsername());
        return view;
    }

    @Override
    public ChatMember getItem(int position) {
        if (members != null) {
            return members.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        if (members == null || members.size() < i) {
            return -1;
        }
        return i;
    }

    @Override
    public int getCount() {
        if (members == null) {
            return 0;
        }
        return members.size();
    }

    public void updateSuggestions(List<ChatMember> members) {
        this.members = members;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                // We don't filter the members as the members should have been filtered by the api
                FilterResults results = new FilterResults();
                results.values = members;
                results.count = members.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                // We don't change the data
            }
        };
    }
}
