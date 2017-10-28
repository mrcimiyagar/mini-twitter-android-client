package kasper.android.custom_twitter.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.activities.ProfileActivity;
import kasper.android.custom_twitter.models.memory.Human;

public class SearchHumanAdapter extends RecyclerView.Adapter<SearchHumanAdapter.SearchItem> {

    private ArrayList<Human> searchResults;

    public SearchHumanAdapter(ArrayList<Human> searchResults) {
        this.searchResults = searchResults;
        this.notifyDataSetChanged();
    }

    @Override
    public SearchItem onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search_human, parent, false));
    }

    @Override
    public void onBindViewHolder(final SearchItem holder, int position) {
        final Human human = searchResults.get(position);
        holder.userTitleTV.setText(human.getUserTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext()
                        , ProfileActivity.class).putExtra("human-id", human.getHumanId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.searchResults.size();
    }

    class SearchItem extends RecyclerView.ViewHolder {

        TextView userTitleTV;

        SearchItem(View itemView) {
            super(itemView);
            this.userTitleTV = itemView.findViewById(R.id.adapter_search_human_user_title_text_view);
        }
    }
}