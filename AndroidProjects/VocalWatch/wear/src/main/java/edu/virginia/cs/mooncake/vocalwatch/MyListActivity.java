package edu.virginia.cs.mooncake.vocalwatch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.virginia.cs.mooncake.vocalwatch.utils.FileUtil;
import edu.virginia.cs.mooncake.vocalwatch.utils.MC;
import edu.virginia.cs.mooncake.vocalwatch.utils.SharedPrefUtil;

public class MyListActivity extends Activity {

    private TextView tvCurrentItem;
    Button btnSelect;
    int currentIndex = 0, itemCount, listType;
    String listName;
    Context context;
    String[] itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        context = this.getApplicationContext();


        tvCurrentItem = (TextView) findViewById(R.id.tvCurrentItem);
        btnSelect = (Button) findViewById(R.id.btnListSelect);

        listType = getIntent().getIntExtra(MC.LIST_TYPE, 0);
        if (listType == 0) {
            itemList = FileUtil.getSubjectList();
            listName = "Subject";

        } else {
            itemList = getIntent().getStringArrayExtra(MC.ITEM_LIST);
            listName = "Reminder";
            btnSelect.setEnabled(false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void btnClick(View v) {
        if (v.getId() == R.id.btnListSelect) {

            if (listType == 0) {
                SharedPrefUtil.putSharedPref(MC.SUBJECT, itemList[currentIndex], context);
                this.finish();
            }

        } else if (v.getId() == R.id.btnListCancel) {
            this.finish();

        } else if (v.getId() == R.id.btnItemNext) {
            currentIndex = (currentIndex + 1) % itemCount;
            refresh();
        } else if (v.getId() == R.id.btnItemPrev) {
            currentIndex = (currentIndex - 1 + itemCount) % itemCount;
            refresh();
        }
    }


    public void refresh() {
        itemCount = itemList.length;
        String str = listName + " (" + (currentIndex + 1) + "/" + itemCount + "): ";
        if (itemList[currentIndex].length() < 50)
            tvCurrentItem.setText(str + " " + itemList[currentIndex]);
        else
            tvCurrentItem.setText(str + " " + itemList[currentIndex].substring(0, 50));
    }

}
