package ifmt.cba.servico;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ifmt.cba.dto.MovimentoEstoqueDTO;
import ifmt.cba.dto.ProdutoDTO;
import ifmt.cba.dto.QuantidadeDescartadaProdutoDTO;
import ifmt.cba.dto.RegistroEstoqueDTO;
import ifmt.cba.negocio.RegistroEstoqueNegocio;
import ifmt.cba.persistencia.FabricaEntityManager;
import ifmt.cba.persistencia.PersistenciaException;
import ifmt.cba.persistencia.ProdutoDAO;
import ifmt.cba.persistencia.RegistroEstoqueDAO;
import ifmt.cba.servico.util.DateRange;
import ifmt.cba.servico.util.Mensagem;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/registro-estoque")
public class RegistroEstoqueServico {
    private static RegistroEstoqueNegocio registroEstoqueNegocio;
    private static RegistroEstoqueDAO registroEstoqueDAO;
    private static ProdutoDAO produtoDAO;

    static {
        try {
            registroEstoqueDAO = new RegistroEstoqueDAO(FabricaEntityManager.getEntityManagerProducao());
            registroEstoqueNegocio = new RegistroEstoqueNegocio(registroEstoqueDAO, produtoDAO);
        } catch (PersistenciaException e) {
            e.printStackTrace();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adicionar(RegistroEstoqueDTO registroEstoqueDTO) {
        Response.ResponseBuilder resposta;
        try {
            registroEstoqueNegocio.inserir(registroEstoqueDTO);
            RegistroEstoqueDTO registroEstoqueDTOTemp = registroEstoqueNegocio.pesquisaCodigo(registroEstoqueDTO.getCodigo());
            registroEstoqueDTOTemp.setLink("/registro-estoque/" + registroEstoqueDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(registroEstoqueDTOTemp);
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
            RegistroEstoqueDTO registroEstoqueDTO = registroEstoqueNegocio.pesquisaCodigo(codigo);
            registroEstoqueNegocio.excluir(registroEstoqueDTO);
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
    public Response pesquisarCodigo(@PathParam("codigo") int codigo) {
        Response.ResponseBuilder resposta;
        try {
            RegistroEstoqueDTO registroEstoqueDTO = registroEstoqueNegocio.pesquisaCodigo(codigo);
            resposta = Response.ok();
            resposta.entity(registroEstoqueDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Path("/relatorio/produtos-descartados")
    @Produces(MediaType.APPLICATION_JSON)
    public Response relatorioProdutosDescartados(@QueryParam("dataInicio") String dataInicio, @QueryParam("dataFim") String dataFim) {
        Response.ResponseBuilder resposta;
        try {
            List<RegistroEstoqueDTO> listaEstoque = new ArrayList<>();
            List<LocalDate> datas = DateRange.getDatesInRange(LocalDate.parse(dataInicio), LocalDate.parse(dataFim));
            for (LocalDate data : datas) {
                listaEstoque.addAll(registroEstoqueNegocio.buscarPorMovimentoEData(MovimentoEstoqueDTO.PRODUCAO, data));
                listaEstoque.addAll(registroEstoqueNegocio.buscarPorMovimentoEData(MovimentoEstoqueDTO.VENCIMENTO, data));
                listaEstoque.addAll(registroEstoqueNegocio.buscarPorMovimentoEData(MovimentoEstoqueDTO.DANIFICADO, data));
            }

            // Usando um Map para agregar a quantidade produzida por produto
            Map<ProdutoDTO, Integer> quantidadePorProduto = new HashMap<>();

            for (RegistroEstoqueDTO registroEstoque : listaEstoque) {
                ProdutoDTO produto = registroEstoque.getProduto();
                int quantidade = registroEstoque.getQuantidade();

                // Adiciona ou atualiza a quantidade no Map
                quantidadePorProduto.merge(produto, quantidade, Integer::sum);
            }

            // Converte o Map para a lista de DTOs e ordena do maior para o menor
            List<QuantidadeDescartadaProdutoDTO> listaQuantidadeProduzidasProdutosDTO = quantidadePorProduto.entrySet()
                    .stream()
                    .map(entry -> {
                        QuantidadeDescartadaProdutoDTO dto = new QuantidadeDescartadaProdutoDTO();
                        dto.setProduto(entry.getKey());
                        dto.setQuantidadeDescartada(entry.getValue());
                        return dto;
                    })
                    .sorted((dto1, dto2) -> Integer.compare(dto2.getQuantidadeDescartada(),
                            dto1.getQuantidadeDescartada())) // Ordena do maior para o menor
                    .collect(Collectors.toList());

            resposta = Response.ok(listaQuantidadeProduzidasProdutosDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    // @GET
    // @Path("/movimento/{movimento}")
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response pesquisarMovimento(@PathParam("movimento") String movimento) {
    //     Response.ResponseBuilder resposta;
    //     try {
    //         List<RegistroEstoqueDTO> listaEstoqueDTO = registroEstoqueNegocio.buscarPorMovimento(MovimentoEstoqueDTO.valueOf(movimento));
    //         for (RegistroEstoqueDTO registroEstoque : listaEstoqueDTO) {
    //             registroEstoque.setLink("/registro-estoque/" + registroEstoque.getCodigo());
    //         }

    //         resposta = Response.ok();
    //         resposta.entity(listaEstoqueDTO);
    //     } catch (Exception ex) {
    //         resposta = Response.status(400);
    //         resposta.entity(new Mensagem(ex.getMessage()));
    //     }
    //     return resposta.build();
    // }

    // @GET
    // @Path("/movimento/{movimento}/data/{data}")
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response pesquisarMovimentoData(@PathParam("movimento") String movimento, @PathParam("data") String data) {
    //     Response.ResponseBuilder resposta;
    //     try {
    //         List<RegistroEstoqueDTO> listaEstoqueDTO = registroEstoqueNegocio.buscarPorMovimentoEData(MovimentoEstoqueDTO.valueOf(movimento), LocalDate.parse(data));
    //         for (RegistroEstoqueDTO registroEstoque : listaEstoqueDTO) {
    //             registroEstoque.setLink("/registro-estoque/" + registroEstoque.getCodigo());
    //         }

    //         resposta = Response.ok();
    //         resposta.entity(listaEstoqueDTO);
    //     } catch (Exception ex) {
    //         resposta = Response.status(400);
    //         resposta.entity(new Mensagem(ex.getMessage()));
    //     }
    //     return resposta.build();
    // }

//    @GET
//    @Path("/produto/{codigo}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response pesquisarProduto(@PathParam("codigo") int codigo) {
//        Response.ResponseBuilder resposta;
//        try {
//            List<RegistroEstoqueDTO> listaEstoqueDTO = registroEstoqueNegocio.buscarPorProduto(produtoNegocio.pesquisaCodigo(codigo));
//            for (RegistroEstoqueDTO registroEstoque : listaEstoqueDTO) {
//                registroEstoque.setLink("/registroEstoque/codigo/" + registroEstoque.getCodigo());
//            }
//
//            resposta = Response.ok();
//            resposta.entity(listaEstoqueDTO);
//        } catch (Exception ex) {
//            resposta = Response.status(400);
//            resposta.entity(new MensagemErro(ex.getMessage()));
//        }
//        return resposta.build();
//    }

}
