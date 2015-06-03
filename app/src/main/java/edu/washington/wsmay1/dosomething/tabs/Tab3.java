package edu.washington.wsmay1.dosomething.tabs;

/**
 * Created by Slee on 6/2/2015.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.washington.wsmay1.dosomething.R;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab3 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_3,container,false);
        return v;
    }
}
