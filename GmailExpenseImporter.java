package com.expensetracker.gmail;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fetches recent bank/transaction emails from Gmail and converts them into Expense objects.
 *
 * Setup before running:
 * 1. Rename your downloaded OAuth file to credentials.json
 * 2. Put it in src/main/resources/credentials.json
 * 3. Update GMAIL_SEARCH_QUERY below to match your bank's sender address
 * 4. Run this file. A browser window opens once for you to approve Gmail access.
 */
public class GmailExpenseImporter {

    // Narrow this to your bank's actual sender to avoid pulling in unrelated emails.
    // Example: "from:alerts@yourbank.com"
    private static final String GMAIL_SEARCH_QUERY = "subject:(transaction OR debited OR payment) newer_than:30d";

    private static final int MAX_RESULTS = 50;

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        Gmail service = GmailAuth.getGmailService();
        ExpenseParser parser = new ExpenseParser();

        List<Expense> expenses = fetchExpenses(service, parser);

        System.out.println("Imported " + expenses.size() + " expense(s):");
        for (Expense e : expenses) {
            System.out.println(e);
        }
    }

    public static List<Expense> fetchExpenses(Gmail service, ExpenseParser parser) throws IOException {
        List<Expense> results = new ArrayList<>();

        ListMessagesResponse response = service.users().messages()
                .list("me")
                .setQ(GMAIL_SEARCH_QUERY)
                .setMaxResults((long) MAX_RESULTS)
                .execute();

        List<Message> messages = response.getMessages();
        if (messages == null || messages.isEmpty()) {
            return results;
        }

        for (Message m : messages) {
            Message fullMessage = service.users().messages().get("me", m.getId()).execute();
            String bodyText = extractPlainText(fullMessage);
            LocalDate date = Instant.ofEpochMilli(fullMessage.getInternalDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            Expense expense = parser.parse(bodyText, date);
            if (expense != null) {
                results.add(expense);
            }
        }

        return results;
    }

    private static String extractPlainText(Message message) {
        StringBuilder text = new StringBuilder();

        MessagePart payload = message.getPayload();
        if (payload == null) {
            return "";
        }

        // Include subject for parsing too
        if (payload.getHeaders() != null) {
            String subject = payload.getHeaders().stream()
                    .filter(h -> "Subject".equalsIgnoreCase(h.getName()))
                    .map(MessagePartHeader::getValue)
                    .collect(Collectors.joining());
            text.append(subject).append(" ");
        }

        collectBodyText(payload, text);
        return text.toString();
    }

    private static void collectBodyText(MessagePart part, StringBuilder text) {
        if (part.getBody() != null && part.getBody().getData() != null
                && "text/plain".equalsIgnoreCase(part.getMimeType())) {
            byte[] decoded = Base64.decodeBase64(part.getBody().getData());
            text.append(new String(decoded)).append(" ");
        }
        if (part.getParts() != null) {
            for (MessagePart child : part.getParts()) {
                collectBodyText(child, text);
            }
        }
    }
}
