package ifmt.cba.servico;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import ifmt.cba.dto.ClienteDTO;
import ifmt.cba.dto.EstadoPedidoDTO;
import ifmt.cba.dto.PedidoDTO;
import ifmt.cba.negocio.ClienteNegocio;
import ifmt.cba.negocio.NegocioException;
import ifmt.cba.negocio.PedidoNegocio;
import ifmt.cba.persistencia.ClienteDAO;
import ifmt.cba.persistencia.FabricaEntityManager;
import ifmt.cba.persistencia.ItemPedidoDAO;
import ifmt.cba.persistencia.PedidoDAO;
import ifmt.cba.persistencia.PersistenciaException;
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

@Path("/pedido")
public class PedidoServico {

    private static PedidoNegocio pedidoNegocio;
    private static ClienteNegocio clienteNegocio;
    private static PedidoDAO pedidoDAO;
    private static ClienteDAO clienteDAO;

    static {
        try {
            pedidoDAO = new PedidoDAO(FabricaEntityManager.getEntityManagerProducao());
            ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(FabricaEntityManager.getEntityManagerProducao());
            clienteDAO = new ClienteDAO(FabricaEntityManager.getEntityManagerProducao());
            pedidoNegocio = new PedidoNegocio(pedidoDAO, itemPedidoDAO, clienteDAO);
            clienteNegocio = new ClienteNegocio(clienteDAO, pedidoDAO);
        } catch (PersistenciaException e) {
            e.printStackTrace();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response adicionar(PedidoDTO pedidoDTO) {
        ResponseBuilder resposta;
        try {
            int idGerado = pedidoNegocio.inserir(pedidoDTO);

            PedidoDTO pedidoDTOTemp = pedidoNegocio.pesquisaCodigo(idGerado);
            pedidoDTOTemp.setLink("/pedido/" + pedidoDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(pedidoDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response alterar(PedidoDTO pedidoDTO) {
        ResponseBuilder resposta;
        try {
            pedidoNegocio.alterar(pedidoDTO);
            PedidoDTO pedidoDTOTemp = pedidoNegocio.pesquisaCodigo(pedidoDTO.getCodigo());
            pedidoDTOTemp.setLink("/pedido/" + pedidoDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(pedidoDTOTemp);
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
            PedidoDTO pedidoDTO = new PedidoDTO();
            pedidoDTO.setCodigo(codigo);
            pedidoNegocio.excluir(pedidoDTO);
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
    public Response buscarPorCodigo(@PathParam("codigo") int codigo) {
        ResponseBuilder resposta;
        try {
            pedidoNegocio.pesquisaCodigo(codigo);
            PedidoDTO pedidoDTOTemp = pedidoNegocio.pesquisaCodigo(codigo);
            pedidoDTOTemp.setLink("/pedido/" + pedidoDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(pedidoDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @PUT
    @Path("/producao")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mudarPedidoParaProducao(PedidoDTO pedidoDTO) {
        ResponseBuilder resposta;
        try {
            pedidoNegocio.mudarPedidoParaProducao(pedidoDTO);
            PedidoDTO pedidoDTOTemp = pedidoNegocio.pesquisaCodigo(pedidoDTO.getCodigo());
            pedidoDTOTemp.setLink("/pedido/" + pedidoDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(pedidoDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @PUT
    @Path("/pronto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mudarPedidoParaPronto(PedidoDTO pedidoDTO) {
        ResponseBuilder resposta;
        try {
            pedidoNegocio.mudarPedidoParaPronto(pedidoDTO);
            PedidoDTO pedidoDTOTemp = pedidoNegocio.pesquisaCodigo(pedidoDTO.getCodigo());
            pedidoDTOTemp.setLink("/pedido/" + pedidoDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(pedidoNegocio.pesquisaCodigo(pedidoDTO.getCodigo()));
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @PUT
    @Path("/entrega")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mudarPedidoParaEntrega(PedidoDTO pedidoDTO) {
        ResponseBuilder resposta;
        try {
            pedidoNegocio.mudarPedidoParaEntrega(pedidoDTO);
            PedidoDTO pedidoDTOTemp = pedidoNegocio.pesquisaCodigo(pedidoDTO.getCodigo());
            pedidoDTOTemp.setLink("/pedido/" + pedidoDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(pedidoDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @PUT
    @Path("/concluido")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response mudarPedidoParaConcluido(PedidoDTO pedidoDTO) {
        ResponseBuilder resposta;
        try {
            pedidoNegocio.mudarPedidoParaConcluido(pedidoDTO);
            PedidoDTO pedidoDTOTemp = pedidoNegocio.pesquisaCodigo(pedidoDTO.getCodigo());
            pedidoDTOTemp.setLink("/pedido/" + pedidoDTOTemp.getCodigo());
            resposta = Response.ok();
            resposta.entity(pedidoDTOTemp);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Path("/dataproducao")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorDataProducao(@QueryParam ("dataInicial") String dataInicial, @QueryParam ("dataFinal") String dataFinal) {
        ResponseBuilder resposta;
        try {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            List<PedidoDTO> listaPedidoDTO = pedidoNegocio.pesquisaPorDataProducao(LocalDate.parse(dataInicial, formato), LocalDate.parse(dataFinal, formato));
            for (PedidoDTO pedidoDTO : listaPedidoDTO) {
                pedidoDTO.setLink("/pedido/" + pedidoDTO.getCodigo());
            }
            resposta = Response.ok();
            resposta.entity(listaPedidoDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Path("/estado")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorEstado(@QueryParam ("estado") String estado) {
        ResponseBuilder resposta;
        try {
            List<PedidoDTO> listaPedidoDTO = pedidoNegocio.pesquisaPorEstado(EstadoPedidoDTO.valueOf(estado));
            for (PedidoDTO pedidoDTO : listaPedidoDTO) {
                pedidoDTO.setLink("/pedido/" + pedidoDTO.getCodigo());
            }
            resposta = Response.ok();
            resposta.entity(listaPedidoDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Path("/estadodata")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pesquisaPorEstadoEData(@QueryParam ("estado") String estado, @QueryParam ("data") String data) {
        ResponseBuilder resposta;
        try {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            List<PedidoDTO> listaPedidoDTO = pedidoNegocio.pesquisaPorEstadoEData(EstadoPedidoDTO.valueOf(estado), LocalDate.parse(data, formato));
            for (PedidoDTO pedidoDTO : listaPedidoDTO) {
                pedidoDTO.setLink("/pedido/" + pedidoDTO.getCodigo());
            }
            resposta = Response.ok();
            resposta.entity(listaPedidoDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Path("/cliente/{codigo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorCliente(@PathParam("codigo") int codigo) {
        ResponseBuilder resposta;
        try {
            ClienteDTO clienteDTO = clienteNegocio.pesquisaCodigo(codigo);
            List<PedidoDTO> listaPedidoDTO = pedidoNegocio.pesquisaPorCliente(clienteDTO);
            for (PedidoDTO pedidoDTO : listaPedidoDTO) {
                pedidoDTO.setLink("/pedido/cliente/" + pedidoDTO.getCodigo());
            }
            resposta = Response.ok();
            resposta.entity(listaPedidoDTO);
        } catch (Exception ex) {
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        }
        return resposta.build();
    }

    //consultar a produção de refeições por dia com totalização mensal
    @GET
    @Path("/relatorioPedidoPeriodo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response relatorioPedidoPeriodo(@QueryParam("dataInicial") String dataInicialStr, @QueryParam("dataFinal") String dataFinalStr) {
        ResponseBuilder resposta;
        try {

            LocalDate dataInicial = LocalDate.parse(dataInicialStr);
            LocalDate dataFinal = LocalDate.parse(dataFinalStr);

            List<PedidoDTO> listaPedidoDTO = pedidoNegocio.pesquisaPorDataProducao(dataInicial, dataFinal);
            double totalizacao = listaPedidoDTO.size();

            resposta = Response.ok();
            resposta.entity(totalizacao);
        } catch (NegocioException ex) {
            // Tratar exceção de negócio
            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        } catch (DateTimeParseException ex) {
            // Tratar exceção de parsing de data
            resposta = Response.status(400);
            resposta.entity(new Mensagem("Formato de data inválido: " + ex.getParsedString()));
        } catch (Exception ex) {
            // Tratar outras exceções
            resposta = Response.status(500);
            resposta.entity(new Mensagem("Erro inesperado: " + ex.getMessage()));
        }
        return resposta.build();
    }

    @GET
    @Path("/media-tempo-pronto-concluido")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularMediaTempoEntreProntoECOncluido() {
        ResponseBuilder resposta;
        try {
            List<PedidoDTO> listaPedidoDTO = pedidoNegocio.pesquisaPorEstado(EstadoPedidoDTO.CONCLUIDO);

            double mediaTempo = listaPedidoDTO.stream()
                    .filter(pedido -> pedido.getHoraPronto() != null && pedido.getHoraFinalizado() != null)
                    .mapToLong(pedido -> Duration.between(pedido.getHoraPronto(), pedido.getHoraFinalizado()).toMinutes())
                    .average()
                    .orElse(0);

            resposta = Response.ok();
            resposta.entity(mediaTempo);
        } catch (NegocioException ex) {

            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        } catch (Exception ex) {

            resposta = Response.status(500);
            resposta.entity(new Mensagem("Erro inesperado: " + ex.getMessage()));
        }
        return resposta.build();
    }
    
    @GET
    @Path("/media-tempo-registrado-pronto")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularMediaTempoRegistradoEPronto() {
        ResponseBuilder resposta;
        try {
            List<PedidoDTO> listaPedidoDTO = pedidoNegocio.pesquisaPorEstado(EstadoPedidoDTO.PRONTO);
            listaPedidoDTO.addAll(pedidoNegocio.pesquisaPorEstado(EstadoPedidoDTO.ENTREGA));
            listaPedidoDTO.addAll(pedidoNegocio.pesquisaPorEstado(EstadoPedidoDTO.CONCLUIDO));

            double mediaTempo = listaPedidoDTO.stream()
                    .filter(pedido -> pedido.getHoraPedido() != null && pedido.getHoraPronto() != null)
                    .mapToLong(pedido -> Duration.between(pedido.getHoraPedido(), pedido.getHoraPronto()).toMinutes())
                    .average()
                    .orElse(0);

            resposta = Response.ok();
            resposta.entity(mediaTempo);
        } catch (NegocioException ex) {

            resposta = Response.status(400);
            resposta.entity(new Mensagem(ex.getMessage()));
        } catch (Exception ex) {

            resposta = Response.status(500);
            resposta.entity(new Mensagem("Erro inesperado: " + ex.getMessage()));
        }
        return resposta.build();
    }

}
