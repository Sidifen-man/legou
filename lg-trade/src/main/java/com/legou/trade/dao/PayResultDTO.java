package com.legou.trade.dao;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "xml")
public class PayResultDTO {
    @JacksonXmlProperty(localName = "return_code")
    private String returnCode="SUCCESS";
    @JacksonXmlProperty(localName = "return_msg")
    private String returnMsg="OK";
}
