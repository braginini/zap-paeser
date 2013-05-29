package com.zibea.parser.core.workers;

import com.zibea.parser.core.exception.PageNotFoundException;
import com.zibea.parser.core.task.Task;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author: Mikhail Bragin
 */
public abstract class ParseWorker implements Runnable {

    protected Task task;

    protected LinkedBlockingQueue<Task> tasks;

    protected Object blinker;

    private static final int attemptNumber = 3;

    protected ParseWorker(LinkedBlockingQueue<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {

        blinker = this;

        nextTask:
        while (blinker != null) {

            try {
                this.task = tasks.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                stopIt();
                return;
            }

            int attempt = 1;

            while (attempt <= attemptNumber) {

                try {

                    if (task != null) {
                        attempt++;

                        String newLocation = testUrl(task.getUrl());

                        while (newLocation != null) {
                            task.setUrl(newLocation);
                            newLocation = testUrl(newLocation);
                        }

                        processTask();
                    }

                    continue nextTask;

                } catch (PageNotFoundException e) {
                    continue nextTask;
                } catch (SocketTimeoutException e) {

                    try {

                        if (attempt == attemptNumber) {
                            tasks.add(task); //giving up the task and adding back to queue to process later
                            continue nextTask;
                        }

                        Thread.sleep(2000);
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

    public void stopIt() {
        this.blinker = null;
    }

    public abstract void processTask() throws InterruptedException;
}
