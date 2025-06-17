package br.com.italo.huntanalyser.service;

import br.com.italo.huntanalyser.dto.DivisionResult;
import br.com.italo.huntanalyser.dto.Transfer;
import br.com.italo.huntanalyser.model.PlayerSession;
import br.com.italo.huntanalyser.model.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class LootDivisionService {

    public DivisionResult calculateLootDivision(Session session) {
        if (session.getPlayers() == null || session.getPlayers().isEmpty()) {
            throw new IllegalArgumentException("Lista de jogadores não pode estar vazia");
        }

        List<PlayerSession> players = session.getPlayers();

        // 1. Calcular o lucro total
        long totalProfit = players.stream()
                .mapToLong(PlayerSession::getBalance)
                .sum();

        // 2. Calcular lucro por jogador
        long profitPerPlayer = totalProfit / players.size();

        // 3. Calcular quanto cada jogador precisa receber/pagar
        Map<String, Long> adjustments = new HashMap<>();
        for (PlayerSession player : players) {
            long adjustment = profitPerPlayer - player.getBalance();
            adjustments.put(player.getPlayer().getId().getPlayerName(), adjustment);
        }

        // 4. Separar quem deve receber e quem deve pagar
        List<Map.Entry<String, Long>> receivers = new ArrayList<>();
        List<Map.Entry<String, Long>> payers = new ArrayList<>();

        for (Map.Entry<String, Long> entry : adjustments.entrySet()) {
            if (entry.getValue() > 0) {
                receivers.add(entry);
            } else if (entry.getValue() < 0) {
                payers.add(entry);
            }
        }

        // 5. Gerar transferências
        List<Transfer> transfers = new ArrayList<>();

        // Ordenar receivers por valor decrescente e payers por valor absoluto decrescente
        receivers.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        payers.sort((a, b) -> Long.compare(Math.abs(b.getValue()), Math.abs(a.getValue())));

        int receiverIndex = 0;
        int payerIndex = 0;

        while (receiverIndex < receivers.size() && payerIndex < payers.size()) {
            Map.Entry<String, Long> receiver = receivers.get(receiverIndex);
            Map.Entry<String, Long> payer = payers.get(payerIndex);

            long toReceive = receiver.getValue();
            long toPay = Math.abs(payer.getValue());

            long transferAmount = Math.min(toReceive, toPay);

            if (transferAmount > 0) {
                Transfer transfer = new Transfer();
                transfer.setFrom(payer.getKey());
                transfer.setTo(receiver.getKey());
                transfer.setAmount(transferAmount);
                transfer.setBankCommand(String.format("Bank: transfer %d to %s", transferAmount, receiver.getKey()));
                transfer.setFormattedMessage(String.format("%s to pay %dk to %s (Bank: transfer %d to %s)",
                        payer.getKey(), transferAmount / 1000, receiver.getKey(), transferAmount, receiver.getKey()));

                transfers.add(transfer);

                // Atualizar valores
                receiver.setValue(toReceive - transferAmount);
                payer.setValue(payer.getValue() + transferAmount);

                // Se o receiver foi totalmente pago, passar para o próximo
                if (receiver.getValue() == 0) {
                    receiverIndex++;
                }

                // Se o payer pagou tudo que devia, passar para o próximo
                if (payer.getValue() == 0) {
                    payerIndex++;
                }
            }
        }

        DivisionResult result = new DivisionResult();
        result.setTransfers(transfers);
        result.setTotalProfit(totalProfit);
        result.setProfitPerPlayer(profitPerPlayer);
        result.setSession(session);

        return result;
    }
}
