package fr.istic.tlc.resources;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import fr.istic.tlc.dao.ChoiceRepository;
import fr.istic.tlc.dao.CommentRepository;
import fr.istic.tlc.dao.MealPreferenceRepository;
import fr.istic.tlc.dao.PollRepository;
import fr.istic.tlc.dao.UserRepository;
import fr.istic.tlc.domain.Choice;
import fr.istic.tlc.domain.Comment;
import fr.istic.tlc.domain.MealPreference;
import fr.istic.tlc.domain.Poll;
import fr.istic.tlc.domain.User;
import fr.istic.tlc.dto.ChoiceUser;

@Path("/api/poll")
public class PollResourceEx {

	@Inject
	PollRepository pollRep;

	@Inject
	UserRepository userRep;

	@Inject
	ChoiceRepository choiceRep;

	@Inject
	MealPreferenceRepository mealprefRep;

	@Inject
	CommentRepository commentRep;

	@Path("/slug/{slug}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Poll getPollBySlug(@PathParam("slug") String slug) {
		Poll p = pollRep.findBySlug(slug);
		if (p != null)
			p.getPollComments().clear();
//		p.getPollChoices().forEach(c -> c.getUsers().clear());
		return p;
	}

	@Path("/aslug/{aslug}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Poll getPollByASlug(@PathParam("aslug") String aslug) {
		return pollRep.findByAdminSlug(aslug);
	}

	@Path("/comment/{slug}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public Comment createComment4Poll(@PathParam("slug") String slug, Comment c) {
		this.commentRep.persist(c);
		Poll p = pollRep.findBySlug(slug);
		p.addComment(c);
		this.pollRep.persistAndFlush(p);
		return c;

	}

	@Path("/update")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public Poll updatePoll(Poll p) {
		Poll p1 = pollRep.findById(p.getId());
		List<Choice> choicesToRemove = new ArrayList<Choice>();
		for (Choice c : p1.getPollChoices()) {
			if (!p.getPollChoices().contains(c)) {

				choicesToRemove.add(c);
				System.err.println("toremove " + c.getId());
			}

		}
		for (Choice c : p.getPollChoices()) {
			if (c.getId() != null) {
				this.choiceRep.getEntityManager().merge(c);
				System.err.println(c.getstartDate().toLocaleString());
				System.err.println(c.getendDate().toLocaleString());
			} else {
				this.choiceRep.getEntityManager().persist(c);
			}
			/*
			 * else { this.choiceRep.getEntityManager().merge(c); }
			 */
		}
		for (Choice c : choicesToRemove) {
			if (c.equals(p1.getSelectedChoice())) {
				p.setSelectedChoice(null);
				p1.setSelectedChoice(null);
				p.setClos(false);
			}
			for (User u : c.getUsers()) {
				u.getUserChoices().remove(c);
			}
			c.getUsers().clear();
			this.choiceRep.delete(c);

		}

		for (Choice c : p.getPollChoices()) {
			System.err.println("tomerge " + c.getId());
		}

		Poll p2 = this.pollRep.getEntityManager().merge(p);
		return p2;

	}

	@Path("/choiceuser")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public User addChoiceUser(ChoiceUser userChoice) {
		User u = this.userRep.find("mail", userChoice.getMail()).firstResult();
		if (u == null) {
			u = new User();
			u.setUsername(userChoice.getUsername());
			u.setIcsurl(userChoice.getIcs());
			u.setMail(userChoice.getMail());
			this.userRep.persist(u);
		}
		

		if (userChoice.getPref() != null && !"".equals(userChoice.getPref())) {
			MealPreference mp = new MealPreference();
			mp.setContent(userChoice.getPref());
			mp.setUser(u);
			this.mealprefRep.persist(mp);
		}
		for (Long choiceId : userChoice.getChoices()) {
			Choice c = this.choiceRep.findById(choiceId);
			c.addUser(u);
			this.choiceRep.persistAndFlush(c);
		}
		return u;
	}

	@Path("/selectedchoice/{choiceid}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public void closePoll(@PathParam("choiceid") String choiceid) {
		Choice c = choiceRep.findById(Long.parseLong(choiceid));
		Poll p = this.pollRep.find("select p from Poll as p join p.pollChoices as c where c.id= ?1", c.getId())
				.firstResult();
		p.setClos(true);
		p.setSelectedChoice(c);
		this.pollRep.persist(p);
		// TODO Send Email

	}

	@GET()
	@Path("polls/{slug}/comments")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comment> getAllCommentsFromPoll(@PathParam("slug") String slug) {
		// On vérifie que le poll existe
		Poll p = this.pollRep.findBySlug(slug);
		return p.getPollComments();
	}

}
