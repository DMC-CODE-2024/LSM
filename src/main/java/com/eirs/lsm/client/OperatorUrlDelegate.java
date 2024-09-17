package com.eirs.lsm.client;

import com.eirs.lsm.alert.AlertIds;
import com.eirs.lsm.alert.AlertMessagePlaceholders;
import com.eirs.lsm.dto.OperatorRequestDTO;
import com.eirs.lsm.dto.OperatorResponseDTO;
import com.eirs.lsm.repository.entity.DeviceSyncOperation;
import com.eirs.lsm.repository.entity.DeviceSyncRequestListIdentity;
import com.eirs.lsm.repository.entity.DeviceSyncRequestStatus;
import com.eirs.lsm.repository.entity.SystemConfigKeys;
import com.eirs.lsm.service.ModuleAlertService;
import com.eirs.lsm.service.SystemConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.utils.Base64;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
public class OperatorUrlDelegate {

    public final Logger log = LoggerFactory.getLogger(this.getClass());

    private String operator;

    private Integer operatorEirId;

    @Value("${provision-gateway.certificate.key-store}")
    private String keyStore;

    @Value("${provision-gateway.certificate.key-password}")
    private String keyPassword;

    @Autowired
    private SystemConfigurationService config;

    @Autowired
    private StringEncryptor stringEncryptor;
    private String basicAuthCredentials = null;

    private Map<String, String> listUrlsMap = new HashMap<>();

    public void setOperator(String operator) {
        this.operator = operator;
    }

    private RestTemplate restTemplate;

    private final String NULL = "NULL";

    @Autowired
    ModuleAlertService moduleAlertService;

    public void setOperatorEirId(Integer operatorEirId) {
        this.operatorEirId = operatorEirId;
    }

    final DateTimeFormatter URL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    public void init() throws Exception {

        String encryptedBasicAuthCredentials = config.findByKey(SystemConfigKeys.OPERATOR_URL_BASIC_AUTH_CREDENTIALS.replaceAll("<OPERATOR>", operator));
        if (encryptedBasicAuthCredentials.startsWith("ENC")) {
            encryptedBasicAuthCredentials = encryptedBasicAuthCredentials.replaceAll("ENC\\(", "").replaceAll("\\)", "");
            log.info("Operator:{} encryptedBasicAuthCredentials:{}", operator, encryptedBasicAuthCredentials);
            basicAuthCredentials = stringEncryptor.decrypt(encryptedBasicAuthCredentials);
            log.info("Operator:{} encryptedBasicAuthCredentials:{} basicAuthCredentials:{}", operator, encryptedBasicAuthCredentials, basicAuthCredentials);
        } else {
            basicAuthCredentials = encryptedBasicAuthCredentials;
        }

        Integer readTimeOutInMinutes = config.findByKey(SystemConfigKeys.OPERATOR_URL_READ_TIME_OUT.replaceAll("<OPERATOR>", operator), 1);
        restTemplate = restTemplateHttps(readTimeOutInMinutes);

        String isEnable = config.findByKey(SystemConfigKeys.ENABLE_PROCESSING_BLACKED_LIST, "NO");
        if (StringUtils.equalsAnyIgnoreCase(isEnable, new String[]{"YES", "TRUE"})) {
            listUrlsMap.put(DeviceSyncRequestListIdentity.BLOCKED_LIST.toString(), config.findByKey(SystemConfigKeys.OPERATOR_URL_BLACKED.replaceAll("<OPERATOR>", operator).replaceAll("<NUMBER>", String.valueOf(operatorEirId))));
        }

        isEnable = config.findByKey(SystemConfigKeys.ENABLE_PROCESSING_EXCEPTION_LIST, "NO");
        if (StringUtils.equalsAnyIgnoreCase(isEnable, new String[]{"YES", "TRUE"})) {
            listUrlsMap.put(DeviceSyncRequestListIdentity.EXCEPTION_LIST.toString(), config.findByKey(SystemConfigKeys.OPERATOR_URL_EXCEPTION.replaceAll("<OPERATOR>", operator).replaceAll("<NUMBER>", String.valueOf(operatorEirId))));
        }

        isEnable = config.findByKey(SystemConfigKeys.ENABLE_PROCESSING_TRACKED_LIST, "NO");
        if (StringUtils.equalsAnyIgnoreCase(isEnable, new String[]{"YES", "TRUE"})) {
            listUrlsMap.put(DeviceSyncRequestListIdentity.TRACKED_LIST.toString(), config.findByKey(SystemConfigKeys.OPERATOR_URL_TRACKED.replaceAll("<OPERATOR>", operator).replaceAll("<NUMBER>", String.valueOf(operatorEirId))));
        }

        isEnable = config.findByKey(SystemConfigKeys.ENABLE_PROCESSING_ALLOWED_TAC, "NO");
        if (StringUtils.equalsAnyIgnoreCase(isEnable, new String[]{"YES", "TRUE"})) {
            listUrlsMap.put(DeviceSyncRequestListIdentity.ALLOWED_TAC.toString(), config.findByKey(SystemConfigKeys.OPERATOR_URL_ALLOWED_TAC.replaceAll("<OPERATOR>", operator).replaceAll("<NUMBER>", String.valueOf(operatorEirId))));
        }

        isEnable = config.findByKey(SystemConfigKeys.ENABLE_PROCESSING_BLOCKED_TAC, "NO");
        if (StringUtils.equalsAnyIgnoreCase(isEnable, new String[]{"YES", "TRUE"})) {
            listUrlsMap.put(DeviceSyncRequestListIdentity.BLOCKED_TAC.toString(), config.findByKey(SystemConfigKeys.OPERATOR_URL_BLOCKED_TAC.replaceAll("<OPERATOR>", operator).replaceAll("<NUMBER>", String.valueOf(operatorEirId))));
        }

    }

    public RestTemplate restTemplateHttps(int connectTimeOut) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(ResourceUtils.getFile(keyStore), keyPassword.toCharArray())
                .build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("https", csf).build();
        PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingConnManager.setMaxTotal(100);
        poolingConnManager.setDefaultMaxPerRoute(100);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(poolingConnManager).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        requestFactory.setConnectTimeout(connectTimeOut * 60 * 1000);
        return new RestTemplate(requestFactory);
    }

    public OperatorResponseDTO callUrl(OperatorRequestDTO requestDTO) {
        OperatorResponseDTO responseDTO = callOperatorUrl(requestDTO);
        return responseDTO;
    }

    public OperatorResponseDTO callOperatorUrl(OperatorRequestDTO requestDTO) {
        long start = System.currentTimeMillis();
        String url = listUrlsMap.get(requestDTO.getListType().toString());
        if (requestDTO.getListType() == DeviceSyncRequestListIdentity.ALLOWED_TAC || requestDTO.getListType() == DeviceSyncRequestListIdentity.BLOCKED_TAC) {
            url = url.replaceAll("<TAC>", StringUtils.isBlank(requestDTO.getTac()) ? NULL : requestDTO.getTac());
            url = url.replaceAll("<REQUEST_DATE>", requestDTO.getRequestDate().format(URL_DATE_FORMATTER));
        } else {
            url = url.replaceAll("<IMEI>", StringUtils.isBlank(requestDTO.getImei()) ? NULL : requestDTO.getImei());
            url = url.replaceAll("<ACTUAL_IMEI>", StringUtils.isBlank(requestDTO.getActualImei()) ? NULL : requestDTO.getActualImei());
            url = url.replaceAll("<IMSI>", StringUtils.isBlank(requestDTO.getImsi()) ? NULL : requestDTO.getImsi());
            url = url.replaceAll("<MSISDN>", StringUtils.isBlank(requestDTO.getMsisdn()) ? NULL : requestDTO.getMsisdn());
            url = url.replaceAll("<REQUEST_DATE>", requestDTO.getRequestDate().format(URL_DATE_FORMATTER));
        }
        OperatorResponseDTO responseDTO = new OperatorResponseDTO();
        log.info("Calling Url for Operator:{} URL:{} Request:{}", requestDTO.getOperatorName(), url, requestDTO);
        try {
            HttpEntity<String> request = new HttpEntity<String>(getBasicAuthHeaders());
            HttpMethod httpMethod = requestDTO.getOperationType() == DeviceSyncOperation.ADD ? HttpMethod.POST : HttpMethod.DELETE;
            ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, request, String.class);

            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.ACCEPTED
                    || response.getStatusCode() == HttpStatus.NO_CONTENT || response.getStatusCode() == HttpStatus.ALREADY_REPORTED) {
                responseDTO.setFailureReason("Success");
                responseDTO.setStatus(DeviceSyncRequestStatus.SYNCED);
            } else {
                responseDTO.setFailureReason("Response Status " + response.getStatusCode());
                responseDTO.setStatus(DeviceSyncRequestStatus.FAILED);
            }

        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("Error while getting calling URL for Operator:{} url:{} Error:{}", operator, url, e.getMessage());
            responseDTO.setFailureReason("Connection refused");
            responseDTO.setStatus(DeviceSyncRequestStatus.CONNECTION_FAILED);
            sendAlert(url, e.getMessage(), requestDTO.getListType());
        } catch (Exception e) {
            log.error("Error while getting calling URL for Operator:{} url:{} Error:{}", operator, url, e.getMessage());
            responseDTO.setFailureReason(e.getMessage());
            responseDTO.setStatus(DeviceSyncRequestStatus.FAILED);
            sendAlert(url, e.getMessage(), requestDTO.getListType());
        }
        log.info("Called Instance URL:{} Response:{} TimeTaken-{}", url, responseDTO.getStatus(), (System.currentTimeMillis() - start));
        responseDTO.setResposeTime(LocalDateTime.now());
        return responseDTO;
    }

    private HttpHeaders getBasicAuthHeaders() {
        byte[] plainCredsBytes = basicAuthCredentials.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }

    public void sendAlert(String url, String exception, DeviceSyncRequestListIdentity listIdentity) {
        Map<AlertMessagePlaceholders, String> map = new HashMap<>();
        map.put(AlertMessagePlaceholders.EXCEPTION, exception);
        map.put(AlertMessagePlaceholders.OPERATOR, operator);
        map.put(AlertMessagePlaceholders.URL, url);
        map.put(AlertMessagePlaceholders.LIST, listIdentity.name());
        moduleAlertService.sendAlert(AlertIds.LIST_SYNC_URL_EXCEPTION, map);
    }

}
