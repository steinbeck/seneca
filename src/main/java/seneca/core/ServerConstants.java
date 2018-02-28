package seneca.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: kalai
 * Date: 04/03/2013
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class ServerConstants {

    private ExecutorService executor = null;

    public ServerConstants(int size) {
        this.executor = Executors.newFixedThreadPool(size);
    }

    public synchronized void initiateExecutor(int size) {
        this.executor = Executors.newFixedThreadPool(size);
    }

    public synchronized ExecutorService getExecutor() {
        return this.executor;
    }
}
