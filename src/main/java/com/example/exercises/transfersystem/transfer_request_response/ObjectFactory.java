//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.01.15 at 08:35:45 PM CET 
//


package com.example.exercises.transfersystem.transfer_request_response;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.example.exercises.transfersystem.transfer_request_response package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TransferRequest_QNAME = new QName("http://www.example.com/exercises/transfersystem/transfer-request-response.xsd", "TransferRequest");
    private final static QName _TransferResponse_QNAME = new QName("http://www.example.com/exercises/transfersystem/transfer-request-response.xsd", "TransferResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.example.exercises.transfersystem.transfer_request_response
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TransferRequestType }
     * 
     */
    public TransferRequestType createTransferRequestType() {
        return new TransferRequestType();
    }

    /**
     * Create an instance of {@link TransferResponseType }
     * 
     */
    public TransferResponseType createTransferResponseType() {
        return new TransferResponseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransferRequestType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TransferRequestType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.example.com/exercises/transfersystem/transfer-request-response.xsd", name = "TransferRequest")
    public JAXBElement<TransferRequestType> createTransferRequest(TransferRequestType value) {
        return new JAXBElement<TransferRequestType>(_TransferRequest_QNAME, TransferRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransferResponseType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TransferResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.example.com/exercises/transfersystem/transfer-request-response.xsd", name = "TransferResponse")
    public JAXBElement<TransferResponseType> createTransferResponse(TransferResponseType value) {
        return new JAXBElement<TransferResponseType>(_TransferResponse_QNAME, TransferResponseType.class, null, value);
    }

}
