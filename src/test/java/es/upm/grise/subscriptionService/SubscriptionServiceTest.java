package es.upm.grise.subscriptionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import es.upm.grise.subscriptionService.exceptions.*;

public class SubscriptionServiceTest {

	private SubscriptionService service;
	private EmailService emailService;

	@BeforeEach
	public void setUp() {
		emailService = mock(EmailService.class);
		service = new SubscriptionService(emailService);
	}

	@Test
	public void testAddSubscriber() throws Exception {
		User userEmail = new User(Delivery.EMAIL, "test@email.com");
		User userLocalNoEmail = new User(Delivery.LOCAL, null);
		User userNoEmail = new User(Delivery.EMAIL, null);

		assertDoesNotThrow(() -> service.addSubscriber(userEmail));
		assertDoesNotThrow(() -> service.addSubscriber(userLocalNoEmail));

		assertThrows(NullUserException.class, () -> service.addSubscriber(null));
		assertThrows(ExistingUserException.class, () -> service.addSubscriber(userEmail));
		assertThrows(UserDoesNotHaveEmailException.class, () -> service.addSubscriber(userNoEmail));
	}

	@Test
	public void testRemoveSubscriber() throws Exception {
		User user = new User(Delivery.EMAIL, "test@email.com");

		assertThrows(NullUserException.class, () -> service.removeSubscriber(null));
		assertThrows(NonExistingUserException.class, () -> service.removeSubscriber(user));

		service.addSubscriber(user);
		assertDoesNotThrow(() -> service.removeSubscriber(user));
		assertThrows(NonExistingUserException.class, () -> service.removeSubscriber(user));
	}

	@Test
	public void testSendMessage() throws Exception {
		User emailUser = new User(Delivery.EMAIL, "email@test.com");
		User localUser = new User(Delivery.LOCAL, "local@test.com");
		User noDeliverUser = new User(Delivery.DO_NOT_DELIVER, "nodeliver@test.com");

		service.addSubscriber(emailUser);
		service.addSubscriber(localUser);
		service.addSubscriber(noDeliverUser);

		Message message = new Message(1, "Test Message");
		int discarded = service.sendMessage(message);

		verify(emailService).sendMessage(emailUser, message);
		assertTrue(localUser.messageExists(message));
		assertEquals(1, discarded);
	}
}