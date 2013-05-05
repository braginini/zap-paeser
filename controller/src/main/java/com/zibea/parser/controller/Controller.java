package com.zibea.parser.controller;

import com.zibea.parser.controller.thread.*;
import com.zibea.parser.controller.thread.task.Task;
import com.zibea.parser.dataaccess.RealtyDao;
import com.zibea.parser.model.domain.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author: Mikhail Bragin
 */
public class Controller {

    private ExecutorService searchPagePool;
    private ExecutorService parsePagePool;
    private ExecutorService offerParsePool;
    private ExecutorService archivePool;

    private static final String searchPageThreadPrefix = "search-page-thread";
    private static final String parsePageThreadPrefix = "page-parse-thread";
    private static final String offerParseThreadPrefix = "offer-parse-thread";
    private static final String archiveThreadPrefix = "archive-thread";

    private BlockingQueue<Task> searchPageTasksQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Task> parsePageTasksQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Task> offerParseTasksQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<Offer> archiveTasksQueue = new LinkedBlockingQueue<>();

    private HashSet<Long> savedOffers = new HashSet<>();

    private static final int searchPagePoolSize = 2;
    private static final int parsePagePoolSize = 2;
    private static final int offerParsePoolSize = 5;
    private static final int archivePoolSize = 1;

    private Queue<Proxy> proxies;
    private Set<Long> savedIds;

    private RealtyDao dao;

    private static final String rootUrl = "http://www.zap.com.br/imoveis/";

    public Controller() throws Exception {
        this.searchPagePool = Executors.newFixedThreadPool(searchPagePoolSize, new CustomThreadFactory(searchPageThreadPrefix));
        this.parsePagePool = Executors.newFixedThreadPool(parsePagePoolSize, new CustomThreadFactory(parsePageThreadPrefix));
        this.offerParsePool = Executors.newFixedThreadPool(offerParsePoolSize, new CustomThreadFactory(offerParseThreadPrefix));
        this.archivePool = Executors.newFixedThreadPool(archivePoolSize, new CustomThreadFactory(archiveThreadPrefix));
        this.dao = new RealtyDao();
        this.dao.createDataSource();
        this.proxies = prepareProxies();
        this.savedIds = dao.getAllSavedOfferIds();
    }

    public void start() throws Exception {

        //TODO LOAD IDS FROM DB AND PUT EM TO MAP, ADD this map to parse page thread


        runThreads();
        prepareTasks();

    }

    private void runThreads() {
        for (int i = 0; i < searchPagePoolSize; i++) {
            this.searchPagePool.execute(new PageSearchThread(searchPageTasksQueue, parsePageTasksQueue, proxies));
        }

        for (int i = 0; i < parsePagePoolSize; i++) {
            this.parsePagePool.execute(new PageParseThread(parsePageTasksQueue, offerParseTasksQueue, proxies, savedIds));
        }

        for (int i = 0; i < offerParsePoolSize; i++) {
            this.offerParsePool.execute(new OfferParseThread(offerParseTasksQueue, archiveTasksQueue, proxies));
        }

        for (int i = 0; i < archivePoolSize; i++) {
            this.archivePool.execute(new ArchiveOfferThread(archiveTasksQueue, dao, savedOffers, proxies));
        }
    }

    private void prepareTasks() {
        List<State> states = dao.getAllStates();

        if (states != null && !states.isEmpty()) {
            for (State state : states) {
                List<City> cities = dao.getCityByState(state);
                for (City city : cities) {
                    for (Apartment apartment : dao.getAllApartments()) {
                        for (Transaction transaction : dao.getAllTransactions()) {
                            for (District district : dao.getDistrictsByCity(city)) {

                                try {
                                    if (searchPageTasksQueue.size() > 100)
                                        Thread.sleep(10000);
                                    searchPageTasksQueue.put(new Task(state, city, apartment, transaction, district,
                                            constructPageSearchUrl(state, city, apartment, transaction)));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private String constructPageSearchUrl(State state, City city, Apartment apartment, Transaction transaction) {
        return rootUrl
                + state.getUrlParam()
                + "+" + city.getUrlParam()
                + "/" + apartment.getUrlParam()
                + "/" + transaction.getUrlParam() +
                "/";
    }

    public Queue<Proxy> prepareProxies() throws IOException {
        Queue<Proxy> proxies = new ConcurrentLinkedQueue();

        BufferedReader br = new BufferedReader(new FileReader("C:\\Proxies\\reliable_list.txt"));

        try {
            String line = br.readLine();
            String[] hostAndPort = line.split(":");
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
            proxies.offer(proxy);

            while (line != null) {
                line = br.readLine();
                if (line != null) {
                    hostAndPort = line.split(":");
                    proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
                    proxies.offer(proxy);
                }
            }
        } finally {
            br.close();
        }
        return proxies;
    }
}
