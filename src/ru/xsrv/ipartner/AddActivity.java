package ru.xsrv.ipartner;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import ru.xsrv.ipartner.interfaces.ICommand;
import ru.xsrv.ipartner.model.Controller;
import ru.xsrv.ipartner.server.v1.requests.Request;

/**
 *
 * Created by calc on 17.09.2015.
 */
public class AddActivity extends Activity {
    private Button addButton;
    private Button cancelButton;
    private EditText editText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //TODO лапшакод
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        addButton = (Button)findViewById(R.id.button2);
        cancelButton = (Button)findViewById(R.id.button3);
        editText = (EditText)findViewById(R.id.editText);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO add
                String body = editText.getText().toString();
                //TODO проверка на заполненое поле
                Request r = Controller.getInstance().addEntryTask(body);
                r.setUserPost(new ICommand() {
                    @Override
                    public void execute() {
                        //TODO добавить бы проверку на успешность :\
                        AddActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // а еще бы одновить наш список
                                Request r2 = Controller.getInstance().getEntriesTask();
                                r2.setUserPost(new ICommand() {
                                    @Override
                                    public void execute() {
                                        onBackPressed();
                                    }
                                });
                                r2.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                            }
                        });
                    }
                });

                r.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}