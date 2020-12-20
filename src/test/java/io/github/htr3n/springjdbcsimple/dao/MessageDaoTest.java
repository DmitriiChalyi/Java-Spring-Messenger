package io.github.htr3n.springjdbcsimple.dao;

import io.github.htr3n.springjdbcsimple.entity.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan
public class MessageDaoTest {

    private static final String TEXT_FIRST = "Alice";
    private static final String TEXT_SECOND = "Bob";
    private static final int ONE_CUSTOMER = 1;
    private static final int TWO_CUSTOMERS = 2;

    @Autowired
    private MessageDao messageDao;

    private Message alice;
    private Message bob;

    @Before
    public void setUp(){
        alice = new Message();
        alice.setText(TEXT_FIRST);

        bob = new Message();
        bob.setText(TEXT_SECOND);
    }

    @Test
    public void create_shouldReturnValidMessage_whenAddingNewMessage() {

        messageDao.create(alice);

        assertThat(alice.getId()).isNotNull();
        
        Optional<Message> result = messageDao.findById(alice.getId());

        assertThat(result).isPresent();
        assertThat(alice).hasFieldOrPropertyWithValue("text", TEXT_FIRST);
    }

    @Test
    public void findById_shouldReturnInvalidMessage_forEmptyDatabase() {
        Optional<Message> invalidMessage = messageDao.findById(new Random().nextInt());
        assertThat(invalidMessage.isPresent()).isFalse();
    }

    @Test
    public void findById_shouldReturnValidMessage_forExistingMessage() {
        messageDao.create(alice);

        Optional<Message> validMessage = messageDao.findById(alice.getId());

        assertThat(validMessage).isPresent();
        assertThat(validMessage.get().getText()).isEqualTo(alice.getText());
    }

    @Test
    public void findAll_shouldYieldEmptyList_forEmptyDatabase() {
        List<Message> noMessages = messageDao.findAll();
        assertThat(noMessages).isNullOrEmpty();
    }
    
    @Test
    public void findAll_shouldYieldListOfMessages_forNonemptyDatabase() {

        messageDao.create(alice);
        List<Message> messages = messageDao.findAll();

        assertThat(messages).isNotNull().hasSize(ONE_CUSTOMER);

        Message result = messages.get(0);

        assertThat(result).hasFieldOrPropertyWithValue("text", TEXT_FIRST);

        messageDao.create(bob);
        messages = messageDao.findAll();

        assertThat(messages).isNotNull().hasSize(TWO_CUSTOMERS);
    }

    @Test
    public void update_shouldYieldFalse_forEmptyMessage() {
        Message notFound = new Message();
        notFound.setId(new Random().nextInt());
        assertThat(messageDao.update(notFound)).isFalse();
    }

    @Test
    public void update_shouldYieldTrue_forExistingMessage() {
        messageDao.create(alice);

        assertThat(alice.getId()).isNotNull();
        assertThat(messageDao.update(alice)).isTrue();

        alice.setText(TEXT_SECOND);
        assertThat(messageDao.update(alice)).isTrue();

        Optional<Message> found = messageDao.findById(alice.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getText()).isEqualTo(alice.getText());
    }

    @Test
    public void delete_shouldYieldFalse_forEmptyDatabaseOrNonexistingMessage() {
    	assertThat(messageDao.delete(new Random().nextInt())).isFalse();
    }

    @Test
    public void delete_shouldYieldTrue_forExistingMessage() {
        messageDao.create(alice);
        assertThat(messageDao.findAll()).hasSize(ONE_CUSTOMER);
        assertThat(messageDao.delete(alice.getId())).isTrue();
        assertThat(messageDao.findById(alice.getId()).isPresent()).isFalse();
        assertThat(messageDao.findAll()).isEmpty();
    }
}
