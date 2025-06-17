package br.com.italo.huntanalyser.dto;

import lombok.Data;

@Data
public class Transfer {
    private String from;
    private String to;
    private long amount;
    private String bankCommand;
    private String formattedMessage;

    public String getAmountFormatted() {
        return amount / 1000 + "k";
    }
}
