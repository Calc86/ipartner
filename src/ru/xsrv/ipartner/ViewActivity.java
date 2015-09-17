package ru.xsrv.ipartner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import ru.xsrv.ipartner.model.Controller;
import ru.xsrv.ipartner.model.Entry;

/**
 *
 * Created by calc on 17.09.2015.
 */
public class ViewActivity extends Activity {
    public static int current = 0;

    private TextView text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);

        text = (TextView)findViewById(R.id.textView);

        fillView();
    }

    private void fillView(){
        if(Controller.getInstance().getEntries() == null) return;
        if(Controller.getInstance().getEntries().size() < current) return;
        Entry e = Controller.getInstance().getEntries().get(current);
        //TODO заменить на стрингбилдер
        String t = "id=" + e.getId() + ", da=" + e.getDa() + ", dm=" + e.getDm() + ", body: " + e.getBody();
        text.setText(t);
    }
}
