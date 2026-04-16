package com.example.service;


import com.example.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Value("${spring.mail.username}")
    private String fromAccount;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SmsHistoryService smsHistoryService;

    public void sendRegistrationEmail(String toAccount) {
        Integer code = RandomUtil.fiveDigit();

        String body = "Sarhisob - ro'yxatdan o'tish uchun tasdiqlash kodi: " + code;

        smsHistoryService.save(toAccount, body, String.valueOf(code));
        sendSimpleMessage(toAccount, "Tasdiqlash kodi", body);
    }

    public String sendSimpleMessage(String toAccount, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAccount);
        msg.setTo(toAccount);
        msg.setSubject(subject);
        msg.setText(text);
        javaMailSender.send(msg);

        return "Mail was sent";
    }
}