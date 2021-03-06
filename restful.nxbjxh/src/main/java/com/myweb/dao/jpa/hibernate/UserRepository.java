package com.myweb.dao.jpa.hibernate;

import com.myweb.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryDefinition(domainClass = User.class, idClass = Integer.class)
public interface UserRepository extends JpaRepository<User,Integer> {

    public List<User> findByIdentityAndPassword(String identity, String password);

    public List<User> findByIdentity(String identity);

    public List<User> findByIdentityAndIdNot(String identity, int id);

    public List<User> findByNameAndIdentity(String name, String identity);
}