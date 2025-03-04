package io.github.svapollo.quarkussocial.domain.repository;

import io.github.svapollo.quarkussocial.domain.model.Follower;
import io.github.svapollo.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean isFollowing(User follower, User user){

        var params = Parameters.with("follower", follower)
                .and("user", user).map();

        PanacheQuery<Follower> query = find("follower = :follower and user =:user", params);
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId){
        var query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        var params = Parameters
                .with("userId", userId)
                .and("followerId", followerId)
                .map();

        delete("follower.id = :followerId and user.id = :userId", params);
    }
}
