package com.transfersystem.service;

import com.example.exercises.transfersystem.transfer_request_response.ActionType;
import com.example.exercises.transfersystem.transfer_request_response.OutcomeType;
import com.example.exercises.transfersystem.transfer_request_response.TransferRequestType;
import com.example.exercises.transfersystem.transfer_request_response.TransferResponseType;
import com.transfersystem.dto.Account;
import com.transfersystem.dto.CurrencyAmount;
import com.transfersystem.dto.Storage;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransferSystemService {

    public TransferResponseType processTransferRequest(TransferRequestType request) throws IOException {
        TransferResponseType response = new TransferResponseType();
        response.setAction(request.getAction());
        response.setRequestId(request.getRequestId());
        response.setCurrency(request.getCurrency());
        response.setTargetAccountNumber(request.getTargetAccountNumber());
        response.setQuantity(request.getQuantity());
        response.setOutcome(OutcomeType.REJECT);
        if (request.getQuantity().compareTo(BigDecimal.ZERO) < 0 || request.getTargetAccountNumber() == null) {
            return response;
        }
        CurrencyAmount currencyAmount = getCurrencyAmountByRequest(request);
        if (currencyAmount.getAmount() == null || currencyAmount.getCurrency() == null) {
            return response;
        }
        Double currentAmountOfCurrency = currencyAmount.getAmount();
        action(currencyAmount, request.getQuantity().doubleValue(), request.getAction());
        if (currencyAmount.getAmount().equals(currentAmountOfCurrency)) {
            return response;
        }
        response.setOutcome(OutcomeType.ACCEPT);
        setNewAmountToCurrency(request, currencyAmount.getAmount());
        AccountsService.saveChangesToJsonStorage();
        return response;
    }

    private void action(CurrencyAmount currencyAmount, Double quantity, ActionType actionType) {
        if (actionType == ActionType.CREDIT) {
            currencyAmount.setAmount(quantity + currencyAmount.getAmount());
        } else if (actionType == ActionType.DEBIT) {
            double newCurrencyAmount = currencyAmount.getAmount() - quantity;
            if (newCurrencyAmount > 0) {
                currencyAmount.setAmount(newCurrencyAmount);
            }
        }
    }

    public CurrencyAmount getCurrencyAmountByRequest(TransferRequestType request) {
        Account account = getAccountByAccountNumber(request.getTargetAccountNumber());
        Optional<CurrencyAmount> currencyAmountOptional = getCurrencyAmountOptional(account, request.getCurrency());
        CurrencyAmount currencyAmount = new CurrencyAmount();
        if (currencyAmountOptional.isPresent())
            currencyAmount = currencyAmountOptional.get();
        return currencyAmount;
    }

    public Optional<CurrencyAmount> getCurrencyAmountOptional(Account account, String currencyStr) {
        return account.getCurrencyAmounts().stream()
                .filter(currency -> currency.getCurrency().equals(currencyStr))
                .findFirst();
    }

    private Account getAccountByAccountNumber(String accountNumber) {
        return Storage.getAccounts().getAccounts().stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findAny().orElse(new Account());
    }

    private void setNewAmountToCurrency(TransferRequestType request, Double amount) {
        int accountIndex = Storage.getAccounts().getAccounts()
                .indexOf(getAccountByAccountNumber(request.getTargetAccountNumber()));
        int currencyIndex = Storage.getAccounts().getAccounts().get(accountIndex)
                .getCurrencyAmounts().indexOf(getCurrencyAmountByRequest(request));
        Account account = Storage.getAccounts().getAccounts().get(accountIndex);
        account.getCurrencyAmounts().get(currencyIndex).setAmount(amount);
        Storage.getAccounts().getAccounts().get(accountIndex)
                .setCurrencyAmounts(account.getCurrencyAmounts());
    }

    public String parseTransferRequestToXmlString(TransferRequestType request) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(TransferRequestType.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        marshaller.marshal(request, sw);
        return sw.toString();
    }

    public String parseTransferResponseToXmlString(TransferResponseType response) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(TransferResponseType.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        marshaller.marshal(response, sw);
        return sw.toString();
    }
}
