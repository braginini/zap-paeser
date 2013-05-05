package com.zibea.parser.controller.thread;

import com.zibea.parser.dataaccess.RealtyDao;
import com.zibea.parser.model.domain.*;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.Proxy;
import java.sql.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * @author: Mikhail Bragin
 */
public class ArchiveOfferThread extends ParseThread {

    private Set<Offer> batch = new HashSet<>();

    private HashSet<Long> savedOffers;

    private RealtyDao dao;

    private static final int batchSize = 1;

    public ArchiveOfferThread(BlockingQueue<Offer> tasks, RealtyDao dao, HashSet<Long> savedOffers, Queue<Proxy> proxies) {
        super(tasks, null, proxies);
        this.dao = dao;
        this.savedOffers = savedOffers;
    }

    @Override
    public void run() {
        while (blinker == this) {
            try {

                if (this.tasks.isEmpty())
                    Thread.sleep(5000);
                //Offer offer = this.tasks.take();
                System.out.println("Parsed non saved offers count=" + tasks.size());

                tasks.drainTo(batch, batchSize);

                System.out.println("Batch size=" + batch.size());
                if (this.batch.size() >= batchSize) {
                    flushBatch();
                }
            } catch (InterruptedException e) {
                this.stopIt();
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void flushBatch() throws SQLException {
        System.out.println("Flushing...");
        Connection connection = dao.getConnection();
        PreparedStatement stmt = null;
        try {
            String tableName = null;
            String schemaName = "realty";

            String SQL = null;
            if (!batch.isEmpty()) {
                tableName = schemaName + ".Offers";
                SQL = "INSERT INTO " + tableName + "(" +
                        "id, " +
                        "state_id, " +
                        "city_id, " +
                        "district_id, " +
                        "apartment_id, " +
                        "transaction_id," +
                        "zap_url," +
                        "price," +
                        "service_fee," +
                        "room_number," +
                        "vaga_number," +
                        "total_area," +
                        "year_built," +
                        "floor_number," +
                        "iptu_fee," +
                        "square_meter_price," +
                        "google_map_url," +
                        "ts_published," +
                        "street," +
                        "images," +
                        "contact_phones," +
                        "contact_name ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            }


            if (tableName != null) {

                stmt = connection.prepareStatement(SQL);

                for (Offer offer : batch) {

                    if (savedOffers.contains(offer.getId()))
                        continue;

                    stmt.setLong(1, offer.getId());
                    stmt.setLong(2, (offer.getState().getId()));
                    stmt.setLong(3, (offer.getCity().getId()));
                    stmt.setLong(4, (offer.getDistrict().getId()));
                    stmt.setLong(5, (offer.getApartment().getId()));
                    stmt.setLong(6, (offer.getTransaction().getId()));
                    stmt.setString(7, offer.getUrl());

                    if (offer.getPrice() != null)
                        stmt.setDouble(8, offer.getPrice());
                    else
                        stmt.setNull(8, Types.DOUBLE);

                    if (offer.getServiceFee() != null)
                        stmt.setDouble(9, offer.getServiceFee());
                    else
                        stmt.setNull(9, Types.DOUBLE);

                    if (offer.getRoomNumber() != null)
                        stmt.setInt(10, offer.getRoomNumber());
                    else
                        stmt.setNull(10, Types.INTEGER);

                    if (offer.getVagaNumber() != null)
                        stmt.setInt(11, offer.getVagaNumber());
                    else
                        stmt.setNull(11, Types.INTEGER);

                    if (offer.getTotalArea() != null)
                        stmt.setInt(12, offer.getTotalArea());
                    else
                        stmt.setNull(12, Types.INTEGER);

                    if (offer.getYearBuilt() != null)
                        stmt.setInt(13, offer.getYearBuilt());
                    else
                        stmt.setNull(13, Types.INTEGER);

                    if (offer.getFloorNumber() != null)
                        stmt.setInt(14, offer.getFloorNumber());
                    else
                        stmt.setNull(14, Types.INTEGER);

                    if (offer.getIptuFee() != null)
                        stmt.setInt(15, offer.getIptuFee());
                    else
                        stmt.setNull(15, Types.INTEGER);

                    if (offer.getPricePerSquareMeter() != null)
                        stmt.setDouble(16, offer.getPricePerSquareMeter());
                    else
                        stmt.setNull(16, Types.DOUBLE);

                    if (offer.getGoogleMapUrl() != null)
                        stmt.setString(17, offer.getGoogleMapUrl());
                    else
                        stmt.setNull(17, Types.VARCHAR);

                    if (offer.getTsPublished() != null)
                        stmt.setLong(18, offer.getTsPublished());
                    else
                        stmt.setLong(18, System.currentTimeMillis());

                    if (offer.getStreetAddress() != null)
                        stmt.setString(19, offer.getStreetAddress());
                    else
                        stmt.setNull(19, Types.VARCHAR);

                    if (offer.getImagesHashes() != null && !offer.getImagesHashes().isEmpty()){
                        StringBuilder sb = new StringBuilder();
                        for (String hash : offer.getImagesHashes()){
                            sb.append(hash);
                            sb.append(",");
                        }
                        sb.deleteCharAt(sb.length() - 1); //remove last index
                        stmt.setString(20, sb.toString());
                    } else
                        stmt.setNull(20, Types.VARCHAR);

                    if (offer.getContactPhones() != null && !offer.getContactPhones().isEmpty()){
                        StringBuilder sb = new StringBuilder();
                        for (String phone : offer.getContactPhones()){
                            sb.append(phone);
                            sb.append(",");
                        }
                        sb.deleteCharAt(sb.length() - 1); //remove last index
                        stmt.setString(21, sb.toString());
                    } else
                        stmt.setNull(21, Types.VARCHAR);

                    if (offer.getContactName() != null)
                        stmt.setString(22, offer.getContactName());
                    else
                        stmt.setNull(22, Types.VARCHAR);

                    stmt.addBatch();
                }
                int[] rs = stmt.executeBatch();
            }
            connection.commit();

            for (Offer offer : batch)
                savedOffers.add(offer.getId());

            batch = new HashSet<>();
            System.out.println("Flushed");

        } catch (BatchUpdateException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().contains("Violation of PRIMARY KEY constraint")) {
                String stringId = e.getMessage().split("\\(")[1].split("\\)")[0];
                long duplicateId = Long.parseLong(stringId);
                batch.remove(new Offer(duplicateId));
                dao.closeConnection(connection);
                flushBatch();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(0);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            dao.closeConnection(connection);
        }

    }

    @Override
    public void createSubTasks() throws IOException, InterruptedException {

    }

    @Override
    public Document parseWithProxy() throws IOException {
        return null;
    }
}
