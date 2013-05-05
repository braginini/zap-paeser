package com.zibea.parser.controller.thread;

import com.zibea.parser.controller.exception.PageNotFoundException;
import com.zibea.parser.controller.thread.task.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * @author: Mikhail Bragin
 */
public abstract class ParseThread<T, E> extends Thread {

    protected BlockingQueue<T> tasks;

    protected BlockingQueue<E> childTasks;

    protected T currentTask;

    protected Thread blinker;

    protected Queue<Proxy> proxies;

    protected Proxy proxy;

    protected ParseThread(BlockingQueue<T> tasks, BlockingQueue<E> childTasks, Queue<Proxy> proxies) {
        this.proxies = proxies;
        this.blinker = this;
        this.tasks = tasks;
        this.childTasks = childTasks;
        this.proxy = proxies.poll();
    }

    public void testUrl(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(this.proxy);
        connection.setConnectTimeout(20 * 1000);
        connection.setReadTimeout(20 * 1000);
        connection.setRequestMethod("HEAD");
        connection.setInstanceFollowRedirects(false); //do not follow redirect
        int returnCode = connection.getResponseCode();
        connection.disconnect();
        if (returnCode == 302) throw new PageNotFoundException("No such page");
        if (returnCode != 200) throw new IOException("Connection error");
    }

    public void stopIt() {
        this.blinker = null;
    }

    public abstract void createSubTasks() throws IOException, InterruptedException;

    public abstract Document parseWithProxy() throws IOException;

    protected long extractOfferId(String url) {
        //id
        String[] splittedUrl = url.split("/");
        String stringId = splittedUrl[splittedUrl.length - 1];
        String[] splittedId = stringId.split("-");
        return Long.parseLong(splittedId[1]);
    }

}
