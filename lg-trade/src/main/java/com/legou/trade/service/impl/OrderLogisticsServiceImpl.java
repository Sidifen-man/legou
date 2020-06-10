package com.legou.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legou.trade.entity.OrderLogistics;
import com.legou.trade.mapper.OrderLogisticsMapper;
import com.legou.trade.service.OrderLogisticsService;
import org.springframework.stereotype.Service;

@Service
public class OrderLogisticsServiceImpl extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements OrderLogisticsService {

}
