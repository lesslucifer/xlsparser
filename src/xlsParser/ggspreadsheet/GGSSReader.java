/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xlsParser.ggspreadsheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.REDIRECT_URI;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import xlsParser.*;

/**
 *
 * @author vinova
 */
public class GGSSReader implements XLSReader {

    private SpreadsheetService service;

    private SpreadsheetEntry getSpreadsheet(String ssname)
            throws Exception {
        SpreadsheetFeed spreadsheet = service.getFeed(FeedURLFactory.getDefault().getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);
        for (SpreadsheetEntry entry : spreadsheet.getEntries()) {
            if (entry.getTitle().getPlainText().equals(ssname)) {
                return entry;
            }
        }

        return null;
    }

    private List<String> getColumns(WorksheetEntry sheetEntry)
            throws Exception {
        URL cellFeedUrl = sheetEntry.getCellFeedUrl();

        CellQuery cellQuery = new CellQuery(cellFeedUrl);
        cellQuery.setMinimumRow(1);
        cellQuery.setMaximumRow(1);

        CellFeed cellFeed = service.query(cellQuery, CellFeed.class);

        List<String> worksheetColumns = new ArrayList<>(sheetEntry.getColCount());
        for (CellEntry cell : cellFeed.getEntries()) {
            worksheetColumns.add(cell.getPlainTextContent());
        }

        return worksheetColumns;
    }

    private Map<String, String> getRowData(ListEntry row) {
        Map<String, String> rowValues = new HashMap<>();
        for (String tag : row.getCustomElements().getTags()) {
            String value = row.getCustomElements().getValue(tag);
            if (value == null) {
                value = "";
            }
            rowValues.put(tag, value);
        }
        return rowValues;
    }

    private List<Map<String, String>> getAllRows(WorksheetEntry sheetEntry)
            throws Exception {
        URL listFeedUrl = sheetEntry.getListFeedUrl();
        ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

        List<Map<String, String>> rows = new ArrayList<>(sheetEntry.getRowCount());
        for (ListEntry row : listFeed.getEntries()) {
            Map<String, String> rowValues = getRowData(row);
            rows.add(rowValues);
        }

        return rows;
    }

    private final String clientID, clientSecrect;
    private String accessToken, refreshToken, authPath;
    private static final List<String> SCOPES = Arrays.asList("https://spreadsheets.google.com/feeds");
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    public GGSSReader(String cid, String cs, String a) throws Exception {
        this.clientID = cid;
        this.clientSecrect = cs;
        this.authPath = a;
        try (BufferedReader fr = new BufferedReader(new FileReader(a))) {
            this.accessToken = fr.readLine();
            this.refreshToken = fr.readLine();
        }
        catch (Exception ex)
        {
            System.err.println(ex);
            this.accessToken = "";
            this.refreshToken = "";
        }

        try
        {
            this.setupService();
        }
        catch (Exception ex)
        {
            this.refreshTokens();
            // try setup service again
            this.setupService();
        }
    }
    
    private void setupService() throws Exception
    {
        Credential credential = getCredential();
        service = new SpreadsheetService("xlsparser");
        service.setOAuth2Credentials(credential);
        service.setProtocolVersion(SpreadsheetService.Versions.V3);
        service.setReadTimeout(60000);
        service.setConnectTimeout(60000);
        
        // test service
        service.getFeed(FeedURLFactory.getDefault().getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);
    }

    private Credential getCredential() throws IOException, URISyntaxException {
        HttpTransport transport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        
        System.out.println("Access token: " + accessToken);
        System.out.println("Refresh token: " + refreshToken);
        
            // Build a new GoogleCredential instance and return it.
        return new GoogleCredential.Builder().setClientSecrets(clientID, clientSecrect)
                .setJsonFactory(jsonFactory).setTransport(transport).build()
                .setAccessToken(accessToken).setRefreshToken(refreshToken);
    }
    
    private void refreshTokens() throws Exception
    {
        HttpTransport transport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        
        // Step 1: Authorize -->
        String authorizationUrl
                = new GoogleAuthorizationCodeRequestUrl(clientID, REDIRECT_URI, SCOPES).build();

        // Point or redirect your user to the authorizationUrl.
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);
        Desktop.getDesktop().browse(new URI(authorizationUrl));

        System.out.print("Paste your code: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String auth = reader.readLine();

        GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(transport, jsonFactory, clientID, clientSecrect,
        auth, REDIRECT_URI).execute();

        accessToken = response.getAccessToken();
        refreshToken = response.getRefreshToken();
            
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(authPath)))) {
            writer.println(response.getAccessToken());
            writer.println(response.getRefreshToken());
            writer.flush();
        }
    }

    @Override
    public XLSWorkbook read(String file) throws Exception {
        SpreadsheetEntry spreadsheet = getSpreadsheet(file);

        List<WorksheetEntry> worksheets = spreadsheet.getWorksheets();

        List<XLSSheet> xlsSheets = new ArrayList(worksheets.size());
        for (int i = 0; i < worksheets.size(); ++i) {
            xlsSheets.add(createXLSSheet(worksheets.get(i)));
        }

        return new BaseXLSWorkbook(xlsSheets);
    }

    private XLSSheet createXLSSheet(WorksheetEntry worksheet) throws Exception {
        List<String> header = getColumns(worksheet);
        Map<String, Integer> headerMap = new HashMap();
        for (int i = 0; i < header.size(); ++i) {
            headerMap.put(header.get(i), i);
        }

        List<Map<String, String>> rows = getAllRows(worksheet);

        List<XLSRecord> contents = new ArrayList(Math.max(1, rows.size() - 1));
        for (int i = 0; i < rows.size(); ++i) {
            contents.add(this.createRecord(headerMap, rows.get(i)));
        }

        return new BaseXLSSheet(worksheet.getTitle().getPlainText(), headerMap, contents);
    }

    private XLSRecord createRecord(Map<String, Integer> headerMap, Map<String, String> row) {
        String[] rowdata = new String[headerMap.size()];

        headerMap.forEach((k, v) -> {
            String kk = k.replaceAll("\\s", "").toLowerCase();
            rowdata[v] = row.get(kk);
        });

        return new BaseXLSRecord(headerMap, rowdata);
    }
}
