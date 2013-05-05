package com.zibea.parser.controller.thread;

import com.zibea.parser.controller.thread.task.Task;
import com.zibea.parser.controller.utils.ZapOfferListingParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * @author: Mikhail Bragin
 */
public class PageParseThread extends ParseThread<Task, Task> {

    private Set<Long> savedIds;

    public PageParseThread(BlockingQueue<Task> tasks, BlockingQueue<Task> childTasks, Queue<Proxy> proxies, Set<Long> savedIds) {
        super(tasks, childTasks, proxies);
        this.savedIds = savedIds;
    }

    @Override
    public void run() {

        while (blinker == this) {
            try {

                if (this.tasks.isEmpty())
                    Thread.sleep(1000);

                this.currentTask = this.tasks.take();

                if (this.currentTask != null) {
                    createSubTasks();
                }

                if (childTasks.size() > 50)
                    Thread.sleep(1000);
            } catch (InterruptedException e) {
                this.stopIt();
                e.printStackTrace();
            }
        }

    }

    //TODO ADD CHECKS FRO DATE AND ID!!! of offers!! (sub tasks))
    public void createSubTasks() throws InterruptedException {
        int attempt = 0;
        boolean parsed = false;
        while (!parsed) {
            try {
                attempt++;

                //testUrl(this.currentTask.getUrl()); //already tested before in another thread
                Document doc = Jsoup.connect(currentTask.getUrl()).get(); //parseWithProxy();//
                ZapOfferListingParser parser = new ZapOfferListingParser(doc, currentTask, savedIds);
                List<Task> preparedTasks = parser.parse();

                for (Task task : preparedTasks)
                    childTasks.put(task);
                parsed = true;
            } catch (IOException e) {
                if (attempt > 5) {
                    if (!proxies.isEmpty())
                        this.proxy = proxies.poll();
                    this.tasks.put(currentTask);
                    e.printStackTrace();
                    return;
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
