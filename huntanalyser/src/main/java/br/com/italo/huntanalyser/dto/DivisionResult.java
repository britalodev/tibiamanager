package br.com.italo.huntanalyser.dto;

import br.com.italo.huntanalyser.model.Session;
import lombok.Data;

import java.util.List;

@Data
public class DivisionResult {
    private List<Transfer> transfers;
    private long totalProfit;
    private long profitPerPlayer;
    private Session session;

    public String getTotalProfitFormatted() {
        return totalProfit / 1000 + "k";
    }

    public String getProfitPerPlayerFormatted() {
        return profitPerPlayer / 1000 + "k";
    }
}
