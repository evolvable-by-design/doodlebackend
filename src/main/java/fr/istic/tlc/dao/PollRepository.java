package fr.istic.tlc.dao;

import javax.enterprise.context.ApplicationScoped;

import fr.istic.tlc.domain.Poll;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PollRepository implements PanacheRepository<Poll> {
    
	public Poll findBySlug(String slug){
		return find("slug", slug).firstResult();	
	}
}
