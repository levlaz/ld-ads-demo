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

    public void insertSummaryEvent(JsonObject event) {
        String SQL = "INSERT INTO ld_summary_event(start_date, end_date, features)" +
            "VALUES(?, ?, to_jsonb(?::json))";

        try {
            Connection conn = connect();
            PreparedStatement query = conn.prepareStatement(SQL);

            query.setInt(1, event.get("startDate").getAsInt());
            query.setInt(2, event.get("endDate").getAsInt());
            query.setString(3, event.get("features").toString());
            
            query.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
    }

    public void insertUserIndexEvent(JsonObject event) {
        String SQL = "INSERT INTO ld_user_index(creation_date, user_id, custom)" + 
            "VALUES(?, ?, to_jsonb(?::json))";

        try { 
            Connection conn = connect();
            PreparedStatement query = conn.prepareStatement(SQL); 
            
            query.setInt(1, event.get("creationDate").getAsInt());
            query.setString(2, event.get("user").getAsJsonObject().get("key").toString());
            query.setString(3, event.get("user").getAsJsonObject().get("custom").toString());

            query.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex.getMessage());
        }
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
                default:
                    logger.warn("Unkown kind, doing nothing: " + kind);
                    break;
            }
        }
    }
}