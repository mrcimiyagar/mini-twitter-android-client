package kasper.android.custom_twitter.extras;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SearchHumanDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        if (position == 0) {
            outRect.top = (int)(30 * parent.getContext().getResources().getDisplayMetrics().density);
        }
        else {
            outRect.top = 0;
        }

        if (position == parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = (int)(4 * parent.getContext().getResources().getDisplayMetrics().density);
        }
        else {
            outRect.bottom = 0;
        }
    }
}
