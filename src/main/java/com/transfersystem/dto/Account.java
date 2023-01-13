
package com.transfersystem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import javax.annotation.processing.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "accountNumber",
    "currencyAmounts"
})
@Generated("jsonschema2pojo")
public class Account {

    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("currencyAmounts")
    private List<CurrencyAmount> currencyAmounts = null;

    @JsonProperty("accountNumber")
    public String getAccountNumber() {
        return accountNumber;
    }

    @JsonProperty("accountNumber")
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @JsonProperty("currencyAmounts")
    public List<CurrencyAmount> getCurrencyAmounts() {
        return currencyAmounts;
    }

    @JsonProperty("currencyAmounts")
    public void setCurrencyAmounts(List<CurrencyAmount> currencyAmounts) {
        this.currencyAmounts = currencyAmounts;
    }

}
