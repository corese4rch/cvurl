package coresearch.cvurl.io.sse;

import coresearch.cvurl.io.helper.model.User;
import coresearch.cvurl.io.mapper.MapperFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InboundServerEventTest {

    @Test
    public void whenDataContainsUserInJson_returnParsedUserObject() {
        final User user = new User("name", 18);
        final String data = MapperFactory.createDefault().writeValue(user);

        final InboundServerEvent event = new InboundServerEvent(null, null, data, -1, MapperFactory.createDefault());

        Assertions.assertEquals(user, event.parseData(User.class));
    }

}