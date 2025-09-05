package br.com.erudio.mail;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import br.com.erudio.config.EmailConfig;
import br.com.erudio.exception.EmailSendException;
import br.com.erudio.exception.InvalidEmailAddressException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailSender implements Serializable {
	
	private static final long serialVersionUID = 1L;

	Logger log = LoggerFactory.getLogger(EmailSender.class);

	private final JavaMailSender mailSender;
	private String to;
	private String subject;
	private String body;
	private ArrayList<InternetAddress> recipients = new ArrayList<InternetAddress>();
	private File attachment;

	public EmailSender(JavaMailSender mailSender) {
		super();
		this.mailSender = mailSender;
	}

	public EmailSender to(String to) throws AddressException {
		this.to = to;
		this.recipients = getRecipients(to);
		return this;
	}

	public EmailSender withSubject(String subject) {
		this.subject = subject;
		return this;
	}

	public EmailSender withMessage(String body) {
		this.body = body;
		return this;
	}

	public EmailSender attach(String  fileDir) {
		this.attachment = new File(fileDir);
		return this;
	}
	
	public void send(EmailConfig config) throws MessagingException, EmailSendException{
		 // Cria uma nova mensagem MIME (suporta HTML, anexos, múltiplos destinatários, etc.)
		MimeMessage message = mailSender.createMimeMessage();
		try {
			// Helper para facilitar configuração da mensagem (multipart=true permite anexos)
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			// Define o remetente do email usando o username configurado
			helper.setFrom(config.getUserName());
			
			// Define os destinatários do email
	        // 'recipients' é uma lista de InternetAddress convertida para array
			helper.setTo(recipients.toArray(new InternetAddress[0]));
			
			// Define o assunto do email
			helper.setSubject(subject);
			
			// Define o corpo do email, com segundo parâmetro 'true' indicando que o conteúdo é HTML
			helper.setText(body, true);
			
			// Se houver anexo definido, adiciona na mensagem
			if(attachment !=null) {
				helper.addAttachment(attachment.getName(), attachment);
			}
			// Envia o email de fato
			mailSender.send(message);
			log.info("Email send To %s with the subject '%s'%n", to, subject);
			
			reset();
		} catch (MessagingException e) {
			
			throw new EmailSendException("Erro ao enviar email para ", e);
		}
		
	}

	private void reset() {
		this.to = null;
		this.subject = null;
		this.body = null;
		this.recipients = null;
		this.attachment = null;
		
	}

	private ArrayList<InternetAddress> getRecipients(String to) throws AddressException {
		// Remove espaços em branco da string de destinatários
		String toWithoutSpaces = to.replaceAll("\\s", "");
		// Quebra a string em "tokens" separados por espaços (cada token seria um email)
		StringTokenizer tok = new StringTokenizer(toWithoutSpaces);
		// Cria a lista que vai armazenar os endereços de email já validados
		ArrayList<InternetAddress> recipientsList = new ArrayList<InternetAddress>();
		// Percorre cada token (email) encontrado
		while (tok.hasMoreElements()) {
			String email = tok.nextElement().toString();
			try {
				// Tenta criar um objeto InternetAddress (valida o formato do email
				// automaticamente)
				recipientsList.add(new InternetAddress(email));
			} catch (AddressException e) {
				throw new InvalidEmailAddressException("Email invalid : " + email, e);
			}
		}
		return recipientsList;
	}
}
