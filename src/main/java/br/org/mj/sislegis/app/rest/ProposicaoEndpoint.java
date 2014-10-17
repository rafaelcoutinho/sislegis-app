package br.org.mj.sislegis.app.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import br.org.mj.sislegis.app.model.Proposicao;

/**
 * 
 */
@Stateless
@Path("/proposicaos")
public class ProposicaoEndpoint
{
   @PersistenceContext(unitName = "sislegis-app-persistence-unit")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(Proposicao entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(ProposicaoEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Proposicao entity = em.find(Proposicao.class, id);
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      em.remove(entity);
      return Response.noContent().build();
   }

   @GET
   @Path("/{id:[0-9][0-9]*}")
   @Produces("application/json")
   public Response findById(@PathParam("id") Long id)
   {
      TypedQuery<Proposicao> findByIdQuery = em.createQuery("SELECT DISTINCT p FROM Proposicao p LEFT JOIN FETCH p.listaReuniao WHERE p.id = :entityId ORDER BY p.id", Proposicao.class);
      findByIdQuery.setParameter("entityId", id);
      Proposicao entity;
      try
      {
         entity = findByIdQuery.getSingleResult();
      }
      catch (NoResultException nre)
      {
         entity = null;
      }
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/json")
   public List<Proposicao> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult)
   {
      TypedQuery<Proposicao> findAllQuery = em.createQuery("SELECT DISTINCT p FROM Proposicao p LEFT JOIN FETCH p.listaReuniao ORDER BY p.id", Proposicao.class);
      if (startPosition != null)
      {
         findAllQuery.setFirstResult(startPosition);
      }
      if (maxResult != null)
      {
         findAllQuery.setMaxResults(maxResult);
      }
      final List<Proposicao> results = findAllQuery.getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(Proposicao entity)
   {
      try
      {
         entity = em.merge(entity);
      }
      catch (OptimisticLockException e)
      {
         return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
      }

      return Response.noContent().build();
   }
}