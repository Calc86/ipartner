package ru.xsrv.ipartner.model;

import android.content.Context;
import android.util.Log;
import ru.xsrv.ipartner.IpartnerApplication;
import ru.xsrv.ipartner.R;
import ru.xsrv.ipartner.interfaces.ICommand;
import ru.xsrv.ipartner.model.cache.TypedCache;
import ru.xsrv.ipartner.server.exceptions.ServerException;
import ru.xsrv.ipartner.server.v1.Devices;
import ru.xsrv.ipartner.server.v1.requests.*;
import ru.xsrv.ipartner.server.v1.responses.*;
import ru.xsrv.ipartner.ui.helpers.ResourceMap;

import java.util.ArrayList;
import java.util.List;

//import com.google.gson.reflect.TypeToken;

/**
 *
 * Created by Calc on 05.11.2014.
 */
public class Controller {
    private final static String TAG = Controller.class.toString();
    private final Cache cache = new Cache();

    //private User user = cache.get(CacheType.USER, User.class);

    //private Order currentOrder = cache.get(CacheType.BASKET, Order.class);
    //private Basket basket = new Basket();
    //private List<Order> orders = new ArrayList<Order>();    //прошлые заказы
    private String deviceName = "UNKNOWN";
    private String session = "";
    private List<Entry> entries = new ArrayList<Entry>();

    private Error lastError;

    private static Controller instance = new Controller();

    private Controller() {
        try {
            deviceName = Devices.getDeviceName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getSession() {
        return session;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void cleanCache(){
        cache.clean();
    }

    public Error getLastError() {
        return lastError;
    }

    protected void setLastError(Error error){
        lastError = error;
    }

    protected boolean setLastError(Response response) {
        try {
            return setLastError(response, false);
        } catch (ServerException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean setLastError(Response response, boolean exception) throws ServerException {
        lastError = null;

        if(response == null){
            lastError = new Error(ErrorType.NETWORK);
        }
        else if(!response.isOk()){
            //lastError = new Error(ErrorType.SERVER);
            lastError = new Error(ErrorType.SERVER, response.getError());
        }

        // вернуть или выкинуть
        if(exception && lastError != null){
            throw new ServerException(response);
        }else
            return lastError != null;
    }

    /*public List<Price> getPrices() {
        //доставать из кеша
        if(prices.size() == 0){
            List<Price> p = null;
            try {
                p = (List<Price>) cache.getList(CacheType.PRICES, new TypeToken<List<Price>>(){}.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(p != null) prices = p;
        }
        Log.d(TAG, "get prices"); //NON-NLS
        return prices;
    }

    public void setPrices(List<Price> prices) {
        cache.create(CacheType.PRICES, prices);
        Log.d(TAG, "set prices"); //NON-NLS
        this.prices = prices;
    }*/

    public static enum ErrorType{
        NETWORK(R.string.controller_error_network),    //ошибка сетевого подключения
        SERVER(R.string.controller_error_server),     //ошибка взаимодействия с сервером, сервер прислал json с отказом
        EXCEPTION(R.string.controller_error_exception),  //исключение где то в коде
        //OTHER("Ошибка")   //периодически прочесывать эти типы ошибок и присваивать им типы
        ;

        private int info;

        ErrorType(int info) {
            this.info = info;
        }

        public String getInfo() {
            return IpartnerApplication.getResourceString(info);
        }
    }

    public static class Error{
        private Exception e;
        private String message;
        private ErrorType type;

        public Error(Exception e) {
            this.message = e.getMessage();
            this.type = ErrorType.EXCEPTION;
            this.e = e;
        }

        public Error(ErrorType type) {
            this.type = type;
            this.message = type.getInfo();
        }

        public Error(ErrorType type, String message) {
            this.message = message;
            this.type = type;
        }

        public Error(ErrorType type, Exception e, String message) {
            this.e = e;
            this.message = message;
            this.type = type;
        }

        public Exception getE() {
            return e;
        }

        public String getMessage() {
            String resourceName  = "err_" + message.toLowerCase().trim();

            int res = ResourceMap.get(IpartnerApplication.getAppContext(), resourceName, "string");
            if(res == 0){
                return message;
            }
            else{
                try {
                    return IpartnerApplication.getResourceString(res);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return message;
                }
            }
        }

        public ErrorType getType() {
            return type;
        }
    }

    public static Controller getInstance() {
        return instance;
    }

    private enum CacheType{
        USER, BASKET, PRICES, IMAGE_COUNT, DELIVERY, BANNERS, PREVIEW
    }

    private static class Cache extends TypedCache{
        private Cache() {
            super(Type.CONTROLLER);
        }

        public void delete(CacheType path, String name) {
            super.delete(path.toString().toLowerCase(), name);
        }

        public void create(CacheType path, String name, Object object) {
            super.create(path.toString().toLowerCase(), name, object);
        }

        public <T> T get(CacheType path, String name, Class<T> c) {
            return super.get(path.toString().toLowerCase(), name, c);
        }

        public List<?> getList(CacheType path, String name, java.lang.reflect.Type t) {
            return super.getList(path.toString().toLowerCase(), name, t);
        }

        public void delete(CacheType path) {
            super.delete(path.toString().toLowerCase());
        }

        public void create(CacheType path, Object object) {
            super.create(path.toString().toLowerCase(), object);
        }

        public <T> T get(CacheType path, Class<T> c) {
            return super.get(path.toString().toLowerCase(), c);
        }

        public List<?> getList(CacheType path, java.lang.reflect.Type t) {
            return super.getList(path.toString().toLowerCase(), t);
        }

        public void createFile(CacheType path, String name, byte[] data){
            super.createFile(path.toString().toLowerCase(), name, data);
        }

        public byte[] getFile(CacheType path, String name){
            return super.getFile(path.toString().toLowerCase(), name);
        }
    }

    /*public Request cancelOrderTask(long order_id){
        //final AuthRequest request = new AuthRequest(email, password);
        if(getUser() == null) return null;
        final OrderCancelRequest request = new OrderCancelRequest(getUser().getId(), order_id, "");

        request.setSystemPost(new ICommand() {
            @Override
            public void execute() {
                if(request.getResult() == null) return;
                String ret = new String(request.getResult());
                OrderCancelResponse response = Response.fromJson(ret, OrderCancelResponse.class);
                if(setLastError(response)) return;
            }
        });
        return request;
    }*/

    public Request testTask(){
        final TestRequest request = new TestRequest();

        request.setSystemPost(new ICommand() {
            @Override
            public void execute() {
                if(request.getResult() == null) return;
                String ret = new String(request.getResult());
                System.out.println(ret);
                /*OrderCancelResponse response = Response.fromJson(ret, OrderCancelResponse.class);
                if(setLastError(response)) return;*/
            }
        });
        return request;
    }

    public Request createSessionTask(){
        final NewSessionRequest request = new NewSessionRequest();

        request.setSystemPost(new ICommand() {
            @Override
            public void execute() {
                if(request.getResult() == null){
                    setLastError(new Error(ErrorType.NETWORK));
                    return;
                }
                String ret = new String(request.getResult());
                Log.d(TAG, ret);
                SimpleResponse response = Response.fromJson(ret, SimpleResponse.class);
                if(setLastError(response)) return;
                setSession(response.data.session);
            }
        });
        return request;
    }

    public Request addEntryTask(String body){
        final AddEntryRequest request = new AddEntryRequest(getSession(), body);

        request.setSystemPost(new ICommand() {
            @Override
            public void execute() {
                if(request.getResult() == null){
                    setLastError(new Error(ErrorType.NETWORK));
                    return;
                }
                String ret = new String(request.getResult());
                Log.d(TAG, ret);
                SimpleResponse response = Response.fromJson(ret, SimpleResponse.class);
                if(setLastError(response)) return;
                //TODO work with response data.id if no error
            }
        });
        return request;
    }

    public Request getEntriesTask(){
        final GetEntriesRequest request = new GetEntriesRequest(getSession());

        request.setSystemPost(new ICommand() {
            @Override
            public void execute() {
                if(request.getResult() == null){
                    setLastError(new Error(ErrorType.NETWORK));
                    return;
                }
                String ret = new String(request.getResult());
                Log.d(TAG, ret);
                GetEntriesResponse response = Response.fromJson(ret, GetEntriesResponse.class);
                if(setLastError(response)) return;
                //TODO work with response data.id if no error
                setEntries(response.getData());
            }
        });
        return request;
    }

    private Context getContext(){
        return IpartnerApplication.getAppContext();
    }

}
