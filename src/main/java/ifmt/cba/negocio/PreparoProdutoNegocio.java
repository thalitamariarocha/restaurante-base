package ifmt.cba.negocio;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import ifmt.cba.dto.PreparoProdutoDTO;
import ifmt.cba.entity.PreparoProduto;
import ifmt.cba.persistencia.PersistenciaException;
import ifmt.cba.persistencia.PreparoProdutoDAO;

public class PreparoProdutoNegocio {
    private ModelMapper modelMapper;
	private PreparoProdutoDAO preparoProdutoDAO;

	public PreparoProdutoNegocio(PreparoProdutoDAO preparoProdutoDAO) {
		this.preparoProdutoDAO = preparoProdutoDAO;
		this.modelMapper = new ModelMapper();
	}

	public void inserir(PreparoProdutoDTO preparoprodutoDTO) throws NegocioException {

		PreparoProduto preparoProduto = this.toEntity(preparoprodutoDTO);
		String mensagemErros = preparoProduto.validar();

		if (!mensagemErros.isEmpty()) {
			throw new NegocioException(mensagemErros);
		}

		try {
			if (preparoProdutoDAO.buscarPorProdutoETipoPreparo(preparoProduto.getProduto().getCodigo(), preparoProduto.getTipoPreparo().getCodigo()) != null) {
				throw new NegocioException("Ja existe esse preparo de produto");
			}
			preparoProdutoDAO.beginTransaction();
			preparoProdutoDAO.incluir(preparoProduto);
			preparoProdutoDAO.commitTransaction();
		} catch (PersistenciaException ex) {
			preparoProdutoDAO.rollbackTransaction();
			throw new NegocioException("Erro ao incluir o preparo de produto - " + ex.getMessage());
		}
	}

	public void alterar(PreparoProdutoDTO preparoProdutoDTO) throws NegocioException {

		PreparoProduto preparoProduto = this.toEntity(preparoProdutoDTO);
		String mensagemErros = preparoProduto.validar();
		if (!mensagemErros.isEmpty()) {
			throw new NegocioException(mensagemErros);
		}
		try {
			if (preparoProdutoDAO.buscarPorCodigo(preparoProduto.getCodigo()) == null) {
				throw new NegocioException("Nao existe esse preparo de produto");
			}
			preparoProdutoDAO.beginTransaction();
			preparoProdutoDAO.alterar(preparoProduto);
			preparoProdutoDAO.commitTransaction();
		} catch (PersistenciaException ex) {
			preparoProdutoDAO.rollbackTransaction();
			throw new NegocioException("Erro ao alterar o preparo de produto - " + ex.getMessage());
		}
	}

	public void excluir(int codigo) throws NegocioException {

		try {
			PreparoProduto preparoProduto = preparoProdutoDAO.buscarPorCodigo(codigo);
			if (preparoProduto == null) {
				throw new NegocioException("Nao existe esse preparo de produto");
			}
			preparoProdutoDAO.beginTransaction();
			preparoProdutoDAO.excluir(preparoProduto);
			preparoProdutoDAO.commitTransaction();
		} catch (PersistenciaException ex) {
			preparoProdutoDAO.rollbackTransaction();
			throw new NegocioException("Erro ao excluir o produto - " + ex.getMessage());
		}
	}

	public PreparoProdutoDTO pesquisaPorCodigo(int codigo) throws NegocioException {
		try {
			PreparoProduto preparoProduto = preparoProdutoDAO.buscarPorCodigo(codigo);
			if (preparoProduto != null) {
				return this.toDTO(preparoProduto);
			}
			return null;
		} catch (PersistenciaException ex) {
			throw new NegocioException("Erro ao pesquisar produto pelo codigo - " + ex.getMessage());
		}
	}

	public List<PreparoProdutoDTO> pesquisaPorParteNome(String nome) throws NegocioException {
		try {
			return this.toDTOAll(preparoProdutoDAO.buscarPorParteNome(nome));
		} catch (PersistenciaException ex) {
			throw new NegocioException("Erro ao pesquisar preparo de produto por nome - " + ex.getMessage());
		}
	}

    public List<PreparoProdutoDTO> pesquisaPorProduto(int codigoProduto) throws NegocioException {
		try {
			return this.toDTOAll(preparoProdutoDAO.buscarPorProduto(codigoProduto));
		} catch (PersistenciaException ex) {
			throw new NegocioException("Erro ao pesquisar preparo de produto por produto - " + ex.getMessage());
		}
	}

    public List<PreparoProdutoDTO> pesquisaPorTipoPreparo(int codigoTipoPreparo) throws NegocioException {
		try {
			return this.toDTOAll(preparoProdutoDAO.buscarPorTipoPreparo(codigoTipoPreparo));
		} catch (PersistenciaException ex) {
			throw new NegocioException("Erro ao pesquisar preparo de produto por tipo preparo - " + ex.getMessage());
		}
	}

    public PreparoProdutoDTO pesquisaPorProdutoETipoPreparo(int codigoProduto, int codigoTipoPreparo) throws NegocioException {
		try {
			PreparoProduto preparoProduto = preparoProdutoDAO.buscarPorProdutoETipoPreparo(codigoProduto, codigoTipoPreparo);
			if (preparoProduto != null) {
				return this.toDTO(preparoProduto);
			}
			return null;
		} catch (PersistenciaException ex) {
			throw new NegocioException("Erro ao pesquisar preparo de produto por produto e tipo preparo - " + ex.getMessage());
		}
	}

	public List<PreparoProdutoDTO> toDTOAll(List<PreparoProduto> listaPreparoProduto) {
		List<PreparoProdutoDTO> listDTO = new ArrayList<PreparoProdutoDTO>();

		for (PreparoProduto preparoProduto : listaPreparoProduto) {
			listDTO.add(this.toDTO(preparoProduto));
		}
		return listDTO;
	}

	public PreparoProdutoDTO toDTO(PreparoProduto preparoProduto) {
		if (preparoProduto == null) {
			throw new IllegalArgumentException("PreparoProduto cannot be null");
		}
		return this.modelMapper.map(preparoProduto, PreparoProdutoDTO.class);
	}
	
	public PreparoProduto toEntity(PreparoProdutoDTO preparoProdutoDTO) {
		if (preparoProdutoDTO == null) {
			throw new IllegalArgumentException("PreparoProdutoDTO cannot be null");
		}
		return this.modelMapper.map(preparoProdutoDTO, PreparoProduto.class);
	}
}
