package com.cjoa.wms.service;

import com.cjoa.wms.dao.ReceiveMapper;
import com.cjoa.wms.dao.UserMapper;
import com.cjoa.wms.dto.ReceiveDto;
import com.cjoa.wms.dto.OrderProdOptionDeliveryDto;
import com.cjoa.wms.dto.UserDto;
import org.apache.ibatis.session.SqlSession;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.cjoa.wms.config.MyBatisConfig.getSqlSession;

public class ReceiveService {
    private ReceiveMapper receiveMapper;

    public List<ReceiveDto> receiveSearchAll(){
        SqlSession sqlSession = getSqlSession();
        receiveMapper = sqlSession.getMapper(ReceiveMapper.class);
        List<ReceiveDto> list = receiveMapper.receiveSearchAll();
        sqlSession.close();
        return list;
    }
    public List<ReceiveDto> receiveSearchByCode(int receiveCode){
        SqlSession sqlSession = getSqlSession();
        receiveMapper = sqlSession.getMapper(ReceiveMapper.class);
        List<ReceiveDto> list = receiveMapper.receiveSearchByCode(receiveCode);
        sqlSession.close();
        return list;
    }
    public List<ReceiveDto> receiveSearchByDate(String startTime, String endTime){
        SqlSession sqlSession = getSqlSession();
        receiveMapper = sqlSession.getMapper(ReceiveMapper.class);
        LocalDate end = LocalDate.parse(endTime);
        LocalDate nextDay = end.plusDays(1);
        String nextDayEndTime = nextDay.toString(); // "2025-04-04" 형태

        Map<String, String> param = Map.of(
                "startTime", startTime,
                "nextDayEndTime", nextDayEndTime
        );
        List<ReceiveDto> list = receiveMapper.receiveSearchByDate(param);
        sqlSession.close();
        return list;
    }

    public OrderProdOptionDeliveryDto checkProductByOptionCode(int optionCode) {
        SqlSession sqlSession = getSqlSession();
        receiveMapper = sqlSession.getMapper(ReceiveMapper.class);
        OrderProdOptionDeliveryDto result = receiveMapper.checkProductByOptionCode(optionCode);
        sqlSession.close();
        return result;
    }

    public int receiveProcess(OrderProdOptionDeliveryDto prodInfo) {
        SqlSession sqlSession = getSqlSession();
        receiveMapper = sqlSession.getMapper(ReceiveMapper.class);
        int result1 = receiveMapper.insertReceive(prodInfo);
        int result2 = receiveMapper.updateStockPlus(prodInfo);
        int result3 = 0;
        // 품절검사후 N으로 전환
        int code = prodInfo.getProdOptionCode();
        String soldout = receiveMapper.checkProdOptionSoldout(code);
        if ("Y".equals(soldout)) {
            result3 = receiveMapper.updateProdOptionSoldout(code);
        } else {
            result3 = 1;
        }

        int result = 0;
        if (result1 > 0 && result2 > 0 && result3 > 0) {
            sqlSession.commit();
            result = 1;
        } else {
            sqlSession.rollback();
        }
        sqlSession.close();
        return result;
    }
}
