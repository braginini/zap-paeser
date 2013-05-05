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
public class PageSearchThread extends ParseThread<Task, Task> {

    private static final String pageParam = "?pag=";

    public PageSearchThread(BlockingQueue<Task> tasks, BlockingQueue<Task> childTasks, Queue<Proxy> proxies) {
        super(tasks, childTasks, proxies);
    }


    @Override
    public void run() {
        while (blinker == this) {

            try {

                if (this.tasks.isEmpty())
                    Thread.sleep(1000);

                this.currentTask = this.tasks.take();

                if (this.currentTask != null) {
                    testUrl(this.currentTask.getUrl());   //TODO add few attempts here
                    createSubTasks();
                }

                if (childTasks.size() > 100)
                    Thread.sleep(1000);
            } catch (IOException e) {
                if (!proxies.isEmpty())
                    this.proxy = proxies.poll();
            } catch (InterruptedException e) {
                this.stopIt();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createSubTasks() throws IOException, InterruptedException {

        for (int i = 1; i <= Short.MAX_VALUE; i++) {

            boolean pagePrepared = false;
            int attempt = 0;

            while (!pagePrepared) {

                attempt++;
                String taskUrl = currentTask.getUrl() + pageParam + i;

                try {
                    testUrl(taskUrl);

                    this.childTasks.put(new Task(currentTask.getState(),
                            currentTask.getCity(),
                            currentTask.getApartment(),
                            currentTask.getTransaction(),
                            currentTask.getDistrict(),
                            taskUrl));
                    pagePrepared = true;

                } catch (PageNotFoundException e) {
                    return;  //if it was non existing page index, finish creating search page tasks
                } catch (IOException e) {
                    if (attempt > 5) {
                        if (!proxies.isEmpty())
                            this.proxy = proxies.poll();
                        this.tasks.put(currentTask);
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
    }

    @Override
    public Document parseWithProxy() throws IOException {
        URL url = new URL(currentTask.getUrl());
        HttpURLConnection uc = (HttpURLConnection) url.openConnection(this.proxy);
        uc.setConnectTimeout(20 * 1000);
        uc.setReadTimeout(20 * 1000);

        uc.connect();

        String line = null;
        StringBuffer tmp = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        while ((line = in.readLine()) != null) {
            tmp.append(line);
        }

        return Jsoup.parse(String.valueOf(tmp));
    }
}
