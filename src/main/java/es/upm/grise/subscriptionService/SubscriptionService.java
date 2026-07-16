package es.upm.grise.subscriptionService;

import java.util.ArrayList;
import java.util.Collection;

import es.upm.grise.subscriptionService.exceptions.ExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NonExistingUserException;
import es.upm.grise.subscriptionService.exceptions.NullUserException;
import es.upm.grise.subscriptionService.exceptions.UserDoesNotHaveEmailException;

public class SubscriptionService {

	private Collection <User> subscribers;
	private EmailService emailService;
	
	/* 
	 * Constructor
	 */
	
	public SubscriptionService(EmailService emailService) {
		subscribers = new ArrayList<User>();
		this.emailService = emailService;
	}

	/* 
	 * Method to code/test
	 */

	public void addSubscriber(User user) throws NullUserException, ExistingUserException, UserDoesNotHaveEmailException {
		if(user==null){
			throw new NullUserException();
		}

		if(subscribers.contains(user)){
			throw new ExistingUserException();
		}

		if(user.getEmail() == null){
			throw new UserDoesNotHaveEmailException();
		}

		if(user.getDeliveryType() == Delivery.LOCAL){
			subscribers.add(user);	
		}

		subscribers.add(user);	
	}
	
	/* 
	 * Method to code/test
	 */
	
	public void removeSubscriber(User user) throws NullUserException, NonExistingUserException {
		if(user == null){
			throw new NullUserException();
		}
		if(!subscribers.contains(user)){
			throw new NonExistingUserException();
		}

		subscribers.remove(user);
	}
	
	/* 
	 * Method to code/test
	 */
	
	public int sendMessage(Message message) {
		int discardedMessages = 0;
		for (User user : subscribers) {
			if (user.getDeliveryType() == Delivery.LOCAL) {
				user.saveMessage(message);
			} else if (user.getDeliveryType() == Delivery.DO_NOT_DELIVER) {
				Message discardedMessage = new Message(message.getId(), "Ha perdido ud. un mensaje");
				user.saveMessage(discardedMessage);
				discardedMessages++;
			}
		}
		return discardedMessages;
		
	}

}
