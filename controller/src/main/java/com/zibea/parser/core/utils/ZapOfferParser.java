package com.zibea.parser.core.utils;

import com.zibea.parser.model.domain.Offer;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author: Mikhail Bragin
 */
public class ZapOfferParser extends HtmlDocumentParser<Offer> {

    public ZapOfferParser(Document doc) {
        super(doc);
    }

    @Override
    public Offer parse() {
        Offer offer = new Offer();
        Elements offerMainContainerEls = doc.getElementsByClass("fc-main");
        Element offerNameContainerEl = null;
        if (offerMainContainerEls != null && !offerMainContainerEls.isEmpty())
            offerNameContainerEl = offerMainContainerEls.get(0);

        if (offerNameContainerEl != null) {

            offer.setUrl(doc.baseUri());
            //publication date
            Elements dateListedEls = offerNameContainerEl.getElementsByClass("dtlisted");

            if (dateListedEls != null && !dateListedEls.isEmpty()) {
                String rawPublicationDate = dateListedEls.get(0).text();
                String[] splittedRawPublicationDate = rawPublicationDate.split(":");
                String publicationDate = splittedRawPublicationDate[1];
                publicationDate = publicationDate.replaceAll("\\s", ""); //remove whitespaces
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                try {
                    offer.setTsPublished(format.parse(publicationDate).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            //street name
            Elements streetAddressEls = offerNameContainerEl.getElementsByClass("street-address");
            if (streetAddressEls != null && !streetAddressEls.isEmpty()) {
                String streetAddress = streetAddressEls.get(0).text();
                offer.setStreetAddress((streetAddress != null && !streetAddress.isEmpty())
                        ? streetAddress : null);
            } else {
                System.out.println("No address [ url=" + doc.baseUri() + " ]");
            }

        }

        //summary
        //id
        offer.setId(extractOfferId(doc.baseUri()));
        //price
        offer.setPrice(extractPrice());

        //service fee
        Double monthlyPayment = getDoubleValue("ctl00_ContentPlaceHolder1_detalhes_lbCondominio", 1);
        offer.setServiceFee(monthlyPayment);

        //price for square meter
        Double squareMeterPrice = getDoubleValue("ctl00_ContentPlaceHolder1_detalhes_lbValorM2", 1);
        offer.setPricePerSquareMeter(squareMeterPrice);

        //room number
        Integer roomNumber = getIntegerValue("ctl00_ContentPlaceHolder1_detalhes_lbDorms", 0);
        offer.setRoomNumber(roomNumber);

        //vagas
        Integer vagaNumber = getIntegerValue("ctl00_ContentPlaceHolder1_detalhes_lbVaga", 0);
        offer.setVagaNumber(vagaNumber);

        //total area
        Integer totalArea = getIntegerValue("ctl00_ContentPlaceHolder1_detalhes_lbAreaUtil", 0);
        offer.setTotalArea(totalArea);

        //floors
        Integer floors = getIntegerValue("ctl00_ContentPlaceHolder1_detalhes_lbAndar", 0);
        offer.setFloorNumber(floors);

        //built year
        Integer builtYear = getYearBuilt();
        offer.setYearBuilt(builtYear);

        //iptu fee
        Integer iptu = getIntegerValue("ctl00_ContentPlaceHolder1_detalhes_lbIPTU", 1);
        offer.setIptuFee(iptu);

        String googleMapUrl = extractGoogleMapUrl();
        offer.setGoogleMapUrl(googleMapUrl);

        List<Long> viewedOffers = extractViewedOffers();
        offer.setViewedOffers(viewedOffers);

        offer.toString();

        //TODO ratings

        //TODO viewed offers
        List<String> contactPhones = extractContactPhones();
        offer.setContactPhones(contactPhones);

        String contactName = extractContactName();
        offer.setContactName(contactName);

        List<String> hashes = extractImages();
        offer.setImagesHashes(hashes);
        return offer;
    }


    private List<String> extractImages() {
        List<String> imageUrls = new ArrayList<>();
        Element galleryEl = doc.getElementById("galleria");
        if (galleryEl != null) {
            Elements galleryImagesEls = galleryEl.getElementsByTag("a");
            for (Element galleryImageEl : galleryImagesEls) {
                String imageUrl = galleryImageEl.attr("href");
                imageUrls.add(imageUrl);
            }
        }
        List<String> hashes = downloadImages(imageUrls);
        return hashes;

    }

    private String extractContactName() {
        String sellerName = null;
        Elements selleNameEls = doc.getElementsByClass("sellerName");
        if (selleNameEls != null && !selleNameEls.isEmpty()) {
            sellerName = selleNameEls.get(0).text();
        }

        return sellerName;
    }

    private List<String> downloadImages(List<String> imageUrls) {
        List<String> hashes = new ArrayList<>(imageUrls.size());
        for (String imageUrl : imageUrls) {
            try {
                URL url = new URL(imageUrl);
                InputStream in = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[2056];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();
                byte[] response = out.toByteArray();

                String hash = generateMD5Hash(response);

                File file = new File("images/" + hash);
                if (!file.exists())
                    file.createNewFile();

                FileOutputStream fos = new FileOutputStream(file, false);
                fos.write(response);
                fos.close();

                hashes.add(hash);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hashes;
    }

    private String generateMD5Hash(byte[] bytes) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");

            m.reset();
            m.update(bytes);
            byte[] digest = m.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aDigest : digest) {
                sb.append(Integer.toString((aDigest & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Long> extractViewedOffers() {
        List<Long> result = new ArrayList<>();
        Elements offers = doc.getElementsByClass("maisOfertasBox");
        if (offers != null && !offers.isEmpty()) {
            for (Element element : offers) {
                Elements imgEls = element.getElementsByAttribute("href");
                if (imgEls != null && !imgEls.isEmpty()) {
                    String url = imgEls.get(0).attr("href");
                    if (url != null && !url.isEmpty()) {
                        result.add(extractOfferId(url));
                    }
                }
            }
        }
        return result;
    }

    private String extractGoogleMapUrl() {
        String url = null;
        Element element = doc.getElementById("ctl00_ContentPlaceHolder1_ucMapa_imgGoogleMap");
        if (element != null)
            url = element.attr("src");
        return url;
    }

    private Integer getYearBuilt() {
        Element el = doc.getElementById("ctl00_ContentPlaceHolder1_detalhes_lbConstrucao");
        Integer builtYear = null;
        if (el != null) {
            Element valueEl = el.getElementsByClass("featureValue").get(0);
            String valueString = valueEl.text();
            builtYear = Integer.parseInt(valueString);

        }
        return builtYear;
    }

    private Integer getIntegerValue(String id, int pos) {
        Element summaryEl = doc.getElementById(id);
        Integer value = null;
        if (summaryEl != null) {
            Element el = summaryEl.getElementsByClass("featureValue").get(0);
            String valueArray[] = el.text().split(" ");
            if (valueArray.length > 1) {
                value = Integer.parseInt((valueArray[pos]));
            }
        }
        return value;
    }

    private Double getDoubleValue(String id, int pos) {
        Element summaryEl = doc.getElementById(id);
        Double value = null;
        if (summaryEl != null) {
            Element el = summaryEl.getElementsByClass("featureValue").get(0);
            String valueArray[] = el.text().split(" ");
            if (valueArray.length > 1) {
                value = Double.parseDouble((valueArray[pos]));
            }
        }
        return value;
    }

    private Double extractPrice() {
        Element summaryEl = doc.getElementById("ctl00_ContentPlaceHolder1_detalhes_lbPrecoVenda");
        Double price = null;
        if (summaryEl != null) {
            Element el = summaryEl.getElementsByClass("featureValue").get(0);
            String elString = el.text().split(" ")[1];
            elString = elString.replaceAll("\\.", "");
            price = Double.parseDouble(elString);
        }
        return price;
    }

    private List<String> extractContactPhones() {
        List<String> phones = new ArrayList<>();
        Element phoneEl = doc.getElementById("MostraFone2");
        if (phoneEl != null) {
            Elements phoneEls = phoneEl.children();
            if (phoneEls != null && !phoneEls.isEmpty()) {
                for (Element el : phoneEls)
                    phones.add(el.text());
            }
        }
        return phones;
    }
}
