package com.zibea.parser.model.domain;

import java.util.List;

/**
 * @author: Mikhail Bragin
 */
public class Offer {

    private Long id;

    private State state;

    private City city;

    private District district;

    private Apartment apartment;

    private Transaction transaction;

    private String url;

    private Double price;

    private Double serviceFee;

    private Integer roomNumber;

    private Integer vagaNumber;

    private Integer totalArea;

    private Integer yearBuilt;

    private Integer floorNumber;

    private Integer iptuFee;

    private Double pricePerSquareMeter;

    private List<String> imagesHashes;

    private List<Long> viewedOffers;

    private String googleMapUrl;

    private Long tsPublished;

    private String streetAddress;

    private List<String> contactPhones;
    private String contactName;

    public Offer() {
    }

    public Offer(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer getVagaNumber() {
        return vagaNumber;
    }

    public void setVagaNumber(Integer vagaNumber) {
        this.vagaNumber = vagaNumber;
    }

    public Integer getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(Integer totalArea) {
        this.totalArea = totalArea;
    }

    public Integer getYearBuilt() {
        return yearBuilt;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public Integer getIptuFee() {
        return iptuFee;
    }

    public void setIptuFee(Integer iptuFee) {
        this.iptuFee = iptuFee;
    }

    public Double getPricePerSquareMeter() {
        return pricePerSquareMeter;
    }

    public void setPricePerSquareMeter(Double pricePerSquareMeter) {
        this.pricePerSquareMeter = pricePerSquareMeter;
    }

    public List<String> getImagesHashes() {
        return imagesHashes;
    }

    public void setImagesHashes(List<String> imageHashes) {
        this.imagesHashes = imageHashes;
    }

    public List<Long> getViewedOffers() {
        return viewedOffers;
    }

    public void setViewedOffers(List<Long> viewedOffers) {
        this.viewedOffers = viewedOffers;
    }

    public String getGoogleMapUrl() {
        return googleMapUrl;
    }

    public void setGoogleMapUrl(String googleMapUrl) {
        this.googleMapUrl = googleMapUrl;
    }

    public Long getTsPublished() {
        return tsPublished;
    }

    public void setTsPublished(Long tsPublished) {
        this.tsPublished = tsPublished;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public List<String> getContactPhones() {
        return contactPhones;
    }

    public void setContactPhones(List<String> contactPhones) {
        this.contactPhones = contactPhones;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Offer offer = (Offer) o;

        if (id != null ? !id.equals(offer.id) : offer.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "id=" + id +
                ", state=" + state +
                ", city=" + city +
                ", district=" + district +
                ", apartment=" + apartment +
                ", transaction=" + transaction +
                ", url='" + url + '\'' +
                ", price=" + price +
                ", serviceFee=" + serviceFee +
                ", roomNumber=" + roomNumber +
                ", vagaNumber=" + vagaNumber +
                ", totalArea=" + totalArea +
                ", yearBuilt=" + yearBuilt +
                ", floorNumber=" + floorNumber +
                ", iptuFee=" + iptuFee +
                ", pricePerSquareMeter=" + pricePerSquareMeter +
                ", imagesHashes=" + imagesHashes +
                ", viewedOffers=" + viewedOffers +
                ", googleMapUrl='" + googleMapUrl + '\'' +
                ", tsPublished=" + tsPublished +
                ", streetAddress='" + streetAddress + '\'' +
                '}';
    }
}
