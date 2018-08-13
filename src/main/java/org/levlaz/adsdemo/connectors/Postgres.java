package org.levlaz.adsdemo.connectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.launchdarkly.eventsource.MessageEvent;

import org.levlaz.adsdemo.SimpleEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Postgres extends SimpleEventHandler {

    private static final Logger logger =
    LoggerFactory.getLogger(Postgres.class.getName());
    private String url;

    public Postgres(String url) throws SQLException {
        this.url = url;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void insertSummaryEvent(JsonObject event) throws SQLException{
        String SQL = "INSERT INTO ld_summary_event(start_date, end_date, features)" +
            "VALUES(?, ?, to_jsonb(?::json))";

        Connection conn = connect();
        PreparedStatement query = conn.prepareStatement(SQL);

        query.setInt(1, event.get("startDate").getAsInt());
        query.setInt(2, event.get("endDate").getAsInt());
        query.setString(3, event.get("features").toString());
        
        query.executeUpdate();
        query.close();
        conn.close();
    }

    public void insertUserIndexEvent(JsonObject event) throws SQLException{
        String SQL = "INSERT INTO ld_user_index(creation_date, user_id, custom)" + 
            "VALUES(?, ?, to_jsonb(?::json))";

        Connection conn = connect();
        PreparedStatement query = conn.prepareStatement(SQL); 

        query.setInt(1, event.get("creationDate").getAsInt());
        query.setString(2, event.get("user").getAsJsonObject().get("key").toString());
        query.setString(3, event.get("user").getAsJsonObject().get("custom").toString());

        query.executeUpdate();
        query.close();
        conn.close();
    }

    public void insertFeatureEvent(JsonObject event) throws SQLException{
        String SQL = "INSERT INTO ld_feature(feature_key, user_key, version, variation, value, default_value, creation_date)" +
            "VALUES(?, ?, ?, ?, ?, ?, ?)";

        Connection conn = connect();
        PreparedStatement query = conn.prepareStatement(SQL);

        query.setString(1, event.get("key").getAsString());
        query.setString(2, event.get("userKey").getAsString());
        query.setInt(3, event.get("version").getAsInt());
        query.setInt(4, event.get("variation").getAsInt());
        query.setString(5, event.get("variation").getAsString());
        query.setString(6, event.get("default").getAsString());
        query.setInt(7, event.get("creationDate").getAsInt());;

        query.executeUpdate();
        query.close();
        conn.close();
    }

    public void insertIdentifyEvent(JsonObject event) throws SQLException{
        String SQL = "INSERT INTO ld_identify_event(user_info, creation_date)" +
            "VALUES(to_jsonb(?::json), ?)";

        Connection conn = connect();
        PreparedStatement query = conn.prepareStatement(SQL);

        query.setString(1, event.get("user").toString());
        query.setInt(2, event.get("creationDate").getAsInt());

        query.executeUpdate();
        query.close();
        conn.close();
    }

    public void insertCustomEvent(JsonObject event) throws SQLException{
        String SQL = "INSERT INTO ld_custom_event(user_key, creation_date, event_key)" + 
            "VALUES(?, ?, ?)";

        Connection conn = connect();
        PreparedStatement query = conn.prepareStatement(SQL);

        query.setString(1, event.get("userKey").toString());
        query.setInt(2, event.get("creationDate").getAsInt());
        query.setString(3, event.get("key").toString());

        query.executeUpdate();
        query.close();
        conn.close();
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) throws Exception {
        String kind;
        JsonParser jp = new JsonParser();
        JsonArray ja = jp.parse(messageEvent.getData()).getAsJsonArray();

        for (int i = 0, size = ja.size(); i < size; i++) {
            JsonObject jo = ja.get(i).getAsJsonObject();
            kind = jo.get("kind").getAsString();

            switch (kind) {
                case "summary":
                    logger.info("Inserting new Summary Event");
                    insertSummaryEvent(jo);
                    break;
                case "index":
                    logger.info("Inserting new Index Event");
                    insertUserIndexEvent(jo);
                    break;
                case "feature":
                    logger.info("Inserting new Feature Event");
                    insertFeatureEvent(jo);
                    break;
                case "identify":
                    logger.info("Inserting new Identify Event");
                    insertIdentifyEvent(jo);
                    break;
                case "custom":
                    logger.info("Inserting new Custom Event");
                    insertCustomEvent(jo);
                    break;
                default:
                    logger.warn("Unknown kind, doing nothing: " + kind);
                    break;
            }
        }
    }
}