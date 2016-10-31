package com.mszostok.service;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import com.mszostok.DemoApplication;
import com.mszostok.Integration;
import com.mszostok.domain.User;
import net.jodah.concurrentunit.Waiter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Category(Integration.class)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MailServiceTest {

  private static final Integer SERVER_PORT = 2222;
  private final Waiter waiter = new Waiter();

  @Autowired
  private MailService mailService;

  @BeforeClass
  public static void setUp() {
    System.setProperty("spring.mail.port", SERVER_PORT.toString());
  }

  @Test
  public void testSendActivationEmailHappyPath() throws Exception {
    // given
    User user = new User();
    user.setEmail("receiver@there.com");
    user.setFirstName("firstName");

    try (SimpleSmtpServer dumbster = SimpleSmtpServer.start(SERVER_PORT)) {

      // when
      new Thread(() -> {
        try {
          mailService.sendActivationEmail(user, "baseUrl").get();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
        waiter.assertTrue(true);
        waiter.resume();
      }).start();


      // then
      waiter.await(10000);

      List<SmtpMessage> emails = dumbster.getReceivedEmails();
      assertThat(emails).hasSize(1);

      SmtpMessage email = emails.get(0);
      assertThat(email.getHeaderValue("Subject")).isEqualTo("Account activation");
      assertThat(email.getBody()).contains("Dear firstName", "Thank you for registering. ");
      assertThat(email.getHeaderValue("To")).isEqualTo("receiver@there.com");
      assertThat(email.getHeaderValue("To")).isEqualTo("receiver@there.com");
    }
  }

}
