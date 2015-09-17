package ru.xsrv.ipartner;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ru.xsrv.ipartner.interfaces.ICommand;
import ru.xsrv.ipartner.model.Controller;
import ru.xsrv.ipartner.model.Entry;
import ru.xsrv.ipartner.server.v1.requests.Request;
import ru.xsrv.ipartner.ui.helpers.ActivityHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    private ListView listView;
    private Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ViewActivity.current = i;
                ActivityHelper.startActivity(MainActivity.this, ViewActivity.class);
            }
        });
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityHelper.startActivity(MainActivity.this, AddActivity.class);
            }
        });

        //Controller.getInstance().testTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        tryToInit();
    }

    private void tryToRetry(){
        ActivityHelper.alertDialog(MainActivity.this, "Нет связи", "Проверьте связь и нажмите Да", "Да", "Нет",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tryToInit();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
    }

    private void tryToInit(){
        //TODO лапша код для быстроты... :)
        Request r = Controller.getInstance().createSessionTask();
        r.setUserPost(new ICommand() {
            @Override
            public void execute() {
                if(Controller.getInstance().getLastError() != null){
                    //TODO показать страшный диалог об обновлении и бла бла бла
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tryToRetry();
                        }
                    });
                    /*Toast.makeText(MainActivity.this, getResources().getString(R.string.error_get_data) + "\n" +
                            Controller.getInstance().getLastError().getMessage(), Toast.LENGTH_LONG).show();*/
                    return;
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //run after set session
                        Request r2 = Controller.getInstance().getEntriesTask();
                        r2.setUserPost(new ICommand() {
                            @Override
                            public void execute() {
                                if(Controller.getInstance().getLastError() != null) return;
                                //run after set entries
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //TODO update list and etc
                                        if(Controller.getInstance().getLastError() != null){
                                            tryToRetry();
                                        }
                                        //updateEntries(); or do it onResume
                                    }
                                });
                            }
                        });
                        r2.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

                        //fill test data
                        //Controller.getInstance().addEntryTask("body1").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        //Controller.getInstance().addEntryTask("body2").executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    }
                });
            }
        });
        r.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void updateEntries(){
        //TODO Обновить лист и т.д.
        //fill list view

        List<String> entries = new ArrayList<String>();
        for( Entry e : Controller.getInstance().getEntries()){
            //TODO заменить всю хрень на стрингбилдер
            //TODO преобразовать таймстампы
            String entry = "da=" + e.getDa() + ",";
            if(e.getDm()!=e.getDa()){
                entry += "dm=" + e.getDm() + ",";
            }
            entry += e.getBody().substring(0, e.getBody().length() >= 200 ? 200 : e.getBody().length());
            entries.add(entry);
        }

        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                entries );

        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateEntries();
    }
}
