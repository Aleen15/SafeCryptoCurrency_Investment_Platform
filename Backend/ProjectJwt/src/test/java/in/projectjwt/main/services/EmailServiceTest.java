package in.projectjwt.main.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String fromEmailId = "test@example.com";  // Mock email ID for testing

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendOtpEmail() {
        // Test data
        String toEmail = "user@example.com";
        String otp = "123456";

        // Call the method
        emailService.sendOtpEmail(toEmail, otp);

        // Verify that JavaMailSender's send method was called with the correct SimpleMailMessage
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

        // Capture the SimpleMailMessage and assert its content
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertEquals("test@example.com", capturedMessage.getFrom());  // check the sender email
        assertEquals(toEmail, capturedMessage.getTo()[0]);  // check recipient email
        assertEquals("Your OTP Code", capturedMessage.getSubject());  // check subject
        assertTrue(capturedMessage.getText().contains(otp));  // check that OTP is in the body of the message
    }

    @Test
    void testSendStockPurchaseEmail() {
        // Test data
        String toEmail = "user@example.com";
        String userName = "John Doe";
        String stockSymbol = "AAPL";
        int noOfShares = 10;
        double purchasePrice = 1500.00;

        // Call the method
        emailService.sendStockPurchaseEmail(toEmail, userName, stockSymbol, noOfShares, purchasePrice);

        // Verify that JavaMailSender's send method was called with the correct SimpleMailMessage
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

        // Capture the SimpleMailMessage and assert its content
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertEquals("no-reply@SafeCryptoStocks.com", capturedMessage.getFrom());  // check the sender email
        assertEquals(toEmail, capturedMessage.getTo()[0]);  // check recipient email
        assertEquals("Stock Purchase Confirmation", capturedMessage.getSubject());  // check subject
        assertTrue(capturedMessage.getText().contains(stockSymbol));  // check that stock symbol is in the message
        assertTrue(capturedMessage.getText().contains(String.valueOf(noOfShares)));  // check that number of shares is in the message
        assertTrue(capturedMessage.getText().contains(String.format("$%.2f", purchasePrice)));  // check that purchase price is in the message
    }

    @Test
    void testSendStockSaleEmail() {
        // Test data
        String toEmail = "user@example.com";
        String userName = "John Doe";
        String stockSymbol = "AAPL";
        int quantitySell = 5;
        double saleValue = 800.00;
        double profitOrLoss = 50.00;

        // Call the method
        emailService.sendStockSaleEmail(toEmail, userName, stockSymbol, quantitySell, saleValue, profitOrLoss);

        // Verify that JavaMailSender's send method was called with the correct SimpleMailMessage
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

        // Capture the SimpleMailMessage and assert its content
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertEquals("no-reply@SafeCryptoStocks.com", capturedMessage.getFrom());  // check the sender email
        assertEquals(toEmail, capturedMessage.getTo()[0]);  // check recipient email
        assertEquals("Stock Sale Confirmation", capturedMessage.getSubject());  // check subject
        assertTrue(capturedMessage.getText().contains(stockSymbol));  // check that stock symbol is in the message
        assertTrue(capturedMessage.getText().contains(String.valueOf(quantitySell)));  // check that number of shares sold is in the message
        assertTrue(capturedMessage.getText().contains(String.format("$%.2f", saleValue)));  // check that sale value is in the message
        assertTrue(capturedMessage.getText().contains(String.format("$%.2f", profitOrLoss)));  // check that profit or loss is in the message
    }
}
