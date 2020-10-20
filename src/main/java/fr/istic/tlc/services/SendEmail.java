package fr.istic.tlc.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;

@ApplicationScoped
public class SendEmail {
	
	@Inject
	Mailer mailer;
	
	public void sendASimpleEmail(String to, String subject, String body) {
	    mailer.send(Mail.withText(to,subject,body));
	}


}
