package ifmt.cba.servico;

import ifmt.cba.dto.BairroDTO;
import ifmt.cba.negocio.BairroNegocio;
import ifmt.cba.persistencia.BairroDAO;
import ifmt.cba.persistencia.FabricaEntityManager;
import ifmt.cba.persistencia.PersistenciaException;
import ifmt.cba.servico.util.Mensagem;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/bairro")
public class BairroServico {
    private static BairroNegocio bairroNegocio;
    private static BairroDAO bairroDAO;

    static {
        try {
            bairroDAO = new BairroDAO(FabricaEntityManager.getEntityManagerProducao());
            bairroNegocio = new BairroNegocio(bairroDAO);
        } catch (PersistenciaException e) {
            e.printStackTrace();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adicionar(BairroDTO bairroDTO) {
        Response.ResponseBuilder resposta;
        try {
            bairroNegocio.inserir(bairroDTO);
            BairroDTO bairroDTOTemp = bairroNegocio.pesquisaCodigo(bairroDTO.getCodigo());
            bairroDTOTemp.setLink("/bairro/codigo/" + bairroDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(bairroDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(ex.getMessage());

        }
        return resposta.build();
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alterar(BairroDTO bairroDTO) {
        Response.ResponseBuilder resposta;
        try {
            bairroNegocio.alterar(bairroDTO);
            BairroDTO bairroDTOTemp = bairroNegocio.pesquisaCodigo(bairroDTO.getCodigo());
            bairroDTOTemp.setLink("/bairro/codigo/" + bairroDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(bairroDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(ex.getMessage());
        }
        return resposta.build();
    }

    @DELETE
    @Path("/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response excluir(@PathParam("codigo") int codigo) {
        Response.ResponseBuilder resposta;
        try {
            bairroNegocio.excluir(codigo);
            resposta = Response.ok();
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorCodigo(@PathParam("codigo") int codigo) {
        Response.ResponseBuilder resposta;
        try {
            BairroDTO bairroDTO = bairroNegocio.pesquisaCodigo(codigo);
            bairroDTO.setLink("/bairro/codigo/" + bairroDTO.getCodigo());
            resposta = Response.ok();
            resposta.entity(bairroDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Path("/nome/{nome}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorNome(@PathParam("nome") String nome) {
        Response.ResponseBuilder resposta;
        try {
            BairroDTO bairroDTO = bairroNegocio.pesquisaParteNome(nome).get(0);
            bairroDTO.setLink("/bairro/codigo/" + bairroDTO.getCodigo());
            resposta = Response.ok();
            resposta.entity(bairroDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }



}
