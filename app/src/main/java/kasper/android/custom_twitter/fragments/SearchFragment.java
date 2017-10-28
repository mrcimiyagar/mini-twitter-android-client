package kasper.android.custom_twitter.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import java.util.ArrayList;

import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.adapters.SearchHumanAdapter;
import kasper.android.custom_twitter.callbacks.OnRequestAnsweredListener;
import kasper.android.custom_twitter.core.MyApp;
import kasper.android.custom_twitter.extras.SearchHumanDecoration;
import kasper.android.custom_twitter.models.memory.Human;
import kasper.android.custom_twitter.models.packets.AnswerSearchUserTitle;
import kasper.android.custom_twitter.models.packets.RequestSearchUserTitle;
import kasper.android.custom_twitter.models.packets.base.BaseAnswer;

public class SearchFragment extends Fragment {

    AppCompatEditText searchET;
    RecyclerView itemsRV;
    FloatingActionButton searchBtn;

    ArrayList<Human> searchResults;

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_search, container, false);

        if (searchResults == null) {
            searchResults = new ArrayList<>();
        }

        this.searchET = contentView.findViewById(R.id.fragment_search_edit_text);
        this.itemsRV = contentView.findViewById(R.id.search_results_recycler_view);
        this.searchBtn = contentView.findViewById(R.id.fragment_search_search_button);

        this.itemsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        this.itemsRV.addItemDecoration(new SearchHumanDecoration());
        this.itemsRV.setAdapter(new SearchHumanAdapter(searchResults));

        this.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (searchET.getText().toString().length() > 0) {

                    RequestSearchUserTitle requestSearchUserTitle = new RequestSearchUserTitle();
                    requestSearchUserTitle.query = searchET.getText().toString();

                    MyApp.getInstance().getNetworkHelper().pushTCP(requestSearchUserTitle, new OnRequestAnsweredListener() {
                        @Override
                        public void onRequestAnswered(BaseAnswer rawAnswer) {

                            final AnswerSearchUserTitle answerSearchUserTitle = (AnswerSearchUserTitle) rawAnswer;

                            searchResults = answerSearchUserTitle.humans;

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    itemsRV.setAdapter(new SearchHumanAdapter(searchResults));
                                }
                            });
                        }
                    });
                }
                else {

                    Toast.makeText(getActivity(), "عنوان جستجو نمی تواند خالی باشد", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return contentView;
    }
}