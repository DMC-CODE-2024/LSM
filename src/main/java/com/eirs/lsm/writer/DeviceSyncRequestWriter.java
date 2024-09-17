package com.eirs.lsm.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public class DeviceSyncRequestWriter extends Writter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${deviceSyncRequest.delete.filePath}")
    private String filePath;

    final String DATE = "<DATE>";
    String selectQuery = "select * from device_sync_request where request_date <'" + DATE + "' and status='SYNCED'";

    DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    DateTimeFormatter fileSuffixDateFormat = DateTimeFormatter.ofPattern("yyyyMMddHH");
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String fullFileHeader = "id,created_on,device_id,failure_reason,identity,imei,imsi,insert_for_sync_time,msisdn,no_of_retry,operation,operator_id,operator_name,request_date,status,sync_request_time,sync_response_time,tac";

    public void writeFullData(LocalDateTime endDate) {
        String queryStartDate = simpleDateFormat.format(endDate);
        String query = selectQuery.replaceAll(DATE, queryStartDate);
        String filename = "PM_DEVICE_SYNC_DELETE_" + endDate.format(fileSuffixDateFormat) + ".csv";
        String filepath = filePath + "/" + filename;

        PrintWriter writer = null;
        try {
            createFile(filepath);

            writer = new PrintWriter(filepath);
            writer.println(fullFileHeader);
            PrintWriter finalWriter = writer;
            jdbcTemplate.query(query, new RowCallbackHandler() {
                public void processRow(ResultSet resultSet) throws SQLException {
                    String id = nullToBlank(resultSet.getString("id"));
                    String created_on = nullToBlank(resultSet.getString("created_on"));
                    String device_id = nullToBlank(resultSet.getString("device_id"));
                    String failure_reason = nullToBlank(resultSet.getString("failure_reason"));
                    String identity = nullToBlank(resultSet.getString("identity"));
                    String imei = nullToBlank(resultSet.getString("imei"));
                    String imsi = nullToBlank(resultSet.getString("imsi"));
                    String insert_for_sync_time = nullToBlank(resultSet.getString("insert_for_sync_time"));
                    String msisdn = nullToBlank(resultSet.getString("msisdn"));
                    String no_of_retry = nullToBlank(resultSet.getString("no_of_retry"));
                    String operation = nullToBlank(resultSet.getString("operation"));
                    String operator_id = nullToBlank(resultSet.getString("operator_id"));
                    String operator_name = nullToBlank(resultSet.getString("operator_name"));
                    String request_date = nullToBlank(resultSet.getString("request_date"));
                    String status = nullToBlank(resultSet.getString("status"));
                    String sync_request_time = nullToBlank(resultSet.getString("sync_request_time"));
                    String sync_response_time = nullToBlank(resultSet.getString("sync_response_time"));
                    String tac = nullToBlank(resultSet.getString("tac"));
                    finalWriter.println(id + "," + created_on + "," + device_id + "," + failure_reason + "," + identity + "," + imei + "," + imsi + "," + insert_for_sync_time + "," + msisdn
                            + "," + no_of_retry + "," + operation + "," + operator_id + "," + operator_name + "," + request_date + "," + status + "," + sync_request_time + "," + sync_response_time + "," + tac);
                }
            });
            log.info("File is written for Filename:{} Query:{}", filename, query);
        } catch (Exception e) {
            log.error("Error While creating file Allow tac Error:{}", e.getMessage(), e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
