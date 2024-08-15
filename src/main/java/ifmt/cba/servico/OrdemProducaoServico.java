package ifmt.cba.servico;

import ifmt.cba.dto.OrdemProducaoDTO;
import ifmt.cba.negocio.NegocioException;
import ifmt.cba.negocio.OrdemProducaoNegocio;
import ifmt.cba.persistencia.ItemOrdemProducaoDAO;
import ifmt.cba.persistencia.OrdemProducaoDAO;
import ifmt.cba.persistencia.PersistenciaException;
import ifmt.cba.persistencia.FabricaEntityManager;
import ifmt.cba.servico.util.Mensagem;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/ordemProducao")
public class OrdemProducaoServico {
    private static OrdemProducaoDAO ordemProducaoDAO;
    private static ItemOrdemProducaoDAO itemOrdemProducaoDAO;
    private static OrdemProducaoNegocio ordemProducaoNegocio;

    static {
        try {
            ordemProducaoDAO = new OrdemProducaoDAO(FabricaEntityManager.getEntityManagerProducao());
            itemOrdemProducaoDAO = new ItemOrdemProducaoDAO(FabricaEntityManager.getEntityManagerProducao());
            ordemProducaoNegocio = new OrdemProducaoNegocio(ordemProducaoDAO, itemOrdemProducaoDAO);
        } catch (PersistenciaException e) {
            e.printStackTrace();
        } catch (NegocioException e) {
            throw new RuntimeException(e);
        }
    }

    //implementar processarOrdemProducao
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/processarOrdemProducao")
    public Response processarOrdemProducao(OrdemProducaoDTO ordemProducaoDTO) {
        Response.ResponseBuilder resposta;
        try {
            ordemProducaoNegocio.processarOrdemProducao(ordemProducaoDTO);
            resposta = Response.ok();
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }


}
