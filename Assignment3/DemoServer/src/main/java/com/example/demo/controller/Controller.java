package com.example.demo.controller;

import com.example.demo.controller.request.Request;
import com.example.demo.dto.AuthorsDTO;
import com.example.demo.dto.BooksDTO;
import com.example.demo.service.ServiceInterface;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.stream.Collectors;

@Path("/book-store")
public class Controller {

    @EJB
    private ServiceInterface serviceInterface;

    @Path("/addNewBook")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewBook(Request request, @Context UriInfo uriInfo){
        List<AuthorsDTO> authors = request.getAuthors()
                .stream()
                        .map(authorRequest -> new AuthorsDTO(authorRequest.getAuthorName()))
                                .collect(Collectors.toList());

        serviceInterface.createNewBookAndAuthor(request.getTitle(),
                request.getPublishedYear(),
                request.getPrice(),
                request.getImage(),
                authors);
        return Response.ok("Added").build();
    }

    @GET
    @Path("/all-books")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BooksDTO> getAllBooks(){
       return serviceInterface.getAllBooks();
    }

    @GET
    @Path("/get-books/{authorName}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BooksDTO> getBooksOfAuthor(@PathParam("authorName") String authorName){
        return serviceInterface.findBooksByAuthor(authorName);
    }

    @GET
    @Path("get-authors/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AuthorsDTO> getAuthorsOfBook(@PathParam("title") String title){
        return serviceInterface.findAuthorsOfBooks(title);
    }

    @PUT
    @Path("/update-book")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBookInformation(Request request, @Context UriInfo uriInfo) {
        List<AuthorsDTO> authors = request.getAuthors()
                .stream()
                .map(authorRequest -> new AuthorsDTO(authorRequest.getAuthorName()))
                .collect(Collectors.toList());
        serviceInterface.updateBook(request.getTitle(),
                request.getPublishedYear(),
                request.getPrice(),
                request.getImage(),
                authors);
        return Response.ok("Updated").build();
    }

    @DELETE
    @Path("/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBook(@PathParam("title") String title){
        serviceInterface.deleteBook(title);
        return Response.status(Response.Status.OK.getStatusCode()).build();
    }
}
