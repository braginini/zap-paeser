package com.zibea.parser.core.workers;

import com.zibea.parser.core.exception.PageNotFoundException;
import com.zibea.parser.core.task.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: Mikhail Bragin
 */
public abstract class NewParseWorker implements Runnable {

    protected LinkedBlockingQueue<Task> taskQueue;

    protected Task task;

    private Object blinker;

    protected NewParseWorker(LinkedBlockingQueue<Task> taskQueue) {
        this.taskQueue = taskQueue;
        this.blinker = this;
    }

    @Override
    public void run() {

        while (blinker != null) {

            task = taskQueue.poll();

            int attempt = 0;

            while (attempt < 5) {
                try {

                    if (task != null) {
                        attempt++;

                        String newLocation = testUrl(task.getUrl());
                        if (newLocation != null) {
                            testUrl(newLocation);
                            task.setUrl(newLocation);
                        }
                        processTask();
                        break;
                    }
                } catch (IOException e) {

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e.printStackTrace();
                        stopIt();
                    }

                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String testUrl(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(20 * 1000);
        connection.setReadTimeout(20 * 1000);
        connection.setRequestMethod("HEAD");
        connection.setInstanceFollowRedirects(false); //do not follow redirect
        int returnCode = connection.getResponseCode();
        String location = connection.getHeaderField("Location");
        connection.disconnect();
        if (returnCode == 302) throw new PageNotFoundException("No such page");
        if (returnCode == 301 && location != null && !location.isEmpty()) return location;
        if (returnCode != 200) throw new IOException("Connection error url=" + url);

        return null;
    }

    protected void stopIt() {
        this.blinker = null;
    }

    public abstract void processTask() throws InterruptedException;
}
