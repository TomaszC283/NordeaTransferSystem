import com.example.exercises.transfersystem.transfer_request_response.ActionType;
import com.example.exercises.transfersystem.transfer_request_response.OutcomeType;
import com.example.exercises.transfersystem.transfer_request_response.TransferRequestType;
import com.example.exercises.transfersystem.transfer_request_response.TransferResponseType;
import com.transfersystem.activemq.ConnectionService;
import com.transfersystem.activemq.MessageProducerService;
import com.transfersystem.activemq.MessageConsumerService;
import com.transfersystem.dto.Account;
import com.transfersystem.dto.CurrencyAmount;
import com.transfersystem.dto.Example;
import com.transfersystem.dto.Storage;
import com.transfersystem.service.AccountsService;
import com.transfersystem.service.TransferSystemService;
import org.junit.jupiter.api.*;

import javax.jms.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransferSystemTest {

    private ConnectionService connectionService;
    private MessageProducerService producerService;
    private MessageConsumerService consumerService;
    private TransferSystemService transferSystemService;

    private final static String SESSION_REQUEST_SUBJECT = "requestSubjectTest";
    private final static String SESSION_RESPONSE_SUBJECT = "responseSubjectTest";

    @BeforeAll
    static void beforeAll() throws FileNotFoundException {
        AccountsService.initializeStorage();
    }

    @BeforeEach
    void initializeService() {
        this.connectionService = new ConnectionService();
        this.connectionService.initializeSession();
        this.producerService = new MessageProducerService(connectionService);
        this.consumerService = new MessageConsumerService(connectionService);
        this.transferSystemService = new TransferSystemService();
    }

    @AfterEach
    void closeConnection() throws JMSException {
        this.connectionService.closeConnection();
    }

    @Test
    @DisplayName("ActiveMQ Send/Receive message")
    void sendAndReceiveMessageByActiveMQTest() throws IOException, JMSException {
        // Send and Receive Request
        String xmlRequest = Files.lines(Paths.get("src/main/resources/example-transfer-request.xml")).collect(Collectors.joining("\n"));
        producerService.sendMessage(xmlRequest, SESSION_REQUEST_SUBJECT);
        String message = consumerService.receiveMessage(SESSION_REQUEST_SUBJECT);
        assertEquals(xmlRequest, message);
        // Send and Receive Response
        String xmlResponse = Files.lines(Paths.get("src/main/resources/example-transfer-response.xml")).collect(Collectors.joining("\n"));
        producerService.sendMessage(xmlResponse, SESSION_RESPONSE_SUBJECT);
        message = consumerService.receiveMessage(SESSION_RESPONSE_SUBJECT);
        assertEquals(xmlResponse, message);
    }

    @Test
    @DisplayName("Unmarshalling example request")
    void exampleTransferRequestUnmarshallTest() throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(TransferRequestType.class);
        Unmarshaller u = context.createUnmarshaller();
        TransferRequestType request =
                (TransferRequestType) u.unmarshal(new InputStreamReader(new FileInputStream("src/main/resources/example-transfer-request.xml"), StandardCharsets.UTF_8));
        assertNotNull(request);
        assertEquals("929a1378-1487-49c8-8237-a8e7ec152cbb", request.getRequestId());
        assertEquals("000142006678", request.getTargetAccountNumber());
        assertEquals(ActionType.CREDIT, request.getAction());
        assertEquals("SEK", request.getCurrency());
        assertEquals(BigDecimal.valueOf(50.00), request.getQuantity().setScale(1, RoundingMode.CEILING));
    }

    @Test
    @DisplayName("Unmarshalling example response")
    void exampleTransferResponseUnmarshallTest() throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(TransferResponseType.class);
        Unmarshaller u = context.createUnmarshaller();
        TransferResponseType response =
                (TransferResponseType) u.unmarshal(new InputStreamReader(new FileInputStream("src/main/resources/example-transfer-response.xml"), StandardCharsets.UTF_8));
        assertNotNull(response);
        assertEquals("929a1378-1487-49c8-8237-a8e7ec152cbb", response.getRequestId());
        assertEquals("000142006678", response.getTargetAccountNumber());
        assertEquals(ActionType.CREDIT, response.getAction());
        assertEquals("SEK", response.getCurrency());
        assertEquals(BigDecimal.valueOf(50.00), response.getQuantity().setScale(1, RoundingMode.CEILING));
        assertEquals(OutcomeType.ACCEPT, response.getOutcome());
    }

    @Test
    @DisplayName("Storage should have Account Numbers and currency amounts not empty after import from JSON Data")
    void exampleAccountsTest() {
        Example accounts = Storage.getAccounts();
        assertNotNull(accounts);
        assertEquals(3, accounts.getAccounts().size());
        Account account = accounts.getAccounts().get(0);
        assertEquals("000142006678", account.getAccountNumber());
        assertNotNull(account.getAccountNumber());
        assertNotNull(account.getCurrencyAmounts());
        account = accounts.getAccounts().get(1);
        assertEquals("111122224010", account.getAccountNumber());
        assertNotNull(account.getAccountNumber());
        assertNotNull(account.getCurrencyAmounts());
        account = accounts.getAccounts().get(2);
        assertEquals("000056013005", account.getAccountNumber());
        assertNotNull(account.getAccountNumber());
        assertNotNull(account.getCurrencyAmounts());
    }

    @Test
    @DisplayName("Account storage should have stored accounts")
    void storageShouldStoreAccounts() {
        assertNotNull(Storage.getAccounts());
        assertNotNull(Storage.getAccounts().getAccounts());
        assertEquals(3, Storage.getAccounts().getAccounts().size());
    }

    @Test
    @DisplayName("Amount of the currency should increase after Credit Request")
    void requestWithCreditActionTest() throws IOException, JAXBException {
        JAXBContext context = JAXBContext.newInstance(TransferRequestType.class);
        Unmarshaller u = context.createUnmarshaller();
        TransferRequestType request =
                (TransferRequestType) u.unmarshal(new InputStreamReader(new FileInputStream("src/main/resources/example-transfer-request.xml"), StandardCharsets.UTF_8));

        CurrencyAmount currencyAmount = transferSystemService.getCurrencyAmountByRequest(request);
        Double amountBeforeProcessingTheRequest = currencyAmount.getAmount();

        TransferResponseType response = transferSystemService.processTransferRequest(request);
        assertEquals("929a1378-1487-49c8-8237-a8e7ec152cbb", response.getRequestId());
        assertEquals(ActionType.CREDIT, response.getAction());
        assertEquals(OutcomeType.ACCEPT, response.getOutcome());
        assertEquals("SEK", response.getCurrency());
        assertEquals("000142006678", response.getTargetAccountNumber());
        assertEquals(request.getQuantity().doubleValue() + amountBeforeProcessingTheRequest, currencyAmount.getAmount());
    }

    @Test
    @DisplayName("App should reject request if you have negative value of quantity in Request")
    void requestsQuantityHasToBePositiveTest() throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(TransferRequestType.class);
        Unmarshaller u = context.createUnmarshaller();
        TransferRequestType request =
                (TransferRequestType) u.unmarshal(new InputStreamReader(new FileInputStream("src/main/resources/example-transfer-request.xml"), StandardCharsets.UTF_8));
        request.setQuantity(BigDecimal.valueOf(-100));
        TransferResponseType response = transferSystemService.processTransferRequest(request);
        assertEquals(OutcomeType.REJECT, response.getOutcome());
    }

    @Test
    @DisplayName("Amount of the currency should decrease after Debit Request")
    void requestWithDebitActionTest() throws IOException {
        TransferRequestType request = new TransferRequestType();
        request.setRequestId("929a1378-1487-49c8-8237-a8e7ec152cbb");
        request.setAction(ActionType.DEBIT);
        request.setQuantity(BigDecimal.valueOf(50));
        request.setTargetAccountNumber("111122224010");
        request.setCurrency("USD");

        CurrencyAmount currencyAmount = transferSystemService.getCurrencyAmountByRequest(request);
        Double amountBeforeProcessingTheRequest = currencyAmount.getAmount();

        TransferResponseType response = transferSystemService.processTransferRequest(request);
        assertEquals("929a1378-1487-49c8-8237-a8e7ec152cbb", response.getRequestId());
        assertEquals(ActionType.DEBIT, response.getAction());
        assertEquals(OutcomeType.ACCEPT, response.getOutcome());
        assertEquals("USD", response.getCurrency());
        assertEquals("111122224010", response.getTargetAccountNumber());
        assertEquals(amountBeforeProcessingTheRequest - request.getQuantity().doubleValue(), currencyAmount.getAmount());
    }

    @Test
    @DisplayName("Currency amount after debit shouldn't have negative value")
    void invalidRequestWithDebitActionTest() throws IOException {
        TransferRequestType request = new TransferRequestType();
        request.setRequestId("929a1378-1487-49c8-8237-a8e7ec152cbb");
        request.setAction(ActionType.DEBIT);
        request.setTargetAccountNumber("111122224010");
        request.setCurrency("USD");
        Double amountBeforeProcessingTheRequest = transferSystemService.getCurrencyAmountByRequest(request).getAmount();
        request.setQuantity(BigDecimal.valueOf(amountBeforeProcessingTheRequest + 10000));
        TransferResponseType response = transferSystemService.processTransferRequest(request);
        Double amountAfterProcessingTheRequest = transferSystemService.getCurrencyAmountByRequest(request).getAmount();
        assertEquals(OutcomeType.REJECT, response.getOutcome());
        assertEquals(amountBeforeProcessingTheRequest, amountAfterProcessingTheRequest);
    }

    @Test
    @DisplayName("Amount of currency in accounts storage should change after Accepted request")
    void amountOfCurrencyChangeTest() throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(TransferRequestType.class);
        Unmarshaller u = context.createUnmarshaller();
        TransferRequestType request =
                (TransferRequestType) u.unmarshal(new InputStreamReader(new FileInputStream("src/main/resources/example-transfer-request.xml"), StandardCharsets.UTF_8));

        CurrencyAmount currencyAmount = transferSystemService.getCurrencyAmountByRequest(request);
        int repeatAmount = 10;
        Double amountBeforeRequest = currencyAmount.getAmount();
        System.out.println(amountBeforeRequest);
        for (int i = 0; i < repeatAmount; i++) {
            transferSystemService.processTransferRequest(request);
        }
        Double amountAfterRequest = amountBeforeRequest + repeatAmount * request.getQuantity().doubleValue();
        currencyAmount = transferSystemService.getCurrencyAmountByRequest(request);
        assertEquals(amountAfterRequest, currencyAmount.getAmount());
    }
}
