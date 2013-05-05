package com.zibea.parser.controller;

import com.zibea.parser.dataaccess.RealtyDao;
import com.zibea.parser.model.domain.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Mikhail Bragin
 */
public class GeoLocationsMain {

    private static final String rootUrl = "http://www.zap.com.br/imoveis/brasil/";

    private static RealtyDao dao;

    public static void main(String[] args) throws Exception {
        dao = new RealtyDao();
        //populateDistricts();
    }

    private static void populateDistricts() throws IOException {
        List<RealtyObject> result;
        List<State> states = dao.getAllStates();
        //get all districts for all cities
        long id = 0l;
        for (State state : states) {
            List<City> stateCities = dao.getCityByState(state);
            for (City city : stateCities) {
                result = new ArrayList<>();
                Document doc = Jsoup.connect("http://www.zap.com.br/imoveis/"
                        + state.getUrlParam() + "+"
                        + city.getUrlParam() + "/")
                        .get();
                Elements elements = doc.getElementsByClass("filtro");
                for (Element element : elements) {
                    Elements filterTitleEls = element.getElementsByTag("h2");
                    if (filterTitleEls != null && !filterTitleEls.isEmpty()) {
                        if (filterTitleEls.get(0).text().toLowerCase().equals("bairro:")) {
                            filterTitleEls = element.getElementsByTag("a");
                            for (Element districtEl : filterTitleEls) {
                                if (!districtEl.textNodes().isEmpty()) {
                                    String url = districtEl.attr("href");
                                    String name = districtEl.textNodes().get(0).text().toLowerCase();
                                    if (!name.toLowerCase().equals("mostrar todos")) {
                                        String urlParam = url.split("/")[4].split("\\+")[2].toLowerCase();
                                        RealtyObject apartment = new District(id, name, urlParam, city);
                                        result.add(apartment);
                                        id++;
                                    }
                                }
                            }
                        }
                    }
                }
                dao.saveBatch(result);
            }
        }
    }

    private static void populateCities() throws IOException {
        List<RealtyObject> result;
        List<State> states = dao.getAllStates();
        //get all districts for all cities
        long id = 0l;
        for (State state : states) {
            result = new ArrayList<>();
            Document doc = Jsoup.connect("http://www.zap.com.br/imoveis/" + state.getUrlParam() + "/").get();
            Elements elements = doc.getElementsByClass("filtro");
            for (Element element : elements) {
                Elements filterTitleEls = element.getElementsByTag("h2");
                if (filterTitleEls != null && !filterTitleEls.isEmpty()) {
                    if (filterTitleEls.get(0).text().toLowerCase().equals("cidade:")) {
                        filterTitleEls = element.getElementsByTag("a");
                        for (Element districtEl : filterTitleEls) {
                            if (!districtEl.textNodes().isEmpty()) {
                                String url = districtEl.attr("href");
                                String name = districtEl.textNodes().get(0).text().toLowerCase();
                                if (!name.toLowerCase().equals("mostrar todos")) {
                                    String urlParam = url.split("/")[4].split("\\+")[1].toLowerCase();
                                    RealtyObject apartment = new City(id, name, urlParam, state);
                                    result.add(apartment);
                                    id++;
                                }
                            }
                        }
                    }
                }
            }
            dao.saveBatch(result);
        }
    }

    private static void populateStates() throws IOException {
        //get all districts for all cities
        List<RealtyObject> result = new ArrayList<>();
        long id = 0l;
        Document doc = Jsoup.connect(rootUrl).get();
        Elements elements = doc.getElementsByClass("filtro");
        for (Element element : elements) {
            Elements filterTitleEls = element.getElementsByTag("h2");
            if (filterTitleEls != null && !filterTitleEls.isEmpty()) {
                if (filterTitleEls.get(0).text().toLowerCase().equals("estado:")) {
                    filterTitleEls = element.getElementsByTag("a");
                    for (Element districtEl : filterTitleEls) {
                        if (!districtEl.textNodes().isEmpty()) {
                            String url = districtEl.attr("href");
                            String name = districtEl.textNodes().get(0).text().toLowerCase();
                            if (!name.toLowerCase().equals("mostrar todos")) {
                                String urlParam = url.split("/")[4].toLowerCase();
                                RealtyObject apartment = new State(id, name, urlParam);
                                result.add(apartment);
                                id++;
                            }
                        }
                    }
                }
            }
        }

        dao.saveBatch(result);
    }

    private static void populateRealtyTypes() throws IOException {
        //get all districts for all cities
        List<RealtyObject> result = new ArrayList<>();
        long id = 0l;
        Document doc = Jsoup.connect(rootUrl).get();
        Elements elements = doc.getElementsByClass("filtro");
        for (Element element : elements) {
            Elements filterTitleEls = element.getElementsByTag("h2");
            if (filterTitleEls != null && !filterTitleEls.isEmpty()) {
                if (filterTitleEls.get(0).text().toLowerCase().equals("tipo:")) {
                    filterTitleEls = element.getElementsByTag("a");
                    for (Element districtEl : filterTitleEls) {
                        if (!districtEl.textNodes().isEmpty()) {
                            String url = districtEl.attr("href");
                            String name = districtEl.textNodes().get(0).text().toLowerCase();
                            if (!name.toLowerCase().equals("mostrar todos")) {
                                String urlParam = url.split("/")[5].toLowerCase();
                                RealtyObject apartment = new Apartment(id, name, urlParam);
                                result.add(apartment);
                                id++;
                            }
                        }
                    }
                }
            }
        }

        dao.saveBatch(result);
    }
}
