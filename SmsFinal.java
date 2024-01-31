/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.voicetry;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsFinal {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the phone number to send the message to: ");
        String toNumber = scanner.nextLine();

        System.out.print("Enter the message: ");
        String message = scanner.nextLine();

        scanner.close();
        String accountSid = "AC176bcd77ba2204797111fb4a79011599";
        String authToken = "8cb9e5de84bb6ddd97f1018cf0f61a3f";
        String trailNumber = "+15512240006";
        String jdbcUrl = "jdbc:postgresql://127.0.0.1:5432/SMS";
        String username = "postgres";
        String password = "root";

        try {
            // Send SMS using Twilio
            sendSmsWithTwilio(accountSid, authToken, trailNumber, toNumber, message);

            // Store SMS data in the database
            storeSmsData(jdbcUrl, username, password, toNumber, trailNumber, message);
        } catch (Exception e) {
            System.out.println("Failed to send SMS: " + e.getMessage());
        }
    }

    private static void sendSmsWithTwilio(String accountSid, String authToken, String trailNumber, String toNumber, String message) throws ApiException {
        try {
            Twilio.init(accountSid, authToken);
            Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(trailNumber),
                    message)
                    .create();
            System.out.println("SMS sent with Twilio.");
        } catch (ApiException e) {
            throw new ApiException("Error sending SMS with Twilio: " + e.getMessage());
        }
    }

    private static void storeSmsData(String jdbcUrl, String username, String password, String toNumber, String fromNumber, String message) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String insertQuery = "INSERT INTO sms_records (to_number, from_number, message, timestamp) VALUES (?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, toNumber);
                preparedStatement.setString(2, fromNumber);
                preparedStatement.setString(3, message);

                // Set the timestamp to the current time
                Timestamp timestamp = new Timestamp(new Date().getTime());
                preparedStatement.setTimestamp(4, timestamp);

                preparedStatement.executeUpdate();

                System.out.println("SMS record inserted into the database.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to store SMS data: " + e.getMessage());
        }
    }
}
