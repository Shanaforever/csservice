package com.centricsoftware.commons.utils;

import cn.hutool.extra.mail.Mail;
import com.centricsoftware.config.entity.CsProperties;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件工具类，同类型工具类{@link cn.hutool.extra.mail.Mail},按个人喜好使用即可
 *
 * @ClassName: MailUtil
 * @Description: 发送Email
 * @author: Harry
 * @date: 2016-5-30 下午9:42:56
 *
 */
@Slf4j
public class MailUtil {



    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // sendMail("mail.bestseller.com.cn","noreply@bestseller.com.cn","PLM
        // Admin","bs_stAff_123$","harry.liang@centricsoftware.com","PO:1631P0100087C质检已完成，请给出QAC决策","PO:1631P0100087C质检已完成，请给出QAC决策<BR/>PO:1631P0100087C质检已完成，请给出QAC决策");
        testSendMail();
    }

    public static void testSendMail() throws Exception{
        CsProperties properties = NodeUtil.getProperties();
        String mailToUsers = NodeUtil.queryExpressionResult(
                "block(buyer= first(POProducts).Product.Attributes.BFGC_ProductionBuyer,buyer.Email+\";\"+buyer.BFGC_TeamLeader.Email)",
                "C14203389");
        System.out.println("mailToUsers=" + mailToUsers);
        String poNumber = "PO12390393245";
        String qaDecision = "不合格/Fail";
        String wareHouse = "050A";
        String vDecision = "不合格/Fail";
        String mDecision = "不合格/Fail";
        String comment = "不合格/Fail";
        mailToUsers = "harry.liang@centricsoftware.com;jief@bestseller.com.cn";
        try {
            if (!"".equals(mailToUsers) && !";".equals(mailToUsers)) {
                String title = "PO:" + poNumber + " 已经完成质检(" + qaDecision + ")，请尽快提供QAC决策. QA Inspection has been done("
                        + qaDecision + "), please make QAC Decision ASAP. ";
                String content = "Dear Buyer,<BR/>";
                content += "PO Number:   " + poNumber + "<BR/>";
                content += "Ware House ID:" + wareHouse + "<BR/>";
                content += "QAV Result:  " + vDecision + "<BR/>";
                content += "QAM Result:  " + mDecision + "<BR/>";
                content += "QA Decision: " + qaDecision + "<BR/>";
                content += "QA Comments: " + comment + "<BR/>";
                MailUtil.sendMail(properties.getValue("mail.host"), properties.getValue("mail.sender"),
                        properties.getValue("mail.senderName"), properties.getValue("mail.senderPwd"), mailToUsers,
                        title, content);
                System.out.println("Sent Mail to =" + mailToUsers);
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Fail to send Mail to =" + mailToUsers);
        }
    }

    public static void testMail() throws Exception {
        Properties prop = new Properties();
        prop.setProperty("mail.host", "mail.bestseller.com.cn");
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        // 使用JavaMail发送邮件的5个步骤
        // 1、创建session
        Session session = Session.getInstance(prop);
        // 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
        session.setDebug(true);
        // 2、通过session得到transport对象
        Transport ts = session.getTransport();
        // 3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
        ts.connect("mail.bestseller.com.cn", "noreply@bestseller.com.cn", "bs_stAff_123$");
        // 4、创建邮件
        Message message = createSimpleMail(session);
        // 5、发送邮件
        ts.sendMessage(message, message.getAllRecipients());
        ts.close();
    }

    /**
     * 发送邮件
     *
     * @param mailHost
     * @param sender
     * @param senderName
     * @param senderPwd
     * @param toUsers
     *            多个用户使用分号分割
     * @param title
     * @param content
     */
    public static String sendMail(String mailHost, String sender, String senderName, String senderPwd, String toUsers,
            String title, String content) {
        Properties prop = new Properties();
        prop.setProperty("mail.host", mailHost);
        prop.setProperty("mail.transport.protocol", "smtp");
        prop.setProperty("mail.smtp.auth", "true");
        Transport ts;
        String result = "success";
        try {
            // 使用JavaMail发送邮件的5个步骤
            // 1、创建session
            Session session = Session.getInstance(prop);
            // 开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
            session.setDebug(true);
            // 2、通过session得到transport对象
            ts = session.getTransport();
            // 3、使用邮箱的用户名和密码连上邮件服务器，发送邮件时，发件人需要提交邮箱的用户名和密码给smtp服务器，用户名和密码都通过验证之后才能够正常发送邮件给收件人。
            ts.connect(mailHost, sender, senderPwd);
            // 4、创建邮件
            Message message = new MimeMessage(session);
            // 指明邮件的发件人
            InternetAddress address = new InternetAddress(sender);
            address.setPersonal(senderName);
            message.setFrom(address);
            // 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
            if (toUsers.indexOf(";") >= 0) {
                String[] toUsersList = toUsers.split(";");
                for (String toUser : toUsersList) {
                    toUser = toUser.trim();
                    if (!"".equals(toUser)) {
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toUser));
                    }
                }
            } else {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(toUsers));
            }
            // 邮件的标题
            message.setSubject(title);
            // 邮件的文本内容
            message.setContent(content, "text/html;charset=UTF-8");
            // 5、发送邮件
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            log.error("", e);
            result = "fail";
        }
        return result;
    }

    /**
     * @Method: createSimpleMail
     * @Description: 创建一封只包含文本的邮件
     * @Anthor:Harry Liang
     *
     * @param session
     * @return
     * @throws Exception
     */
    public static MimeMessage createSimpleMail(Session session)
            throws Exception {
        // 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 指明邮件的发件人
        InternetAddress address = new InternetAddress("noreply@bestseller.com.cn");
        address.setPersonal("PLM Admin");
        message.setFrom(address);
        // 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("harry.liang@centricsoftware.com"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("jief@bestseller.com.cn"));
        // 邮件的标题
        message.setSubject("邮件发送个测试");
        // 邮件的文本内容
        message.setContent("你好啊！", "text/html;charset=UTF-8");
        // 返回创建好的邮件对象
        return message;
    }
}