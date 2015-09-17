package ru.xsrv.ipartner.server.v1.responses;

import ru.xsrv.ipartner.model.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by calc on 17.09.2015.
 */
public class GetEntriesResponse extends Response {
    /*
    "data":[
	[
		{"id": "4klJeiCKTs", "body": "Вторая запись", "da": "1442236233", "dm": "1442236233"},
		{"id": "2rRwFT9HOk", "body": "Первая запись", "da": "1442236206", "dm": "1442236206"}
	]
	]
     */
    protected List<ArrayList<Entry>> data = new ArrayList<ArrayList<Entry>>();

    public ArrayList<Entry> getData() {
        //TODO проверить по документации на API
        if(data.size() == 0) return new ArrayList<Entry>();
        else
        return data.get(0);
    }
}
