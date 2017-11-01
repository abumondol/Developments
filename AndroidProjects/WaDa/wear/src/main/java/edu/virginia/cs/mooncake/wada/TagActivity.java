package edu.virginia.cs.mooncake.wada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import edu.virginia.cs.mooncake.wada.utils.ConstantsUtil;
import edu.virginia.cs.mooncake.wada.utils.SharedPrefUtil;

public class TagActivity extends Activity {

    private TextView tvTagSelected;
    boolean layoutInflated = false;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                tvTagSelected = (TextView) stub.findViewById(R.id.tvTagAll);
                layoutInflated = true;
                transferSharedPrefToTemp();
                refresh();
            }
        });

        context = this.getApplicationContext();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (layoutInflated)
            refresh();

    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnSelectCancel) {
            this.finish();

        } else if (v.getId() == R.id.btnSelectDone) {
            transferSharedPrefFromTemp();
            this.finish();

        } else {
            int tag_type = -1;
            switch(v.getId()) {
                case R.id.btnSelectSubject:
                    tag_type = 0;
                    break;
                case R.id.btnSelectActivity:
                    tag_type = 1;
                    break;
                case R.id.btnSelectPosition:
                    tag_type = 2;
                    break;
                case R.id.btnSelectLocation:
                    tag_type = 3;
                    break;
                case R.id.btnSelectState:
                    tag_type = 4;
                    break;
                case R.id.btnSelectExtra:
                    tag_type = 5;
                    break;
                default:
                    return;
            }

            Intent i = new Intent(this.getApplicationContext(), TagSelectActivity.class);
            i.putExtra(ConstantsUtil.TAG_TYPE, tag_type);
            startActivity(i);
        }
    }

    private void transferSharedPrefToTemp() {
        String[] tag_names = ConstantsUtil.TAG_NAMES;
        String s;
        for(int i=0;i<tag_names.length;i++){
            s = SharedPrefUtil.getSharedPref(tag_names[i], context);
            SharedPrefUtil.putSharedPref(tag_names[i]+ConstantsUtil.TEMP, s, context);
        }
    }

    private void transferSharedPrefFromTemp() {
        String[] tag_names = ConstantsUtil.TAG_NAMES;
        String s;
        for(int i=0;i<tag_names.length;i++){
            s = SharedPrefUtil.getSharedPref(tag_names[i]+ConstantsUtil.TEMP, context);
            SharedPrefUtil.putSharedPref(tag_names[i], s, context);
        }
    }

    private void refresh() {
        String[] tag_names = ConstantsUtil.TAG_NAMES;
        String s=SharedPrefUtil.getSharedPref(tag_names[0]+ConstantsUtil.TEMP, context);;
        for(int i=1;i<tag_names.length;i++){
            s += " - "+SharedPrefUtil.getSharedPref(tag_names[i]+ConstantsUtil.TEMP, context);
        }

        tvTagSelected.setText(s);
    }
}
