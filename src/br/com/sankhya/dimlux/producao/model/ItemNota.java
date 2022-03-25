package br.com.sankhya.dimlux.producao.model;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.dwfdata.vo.CabecalhoNotaVO;
import br.com.sankhya.modelcore.dwfdata.vo.ItemNotaVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

public class ItemNota {

    public static Collection<ItemNotaVO> montaItensNota(Collection<DynamicVO> itensRevenda, CabecalhoNotaVO notaVO, BigDecimal codLocalDest) throws Exception {

        EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
        JdbcWrapper jdbc = dwfFacade.getJdbcWrapper();
        jdbc.openSession();

        Collection<ItemNotaVO> itens = new ArrayList<>();

        for (DynamicVO item: itensRevenda) {
            ItemNotaVO itemVO = (ItemNotaVO) dwfFacade.getDefaultValueObjectInstance("ItemNota", ItemNotaVO.class);
            itemVO.setNUNOTA(notaVO.getNUNOTA());
            itemVO.setCODPROD(item.asBigDecimal("CODPROD"));
            itemVO.setQTDNEG(item.asBigDecimal("QTDNEG"));
            itemVO.setVLRUNIT(item.asBigDecimal("VLRUNIT"));
            itemVO.setVLRTOT(itemVO.getQTDNEG().multiply(itemVO.getVLRUNIT()));
            itemVO.setUSOPROD(item.asString("USOPROD"));
            itemVO.setCODEMP(item.asBigDecimal("CODEMP"));
            itemVO.setCODVOL(item.asString("CODVOL"));
            itemVO.setCONTROLE(item.asString("CONTROLE"));
            itemVO.setOBSERVACAO(item.asString("OBSERVACAO"));
            itemVO.setPENDENTE("N");
            itemVO.setRESERVA("N");
            itemVO.setCODLOCALORIG(item.asBigDecimal("CODLOCALORIG"));
            itemVO.setProperty("CODLOCALDEST",codLocalDest);
            itemVO.setATUALESTOQUE(BigDecimal.valueOf(-1));
            itemVO.setProperty("AD_CORCABO", item.asString("AD_CORCABO"));

            //if (true) throw new MGEModelException("VLR UNIT: " + itemVO.getVLRUNIT() + " VLRTOT: " +itemVO.getVLRTOT());

            itens.add(itemVO);
        }

        jdbc.closeSession();

        return itens;
    }
}
