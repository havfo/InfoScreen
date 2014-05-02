package net.fosstveit.infoscreen;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;
import net.fosstveit.infoscreen.fragments.InfoScreenFragment;

public class InfoScreenActivity extends FragmentActivity {

    private InfoScreenFragment mainFragment1;

    private InfoScreenFragment mainFragment2;

    private InfoScreenFragment mainFragment3;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.infoscreenactivity_layout);

        mainFragment1 = (InfoScreenFragment) getFragmentManager().findFragmentById(R.id.main_fragment1);
        mainFragment2 = (InfoScreenFragment) getFragmentManager().findFragmentById(R.id.main_fragment2);
        mainFragment3 = (InfoScreenFragment) getFragmentManager().findFragmentById(R.id.main_fragment3);
    }
}
