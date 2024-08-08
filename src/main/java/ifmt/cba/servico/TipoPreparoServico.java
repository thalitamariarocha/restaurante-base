package ifmt.cba.servico;

import java.util.List;

import ifmt.cba.dto.TipoPreparoDTO;
import ifmt.cba.negocio.NegocioException;
import ifmt.cba.negocio.TipoPreparoNegocio;
import ifmt.cba.persistencia.FabricaEntityManager;
import ifmt.cba.persistencia.PersistenciaException;
import ifmt.cba.persistencia.TipoPreparoDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

@Path("/tipopreparo")
public class TipoPreparoServico {
    
    private static TipoPreparoNegocio tipoPreparoNegocio;
    private static TipoPreparoDAO tipoPreparoDAO;

    static {
        try {
            tipoPreparoDAO = new TipoPreparoDAO(FabricaEntityManager.getEntityManagerProducao());
            tipoPreparoNegocio = new TipoPreparoNegocio(tipoPreparoDAO);
        } catch (PersistenciaException | NegocioException e) {
            e.printStackTrace();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adicionar(TipoPreparoDTO tipoPreparoDTO) {
        ResponseBuilder resposta;
        try {
            tipoPreparoNegocio.inserir(tipoPreparoDTO);
            resposta = Response.ok();
            resposta.entity(tipoPreparoNegocio.pesquisaParteDescricao(tipoPreparoDTO.getDescricao()).get(0));
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity("{\"erro\": \""+ex.getMessage()+"\"}");
        }
        return resposta.build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alterar(TipoPreparoDTO tipoPreparoDTO) {
        ResponseBuilder resposta;
        try {
            tipoPreparoNegocio.alterar(tipoPreparoDTO);
            resposta = Response.ok();
            resposta.entity(tipoPreparoNegocio.pesquisaCodigo(tipoPreparoDTO.getCodigo()));
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity("{\"erro\": \""+ex.getMessage()+"\"}");
        }
        return resposta.build();
    }

    @DELETE
    @Path("/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response excluir(@PathParam("codigo") int codigo) {
        ResponseBuilder resposta;
        try {
            tipoPreparoNegocio.excluir(codigo);
            resposta = Response.ok();
            resposta.entity(tipoPreparoNegocio.pesquisaCodigo(codigo));
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity("{\"erro\": \""+ex.getMessage()+"\"}");
        }
        return resposta.build();
    }

    @GET
    @Path("/codigo/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarTipoPreparoPorCodigo(@PathParam("codigo") int codigo) {
        ResponseBuilder resposta;
        try {
            TipoPreparoDTO tipoPreparoDTO = tipoPreparoNegocio.pesquisaCodigo(codigo);
            resposta = Response.ok();
            resposta.entity(tipoPreparoDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity("{\"erro\": \""+ex.getMessage()+"\"}");
        }
        return resposta.build();
    }

    @GET
    @Path("/nome/{nome}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarTipoPreparoPorNome(@PathParam("nome") String descricao) {
        ResponseBuilder resposta;
        try {
            List<TipoPreparoDTO> listaTipoPreparoDTO = tipoPreparoNegocio.pesquisaParteDescricao(descricao);
            resposta = Response.ok();
            resposta.entity(listaTipoPreparoDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity("{\"erro\": \""+ex.getMessage()+"\"}");
        }
        return resposta.build();
    }
}
