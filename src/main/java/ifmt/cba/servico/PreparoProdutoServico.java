package ifmt.cba.servico;

import java.util.List;

import ifmt.cba.dto.PreparoProdutoDTO;
import ifmt.cba.negocio.PreparoProdutoNegocio;
import ifmt.cba.persistencia.FabricaEntityManager;
import ifmt.cba.persistencia.PersistenciaException;
import ifmt.cba.persistencia.PreparoProdutoDAO;
import ifmt.cba.servico.util.Mensagem;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

@Path("/preparo-produto")
public class PreparoProdutoServico {

    private static PreparoProdutoNegocio preparoProdutoNegocio;
    private static PreparoProdutoDAO preparoProdutoDAO;

    static {
        try {
            preparoProdutoDAO = new PreparoProdutoDAO(FabricaEntityManager.getEntityManagerProducao());
            preparoProdutoNegocio = new PreparoProdutoNegocio(preparoProdutoDAO);
        } catch (PersistenciaException e) {
            e.printStackTrace();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adicionar(PreparoProdutoDTO preparoProdutoDTO) {
        ResponseBuilder resposta;
        try {
            preparoProdutoNegocio.inserir(preparoProdutoDTO);
            PreparoProdutoDTO preparoProdutoDTOTemp = preparoProdutoNegocio.pesquisaPorProdutoETipoPreparo(preparoProdutoDTO.getProduto().getCodigo(), preparoProdutoDTO.getTipoPreparo().getCodigo());
            preparoProdutoDTOTemp.setLink("/preparo-produto/" + preparoProdutoDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(preparoProdutoDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alterar(PreparoProdutoDTO preparoProdutoDTO) {
        ResponseBuilder resposta;
        try {
            preparoProdutoNegocio.alterar(preparoProdutoDTO);
            PreparoProdutoDTO preparoProdutoDTOTemp = preparoProdutoNegocio.pesquisaPorCodigo(preparoProdutoDTO.getCodigo());
            preparoProdutoDTOTemp.setLink("/preparo-produto/" + preparoProdutoDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(preparoProdutoDTOTemp);
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
        ResponseBuilder resposta;
        try {
            preparoProdutoNegocio.excluir(codigo);
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
    public Response buscarProdutoPorCodigo(@PathParam("codigo") int codigo) {
        ResponseBuilder resposta;
        try {
            preparoProdutoNegocio.pesquisaPorCodigo(codigo);
            PreparoProdutoDTO preparoProdutoDTOTemp = preparoProdutoNegocio.pesquisaPorCodigo(codigo);
            preparoProdutoDTOTemp.setLink("/preparo-produto/" + preparoProdutoDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(preparoProdutoDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPreparoProdutoPorNome(@QueryParam("nome") String nome) {
        ResponseBuilder resposta;
        try {
            List<PreparoProdutoDTO> listaPreparoProdutoDTO = preparoProdutoNegocio.pesquisaPorParteNome(nome);
            for (PreparoProdutoDTO preparoProdutoDTO : listaPreparoProdutoDTO) {
                preparoProdutoDTO.setLink("/preparo-produto/" + preparoProdutoDTO.getCodigo());
            }
            resposta = Response.ok();
            resposta.entity(listaPreparoProdutoDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Path("/produto/{produto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPreparoProdutoPorProduto(@PathParam("produto") int codigoProduto) {
        ResponseBuilder resposta;
        try {
            List<PreparoProdutoDTO> listaPreparoProdutoDTO = preparoProdutoNegocio.pesquisaPorProduto(codigoProduto);
            for (PreparoProdutoDTO preparoProdutoDTO : listaPreparoProdutoDTO) {
                preparoProdutoDTO.setLink("/preparo-produto/" + preparoProdutoDTO.getCodigo());
            }
            System.out.println(listaPreparoProdutoDTO);
            resposta = Response.ok();
            resposta.entity(listaPreparoProdutoDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }
}
