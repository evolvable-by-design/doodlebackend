package fr.istic.tlc.resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import fr.istic.tlc.dao.ChoiceRepository;
import fr.istic.tlc.dao.PollRepository;
import fr.istic.tlc.dao.UserRepository;
import fr.istic.tlc.dto.EventDTO;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.util.JCacheTimeZoneCache;
import net.fortuna.ical4j.util.MapTimeZoneCache;

@Path("/api/ics")
public class ICSResources {
	
	@Inject
	PollRepository pollRep;

	@Inject
	UserRepository userRep;

	@Inject
	ChoiceRepository choiceRep;


	@GET
	@javax.ws.rs.Produces(MediaType.APPLICATION_JSON)
	public List<EventDTO> parseCalendartoAppointments()
			throws IOException, ParserException, InterruptedException, ExecutionException, MessagingException {
		List<EventDTO> appointments = new ArrayList<>();
		String s = "http://zimbra.inria.fr/home/olivier.barais@irisa.fr/Calendar.ics";
		System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache.class.getName());
		
		JCacheTimeZoneCache c  = null;

		CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
		client.start();
		HttpGet request = new HttpGet(s);

		Future<HttpResponse> future = client.execute(request, null);
		HttpResponse response = future.get();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		response.getEntity().writeTo(out);
		String responseString = out.toString();
		out.close();

		client.close();


		
		  StringReader sin = new StringReader(responseString);
		  
		  CalendarBuilder builder = new CalendarBuilder();
		  
		  Calendar calendar = builder.build(sin);
		 

		ComponentList<CalendarComponent> events = calendar.getComponents(Component.VEVENT);

		for (CalendarComponent event : events) {
			EventDTO a = new EventDTO();
			a.setStartDate(((VEvent) event).getStartDate().getDate());
			a.setEndDate(((VEvent) event).getEndDate().getDate());			
			if (((VEvent) event).getSummary() != null)
				a.setDescription(((VEvent) event).getSummary().getValue());
//			a.setStartDate(event.);
			appointments.add(a);
		}
		return appointments;
	}
}
