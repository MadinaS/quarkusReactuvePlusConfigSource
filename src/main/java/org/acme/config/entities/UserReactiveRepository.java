package org.acme.config.entities;


import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;


@RequestScoped
@Transactional
public class UserReactiveRepository {

    @Inject
    MySQLPool client;



    public Uni<UserEntity> findByChatKey(String chatToken) {

        return client.preparedQuery("SELECT * FROM user WHERE chat_token = ?").execute(Tuple.of(chatToken))
                .onItem().apply(RowSet::iterator)
                .onItem().apply(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    private static UserEntity from(Row row) {
        return new UserEntity(row.getString("user_nickname"), row.getString("chat_token"));
    }

}
