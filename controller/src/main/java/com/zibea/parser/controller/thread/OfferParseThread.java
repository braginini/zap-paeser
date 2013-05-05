package com.zibea.parser.controller.thread;

import com.zibea.parser.controller.thread.task.Task;
import com.zibea.parser.controller.utils.ZapOfferParser;
import com.zibea.parser.model.domain.Offer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.*;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * @author: Mikhail Bragin
 */
public class OfferParseThread extends ParseThread<Task, Offer> {

    public OfferParseThread(BlockingQueue<Task> tasks, BlockingQueue<Offer> childTasks, Queue<Proxy> proxies) {
        super(tasks, childTasks, proxies);
    }

    @Override
    public void run() {

        while (blinker == this) {
            try {

                if (this.tasks.isEmpty() || this.childTasks.size() > 20)
                    Thread.sleep(1000);

                this.currentTask = this.tasks.take();

                if (this.currentTask != null) {
                    testUrl(this.currentTask.getUrl());   //TODO add few attempts here
                    parsePage();
                }
            } catch (IOException e) {
                if (!proxies.isEmpty()) {
                    System.out.println("Bad proxy, polling another one [proxy=" + proxy + "]");
                    this.proxy = proxies.poll();
                }
            } catch (InterruptedException e) {
                this.stopIt();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void parsePage() {

        int attempt = 0;  //TODO ATTEMPT
        boolean parsed = false;
        while (!parsed) {
            try {
                attempt++;

                Document doc = Jsoup.connect(currentTask.getUrl()).timeout(20 * 1000).get(); //parseWithProxy();//);
                ZapOfferParser parser = new ZapOfferParser(doc);
                Offer offer = parser.parse();

                offer.setState(currentTask.getState());
                offer.setCity(currentTask.getCity());
                offer.setDistrict(currentTask.getDistrict());
                offer.setApartment(currentTask.getApartment());
                offer.setTransaction(currentTask.getTransaction());

                childTasks.put(offer); //put task to archiving queue
                parsed = true;
            } catch (IOException e) {
                try {
                    if (attempt > 5) {
                        //if the task is not finished put it back to queue
                        this.tasks.put(currentTask);
                        System.out.println("Giving up to get document. Put task back to queue [url="
                                + currentTask.getUrl() + "]");
                        return;
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void createSubTasks() throws IOException, InterruptedException {

    }

    public Document parseWithProxy() throws IOException {
        URL url = new URL(currentTask.getUrl());
        HttpURLConnection uc = (HttpURLConnection) url.openConnection(this.proxy);
        uc.setConnectTimeout(20 * 1000);
        uc.setReadTimeout(20 * 1000);

        uc.connect();

        String line;
        StringBuffer tmp = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        while ((line = in.readLine()) != null) {
            tmp.append(line);
        }

        return Jsoup.parse(String.valueOf(tmp));
    }
}
