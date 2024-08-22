package ifmt.cba.servico;

import ifmt.cba.dto.ColaboradorDTO;
import ifmt.cba.negocio.ColaboradorNegocio;
import ifmt.cba.persistencia.ColaboradorDAO;
import ifmt.cba.persistencia.FabricaEntityManager;
import ifmt.cba.persistencia.PersistenciaException;
import ifmt.cba.servico.util.Mensagem;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/colaborador")
public class ColaboradorServico {
    private static ColaboradorDAO colaboradorDAO;
    private static ColaboradorNegocio colaboradorNegocio;

    static {
        try {
            colaboradorDAO = new ColaboradorDAO(FabricaEntityManager.getEntityManagerProducao());
            colaboradorNegocio = new ColaboradorNegocio(colaboradorDAO);
        } catch (PersistenciaException e) {
            e.printStackTrace();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adicionar(ColaboradorDTO colaboradorDTO) {
        Response.ResponseBuilder resposta;
        try {
            colaboradorNegocio.inserir(colaboradorDTO);
            ColaboradorDTO colaboradorDTOTemp = colaboradorNegocio.pesquisaCPF(colaboradorDTO.getCPF());
            colaboradorDTOTemp.setLink("/colaborador/" + colaboradorDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(colaboradorDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alterar(ColaboradorDTO colaboradorDTO) {
        Response.ResponseBuilder resposta;
        try {
            colaboradorNegocio.alterar(colaboradorDTO);
            ColaboradorDTO colaboradorDTOTemp = colaboradorNegocio.pesquisaParteNome(colaboradorDTO.getNome()).get(0);
            colaboradorDTOTemp.setLink("/colaborador/" + colaboradorDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(colaboradorDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @DELETE
    @Path("/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response excluir(@PathParam("codigo") int codigo) {
        Response.ResponseBuilder resposta;
        try {
            colaboradorNegocio.excluir(codigo);
            resposta = Response.noContent();
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Path("/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pesquisaCodigo(@PathParam("codigo") int codigo) {
        Response.ResponseBuilder resposta;
        try {
            ColaboradorDTO colaboradorDTO = colaboradorNegocio.pesquisaCodigo(codigo);
            colaboradorDTO.setLink("/colaborador/" + colaboradorDTO.getCodigo());
            resposta = Response.ok();
            resposta.entity(colaboradorDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response pesquisaNome(@QueryParam("nome") String nome) {
        Response.ResponseBuilder resposta;
        try {
            ColaboradorDTO colaboradorDTO = colaboradorNegocio.pesquisaParteNome(nome).get(0);
            colaboradorDTO.setLink("/colaborador/" + colaboradorDTO.getCodigo());
            resposta = Response.ok();
            resposta.entity(colaboradorDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

}
