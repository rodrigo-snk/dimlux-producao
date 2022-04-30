package br.com.sankhya.dimlux.producao.events;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.BigDecimalUtil;

public class PreencheNumPedido2 implements EventoProgramavelJava {
    @Override
    public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {

        DynamicVO cabVO = (DynamicVO) persistenceEvent.getVo();

        if (!BigDecimalUtil.isNullOrZero(cabVO.asBigDecimal("IDIPROC"))) {
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            DynamicVO ordemProducaoVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO("CabecalhoInstanciaProcesso", cabVO.asBigDecimal("IDIPROC"));
            DynamicVO pedidoVO = (DynamicVO) dwfFacade.findEntityByPrimaryKeyAsVO(DynamicEntityNames.CABECALHO_NOTA, ordemProducaoVO.asBigDecimal("NUNOTA"));
            cabVO.setProperty("NUMPEDIDO2", pedidoVO.asBigDecimal("NUMNOTA").toString());
        }

    }

    @Override
    public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterInsert(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void afterDelete(PersistenceEvent persistenceEvent) throws Exception {

    }

    @Override
    public void beforeCommit(TransactionContext transactionContext) throws Exception {

    }
}
