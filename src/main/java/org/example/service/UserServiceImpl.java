package org.example.service;

import net.bytebuddy.utility.RandomString;
import org.example.model.UserDtls;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncode;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public UserDtls createUser(UserDtls user, String url) {
        user.setPassword(passwordEncode.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnabled(false);
        RandomString rn = new RandomString();
        user.setVerificationCode(rn.make(64));
        UserDtls us  = userRepo.save(user);
        sendVerificationMail(user, url);
        return  us;
    }

    @Override
    public boolean checkEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public boolean verifyAccount(String code) {
        UserDtls user = userRepo.findByVerificationCode(code);
        if (user!=null) {
            user.setEnabled(true);
            user.setAccountNotLocked(true); //эту строку сам добавил
            user.setVerificationCode(null);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    public void sendVerificationMail(UserDtls user, String url) {
        String from = "bvseller.info@gmail.com";
        String to = user.getEmail();
        String subject = "Подтвердите почтовый ящик";
        String content = "Здравствуйте, [[name]],<br>" +
                "Пожалуйста, пройдите по ссылке ниже для подтверждения регистрации:<br>" +
                "<h3><a href=\"[[URL]]\" target=\"_self\">Подтверждение регистрации</a></h3>" +
                "Спасибо,<br>" +
                "BVSeller";

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from, "Bonusver");
            helper.setTo(to);
            helper.setSubject(subject);
            content = content.replace("[[name]]", user.getFullName());
            String siteUrl = url + "/verify?code=" + user.getVerificationCode();
            content = content.replace("[[URL]]", siteUrl);
            helper.setText(content, true);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forgottenPass (UserDtls user) {
        String from = "bvseller.info@gmail.com";
        String to = user.getEmail();
        String subject = "Сброс пароля";
        String content = "Здравствуйте, [[name]],<br>" +
                "Пожалуйста, пройдите по ссылке ниже для сброса пароля:<br>" +
                "<h3><a href=\"https://bvseller.ru/loadForgotPassword\">Сброс пароля</a></h3>" +
                "Спасибо,<br>" +
                "BVSeller";

        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from, "Bonusver");
            helper.setTo(to);
            helper.setSubject(subject);
            content = content.replace("[[name]]", user.getFullName());
            helper.setText(content, true);
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
