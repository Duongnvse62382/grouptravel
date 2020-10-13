package com.fpt.gta.config;

import com.fpt.gta.model.service.AuthenticationService;
import com.fpt.gta.model.service.DocumentService;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mail.SearchTermStrategy;
import org.springframework.integration.mail.dsl.Mail;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.SearchTerm;
import java.security.GeneralSecurityException;

//@Configuration
//@EnableIntegration
public class MailConfig {
private  static final int REFRESH=60000;
    @Value("${config.mail.url}")
    public String url;

//    @Bean
//    public IntegrationFlow mailListener(@Autowired DocumentService documentService, @Autowired AuthenticationService authenticationService) throws GeneralSecurityException {
//        MailSSLSocketFactory socketFactory = new MailSSLSocketFactory();
//        socketFactory.setTrustAllHosts(true);
//
//        return IntegrationFlows.from(Mail.imapInboundAdapter(url)
//                        .javaMailProperties(p -> {
//                                    p.put("mail.imaps.ssl.trust", "*");
//                                    p.put("mail.debug", "false");
//                                    p.put("mail.imap.partialfetch", "false");
//                                    p.put("mail.imap.fetchsize", "1048576");
//                                    p.put("mail.imaps.partialfetch", "false");
//                                    p.put("mail.imaps.fetchsize", "1048576");
//                                    p.put("mail.auth", "true");
//                                }
//                        )
//                        .shouldDeleteMessages(false)
//                        .shouldMarkMessagesAsRead(true)
//                        .simpleContent(true)
//                        .searchTermStrategy(new SearchTermStrategy() {
//                            @Override
//                            public SearchTerm generateSearchTerm(Flags flags, Folder folder) {
//                                return new NotTerm(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
//                            }
//                        })
//                ,
//                e -> e.autoStartup(true).poller(Pollers.fixedDelay(REFRESH).maxMessagesPerPoll(1)))
//                .handle(message -> {
//                    try {
//                        System.err.println("Start handle message");
//                        MimeMessage mimeMessage = (MimeMessage) message.getPayload();
//                        handleMail(mimeMessage, authenticationService, documentService);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                })
//                .get();
//    }

    public void handleMail(MimeMessage message, AuthenticationService authenticationService, DocumentService documentService) throws Exception {
        System.err.println("Handling Email");
        MimeMessage mimeMessage = message;
        InternetAddress senderAddress = (InternetAddress) mimeMessage.getFrom()[0];
        InternetAddress receiverAddress = (InternetAddress) mimeMessage.getRecipients(Message.RecipientType.TO)[0];
        int idGroup;
        String idGroupStr = receiverAddress.toString();
        if (idGroupStr.startsWith("gta-uploadservice-group-") && idGroupStr.endsWith("@areyousure.xyz")) {
            idGroupStr = StringUtils.removeStart(idGroupStr, "gta-uploadservice-group-");
            idGroupStr = StringUtils.removeEnd(idGroupStr, "@areyousure.xyz");

            idGroup = Integer.parseInt(idGroupStr);
            System.err.println(senderAddress.getAddress());
            System.err.println(receiverAddress.getAddress());

            authenticationService.checkJoinedGroupByEmail(senderAddress.getAddress(), idGroup);

            Multipart multipart = (Multipart) message.getContent();
            for (int j = 0; j < multipart.getCount(); j++) {
                BodyPart bodyPart = multipart.getBodyPart(j);
                String disposition = bodyPart.getDisposition();
                if (disposition != null && Part.ATTACHMENT.equalsIgnoreCase(disposition)) { // BodyPart.ATTACHMENT doesn't work for gmail
                    DataHandler handler = bodyPart.getDataHandler();
                    String contentType = handler.getContentType().split(";")[0];
                    System.err.println(handler.getContentType());
                    byte[] contentByteArray = IOUtils.toByteArray(handler.getInputStream());
                    System.out.println("Email attachment content type:" + contentType);
                    documentService.addNewDocumentGroup(idGroup, contentType, contentByteArray);
                }
            }
        }
    }
}
