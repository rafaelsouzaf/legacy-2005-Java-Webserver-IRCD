package chatmania.extra.mail;

import chatmania.extra.log.LogIRCd;
import chatmania.ircd.Config;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

public class Mail {

    private String para = "";
    private String assunto = "";
    private String mensagem = "";

    public Mail(String para, String assunto, String mensagem) {

        this.para = para;
        this.assunto = assunto;
        this.mensagem = mensagem;

    }

    public boolean envia() {

        try {

            Properties prop = new Properties();
            prop.put("mail.smtp.host", MailConfig.HOST);
            prop.put("mail.smtp.port", "" + MailConfig.PORT);
            Session session = Session.getDefaultInstance(prop, null);

            // Construct the message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(Config.EMAIL, Config.NETWORK));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(para));
            msg.setSentDate(new Date());
            msg.setSubject(assunto);
            msg.setText(mensagem);

            // Send the message
            Transport.send(msg);

            return true;

        } catch (AddressException e) {

            new LogIRCd("Mail.java", "envia()", "" + e + "");

        } catch (MessagingException e) {

            new LogIRCd("Mail.java", "envia()", "" + e + "");

        } catch (UnsupportedEncodingException e) {

            new LogIRCd("Mail.java", "envia()", "" + e + "");

        }

        return false;

    }

}
