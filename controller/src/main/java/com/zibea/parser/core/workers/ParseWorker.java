package com.zibea.parser.core.workers;

import com.zibea.parser.core.exception.PageNotFoundException;
import com.zibea.parser.core.task.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author: Mikhail Bragin
 */
public abstract class ParseWorker implements Runnable {

    protected Task task;

    protected ParseWorker(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        int attempt = 0;

        while (attempt < 5) {
            try {

                if (task != null) {
                    attempt++;
                    testUrl(task.getUrl());   //TODO add few attempts here
                }
            } catch (IOException e) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e.printStackTrace();
                    return;
                }
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            processTask();
        } catch (InterruptedException e) {
            return;
        }
    }

    public void testUrl(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(20 * 1000);
        connection.setReadTimeout(20 * 1000);
        connection.setRequestMethod("HEAD");
        connection.setInstanceFollowRedirects(false); //do not follow redirect
        int returnCode = connection.getResponseCode();
        connection.disconnect();
        if (returnCode == 302) throw new PageNotFoundException("No such page");
        if (returnCode != 200) throw new IOException("Connection error " + returnCode + " url=" + url);
    }

    public abstract void processTask() throws InterruptedException;
}
