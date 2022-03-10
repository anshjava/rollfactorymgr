package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.ImplementedBy;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.client.Client;

import java.time.ZonedDateTime;
import java.util.List;

@ImplementedBy(ClientServiceMock.class)
public interface ClientService {
    /**
     * Update local cached Registry by values from server
     *
     * @throws WebServiceException
     */
    void updateRegistryFromServer() throws WebServiceException;

    /**
     * Get local cached Client Registry
     *
     * @return local cached Client Registry
     * @throws WebServiceException
     */
    List<Client> getLocalClientRegistry();

    /**
     * Find Client by id in local cached Client Registry
     *
     * @param id id of Client
     * @return found Client
     * @throws WebServiceException
     */
    Client findClientById(Long id) throws WebServiceException;

    /**
     * Find Client by companyName pattern in local cached Client Registry
     *
     * @param companyName name/part of name of Client
     * @return list of matched Clients
     * @throws WebServiceException
     */
    List<Client> findClientByNamePattern(String companyName) throws WebServiceException;

    /**
     * Find Client by parameters in local cached Client Registry
     *
     * @param id               part of client id
     * @param companyName      part of client's company name
     * @param creationDateFrom client creation date from
     * @param creationDateTo   client creation date to
     * @param city             part of city
     * @param address          part of address
     * @param buyerName        part of client's buyer name
     * @param phone            part of client's buyer phone
     * @param email            part of client's buyer email
     * @return list of matched Clients
     * @throws WebServiceException
     */
    List<Client> findClientByParams(Long id, String companyName, ZonedDateTime creationDateFrom, ZonedDateTime creationDateTo,
                                    String city, String address, String buyerName, String phone, String email) throws WebServiceException;

    /**
     * Create Client on Server Registry
     *
     * @param companyName client's company name
     * @param city        city
     * @param address     address
     * @param buyerName   client's buyer name
     * @param phone       client's buyer phone
     * @param email       client's buyer email
     * @return new Client
     * @throws WebServiceException if validation fail
     */
    Client createClient(String companyName, String city, String address, String buyerName, String phone, String email) throws WebServiceException;

    /**
     * Remove Client on Server Registry by id
     *
     * @param id id of Client to remove
     * @return true if success
     * @throws WebServiceException if Client with specified id was not found or there is some orders by this client
     */
    boolean removeClientById(Long id) throws WebServiceException;

    /**
     * Update Client on Server Registry with new parameters
     *
     * @param client - Client with same id but diffirent parameters
     * @return updated Client
     * @throws WebServiceException if validation fail or all parameters are equals
     */
    Client updateClient(Client client) throws WebServiceException;

}
