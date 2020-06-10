package com.legou.trade.dao;

import com.legou.common.dto.BaseDTO;
import com.legou.trade.entity.Order;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderDTO  extends BaseDTO {
    private Long orderId;
    private Long totalFee;
    private Long postFee;
    private Long actualFee;
    private Integer paymentType;
    private Long userId;
    private Integer status;
    private Date createTime;
    private Date payTime;
    private Date consignTime;
    private Date endTime;
    private Date closeTime;
    private Date commentTime;

    public OrderDTO(Order entity) {
        super(entity);
        this.status = entity.getStatus().getValue();
    }

}
