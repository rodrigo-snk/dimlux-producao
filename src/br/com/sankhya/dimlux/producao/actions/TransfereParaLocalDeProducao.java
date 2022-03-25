package br.com.sankhya.dimlux.producao.actions;

import br.com.sankhya.dimlux.producao.model.ItemNota;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.mgecomercial.model.facades.helpper.ItemNotaHelpper;
import br.com.sankhya.modelcore.comercial.impostos.ImpostosHelpper;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.CollectionUtils;
import com.sankhya.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class TransfereParaLocalDeProducao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {

        Registro[] linhas = contextoAcao.getLinhas();
        Collection<BigDecimal> nuNotasTransf = new HashSet<>();
        StringBuilder msgRetorno = new StringBuilder();


        for (Registro linha: linhas) {
            BigDecimal nuNota = (BigDecimal) linha.getCampo("NUNOTA");

            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            CabecalhoNotaVO cabVO = (CabecalhoNotaVO) dwfFacade.findEntityByPrimaryKeyAsVO(DynamicEntityNames.CABECALHO_NOTA, nuNota, CabecalhoNotaVO.class);
            Collection<DynamicVO> itensVO = cabVO.asCollection(DynamicEntityNames.ITEM_NOTA);

            List<DynamicVO> itensRevenda = itensVO.stream().filter(vo -> "S".equals(StringUtils.getNullAsEmpty(vo.asDymamicVO(DynamicEntityNames.PRODUTO).asString("AD_REVENDA")))).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(itensRevenda)) {
                CabecalhoNotaVO cabTransfVO = (CabecalhoNotaVO) dwfFacade.getDefaultValueObjectInstance(DynamicEntityNames.CABECALHO_NOTA, CabecalhoNotaVO.class);
                cabTransfVO.setCODTIPOPER(BigDecimal.valueOf(701));
                cabTransfVO.setTIPMOV("T");
                cabTransfVO.setNUMNOTA(BigDecimal.ZERO);
                cabTransfVO.setCODEMP(cabVO.getCODEMP());
                cabTransfVO.setCODEMPNEGOC(cabVO.getCODEMP());
                dwfFacade.createEntity(DynamicEntityNames.CABECALHO_NOTA, cabTransfVO);

                Collection<ItemNotaVO> itens = ItemNota.montaItensNota(itensRevenda, cabTransfVO, BigDecimal.valueOf(1001));

                ItemNotaHelpper.saveItensNota(itens, cabTransfVO);

                Collection<DynamicVO> itensteste = cabTransfVO.asCollection(DynamicEntityNames.ITEM_NOTA);

                /*for (DynamicVO item: itensteste) {
                    if (true) throw new MGEModelException("VLR UNIT: " + item.asBigDecimal("VLRUNIT") + " VLRTOT: " +item.asBigDecimal("VLRTOT"));
                }*/

                nuNotasTransf.add(cabTransfVO.getNUNOTA());

                ItemNotaHelpper.calcularTotalItens(cabTransfVO, null, null);

                // Recalculo de impostos
                final ImpostosHelpper impostos = new ImpostosHelpper();
                impostos.calcularImpostos(cabTransfVO.getNUNOTA());
                impostos.totalizarNota(cabTransfVO.getNUNOTA());

                // Refaz financeiro
                //centralFinanceiro.refazerFinanceiro();

            }
        }

        nuNotasTransf.forEach(msgRetorno::append);

        contextoAcao.setMensagemRetorno(msgRetorno.toString());


    }
}
