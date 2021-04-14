package com.restapi.messenger.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
/*
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
*/
import com.restapi.messenger.model.Message;
import com.restapi.messenger.resources.beans.MessageFilterBean;
import com.restapi.messenger.service.MessageService;


import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;



@Path("/messages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

	//@Context
	MessageService messageService = new MessageService();
	
	@GET
	@Path("/allMessages")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Message> getMessage(){
		  System.out.println("json method called");
		  List<Message> msgs = messageService.getAllMessages();
		  return msgs ;
	}
	
	@GET
	@Path("/allMessages")
	@Produces(MediaType.TEXT_XML)
	public List<Message> getMessage_XML(){
		  System.out.println("xml method called");
		  List<Message> msgs = messageService.getAllMessages();
		  return msgs ;
		  
		// List<Message> list = new ArrayList<Message>();
			// list = messageService.getAllMessages();
			// GenericEntity<List<Message>> entity = new GenericEntity<List<Message>>(list){};
		   //  return Response.ok(entity).build();
	}
	
	/*
	https://dennis-xlc.gitbooks.io/restful-java-with-jax-rs-2-0-2rd-edition/content/en/part1/chapter7/complex_responses.html
	@GET
	@Produces("application/xml")
	public Response getCustomerList() {
	    List<Customer> list = new ArrayList<Customer>();
	    list.add(new Customer(...));

	    GenericEntity entity = new GenericEntity<List<Customer>>(list){};
	    return Response.ok(entity).build();
	}  */
	
	
	// http://localhost:8080/messenger-test/webapi/messages/testtext/
	@GET
	@Path("/testtext")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		
		return "test text plain return";
	}
	
	
	@GET
	public List<Message> getMessages(@BeanParam MessageFilterBean filterBean) {
		
		if (filterBean.getYear() > 0) {
			return messageService.getAllMessagesForYear(filterBean.getYear());
		}
		if (filterBean.getStart() >= 0 && filterBean.getSize() > 0) {
			return messageService.getAllMessagesPaginated(filterBean.getStart(), filterBean.getSize());
		}
		return messageService.getAllMessages();
	}

	@POST
	public Response addMessage(Message message, @Context UriInfo uriInfo) {
		
		Message newMessage = messageService.addMessage(message);
		String newId = String.valueOf(newMessage.getId());
		URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
		return Response.created(uri)
				.entity(newMessage)
				.build();
	}
	
	@PUT
	@Path("/{messageId}")
	public Message updateMessage(@PathParam("messageId") long id, Message message) {
		message.setId(id);
		return messageService.updateMessage(message);
	}
	
	@DELETE
	@Path("/{messageId}")
	public void deleteMessage(@PathParam("messageId") long id) {
		messageService.removeMessage(id);
	}
	
	
	@GET
	@Path("/{messageId}")
	public Message getMessage(@PathParam("messageId") long id, @Context UriInfo uriInfo) {
		Message message = messageService.getMessage(id);
		message.addLink(getUriForSelf(uriInfo, message), "self");
		message.addLink(getUriForProfile(uriInfo, message), "profile");
		message.addLink(getUriForComments(uriInfo, message), "comments");
		
		return message;
		
	}

	private String getUriForComments(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder()
				.path(MessageResource.class)
	       		.path(MessageResource.class, "getCommentResource")
	       		.path(CommentResource.class)
	       		.resolveTemplate("messageId", message.getId())
	            .build();
	    return uri.toString();
	}

	private String getUriForProfile(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder()
       		 .path(ProfileResource.class)
       		 .path(message.getAuthor())
             .build();
        return uri.toString();
	}

	private String getUriForSelf(UriInfo uriInfo, Message message) {
		String uri = uriInfo.getBaseUriBuilder()
		 .path(MessageResource.class)
		 .path(Long.toString(message.getId()))
		 .build()
		 .toString();
		return uri;
	}
	
	
	
	
	@Path("/{messageId}/comments")
	public CommentResource getCommentResource() {
		return new CommentResource();
	}
	
	
}
