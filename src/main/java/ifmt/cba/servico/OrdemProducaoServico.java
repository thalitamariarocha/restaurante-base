package ifmt.cba.servico;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ifmt.cba.dto.EstadoOrdemProducaoDTO;
import ifmt.cba.dto.ItemOrdemProducaoDTO;
import ifmt.cba.dto.OrdemProducaoDTO;
import ifmt.cba.dto.ProdutoDTO;
import ifmt.cba.dto.QuantidadeProduzidasProdutosDTO;
import ifmt.cba.negocio.NegocioException;
import ifmt.cba.negocio.OrdemProducaoNegocio;
import ifmt.cba.persistencia.ItemOrdemProducaoDAO;
import ifmt.cba.persistencia.OrdemProducaoDAO;
import ifmt.cba.persistencia.PersistenciaException;
import ifmt.cba.persistencia.FabricaEntityManager;
import ifmt.cba.servico.util.Mensagem;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Response.ResponseBuilder;

@Path("/ordem-producao")
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adicionar(OrdemProducaoDTO ordemProducaoDTO) {
        ResponseBuilder resposta;
        try {
            int idGErado = ordemProducaoNegocio.inserir(ordemProducaoDTO);
            
            OrdemProducaoDTO ordemProducaoTemp = ordemProducaoNegocio.pesquisaCodigo(idGErado);
            ordemProducaoTemp.setLink("/ordem-producao/" + ordemProducaoTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(ordemProducaoTemp);
        } catch (Exception ex) {
            ex.printStackTrace();
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alterar(OrdemProducaoDTO ordemProducaoDTO) {
        ResponseBuilder resposta;
        try {
            ordemProducaoNegocio.alterar(ordemProducaoDTO);
            OrdemProducaoDTO ordemProducaoTemp = ordemProducaoNegocio.pesquisaCodigo(ordemProducaoDTO.getCodigo());
            ordemProducaoTemp.setLink("/ordem-producao/" + ordemProducaoTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(ordemProducaoTemp);
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
            OrdemProducaoDTO ordemProducaoDTO = new OrdemProducaoDTO();
            ordemProducaoDTO.setCodigo(codigo);
            ordemProducaoNegocio.excluir(ordemProducaoDTO);
            resposta = Response.noContent();
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{codigo}")
    public Response pesquisarPorCodigo(@PathParam("codigo") int codigo) {
        ResponseBuilder resposta;
        try {
            OrdemProducaoDTO ordemProducaoDTO = ordemProducaoNegocio.pesquisaCodigo(codigo);
            ordemProducaoDTO.setLink("/ordem-producao/" + ordemProducaoDTO.getCodigo());
            resposta = Response.ok();
            resposta.entity(ordemProducaoDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @PUT
    @Path("/item")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alterarItem(ItemOrdemProducaoDTO itemOrdemProducaoDTO) {
        ResponseBuilder resposta;
        try {
            ordemProducaoNegocio.alterarItemOrdem(itemOrdemProducaoDTO);
            ItemOrdemProducaoDTO itemOrdemProducaoTemp = ordemProducaoNegocio
                    .pesquisarItemOrdemProducaoPorCodigo(itemOrdemProducaoDTO.getCodigo());
            itemOrdemProducaoTemp.setLink("/ordem-producao/item/" + itemOrdemProducaoTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(itemOrdemProducaoTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @DELETE
    @Path("/item/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response excluirItem(@PathParam("codigo") int codigo) {
        ResponseBuilder resposta;
        try {
            ItemOrdemProducaoDTO itemOrdemProducaoDTO = new ItemOrdemProducaoDTO();
            itemOrdemProducaoDTO.setCodigo(codigo);
            ordemProducaoNegocio.excluirItemOrdem(itemOrdemProducaoDTO);
            resposta = Response.noContent();
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{codigo}/processar-ordem")
    public Response processarOrdemProducao(@PathParam("codigo") int codigo) {
        Response.ResponseBuilder resposta;
        try {
            OrdemProducaoDTO ordemProducaoDTO = ordemProducaoNegocio.pesquisaCodigo(codigo);
            ordemProducaoNegocio.processarOrdemProducao(ordemProducaoDTO);
            resposta = Response.ok();
        } catch (Exception ex) {
            ex.printStackTrace();
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/relatorio/produtos-mais-produzidos")
    public Response pesquisarProdutosMaisProduzidos() {
        Response.ResponseBuilder resposta;
        try {
            List<OrdemProducaoDTO> listaOrdemProducao = ordemProducaoNegocio
                    .pesquisaPorEstado(EstadoOrdemProducaoDTO.PROCESSADA);

            // Usando um Map para agregar a quantidade produzida por produto
            Map<ProdutoDTO, Integer> quantidadePorProduto = new HashMap<>();

            for (OrdemProducaoDTO ordemProducaoDTO : listaOrdemProducao) {
                for (ItemOrdemProducaoDTO itemOrdemProducaoDTO : ordemProducaoDTO.getListaItens()) {
                    ProdutoDTO produto = itemOrdemProducaoDTO.getPreparoProduto().getProduto();
                    int quantidade = itemOrdemProducaoDTO.getQuantidadePorcao();

                    // Adiciona ou atualiza a quantidade no Map
                    quantidadePorProduto.merge(produto, quantidade, Integer::sum);
                }
            }

            // Converte o Map para a lista de DTOs e ordena do maior para o menor
            List<QuantidadeProduzidasProdutosDTO> listaQuantidadeProduzidasProdutosDTO = quantidadePorProduto.entrySet()
                    .stream()
                    .map(entry -> {
                        QuantidadeProduzidasProdutosDTO dto = new QuantidadeProduzidasProdutosDTO();
                        dto.setProduto(entry.getKey());
                        dto.setQuantidadeProduzida(entry.getValue());
                        return dto;
                    })
                    .sorted((dto1, dto2) -> Integer.compare(dto2.getQuantidadeProduzida(),
                            dto1.getQuantidadeProduzida())) // Ordena do maior para o menor
                    .collect(Collectors.toList());

            resposta = Response.ok(listaQuantidadeProduzidasProdutosDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/relatorio/produtos-menos-produzidos")
    public Response pesquisarProdutosMenosProduzidos() {
        Response.ResponseBuilder resposta;
        try {
            List<OrdemProducaoDTO> listaOrdemProducao = ordemProducaoNegocio
                    .pesquisaPorEstado(EstadoOrdemProducaoDTO.PROCESSADA);

            // Usando um Map para agregar a quantidade produzida por produto
            Map<ProdutoDTO, Integer> quantidadePorProduto = new HashMap<>();

            for (OrdemProducaoDTO ordemProducaoDTO : listaOrdemProducao) {
                for (ItemOrdemProducaoDTO itemOrdemProducaoDTO : ordemProducaoDTO.getListaItens()) {
                    ProdutoDTO produto = itemOrdemProducaoDTO.getPreparoProduto().getProduto();
                    int quantidade = itemOrdemProducaoDTO.getQuantidadePorcao();

                    // Adiciona ou atualiza a quantidade no Map
                    quantidadePorProduto.merge(produto, quantidade, Integer::sum);
                }
            }

            // Converte o Map para a lista de DTOs e ordena do menor para o maior
            List<QuantidadeProduzidasProdutosDTO> listaQuantidadeProduzidasProdutosDTO = quantidadePorProduto.entrySet()
                    .stream()
                    .map(entry -> {
                        QuantidadeProduzidasProdutosDTO dto = new QuantidadeProduzidasProdutosDTO();
                        dto.setProduto(entry.getKey());
                        dto.setQuantidadeProduzida(entry.getValue());
                        return dto;
                    })
                    .sorted((dto1, dto2) -> Integer.compare(dto1.getQuantidadeProduzida(),
                            dto2.getQuantidadeProduzida())) // Ordena do menor para o maior
                    .collect(Collectors.toList());

            resposta = Response.ok(listaQuantidadeProduzidasProdutosDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

}
