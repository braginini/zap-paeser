package com.zibea.parser.controller.utils;

import org.jsoup.nodes.Document;

/**
 * @author: Mikhail Bragin
 */
public abstract  class HtmlDocumentParser<T>  {

    T parseResult;

    Document doc;

    public HtmlDocumentParser(Document doc) {
        this.doc = doc;
    }

    public abstract T parse();



    protected long extractOfferId(String url) {
        //id
        String[] splittedUrl = url.split("/");
        String stringId = splittedUrl[splittedUrl.length - 1];
        String[] splittedId = stringId.split("-");
        return Long.parseLong(splittedId[1]);
    }
}
