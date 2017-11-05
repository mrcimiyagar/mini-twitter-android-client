package kasper.android.custom_twitter.adapters;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.activities.ProfileActivity;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.models.database.MyData;
import kasper.android.custom_twitter.models.memory.Human;
import kasper.android.custom_twitter.models.packets.RequestAcceptFollowRequest;
import kasper.android.custom_twitter.models.packets.RequestIgnoreFollowRequest;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class FollowRequestAdapter extends RecyclerView.Adapter<FollowRequestAdapter.RequestItem> {

    private AppCompatActivity activity;
    private ArrayList<Human> followRequests;

    public FollowRequestAdapter(AppCompatActivity activity, ArrayList<Human> followRequests) {
        this.activity = activity;
        this.followRequests = followRequests;
        this.notifyDataSetChanged();
    }

    @Override
    public RequestItem onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RequestItem(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_follow_request, parent, false));
    }

    @Override
    public void onBindViewHolder(final RequestItem holder, int position) {

        final Human human = followRequests.get(position);
        holder.userTitleTV.setText(human.getUserTitle());
        holder.accentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestAcceptFollowRequest requestAcceptFollowRequest = new RequestAcceptFollowRequest();
                requestAcceptFollowRequest.targetRequestId = human.getHumanId();

                MyApp.getInstance().getNetworkHelper().pushTCP(requestAcceptFollowRequest, new OnRequestAnsweredListener() {
                    @Override
                    public void onRequestAnswered(BaseAnswer rawAnswer) {

                        Realm realm = Realm.getDefaultInstance();

                        realm.beginTransaction();

                        kasper.android.custom_twitter.models.database.Human dHuman = new kasper.android.custom_twitter.models.database.Human();
                        dHuman.setHumanId(human.getHumanId());

                        realm.where(MyData.class).findFirst().getHuman().getFollowers().add(dHuman);

                        realm.commitTransaction();

                        realm.close();

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                followRequests.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        });
                    }
                });
            }
        });
        holder.ignoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestIgnoreFollowRequest requestIgnoreFollowRequest = new RequestIgnoreFollowRequest();
                requestIgnoreFollowRequest.targetRequestId = human.getHumanId();

                MyApp.getInstance().getNetworkHelper().pushTCP(requestIgnoreFollowRequest, new OnRequestAnsweredListener() {
                    @Override
                    public void onRequestAnswered(BaseAnswer rawAnswer) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                followRequests.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        });
                    }
                });
            }
        });
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
        return this.followRequests.size();
    }

    class RequestItem extends RecyclerView.ViewHolder {

        TextView userTitleTV;
        Button accentBtn;
        Button ignoreBtn;

        RequestItem(View itemView) {
            super(itemView);
            this.userTitleTV = itemView.findViewById(R.id.adapter_follow_request_user_title_text_view);
            this.accentBtn = itemView.findViewById(R.id.adapter_follow_request_accept_button);
            this.ignoreBtn = itemView.findViewById(R.id.adapter_follow_request_ignore_button);
        }
    }
}