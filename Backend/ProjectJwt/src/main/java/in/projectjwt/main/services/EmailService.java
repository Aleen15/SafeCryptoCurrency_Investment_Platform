package in.projectjwt.main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



@Service
public class EmailService {
	
	@Autowired
    private JavaMailSender mailSender;
	
	@Value("${spring.mail.username")
	private String fromEmailId;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }



	public void sendOtpEmail(String toEmail, String otp) {
		// TODO Auto-generated method stub
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmailId);
	        message.setTo(toEmail);
	        message.setSubject("Your OTP Code");
	        message.setText("Your OTP code is: " + otp);
	        mailSender.send(message);
	        System.out.println("Email sent successfully to " + toEmail);
			
		}
		catch(Exception e) {
			System.out.println("Error in sending email: " + e.getMessage());
            e.printStackTrace();
			
		}
		
	}
	
	public void sendStockPurchaseEmail(String toEmail, String userName, String stockSymbol, int noOfShares, double purchasePrice) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@SafeCryptoStocks.com");
        message.setTo(toEmail);
        message.setSubject("Stock Purchase Confirmation");

        String text = String.format("Hello %s,\n\n" +
                                    "Your stock purchase was successful!\n\n" +
                                    "Stock: %s\n" +
                                    "Number of Shares: %d\n" +
                                    "Total Purchase Price: $%.2f\n\n" +
                                    "Thank you for using our service.\n\n" +
                                    "Best regards,\n" +
                                    "SafeCryptoStocks", userName, stockSymbol, noOfShares, purchasePrice);
        
        message.setText(text);
        
        mailSender.send(message);
    }
	 // New method for sending stock sale email notification
    public void sendStockSaleEmail(String toEmail, String userName, String stockSymbol, int quantitySell, double saleValue, double profitOrLoss) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@SafeCryptoStocks.com");
        message.setTo(toEmail);
        message.setSubject("Stock Sale Confirmation");

        String text = String.format("Hello %s,\n\n" +
                                    "Your stock sale was successful!\n\n" +
                                    "Stock: %s\n" +
                                    "Number of Shares Sold: %d\n" +
                                    "Total Sale Value: $%.2f\n" +
                                    "Profit/Loss from Sale: $%.2f\n\n" +
                                    "Thank you for using our service.\n\n" +
                                    "Best regards,\n" +
                                    "SafeCryptoStocks", userName, stockSymbol, quantitySell, saleValue, profitOrLoss);
        
        message.setText(text);
        
        mailSender.send(message);
    }

}
